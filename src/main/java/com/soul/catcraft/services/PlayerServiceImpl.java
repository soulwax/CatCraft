package com.soul.catcraft.services;

import com.google.common.cache.Cache;
import com.google.common.eventbus.EventBus;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.soul.catcraft.events.PlayerJoinProcessedEvent;
import com.soul.catcraft.models.PlayerData;
import com.soul.catcraft.repositories.PlayerRepository;
import jakarta.inject.Singleton;
import kotlin.jvm.internal.TypeReference;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.soul.catcraft.Commands.plugin;

@Singleton
public abstract class PlayerServiceImpl implements PlayerService {
    private static final Logger logger = LoggerFactory.getLogger(PlayerServiceImpl.class);

    private final PlayerRepository playerRepository;
    private final ChatService chatService;
    private final ConfigService configService;
    private final Cache<String, PlayerData> playerCache;
    private final EventBus eventBus;

    @Inject
    public PlayerServiceImpl(
            PlayerRepository playerRepository,
            ChatService chatService,
            ConfigService configService,
            EventBus eventBus) {
        this.playerRepository = playerRepository;
        this.chatService = chatService;
        this.configService = configService;
        this.eventBus = eventBus;

        // Initialize Caffeine cache
        this.playerCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .removalListener((key, value, cause) -> {
                    logger.debug("Player {} removed from cache: {}", key, cause);
                })
                .build();
    }

    @Override
    public CompletableFuture<Void> handlePlayerJoin(Player player) {
        String uuid = player.getUniqueId().toString();

        return playerRepository.findByUuid(uuid)
                .thenCompose(playerData -> {
                    if (playerData == null) {
                        return handleNewPlayer(player);
                    } else {
                        return handleReturningPlayer(player, playerData);
                    }
                })
                .thenRun(() -> {
                    // Cache the player
                    playerCache.put(uuid, createPlayerDataFromPlayer(player));

                    // Fire custom event
                    eventBus.post(new PlayerJoinProcessedEvent(player));

                    logger.info("Player join processed: {} ({})",
                            player.getName(), uuid);
                });
    }

    private CompletableFuture<Void> handleNewPlayer(Player player) {
        PlayerData newData = new PlayerData();
        newData.setUuid(player.getUniqueId().toString());
        newData.setDisplayName(player.getDisplayName());
        newData.setFirstJoin(Timestamp.from(Instant.now()));
        newData.setLastSeen(Timestamp.from(Instant.now()));
        newData.setPreviousNames("[]"); // Empty JSON array

        return playerRepository.save(newData)
                .thenRun(() -> {
                    // Send welcome message
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Component welcomeMessage = chatService.createWelcomeMessage(player);
                        player.sendMessage(welcomeMessage);

                        // Broadcast new player announcement
                        if (configService.getFeatures().announceNewPlayers) {
                            Component announcement = chatService.createNewPlayerAnnouncement(player);
                            Bukkit.broadcast(announcement, "catcraft.see.joins");
                        }
                    });

                    logger.info("New player registered: {} ({})",
                            player.getName(), player.getUniqueId());
                });
    }

    private CompletableFuture<Void> handleReturningPlayer(Player player, PlayerData playerData) {
        // Check for name change
        boolean nameChanged = !player.getDisplayName().equals(playerData.getDisplayName());

        if (nameChanged) {
            updatePlayerNameHistory(playerData, player.getDisplayName());
        }

        // Update last seen
        playerData.setLastSeen(Timestamp.from(Instant.now()));
        playerData.setDisplayName(player.getDisplayName());

        return playerRepository.save(playerData)
                .thenRun(() -> {
                    if (nameChanged && configService.getGeneral().announceNameChanges) {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            Component nameChangeMessage = chatService.createNameChangeMessage(
                                    playerData.getDisplayName(), player.getDisplayName());
                            Bukkit.broadcast(nameChangeMessage, "catcraft.see.namechanges");
                        });
                    }

                    logger.info("Returning player updated: {} ({})",
                            player.getName(), player.getUniqueId());
                });
    }

    @Override
    public CompletableFuture<Void> disarmPlayer(Player admin, Player target) {
        if (!admin.hasPermission("catcraft.admin.disarm")) {
            admin.sendMessage(String.valueOf(chatService.createErrorMessage("No permission")));
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.supplyAsync(() -> {
            List<ItemStack> removedItems = new ArrayList<>();
            PlayerInventory inventory = target.getInventory();

            // Remove armor
            if (inventory.getHelmet() != null) {
                removedItems.add(inventory.getHelmet());
                inventory.setHelmet(null);
            }
            if (inventory.getChestplate() != null) {
                removedItems.add(inventory.getChestplate());
                inventory.setChestplate(null);
            }
            if (inventory.getLeggings() != null) {
                removedItems.add(inventory.getLeggings());
                inventory.setLeggings(null);
            }
            if (inventory.getBoots() != null) {
                removedItems.add(inventory.getBoots());
                inventory.setBoots(null);
            }

            // Remove offhand
            if (inventory.getItemInOffHand().getType() != Material.AIR) {
                removedItems.add(inventory.getItemInOffHand());
                inventory.setItemInOffHand(new ItemStack(Material.AIR));
            }

            // Remove main hand if configured
            if (configService.getFeatures().disarmMainHand &&
                    inventory.getItemInMainHand().getType() != Material.AIR) {
                removedItems.add(inventory.getItemInMainHand());
                inventory.setItemInMainHand(new ItemStack(Material.AIR));
            }

            return removedItems;
        }).thenAccept(removedItems -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                // Give items to admin
                for (ItemStack item : removedItems) {
                    admin.getInventory().addItem(item);
                }

                // Log the action
                playerRepository.savePlayerSession(admin,
                        "DISARM_PLAYER:" + target.getName());

                // Send messages
                admin.sendMessage(String.valueOf(chatService.createSuccessMessage(
                        "Disarmed " + target.getDisplayName() + " - " + removedItems.size() + " items")));
                target.sendMessage(String.valueOf(chatService.createWarningMessage(
                        "You have been disarmed by " + admin.getDisplayName())));

                logger.info("Player {} disarmed {} ({} items)",
                        admin.getName(), target.getName(), removedItems.size());
            });
        });
    }

    @Override
    public CompletableFuture<PlayerData> getPlayerData(String uuid) {
        PlayerData cached = playerCache.getIfPresent(uuid);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }

        return playerRepository.findByUuid(uuid)
                .thenApply(playerData -> {
                    if (playerData != null) {
                        playerCache.put(uuid, playerData);
                    }
                    return playerData;
                });
    }

    @Override
    public CompletableFuture<List<String>> getTopPlayers(String metric, int limit) {
        return playerRepository.getTopPlayersByMetric(metric, limit);
    }

    private void updatePlayerNameHistory(PlayerData playerData, String newName) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<String> nameHistory = mapper.readValue(
                    playerData.getPreviousNames(),
                    new TypeReference<List<String>>() {
                    }
            );

            if (!nameHistory.contains(playerData.getDisplayName())) {
                nameHistory.add(playerData.getDisplayName());

                // Keep only last 10 names
                if (nameHistory.size() > 10) {
                    nameHistory = nameHistory.subList(nameHistory.size() - 10, nameHistory.size());
                }

                playerData.setPreviousNames(mapper.writeValueAsString(nameHistory));
            }
        } catch (Exception e) {
            logger.error("Failed to update name history for {}", playerData.getUuid(), e);
        }
    }

    // services/PlayerServiceImpl.java - handleCatProtection method
    @Override
    public void handleCatProtection(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();

        if (!isProtectedEntity(damaged)) {
            return;
        }

        if (!configService.getConfig().features.protectCats) {
            return;
        }

        Entity damager = getDamageSource(event);
        Player attacker = getPlayerAttacker(damager);

        if (attacker == null) {
            return; // Only protect from player damage
        }

        // Cancel the damage
        event.setCancelled(true);

        // Apply retribution damage
        double retributionDamage = calculateRetributionDamage(event.getDamage());
        attacker.damage(retributionDamage);

        // Send warning message
        Component warningMessage = Component.text()
                .append(Component.text("⚠ ", NamedTextColor.RED))
                .append(Component.text("You have angered the cat gods! ", NamedTextColor.DARK_RED))
                .append(Component.text("Cats are protected on this server.", NamedTextColor.RED))
                .build();
        attacker.sendMessage(warningMessage);

        // Log the incident
        logCatProtectionIncident(attacker, damaged, event.getDamage());

        // Apply additional penalties for repeat offenders
        applyRepeatOffenderPenalties(attacker);

        if (configService.getConfig().general.verbose) {
            logger.info("Cat protection triggered: {} attempted to harm {} (damage: {})",
                    attacker.getName(), damaged.getType(), event.getDamage());
        }
    }

    private boolean isProtectedEntity(Entity entity) {
        return entity instanceof Cat ||
                entity instanceof Ocelot ||
                (entity instanceof Tameable && ((Tameable) entity).getOwner() != null);
    }

    private Entity getDamageSource(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();

        // Handle projectiles
        if (damager instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) damager).getShooter();
            if (shooter instanceof Entity) {
                return (Entity) shooter;
            }
        }

        return damager;
    }

    private Player getPlayerAttacker(Entity damager) {
        if (damager instanceof Player) {
            return (Player) damager;
        }

        // Handle tamed entities attacking on behalf of players
        if (damager instanceof Tameable) {
            AnimalTamer owner = ((Tameable) damager).getOwner();
            if (owner instanceof Player) {
                return (Player) owner;
            }
        }

        return null;
    }

    private double calculateRetributionDamage(double originalDamage) {
        // Return 2x the damage they tried to deal, minimum 4 hearts
        return Math.max(originalDamage * 2.0, 8.0);
    }

    private void logCatProtectionIncident(Player attacker, Entity cat, double damage) {
        String metadata = String.format("{\"cat_type\":\"%s\",\"damage\":%.2f,\"location\":\"%s\"}",
                cat.getType().name(), damage, formatLocation(cat.getLocation()));

        playerRepository.savePlayerSession(attacker, "CAT_ATTACK_BLOCKED:" + metadata);
    }

    private void applyRepeatOffenderPenalties(Player attacker) {
        // Get recent cat attack attempts
        String uuid = attacker.getUniqueId().toString();

        playerRepository.getPlayerHistory(uuid, 10)
                .thenAccept(sessions -> {
                    long recentCatAttacks = sessions.stream()
                            .filter(session -> session.getAction().startsWith("CAT_ATTACK_BLOCKED"))
                            .filter(session -> isRecentSession(session, 3600000)) // Last hour
                            .count();

                    if (recentCatAttacks >= 3) {
                        // Escalating penalties
                        applyEscalatingPenalty(attacker, (int) recentCatAttacks);
                    }
                });
    }

    private void applyEscalatingPenalty(Player attacker, int offenseCount) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            switch (offenseCount) {
                case 3:
                    // Temporary blindness
                    Object PotionEffectType = null;
                    attacker.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0));
                    attacker.sendMessage(String.valueOf(Component.text("The cat spirits cloud your vision...", NamedTextColor.DARK_PURPLE)));
                    break;
                case 5:
                    // Slowness and weakness
                    attacker.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 400, 1));
                    attacker.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 400, 1));
                    attacker.sendMessage(String.valueOf(Component.text("You feel the weight of your crimes against cats...", NamedTextColor.RED)));
                    break;
                case 7:
                    // Temporary ban from cat protection zone (if applicable)
                    Location spawnLoc = attacker.getWorld().getSpawnLocation();
                    attacker.teleport(spawnLoc);
                    attacker.sendMessage(String.valueOf(Component.text("The cat gods banish you from their domain!", NamedTextColor.DARK_RED)));

                    // Schedule a lightning strike for dramatic effect
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        attacker.getWorld().strikeLightningEffect(attacker.getLocation());
                    }, 20L);
                    break;
                default:
                    if (offenseCount >= 10) {
                        // Extreme measures - temporary server ban
                        PlayerService playerService;
                        playerService.banPlayer(attacker.getUniqueId().toString(),
                                "Repeated attacks on protected cats",
                                300000); // 5 minute ban
                        attacker.kick(Component.text("Banned for 5 minutes: Stop attacking cats!", NamedTextColor.RED));
                    }
                    break;
            }

            // Log the penalty
            playerRepository.savePlayerSession(attacker,
                    "CAT_PROTECTION_PENALTY:level_" + Math.min(offenseCount, 10));
        });
    }
}