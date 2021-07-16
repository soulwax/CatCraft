package com.gray17.soul.catcraft;

import java.util.List;

public record ConfigFile(CatCraft plugin) {
    public static boolean VERBOSE;
    public static boolean VERBOSE_PLAYER_ONLY;
    public static boolean IS_GET_CMD_ACTIVATED;
    public static boolean SHOULD_DISARM_MAINHAND = false;
    public static boolean SHOULD_RELAY_CHESTS = false;
    public static boolean SHOULD_BE_OP = true;
    public static List<String> RULES_CONFIG;

    public void init() {
        this.plugin.saveDefaultConfig();

        VERBOSE = this.plugin.getConfig().getBoolean("verbose");
        IS_GET_CMD_ACTIVATED = this.plugin.getConfig().getBoolean("get-command");
        VERBOSE_PLAYER_ONLY = this.plugin.getConfig().getBoolean("verbose-player-only");
        SHOULD_DISARM_MAINHAND = this.plugin.getConfig().getBoolean("disarm-mainhand");
        SHOULD_BE_OP = this.plugin.getConfig().getBoolean("cc-must-be-op");
        SHOULD_RELAY_CHESTS = this.plugin.getConfig().getBoolean("relay-chests");
        RULES_CONFIG = this.plugin.getConfig().getStringList("rules");
    }

    public void reload() {
        this.plugin.reloadConfig();
        this.plugin.init();
        plugin.debugger.info("CatCraft reloaded!");
    }
}
