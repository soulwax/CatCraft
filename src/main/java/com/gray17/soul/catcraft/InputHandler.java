package com.gray17.soul.catcraft;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.HumanEntity;
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

import com.gray17.soul.catcraft.emoji.EmojiLibrary;

import java.util.concurrent.atomic.AtomicInteger;

public class InputHandler implements Listener {
	public static boolean VERBOSE;
	public static boolean VERBOSE_PLAYER_ONLY;
	public static boolean IS_GET_CMD_ACTIVATED;
	private final PlayerHandler playerHandler;
	private final CatCraft plugin;
	private final Debugger debugger;

	public InputHandler(CatCraft plugin, Debugger debugger) {
		this.plugin = plugin;
		this.debugger = debugger;
		this.playerHandler = this.plugin.playerHandler;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	public void init() {
		VERBOSE = this.plugin.getConfig().getBoolean("verbose");
		IS_GET_CMD_ACTIVATED = this.plugin.getConfig().getBoolean("get command activated");
		VERBOSE_PLAYER_ONLY = this.plugin.getConfig().getBoolean("verbose player only");

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

	public void catDeath(EntityDeathEvent e) {
		if (e.getEntity() instanceof Ocelot || e.getEntity() instanceof Cat) {
			if (VERBOSE) {
				debugger.info("Ocelot died");
			}

			if (e.getEntity().getKiller() != null && e.getEntity().getKiller() != null) {
				Player p = e.getEntity().getKiller();
				p.damage(9000.0D);
				if (VERBOSE) {
					debugger.info(p + " received 9000 damage");
				}

				for (Player all : this.playerHandler.getPlayers()) {
					all.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + p.getName() + " killed a cat "
							+ "and is now recieving his righteous judgement!");
				}

				e.getEntity().getWorld().setStorm(true);
				e.getEntity().getWorld().setThundering(true);
				e.getEntity().getWorld().setThunderDuration(1000);
				if (VERBOSE) {
					debugger.info("Thunderstorm inflicted");
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void ward(EntityDamageByEntityEvent evt) {

		double dmg = evt.getDamage();
		Entity damaged = evt.getEntity();
		Entity damager = evt.getDamager() instanceof Arrow ? (Entity) ((Arrow) evt.getDamager()).getShooter()
				: evt.getDamager();
		if (VERBOSE) {
			// if verbose mode is only for player vs player interaction AND (thus); if the
			// damager is not a player AND the damaged not one, do nothing
			if (!VERBOSE_PLAYER_ONLY || (damaged instanceof Player && damager instanceof Player))
				if (evt.getDamager() instanceof Arrow) {
					ProjectileSource attacker = ((Arrow) evt.getDamager()).getShooter();
					debugger.info(evt.getEntity() + " was harmed by " + evt.getDamager() + " of the type "
							+ evt.getDamager().getType() + ", shooter: " + attacker + ", damage: " + dmg);
				} else if (evt.getDamager() instanceof Fireball) {
					ProjectileSource attacker = ((Fireball) evt.getDamager()).getShooter();
					debugger.info(evt.getEntity() + " was harmed by " + evt.getDamager() + " of the type "
							+ evt.getDamager().getType() + ", shooter: " + attacker + ", damage: " + dmg);
				} else {
					debugger.info(evt.getEntity() + " was harmed by " + evt.getDamager() + " of the type "
							+ evt.getDamager().getType() + ", damage: " + dmg);
				}
		}

		if (evt.getEntity() instanceof Ocelot || evt.getEntity() instanceof Cat) {
			if (VERBOSE) {
				this.plugin.getLogger().info("A defended entity has been attacked! Type: " + evt.getEntity());
			}

			Player offender;
			Entity ddealer = evt.getDamager();
			if (ddealer instanceof Player) {
				if (VERBOSE) {
					debugger.info("Damager :" + ddealer + " is a Player");
				}

				offender = (Player) ddealer;
				offender.damage(dmg);
				if (VERBOSE) {
					debugger.info(ddealer + " received " + dmg + " damage");
				}
			} else if (ddealer instanceof Arrow) {
				if (VERBOSE) {
					debugger.info("Damager was an arrow, owner: " + ((Arrow) ddealer).getShooter());
				}

				offender = (Player) ((Arrow) ddealer).getShooter();
				if(offender != null) {
					offender.damage(dmg);
					if (VERBOSE) {
						debugger.info(((Arrow) ddealer).getShooter() + " received " + dmg + " damage.");
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void playerInteract(InventoryOpenEvent evt) {
		if(!CatCraft.shouldRelayChestOpenings && !InputHandler.VERBOSE) return;

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
		String messageModified = findAndReplaceEmojiRND(message);

		if (messageModified.isEmpty()) {
			messageModified = message;
		}

		String result;
		if (event.isAsynchronous()) {
			// Formatting
			if (p.hasPermission("chat.format.member")) {
				result = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_GREEN + "MEMBER" + ChatColor.DARK_GRAY + "] "
						+ ChatColor.DARK_GREEN + p.getDisplayName() + ChatColor.DARK_GRAY + ": " + ChatColor.WHITE
						+ messageModified;
				// This will give the player with that permission node that Chat format.
			} else if (p.hasPermission("chat.format.moderator")) {
				result = ChatColor.DARK_GRAY + "[" + ChatColor.RED + "MODERATOR" + ChatColor.DARK_GRAY + "] " + ChatColor.RED
								+ p.getDisplayName() + ChatColor.DARK_GRAY + ": " + ChatColor.WHITE + messageModified;
				// This will give the player with that permission node that Chat format.
			} else if (p.hasPermission("chat.format.admin")) {
				result = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "ADMIN" + ChatColor.DARK_GRAY + "] "
						+ ChatColor.DARK_RED + p.getDisplayName() + ChatColor.DARK_GRAY + ": " + ChatColor.WHITE
						+ messageModified;
				// This will give the player with that permission node that Chat format.
			} else {
				result = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_GREEN + "MEMBER" + ChatColor.DARK_GRAY + "] "
						+ ChatColor.DARK_GREEN + p.getDisplayName() + ChatColor.DARK_GRAY + ": " + ChatColor.WHITE
						+ messageModified;
			}
			event.setFormat(result);
			event.setMessage(messageModified);
		}
	}

	/* replace normal emote with emoji */
	private String findAndReplaceEmojiRND(String original) {
		String result = "";

		String[] cases = {":)", ":D", ":(", ">:(", ":O", "o/"};
		int i;
		for(i = 0; i < cases.length; i++)
			if (original.contains(cases[i]))
				break;

		String emojiResult = "";
		switch (i) {
			case 0 -> emojiResult += EmojiLibrary.getRandomEmoji(EmojiLibrary.SYMPATHY_EMOJI);
			case 1 -> emojiResult += EmojiLibrary.getRandomEmoji(EmojiLibrary.JOY_EMOJI);
			case 2 -> emojiResult += EmojiLibrary.getRandomEmoji(EmojiLibrary.SADNESS_EMOJI);
			case 3 -> emojiResult += EmojiLibrary.getRandomEmoji(EmojiLibrary.AMGER_EMOJI);
			case 4 -> emojiResult += EmojiLibrary.getRandomEmoji(EmojiLibrary.SURPRISE_EMOJI);
			case 5 -> emojiResult += EmojiLibrary.getRandomEmoji(EmojiLibrary.GREETING_EMOJI);
			default -> emojiResult = "";
		}

		if (!emojiResult.isEmpty()) result = original.replace(cases[i], emojiResult);

		return result;
	}
}
