
package de.cirrus.catcraft;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class PlayerHandler {
    private CatCraft plugin;
    private FileData data;
    private List<Player> players = new ArrayList();

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
        Iterator var3 = this.players.iterator();

        while(var3.hasNext()) {
            Player p = (Player)var3.next();
            if(p.getName().equals(name)) {
                result = p;
            }
        }

        return result;
    }

    public void addPlayer(Player player) {
        if(player != null) {
            this.players.add(player);
        }

        if(!this.data.checkPlayerUUID(player)) {
            this.data.addPlayerToList(player);
            if(InputHandler.VERBOSE) {
                System.out.println("The player " + player.getDisplayName() + " is new and was added to the Player List.");
            }

            try {
                this.data.writeTextFile();
            } catch (IOException var3) {
                var3.printStackTrace();
            }
        } else if(InputHandler.VERBOSE) {
            this.plugin.getLogger().info("The player " + player.getDisplayName() + " joined but was found in the Player List, UUID: " + player.getUniqueId().toString());
        }

    }

    public void removePlayer(Player player) {
        if(player != null) {
            this.players.remove(player);
            if(InputHandler.VERBOSE) {
                this.plugin.getLogger().info("Player " + player.getName() + " left");
            }

            List invViewers = player.getInventory().getViewers();
            List enderViewers = player.getEnderChest().getViewers();

            int i;
            for(i = 0; i < invViewers.size(); ++i) {
                if(invViewers.get(i) != null) {
                    ((HumanEntity)invViewers.get(i)).closeInventory();
                }
            }

            for(i = 0; i < enderViewers.size(); ++i) {
                if(enderViewers.get(i) != null) {
                    ((HumanEntity)enderViewers.get(i)).closeInventory();
                }
            }
        }

    }

    private void getOnlinePlayers() {
        Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
        if(InputHandler.VERBOSE) {
            this.plugin.getLogger().info("retreived online players");
        }

        if(players != null) {
            for(Player p : players) {
                this.addPlayer(p);
                if(!this.data.checkPlayerUUID(p)) {
                    this.data.addPlayerToList(p);

                    try {
                        this.data.writeTextFile();
                        if(InputHandler.VERBOSE) {
                            System.out.println("Wrote Player Data while checking all online Players");
                        }
                    } catch (IOException var7) {
                        var7.printStackTrace();
                    }
                } else if(InputHandler.VERBOSE) {
                    System.out.println("Checked all Player Data but no new Players found...");
                }
            }
        }

    }
}
