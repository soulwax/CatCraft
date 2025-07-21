// File: src/main/java/com/soul/catcraft/Commands.java

package com.soul.catcraft;

import com.soul.catcraft.emoji.EmojiLibrary;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.md_5.bungee.api.ChatColor;

import static com.soul.catcraft.ConfigFile.RULES_CONFIG;

public final class Commands {
    public static CatCraft plugin;
    public static Commands c;

    public static final String HELP_MESSAGE = """
            ------------------------------
            '/catcraft - alias: /cc' - global command prefix that addresses oakheim.com commands.
            /ccw <player> <message>: sends a whisper to a target player.

            /anon <message>: sends a message that gets bypassed by the discord bot.

            -msgall <message>: sends everyone currently online an anonymous message.
            -inv <player>: peeks into the player's inventory.
            -ender <player>: peeks into the player's ender chest.
            -disarm <player>: steals the target's armor slot contents (and optionally the main hand object).
            -rules: displays the server rules if there are any.
            -help: displays all commands
            -credits: plugin credits
            ------------------------------
            """;
    public static final String CREDITS_MESSAGE = """


            --------credits----------
            Author on Github: soulwax - ingame: sou1wax - discord: soulwax#5473
            Source: github.com/soulwax/CatCraft
            server: ratcraft.org
            Dynmap: map.ratcraft.org
            Special thanks to: Morrigan for hosting
            ---------------------------

            """;

    public Commands() {
    }

    public static void init(CatCraft ccplugin) {
        c = new Commands();
        plugin = ccplugin;
    }

    public void disarm(CommandSender sender, Player target, boolean shouldDisarmMainhand) {
        if (target == null) {
            return;
        }

        ItemStack[] inventory = disarmTarget(target, shouldDisarmMainhand);

        if (sender instanceof Player receiver) {
            addItemsToReceiverInventory(inventory, receiver);
        }
    }

    private ItemStack[] disarmTarget(Player target, boolean shouldDisarmMainhand) {
        ItemStack[] inventory = new ItemStack[6];
        PlayerInventory targetInventory = target.getInventory();
        PlayerInventory equipment = (PlayerInventory) (target).getEquipment();

        inventory[0] = tryRemovingItem(equipment.getHelmet(), targetInventory, "helmet");
        inventory[1] = tryRemovingItem(equipment.getChestplate(), targetInventory, "chestplate");
        inventory[2] = tryRemovingItem(equipment.getLeggings(), targetInventory, "leggings");
        inventory[3] = tryRemovingItem(equipment.getBoots(), targetInventory, "boots");
        inventory[4] = tryRemovingItem(equipment.getItemInOffHand(), targetInventory, "offHand");

        if (shouldDisarmMainhand) {
            inventory[5] = tryRemovingItem(equipment.getItemInMainHand(), targetInventory, "mainHand");
        }

        return inventory;
    }

    private ItemStack tryRemovingItem(ItemStack item, PlayerInventory inventory, String type) {
        if (item == null) {
            return null;
        }

        switch (type) {
            case "helmet":
                inventory.setHelmet(null);
                break;
            case "chestplate":
                inventory.setChestplate(null);
                break;
            case "leggings":
                inventory.setLeggings(null);
                break;
            case "boots":
                inventory.setBoots(null);
                break;
            case "offHand":
                inventory.setItemInOffHand(null);
                break;
            case "mainHand":
                inventory.setItemInMainHand(null);
                break;
        }

        return item;
    }

    private void addItemsToReceiverInventory(ItemStack[] inventory, Player receiver) {
        for (ItemStack item : inventory) {
            if (item != null) {
                receiver.getInventory().addItem(item);
            }
        }
    }

    public void openInventory(Player sender, Player target) {
        if (target != null && target.isOnline()) {
            PlayerInventory inventory = target.getInventory();
            sender.openInventory(inventory);
        }

    }

    public void openEnderInventory(Player sender, Player target) {
        if (target != null) {
            Inventory inventory = target.getEnderChest();
            sender.openInventory(inventory);
        }

    }

    public void sendMessageToAll(CommandSender sender, String[] args) {
        String message;
        // anon <message> - args 0 is starting index
        message = this.constructMessage(args, 0);
        String messageModified = EmojiLibrary.findAndReplaceEmojiRND(message);
        if (messageModified.isEmpty())
            messageModified = message;
        String formattedMessage = InputHandler.setFormat(sender, messageModified);

        for (Player p : plugin.playerHandler.getPlayers()) {
            if ((p != null) && p.isOnline()) {
                p.sendMessage(formattedMessage);
            }
        }
    }

    public String sendAnonMessageToAll(String[] args) {
        String message = constructMessage(args, 1); // Use case: /cc msgall <player> - index 1
        for (Player p : plugin.playerHandler.getPlayers()) {
            if ((p != null) && p.isOnline()) {
                p.sendMessage(message);
            }
        }

        return message;
    }

    public void sendMessage(Player receiver, CommandSender sender, String[] args) {
        String message = constructAndFormatMessage(args);

        if (message.isEmpty()) {
            sender.sendMessage("[Catcraft]: Empty message. Try again");
            return;
        }

        if (receiver != null && receiver.isOnline()) {
            sendWhisperMessage(sender, receiver, message);
        } else {
            sendUndeliveredMessage(sender, receiver);
        }
    }

    private String constructAndFormatMessage(String[] args) {
        if (args.length < 1) {
            return "";
        }

        String message = constructMessage(args, 1);
        return EmojiLibrary.findAndReplaceEmojiRND(message);
    }

    private void sendWhisperMessage(CommandSender sender, Player receiver, String message) {
        receiver.sendMessage(ChatColor.WHITE + "[" + ChatColor.GREEN + sender.getName() + ChatColor.WHITE
                + "] (whispers): " + message);
        sender.sendMessage(ChatColor.WHITE + "[" + sender.getName() + ChatColor.GREEN + " ==> " + ChatColor.WHITE
                + receiver.getDisplayName() + "]: " + message);
    }

    private void sendUndeliveredMessage(CommandSender sender, Player receiver) {
        if (receiver == null) {
            sender.sendMessage(
                    ChatColor.WHITE + "[" + sender.getName() + ChatColor.RED + " =//=> " + ChatColor.WHITE + "]: ");
            sender.sendMessage("[CatCraft]: " + ChatColor.GRAY
                    + "Could not deliver message, receiving player is offline. Use /mail instead.");
        }
    }

    private String constructMessage(String[] args, int startingIndex) {
        StringBuilder sb = new StringBuilder();

        // usecase /cc msgall <message>: argument 0 -> "msgall"; argument 1..2..x:
        // message string
        // usecase /ccw <player> <message>: argument 0 -> player name; argument 1..2..x:
        // message string
        // usecase /anon <message>: argument 0 -> message
        for (int i = startingIndex; i < args.length; i++) {
            if (i != startingIndex) {
                sb.append(' ');
            }
            sb.append(args[i]);
        }

        return sb.toString();
    }

    public void help(CommandSender sender) {
        if (sender instanceof Player) {
            sender.sendMessage(Commands.HELP_MESSAGE);
        } else {
            plugin.getLogger().info(Commands.HELP_MESSAGE);
        }

    }

    public void credits(CommandSender sender) {
        if (sender instanceof Player) {
            sender.sendMessage(Commands.CREDITS_MESSAGE);
        } else {
            plugin.getLogger().info(Commands.CREDITS_MESSAGE);
        }

    }

    public void rules(CommandSender sender) {
        for (String s : RULES_CONFIG) {
            sender.sendMessage(s);
        }
    }
}
