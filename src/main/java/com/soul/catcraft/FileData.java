package com.soul.catcraft;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

import static com.soul.catcraft.Constants.FileSystem.*;

public class FileData {
    public CatCraft plugin;
    public File playerDataFile;
    private List<String> playerList;

    public FileData(CatCraft plugin) {
        this.plugin = plugin;
        this.playerList = new ArrayList<>();
    }

    public void init() {
        this.playerDataFile = new File(PLAYER_LIST_PATH);
        if (!this.playerDataFile.exists()) {
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

        if (newFile) {
            plugin.getLogger().info(String.format(Constants.ErrorMessages.FILE_CREATION_SUCCESS,
                    this.playerDataFile.getAbsoluteFile()));
        } else {
            plugin.getLogger().severe(Constants.ErrorMessages.FILE_CREATION_ERROR);
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
        List<String> playerDataList;
        try {
            playerDataList = readTextFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        for (String playerData : playerDataList) {
            String[] playerStats = playerData.split(PLAYER_DATA_DELIMITER);

            // Skip invalid player data
            if (playerStats.length < MIN_PLAYER_DATA_LENGTH) {
                continue;
            }

            String storedUUID = playerStats[PLAYER_UUID_INDEX];
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
        String storedName = playerStats[PLAYER_NAME_INDEX];
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
        String oldPlayerData = oldName + PLAYER_DATA_DELIMITER + playerUUID;

        for (int i = 0; i < playerList.size(); ++i) {
            if (playerList.get(i).equals(oldPlayerData)) {
                updatePlayerData(i, player);
                break;
            }
        }
    }

    private void updatePlayerData(int index, Player player) {
        String newPlayerData = player.getDisplayName() + PLAYER_DATA_DELIMITER + player.getUniqueId().toString();
        playerList.set(index, newPlayerData);
        logUpdatedPlayer(player, newPlayerData);

        try {
            writeTextFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logUpdatedPlayer(Player player, String newPlayerData) {
        String oldName = newPlayerData.split(PLAYER_DATA_DELIMITER)[PLAYER_NAME_INDEX];
        plugin.log.playerJoined(player, false, true, oldName);
    }

    public void addPlayerToList(Player player) {
        this.playerList.add(player.getDisplayName() + PLAYER_DATA_DELIMITER + player.getUniqueId());
    }
}