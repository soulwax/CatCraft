package com.soul.catcraft;

import com.soul.catcraft.emoji.EmojiLibrary;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
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

import static com.soul.catcraft.ConfigFile.*;
import static com.soul.catcraft.Constants.*;
import static com.soul.catcraft.Constants.ChatRoles.*;

public class InputHandler implements Listener {

    private final PlayerHandler playerHandler;
    private final Debugger debugger;

    public InputHandler(CatCraft plugin, Debugger debugger) {
        this.debugger = debugger;
        this.playerHandler = plugin.playerHandler;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void init() {
        // Initialization logic if needed
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
        LivingEntity entity = event.getEntity();
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
            debugger.info(Constants.DebugMessages.PROTECTED_ENTITY_DIED);
        }
    }

    private void punishKiller(Player killer) {
        killer.damage(PUNISHING_DAMAGE);
        logKillerDamage(killer, PUNISHING_DAMAGE);
    }

    private void logKillerDamage(Player killer, double damage) {
        if (VERBOSE) {
            debugger.info(String.format(Constants.DebugMessages.KILLER_DAMAGE_FORMAT, killer, damage));
        }
    }

    private void notifyPlayers(Player killer) {
        String message = String.format(Constants.NotificationMessages.CAT_KILLER_PUNISHMENT, killer.getName());
        for (Player player : playerHandler.getPlayers()) {
            player.sendMessage(message);
        }
    }

    private void inflictThunderstorm(World world) {
        world.setStorm(true);
        world.setThundering(true);
        world.setThunderDuration(THUNDERSTORM_DURATION);
        logThunderstormInflicted();
    }

    private void logThunderstormInflicted() {
        if (VERBOSE) {
            debugger.info(Constants.DebugMessages.THUNDERSTORM_INFLICTED);
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
        Entity damager = evt.getDamager();
        if (damager instanceof Arrow arrow) {
            ProjectileSource shooter = arrow.getShooter();
            return shooter instanceof Entity ? (Entity) shooter : damager;
        }
        return damager;
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
        String message = String.format(Constants.DebugMessages.DAMAGE_EVENT_FORMAT,
                damaged, damager, damager.getType(), damage);

        if (damager instanceof Arrow || damager instanceof Fireball) {
            ProjectileSource attacker = damager instanceof Arrow ? ((Arrow) damager).getShooter()
                    : ((Fireball) damager).getShooter();
            message += String.format(Constants.DebugMessages.DAMAGE_EVENT_SHOOTER_FORMAT, attacker);
        }

        return message;
    }

    private boolean isDefendedEntity(Entity entity) {
        return DEFENDED_ENTITIES.stream()
                .anyMatch(defendedClass -> defendedClass.isInstance(entity));
    }

    private Player getOffenderFromDamager(Entity damager) {
        if (damager instanceof Player) {
            return (Player) damager;
        } else if (damager instanceof Arrow) {
            ProjectileSource shooter = ((Arrow) damager).getShooter();
            return shooter instanceof Player ? (Player) shooter : null;
        }
        return null;
    }

    private void logOffenderDamage(Player offender, double damage) {
        if (VERBOSE) {
            debugger.info(String.format(Constants.DebugMessages.KILLER_DAMAGE_FORMAT, offender, damage));
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void playerInteract(InventoryOpenEvent evt) {
        if (!SHOULD_RELAY_CHESTS && !VERBOSE)
            return;

        HumanEntity player = evt.getPlayer();
        Location loc = player.getLocation();
        debugger.info(String.format(Constants.DebugMessages.PLAYER_INVENTORY_FORMAT,
                player.getName(), evt.getInventory().getType(),
                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
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
            String formatResult = setFormat(p, messageModified);
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

        if (player.hasPermission(ADMIN_PERMISSION)) {
            roleColor = ADMIN_COLOR;
            role = ADMIN_ROLE;
        } else if (player.hasPermission(MODERATOR_PERMISSION)) {
            roleColor = MODERATOR_COLOR;
            role = MODERATOR_ROLE;
        } else {
            roleColor = MEMBER_COLOR;
            role = MEMBER_ROLE;
        }

        return Constants.ChatFormatting.BRACKET_COLOR + "[" + roleColor + role + Constants.ChatFormatting.BRACKET_COLOR
                + "] "
                + roleColor + player.getDisplayName() + Constants.ChatFormatting.BRACKET_COLOR + ": "
                + Constants.ChatFormatting.MESSAGE_COLOR + message;
    }

    private static String formatConsoleMessage(String message) {
        return Constants.ChatFormatting.BRACKET_COLOR + "[" + Constants.ChatFormatting.SERVER_MAGIC + "SERVER"
                + Constants.ChatFormatting.BRACKET_COLOR + "] " + Constants.ChatFormatting.MESSAGE_COLOR
                + "CONSOLE" + Constants.ChatFormatting.BRACKET_COLOR + ": " + Constants.ChatFormatting.MESSAGE_COLOR
                + message;
    }
}