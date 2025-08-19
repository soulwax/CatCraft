package com.soul.catcraft;

import com.soul.catcraft.emoji.EmojiLibrary;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static com.soul.catcraft.ConfigFile.RULES_CONFIG;
import static com.soul.catcraft.Constants.Commands.*;
import static com.soul.catcraft.Constants.EquipmentSlots.*;
import static com.soul.catcraft.Constants.ErrorMessages.*;
import static com.soul.catcraft.Constants.PluginInfo.*;

public final class Commands {
    public static CatCraft plugin;
    public static Commands c;

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
        ItemStack[] inventory = new ItemStack[EQUIPMENT_SLOTS_COUNT];
        PlayerInventory targetInventory = target.getInventory();
        PlayerInventory equipment = target.getInventory();

        inventory[HELMET_INDEX] = tryRemovingItem(equipment.getHelmet(), targetInventory, HELMET);
        inventory[CHESTPLATE_INDEX] = tryRemovingItem(equipment.getChestplate(), targetInventory, CHESTPLATE);
        inventory[LEGGINGS_INDEX] = tryRemovingItem(equipment.getLeggings(), targetInventory, LEGGINGS);
        inventory[BOOTS_INDEX] = tryRemovingItem(equipment.getBoots(), targetInventory, BOOTS);
        inventory[OFF_HAND_INDEX] = tryRemovingItem(equipment.getItemInOffHand(), targetInventory, OFF_HAND);

        if (shouldDisarmMainhand) {
            inventory[MAIN_HAND_INDEX] = tryRemovingItem(equipment.getItemInMainHand(), targetInventory, MAIN_HAND);
        }

        return inventory;
    }

    private ItemStack tryRemovingItem(ItemStack item, PlayerInventory inventory, String type) {
        if (item == null) {
            return null;
        }

        switch (type) {
            case HELMET:
                inventory.setHelmet(null);
                break;
            case CHESTPLATE:
                inventory.setChestplate(null);
                break;
            case LEGGINGS:
                inventory.setLeggings(null);
                break;
            case BOOTS:
                inventory.setBoots(null);
                break;
            case OFF_HAND:
                inventory.setItemInOffHand(null);
                break;
            case MAIN_HAND:
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
        String message = this.constructMessage(args, ANON_MESSAGE_START_INDEX);
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
        String message = constructMessage(args, MSGALL_MESSAGE_START_INDEX);
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
            sender.sendMessage(EMPTY_MESSAGE);
            return;
        }

        if (receiver == null) {
            sendPlayerNotFoundMessage(sender);
            return;
        }

        if (!receiver.isOnline()) {
            sendPlayerOfflineMessage(sender, receiver);
            return;
        }

        sendWhisperMessage(sender, receiver, message);
    }

    private String constructAndFormatMessage(String[] args) {
        if (args.length < 1) {
            return "";
        }

        String message = constructMessage(args, CCW_MESSAGE_START_INDEX);
        String messageModified = EmojiLibrary.findAndReplaceEmojiRND(message);
        return messageModified.isEmpty() ? message : messageModified;
    }

    private void sendPlayerNotFoundMessage(CommandSender sender) {
        sender.sendMessage(String.format(Constants.ChatFormatting.WHISPER_ERROR_FORMAT, sender.getName()));
        sender.sendMessage(PLAYER_NOT_FOUND);
    }

    private void sendPlayerOfflineMessage(CommandSender sender, Player receiver) {
        sender.sendMessage(String.format(Constants.ChatFormatting.WHISPER_ERROR_FORMAT, sender.getName()));
        sender.sendMessage(PLAYER_OFFLINE);
    }

    private void sendWhisperMessage(CommandSender sender, Player receiver, String message) {
        String receiveFormat = String.format(Constants.ChatFormatting.WHISPER_RECEIVE_FORMAT,
                Constants.ChatFormatting.WHISPER_SENDER_COLOR + sender.getName(), message);
        String sendFormat = String.format(Constants.ChatFormatting.WHISPER_SEND_FORMAT,
                sender.getName(), receiver.getDisplayName(), message);

        receiver.sendMessage(receiveFormat);
        sender.sendMessage(sendFormat);
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
            sender.sendMessage(HELP_MESSAGE);
        } else {
            plugin.getLogger().info(HELP_MESSAGE);
        }
    }

    public void credits(CommandSender sender) {
        if (sender instanceof Player) {
            sender.sendMessage(CREDITS_MESSAGE);
        } else {
            plugin.getLogger().info(CREDITS_MESSAGE);
        }
    }

    public void rules(CommandSender sender) {
        for (String s : RULES_CONFIG) {
            sender.sendMessage(s);
        }
    }
}