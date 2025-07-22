// File: src/main/java/com/soul/catcraft/services/ChatService.java

package com.soul.catcraft.services;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import java.util.concurrent.CompletableFuture;

public interface ChatService {
    void processPlayerMessage(Player player, String message);
    Component createChatMessage(Player player, String message);
    Component createWelcomeMessage(Player player);
    Component createNewPlayerAnnouncement(Player player);
    Component createNameChangeMessage(String oldName, String newName);
    Component createErrorMessage(String message);
    Component createSuccessMessage(String message);
    Component createWarningMessage(String message);
    CompletableFuture<Void> sendPrivateMessage(Player sender, Player recipient, String message);
    CompletableFuture<Void> broadcastMessage(Component message, String permission);
}