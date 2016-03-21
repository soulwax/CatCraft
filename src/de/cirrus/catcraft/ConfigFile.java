package de.cirrus.catcraft;


public final class ConfigFile {
    private CatCraft plugin;

    public ConfigFile(CatCraft plugin) {
        this.plugin = plugin;
    }

    public void init() {
        this.plugin.saveDefaultConfig();
    }

    public void reload() {
        this.plugin.reloadConfig();
        this.plugin.init();
        this.plugin.getLogger().info("CatCraft reloaded!");
    }
}
