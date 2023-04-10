package com.soul.catcraft;

import java.util.List;

import com.soul.catcraft.emoji.EmojiLibrary;

public record ConfigFile(CatCraft plugin) {
    public static boolean VERBOSE;
    public static boolean VERBOSE_PLAYER_ONLY;
    public static boolean IS_GET_CMD_ACTIVATED;
    public static boolean SHOULD_DISARM_MAINHAND = false;
    public static boolean SHOULD_RELAY_CHESTS = false;
    public static boolean SHOULD_BE_OP = true;
    public static List<String> RULES_CONFIG;
    public static boolean LITERALLY1984;
    public static boolean REPLACE_EMOJI;

    public void init() {
        this.plugin.saveDefaultConfig();

        VERBOSE = this.plugin.getConfig().getBoolean("verbose");
        IS_GET_CMD_ACTIVATED = this.plugin.getConfig().getBoolean("get-command");
        VERBOSE_PLAYER_ONLY = this.plugin.getConfig().getBoolean("verbose-player-only");
        SHOULD_DISARM_MAINHAND = this.plugin.getConfig().getBoolean("disarm-mainhand");
        SHOULD_BE_OP = this.plugin.getConfig().getBoolean("cc-must-be-op");
        SHOULD_RELAY_CHESTS = this.plugin.getConfig().getBoolean("relay-chests");
        RULES_CONFIG = this.plugin.getConfig().getStringList("rules");
        LITERALLY1984 = this.plugin.getConfig().getBoolean("literally-1984");
        REPLACE_EMOJI = this.plugin.getConfig().getBoolean("replace-emoji");
        
        EmojiLibrary.setReplaceEmojiConfig(REPLACE_EMOJI);
        if(LITERALLY1984) {
            VERBOSE = true;
            IS_GET_CMD_ACTIVATED = SHOULD_DISARM_MAINHAND = SHOULD_RELAY_CHESTS = true;
            VERBOSE_PLAYER_ONLY = false;
        }
    }

    public void reload() {
        this.plugin.reloadConfig();
        this.plugin.init();
        plugin.debugger.info("CatCraft reloaded!");
    }
}
