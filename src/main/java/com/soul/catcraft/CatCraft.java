package com.soul.catcraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.soul.catcraft.emoji.EmojiLibrary;

import static com.soul.catcraft.ConfigFile.*;

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

        if (args.length < 1) {
            sender.sendMessage(
                    "Syntax error, at least one argument is needed. Use '/catcraft /cc or /ccw help' for a detailed command description.");
            return false;
        }

        if (isSpecialCommand(sender, cmd, args)) {
            return true;
        }

        if (isPlayer && (!sender.isOp() && SHOULD_BE_OP)) {
            sender.sendMessage(
                    "You are not permitted to use the catcraft plugin commands. Ask an administrator for help.");
            return false;
        }

        String target = args.length > 1 ? args[1] : null;
        logCommand(sender, cmd, target);

        switch (args[0].toLowerCase()) {
            case "disarm":
                handleDisarm(sender, target);
                break;
            case "inv":
                handleOpenInventory(sender, target);
                break;
            case "msgall":
                handleMsgAll(sender, args);
                break;
            case "ender":
                handleOpenEnderInventory(sender, target);
                break;
            case "reload":
                handleReload(sender);
                break;
            case "help":
                Commands.c.help(sender);
                break;
            case "credits":
                Commands.c.credits(sender);
                break;
            case "rules":
                Commands.c.rules(sender);
                break;
            default:
                return false;
        }

        return true;
    }

    private boolean isValidCommand(Command cmd) {
        String commandName = cmd.getName().toLowerCase();
        return commandName.equals("catcraft") || commandName.equals("cc") || commandName.equals("ccw")
                || commandName.equals("anon");
    }

    private boolean isSpecialCommand(CommandSender sender, Command cmd, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ccw")) {
            String target = args[0];
            if (target == null) {
                sender.sendMessage(
                        "[CatCraft]: You must specify a target when using /ccw. Example: /ccw <player> <message>");
            } else {
                Commands.c.sendMessage(this.playerHandler.getPlayer(target), sender, args);
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("anon")) {
            if (sender instanceof Player) {
                Commands.c.sendMessageToAll(sender, args);
            }
            return true;
        }
        return false;
    }

    private void logCommand(CommandSender sender, Command cmd, String target) {
        if (VERBOSE) {
            this.debugger.info(
                    sender.getName() + " ==CatCraft==> " + cmd.getName() + (target != null ? (" on " + target) : ""));
        }
    }

    private void handleDisarm(CommandSender sender, String target) {
        if (target == null) {
            return;
        }
        Commands.c.disarm(sender, this.playerHandler.getPlayer(target), SHOULD_DISARM_MAINHAND);
        if (VERBOSE) {
            this.debugger.info(sender.getName() + " ==DISARMS==> " + target);
        }
    }

    private void handleOpenInventory(CommandSender sender, String target) {
        if (!(sender instanceof Player) || target == null) {
            return;
        }
        Commands.c.openInventory((Player) sender, this.playerHandler.getPlayer(target));
        if (VERBOSE) {
            this.debugger.info(sender.getName() + " ==PEEKS-INTO-INVENTORY==> " + target);
        }
    }

    private void handleMsgAll(CommandSender sender, String[] args) {
        String msg = Commands.c.sendAnonMessageToAll(args);
        if (VERBOSE) {
            if (sender instanceof Player p) {
                p.sendMessage(p.getName() + " ==ALL==> " + msg);
            }
            this.debugger.info("Message sent to everyone.");
        }
    }

    private void handleOpenEnderInventory(CommandSender sender, String target) {
        if (!(sender instanceof Player) || target == null) {
            return;
        }
        Commands.c.openEnderInventory((Player) sender, this.playerHandler.getPlayer(target));
        if (VERBOSE) {
            this.debugger.info(sender.getName() + " ==PEEKS-INTO-ENDER=CHEST==> " + target);
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

        getLogger().info("                    ");
        getLogger().info("                      " + this.getDescription().getVersion() + " ");
        getLogger().info("   _  _            Successfully invoked! ");
        getLogger().info(" _(_)(_)_\t\t   ");
        getLogger().info("(_).--.(_)          No cats were harmed      ");
        getLogger().info("  /    \\         while testing this plugin      ");
        getLogger().info("  \\    /  _  _  ");
        getLogger().info("   '--' _(_)(_)_      Contact Author via:");
        getLogger().info("       (_).--.(_)     - Discord: soul.wax");
        getLogger().info("         /    \\ ");
        getLogger().info("         \\    / ");
        getLogger().info("          '--'   ");
    }

    public void onEnable() {
        this.init();
    }

    public void onDisable() {
        this.debugger.info("---   onDisable has been invoked!   ---");
    }
}
