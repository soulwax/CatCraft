package com.soul.catcraft;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import static com.soul.catcraft.ConfigFile.VERBOSE;

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

    public void removePlayer(Player player) {
        if (player == null)
            return;

        players.remove(player);
        logIfVerbose("Player " + player.getName() + " left.");

        closeViewers(player.getInventory().getViewers());
        closeViewers(player.getEnderChest().getViewers());
    }

    private void closeViewers(List<HumanEntity> viewers) {
        viewers.stream().filter(Objects::nonNull).forEach(HumanEntity::closeInventory);
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

    private void logIfVerbose(String message) {
        if (VERBOSE) {
            plugin.debugger.info(message);
        }
    }

}