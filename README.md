# CatCraft
A bukkit plugin for Minecraft with various functionalities for personal use. Copyright Cirrus 2021

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



