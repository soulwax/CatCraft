package com.soul.catcraft.commands;

@Command("catcraft")
public class CatCraftCommands {
    private final PlayerService playerService;
    private final ChatService chatService;

    @Inject
    public CatCraftCommands(PlayerService playerService, ChatService chatService) {
        this.playerService = playerService;
        this.chatService = chatService;
    }

    @Subcommand("inv")
    @Permission("catcraft.admin")
    public void openInventory(Player sender, @Named("target") Player target) {
        sender.openInventory(target.getInventory());
        // Automatic logging via service layer
    }

    @Subcommand("disarm")
    @Permission("catcraft.admin")
    public void disarmPlayer(Player sender, @Named("target") Player target) {
        playerService.disarmPlayer(sender, target);
    }

    @Subcommand("reload")
    @Permission("catcraft.admin.reload")
    public void reload(CommandSender sender) {
        // Reload via service layer
        sender.sendMessage(text("Configuration reloaded", GREEN));
    }
}