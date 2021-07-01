package com.gray17.soul.catcraft;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;


import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

public class Logger {
    private static final String PLAYER_LOGS_DIR = FileData.PLUGIN_ROOT_DIR +  "PlayerLogs/";
    public File playerLogFile;
    

    public void init() {
        File dir = new File(PLAYER_LOGS_DIR);
        if(!dir.exists()) {
            dir.mkdirs();
        }

    }
    

	private void createNewPlayerLogfile(Player player) throws IOException {
        String fileName = player.getDisplayName() + ".txt";
        this.playerLogFile = new File(PLAYER_LOGS_DIR + fileName);
        if(!this.playerLogFile.exists()) {
            this.playerLogFile.createNewFile();
        }

    }

    public void playerJoined(Player player, boolean isNewPlayer, boolean hasNameChanged, String oldName) {
    	String logFileMessage = getNewLogString(player, isNewPlayer, hasNameChanged, oldName);
    	
    	try {
    		this.createNewPlayerLogfile(player);
    		Writer output;
    		output = new BufferedWriter(new FileWriter(this.playerLogFile, true));
    		((BufferedWriter) output).newLine();
    		output.append(logFileMessage);
    		output.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    private String getNewLogString(Player player, boolean isNewPlayer, boolean hasNameChanged, String oldName) {
    	String result = "";
    	if (isNewPlayer) {
    		result += "NEW, ";
    	} else if (!isNewPlayer && !hasNameChanged) {
    		result += "KNOWN, ";
    	} else if (!isNewPlayer && hasNameChanged) {
    		result += "NAMECHANGE, ";
    	} else if (isNewPlayer && hasNameChanged) {
    		result += "Erroneous Logfile - Player can't have his name changed AND be new. - ";
    	}
    		
    	if(!StringUtils.isBlank(result)) {
    		result += "Name: " + player.getName() + ", ";
        	if(hasNameChanged) {
        		result += "Old Name: " + oldName + ", ";        			
        	}
        	result += "UUID: " + player.getUniqueId().toString() + ", ";
        	LocalDateTime date = LocalDateTime.now();
        	result += date;
    	}
    	
    	
    	return result;
    }
}