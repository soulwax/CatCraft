// File: src/main/java/com/soul/catcraft/repositories/PlayerRepository.java

package com.soul.catcraft.repositories;

import com.soul.catcraft.models.PlayerData;
import com.soul.catcraft.models.PlayerSession;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface PlayerRepository {
    CompletableFuture<PlayerData> findByUuid(String uuid);
    CompletableFuture<Void> save(PlayerData playerData);
    CompletableFuture<Void> savePlayerSession(Player player, String action);
    CompletableFuture<List<PlayerSession>> getPlayerHistory(String uuid, int limit);
    CompletableFuture<Map<String, Integer>> getPlayerStatistics();
    CompletableFuture<List<String>> getTopPlayersByMetric(String metric, int limit);
    CompletableFuture<List<PlayerData>> findRecentPlayers(int days);
    CompletableFuture<Void> deleteOldSessions(int daysToKeep);
}