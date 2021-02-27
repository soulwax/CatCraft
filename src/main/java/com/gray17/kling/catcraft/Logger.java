package com.gray17.kling.catcraft;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.bukkit.entity.Player;

public class Logger {
    private static final Charset ENCODING = StandardCharsets.UTF_8;
    private static final String PLAYER_LOGS_DIR = FileData.PLUGIN_ROOT_DIR +  "PlayerLogs/";
    public File playerLogFile;


    public void init() {
        File dir = new File(PLAYER_LOGS_DIR);
        if(!dir.exists()) {
            dir.mkdirs();
        }

    }

    @SuppressWarnings("unchecked")
	private void createPlayerLogfile(Player player, String msg) throws IOException {
        String fileName = player.getDisplayName() + ".txt";
        @SuppressWarnings("rawtypes")
		ArrayList message = new ArrayList();
        message.add(msg);
        this.playerLogFile = new File(PLAYER_LOGS_DIR + fileName);
        if(!this.playerLogFile.exists()) {
            this.playerLogFile.createNewFile();
        }

        Path path = Paths.get(PLAYER_LOGS_DIR + fileName, new String[0]);
        Files.write(path, message, ENCODING, new OpenOption[0]);
    }

    public void knownPlayerJoined(Player player) {
        String msg = "KNOWN," + player.getUniqueId().toString();

        try {
            this.createPlayerLogfile(player, msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void newPlayerJoined(Player player) {
        String msg = "NEW," + player.getUniqueId().toString();

        try {
            this.createPlayerLogfile(player, msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void changedPlayerJoined(Player player, String oldName) {
        String msg = "NAMECHANGE," + player.getUniqueId().toString() + "," + oldName;

        try {
            this.createPlayerLogfile(player, msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}