package com.gray17.soul.catcraft;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.md_5.bungee.api.ChatColor;

import static com.gray17.soul.catcraft.InputHandler.setFormat;

public final class Commands {
	public static CatCraft plugin;
	public static Commands c;
	public static final String HELP_MESSAGE = "\n\n------------------------------\n'/catcraft - alias: /cc' - global command prefix that addresses oakheim.com commands.\n/ccw <player> <message>: sends a whisper to a target player.\n\n-msgall <message>: sends everyone currently online an anonymous message.\n-inv <player>: peeks into the player's inventory.\n-ender <player>: peeks into the player's ender chest.\n-disarm <player>: steals the target's armor slot contents (and optionally the main hand object).\n-rules: displays the server rules if there are any.\n-help: displays all commands\n-credits: plugin credits\n------------------------------\n";
	public static final String CREDITS_MESSAGE = "\n\n--------credits----------\nAuthor: sou1wax / Discord: soulwax#5586\nSource: github.com/Korriban/CatCraft\nserver: oakheim.com\nSpecial thanks to: Morrigan for hosting\n---------------------------\n\n";
	public Commands() {
		
	}

	public static void init(CatCraft ccplugin) {
		c = new Commands();
		plugin = ccplugin;
	}

	public final void disarm(CommandSender sender, Player target, boolean shouldDisarmMainhand) {
		if (target != null) {

			ItemStack[] inventory = new ItemStack[6];
			if (Objects.requireNonNull(target.getEquipment()).getHelmet() != null) {
				inventory[0] = target.getEquipment().getHelmet();
				target.getInventory().setHelmet(null);
			}
			if (target.getEquipment().getChestplate() != null) {
				inventory[1] = target.getEquipment().getChestplate();
				target.getInventory().setChestplate(null);
			}
			if (target.getEquipment().getLeggings() != null) {
				inventory[2] = target.getEquipment().getLeggings();
				target.getInventory().setLeggings(null);
			}
			if (target.getEquipment().getBoots() != null) {
				inventory[3] = target.getEquipment().getBoots();
				target.getInventory().setBoots(null);
			}
			target.getEquipment().getItemInOffHand();
			inventory[4] = target.getEquipment().getItemInOffHand();
			target.getInventory().setItemInOffHand(null);
			if (shouldDisarmMainhand) {
				inventory[5] = target.getEquipment().getItemInMainHand();
				target.getInventory().setItemInMainHand(null);
			}

			if (sender instanceof Player receiver) {

				for (ItemStack item : inventory) {
					if (item != null)
						receiver.getInventory().addItem(item);
				}
			}
		}
	}

	public final void openInventory(Player sender, Player target) {
		if (target != null && target.isOnline()) {
			PlayerInventory inventory = target.getInventory();
			sender.openInventory(inventory);
		}

	}

	public final void openEnderInventory(Player sender, Player target) {
		if (target != null) {
			Inventory inventory = target.getEnderChest();
			sender.openInventory(inventory);
		}

	}

	public final void sendMessageToAll(CommandSender sender, String[] args) {
		String message;
		//anon <message> - args 0 is starting index
		message = this.constructMessage(args, 0);
		String messageModified = InputHandler.findAndReplaceEmojiRND(message);
		if(messageModified.isEmpty()) messageModified = message;
		String formattedMessage = InputHandler.setFormat(sender, messageModified);


		for (Player p : plugin.playerHandler.getPlayers()) {
			if ((p != null) && p.isOnline()) {
				p.sendMessage(formattedMessage);
			}
		}

	}

	public final void sendAnonMessageToAll(String[] args) {
		String message = constructMessage(args,1); // Use case: /cc msgall <player> - index 1
		for (Player p : plugin.playerHandler.getPlayers()) {
			if ((p != null) && p.isOnline()) {
				p.sendMessage(message);
			}
		}
	}

	public final void sendMessage(Player receiver, CommandSender sender, String[] args) {
		
		String message = "";
		if (args.length > 1) {
			message = this.constructMessage(args, 1);
		}
		
		if (message.isEmpty()) {
			sender.sendMessage("[Catcraft]: Empty message. Try again");
			return;
		}
		
		if (receiver != null && receiver.isOnline()) {
			receiver.sendMessage(ChatColor.WHITE + "[" + ChatColor.GREEN + sender.getName() + ChatColor.WHITE + "] (whispers): " + message);
			sender.sendMessage(ChatColor.WHITE + "[" + sender.getName() + ChatColor.GREEN + " ==> " + ChatColor.WHITE + receiver.getDisplayName() + "]: " + message);
		} else {
			if(receiver == null) {
				sender.sendMessage(ChatColor.WHITE + "[" + sender.getName() + ChatColor.RED + " =//=> " +ChatColor.WHITE + "]: " + message);
				sender.sendMessage("[CatCraft]: " + ChatColor.GRAY + "Could not deliver message, receiving player is offline. Use /mail instead.");
			}	
		}
	}

	private String constructMessage(String[] args, int startingIndex) {
		StringBuilder sb = new StringBuilder();

		// usecase /cc msgall <message>: argument 0 -> "msgall"; argument 1..2..x: message string
		// usecase /ccw <player> <message>: argument 0 -> player nameM argument 1..2..x: message string
		// usecase /typo <message: argument 0 -> message
		// ==> hence always starting index at 1 !
		for (int i = startingIndex; i < args.length; ++i) {
			if (i != startingIndex) {
				sb.append(' ');
			}
			sb.append(args[i]);
		}

		return sb.toString();
	}

	public final void help(CommandSender sender) {
		if (sender instanceof Player) {
			sender.sendMessage(Commands.HELP_MESSAGE);
		} else {
			plugin.getLogger().info(Commands.HELP_MESSAGE);
		}

	}

	public final void credits(CommandSender sender) {
		if (sender instanceof Player) {
			sender.sendMessage(Commands.CREDITS_MESSAGE);
		} else {
			plugin.getLogger().info(Commands.CREDITS_MESSAGE);
		}

	}

	public final void rules(CommandSender sender) {
		List<String> rules = plugin.getConfig().getStringList("rules");

		for (String s : rules) {
			sender.sendMessage(s);
		}

	}
}
