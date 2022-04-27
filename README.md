# CatCraft 
### (Minecraft Version 1.17.1)


A bukkit plugin for Minecraft with various functionalities for personal use. 

Feature List:
- Non-invasive player logger via login timestamp  
- Relaying opened chests (Can be turned off - see config.yml)
- Relaying entities dealing damage to other entities to the server shell (Can be turned off - see config.yml)
- Defends cats by deflecting damage inflicted to cats, ocelots and so on. Instantly kills an entity that killed a cat, ocelot etc (Low event priority to keep it lightweight)
- Formats the chat to differentiate between members, mods and admins
- Japanese emoji!


### Command List:

**/cc** or **/catcraft** followed by:
- inv [player] -> Peek into player's inventory
- ender [player] -> Peek into a player's ender chest
- disarm [player] -> Take his armor (and optionally main-hand) into your own inventory
- msgall [message] -> Sends an anonymous instant message to all logged in players
- reload -> reloads the plugin
- help -> List possible commands
- rules -> List rules specified in the config.yml file
- credits -> List credits, including the link to this github repo

**Private whisper function:**
- /ccw [player] [message] -> Sends a non-anonymous private message to a player.

**Discord Ignore Global Chat Message function:**
- /anon [message]

### Permission nodes:
- catcraft.admin -> for /cc or /catcraft commands (WARNING: recommended to give it only to the highest ranked players / OPs)
- catcraft.whisper -> for /ccw (recommended to allow all players to use it)
- chat.format.* (use to your hearts content if you want to differentiate the power levels via chat)
- - chat.format.member
- - chat.format.moderator
- - chat.format.admin


## Build Instructions
### Eclipse:
1. Open Eclipse
2. Import project as "Existing maven project"
3. Build as Maven Install

### IntelliJ Idea:
1. Open project root folder as -> Maven, not Eclipse! - click OK 
2. Add Configuration -> Add New (+) -> choose: "Maven" from the list
3. Type in the "Command Line" option under "Parameters" -> "install" (without the "")
4. Click the Play Button (or Shift+F10) -> the generated snapshot .jar file will be inside the /project_root/target folder

### Visual Studio Code:
1. You may need additional plugins such as:
   - "Maven for Java"
   - "Extension Pack for Java"
2. You may need to install maven and adjust the path varibale to point to the executable.
3. Once everything is set up, open a terminal within VS Code and type: 
```mvn jar:jar install:install```
4. Your .jar file should should be contained in the /target folder now. Enjoy!

**Final step:** Put the generated .jar in the /plugins folder of your MC server.
Important last recommendation for 1.17.1 and upwards: Delete the /plugins/CatCraft folder due to drastic changes made to how the logger works.

Done!
If you encounter any bugs or have ideas how to improve it, contact me via admin@7reed.com or via Discord: soulwax#5586


The source code is MIT licensed as of 2022. 


Non-commercial use of the generated *.jar file and resulting config / log files is permitted for your own server. Author: Korriban



