// File: src/main/java/com/soul/PlayerHandler.java

package com.soul.catcraft;

import static com.soul.catcraft.ConfigFile.VERBOSE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class PlayerHandler {
    private final CatCraft plugin;
    private final FileData data;
    private final Set<Player> players = new HashSet<>();

    public PlayerHandler(CatCraft plugin) {
        this.plugin = plugin;
        this.data = plugin.data;
    }

    public void init() {
        this.players.clear();
        this.getOnlinePlayers();
    }

    public Set<Player> getPlayers() {
        return this.players;
    }

    public Player getPlayer(String name) {
        Player result = null;

        for (Player p : this.players) {
            if (p.getName().equals(name)) {
                result = p;
            }
        }

        return result;
    }

    public void addPlayer(Player player) {
        if (player == null)
            return;

        players.add(player);
        String displayName = player.getDisplayName();

        if (data.checkPlayerUUID(player)) {
            data.addPlayerToList(player);
            logIfVerbose("The player " + displayName + " is NEW. He was added to the CatCraft Player List.");

            try {
                data.writeTextFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            logIfVerbose(
                    "Player: " + displayName + " joined - found in the Player List, UUID: " + player.getUniqueId());
        }
    }

    // More thread-safe way to remove a player
    public void removePlayer(Player player) {
        if (player == null)
            return;

        try {
            synchronized (players) { // Synchronize access to players set
                players.remove(player);
            }
            logIfVerbose("Player " + player.getName() + " left.");

            // Close viewers safely
            try {
                closeViewers(new ArrayList<>(player.getInventory().getViewers()));
                closeViewers(new ArrayList<>(player.getEnderChest().getViewers()));
            } catch (Exception e) {
                plugin.getLogger()
                        .warning("Error closing viewers for player: " + player.getName() + " - " + e.getMessage());
            }

        } catch (Exception e) {
            plugin.getLogger().severe("Error removing player: " + player.getName() + " - " + e.getMessage()
                    + ". Attempting to refresh player list.");
            // Fallback to refresh
            try {
                this.checkOnlinePlayers();
                plugin.getLogger().info("Player list refreshed after error.");
            } catch (Exception ex) {
                plugin.getLogger().severe("Error refreshing online players: " + ex.getMessage());
            }
        }
    }

    private void closeViewers(List<HumanEntity> viewers) {
        // Create a copy to avoid ConcurrentModificationException
        new ArrayList<>(viewers)
                .stream()
                .filter(Objects::nonNull)
                .forEach(HumanEntity::closeInventory);
    }

    public void getOnlinePlayers() {
        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        logIfVerbose("retrieved online players");

        for (Player player : onlinePlayers) {
            addPlayer(player);

            if (data.checkPlayerUUID(player)) {
                data.addPlayerToList(player);

                try {
                    data.writeTextFile();
                    logIfVerbose("Wrote Player Data while checking all online Players");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                logIfVerbose("Checked all Player Data but no new Players found...");
            }
        }
    }

    public void clearPlayers() {
        players.clear();
        plugin.getLogger().warning("Cleared all players from PlayerHandler... This should not happen often. If it does, please report it.");
    }

    private void logIfVerbose(String message) {
        if (VERBOSE) {
            plugin.debugger.info(message);
        }
    }

    // If a player disconnects, check which players are still online, adapt the player list accordingly.
    // This method is a FALLBACK to removing players from the HashSet directly in case of concurrency blockades.
    // This ensures that the player list is always up to date.
    public void checkOnlinePlayers() {
        try {
            Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
            Set<Player> currentPlayers = new HashSet<>(players);

            // Clear players, add only those who are still online
            this.clearPlayers();
            for (Player player : onlinePlayers) {
                if (currentPlayers.contains(player)) {
                    players.add(player);
                }
            }

            plugin.getLogger().info("Checked online players and updated player list.");
        } catch (Exception e) {
            plugin.getLogger().severe("Error checking online players: " + e.getMessage());
        }
    }

}