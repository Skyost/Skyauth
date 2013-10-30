package com.skyost.auth;

import java.io.File;

import org.bukkit.plugin.Plugin;

import com.skyost.auth.utils.Config;

public class MessagesFile extends Config {
	public MessagesFile(Plugin plugin) {
		CONFIG_FILE = new File(plugin.getDataFolder(), "messages.yml");
		CONFIG_HEADER = "Skyauth Messages";
	}
	
	public String Messages_1 = "§4You must be logged in / registred !";
	public String Messages_2 = "§4You are already logged in !";
	public String Messages_3 = "§4Incorrect password !";
	public String Messages_4 = "§2You have been logged in !";
	public String Messages_5 = "§4You have already an account !";
	public String Messages_6 = "§4The confirmation is incorrect !";
	public String Messages_7 = "§2Success ! You can now log in to this server with /login <password>. An unique code has been generated, it will be used if you want to change your password : /code/.";
	public String Messages_8 = "§6You session has expired, log in with /login <password>.";
	public String Messages_9 = "§4This code is incorrect.";
	public String Messages_10 = "§2Success ! Your password has been changed ! If you were logged in, you have been disconnected, login you back with /login <password>.";
	public String Messages_11 = "Max trials reached !";
	public String Messages_12 = "§4You must be registered on this server !";
	public String Messages_13 = "§6Welcome /player/ ! Please register to play on this server. /register <password> <confirm>";
	public String Messages_14 = "§6Welcome back /player/ ! Please log in to play on this server. /login <password>";
	public String Messages_15 = "§4/player/ has reached the max trials allowed !";
	public String Messages_16 = "§4Sorry, you have reached your max trials ! Please wait a moment.";
	public String Messages_17 = "§2You can retry to connect you now.";
	
}
