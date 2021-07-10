package com.gray17.soul.catcraft;

public record ConfigFile(CatCraft plugin) {

    public void init() {
        this.plugin.saveDefaultConfig();
    }

    public void reload() {
        this.plugin.reloadConfig();
        this.plugin.init();
        plugin.debugger.info("CatCraft reloaded!");
    }
}
