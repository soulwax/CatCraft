package com.soul.catcraft;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Cat;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;

public final class Constants {
    // Prevent instantiation
    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    // === COMBAT CONSTANTS ===
    public static final double PUNISHING_DAMAGE = 9000.0D;
    public static final int THUNDERSTORM_DURATION = 1000;

    // Protected entities
    public static final List<Class<? extends LivingEntity>> DEFENDED_ENTITIES = new ArrayList<>();

    static {
        DEFENDED_ENTITIES.add(Ocelot.class);
        DEFENDED_ENTITIES.add(Cat.class);
    }

    // === FILE SYSTEM CONSTANTS ===
    public static final class FileSystem {
        public static final Charset ENCODING = StandardCharsets.UTF_8;
        public static final String PLUGIN_ROOT_DIR = "./plugins/CatCraft/";
        public static final String PLAYER_LIST_FILENAME = "PlayerList.txt";
        public static final String PLAYER_LIST_PATH = PLUGIN_ROOT_DIR + PLAYER_LIST_FILENAME;

        // File data parsing constants
        public static final String PLAYER_DATA_DELIMITER = ",";
        public static final int MIN_PLAYER_DATA_LENGTH = 2;
        public static final int PLAYER_NAME_INDEX = 0;
        public static final int PLAYER_UUID_INDEX = 1;

        private FileSystem() {
        }
    }

    // === CHAT ROLE CONSTANTS ===
    public static final class ChatRoles {
        public static final String ADMIN_PERMISSION = "chat.format.admin";
        public static final String MODERATOR_PERMISSION = "chat.format.moderator";
        public static final String MEMBER_PERMISSION = "chat.format.member";

        public static final String ADMIN_ROLE = "ADMIN";
        public static final String MODERATOR_ROLE = "MODERATOR";
        public static final String MEMBER_ROLE = "MEMBER";

        public static final ChatColor ADMIN_COLOR = ChatColor.DARK_RED;
        public static final ChatColor MODERATOR_COLOR = ChatColor.RED;
        public static final ChatColor MEMBER_COLOR = ChatColor.DARK_GREEN;

        private ChatRoles() {
        }
    }

    // === CHAT FORMATTING CONSTANTS ===
    public static final class ChatFormatting {
        public static final ChatColor BRACKET_COLOR = ChatColor.DARK_GRAY;
        public static final ChatColor MESSAGE_COLOR = ChatColor.WHITE;
        public static final ChatColor SERVER_MAGIC = ChatColor.MAGIC;
        public static final ChatColor WHISPER_SENDER_COLOR = ChatColor.GREEN;
        public static final ChatColor WHISPER_ARROW_COLOR = ChatColor.GREEN;
        public static final ChatColor ERROR_COLOR = ChatColor.RED;
        public static final ChatColor INFO_COLOR = ChatColor.GRAY;

        // Chat message templates
        public static final String WHISPER_RECEIVE_FORMAT = ChatColor.WHITE + "[%s" + ChatColor.WHITE
                + "] (whispers): %s";
        public static final String WHISPER_SEND_FORMAT = ChatColor.WHITE + "[%s" + WHISPER_ARROW_COLOR + " ==> "
                + ChatColor.WHITE + "%s]: %s";
        public static final String WHISPER_ERROR_FORMAT = ChatColor.WHITE + "[%s" + ERROR_COLOR + " =//=> "
                + ChatColor.WHITE + "]: ";

        private ChatFormatting() {
        }
    }

    // === COMMAND CONSTANTS ===
    public static final class Commands {
        // Command names
        public static final String CATCRAFT_CMD = "catcraft";
        public static final String CC_CMD = "cc";
        public static final String CCW_CMD = "ccw";
        public static final String ANON_CMD = "anon";

        // Subcommands
        public static final String DISARM_SUBCMD = "disarm";
        public static final String INV_SUBCMD = "inv";
        public static final String MSGALL_SUBCMD = "msgall";
        public static final String ENDER_SUBCMD = "ender";
        public static final String RELOAD_SUBCMD = "reload";
        public static final String HELP_SUBCMD = "help";
        public static final String CREDITS_SUBCMD = "credits";
        public static final String RULES_SUBCMD = "rules";

        // Command arguments
        public static final int MIN_ARGS_REQUIRED = 1;
        public static final int MSGALL_MESSAGE_START_INDEX = 1;
        public static final int CCW_MESSAGE_START_INDEX = 1;
        public static final int ANON_MESSAGE_START_INDEX = 0;

        private Commands() {
        }
    }

    // === ERROR MESSAGES ===
    public static final class ErrorMessages {
        public static final String SYNTAX_ERROR = "Syntax error, at least one argument is needed. Use '/catcraft /cc or /ccw help' for a detailed command description.";
        public static final String NO_PERMISSION = "You are not permitted to use the catcraft plugin commands. Ask an administrator for help.";
        public static final String CCW_NO_TARGET = "[CatCraft]: You must specify a target when using /ccw. Example: /ccw <player> <message>";
        public static final String EMPTY_MESSAGE = "[Catcraft]: Empty message. Try again";
        public static final String PLAYER_OFFLINE = "[CatCraft]: Could not deliver message, receiving player is offline. Use /mail instead.";
        public static final String PLAYER_NOT_FOUND = "[CatCraft]: Player not found.";
        public static final String FILE_CREATION_SUCCESS = "Success: Player Data File created. Location: %s";
        public static final String FILE_CREATION_ERROR = "Something went terribly wrong with creating a new Player Data File. See stack trace above.";

        private ErrorMessages() {
        }
    }

    // === EQUIPMENT SLOT CONSTANTS ===
    public static final class EquipmentSlots {
        public static final String HELMET = "helmet";
        public static final String CHESTPLATE = "chestplate";
        public static final String LEGGINGS = "leggings";
        public static final String BOOTS = "boots";
        public static final String OFF_HAND = "offHand";
        public static final String MAIN_HAND = "mainHand";

        public static final int EQUIPMENT_SLOTS_COUNT = 6;
        public static final int HELMET_INDEX = 0;
        public static final int CHESTPLATE_INDEX = 1;
        public static final int LEGGINGS_INDEX = 2;
        public static final int BOOTS_INDEX = 3;
        public static final int OFF_HAND_INDEX = 4;
        public static final int MAIN_HAND_INDEX = 5;

        private EquipmentSlots() {
        }
    }

    // === PLUGIN INFO CONSTANTS ===
    public static final class PluginInfo {
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

        public static final String STARTUP_ASCII = """

                                          %s
                       _  _            Successfully invoked!
                     _(_)(_)_
                    (_).--.(_)          No cats were harmed
                      /    \\         while testing this plugin
                      \\    /  _  _
                       '--' _(_)(_)_      Contact Author via:
                           (_).--.(_)     - Discord: soul.wax
                             /    \\
                             \\    /
                              '--'
                """;

        public static final String SHUTDOWN_MESSAGE = "---   onDisable has been invoked!   ---";

        private PluginInfo() {
        }
    }

    // === DEBUG MESSAGES ===
    public static final class DebugMessages {
        public static final String PROTECTED_ENTITY_DIED = "Protected entity died";
        public static final String KILLER_DAMAGE_FORMAT = "%s received %s damage";
        public static final String THUNDERSTORM_INFLICTED = "Thunderstorm inflicted";
        public static final String DAMAGE_EVENT_FORMAT = "%s was harmed by %s of the type %s, damage: %s";
        public static final String DAMAGE_EVENT_SHOOTER_FORMAT = ", shooter: %s";
        public static final String PLAYER_INVENTORY_FORMAT = "Player %s opened %s at location: x=%d y=%d z=%d";
        public static final String COMMAND_EXECUTION_FORMAT = "%s ==CatCraft==> %s%s";
        public static final String DISARM_ACTION_FORMAT = "%s ==DISARMS==> %s";
        public static final String INVENTORY_PEEK_FORMAT = "%s ==PEEKS-INTO-INVENTORY==> %s";
        public static final String ENDER_PEEK_FORMAT = "%s ==PEEKS-INTO-ENDER=CHEST==> %s";
        public static final String MESSAGE_SENT_ALL = "Message sent to everyone.";
        public static final String MSGALL_FORMAT = "%s ==ALL==> %s";

        private DebugMessages() {
        }
    }

    // === NOTIFICATION MESSAGES ===
    public static final class NotificationMessages {
        public static final String CAT_KILLER_PUNISHMENT = ChatColor.DARK_RED + "" + ChatColor.BOLD
                + "%s killed a protected animal and is now receiving righteous judgement!";

        private NotificationMessages() {
        }
    }
}