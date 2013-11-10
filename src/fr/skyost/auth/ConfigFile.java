package fr.skyost.auth;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.plugin.Plugin;

import fr.skyost.auth.utils.Config;

public class ConfigFile extends Config {
	
	public HashMap<String, ArrayList<String>> Data = new HashMap<String, ArrayList<String>>();
	public HashMap<String, ArrayList<String>> Temp = new HashMap<String, ArrayList<String>>();
	
	public int SessionLength = 7200;
	public int ForgiveDelay = 900;
	public int MaxTry = 5;
	
	public int ReloadDelay = 300;
	
	public boolean EncryptPassword = true;
	public boolean CheckForUpdates = true;
	
	public ConfigFile(Plugin plugin) {
		CONFIG_FILE = new File(plugin.getDataFolder(), "config.yml");
		CONFIG_HEADER = "##################################################### #";
		CONFIG_HEADER += "\n               Skyauth Configuration                  #";
		CONFIG_HEADER += "\n   See http://dev.bukkit.org/bukkit-plugins/skyauth   #";
		CONFIG_HEADER += "\n              for more informations.                  #";
		CONFIG_HEADER += "\n##################################################### #";
	}
	
}
