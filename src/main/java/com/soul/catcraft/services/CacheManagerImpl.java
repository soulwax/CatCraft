// File: src/main/java/com/soul/catcraft/services/CacheManagerImpl.java

package com.soul.catcraft.services;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheStats;
import com.google.common.cache.RemovalCause;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.soul.catcraft.models.PlayerData;
import com.soul.catcraft.models.PlayerSession;

import net.kyori.adventure.text.Component;

@Singleton
public class CacheManagerImpl implements CacheManager {
    private static final Logger logger = LoggerFactory.getLogger(CacheManagerImpl.class);

    private final Cache<String, PlayerData> playerCache;
    private final Cache<String, Component> messageCache;
    private final Cache<String, List<PlayerSession>> sessionCache;
    private final Cache<String, Object> generalCache;

    private final ConfigService configService;
    private final ScheduledExecutorService executorService;

    @Inject
    public CacheManagerImpl(ConfigService configService, ScheduledExecutorService executorService) {
        this.configService = configService;
        this.executorService = executorService;

        // Initialize caches with different configurations
        this.playerCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .expireAfterAccess(15, TimeUnit.MINUTES)
                .recordStats()
                .removalListener(this::onPlayerCacheRemoval)
                .build();

        this.messageCache = Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .recordStats()
                .build();

        this.sessionCache = Caffeine.newBuilder()
                .maximumSize(200)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .recordStats()
                .build();

        this.generalCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .recordStats()
                .build();

        // Schedule cache maintenance
        startCacheMaintenance();
    }

    @Override
    public void cachePlayerData(String uuid, PlayerData playerData) {
        if (playerData != null) {
            playerCache.put(uuid, playerData);
        }
    }

    @Override
    public Optional<PlayerData> getCachedPlayerData(String uuid) {
        return Optional.ofNullable(playerCache.getIfPresent(uuid));
    }

    @Override
    public void invalidatePlayerData(String uuid) {
        playerCache.invalidate(uuid);
        // Also invalidate related session cache
        sessionCache.invalidate(uuid + "_history");
    }

    @Override
    public void cacheFormattedMessage(String key, Component message) {
        messageCache.put(key, message);
    }

    @Override
    public Optional<Component> getCachedMessage(String key) {
        return Optional.ofNullable(messageCache.getIfPresent(key));
    }

    @Override
    public void cachePlayerSessions(String uuid, List<PlayerSession> sessions) {
        sessionCache.put(uuid + "_history", sessions);
    }

    @Override
    public Optional<List<PlayerSession>> getCachedPlayerSessions(String uuid) {
        @SuppressWarnings("unchecked")
        List<PlayerSession> sessions = (List<PlayerSession>) sessionCache.getIfPresent(uuid + "_history");
        return Optional.ofNullable(sessions);
    }

    @Override
    public <T> void cacheObject(String key, T object, Duration duration) {
        // For custom duration caching, use the general cache
        generalCache.put(key, new CacheEntry<>(object, System.currentTimeMillis() + duration.toMillis()));
    }

    @Override
    public <T> Optional<T> getCachedObject(String key, Class<T> type) {
        Object cached = generalCache.getIfPresent(key);
        if (cached instanceof CacheEntry) {
            @SuppressWarnings("unchecked")
            CacheEntry<T> entry = (CacheEntry<T>) cached;

            if (System.currentTimeMillis() < entry.expiry) {
                return Optional.of(entry.value);
            } else {
                generalCache.invalidate(key);
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    @Override
    public CacheStats getPlayerCacheStats() {
        return playerCache.stats();
    }

    @Override
    public CacheStats getMessageCacheStats() {
        return messageCache.stats();
    }

    @Override
    public void invalidateAll() {
        playerCache.invalidateAll();
        messageCache.invalidateAll();
        sessionCache.invalidateAll();
        generalCache.invalidateAll();

        logger.info("All caches invalidated");
    }

    @Override
    public Map<String, CacheStats> getAllCacheStats() {
        Map<String, CacheStats> stats = new HashMap<>();
        stats.put("players", playerCache.stats());
        stats.put("messages", messageCache.stats());
        stats.put("sessions", sessionCache.stats());
        stats.put("general", generalCache.stats());
        return stats;
    }

    private void onPlayerCacheRemoval(String uuid, PlayerData playerData, RemovalCause cause) {
        if (configService.getConfig().general.debugMode) {
            logger.debug("Player data for {} removed from cache: {}", uuid, cause);
        }
    }

    private void startCacheMaintenance() {
        // Clean expired entries every 10 minutes
        executorService.scheduleAtFixedRate(() -> {
            try {
                playerCache.cleanUp();
                messageCache.cleanUp();
                sessionCache.cleanUp();
                generalCache.cleanUp();

                if (configService.getConfig().general.debugMode) {
                    logCacheStatistics();
                }
            } catch (Exception e) {
                logger.error("Error during cache maintenance", e);
            }
        }, 10, 10, TimeUnit.MINUTES);
    }

    private void logCacheStatistics() {
        Map<String, CacheStats> allStats = getAllCacheStats();

        for (Map.Entry<String, CacheStats> entry : allStats.entrySet()) {
            CacheStats stats = entry.getValue();
            logger.debug("Cache '{}' - Size: {}, Hit Rate: {:.2f}%, Evictions: {}",
                    entry.getKey(),
                    getCacheSize(entry.getKey()),
                    stats.hitRate() * 100,
                    stats.evictionCount());
        }
    }

    private long getCacheSize(String cacheName) {
        switch (cacheName) {
            case "players":
                return playerCache.estimatedSize();
            case "messages":
                return messageCache.estimatedSize();
            case "sessions":
                return sessionCache.estimatedSize();
            case "general":
                return generalCache.estimatedSize();
            default:
                return 0;
        }
    }

    private static class CacheEntry<T> {
        final T value;
        final long expiry;

        CacheEntry(T value, long expiry) {
            this.value = value;
            this.expiry = expiry;
        }
    }
}