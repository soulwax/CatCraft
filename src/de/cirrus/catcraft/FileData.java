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
import java.util.Iterator;
import java.util.List;
import org.bukkit.entity.Player;

public class FileData {
    private static final Charset ENCODING;
    private static final String DIRECTORY = "plugins/CatCraft/";
    private static final String FILE_NAME = "PlayerList.txt";
    public CatCraft plugin;
    public File playerDataFile;
    private List<String> playerList;

    public FileData(CatCraft plugin) {
        this.plugin = plugin;
        this.playerList = new ArrayList();
    }

    public void init() {
        this.playerDataFile = new File("plugins/CatCraft/PlayerList.txt");
        if(!this.playerDataFile.exists()) {
            this.createPlayerFile();
        }

        try {
            this.playerList = this.readTextFile();
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    private void createPlayerFile() {
        try {
            this.playerDataFile.createNewFile();
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public List<String> readTextFile() throws IOException {
        Path path = Paths.get("plugins/CatCraft/PlayerList.txt", new String[0]);
        return Files.readAllLines(path, ENCODING);
    }

    public void writeTextFile() throws IOException {
        Path path = Paths.get("plugins/CatCraft/PlayerList.txt", new String[0]);
        Files.write(path, this.playerList, ENCODING, new OpenOption[0]);
    }

    public boolean checkPlayerUUID(Player player) {
        try {
            Iterator e = this.readTextFile().iterator();

            while(e.hasNext()) {
                String s = (String)e.next();
                String[] playerStats = s.split(",");
                if(playerStats.length > 1 && playerStats[1].equals(player.getUniqueId().toString())) {
                    if(!playerStats[0].equals(player.getDisplayName())) {
                        this.correctName(playerStats[0], player);
                    } else {
                        this.plugin.log.knownPlayerJoined(player);
                    }

                    return true;
                }
            }
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        this.plugin.log.newPlayerJoined(player);
        return false;
    }

    public void correctName(String oldName, Player player) {
        try {
            for(int e = 0; e < this.playerList.size(); ++e) {
                if(((String)this.playerList.get(e)).equals(oldName + "," + player.getUniqueId().toString())) {
                    this.playerList.set(e, player.getDisplayName() + "," + player.getUniqueId().toString());
                    this.plugin.log.changedPlayerJoined(player, oldName);
                    this.writeTextFile();
                }
            }
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public void addPlayerToList(Player player) {
        this.playerList.add(player.getDisplayName() + "," + player.getUniqueId().toString());
    }

    static {
        ENCODING = StandardCharsets.UTF_8;
    }
}
