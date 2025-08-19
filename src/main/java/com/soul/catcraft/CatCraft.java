package com.soul.catcraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.soul.catcraft.emoji.EmojiLibrary;

import static com.soul.catcraft.ConfigFile.*;
import static com.soul.catcraft.Constants.Commands.*;
import static com.soul.catcraft.Constants.ErrorMessages.*;
import static com.soul.catcraft.Constants.DebugMessages.*;
import static com.soul.catcraft.Constants.PluginInfo.*;

public class CatCraft extends JavaPlugin {
    public PlayerHandler playerHandler;
    public Debugger debugger;
    public ConfigFile configFile;
    public InputHandler input;
    public FileData data;
    public Logger log;
    public static EmojiLibrary emojiLibrary;

    public static Plugin plugin;

    public CatCraft() {
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean isPlayer = sender instanceof Player;

        if (!isValidCommand(cmd)) {
            return false;
        }

        if (args.length < MIN_ARGS_REQUIRED) {
            sender.sendMessage(SYNTAX_ERROR);
            return false;
        }

        if (isSpecialCommand(sender, cmd, args)) {
            return true;
        }

        if (isPlayer && (!sender.isOp() && SHOULD_BE_OP)) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        String target = args.length > 1 ? args[1] : null;
        logCommand(sender, cmd, target);

        switch (args[0].toLowerCase()) {
            case DISARM_SUBCMD:
                handleDisarm(sender, target);
                break;
            case INV_SUBCMD:
                handleOpenInventory(sender, target);
                break;
            case MSGALL_SUBCMD:
                handleMsgAll(sender, args);
                break;
            case ENDER_SUBCMD:
                handleOpenEnderInventory(sender, target);
                break;
            case RELOAD_SUBCMD:
                handleReload(sender);
                break;
            case HELP_SUBCMD:
                Commands.c.help(sender);
                break;
            case CREDITS_SUBCMD:
                Commands.c.credits(sender);
                break;
            case RULES_SUBCMD:
                Commands.c.rules(sender);
                break;
            default:
                return false;
        }

        return true;
    }

    private boolean isValidCommand(Command cmd) {
        String commandName = cmd.getName().toLowerCase();
        return commandName.equals(CATCRAFT_CMD) ||
                commandName.equals(CC_CMD) ||
                commandName.equals(CCW_CMD) ||
                commandName.equals(ANON_CMD);
    }

    private boolean isSpecialCommand(CommandSender sender, Command cmd, String[] args) {
        if (cmd.getName().equalsIgnoreCase(CCW_CMD)) {
            String target = args[0];
            if (target == null) {
                sender.sendMessage(CCW_NO_TARGET);
            } else {
                Commands.c.sendMessage(this.playerHandler.getPlayer(target), sender, args);
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase(ANON_CMD)) {
            if (sender instanceof Player) {
                Commands.c.sendMessageToAll(sender, args);
            }
            return true;
        }
        return false;
    }

    private void logCommand(CommandSender sender, Command cmd, String target) {
        if (VERBOSE) {
            String targetStr = target != null ? (" on " + target) : "";
            this.debugger.info(String.format(COMMAND_EXECUTION_FORMAT,
                    sender.getName(), cmd.getName(), targetStr));
        }
    }

    private void handleDisarm(CommandSender sender, String target) {
        if (target == null) {
            return;
        }
        Commands.c.disarm(sender, this.playerHandler.getPlayer(target), SHOULD_DISARM_MAINHAND);
        if (VERBOSE) {
            this.debugger.info(String.format(DISARM_ACTION_FORMAT, sender.getName(), target));
        }
    }

    private void handleOpenInventory(CommandSender sender, String target) {
        if (!(sender instanceof Player) || target == null) {
            return;
        }
        Commands.c.openInventory((Player) sender, this.playerHandler.getPlayer(target));
        if (VERBOSE) {
            this.debugger.info(String.format(INVENTORY_PEEK_FORMAT, sender.getName(), target));
        }
    }

    private void handleMsgAll(CommandSender sender, String[] args) {
        String msg = Commands.c.sendAnonMessageToAll(args);
        if (VERBOSE) {
            if (sender instanceof Player p) {
                p.sendMessage(String.format(MSGALL_FORMAT, p.getName(), msg));
            }
            this.debugger.info(MESSAGE_SENT_ALL);
        }
    }

    private void handleOpenEnderInventory(CommandSender sender, String target) {
        if (!(sender instanceof Player) || target == null) {
            return;
        }
        Commands.c.openEnderInventory((Player) sender, this.playerHandler.getPlayer(target));
        if (VERBOSE) {
            this.debugger.info(String.format(ENDER_PEEK_FORMAT, sender.getName(), target));
        }
    }

    private void handleReload(CommandSender sender) {
        if (!(sender instanceof Player)) {
            this.configFile.reload();
        }
    }

    public void init() {
        Commands.init(this);
        if (this.log == null) {
            this.log = new Logger();
        }
        this.log.init();

        if (this.debugger == null) {
            this.debugger = new Debugger(this);
        }
        this.debugger.init();

        if (this.configFile == null) {
            this.configFile = new ConfigFile(this);
        }
        this.configFile.init();

        if (this.data == null) {
            this.data = new FileData(this);
        }
        this.data.init();

        if (this.playerHandler == null) {
            this.playerHandler = new PlayerHandler(this);
        }
        this.playerHandler.init();

        if (this.input == null) {
            this.input = new InputHandler(this, debugger);
        }
        this.input.init();
    }

    public void onLoad() {
        String startupMessage = String.format(STARTUP_ASCII, this.getDescription().getVersion());
        getLogger().info(startupMessage);
    }

    public void onEnable() {
        this.init();
    }

    public void onDisable() {
        this.debugger.info(SHUTDOWN_MESSAGE);
    }
}