// File: src/main/java/com/soul/catcraft/services/MetricsService.java

package com.soul.catcraft.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soul.catcraft.config.PluginConfig;
import com.soul.catcraft.models.PlayerMetrics;
import com.soul.catcraft.models.PlayerSession;
import com.soul.catcraft.models.ServerMetrics;
import com.soul.catcraft.repositories.PlayerRepository;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

// services/MetricsService.java
public interface MetricsService {
    void startMetricsCollection();

    CompletableFuture<ServerMetrics> getServerMetrics();

    CompletableFuture<PlayerMetrics> getPlayerMetrics(String uuid);

    void recordEvent(String eventType, Map<String, Object> data);

    void recordPerformanceMetric(String operation, long durationMs);
}

// services/MetricsServiceImpl.java
@Singleton
public class MetricsServiceImpl implements MetricsService {
    private static final Logger logger = LoggerFactory.getLogger(MetricsServiceImpl.class);

    private final PlayerRepository playerRepository;
    private final ConfigService configService;
    private final ScheduledExecutorService executorService;
    private final Plugin plugin;

    private final ConcurrentHashMap<String, AtomicLong> eventCounters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LongAdder> performanceMetrics = new ConcurrentHashMap<>();
    private final Queue<PerformanceEntry> performanceHistory = new ConcurrentLinkedQueue<>();

    @Inject
    public MetricsServiceImpl(PlayerRepository playerRepository, ConfigService configService,
            ScheduledExecutorService executorService, Plugin plugin) {
        this.playerRepository = playerRepository;
        this.configService = configService;
        this.executorService = executorService;
        this.plugin = plugin;
    }

    @Override
    public void startMetricsCollection() {
        if (!configService.getConfig().general.enableMetrics) {
            return;
        }

        // Collect server metrics every 5 minutes
        executorService.scheduleAtFixedRate(this::collectServerMetrics, 0, 5, TimeUnit.MINUTES);

        // Clean old performance data every hour
        executorService.scheduleAtFixedRate(this::cleanOldMetrics, 1, 1, TimeUnit.HOURS);

        // Setup bStats if available
        setupBStats();

        logger.info("Metrics collection started");
    }

    @Override
    public CompletableFuture<ServerMetrics> getServerMetrics() {
        return CompletableFuture.supplyAsync(() -> {
            ServerMetrics metrics = new ServerMetrics();

            // Basic server info
            metrics.setServerVersion(Bukkit.getVersion());
            metrics.setPluginVersion(plugin.getDescription().getVersion());
            metrics.setOnlinePlayerCount(Bukkit.getOnlinePlayers().size());
            metrics.setMaxPlayers(Bukkit.getMaxPlayers());

            // Performance metrics
            Runtime runtime = Runtime.getRuntime();
            metrics.setUsedMemoryMB((runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024);
            metrics.setMaxMemoryMB(runtime.maxMemory() / 1024 / 1024);
            metrics.setFreeMemoryMB(runtime.freeMemory() / 1024 / 1024);

            // TPS calculation (simplified)
            metrics.setTps(calculateAverageTps());

            // Event counters
            metrics.setEventCounts(eventCounters.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().get())));

            // Performance data
            metrics.setPerformanceMetrics(performanceMetrics.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().doubleValue())));

            return metrics;
        });
    }

    @Override
    public CompletableFuture<PlayerMetrics> getPlayerMetrics(String uuid) {
        return playerRepository.getPlayerHistory(uuid, 100)
                .thenCompose(sessions -> {
                    PlayerMetrics metrics = new PlayerMetrics();
                    metrics.setPlayerUuid(uuid);

                    // Session analysis
                    long totalSessions = sessions.size();
                    long averageSessionLength = calculateAverageSessionLength(sessions);
                    Map<String, Long> actionCounts = countActionTypes(sessions);

                    metrics.setTotalSessions(totalSessions);
                    metrics.setAverageSessionLength(averageSessionLength);
                    metrics.setActionCounts(actionCounts);

                    // Recent activity
                    List<PlayerSession> recentSessions = sessions.stream()
                            .filter(session -> isRecentSession(session, 7 * 24 * 60 * 60 * 1000)) // Last 7 days
                            .collect(Collectors.toList());

                    metrics.setRecentActivityCount(recentSessions.size());
                    metrics.setLastActivity(sessions.isEmpty() ? null : sessions.get(0).getTimestamp());

                    return CompletableFuture.completedFuture(metrics);
                });
    }

    @Override
    public void recordEvent(String eventType, Map<String, Object> data) {
        eventCounters.computeIfAbsent(eventType, k -> new AtomicLong(0)).incrementAndGet();

        // Store detailed event data if needed
        if (configService.getConfig().general.debugMode) {
            logger.debug("Event recorded: {} with data: {}", eventType, data);
        }
    }

    @Override
    public void recordPerformanceMetric(String operation, long durationMs) {
        performanceMetrics.computeIfAbsent(operation, k -> new LongAdder()).add(durationMs);

        // Store recent performance data
        performanceHistory.offer(new PerformanceEntry(operation, durationMs, System.currentTimeMillis()));

        // Limit queue size
        while (performanceHistory.size() > 1000) {
            performanceHistory.poll();
        }

        // Alert on slow operations
        if (durationMs > 1000) { // 1 second threshold
            logger.warn("Slow operation detected: {} took {}ms", operation, durationMs);
        }
    }

    private void collectServerMetrics() {
        try {
            // Record current server state
            recordEvent("server_tick", Map.of(
                    "online_players", Bukkit.getOnlinePlayers().size(),
                    "worlds_loaded", Bukkit.getWorlds().size(),
                    "plugins_enabled", Bukkit.getPluginManager().getPlugins().length));

            // Memory usage
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            recordEvent("memory_usage", Map.of(
                    "used_mb", usedMemory / 1024 / 1024,
                    "free_mb", runtime.freeMemory() / 1024 / 1024,
                    "max_mb", runtime.maxMemory() / 1024 / 1024));

        } catch (Exception e) {
            logger.error("Error collecting server metrics", e);
        }
    }

    private void cleanOldMetrics() {
        long cutoffTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000); // 24 hours
        performanceHistory.removeIf(entry -> entry.timestamp < cutoffTime);

        logger.debug("Cleaned old metrics, {} performance entries remaining", performanceHistory.size());
    }

    private void setupBStats() {
        try {
            // Initialize bStats if available
            int pluginId = 12345; // Replace with actual bStats plugin ID
            Metrics metrics = new Metrics((JavaPlugin) plugin, pluginId);

            // Custom metrics
            metrics.addCustomChart(new Metrics.SingleLineChart("online_players",
                    () -> Bukkit.getOnlinePlayers().size()));

            metrics.addCustomChart(new Metrics.SimplePie("database_type",
                    () -> configService.getConfig().database.type));

            metrics.addCustomChart(new Metrics.AdvancedPie("feature_usage", () -> {
                Map<String, Integer> featureMap = new HashMap<>();
                PluginConfig config = configService.getConfig();

                featureMap.put("cat_protection", config.features.protectCats ? 1 : 0);
                featureMap.put("emoji_replacement", config.chat.enableEmojiReplacement ? 1 : 0);
                featureMap.put("verbose_logging", config.general.verbose ? 1 : 0);

                return featureMap;
            }));

            logger.info("bStats metrics initialized");

        } catch (Exception e) {
            logger.debug("bStats not available: {}", e.getMessage());
        }
    }

    private double calculateAverageTps() {
        // Simplified TPS calculation - in production, use server-specific APIs
        try {
            return 20.0; // Placeholder - implement actual TPS calculation
        } catch (Exception e) {
            return -1.0;
        }
    }

    private long calculateAverageSessionLength(List<PlayerSession> sessions) {
        if (sessions.size() < 2)
            return 0;

        // Group sessions by date and calculate average length
        // This is simplified - implement proper session length calculation
        return sessions.stream()
                .mapToLong(session -> 1800000) // 30 minutes average placeholder
                .reduce(0L, Long::sum) / sessions.size();
    }

    private Map<String, Long> countActionTypes(List<PlayerSession> sessions) {
        return sessions.stream()
                .collect(Collectors.groupingBy(
                        PlayerSession::getAction,
                        Collectors.counting()));
    }

    private static class PerformanceEntry {
        final String operation;
        final long duration;
        final long timestamp;

        PerformanceEntry(String operation, long duration, long timestamp) {
            this.operation = operation;
            this.duration = duration;
            this.timestamp = timestamp;
        }
    }
}