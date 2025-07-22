// File: src/main/java/com/soul/catcraft/models/ServerMetrics.java

package com.soul.catcraft.models;

import java.util.Map;

public class ServerMetrics {
    private String serverVersion;
    private String pluginVersion;
    private int onlinePlayerCount;
    private int maxPlayers;
    private long usedMemoryMB;
    private long maxMemoryMB;
    private long freeMemoryMB;
    private double tps;
    private Map<String, Long> eventCounts;
    private Map<String, Double> performanceMetrics;

    // Constructors, getters, setters
}