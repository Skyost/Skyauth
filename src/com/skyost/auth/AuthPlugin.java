package com.skyost.auth;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.skyost.auth.listeners.CommandsExecutor;
import com.skyost.auth.listeners.EventsListener;
import com.skyost.auth.utils.Metrics;
import com.skyost.auth.utils.Updater;

public class AuthPlugin extends JavaPlugin {
	
	public static ConfigFile config;
	public static MessagesFile messages;
	
	public static final HashMap<String, String> sessions = new HashMap<String, String>();
	public static final HashMap<String, Integer> tried = new HashMap<String, Integer>();
	
	@Override
	public void onEnable() {
		try {
			init();
			startMetrics();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private final void init() throws Exception {
		config = new ConfigFile(this);
		config.init();
		messages = new MessagesFile(this);
		messages.init();
		config.save();
		Bukkit.getPluginManager().registerEvents(new EventsListener(), this);
		if(config.CheckForUpdates) {
			new Updater(this, 65625, this.getFile(), Updater.UpdateType.DEFAULT, true);
		}
		if(config.SessionLength <= 0) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Skyauth] SessionLength must be positive !");
		}
		if(config.ForgiveDelay <= 0) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Skyauth] ForgiveDelay must be positive !");
		}
		CommandExecutor executor = new CommandsExecutor();
		this.getCommand("login").setExecutor(executor);
		this.getCommand("register").setExecutor(executor);
		this.getCommand("change").setExecutor(executor);
	}
	
	private final void startMetrics() throws Exception {
		Metrics metrics = new Metrics(this);
    	metrics.createGraph("EncryptGraph").addPlotter(new Metrics.Plotter("Encrypting password") {	
    			
    		@Override
    		public int getValue() {	
    			return 1;
    		}
    			
    		@Override
    		public String getColumnName() {
    			if(config.EncryptPassword) {
    				return "Yes";
    			}
    			else {
    				return "No";
    			}
    		}
    			
    	});
    	metrics.start();
	}
	
	public static final boolean isLogged(final Player player) {
		if(sessions.get(player.getName()) != null) {
			return true;
		}
		return false;
	}
}
