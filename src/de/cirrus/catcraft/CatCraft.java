package de.cirrus.catcraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class CatCraft extends JavaPlugin {
    public static final String help = "\n\n------------------------------\n\'/catcraft\' - global command prefix that adresses catnet commands.\n-msg [Player<optionally:all>] [message]: sends the target an anonymous message.\n-inv [Player]: peeks into the player\'s inventory.\n-ender [Player]: peeks into the player\'s ender chest.\n-disarm [Player]: steals the target\'s armor slot contents.\n-rules: displays the server rules (may not be updated yet)\n-help: displays all possible commands\n-credits: plugin credits\n------------------------------\n";
    public static final String consoleHelp = "console only commands: \n-reload: reloads CatCraft, used when changes were applied to the config.yml during runtime\n------------------------------\n\n";
    public static final String credits = "\n\n--------credits----------\nserver: catnet.de\nplugin author: cirrus\ncontact: cirrus@catnet.de\nspecial thanks to: sh4ni\n---------------------------\n\n";
    public PlayerHandler playerHandler;
    public ConfigFile configFile;
    public InputHandler input;
    public FileData data;
    public Logger log;

    private static boolean shouldDisarmMainhand = false;

    public CatCraft() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!cmd.getName().equalsIgnoreCase("catcraft") && !cmd.getName().equalsIgnoreCase("cc")) {
            return false;
        } else if(args.length < 1) {
            sender.sendMessage("Syntax error, at least one argument is needed, use \'/catcraft(or cc) help\' for a detailed command description");
            return false;
        } else if(sender instanceof Player && !sender.isOp()) {
            return true;
        } else {
            if(InputHandler.VERBOSE) {
                this.getLogger().info(sender.getName() + " used " + cmd.getName() + ", permission was granted!");
            }

            String var5 = args[0].toLowerCase();
            byte var6 = -1;
            switch(var5.hashCode()) {
                case -1331559666:
                    if(var5.equals("disarm")) {
                        var6 = 3;
                    }
                    break;
                case -934641255:
                    if(var5.equals("reload")) {
                        var6 = 5;
                    }
                    break;
                case 104433:
                    if(var5.equals("inv")) {
                        var6 = 1;
                    }
                    break;
                case 108417:
                    if(var5.equals("msg")) {
                        var6 = 0;
                    }
                    break;
                case 3198785:
                    if(var5.equals("help")) {
                        var6 = 4;
                    }
                    break;
                case 96651976:
                    if(var5.equals("ender")) {
                        var6 = 2;
                    }
                    break;
                case 1028633754:
                    if(var5.equals("credits")) {
                        var6 = 6;
                    }
            }

            switch(var6) {
                case 0:
                    if(args[1].equals("all")) {
                        Commands.c.sendMessageToAll(args);
                        if(InputHandler.VERBOSE) {
                            this.getLogger().info("Message sent to all");
                        }
                    } else {
                        Commands.c.sendMessage(this.playerHandler.getPlayer(args[1]), sender, args);
                    }
                    break;
                case 1:
                    if(sender instanceof Player) {
                        Commands.c.openInventory((Player)sender, this.playerHandler.getPlayer(args[1]));
                        if(InputHandler.VERBOSE) {
                            this.getLogger().info(sender.getName() + " inspects the inventory of " + args[1]);
                        }
                    }
                    break;
                case 2:
                    if(sender instanceof Player) {
                        Commands.c.openEnderInventory((Player)sender, this.playerHandler.getPlayer(args[1]));
                        if(InputHandler.VERBOSE) {
                            this.getLogger().info(sender.getName() + " inspects the enderchest of " + args[1]);
                        }
                    }
                    break;
                case 3:
                    Commands.c.disarm(sender, this.playerHandler.getPlayer(args[1]), shouldDisarmMainhand);
                    if(InputHandler.VERBOSE) {
                        this.getLogger().info(sender.getName() + " disarms " + args[1]);
                    }

                    break;
                case 4:
                    Commands.c.help(sender);
                    break;
                case 5:
                    if(sender instanceof Player) {
                        return false;
                    }

                    this.configFile.reload();
                    break;
                case 6:
                    Commands.c.credits(sender);
                    break;
                default:
                    return false;
            }

            return true;
        }
    }

    public void init() {
        Commands.init(this);
        if(this.log == null) {
            this.log = new Logger();
        }

        this.log.init();
        if(this.configFile == null) {
            this.configFile = new ConfigFile(this);
        }

        this.configFile.init();
        if(this.data == null) {
            this.data = new FileData(this);
        }

        this.data.init();
        if(this.playerHandler == null) {
            this.playerHandler = new PlayerHandler(this);
        }

        this.playerHandler.init();
        if(this.input == null) {
            this.input = new InputHandler(this);
        }

        this.input.init();

        shouldDisarmMainhand = this.getConfig().getBoolean("disarm mainhand");
    }

    public void onEnable() {
        this.init();
        this.getLogger().info("                    ");
        this.getLogger().info("                      " + this.getDescription().getFullName() + " ");
        this.getLogger().info("   _  _            Successfully invoked! ");
        this.getLogger().info(" _(_)(_)_\t\t   ");
        this.getLogger().info("(_).--.(_)          No cats were harmed      ");
        this.getLogger().info("  /    \\         while testing this plugin      ");
        this.getLogger().info("  \\    /  _  _  ");
        this.getLogger().info("   \'--\' _(_)(_)_ ");
        this.getLogger().info("       (_).--.(_)      We promise!");
        this.getLogger().info("         /    \\ ");
        this.getLogger().info("         \\    / ");
        this.getLogger().info("          \'--\'   ");
    }

    public void onDisable() {
        this.getLogger().info("---   onDisable has been invoked!   ---");
    }
}
