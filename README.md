# CatCraft
### (Minecraft Version 1.19.4)

## HUGE RECOMMENDATION:
## DELETE THE /plugins/CatCraft FOLDER BEFORE UPDATING TO THIS VERSION! I USE YAML LOG FILES NOW AND THE OLD LOG FILES ARE NOT COMPATIBLE WITH THE NEW VERSION!

A bukkit plugin for Minecraft with various functionalities for personal use.

Feature List:
- Non-invasive player logger via login timestamp
- Relaying opened chests (Can be turned off - see config.yml)
- Relaying entities dealing damage to other entities to the server shell (Can be turned off - see config.yml)
- Defends cats by deflecting damage inflicted to cats, ocelots and so on. Instantly kills an entity that killed a cat, ocelot etc (Low event priority to keep it lightweight)
- Formats the chat to differentiate between members, mods and admins
- Japanese emoji replacement (Can be turned off - see config.yml)


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
- - chat.format.member (default)
- - chat.format.moderator
- - chat.format.admin (op)


## Build Instructions
You need to have JDK 17 installed. (https://adoptium.net/?variant=openjdk17&jvmVariant=hotspot)
All there is to it: 
```bash
.\gradlew jar
```
On Linux / Mac:
```bash
./gradlew jar
```

You can use different IDEs to your hearts content. I use IntelliJ IDEA Community Edition or VS Code, it doesn't matter.

**Final step:** Put the generated .jar in the /plugins folder of your MC server.
Important last recommendation for 1.17.1 and upwards: Delete the /plugins/CatCraft folder due to drastic changes made to how the logger works.

Done!
If you encounter any bugs or have ideas how to improve it, contact me via github or Discord: soulwax#5473


The source code is MIT licensed as of 2023.


Non-commercial use of the generated *.jar file and resulting config / log files is permitted for your own server. Author: https://github.com/Soulwax



