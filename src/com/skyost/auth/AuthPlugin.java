package com.skyost.auth;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.skyost.auth.listeners.CommandsExecutor;
import com.skyost.auth.listeners.EventsListener;
import com.skyost.auth.tasks.SkyauthTasks;
import com.skyost.auth.utils.Metrics;
import com.skyost.auth.utils.Updater;

public class AuthPlugin extends JavaPlugin {
	
	public static ConfigFile config;
	public static MessagesFile messages;
	
	public static HashMap<String, ArrayList<String>> data;
	private static Statement stat;
	
	public static final HashMap<String, String> sessions = new HashMap<String, String>();
	public static final HashMap<String, Integer> tried = new HashMap<String, Integer>();
	
	@Override
	public final void onEnable() {
		try {
			init();
			startMetrics();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public final void onDisable() {
		try {
			if(config.MySQL_Use) {
				ResultSet rs = stat.executeQuery("SELECT Account, Password, Code FROM Skyauth_Data");
				ArrayList<String> arrayData = new ArrayList<String>();
				while(rs.next()) {
					arrayData.add(0, rs.getString("Password"));
					arrayData.add(1, rs.getString("Code"));
					AuthPlugin.data.put(rs.getString("Account"), arrayData);
				}
				for(Entry<String, ArrayList<String>> entry : AuthPlugin.data.entrySet()) {
					stat.execute("INSERT INTO Skyauth_Data(User, Password, Code) VALUES('" + entry.getKey() + "', '" + entry.getValue().get(0) + "', '" + entry.getValue().get(1) + "')");
				}
				stat.getConnection().close();
				stat.close();
			}
			else {
				AuthPlugin.data.putAll(AuthPlugin.config.Data);
				AuthPlugin.config.Data.putAll(AuthPlugin.data);
				AuthPlugin.config.save();
			}
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
		if(config.MySQL_Use) {
			data = new HashMap<String, ArrayList<String>>();
			stat = DriverManager.getConnection("jdbc:mysql://" + config.MySQL_Host + ":" + config.MySQL_Port + "/" + config.MySQL_Database, config.MySQL_Username, config.MySQL_Password).createStatement();
			stat.execute("CREATE TABLE IF NOT EXISTS Skyauth_Data(User TINYTEXT, Password TINYTEXT, Code NUMERIC(8))");
			ResultSet rs = stat.executeQuery("SELECT User, Password, Code FROM Skyauth_Data");
			ArrayList<String> arrayData = new ArrayList<String>();
			while(rs.next()) {
				arrayData.add(0, rs.getString("Password"));
				arrayData.add(1, rs.getString("Code"));
				data.put(rs.getString("User"), arrayData);
			}
		}
		else {
			data = new HashMap<String, ArrayList<String>>(config.Data);
		}
		CommandExecutor executor = new CommandsExecutor();
		this.getCommand("login").setExecutor(executor);
		this.getCommand("register").setExecutor(executor);
		this.getCommand("change").setExecutor(executor);
		int reloadDelay = config.ReloadDelay * 20;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new SkyauthTasks(stat), reloadDelay, reloadDelay);
	}
	
	private final void startMetrics() throws IOException {
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
