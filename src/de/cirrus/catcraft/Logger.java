package de.cirrus.catcraft;

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
    private CatCraft plugin;
    private static final Charset ENCODING;
    private static final String DIRECTORY = "./plugins/CatCraft/PlayerLogs/";
    public File playerLogFile;

    public Logger(CatCraft plugin) {
        this.plugin = plugin;
    }

    public void init() {
        File dir = new File(DIRECTORY);
        if(!dir.exists()) {
            dir.mkdirs();
        }

    }

    private void createPlayerLogfile(Player player, String msg) throws IOException {
        String fileName = player.getDisplayName() + ".txt";
        ArrayList message = new ArrayList();
        message.add(msg);
        this.playerLogFile = new File(DIRECTORY + fileName);
        if(!this.playerLogFile.exists()) {
            this.playerLogFile.createNewFile();
        }

        Path path = Paths.get(DIRECTORY + fileName, new String[0]);
        Files.write(path, message, ENCODING, new OpenOption[0]);
    }

    public void knownPlayerJoined(Player player) {
        String msg = "KNOWN," + player.getUniqueId().toString();

        try {
            this.createPlayerLogfile(player, msg);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public void newPlayerJoined(Player player) {
        String msg = "NEW," + player.getUniqueId().toString();

        try {
            this.createPlayerLogfile(player, msg);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public void changedPlayerJoined(Player player, String oldName) {
        String msg = "NAMECHANGE," + player.getUniqueId().toString() + "," + oldName;

        try {
            this.createPlayerLogfile(player, msg);
        } catch (IOException var5) {
            var5.printStackTrace();
        }

    }

    static {
        ENCODING = StandardCharsets.UTF_8;
    }
}