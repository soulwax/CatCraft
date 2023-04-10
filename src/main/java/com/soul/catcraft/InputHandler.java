package com.soul.catcraft;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.World;


import static com.soul.catcraft.ConfigFile.*;
import com.soul.catcraft.emoji.EmojiLibrary;

import java.util.ArrayList;
import java.util.List;

public class InputHandler implements Listener {

    private final PlayerHandler playerHandler;
    private final CatCraft plugin;
    private final Debugger debugger;
    private List<Class<? extends LivingEntity>> defendedEntities = new ArrayList<Class<? extends LivingEntity>>();

    public InputHandler(CatCraft plugin, Debugger debugger) {
        this.plugin = plugin;
        this.debugger = debugger;
        this.playerHandler = this.plugin.playerHandler;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);

        // ? Defended entities, add more here
        defendedEntities.add(Ocelot.class);
        defendedEntities.add(Cat.class);
    }

    public void init() {
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void playerConnects(PlayerJoinEvent e) {
        this.playerHandler.addPlayer(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void playerDisconnects(PlayerQuitEvent e) {
        this.playerHandler.removePlayer(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void catDeath(EntityDeathEvent event) {
        LivingEntity entity = (LivingEntity) event.getEntity(); // seems redudant, but it changed since I wrote this a
                                                                // lot
        if (isDefendedEntity(entity)) {
            logOcelotDeath();

            Player killer = entity.getKiller();
            if (killer != null) {
                punishKiller(killer);
                notifyPlayers(killer);
                inflictThunderstorm(entity.getWorld());
            }
        }
    }

    private void logOcelotDeath() {
        if (VERBOSE) {
            debugger.info("Ocelot died");
        }
    }

    private void punishKiller(Player killer) {
        double damage = 9000.0D;
        killer.damage(damage);
        logKillerDamage(killer, damage);
    }

    private void logKillerDamage(Player killer, double damage) {
        if (VERBOSE) {
            debugger.info(killer + " received " + damage + " damage");
        }
    }

    private void notifyPlayers(Player killer) {
        String message = ChatColor.DARK_RED + "" + ChatColor.BOLD + killer.getName()
                + " killed a cat and is now receiving his righteous judgement!";
        for (Player player : playerHandler.getPlayers()) {
            player.sendMessage(message);
        }
    }

    private void inflictThunderstorm(World world) {
        world.setStorm(true);
        world.setThundering(true);
        world.setThunderDuration(1000);
        logThunderstormInflicted();
    }

    private void logThunderstormInflicted() {
        if (VERBOSE) {
            debugger.info("Thunderstorm inflicted");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void ward(EntityDamageByEntityEvent evt) {
        double damage = evt.getDamage();
        Entity damaged = evt.getEntity();
        Entity damager = getDamagerFromEvent(evt);

        logDamageEvent(damaged, damager, damage);

        if (isDefendedEntity(damaged)) {
            Player offender = getOffenderFromDamager(damager);
            if (offender != null) {
                offender.damage(damage);
                logOffenderDamage(offender, damage);
            }
        }
    }

    private Entity getDamagerFromEvent(EntityDamageByEntityEvent evt) {
        if (evt.getDamager() instanceof Arrow) {
            return (Entity) ((Arrow) evt.getDamager()).getShooter();
        }
        return evt.getDamager();
    }

    private void logDamageEvent(Entity damaged, Entity damager, double damage) {
        if (!VERBOSE) {
            return;
        }

        if (shouldLogDamageEvent(damaged, damager)) {
            String message = buildDamageMessage(damaged, damager, damage);
            debugger.info(message);
        }
    }

    private boolean shouldLogDamageEvent(Entity damaged, Entity damager) {
        return !VERBOSE_PLAYER_ONLY || (damaged instanceof Player && damager instanceof Player);
    }

    private String buildDamageMessage(Entity damaged, Entity damager, double damage) {
        String message = damaged + " was harmed by " + damager + " of the type " + damager.getType() + ", damage: "
                + damage;

        if (damager instanceof Arrow || damager instanceof Fireball) {
            ProjectileSource attacker = damager instanceof Arrow ? ((Arrow) damager).getShooter()
                    : ((Fireball) damager).getShooter();
            message += ", shooter: " + attacker;
        }

        return message;
    }

    private boolean isDefendedEntity(Entity entity) {
        for (Class<? extends LivingEntity> defendedEntity : defendedEntities) {
            if (defendedEntity.isInstance(entity)) {
                return true;
            }
        }
        return false;
    }

    private Player getOffenderFromDamager(Entity damager) {
        if (damager instanceof Player) {
            return (Player) damager;
        } else if (damager instanceof Arrow) {
            return (Player) ((Arrow) damager).getShooter();
        }
        return null;
    }

    private void logOffenderDamage(Player offender, double damage) {
        if (VERBOSE) {
            debugger.info(offender + " received " + damage + " damage");
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void playerInteract(InventoryOpenEvent evt) {
        if (!SHOULD_RELAY_CHESTS && !VERBOSE)
            return;

        HumanEntity player = evt.getPlayer();
        Location loc = player.getLocation();
        evt.getInventory().getType();
        debugger.info("Player " + player.getName() + " opened " + evt.getInventory().getType()
                + " at location: x=" + loc.getBlockX() + " y=" + loc.getBlockY() + " z=" + loc.getBlockZ());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerSentMessage(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        String message = event.getMessage();
        String messageModified = EmojiLibrary.findAndReplaceEmojiRND(message);

        if (messageModified.isEmpty()) {
            messageModified = message;
        }

        if (event.isAsynchronous()) {
            String formatResult = InputHandler.setFormat(p, messageModified);
            event.setFormat(formatResult);
            event.setMessage(messageModified);
        }
    }

    public static String setFormat(CommandSender sender, String message) {
        if (sender instanceof Player player) {
            return formatPlayerMessage(player, message);
        } else {
            return formatConsoleMessage(message);
        }
    }

    private static String formatPlayerMessage(Player player, String message) {
        ChatColor roleColor;
        String role;

        if (player.hasPermission("chat.format.admin")) {
            roleColor = ChatColor.DARK_RED;
            role = "ADMIN";
        } else if (player.hasPermission("chat.format.moderator")) {
            roleColor = ChatColor.RED;
            role = "MODERATOR";
        } else {
            roleColor = ChatColor.DARK_GREEN;
            role = "MEMBER";
        }

        return ChatColor.DARK_GRAY + "[" + roleColor + role + ChatColor.DARK_GRAY + "] "
                + roleColor + player.getDisplayName() + ChatColor.DARK_GRAY + ": " + ChatColor.WHITE + message;
    }

    private static String formatConsoleMessage(String message) {
        return ChatColor.DARK_GRAY + "[" + ChatColor.MAGIC + "SERVER" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE
                + "CONSOLE" + ChatColor.DARK_GRAY + ": " + ChatColor.WHITE + message;
    }
}
