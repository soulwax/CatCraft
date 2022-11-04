package com.gray17.soul.catcraft;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import static com.gray17.soul.catcraft.ConfigFile.VERBOSE;

public class PlayerHandler {
    private final CatCraft plugin;
    private final FileData data;
	private final ArrayList<Player> players = new ArrayList<>();

    public PlayerHandler(CatCraft plugin) {
        this.plugin = plugin;
        this.data = plugin.data;
    }

    public void init() {
        this.players.clear();
        this.getOnlinePlayers();
    }

    public List<Player> getPlayers() {
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
        if(player != null) {
            this.players.add(player);
        }
        if(player != null) {
            String displayName = player.getDisplayName();

            if(this.data.checkPlayerUUID(player)) {
    
                this.data.addPlayerToList(player);
                if(VERBOSE)
                    plugin.debugger.info("The player " + displayName + " is NEW. He was added to the CatCraft Player List.");
    
                try {
                    this.data.writeTextFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if(VERBOSE) {
                plugin.debugger.info("Player: " + displayName + " joined - found in the Player List, UUID: " + player.getUniqueId());
            }
        }
    }

    public void removePlayer(Player player) {
        if(player != null) {
            this.players.remove(player);
            if(VERBOSE) {
            	plugin.debugger.info("Player " + player.getName() + " left.");
            }

			List<HumanEntity> invViewers = player.getInventory().getViewers();
			List<HumanEntity> enderViewers = player.getEnderChest().getViewers();

            AtomicInteger i = new AtomicInteger();
            for(i.set(0); i.get() < invViewers.size(); i.incrementAndGet()) {
                if(invViewers.get(i.get()) != null) {
                    (invViewers.get(i.get())).closeInventory();
                }
            }

            for(i.set(0); i.get() < enderViewers.size(); i.incrementAndGet()) {
                if(enderViewers.get(i.get()) != null) {
                    (enderViewers.get(i.get())).closeInventory();
                }
            }
        }
    }

    private void getOnlinePlayers() {
        Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
        if(VERBOSE) {
        	plugin.debugger.info("retrieved online players");
        }

        for(Player p : players) {

            this.addPlayer(p);
            if(this.data.checkPlayerUUID(p)) {
                this.data.addPlayerToList(p);

                try {
                    this.data.writeTextFile();
                    if(VERBOSE) {
                        plugin.debugger.info("Wrote Player Data while checking all online Players");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if(VERBOSE) {
                plugin.debugger.info("Checked all Player Data but no new Players found...");
            }
        }
    }
}