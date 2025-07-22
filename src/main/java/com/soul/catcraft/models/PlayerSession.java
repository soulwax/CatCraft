// File: src/main/java/com/soul/catcraft/models/PlayerSession.java

package com.soul.catcraft.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.security.Timestamp;

@DatabaseTable(tableName = "player_sessions")
public class PlayerSession {
    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(columnName = "player_uuid", index = true)
    private String playerUuid;

    @DatabaseField
    private String action;

    @DatabaseField(index = true)
    private Timestamp timestamp;

    @DatabaseField
    private String metadata; // JSON for additional data

    // Constructors, getters, setters
}