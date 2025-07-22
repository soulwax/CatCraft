// File: src/main/java/com/soul/catcraft/models/PlayerMetrics.java

package com.soul.catcraft.models;

import java.sql.Timestamp;
import java.util.Map;

public class PlayerMetrics {
    private String playerUuid;
    private long totalSessions;
    private long averageSessionLength;
    private long recentActivityCount;
    private Timestamp lastActivity;
    private Map<String, Long> actionCounts;

    // Constructors, getters, setters
}