// File: src/main/java/com/soul/catcraft/repositories/PlayerRepositoryImpl.java

package com.soul.catcraft.repositories;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.entity.Player;

import com.google.inject.Singleton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.soul.catcraft.models.PlayerData;
import com.soul.catcraft.models.PlayerSession;

// repositories/PlayerRepositoryImpl.java (expanded)
@Singleton
public class PlayerRepositoryImpl implements PlayerRepository {
    private final Dao<PlayerData, String> playerDao;
    private final Dao<PlayerSession, Long> sessionDao;
    private final ConnectionSource connectionSource;
    private final ExecutorService asyncExecutor;

    @Inject
    public PlayerRepositoryImpl(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
        this.playerDao = DaoManager.createDao(connectionSource, PlayerData.class);
        this.sessionDao = DaoManager.createDao(connectionSource, PlayerSession.class);
        this.asyncExecutor = Executors.newFixedThreadPool(4);

        // Create tables if they don't exist
        TableUtils.createTableIfNotExists(connectionSource, PlayerData.class);
        TableUtils.createTableIfNotExists(connectionSource, PlayerSession.class);
    }

    @Override
    public CompletableFuture<Void> savePlayerSession(Player player, String action) {
        return CompletableFuture.runAsync(() -> {
            try {
                TransactionManager.callInTransaction(connectionSource, () -> {
                    // Update player data
                    PlayerData playerData = playerDao.queryForId(player.getUniqueId().toString());
                    if (playerData == null) {
                        playerData = new PlayerData(player);
                    }
                    playerData.setLastSeen(Timestamp.from(Instant.now()));
                    playerDao.createOrUpdate(playerData);

                    // Log session
                    PlayerSession session = new PlayerSession(
                            player.getUniqueId().toString(),
                            action,
                            Timestamp.from(Instant.now()));
                    sessionDao.create(session);

                    return null;
                });
            } catch (SQLException e) {
                throw new RuntimeException("Transaction failed", e);
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<List<PlayerSession>> getPlayerHistory(String uuid, int limit) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return sessionDao.queryBuilder()
                        .where().eq("player_uuid", uuid)
                        .and().orderBy("timestamp", false)
                        .limit((long) limit)
                        .query();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to query player history", e);
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<Map<String, Integer>> getPlayerStatistics() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Integer> stats = new HashMap<>();

                // Total players
                stats.put("total_players", (int) playerDao.countOf());

                // Players online today
                Timestamp today = Timestamp.valueOf(LocalDate.now().atStartOfDay());
                stats.put("players_today", (int) playerDao.queryBuilder()
                        .where().ge("last_seen", today)
                        .countOf());

                // New players this week
                Timestamp weekAgo = Timestamp.from(Instant.now().minus(7, ChronoUnit.DAYS));
                stats.put("new_players_week", (int) playerDao.queryBuilder()
                        .where().ge("first_join", weekAgo)
                        .countOf());

                return stats;
            } catch (SQLException e) {
                throw new RuntimeException("Failed to generate statistics", e);
            }
        }, asyncExecutor);
    }
}