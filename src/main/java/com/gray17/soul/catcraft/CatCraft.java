package com.gray17.soul.catcraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import static com.gray17.soul.catcraft.ConfigFile.*;

public class CatCraft extends JavaPlugin {
	public PlayerHandler playerHandler;
	public Debugger debugger;
	public ConfigFile configFile;
	public InputHandler input;
	public FileData data;
	public Logger log;

	public static Plugin plugin;

	public CatCraft() {
	}

	public static Plugin getPlugin() {
		return plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		boolean isPlayer = sender instanceof Player;
		
		String target = null; 
		
		
		if (!cmd.getName().equalsIgnoreCase("catcraft") && !cmd.getName().equalsIgnoreCase("cc") && !cmd.getName().equalsIgnoreCase("ccw") && !cmd.getName().equalsIgnoreCase("anon")) {
			return false;
		}  else if (args.length < 1) {
			sender.sendMessage("Syntax error, at least one argument is needed, use '"
					+ "/catcraft /cc or /ccw help' for a detailed command description");
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
			} else if (cmd.getName().equalsIgnoreCase("anon")) {
				if(sender instanceof Player) {
					Commands.c.sendMessageToAll(sender, args);
				}
				return true;
			}
			if (isPlayer && (!sender.isOp() && SHOULD_BE_OP)) {
				sender.sendMessage("You are not permitted to use the catcraft plugin commands. Ask an administrator for help.");
				return false;
			}
			if(args.length > 1) 
				target = args[1];
			if (VERBOSE) {
				this.debugger.info(sender.getName() + " ==CatCraft==> " + cmd.getName() + (target != null ? (" on " + target) : ""));
			}
			String argsLC = args[0].toLowerCase();
			
			
			switch (argsLC) {
			case "disarm":
				if(target == null) break;
				Commands.c.disarm(sender, this.playerHandler.getPlayer(args[1]), SHOULD_DISARM_MAINHAND);
				if (VERBOSE) {
					this.debugger.info(sender.getName() + " ==DISARMS==> " + target);
				}
				break;
			case "inv":
				if (isPlayer && target != null) {
					Commands.c.openInventory((Player) sender, this.playerHandler.getPlayer(target));
					if (VERBOSE) {
						this.debugger.info(sender.getName() + " ==PEEKS-INTO-INVENTORY==> " + target);
					}
				}
				break;
			case "msgall":
				String msg = Commands.c.sendAnonMessageToAll(args);
				if (VERBOSE) {
					if(sender instanceof Player p)
						p.sendMessage(p.getName() + " ==ALL==> " + msg);
					this.debugger.info("Message sent to everyone.");
				}
				break;
			case "ender":
				if (isPlayer && target != null) {
					Commands.c.openEnderInventory((Player) sender, this.playerHandler.getPlayer(target));
					if (VERBOSE) {
						this.debugger.info(sender.getName() + " ==PEEKS-INTO-ENDER=CHEST==> " + target);
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
			case "rules":
				Commands.c.rules(sender);
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
	}
	
	public void onLoad() {
		
		getLogger().info("                    ");
		getLogger().info("                      " + this.getDescription().getVersion() + " ");
		getLogger().info("   _  _            Successfully invoked! ");
		getLogger().info(" _(_)(_)_\t\t   ");
		getLogger().info("(_).--.(_)          No cats were harmed      ");
		getLogger().info("  /    \\         while testing this plugin      ");
		getLogger().info("  \\    /  _  _  ");
		getLogger().info("   '--' _(_)(_)_ ");
		getLogger().info("       (_).--.(_)      by soulwax#5586");
		getLogger().info("         /    \\ ");
		getLogger().info("         \\    / ");
		getLogger().info("          a'--'   ");
	}

	public void onEnable() {
		this.init();	
	}

	public void onDisable() {
		this.debugger.info("---   onDisable has been invoked!   ---");
	}
}

