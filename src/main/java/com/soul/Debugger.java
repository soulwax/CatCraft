// File: src/main/java/com/soul/Debugger.java

package com.soul.catcraft;

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

	@SuppressWarnings("Unused")
	public void warning(String warnMessage) {
		this.plugin.getLogger().warning(warnMessage);
	}

	@SuppressWarnings("Unused")
	public void severe(String errorMessage) {
		this.plugin.getLogger().severe(errorMessage);
	}

	@SuppressWarnings("Unused")
	public ConsoleCommandSender getConsole() {
		return console;
	}

	@SuppressWarnings("Unused")
	public void setConsole(ConsoleCommandSender console) {
		this.console = console;
	}
}