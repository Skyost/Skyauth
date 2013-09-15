package com.skyost.auth.tasks;

import org.bukkit.entity.Player;

import com.skyost.auth.AuthPlugin;

public class SessionsTask implements Runnable {
	
	private AuthPlugin auth;
	private Player player;
	
	public SessionsTask(AuthPlugin Auth, Player Player) {
		this.auth = Auth;
		this.player = Player;
	}

	@Override
	public void run() {
		if(auth.sessions.get(player.getName()) != null) {
			auth.sessions.remove(player.getName());
		}
		if(player.isOnline()) {
			player.sendMessage(auth.messages.Messages_8);
		}
	}

}
