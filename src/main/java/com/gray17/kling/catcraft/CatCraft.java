package com.gray17.kling.catcraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CatCraft extends JavaPlugin {
	public static final String help = "\n\n------------------------------\n\'/catcraft\' - global command prefix that adresses catnet commands.\n-msg [Player<optionally:all>] [message]: sends the target an anonymous message.\n-inv [Player]: peeks into the player\'s inventory.\n-ender [Player]: peeks into the player\'s ender chest.\n-disarm [Player]: steals the target\'s armor slot contents.\n-rules: displays the server rules (may not be updated yet)\n-help: displays all possible commands\n-credits: plugin credits\n------------------------------\n";
	public static final String consoleHelp = "console only commands: \n-reload: reloads CatCraft, used when changes were applied to the config.yml during runtime\n------------------------------\n\n";
	public static final String credits = "\n\n--------credits----------\nserver: gray17.com\nplugin author: kling\ncontact: kling@gray17.com\nspecial thanks to: morrigan for hosting\n---------------------------\n\n";
	public PlayerHandler playerHandler;
	public Debugger debugger;
	public ConfigFile configFile;
	public InputHandler input;
	public FileData data;
	public Logger log;
	public ItemManager itemManager;

	private static boolean shouldDisarmMainhand = false;
	private static boolean mustBeOP = true;

	public CatCraft() {
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		boolean isPlayer = sender instanceof Player;
		
		String target = null; 
		
		
		if (!cmd.getName().equalsIgnoreCase("catcraft") && !cmd.getName().equalsIgnoreCase("cc")) {
			return false;
		} else if (args.length < 1) {
			sender.sendMessage("Syntax error, at least one argument is needed, use \'"
					+ "/catcraft(or cc) help\' for a detailed command description");
			return false;
		} else if (isPlayer && (!sender.isOp() && mustBeOP)) {
			sender.sendMessage("You are not permitted to use the catcraft plugin commands. Ask an administrator for help.");
			return false;
		} else {
			if(args.length > 1) 
				target = args[1];
			if (InputHandler.VERBOSE) {
				
				this.debugger.info(sender.getName() + " used command: " + cmd.getName() + (target != null ? (" on " + target) : ""));
			}
			String argsLC = args[0].toLowerCase();
			
			switch (argsLC) {
			case "disarm":
				if(target == null) break;
				Commands.c.disarm(sender, this.playerHandler.getPlayer(args[1]), shouldDisarmMainhand);
				if (InputHandler.VERBOSE) {
					this.debugger.info(sender.getName() + " disarms " + target);
				}
				break;
			case "inv":
				if (isPlayer && target != null) {
					Commands.c.openInventory((Player) sender, this.playerHandler.getPlayer(target));
					if (InputHandler.VERBOSE) {
						this.debugger.info(sender.getName() + " inspects the inventory of " + target);
					}
				}
				break;
			case "msg":
				if (target == null) break;
				if (args[1].equalsIgnoreCase("all")) {
					Commands.c.sendMessageToAll(args);
					if (InputHandler.VERBOSE) {
						this.debugger.info("Message sent to all");
					}
				} else {
					Commands.c.sendMessage(this.playerHandler.getPlayer(target), sender, args);
				}
				break;
			case "ender":
				if (isPlayer && target != null) {
					Commands.c.openEnderInventory((Player) sender, this.playerHandler.getPlayer(target));
					if (InputHandler.VERBOSE) {
						this.debugger.info(sender.getName() + " inspects the enderchest of " + target);
					}
				}
				break;
			case "givewatch":
				if (isPlayer && target != null) {
					this.itemManager.giveItem((Player) sender);
					if (InputHandler.VERBOSE) {
						this.debugger.info(sender.getName() + " gets the power watch! ");
					}
				}				
				break;
			case "reload":
				if (isPlayer) {
					return false;
				}
				this.configFile.reload();
				break;
			case "help":
				Commands.c.help(sender);
				break;
			case "credits":
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
		if (this.log == null) {
			this.log = new Logger();
		}
		this.log.init();
		
		if(this.debugger == null) {
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


		shouldDisarmMainhand = this.getConfig().getBoolean("disarm mainhand");
		mustBeOP = this.getConfig().getBoolean("must be op to use");
		
		if(this.itemManager == null) {
			this.itemManager = new ItemManager(this);
		}
		this.itemManager.init();
		
	}
	
	public void onLoad() {
		
		getLogger().info("                    ");
		getLogger().info("                      " + this.getDescription().getVersion() + " ");
		getLogger().info("   _  _            Successfully invoked! ");
		getLogger().info(" _(_)(_)_\t\t   ");
		getLogger().info("(_).--.(_)          No cats were harmed      ");
		getLogger().info("  /    \\         while testing this plugin      ");
		getLogger().info("  \\    /  _  _  ");
		getLogger().info("   \'--\' _(_)(_)_ ");
		getLogger().info("       (_).--.(_)      by soulwax");
		getLogger().info("         /    \\ ");
		getLogger().info("         \\    / ");
		getLogger().info("          \'--\'   ");	
	}

	public void onEnable() {
		this.init();	
	}

	public void onDisable() {
		this.debugger.info("---   onDisable has been invoked!   ---");
	}
}
