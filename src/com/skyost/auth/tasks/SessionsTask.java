package com.skyost.auth.tasks;

import org.bukkit.entity.Player;

import com.skyost.auth.AuthPlugin;

public class SessionsTask implements Runnable {
	
	private Player player;
	
	public SessionsTask(final Player Player) {
		this.player = Player;
	}

	@Override
	public void run() {
		if(AuthPlugin.sessions.get(player.getName()) != null) {
			AuthPlugin.sessions.remove(player.getName());
		}
		if(player.isOnline()) {
			player.sendMessage(AuthPlugin.messages.Messages_8);
		}
	}

}
