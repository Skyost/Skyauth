package fr.skyost.auth.tasks;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.skyost.auth.AuthPlugin;
import fr.skyost.auth.utils.Utils;

public class SessionsTask implements Runnable {
	
	private Player player;
	
	public SessionsTask(final Player Player) {
		this.player = Player;
	}

	@Override
	public void run() {
		if(AuthPlugin.sessions.get(player.getName()) != null) {
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
		if(player.isOnline()) {
			player.sendMessage(AuthPlugin.messages.Messages_8);
		}
	}

}
