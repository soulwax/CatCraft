package com.soul.catcraft.listeners;

public class PlayerEventListener implements Listener {
    private final PlayerService playerService;
    private final ChatService chatService;

    @Inject
    public PlayerEventListener(PlayerService playerService, ChatService chatService) {
        this.playerService = playerService;
        this.chatService = chatService;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerService.handlePlayerJoin(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true); // Cancel default handling
        chatService.processPlayerMessage(event.getPlayer(), event.getMessage());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        playerService.handleCatProtection(event);
    }
}