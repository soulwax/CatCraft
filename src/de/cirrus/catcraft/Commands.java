package de.cirrus.catcraft;

import java.util.Iterator;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class Commands {
    public static CatCraft plugin;
    public static Commands c;

    public Commands() {
    }

    public static void init(CatCraft plugin) {
        c = new Commands();
        plugin = plugin;
    }

    public final void disarm(Player sender, Player target) {
        if(target != null) {
            if(target.getEquipment().getHelmet() != null) {
                ItemStack helmet = target.getEquipment().getHelmet();
                sender.getInventory().addItem(new ItemStack[]{helmet});
                target.getEquipment().setHelmet((ItemStack)null);
            }

            if(target.getEquipment().getChestplate() != null) {
                ItemStack chestplate = target.getEquipment().getChestplate();
                sender.getInventory().addItem(new ItemStack[]{chestplate});
                target.getEquipment().setChestplate((ItemStack)null);
            }

            if(target.getEquipment().getLeggings() != null) {
                ItemStack leggings = target.getEquipment().getLeggings();
                sender.getInventory().addItem(new ItemStack[]{leggings});
                target.getEquipment().setLeggings((ItemStack)null);
            }

            if(target.getEquipment().getBoots() != null) {
                ItemStack boots = target.getEquipment().getBoots();
                sender.getInventory().addItem(new ItemStack[]{boots});
                target.getEquipment().setBoots((ItemStack)null);
            }

        }
    }

    public final void openInventory(Player sender, Player target) {
        if(target != null && target.isOnline()) {
            PlayerInventory inventory = target.getInventory();
            sender.openInventory(inventory);
        }

    }

    public final void openEnderInventory(Player sender, Player target) {
        if(target != null) {
            Inventory inventory = target.getEnderChest();
            sender.openInventory(inventory);
        }

    }

    public final void sendMessageToAll(String[] args) {
        String message = this.constructMessage(args, 2);
        Iterator var3 = plugin.playerHandler.getPlayers().iterator();

        while(var3.hasNext()) {
            Player p = (Player)var3.next();
            if(p instanceof Player && p.isOnline() && p != null) {
                p.sendMessage(message);
            }
        }

    }

    public final void sendMessage(Player receiver, CommandSender sender, String[] args) {
        if(receiver != null && receiver instanceof Player && receiver.isOnline()) {
            String message = this.constructMessage(args, 2);
            receiver.sendMessage(message);
            if(InputHandler.VERBOSE) {
                sender.sendMessage("Message delivered to " + receiver.getDisplayName() + ".");
            }
        } else if(InputHandler.VERBOSE) {
            sender.sendMessage("Could not deliver message, receiving player seems not to be connected.");
        }

    }

    private final String constructMessage(String[] args, int startingIndex) {
        StringBuilder sb = new StringBuilder();

        for(int i = startingIndex; i < args.length; ++i) {
            if(i != startingIndex) {
                sb.append(' ');
            }

            sb.append(args[i]);
        }

        return sb.toString();
    }

    public final void getActivePlayers(boolean isPlayer) {
        Iterator var2;
        Player p;
        if(!isPlayer) {
            var2 = plugin.playerHandler.getPlayers().iterator();

            while(var2.hasNext()) {
                p = (Player)var2.next();
                System.out.println(p.getName());
            }
        } else {
            var2 = plugin.playerHandler.getPlayers().iterator();

            while(var2.hasNext()) {
                p = (Player)var2.next();
                plugin.getLogger().info(p.getName());
            }
        }

    }

    public final void help(CommandSender sender) {
        if(sender instanceof Player) {
            sender.sendMessage("\n\n------------------------------\n\'/catcraft\' - global command prefix that adresses catnet commands.\n-msg [Player<optionally:all>] [message]: sends the target an anonymous message.\n-inv [Player]: peeks into the player\'s inventory.\n-ender [Player]: peeks into the player\'s ender chest.\n-disarm [Player]: steals the target\'s armor slot contents.\n-rules: displays the server rules (may not be updated yet)\n-help: displays all possible commands\n-credits: plugin credits\n------------------------------\n");
        } else {
            plugin.getLogger().info("\n\n------------------------------\n\'/catcraft\' - global command prefix that adresses catnet commands.\n-msg [Player<optionally:all>] [message]: sends the target an anonymous message.\n-inv [Player]: peeks into the player\'s inventory.\n-ender [Player]: peeks into the player\'s ender chest.\n-disarm [Player]: steals the target\'s armor slot contents.\n-rules: displays the server rules (may not be updated yet)\n-help: displays all possible commands\n-credits: plugin credits\n------------------------------\nconsole only commands: \n-reload: reloads CatCraft, used when changes were applied to the config.yml during runtime\n------------------------------\n\n");
        }

    }

    public final void credits(CommandSender sender) {
        if(sender instanceof Player) {
            sender.sendMessage("\n\n--------credits----------\nserver: catnet.de\nplugin author: cirrus\ncontact: cirrus@catnet.de\nspecial thanks to: sh4ni\n---------------------------\n\n");
        } else {
            plugin.getLogger().info("\n\n--------credits----------\nserver: catnet.de\nplugin author: cirrus\ncontact: cirrus@catnet.de\nspecial thanks to: sh4ni\n---------------------------\n\n");
        }

    }

    public final void rules(CommandSender sender) {
        List rules = plugin.getConfig().getStringList("rules");
        Iterator var3 = rules.iterator();

        while(var3.hasNext()) {
            String s = (String)var3.next();
            sender.sendMessage(s);
        }

    }
}
