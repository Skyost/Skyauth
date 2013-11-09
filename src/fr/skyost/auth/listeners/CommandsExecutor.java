package fr.skyost.auth.listeners;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.skyost.auth.AuthPlugin;
import fr.skyost.auth.tasks.ForgiveTask;
import fr.skyost.auth.tasks.SessionsTask;
import fr.skyost.auth.utils.Utils;

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
					if(AuthPlugin.data.get(playername) != null) {
						String truepassword = AuthPlugin.data.get(playername).get(0);
						if(AuthPlugin.config.EncryptPassword) {
							truepassword = Utils.passworder(truepassword);
						}
						if(truepassword.equals(args[0])) {
							AuthPlugin.sessions.put(playername, player.getAddress().getHostString());
							player.sendMessage(AuthPlugin.messages.Messages_4);
							player.teleport(Utils.StringToLocation(AuthPlugin.temp.get(playername).get(0)));
							player.setGameMode(GameMode.valueOf(AuthPlugin.temp.get(playername).get(1)));
							Inventory inv = Utils.StringToInventory(AuthPlugin.temp.get(playername).get(2));
							for(ItemStack ie : player.getInventory().getContents()) {
			    				if(ie != null) {
			    					player.getInventory().removeItem(ie);
			    				}
			    			}
			    			for(ItemStack ie : inv.getContents()) {
			    				if(ie != null) {
			    					player.getInventory().addItem(ie);
			    				}
			    			}
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
						if(AuthPlugin.data.get(playername) == null) {
							if(args[0].equals(args[1])) {
								ArrayList<String> arrayData = new ArrayList<String>();
								if(AuthPlugin.config.EncryptPassword) {
									arrayData.add(0, Utils.passworder(args[0]));
								}
								else {
									arrayData.add(0, args[0]);
								}
								int code = new Random().nextInt(99999999);
								arrayData.add(1, String.valueOf(code));
								AuthPlugin.data.put(playername, arrayData);
								String message = AuthPlugin.messages.Messages_7.replaceAll("/code/", String.valueOf(code));
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
					if(AuthPlugin.data.get(playername) != null) {
						if(args[1].equals(args[2])) {
							if(AuthPlugin.data.get(playername).get(1) == args[0]) {
								if(AuthPlugin.config.EncryptPassword) {
									AuthPlugin.data.get(playername).set(1, Utils.passworder(args[1]));
								}
								else {
									AuthPlugin.data.get(playername).set(1, args[1]);
								}
								AuthPlugin.sessions.remove(playername);
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
		else if(cmd.getName().equalsIgnoreCase("reload-skyauth")) {
			try {
				AuthPlugin.reload(sender);
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return true;
	}

}
