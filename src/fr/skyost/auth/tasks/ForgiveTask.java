package fr.skyost.auth.tasks;

import org.bukkit.entity.Player;

import fr.skyost.auth.AuthPlugin;

public class ForgiveTask implements Runnable {
	
	private Player player;
	
	public ForgiveTask(final Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		if(AuthPlugin.tried.get(player.getName()) != null) {
			AuthPlugin.tried.remove(player.getName());
		}
		if(player.isOnline()) {
			player.sendMessage(AuthPlugin.messages.Messages_17);
		}
	}

}
