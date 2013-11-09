package com.skyost.auth.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.skyost.auth.AuthPlugin;

public class EventsListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public static final void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		if(!AuthPlugin.isLogged(event.getPlayer())) {
			if(!event.getPlayer().hasPermission("skyauth.bypass")) {
				String message = event.getMessage().toUpperCase();
				if(!(message.startsWith("/REGISTER") || message.startsWith("/LOGIN") || message.startsWith("/CHANGE"))) {
					event.getPlayer().sendMessage(AuthPlugin.messages.Messages_1);
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private static final  void onPlayerMoveEvent(PlayerMoveEvent event) {
		if(!AuthPlugin.isLogged(event.getPlayer())) {
			if(!event.getPlayer().hasPermission("skyauth.bypass")) {
				if(event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
					event.setTo(event.getFrom());
					event.getPlayer().sendMessage(AuthPlugin.messages.Messages_1);
					return;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private static final  void onPlayerDropItem(PlayerDropItemEvent event) {
		if(!AuthPlugin.isLogged(event.getPlayer())) {
			if(!event.getPlayer().hasPermission("skyauth.bypass")) {
				event.getPlayer().sendMessage(AuthPlugin.messages.Messages_1);
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private static final  void onPlayerChat(AsyncPlayerChatEvent event) {
		if(!AuthPlugin.isLogged(event.getPlayer())) {
			if(!event.getPlayer().hasPermission("skyauth.bypass")) {
				event.getPlayer().sendMessage(AuthPlugin.messages.Messages_1);
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private static final  void onPlayerInteract(PlayerInteractEvent event) {
		if(!AuthPlugin.isLogged(event.getPlayer())) {
			if(!event.getPlayer().hasPermission("skyauth.bypass")) {
				event.getPlayer().sendMessage(AuthPlugin.messages.Messages_1);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private static final  void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if(AuthPlugin.isLogged(player)) {
			if(!event.getPlayer().hasPermission("skyauth.bypass")) {
				if(!(player.getAddress().getHostString().equalsIgnoreCase(AuthPlugin.sessions.get(player.getName())))) {
					AuthPlugin.sessions.remove(player.getName());
				}
			}
		}
		else {
			if(!event.getPlayer().hasPermission("skyauth.bypass")) {
				String message;
				if(AuthPlugin.config.Data.get(player.getName()) != null) {
					message = AuthPlugin.messages.Messages_14.replaceAll("/player/", event.getPlayer().getName());
				}
				else {
					message = AuthPlugin.messages.Messages_13.replaceAll("/player/", event.getPlayer().getName());
				}
				player.sendMessage(message);
			}
		}
	}
	
}