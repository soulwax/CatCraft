// File: src/main/java/com/soul/catcraft/services/GuiService.java

package com.soul.catcraft.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.j256.ormlite.logger.LoggerFactory;
import com.soul.catcraft.config.PluginConfig;
import com.soul.catcraft.models.PlayerData;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Consumer;
import org.yaml.snakeyaml.internal.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

// services/GuiService.java
public interface GuiService {
    void openPlayerStatsGui(Player player, String targetUuid);
    void openAdminPanel(Player player);
    void openPlayerListGui(Player player);
    void openConfigurationGui(Player player);
}

@Singleton
public class GuiServiceImpl implements GuiService {
    private static final Logger logger = LoggerFactory.getLogger(GuiServiceImpl.class);

    private final PlayerService playerService;
    private final ConfigService configService;
    private final Plugin plugin;

    @Inject
    public GuiServiceImpl(PlayerService playerService, ConfigService configService, Plugin plugin)ß {
        this.playerService = playerService;
        this.configService = configService;
        this.plugin = plugin;
    }

    @Override
    public void openPlayerStatsGui(Player player, String targetUuid) {
        playerService.getPlayerData(targetUuid).thenAccept(playerData -> {
            if (playerData == null) {
                player.sendMessage(String.valueOf(Component.text("Player not found!", NamedTextColor.RED)));
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                Gui gui = Gui.gui()
                        .title(Component.text("Player Stats: " + playerData.getDisplayName()))
                        .rows(6)
                        .disableAllInteractions()
                        .create();

                // Player head
                ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(targetUuid)));
                skullMeta.displayName(Component.text(playerData.getDisplayName(), NamedTextColor.GOLD));

//                List<Component> lore = Arrays.asList(
                        Component.text("First Join: " + formatDate(playerData.getFirstJoin()), NamedTextColor.GRAY),
                        Component.text("Last Seen: " + formatDate(playerData.getLastSeen()), NamedTextColor.GRAY),
                        Component.text("Playtime: " + formatPlaytime(playerData.getTotalPlaytime()), NamedTextColor.GRAY),
                        Component.text("Status: " + (playerData.isBanned() ? "Banned" : "Active"),
                                playerData.isBanned() ? NamedTextColor.RED : NamedTextColor.GREEN)
                );
                skullMeta.lore(lore);
                playerHead.setItemMeta(skullMeta);

                gui.setItem(4, 1, ItemBuilder.from(playerHead).asGuiItem());

                // Statistics items
                addStatItem(gui, 1, 2, Material.CLOCK, "Playtime",
                        formatPlaytime(playerData.getTotalPlaytime()));
                addStatItem(gui, 3, 2, Material.CALENDAR, "First Join",
                        formatDate(playerData.getFirstJoin()));
                addStatItem(gui, 5, 2, Material.EMERALD, "Status",
                        playerData.isBanned() ? "Banned" : "Active");
                addStatItem(gui, 7, 2, Material.NAME_TAG, "Previous Names",
                        String.valueOf(countPreviousNames(playerData.getPreviousNames())));

                // Admin actions (if player has permission)
                if (player.hasPermission("catcraft.admin")) {
                    addAdminActions(gui, player, playerData);
                }

                // Back button
                gui.setItem(6, 4, ItemBuilder.from(Material.ARROW)
                        .name(Component.text("Back", NamedTextColor.RED))
                        .asGuiItem(event -> openPlayerListGui(player)));

                gui.open(player);
            });
        });
    }

    @Override
    public void openAdminPanel(Player player) {
        if (!player.hasPermission("catcraft.admin")) {
            player.sendMessage(String.valueOf(Component.text("No permission!", NamedTextColor.RED)));
            return;
        }

        Gui gui = Gui.gui()
                .title(Component.text("CatCraft Admin Panel"))
                .rows(4)
                .create();

        // Player management
        gui.setItem(2, 1, ItemBuilder.from(Material.PLAYER_HEAD)
                .name(Component.text("Player Management", NamedTextColor.BLUE))
                .lore(Component.text("View and manage players"))
                .asGuiItem(event -> openPlayerListGui(player)));

        // Server statistics
        gui.setItem(4, 1, ItemBuilder.from(Material.BOOK)
                .name(Component.text("Server Statistics", NamedTextColor.GREEN))
                .lore(Component.text("View server statistics"))
                .asGuiItem(event -> openStatisticsGui(player)));

        // Configuration
        gui.setItem(6, 1, ItemBuilder.from(Material.REDSTONE)
                .name(Component.text("Configuration", NamedTextColor.GOLD))
                .lore(Component.text("Modify plugin settings"))
                .asGuiItem(event -> openConfigurationGui(player)));

        // Reload plugin
        gui.setItem(8, 1, ItemBuilder.from(Material.COMMAND_BLOCK)
                .name(Component.text("Reload Config", NamedTextColor.YELLOW))
                .lore(Component.text("Reload plugin configuration"))
                .asGuiItem(event -> {
                    configService.reloadConfiguration();
                    player.sendMessage(Component.text("Configuration reloaded!", NamedTextColor.GREEN));
                    player.closeInventory();
                }));

        gui.open(player);
    }

    @Override
    public void openPlayerListGui(Player player) {
        PaginatedGui gui = Gui.paginated()
                .title(Component.text("Online Players"))
                .rows(6)
                .pageSize(45)
                .create();

        // Add online players
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            ItemStack playerItem = createPlayerItem(onlinePlayer);
            gui.addItem(ItemBuilder.from(playerItem)
                    .asGuiItem(event -> openPlayerStatsGui(player, onlinePlayer.getUniqueId().toString())));
        }

        // Navigation buttons
        gui.setItem(6, 3, ItemBuilder.from(Material.ARROW)
                .name(Component.text("Previous Page", NamedTextColor.YELLOW))
                .asGuiItem(event -> gui.previous()));

        gui.setItem(6, 7, ItemBuilder.from(Material.ARROW)
                .name(Component.text("Next Page", NamedTextColor.YELLOW))
                .asGuiItem(event -> gui.next()));

        gui.open(player);
    }

    @Override
    public void openConfigurationGui(Player player) {
        if (!player.hasPermission("catcraft.admin.config")) {
            player.sendMessage(String.valueOf(Component.text("No permission!", NamedTextColor.RED)));
            return;
        }

        Gui gui = Gui.gui()
                .title(Component.text("Configuration Settings"))
                .rows(5)
                .create();

        PluginConfig config = configService.getConfig();

        // Chat settings
        addToggleItem(gui, 1, 1, Material.BOOK_AND_QUILL, "Chat Features",
                "Enable emoji replacement: " + config.chat.enableEmojiReplacement,
                config.chat.enableEmojiReplacement,
                (enabled) -> {
                    config.chat.enableEmojiReplacement = enabled;
                    configService.saveConfiguration();
                });

        // Cat protection
        addToggleItem(gui, 3, 1, Material.OCELOT_SPAWN_EGG, "Cat Protection",
                "Protect cats from damage: " + config.features.protectCats,
                config.features.protectCats,
                (enabled) -> {
                    config.features.protectCats = enabled;
                    configService.saveConfiguration();
                });

        // Verbose logging
        addToggleItem(gui, 5, 1, Material.PAPER, "Verbose Logging",
                "Enable verbose logging: " + config.general.verbose,
                config.general.verbose,
                (enabled) -> {
                    config.general.verbose = enabled;
                    configService.saveConfiguration();
                });

        // Disarm main hand
        addToggleItem(gui, 7, 1, Material.DIAMOND_SWORD, "Disarm Main Hand",
                "Disarm main hand item: " + config.features.disarmMainHand,
                config.features.disarmMainHand,
                (enabled) -> {
                    config.features.disarmMainHand = enabled;
                    configService.saveConfiguration();
                });

        gui.open(player);
    }

    private void addStatItem(Gui gui, int slot, int row, Material material, String name, String value) {
        gui.setItem(slot, row, ItemBuilder.from(material)
                .name(Component.text(name, NamedTextColor.BLUE))
                .lore(Component.text(value, NamedTextColor.GRAY))
                .asGuiItem());
    }

    private void addAdminActions(Gui gui, Player admin, PlayerData playerData) {
        // Ban/Unban button
        if (playerData.isBanned()) {
            gui.setItem(2, 4, ItemBuilder.from(Material.GREEN_WOOL)
                    .name(Component.text("Unban Player", NamedTextColor.GREEN))
                    .asGuiItem(event -> {
                        playerService.unbanPlayer(playerData.getUuid());
                        admin.sendMessage(String.valueOf(Component.text("Player unbanned!", NamedTextColor.GREEN)));
                        admin.closeInventory();
                    }));
        } else {
            gui.setItem(2, 4, ItemBuilder.from(Material.RED_WOOL)
                    .name(Component.text("Ban Player", NamedTextColor.RED))
                    .asGuiItem(event -> {
                        // Open ban reason input (simplified for now)
                        playerService.banPlayer(playerData.getUuid(), "Banned via GUI", 0);
                        admin.sendMessage(String.valueOf(Component.text("Player banned!", NamedTextColor.RED)));
                        admin.closeInventory();
                    }));
        }

        // Teleport to player (if online)
        Player target = Bukkit.getPlayer(UUID.fromString(playerData.getUuid()));
        if (target != null && target.isOnline()) {
            gui.setItem(4, 4, ItemBuilder.from(Material.ENDER_PEARL)
                    .name(Component.text("Teleport to Player", NamedTextColor.PURPLE))
                    .asGuiItem(event -> {
                        admin.teleport(target);
                        admin.sendMessage(String.valueOf(Component.text("Teleported to " + target.getName(), NamedTextColor.GREEN)));
                        admin.closeInventory();
                    }));
        }

        // View session history
        gui.setItem(6, 4, ItemBuilder.from(Material.WRITABLE_BOOK)
                .name(Component.text("Session History", NamedTextColor.YELLOW))
                .asGuiItem(event -> openSessionHistoryGui(admin, playerData.getUuid())));
    }

    private void addToggleItem(Gui gui, int slot, int row, Material material, String name, String description,
                               boolean currentValue, Consumer<Boolean> onToggle) {
        Material displayMaterial = currentValue ? Material.GREEN_WOOL : Material.RED_WOOL;
        String status = currentValue ? "Enabled" : "Disabled";

        gui.setItem(slot, row, ItemBuilder.from(displayMaterial)
                .name(Component.text(name, NamedTextColor.BLUE))
                .lore(
                        Component.text(description, NamedTextColor.GRAY),
                        Component.text("Status: " + status, currentValue ? NamedTextColor.GREEN : NamedTextColor.RED),
                        Component.text("Click to toggle", NamedTextColor.YELLOW)
                )
                .asGuiItem(event -> {
                    onToggle.accept(!currentValue);
                    event.getWhoClicked().sendMessage(
                            String.valueOf(Component.text(name + " " + (!currentValue ? "enabled" : "disabled"),
                                    !currentValue ? NamedTextColor.GREEN : NamedTextColor.RED))
                    );
                    openConfigurationGui((Player) event.getWhoClicked());
                }));
    }

    private ItemStack createPlayerItem(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(player);
        meta.displayName(Component.text(player.getDisplayName(), NamedTextColor.GOLD));
        meta.lore(Arrays.asList(
                Component.text("Click to view stats", NamedTextColor.GRAY),
                Component.text("Online now", NamedTextColor.GREEN)
        ));
        item.setItemMeta(meta);
        return item;
    }

    // Additional helper methods...
}