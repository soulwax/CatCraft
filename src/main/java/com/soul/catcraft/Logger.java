package com.soul.catcraft;

import static com.soul.catcraft.ConfigFile.VERBOSE;

import org.bukkit.entity.Player;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public class Logger {
    private static final String PLAYER_LOGS_FILE = FileData.PLUGIN_ROOT_DIR + "PlayerLogs.yml";

    public void init() {
        File file = new File(PLAYER_LOGS_FILE);
        if (!file.exists()) {
            try {
                if (file.createNewFile() && VERBOSE) {
                    CatCraft.getPlugin().getLogger().info("Player Logs File created at:" + file.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void playerJoined(Player player, boolean isNewPlayer, boolean hasNameChanged, String oldName) {
        Map<String, Object> logEntry = createLogEntry(player, isNewPlayer, hasNameChanged, oldName);

        try {
            DumperOptions options = new DumperOptions();
            options.setIndent(2);
            options.setPrettyFlow(true);
            Yaml yaml = new Yaml(options);

            FileWriter writer = new FileWriter(PLAYER_LOGS_FILE, true);
            yaml.dump(logEntry, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> createLogEntry(Player player, boolean isNewPlayer, boolean hasNameChanged, String oldName) {
        Map<String, Object> logEntry = new LinkedHashMap<>();

        logEntry.put("timestamp", Instant.now().toString());
        logEntry.put("uuid", player.getUniqueId().toString());
        logEntry.put("name", player.getName());

        if (isNewPlayer) {
            logEntry.put("status", "NEW");
        } else if (hasNameChanged) {
            logEntry.put("status", "NAMECHANGE");
            logEntry.put("oldName", oldName);
        } else {
            logEntry.put("status", "KNOWN");
        }

        return logEntry;
    }
}