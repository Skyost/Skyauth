package com.skyost.auth.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.skyost.auth.AuthPlugin;

public class Listeners implements Listener {
	
	AuthPlugin auth;
	
	public Listeners(AuthPlugin Auth) {
		this.auth = Auth;
	}
	
	@EventHandler
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		if(!auth.isLogged(event.getPlayer())) {
			if(!event.getPlayer().hasPermission("skyauth.bypass")) {
				String message = event.getMessage().toUpperCase();
				if(!(message.startsWith("/REGISTER") || message.startsWith("/LOGIN") || message.startsWith("/CHANGE"))) {
					event.getPlayer().sendMessage(auth.messages.Messages_1);
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	private void onPlayerMoveEvent(PlayerMoveEvent event) {
		if(!auth.isLogged(event.getPlayer())) {
			if(!event.getPlayer().hasPermission("skyauth.bypass")) {
				if(event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
					event.setTo(event.getFrom());
					event.getPlayer().sendMessage(auth.messages.Messages_1);
					return;
				}
			}
		}
	}
	
	@EventHandler
	private void onPlayerDropItem(PlayerDropItemEvent event) {
		if(!auth.isLogged(event.getPlayer())) {
			if(!event.getPlayer().hasPermission("skyauth.bypass")) {
				event.getPlayer().sendMessage(auth.messages.Messages_1);
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	private void onPlayerChat(AsyncPlayerChatEvent event) {
		if(!auth.isLogged(event.getPlayer())) {
			if(!event.getPlayer().hasPermission("skyauth.bypass")) {
				event.getPlayer().sendMessage(auth.messages.Messages_1);
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event) {
		if(!auth.isLogged(event.getPlayer())) {
			if(!event.getPlayer().hasPermission("skyauth.bypass")) {
				event.getPlayer().sendMessage(auth.messages.Messages_1);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if(auth.isLogged(player)) {
			if(!event.getPlayer().hasPermission("skyauth.bypass")) {
				if(!(player.getAddress().getHostString().equalsIgnoreCase(auth.sessions.get(player.getName())))) {
					auth.sessions.remove(player.getName());
				}
			}
		}
		else {
			String message;
			if(auth.config.Accounts.get(player.getName()) != null) {
				message = auth.messages.Messages_14.replaceAll("/player/", event.getPlayer().getName());
				player.sendMessage(message);
			}
			else {
				message = auth.messages.Messages_13.replaceAll("/player/", event.getPlayer().getName());
				player.sendMessage(message);
			}
		}
	}
}
