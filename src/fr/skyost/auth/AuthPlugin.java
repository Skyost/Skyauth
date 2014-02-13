package fr.skyost.auth;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import fr.skyost.auth.listeners.CommandsExecutor;
import fr.skyost.auth.listeners.EventsListener;
import fr.skyost.auth.tasks.SkyauthTasks;
import fr.skyost.auth.utils.Metrics;
import fr.skyost.auth.utils.Skyupdater;
import fr.skyost.auth.utils.Utils;

public class AuthPlugin extends JavaPlugin {
	
	public static ConfigFile config;
	public static MessagesFile messages;
	
	private static Statement stat;
	private static boolean useMySQL;
	
	public static final List<String> availableAlgorithms = Arrays.asList(new String[] {"PLAIN", "CHAR", "MD2", "MD5", "SHA-1", "SHA-256", "SHA-384", "SHA-512"});
	
	public static final HashMap<String, ArrayList<String>> data = new HashMap<String, ArrayList<String>>();
	public static final HashMap<String, ArrayList<String>> temp = new HashMap<String, ArrayList<String>>();
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
			reload();
			final ArrayList<String> arrayData = new ArrayList<String>();
			for(Player online : Bukkit.getOnlinePlayers()) {
				arrayData.add(0, Utils.LocationToString(online.getLocation()));
				arrayData.add(1, online.getGameMode().name());
				arrayData.add(2, Utils.InventoryToString(online.getInventory()));
				online.teleport(online.getWorld().getSpawnLocation());
				online.setGameMode(GameMode.CREATIVE);
				for(ItemStack ie : online.getInventory().getContents()) {
    				if(ie != null) {
    					online.getInventory().removeItem(ie);
    				}
    			}
				temp.put(online.getName(), arrayData);
			}
			for(Entry<String, ArrayList<String>> entry : temp.entrySet()) {
				config.Temp.put(entry.getKey(), entry.getValue());
			}
			config.save();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private final void init() throws Exception {
		final CommandSender console = Bukkit.getConsoleSender();
		config = new ConfigFile(this);
		config.init();
		messages = new MessagesFile(this);
		messages.init();
		MySQLFile mysql = new MySQLFile(this);
		mysql.init();
		useMySQL = mysql.MySQL_Use;
		if(config.CheckForUpdates) {
			new Skyupdater(this, 65625, this.getFile(), true, true);
		}
		if(config.SessionLength <= 0) {
			console.sendMessage(ChatColor.RED + "[Skyauth] SessionLength must be positive !");
			config.SessionLength = 7200;
		}
		if(config.ForgiveDelay <= 0) {
			console.sendMessage(ChatColor.RED + "[Skyauth] ForgiveDelay must be positive !");
			config.ForgiveDelay = 900;
		}
		if(config.MaxTry <= 0) {
			console.sendMessage(ChatColor.RED + "[Skyauth] MaxTry must be positive !");
			config.ForgiveDelay = 5;
		}
		if(config.ReloadDelay <= 0) {
			console.sendMessage(ChatColor.RED + "[Skyauth] ReloadDelay must be positive !");
			config.ForgiveDelay = 300;
		}
		if(!availableAlgorithms.contains(config.PasswordAlgorithm)) {
			console.sendMessage(ChatColor.RED + "[Skyauth] PasswordAlgorithm must be a valid algorithm :");
			for(String algo : availableAlgorithms) {
				console.sendMessage(ChatColor.RED + algo);
			}
			config.PasswordAlgorithm = "MD5";
		}
		if(useMySQL) {
			stat = DriverManager.getConnection("jdbc:mysql://" + mysql.MySQL_Host + ":" + mysql.MySQL_Port + "/" + mysql.MySQL_Database, mysql.MySQL_Username, mysql.MySQL_Password).createStatement();
			stat.execute("CREATE TABLE IF NOT EXISTS Skyauth_Data(User TINYTEXT, Password TINYTEXT, Code NUMERIC(8))");
		}
		for(Entry<String, ArrayList<String>> entry : config.Temp.entrySet()) {
			temp.put(entry.getKey(), entry.getValue());
		}
		config.Temp.clear();
		config.save();
		Bukkit.getPluginManager().registerEvents(new EventsListener(), this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new SkyauthTasks(), 0, config.ReloadDelay * 20);
		CommandExecutor executor = new CommandsExecutor();
		this.getCommand("login").setExecutor(executor);
		this.getCommand("logout").setExecutor(executor);
		this.getCommand("register").setExecutor(executor);
		this.getCommand("change").setExecutor(executor);
		this.getCommand("reload-skyauth").setExecutor(executor);
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
    			return config.PasswordAlgorithm;
    		}
    			
    	});
    	metrics.start();
	}
	
	public static void reload() throws Exception {
		if(useMySQL()) {
			final ArrayList<String> arrayData = new ArrayList<String>();
			ResultSet rs = stat.executeQuery("SELECT User, Password, Code FROM Skyauth_Data");
			while(rs.next()) {
				arrayData.add(0, rs.getString("Password"));
				arrayData.add(1, rs.getString("Code"));
				data.put(rs.getString("User"), arrayData);
			}
			stat.executeUpdate("TRUNCATE TABLE Skyauth_Data");
			for(Entry<String, ArrayList<String>> entry : data.entrySet()) {
				stat.executeUpdate("INSERT INTO Skyauth_Data(User, Password, Code) VALUES('" + entry.getKey() + "', '" + entry.getValue().get(0) + "', '" + entry.getValue().get(1) + "')");
			}
		}
		else {
			data.putAll(config.Data);
			config.Data = data;
			config.save();
		}
	}
	
	public static final boolean useMySQL() {
		return useMySQL;
	}
	
	public static final boolean isLogged(final Player player) {
		if(sessions.get(player.getName()) != null) {
			return true;
		}
		return false;
	}
}
