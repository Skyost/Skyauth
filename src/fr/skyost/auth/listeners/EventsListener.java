package fr.skyost.auth.listeners;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.Location;
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
import org.bukkit.inventory.ItemStack;

import fr.skyost.auth.AuthPlugin;
import fr.skyost.auth.utils.Utils;

public class EventsListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public final void onPlayerCommandPreprocessEvent(final PlayerCommandPreprocessEvent event) {
		final Player player = event.getPlayer();
		if(!AuthPlugin.isLogged(player)) {
			if(!player.hasPermission("skyauth.bypass")) {
				String message = event.getMessage().toUpperCase();
				if(!(message.startsWith("/REGISTER") || message.startsWith("/LOGIN") || message.startsWith("/CHANGE"))) {
					player.sendMessage(AuthPlugin.messages.Messages_1);
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private final void onPlayerMoveEvent(final PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		if(!AuthPlugin.isLogged(player)) {
			if(!player.hasPermission("skyauth.bypass")) {
				final Location from = event.getFrom();
				final Location to = event.getTo();
				if(from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ() || from.getBlockY() != to.getBlockY()) {
					event.setTo(from);
					player.sendMessage(AuthPlugin.messages.Messages_1);
					return;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private final void onPlayerDropItem(final PlayerDropItemEvent event) {
		final Player player = event.getPlayer();
		if(!AuthPlugin.isLogged(player)) {
			if(!player.hasPermission("skyauth.bypass")) {
				player.sendMessage(AuthPlugin.messages.Messages_1);
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private final void onPlayerChat(final AsyncPlayerChatEvent event) {
		final Player player = event.getPlayer();
		if(!AuthPlugin.isLogged(player)) {
			if(!player.hasPermission("skyauth.bypass")) {
				player.sendMessage(AuthPlugin.messages.Messages_1);
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private final void onPlayerInteract(final PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if(!AuthPlugin.isLogged(player)) {
			if(!player.hasPermission("skyauth.bypass")) {
				player.sendMessage(AuthPlugin.messages.Messages_1);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private final void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		if(AuthPlugin.isLogged(player)) {
			if(!player.hasPermission("skyauth.bypass")) {
				if(!(player.getAddress().getHostString().equalsIgnoreCase(AuthPlugin.sessions.get(player.getName())))) {
					AuthPlugin.sessions.remove(player.getName());
					final ArrayList<String> arrayData = new ArrayList<String>();
					arrayData.add(0, Utils.LocationToString(player.getLocation()));
					arrayData.add(1, player.getGameMode().name());
					arrayData.add(2, Utils.InventoryToString(player.getInventory()));
					player.teleport(player.getWorld().getSpawnLocation());
					player.setGameMode(GameMode.CREATIVE);
					for(ItemStack ie : player.getInventory().getContents()) {
	    				if(ie != null) {
	    					player.getInventory().removeItem(ie);
	    				}
	    			}
					AuthPlugin.temp.put(player.getName(), arrayData);
				}
			}
		}
		else {
			if(!player.hasPermission("skyauth.bypass")) {
				String message;
				if(AuthPlugin.config.Data.get(player.getName()) != null) {
					message = AuthPlugin.messages.Messages_14.replaceAll("/player/", player.getName());
				}
				else {
					message = AuthPlugin.messages.Messages_13.replaceAll("/player/", player.getName());
				}
				final ArrayList<String> arrayData = new ArrayList<String>();
				arrayData.add(0, Utils.LocationToString(player.getLocation()));
				arrayData.add(1, player.getGameMode().name());
				arrayData.add(2, Utils.InventoryToString(player.getInventory()));
				player.teleport(player.getWorld().getSpawnLocation());
				player.setGameMode(GameMode.CREATIVE);
				for(ItemStack ie : player.getInventory().getContents()) {
    				if(ie != null) {
    					player.getInventory().removeItem(ie);
    				}
    			}
				AuthPlugin.temp.put(player.getName(), arrayData);
				player.sendMessage(message);
			}
		}
	}
	
}
