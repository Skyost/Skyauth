package com.skyost.auth.tasks;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.skyost.auth.AuthPlugin;
import com.skyost.auth.utils.Updater;

public class UpdateTask implements Runnable {
	
	AuthPlugin auth;
	
	public UpdateTask(AuthPlugin auth) {
		this.auth = auth;
	}
	
	@SuppressWarnings("incomplete-switch")
	@Override
	public void run() {
		Updater updater = new Updater(auth, 65625, new File(auth.config.PluginFile), Updater.UpdateType.DEFAULT, true);
		Updater.UpdateResult result = updater.getResult();
       	Player[] ops = Bukkit.getServer().getOnlinePlayers();
	    switch(result) {
	    case SUCCESS:
	    	for(int i = 0; i < ops.length; i++) {
				if(ops[i].isOp()) {
					ops[i].sendMessage(ChatColor.GREEN + "[Skyauth] Update found: The update " + updater.getLatestName() + " has been downloaded, so you just have to do a simple reload.");
				}
			}
	       	break;
	    case FAIL_DBO:
	        for(int i = 0; i < ops.length; i++) {
				if(ops[i].isOp()) {
						ops[i].sendMessage(ChatColor.RED + "[Skyauth] Download Failed: The updater found an update, but was unable to download it.");
				}
			}
	        break;
	    case FAIL_DOWNLOAD:
	    	for(int i = 0; i < ops.length; i++) {
	    		if(ops[i].isOp()) {
					ops[i].sendMessage(ChatColor.GREEN + "[Skyauth] Update found: There was an update found : " + updater.getLatestName() + ".");
				}
			}
	        break;
	    }
    }

}
