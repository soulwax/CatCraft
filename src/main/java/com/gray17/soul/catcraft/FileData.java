package com.gray17.soul.catcraft;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

public class FileData {
    private static final Charset ENCODING = StandardCharsets.UTF_8;

    public static final String PLUGIN_ROOT_DIR = "./plugins/CatCraft/";
    public static final String PLAYER_LIST_FILENAME = "PlayerList.txt";
    public static final String PLAYER_LIST_PATH = PLUGIN_ROOT_DIR + PLAYER_LIST_FILENAME;

    public CatCraft plugin;
    public File playerDataFile;
    private List<String> playerList;

    public FileData(CatCraft plugin) {
        this.plugin = plugin;
        this.playerList = new ArrayList<>();
    }

    public void init() {
        this.playerDataFile = new File(PLAYER_LIST_PATH);
        if(!this.playerDataFile.exists()) {
            this.createPlayerFile();
        }

        try {
            this.playerList = this.readTextFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createPlayerFile() {
        try {
            if(this.playerDataFile.createNewFile())
                CatCraft.getPlugin().getLogger().info("Created Player File in \"" + playerDataFile.getAbsolutePath() + "\"");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> readTextFile() throws IOException {
        Path path = Paths.get(PLAYER_LIST_PATH);
        return Files.readAllLines(path, ENCODING);
    }

    public void writeTextFile() throws IOException {
        Path path = Paths.get(PLAYER_LIST_PATH);
        Files.write(path, this.playerList, ENCODING);
    }

    public boolean checkPlayerUUID(Player player) {
        try {

            for (String s : this.readTextFile()) {
                String[] playerStats;
                playerStats = s.split(",");

                // If player is known
                if (playerStats.length > 1 && playerStats[1].equals(player.getUniqueId().toString())) {

                    // If name doesn't match but UUID is the same
                    if (!playerStats[0].equals(player.getDisplayName())) {
                        this.updateName(playerStats[0], player);
                        // Else (if name and UUID match)
                    } else {
                        this.plugin.log.playerJoined(player, false, false, null);
                    }

                    // When player is known, leave method
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // If new player has joined
        this.plugin.log.playerJoined(player, true, false, null);
        return true;
    }

    public void updateName(String oldName, Player player) {
        try {
            for(int e = 0; e < this.playerList.size(); ++e) {
                if((this.playerList.get(e)).equals(oldName + "," + player.getUniqueId())) {
                    this.playerList.set(e, player.getDisplayName() + "," + player.getUniqueId());
                    this.plugin.log.playerJoined(player, false, true, oldName);
                    this.writeTextFile();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addPlayerToList(Player player) {
        this.playerList.add(player.getDisplayName() + "," + player.getUniqueId());
    }
}

