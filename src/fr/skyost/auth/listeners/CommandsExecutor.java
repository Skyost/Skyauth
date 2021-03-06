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
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
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
		try {
			if(cmd.getName().equalsIgnoreCase("login")) {
				if(args.length == 1) {
					if(AuthPlugin.data.get(playername) != null) {
						if(!AuthPlugin.isLogged(player)) {
							if(Utils.isCorrect(args[0], AuthPlugin.data.get(playername).get(0))) {
								AuthPlugin.sessions.put(playername, player.getAddress().getHostString());
								player.sendMessage(AuthPlugin.messages.Messages_4);
								if(AuthPlugin.temp.get(playername) != null) {
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
						   			AuthPlugin.temp.remove(playername);
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
							player.sendMessage(AuthPlugin.messages.Messages_2);
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
			else if(cmd.getName().equalsIgnoreCase("logout")) {
				if(AuthPlugin.data.get(playername) != null) {
					if(AuthPlugin.isLogged(player)) {
						AuthPlugin.sessions.remove(player.getName());
						final ArrayList<String> arrayData = new ArrayList<String>();
						arrayData.add(0, Utils.LocationToString(player.getLocation()));
						arrayData.add(1, player.getGameMode().name());
						arrayData.add(2, Utils.InventoryToString(player.getInventory()));
						player.teleport(player.getWorld().getSpawnLocation());
						player.setGameMode(GameMode.CREATIVE);
						for(ItemStack ie : player.getInventory().getContents()) {
							if(ie != null) {
								player.getInventory().removeItem(ie);
							}
						}
						AuthPlugin.temp.put(player.getName(), arrayData);
						player.sendMessage(AuthPlugin.messages.Messages_18);
					}
					else {
						player.sendMessage(AuthPlugin.messages.Messages_1);
					}
				}
				else {
					player.sendMessage(AuthPlugin.messages.Messages_12);
				}
			}
			else if(cmd.getName().equalsIgnoreCase("register")) {
					if(args.length == 2) {
						if(AuthPlugin.data.get(playername) == null) {
							if(!AuthPlugin.isLogged(player)) {
								if(args[0].equals(args[1])) {
									ArrayList<String> arrayData = new ArrayList<String>();
									arrayData.add(0, Utils.encrypt(args[0]));
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
								player.sendMessage(AuthPlugin.messages.Messages_2);
							}
						}
						else {
							player.sendMessage(AuthPlugin.messages.Messages_5);
						}
					}
					else {
						return false;
					}
			}
			else if(cmd.getName().equalsIgnoreCase("change")) {
				if(args.length == 3) {
					if(AuthPlugin.data.get(playername) != null) {
						if(args[1].equals(args[2])) {
							if(AuthPlugin.data.get(playername).get(1).equals(args[0])) {
								AuthPlugin.data.get(playername).set(0, Utils.encrypt(args[1]));
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
			else if(cmd.getName().equalsIgnoreCase("reload-skyauth")) {
				try {
					sender.sendMessage(ChatColor.GREEN + "Reloading Skyauth...");
					AuthPlugin.reload();
					sender.sendMessage(ChatColor.GREEN + "Done !");
					
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return true;
	}

}
