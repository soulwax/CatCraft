package com.gray17.soul.catcraft;

import java.util.Iterator;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.md_5.bungee.api.ChatColor;

public final class Commands {
	public static CatCraft plugin;
	public static Commands c;
	public ItemStack watch;
	
	public Commands() {
		
	}

	public static void init(CatCraft ccplugin) {
		c = new Commands();
		plugin = ccplugin;
		
		
	}
	
	public static void deInit() {
		c = null;
	}

	public final void disarm(CommandSender sender, Player target, boolean shouldDisarmMainhand) {
		if (target != null) {

			ItemStack[] inventory = new ItemStack[6];
			if (target.getEquipment().getHelmet() != null) {
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
			if (target.getEquipment().getItemInOffHand() != null) {
				inventory[4] = target.getEquipment().getItemInOffHand();
				target.getInventory().setItemInOffHand(null);
			}
			if (target.getEquipment().getItemInMainHand() != null && shouldDisarmMainhand) {
				inventory[5] = target.getEquipment().getItemInMainHand();
				target.getInventory().setItemInMainHand(null);
			}

			if (sender instanceof Player) {
				Player receiver = (Player) sender;

				for (ItemStack item : inventory) {
					if (item != null)
						receiver.getInventory().addItem(item);
				}
			}
		}
	}
	
	public final void giveItem(Player player) {
		if(watch != null) {
			player.getInventory().addItem(watch);
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

	//fixed
	public final void sendMessageToAll(String[] args) {
		String message = this.constructMessage(args, 2);

		for (Player p : plugin.playerHandler.getPlayers()) {
			if (p instanceof Player && p.isOnline() && p != null) {
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
		
		if (receiver != null && receiver instanceof Player && receiver.isOnline()) {
			receiver.sendMessage(ChatColor.WHITE + "[" + ChatColor.GREEN + sender.getName() + ChatColor.WHITE + "]: " + message);
			sender.sendMessage(ChatColor.WHITE + "[" + sender.getName() + ChatColor.GREEN + " ==> " + ChatColor.WHITE + receiver.getDisplayName() + ": " + message);
		} else {
			if(receiver == null) {
				sender.sendMessage(ChatColor.WHITE + "[" + sender.getName() + ChatColor.RED + " =//=> " +ChatColor.WHITE + "]: " + message);
				sender.sendMessage("[CatCraft]: " + ChatColor.GRAY + "Could not deliver message, receiving player is offline. Use /mail instead.");
			}	
		}
	}

	private final String constructMessage(String[] args, int startingIndex) {
		StringBuilder sb = new StringBuilder();

		for (int i = startingIndex; i < args.length; ++i) {
			if (i != startingIndex) {
				sb.append(' ');
			}

			sb.append(args[i]);
		}

		return sb.toString();
	}

	public final void getActivePlayers(boolean isPlayer) {
		@SuppressWarnings("rawtypes")
		Iterator var2;
		Player p;
		if (!isPlayer) {
			var2 = plugin.playerHandler.getPlayers().iterator();

			while (var2.hasNext()) {
				p = (Player) var2.next();
				System.out.println(p.getName());
			}
		} else {
			var2 = plugin.playerHandler.getPlayers().iterator();

			while (var2.hasNext()) {
				p = (Player) var2.next();
				plugin.getLogger().info(p.getName());
			}
		}

	}

	public final void help(CommandSender sender) {
		if (sender instanceof Player) {
			sender.sendMessage(
					"\n\n------------------------------\n\'/catcraft\' - global command prefix that adresses catnet commands.\n-msg [Player<optionally:all>] [message]: sends the target an anonymous message.\n-inv [Player]: peeks into the player\'s inventory.\n-ender [Player]: peeks into the player\'s ender chest.\n-disarm [Player]: steals the target\'s armor slot contents.\n-rules: displays the server rules (may not be updated yet)\n-help: displays all possible commands\n-credits: plugin credits\n------------------------------\n");
		} else {
			plugin.getLogger().info(
					"\n\n------------------------------\n\'/catcraft\' - global command prefix that adresses catnet commands.\n-msg [Player<optionally:all>] [message]: sends the target an anonymous message.\n-inv [Player]: peeks into the player\'s inventory.\n-ender [Player]: peeks into the player\'s ender chest.\n-disarm [Player]: steals the target\'s armor slot contents.\n-rules: displays the server rules (may not be updated yet)\n-help: displays all possible commands\n-credits: plugin credits\n------------------------------\nconsole only commands: \n-reload: reloads CatCraft, used when changes were applied to the config.yml during runtime\n------------------------------\n\n");
		}

	}

	@SuppressWarnings("static-access")
	public final void credits(CommandSender sender) {
		if (sender instanceof Player) {
			sender.sendMessage(plugin.credits);
		} else {
			plugin.getLogger().info(plugin.credits);
		}

	}

	public final void rules(CommandSender sender) {
		@SuppressWarnings("rawtypes")
		List rules = plugin.getConfig().getStringList("rules");
		@SuppressWarnings("rawtypes")
		Iterator var3 = rules.iterator();

		while (var3.hasNext()) {
			String s = (String) var3.next();
			sender.sendMessage(s);
		}

	}
}
