package com.soul.catcraft;

import static com.soul.catcraft.ConfigFile.VERBOSE;
import static com.soul.catcraft.Constants.Logging.*;

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

    public void init() {
        File file = new File(PLAYER_LOGS_PATH);
        if (!file.exists()) {
            try {
                if (file.createNewFile() && VERBOSE) {
                    CatCraft.getPlugin().getLogger().info(
                            String.format(LOG_FILE_CREATED, file.getAbsolutePath()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void playerJoined(Player player, boolean isNewPlayer, boolean hasNameChanged, String oldName) {
        Map<String, Object> logEntry = createLogEntry(player, isNewPlayer, hasNameChanged, oldName);

        try {
            DumperOptions options = createYamlOptions();
            Yaml yaml = new Yaml(options);

            FileWriter writer = new FileWriter(PLAYER_LOGS_PATH, true);
            yaml.dump(logEntry, writer);
            writer.write(YAML_SEPARATOR);
            writer.close();
        } catch (IOException e) {
            handleLoggingError(e, player);
        }
    }

    private DumperOptions createYamlOptions() {
        DumperOptions options = new DumperOptions();
        options.setIndent(YAML_INDENT);
        options.setPrettyFlow(YAML_PRETTY_FLOW);
        return options;
    }

    private void handleLoggingError(IOException e, Player player) {
        // Print stack trace to console
        // noinspection CallToPrintStackTrace
        e.printStackTrace();
        // Log the error to the plugin logger
        CatCraft.getPlugin().getLogger().severe(
                String.format(LOG_ERROR_PREFIX, player.getName(), e.getMessage()));
    }

    private Map<String, Object> createLogEntry(Player player, boolean isNewPlayer, boolean hasNameChanged,
            String oldName) {
        Map<String, Object> logEntry = new LinkedHashMap<>();

        logEntry.put(LOG_TIMESTAMP_KEY, Instant.now().toString());
        logEntry.put(LOG_UUID_KEY, player.getUniqueId().toString());
        logEntry.put(LOG_NAME_KEY, player.getName());

        if (isNewPlayer) {
            logEntry.put(LOG_STATUS_KEY, STATUS_NEW);
        } else if (hasNameChanged) {
            logEntry.put(LOG_STATUS_KEY, STATUS_NAMECHANGE);
            logEntry.put(LOG_OLD_NAME_KEY, oldName);
        } else {
            logEntry.put(LOG_STATUS_KEY, STATUS_KNOWN);
        }

        return logEntry;
    }
}