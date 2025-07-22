// File: src/main/java/com/soul/catcraft/services/ChatServiceImpl.java

package com.soul.catcraft.services;

import com.google.common.cache.Cache;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.awt.*;

// services/ChatServiceImpl.java (comprehensive implementation)
@Singleton
public class ChatServiceImpl implements ChatService {
    private static final Logger logger = LoggerFactory.getLogger(ChatServiceImpl.class);

    private final MiniMessage miniMessage;
    private final ConfigService configService;
    private final PlayerService playerService;
    private final Cache<String, Component> messageCache;
    private final EventBus eventBus;

    @Inject
    public ChatServiceImpl(ConfigService configService, PlayerService playerService, EventBus eventBus) {
        this.configService = configService;
        this.playerService = playerService;
        this.eventBus = eventBus;
        this.miniMessage = MiniMessage.miniMessage();

        // Cache for formatted messages to reduce processing
        this.messageCache = Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
    }

    @Override
    public void processPlayerMessage(Player player, String message) {
        // Anti-spam check
        if (isSpamming(player, message)) {
            player.sendMessage(createErrorMessage("Please slow down your messages"));
            return;
        }

        // Filter profanity if enabled
        String filteredMessage = configService.getChat().enableProfanityFilter ?
                filterProfanity(message) : message;

        // Process emoji replacement
        String processedMessage = configService.getChat().enableEmojiReplacement ?
                EmojiLibrary.findAndReplaceEmojiRND(filteredMessage) : filteredMessage;

        // Create chat component
        Component chatComponent = createChatMessage(player, processedMessage);

        // Determine recipients based on range/permissions
        Set<Player> recipients = getMessageRecipients(player, message);

        // Send message
        for (Player recipient : recipients) {
            recipient.sendMessage(chatComponent);
        }

        // Log to console
        logger.info("[CHAT] {}: {}", player.getName(), message);

        // Fire chat event for other plugins
        eventBus.post(new PlayerChatProcessedEvent(player, message, recipients));
    }

    @Override
    public Component createChatMessage(Player player, String message) {
        String cacheKey = player.getUniqueId() + ":" + player.getDisplayName() + ":" + getRoleColor(player);
        Component playerPrefix = messageCache.get(cacheKey, key -> createPlayerPrefix(player));

        return Component.text()
                .append(playerPrefix)
                .append(Component.text(": ", NamedTextColor.DARK_GRAY))
                .append(parseMessageContent(message))
                .hoverEvent(HoverEvent.showText(createPlayerHoverInfo(player)))
                .clickEvent(ClickEvent.suggestCommand("/msg " + player.getName() + " "))
                .build();
    }

    private Component createPlayerPrefix(Player player) {
        String role = getPlayerRole(player);
        NamedTextColor roleColor = getRoleColor(player);

        return Component.text()
                .append(Component.text("[", NamedTextColor.DARK_GRAY))
                .append(Component.text(role, roleColor))
                .append(Component.text("] ", NamedTextColor.DARK_GRAY))
                .append(Component.text(player.getDisplayName(), roleColor))
                .build();
    }

    private Component parseMessageContent(String message) {
        // Parse URLs for click events
        String urlPattern = "https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+";
        Pattern pattern = Pattern.compile(urlPattern);
        Matcher matcher = pattern.matcher(message);

        if (!matcher.find()) {
            return Component.text(message, NamedTextColor.WHITE);
        }

        // Build component with clickable URLs
        ComponentBuilder builder = Component.text();
        int lastEnd = 0;

        matcher.reset();
        while (matcher.find()) {
            // Add text before URL
            if (matcher.start() > lastEnd) {
                builder.append(Component.text(
                        message.substring(lastEnd, matcher.start()),
                        NamedTextColor.WHITE
                ));
            }

            // Add clickable URL
            String url = matcher.group();
            builder.append(Component.text(url, NamedTextColor.BLUE)
                    .hoverEvent(HoverEvent.showText(Component.text("Click to open: " + url)))
                    .clickEvent(ClickEvent.openUrl(url)));

            lastEnd = matcher.end();
        }

        // Add remaining text
        if (lastEnd < message.length()) {
            builder.append(Component.text(
                    message.substring(lastEnd),
                    NamedTextColor.WHITE
            ));
        }

        return builder.build();
    }

    private Component createPlayerHoverInfo(Player player) {
        return playerService.getPlayerData(player.getUniqueId().toString())
                .thenApply(playerData -> {
                    if (playerData == null) return Component.text("Loading...");

                    return Component.text()
                            .append(Component.text("Player: ", NamedTextColor.GRAY))
                            .append(Component.text(player.getDisplayName(), NamedTextColor.WHITE))
                            .append(Component.newline())
                            .append(Component.text("First Joined: ", NamedTextColor.GRAY))
                            .append(Component.text(formatDate(playerData.getFirstJoin()), NamedTextColor.WHITE))
                            .append(Component.newline())
                            .append(Component.text("Playtime: ", NamedTextColor.GRAY))
                            .append(Component.text(formatPlaytime(playerData.getTotalPlaytime()), NamedTextColor.WHITE))
                            .append(Component.newline())
                            .append(Component.text("Click to send private message", NamedTextColor.YELLOW))
                            .build();
                })
                .join(); // Note: In real implementation, use async properly
    }

    @Override
    public Component createWelcomeMessage(Player player) {
        return miniMessage.deserialize(
                configService.getChat().welcomeMessageTemplate
                        .replace("{player}", player.getDisplayName())
                        .replace("{server}", Bukkit.getServerName())
        );
    }

    @Override
    public Component createNewPlayerAnnouncement(Player player) {
        return Component.text()
                .append(Component.text("Welcome ", NamedTextColor.GREEN))
                .append(Component.text(player.getDisplayName(), NamedTextColor.GOLD))
                .append(Component.text(" to the server! ", NamedTextColor.GREEN))
                .append(Component.text("(First time joining)", NamedTextColor.GRAY))
                .hoverEvent(HoverEvent.showText(Component.text("Click to welcome them!")))
                .clickEvent(ClickEvent.suggestCommand("/msg " + player.getName() + " Welcome to the server!"))
                .build();
    }

    @Override
    public Component createNameChangeMessage(String oldName, String newName) {
        return Component.text()
                .append(Component.text(oldName, NamedTextColor.GRAY))
                .append(Component.text(" is now known as ", NamedTextColor.YELLOW))
                .append(Component.text(newName, NamedTextColor.WHITE))
                .build();
    }

    private Set<Player> getMessageRecipients(Player sender, String message) {
        Set<Player> recipients = new HashSet<>();

        // Local chat (range-based)
        if (configService.getChat().enableLocalChat && !message.startsWith("!")) {
            double range = configService.getChat().localChatRange;
            Location senderLoc = sender.getLocation();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getWorld().equals(sender.getWorld()) &&
                        player.getLocation().distance(senderLoc) <= range) {
                    recipients.add(player);
                }
            }
        } else {
            // Global chat
            recipients.addAll(Bukkit.getOnlinePlayers());
        }

        // Remove players who have sender ignored
        recipients.removeIf(player -> playerService.hasPlayerIgnored(player, sender));

        return recipients;
    }

    private boolean isSpamming(Player player, String message) {
        // Implementation for anti-spam logic
        // Could use a sliding window or rate limiter
        return false; // Simplified for now
    }

    private String filterProfanity(String message) {
        // Implementation for profanity filtering
        // Could use a word list or external service
        return message; // Simplified for now
    }
}