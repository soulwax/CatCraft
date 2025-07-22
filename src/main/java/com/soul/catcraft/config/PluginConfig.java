package com.soul.catcraft.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// config/PluginConfig.java (expanded)
public class PluginConfig {
    public GeneralConfig general = new GeneralConfig();
    public DatabaseConfig database = new DatabaseConfig();
    public ChatConfig chat = new ChatConfig();
    public FeatureConfig features = new FeatureConfig();
    public SecurityConfig security = new SecurityConfig();

    public static class GeneralConfig {
        public boolean verbose = true;
        public boolean debugMode = false;
        public String serverDisplayName = "CatCraft Server";
        public boolean enableMetrics = true;
        public int asyncThreadPoolSize = 4;
    }

    public static class DatabaseConfig {
        public String type = "sqlite"; // sqlite, mysql, postgresql
        public String host = "localhost";
        public int port = 3306;
        public String database = "catcraft";
        public String username = "catcraft";
        public String password = "password";
        public int maxPoolSize = 10;
        public int connectionTimeout = 30000;
        public int idleTimeout = 600000;
        public boolean enableConnectionLeakDetection = true;
    }

    public static class ChatConfig {
        public boolean enableLocalChat = false;
        public double localChatRange = 100.0;
        public boolean enableEmojiReplacement = true;
        public boolean enableProfanityFilter = false;
        public boolean enableChatHistory = true;
        public int chatHistorySize = 100;
        public String welcomeMessageTemplate = "<green>Welcome <gold>{player}</gold> to {server}!</green>";
        public boolean announceNewPlayers = true;
        public boolean announceNameChanges = true;
        public boolean enableClickableUrls = true;
        public int antiSpamMessageLimit = 5;
        public int antiSpamTimeWindow = 10; // seconds
    }

    public static class FeatureConfig {
        public boolean protectCats = true;
        public boolean disarmMainHand = false;
        public boolean relayChestAccess = false;
        public boolean enablePlayerStatistics = true;
        public boolean enableCommandCooldowns = true;
        public Map<String, Integer> commandCooldowns = Map.of(
                "disarm", 30,
                "reload", 5
        );
        public boolean enableAuditLog = true;
        public boolean enableBackupSystem = false;
        public int backupIntervalHours = 6;
    }

    public static class SecurityConfig {
        public boolean enableRateLimiting = true;
        public int maxCommandsPerMinute = 20;
        public boolean enableIpWhitelist = false;
        public List<String> whitelistedIps = new ArrayList<>();
        public boolean enableBruteForceProtection = true;
        public int maxFailedAttempts = 5;
        public int lockoutDurationMinutes = 10;
        public boolean enableAuditLogging = true;
        public boolean enableSqlInjectionProtection = true;
    }
}