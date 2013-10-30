package com.skyost.auth.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.skyost.auth.AuthPlugin;
import com.skyost.auth.tasks.SessionsTask;
import com.skyost.auth.tasks.ForgiveTask;
import com.skyost.auth.utils.PasswordUtils;

public class CommandsExecutor implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = null;
		String playername = null;
		if(sender instanceof Player) {
			player = (Player)sender;
			playername = player.getName();
			if(AuthPlugin.tried.get(playername) != null && AuthPlugin.tried.get(playername) >= AuthPlugin.config.MaxTry) {
				player.sendMessage(AuthPlugin.messages.Messages_16);
				return true;
			}
		}
		else {
			sender.sendMessage(ChatColor.RED + "[Skyauth] Please do this from the game !");
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("login")) {
			if(args.length == 1) {
				if(!AuthPlugin.isLogged(player)) {
					if(AuthPlugin.config.Accounts.get(playername) != null) {
						String truepassword = AuthPlugin.config.Accounts.get(playername);
						if(AuthPlugin.config.EncryptPassword) {
							truepassword = PasswordUtils.decrypt(truepassword);
						}
						if(truepassword.equals(args[0])) {
							AuthPlugin.sessions.put(playername, player.getAddress().getHostString());
							player.sendMessage(AuthPlugin.messages.Messages_4);
							Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("Skyauth"), new SessionsTask(player), AuthPlugin.config.SessionLength * 20);
						}
						else {
							if(AuthPlugin.tried.get(playername) == null) {
								AuthPlugin.tried.put(playername, 1);
							}
							else {
								AuthPlugin.tried.put(playername, AuthPlugin.tried.get(playername) + 1);
								if(AuthPlugin.tried.get(playername) == AuthPlugin.config.MaxTry) {
									Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("Skyauth"), new ForgiveTask(player), AuthPlugin.config.ForgiveDelay * 20);
									player.kickPlayer(AuthPlugin.messages.Messages_11);
									for(Player online : Bukkit.getOnlinePlayers()) {
										if(online.isOp()) {
											online.sendMessage(AuthPlugin.messages.Messages_15.replaceAll("/player/", playername));
										}
									}
								}
							}
							player.sendMessage(AuthPlugin.messages.Messages_3);
						}
					}
					else {
						player.sendMessage(AuthPlugin.messages.Messages_12);
					}
				}
				else {
					player.sendMessage(AuthPlugin.messages.Messages_2);
				}
			}
			else {
				return false;
			}
		}
		else if(cmd.getName().equalsIgnoreCase("register")) {
			try {
				if(args.length == 2) {
					if(!AuthPlugin.isLogged(player)) {
						if(AuthPlugin.config.Accounts.get(playername) == null) {
							if(args[0].equals(args[1])) {
								if(AuthPlugin.config.EncryptPassword) {
									AuthPlugin.config.Accounts.put(playername, PasswordUtils.encrypt(args[0]));
								}
								else {
									AuthPlugin.config.Accounts.put(playername, args[0]);
								}
								int code = new Random().nextInt(99999999);
								AuthPlugin.config.Codes.put(playername, code);
								AuthPlugin.config.save();
								String message = AuthPlugin.messages.Messages_7.replaceAll("/code/", "" + code);
								player.sendMessage(message);
							}
							else {
								player.sendMessage(AuthPlugin.messages.Messages_6);
							}
						}
						else {
							player.sendMessage(AuthPlugin.messages.Messages_5);
						}
					}
					else {
						player.sendMessage(AuthPlugin.messages.Messages_2);
					}
				}
				else {
					return false;
				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		else if(cmd.getName().equalsIgnoreCase("change")) {
			try {
				if(args.length == 3) {
					if(AuthPlugin.config.Accounts.get(playername) != null) {
						if(args[1].equals(args[2])) {
							if(AuthPlugin.config.Codes.get(playername) == Integer.parseInt(args[0])) {
								if(AuthPlugin.config.EncryptPassword) {
									AuthPlugin.config.Accounts.put(playername, PasswordUtils.encrypt(args[1]));
								}
								else {
									AuthPlugin.config.Accounts.put(playername, args[1]);
								}
								AuthPlugin.sessions.remove(playername);
								AuthPlugin.config.save();
								player.sendMessage(AuthPlugin.messages.Messages_10);
							}
							else {
								player.sendMessage(AuthPlugin.messages.Messages_9);
							}
						}
						else {
							player.sendMessage(AuthPlugin.messages.Messages_6);
						}
					}
					else {
						player.sendMessage(AuthPlugin.messages.Messages_12);
					}
				}
				else {
					return false;
				}
			}
			catch(Exception ex) {
				player.sendMessage(AuthPlugin.messages.Messages_9);
			}
		}
		return true;
	}

}
