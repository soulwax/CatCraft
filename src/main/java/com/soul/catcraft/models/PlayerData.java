package com.soul.catcraft.models;

@DatabaseTable(tableName = "player_data")
public class PlayerData {
    @DatabaseField(id = true)
    private String uuid;

    @DatabaseField
    private String displayName;

    @DatabaseField
    private String previousNames; // JSON array of name history

    @DatabaseField
    private Timestamp firstJoin;

    @DatabaseField
    private Timestamp lastSeen;

    @DatabaseField
    private int totalPlaytime; // minutes

    @DatabaseField
    private String permissions; // JSON array for custom permissions

    @DatabaseField
    private boolean isBanned;

    @DatabaseField
    private String banReason;

    @DatabaseField
    private Timestamp banExpiry;

    // Constructors, getters, setters
}