package com.gray17.soul.catcraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CatCraft extends JavaPlugin {
	public PlayerHandler playerHandler;
	public Debugger debugger;
	public ConfigFile configFile;
	public InputHandler input;
	public FileData data;
	public Logger log;

	private static boolean shouldDisarmMainhand = false;
	private static boolean mustBeOP = true;

	public CatCraft() {
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		boolean isPlayer = sender instanceof Player;
		
		String target = null; 
		
		
		if (!cmd.getName().equalsIgnoreCase("catcraft") && !cmd.getName().equalsIgnoreCase("cc") && !cmd.getName().equalsIgnoreCase("ccw")) {
			return false;
		}  else if (args.length < 1) {
			sender.sendMessage("Syntax error, at least one argument is needed, use \'"
					+ "/catcraft /cc or /ccw help\' for a detailed command description");
			return false;
		} else {
			//cmd args0 args1...
			//ccw Player Message1 Message2
			if(cmd.getName().equalsIgnoreCase("ccw")) {
				target = args[0];
				if (target == null) sender.sendMessage("[CatCraft]: You must specify a target when using /ccw. Example: /ccw <player> <message>");
				else {
					Commands.c.sendMessage(this.playerHandler.getPlayer(target), sender, args);
				}
				return true;
			}
			if (isPlayer && (!sender.isOp() && mustBeOP)) {
				sender.sendMessage("You are not permitted to use the catcraft plugin commands. Ask an administrator for help.");
				return false;
			}
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
			case "msgall":
				Commands.c.sendMessageToAll(args);
				if (InputHandler.VERBOSE) {
					this.debugger.info("[CatCraft]: Message sent to everyone.");
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
		getLogger().info("       (_).--.(_)      by soulwax#5586");
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
