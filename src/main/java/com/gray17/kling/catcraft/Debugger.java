package com.gray17.kling.catcraft;

import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;

public class Debugger {
	
	private CatCraft plugin;
	private Server server = null;
	private ConsoleCommandSender console = null;

	
	public Debugger(CatCraft plugin) {
		 this.plugin = plugin;
		 this.server = plugin.getServer();
		 this.setConsole(server.getConsoleSender());
	}
	
	public void init() {
	}
	
	public void info(String infoMessage) {
		this.plugin.getLogger().info(infoMessage);		
	}
	
	public void warning(String warnMessage) {
		this.plugin.getLogger().warning(warnMessage);
	}
	
	public void severe(String errorMessage) {
		this.plugin.getLogger().severe(errorMessage);
	}

	public ConsoleCommandSender getConsole() {
		return console;
	}

	public void setConsole(ConsoleCommandSender console) {
		this.console = console;
	}
}
