// File: src/main/java/com/soul/catcraft/services/PlayerService.java

package com.soul.catcraft.services;

import com.soul.catcraft.models.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PlayerService {
    CompletableFuture<Void> handlePlayerJoin(Player player);
    CompletableFuture<Void> handlePlayerQuit(Player player);
    CompletableFuture<Void> disarmPlayer(Player admin, Player target);
    CompletableFuture<PlayerData> getPlayerData(String uuid);
    CompletableFuture<List<String>> getTopPlayers(String metric, int limit);
    CompletableFuture<Void> updatePlayerPlaytime(Player player, int minutes);
    void handleCatProtection(EntityDamageByEntityEvent event);
    boolean hasPlayerIgnored(Player player, Player target);
    CompletableFuture<Void> setPlayerIgnored(Player player, Player target, boolean ignored);
    CompletableFuture<Boolean> isPlayerBanned(String uuid);
    CompletableFuture<Void> banPlayer(String uuid, String reason, long durationMs);
    CompletableFuture<Void> unbanPlayer(String uuid);
}