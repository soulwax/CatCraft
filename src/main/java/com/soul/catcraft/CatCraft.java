// File: src/main/java/com/soul/catcraft/CatCraft.java

package com.soul.catcraft;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.soul.catcraft.listeners.PlayerEventListener;
import com.soul.catcraft.services.ChatService;
import com.soul.catcraft.services.PlayerService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.soul.catcraft.emoji.EmojiLibrary;

import static com.soul.catcraft.ConfigFile.*;

public class CatCraft extends JavaPlugin {
//    public PlayerHandler playerHandler;
//    public Debugger debugger;
//    public ConfigFile configFile;
//    public InputHandler input;
//    public FileData data;
//    public Logger log;
//    public static EmojiLibrary emojiLibrary;
//
//    public static Plugin plugin;
//
//    public CatCraft() {
//    }
//
//    public static Plugin getPlugin() {
//        return plugin;
//    }
//
//    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
//        boolean isPlayer = sender instanceof Player;
//
//        if (!isValidCommand(cmd)) {
//            return false;
//        }
//
//        if (args.length < 1) {
//            sender.sendMessage(
//                    "Syntax error, at least one argument is needed. Use '/catcraft /cc or /ccw help' for a detailed command description.");
//            return false;
//        }
//
//        if (isSpecialCommand(sender, cmd, args)) {
//            return true;
//        }
//
//        if (isPlayer && (!sender.isOp() && SHOULD_BE_OP)) {
//            sender.sendMessage(
//                    "You are not permitted to use the catcraft plugin commands. Ask an administrator for help.");
//            return false;
//        }
//
//        String target = args.length > 1 ? args[1] : null;
//        logCommand(sender, cmd, target);
//
//        switch (args[0].toLowerCase()) {
//            case "disarm":
//                handleDisarm(sender, target);
//                break;
//            case "inv":
//                handleOpenInventory(sender, target);
//                break;
//            case "msgall":
//                handleMsgAll(sender, args);
//                break;
//            case "ender":
//                handleOpenEnderInventory(sender, target);
//                break;
//            case "reload":
//                handleReload(sender);
//                break;
//            case "help":
//                Commands.c.help(sender);
//                break;
//            case "credits":
//                Commands.c.credits(sender);
//                break;
//            case "rules":
//                Commands.c.rules(sender);
//                break;
//            default:
//                return false;
//        }
//
//        return true;
//    }
//
//    private boolean isValidCommand(Command cmd) {
//        String commandName = cmd.getName().toLowerCase();
//        return commandName.equals("catcraft") || commandName.equals("cc") || commandName.equals("ccw")
//                || commandName.equals("anon");
//    }
//
//    private boolean isSpecialCommand(CommandSender sender, Command cmd, String[] args) {
//        if (cmd.getName().equalsIgnoreCase("ccw")) {
//            String target = args[0];
//            if (target == null) {
//                sender.sendMessage(
//                        "[CatCraft]: You must specify a target when using /ccw. Example: /ccw <player> <message>");
//            } else {
//                Commands.c.sendMessage(this.playerHandler.getPlayer(target), sender, args);
//            }
//            return true;
//        } else if (cmd.getName().equalsIgnoreCase("anon")) {
//            if (sender instanceof Player) {
//                Commands.c.sendMessageToAll(sender, args);
//            }
//            return true;
//        }
//        return false;
//    }
//
//    private void logCommand(CommandSender sender, Command cmd, String target) {
//        if (VERBOSE) {
//            this.debugger.info(
//                    sender.getName() + " ==CatCraft==> " + cmd.getName() + (target != null ? (" on " + target) : ""));
//        }
//    }
//
//    private void handleDisarm(CommandSender sender, String target) {
//        if (target == null) {
//            return;
//        }
//        Commands.c.disarm(sender, this.playerHandler.getPlayer(target), SHOULD_DISARM_MAINHAND);
//        if (VERBOSE) {
//            this.debugger.info(sender.getName() + " ==DISARMS==> " + target);
//        }
//    }
//
//    private void handleOpenInventory(CommandSender sender, String target) {
//        if (!(sender instanceof Player) || target == null) {
//            return;
//        }
//        Commands.c.openInventory((Player) sender, this.playerHandler.getPlayer(target));
//        if (VERBOSE) {
//            this.debugger.info(sender.getName() + " ==PEEKS-INTO-INVENTORY==> " + target);
//        }
//    }
//
//    private void handleMsgAll(CommandSender sender, String[] args) {
//        String msg = Commands.c.sendAnonMessageToAll(args);
//        if (VERBOSE) {
//            if (sender instanceof Player p) {
//                p.sendMessage(p.getName() + " ==ALL==> " + msg);
//            }
//            this.debugger.info("Message sent to everyone.");
//        }
//    }
//
//    private void handleOpenEnderInventory(CommandSender sender, String target) {
//        if (!(sender instanceof Player) || target == null) {
//            return;
//        }
//        Commands.c.openEnderInventory((Player) sender, this.playerHandler.getPlayer(target));
//        if (VERBOSE) {
//            this.debugger.info(sender.getName() + " ==PEEKS-INTO-ENDER=CHEST==> " + target);
//        }
//    }
//
//    private void handleReload(CommandSender sender) {
//        if (!(sender instanceof Player)) {
//            this.configFile.reload();
//        }
//    }
//
//    public void init() {
//        Commands.init(this);
//        if (this.log == null) {
//            this.log = new Logger();
//        }
//        this.log.init();
//
//        if (this.debugger == null) {
//            this.debugger = new Debugger(this);
//        }
//        this.debugger.init();
//
//        if (this.configFile == null) {
//            this.configFile = new ConfigFile(this);
//        }
//        this.configFile.init();
//
//        if (this.data == null) {
//            this.data = new FileData(this);
//        }
//        this.data.init();
//
//        if (this.playerHandler == null) {
//            this.playerHandler = new PlayerHandler(this);
//        }
//        this.playerHandler.init();
//
//        if (this.input == null) {
//            this.input = new InputHandler(this, debugger);
//        }
//        this.input.init();
//    }
//
//    public void onLoad() {
//
//        getLogger().info("                    ");
//        getLogger().info("                      " + this.getDescription().getVersion() + " ");
//        getLogger().info("   _  _            Successfully invoked! ");
//        getLogger().info(" _(_)(_)_\t\t   ");
//        getLogger().info("(_).--.(_)          No cats were harmed      ");
//        getLogger().info("  /    \\         while testing this plugin      ");
//        getLogger().info("  \\    /  _  _  ");
//        getLogger().info("   '--' _(_)(_)_      Contact Author via:");
//        getLogger().info("       (_).--.(_)     - Discord: soul.wax");
//        getLogger().info("         /    \\ ");
//        getLogger().info("         \\    / ");
//        getLogger().info("          '--'   ");
//    }
//
//    public void onEnable() {
//        this.init();
//    }
//
//    public void onDisable() {
//        this.debugger.info("---   onDisable has been invoked!   ---");
//    }
//}


    private void setupCommands() {
        BukkitCommandManager commandManager = new BukkitCommandManager(this);

        // Register command classes
        commandManager.register(injector.getInstance(CatCraftCommands.class));
        commandManager.register(injector.getInstance(PlayerCommands.class));
        commandManager.register(injector.getInstance(AdminCommands.class));

        // Register context resolvers
        commandManager.registerContextResolver(Player.class, context -> {
            String name = context.pop();
            Player player = Bukkit.getPlayer(name);
            if (player == null) {
                throw new CommandException("Player '" + name + "' not found or offline");
            }
            return player;
        });

        // Register parameter validators
        commandManager.registerParameterValidator(Player.class, (parameter, argument, actor) -> {
            if (!actor.hasPermission("catcraft.admin")) {
                throw new CommandException("You don't have permission to target other players");
            }
        });

        // Register exception handlers
        commandManager.registerExceptionHandler(CommandException.class, (actor, exception) -> {
            actor.reply(ChatColor.RED + exception.getMessage());
        });
    }

    // commands/AdminCommands.java
    @Command("catcraft admin")
    public class AdminCommands {
        private final PlayerService playerService;
        private final GuiService guiService;
        private final ConfigService configService;

        @Inject
        public AdminCommands(PlayerService playerService, GuiService guiService, ConfigService configService) {
            this.playerService = playerService;
            this.guiService = guiService;
            this.configService = configService;
        }

        @Subcommand("panel")
        @Permission("catcraft.admin")
        public void openAdminPanel(Player sender) {
            guiService.openAdminPanel(sender);
        }

        @Subcommand("ban")
        @Permission("catcraft.admin.ban")
        public void banPlayer(Player sender, @Named("target") String targetName,
                              @Optional @Named("duration") String duration,
                              @Optional @Named("reason") String reason) {

            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            if (target == null || !target.hasPlayedBefore()) {
                sender.sendMessage(text("Player not found!", RED));
                return;
            }

            long durationMs = parseDuration(duration);
            String banReason = reason != null ? reason : "Banned by admin";

            playerService.banPlayer(target.getUniqueId().toString(), banReason, durationMs)
                    .thenRun(() -> {
                        sender.sendMessage(text("Player banned successfully!", GREEN));

                        // Kick if online
                        Player onlineTarget = target.getPlayer();
                        if (onlineTarget != null && onlineTarget.isOnline()) {
                            onlineTarget.kick(text("You have been banned: " + banReason, RED));
                        }
                    });
        }

        @Subcommand("unban")
        @Permission("catcraft.admin.ban")
        public void unbanPlayer(Player sender, @Named("target") String targetName) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            if (target == null) {
                sender.sendMessage(text("Player not found!", RED));
                return;
            }

            playerService.unbanPlayer(target.getUniqueId().toString())
                    .thenRun(() -> sender.sendMessage(text("Player unbanned successfully!", GREEN)));
        }

        @Subcommand("stats")
        @Permission("catcraft.admin.stats")
        public void viewServerStats(CommandSender sender) {
            playerService.getPlayerData("server-stats").thenAccept(data -> {
                sender.sendMessage(text("=== Server Statistics ===", GOLD));
                sender.sendMessage(text("Total Players: " + data.getTotalPlaytime(), GRAY));
                sender.sendMessage(text("Active Today: " + data.getDisplayName(), GRAY));
                // Add more statistics...
            });
        }

        private long parseDuration(String duration) {
            if (duration == null) return 0; // Permanent ban

            try {
                if (duration.endsWith("d")) {
                    return Long.parseLong(duration.substring(0, duration.length() - 1)) * 24 * 60 * 60 * 1000;
                } else if (duration.endsWith("h")) {
                    return Long.parseLong(duration.substring(0, duration.length() - 1)) * 60 * 60 * 1000;
                } else if (duration.endsWith("m")) {
                    return Long.parseLong(duration.substring(0, duration.length() - 1)) * 60 * 1000;
                }
            } catch (NumberFormatException e) {
                // Invalid format, default to permanent
            }
            return 0;
        }
    }

    // commands/PlayerCommands.java
    @Command("catcraft player")
    public class PlayerCommands {
        private final PlayerService playerService;
        private final ChatService chatService;

        @Inject
        public PlayerCommands(PlayerService playerService, ChatService chatService) {
            this.playerService = playerService;
            this.chatService = chatService;
        }

        @Subcommand("stats")
        @Permission("catcraft.player.stats")
        public void viewStats(Player sender, @Optional @Named("target") Player target) {
            Player playerToView = target != null ? target : sender;

            if (target != null && !sender.hasPermission("catcraft.admin")) {
                sender.sendMessage(text("You can only view your own stats!", RED));
                return;
            }

            playerService.getPlayerData(playerToView.getUniqueId().toString())
                    .thenAccept(data -> {
                        if (data == null) {
                            sender.sendMessage(text("Player data not found!", RED));
                            return;
                        }

                        sender.sendMessage(text("=== Player Statistics ===", GOLD));
                        sender.sendMessage(text("Name: " + data.getDisplayName(), GRAY));
                        sender.sendMessage(text("First Join: " + formatDate(data.getFirstJoin()), GRAY));
                        sender.sendMessage(text("Last Seen: " + formatDate(data.getLastSeen()), GRAY));
                        sender.sendMessage(text("Playtime: " + formatPlaytime(data.getTotalPlaytime()), GRAY));
                    });
        }

        @Subcommand("ignore")
        @Permission("catcraft.player.ignore")
        public void ignorePlayer(Player sender, @Named("target") Player target) {
            if (sender.equals(target)) {
                sender.sendMessage(text("You cannot ignore yourself!", RED));
                return;
            }

            playerService.setPlayerIgnored(sender, target, true)
                    .thenRun(() -> sender.sendMessage(text("You are now ignoring " + target.getName(), GREEN)));
        }

        @Subcommand("unignore")
        @Permission("catcraft.player.ignore")
        public void unignorePlayer(Player sender, @Named("target") Player target) {
            playerService.setPlayerIgnored(sender, target, false)
                    .thenRun(() -> sender.sendMessage(text("You are no longer ignoring " + target.getName(), GREEN)));
        }
    }
}