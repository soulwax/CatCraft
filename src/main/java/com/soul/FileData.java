// File: src/main/java/com/soul/catcraft/FileData.java

package com.soul.catcraft;

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

// 

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
        boolean newFile = false;
        try {
            newFile = this.playerDataFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(newFile) plugin.getLogger().info("Success: Player Data File created. Location: " + this.playerDataFile.getAbsoluteFile());
        else plugin.getLogger().severe("Something went terribly wrong with creating a new Player Data File. See stack trace above.");
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
        List<String> playerDataList;
        try {
            playerDataList = readTextFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        for (String playerData : playerDataList) {
            String[] playerStats = playerData.split(",");

            // Skip invalid player data
            if (playerStats.length <= 1) {
                continue;
            }

            String storedUUID = playerStats[1];
            String currentUUID = player.getUniqueId().toString();

            // If player is known
            if (storedUUID.equals(currentUUID)) {
                handleKnownPlayer(player, playerStats);
                return false;
            }
        }

        // If new player has joined
        logNewPlayer(player);
        return true;
    }

    private void handleKnownPlayer(Player player, String[] playerStats) {
        String storedName = playerStats[0];
        String currentName = player.getDisplayName();

        // If name doesn't match but UUID is the same
        if (!storedName.equals(currentName)) {
            updateName(storedName, player);
        } else {
            // If name and UUID match
            plugin.log.playerJoined(player, false, false, null);
        }
    }

    private void logNewPlayer(Player player) {
        plugin.log.playerJoined(player, true, false, null);
    }

    public void updateName(String oldName, Player player) {
        String playerUUID = player.getUniqueId().toString();
        String oldPlayerData = oldName + "," + playerUUID;

        for (int i = 0; i < playerList.size(); ++i) {
            if (playerList.get(i).equals(oldPlayerData)) {
                updatePlayerData(i, player);
                break;
            }
        }
    }

    private void updatePlayerData(int index, Player player) {
        String newPlayerData = player.getDisplayName() + "," + player.getUniqueId().toString();
        playerList.set(index, newPlayerData);
        logUpdatedPlayer(player, newPlayerData);

        try {
            writeTextFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logUpdatedPlayer(Player player, String newPlayerData) {
        String oldName = newPlayerData.split(",")[0];
        plugin.log.playerJoined(player, false, true, oldName);
    }

    public void addPlayerToList(Player player) {
        this.playerList.add(player.getDisplayName() + "," + player.getUniqueId());
    }
}

