# CatCraft
A bukkit plugin for Minecraft with various functionalities for personal use. 
Feature List:
- Player login logger
- Log opened chests and inflicted damage of Entity objects inside the server cmd

Commands /cc or /catcraft followed by:
- inv [player] -> Peek into player's inventory
- disarm [player] -> Take his armor (and optionally main-hand) into your own inventory
- msgall [message] -> Sends an anonymous instant message to all logged in players
- reload -> reloads the plugin
- help -> List possible commands
- credits -> List credits, including the link to this github repo

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

Final step: Put the generated .jar in the /plugins folder of your MC server.
Important last recommendation for 1.17.1: Delete the /plugins/CatCraft folder due to drastic changes made to how the logger works.

Done!
The source code is subject to copyright as of 2021. Non-commercial use of the generated *.jar file and resulting config / log files is permitted for your own server. Author: Korriban



