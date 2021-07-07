# CatCraft
A bukkit plugin for Minecraft with various functionalities for personal use. 

Feature List:
- Non-invasive player logger via login timestamp  
- Relaying opened chests (Can be turned off - see config.yml)
- Relaying entities dealing damage to other entities to the server command line. (Can be turned off - see config.yml)
- Defends cats by deflecting damage inflicted to cats, ocelots and so on. Instantly kills an entity that killed a cat, ocelot etc. (Low event priority to keep it light weight)
- Formats the chat to differentiate between members, mods and admins.
- Soon: Japanese emoji 


Command List: 

**/cc** or **/catcraft** followed by:
- inv [player] -> Peek into player's inventory
- disarm [player] -> Take his armor (and optionally main-hand) into your own inventory
- msgall [message] -> Sends an anonymous instant message to all logged in players
- reload -> reloads the plugin
- help -> List possible commands
- credits -> List credits, including the link to this github repo

Private whisper function:
/ccw [player] [message] -> Sends a non-anonymous private message to a player.

## Build Instructions
### Eclipse:
1. Open Eclipse
2. Import project as "Existing maven project"
3. Build as Maven Install

### IntelliJ Idea (recommended):
1. Open project root folder as -> Maven, not Eclipse! - click OK 
2. Add Configuration -> Add New (+) -> choose: "Maven" from the list
3. Type in the "Command Line" option under "Parameters" -> "install" (without the "")
4. Click the Play Button (or Shift+F10) -> the generated snapshot .jar file will be inside the /project_root/target folder

**Final step:** Put the generated .jar in the /plugins folder of your MC server.
Important last recommendation for 1.17.1: Delete the /plugins/CatCraft folder due to drastic changes made to how the logger works.

Done!
If you encounter any bugs or have ideas how to improve it, contact me via admin@7reed.com


The source code is subject to copyright as of 2021. 


Non-commercial use of the generated *.jar file and resulting config / log files is permitted for your own server. Author: Korriban



