package com.gray17.soul.catcraft;

import org.bukkit.command.ConsoleCommandSender;

public class Debugger {
	
	private final CatCraft plugin;
	private ConsoleCommandSender console;

	
	public Debugger(CatCraft plugin) {
		this.plugin = plugin;
		this.console = plugin.getServer().getConsoleSender();
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
