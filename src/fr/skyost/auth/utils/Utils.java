package fr.skyost.auth.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.skyost.auth.AuthPlugin;

@SuppressWarnings("deprecation")
public class Utils {
	
	public static final boolean isCorrect(final String password, final String truePassword) throws NoSuchAlgorithmException {
		String blankPassword = "";
		final String algorithm = AuthPlugin.config.PasswordAlgorithm.toUpperCase();
		switch(algorithm) {
		case "CHAR":
			for(int i = 0; i < password.length(); i++)  {
				int c = password.charAt(i) ^ 48;  
				blankPassword += (char)c; 
			}
			break;
		case "PLAIN":
			blankPassword = password;
			break;
		case "MD2":
		case "MD5":
		case "SHA-1":
		case "SHA-256":
		case "SHA-384":
		case "SHA-512":
			MessageDigest md = MessageDigest.getInstance(algorithm);
			byte[] array = md.digest(password.getBytes());
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
			}
			blankPassword = sb.toString();
			break;
		}
		if(blankPassword.equals(truePassword)) {
			return true;
		}
		return false;
	}
	
	public static final String encrypt(final String string) throws NoSuchAlgorithmException {
		String blankString = "";
		final String algorithm = AuthPlugin.config.PasswordAlgorithm.toUpperCase();
		switch(algorithm) {
		case "CHAR":
			for(int i = 0; i < string.length(); i++)  {
				int c = string.charAt(i) ^ 48;  
				blankString += (char)c; 
			}
			break;
		case "PLAIN":
			blankString = string;
			break;
		case "MD2":
		case "MD5":
		case "SHA-1":
		case "SHA-256":
		case "SHA-384":
		case "SHA-512":
			MessageDigest md = MessageDigest.getInstance(algorithm);
			byte[] array = md.digest(string.getBytes());
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
			}
			blankString = sb.toString();
			break;
		}
		return blankString;
	}
	
	public static final String LocationToString(final Location loc) {
		return loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ();
	}
	
	public static final Location StringToLocation(final String loc) {
		String[] arrayLoc = loc.split(":");
		return new Location(Bukkit.getWorld(arrayLoc[0]), Double.parseDouble(arrayLoc[1]), Double.parseDouble(arrayLoc[2]), Double.parseDouble(arrayLoc[3]));
	}
	
	public static String InventoryToString(Inventory invInventory) {
        String serialization = invInventory.getSize() + ";";
        for(int i = 0; i < invInventory.getSize(); i++) {
            ItemStack is = invInventory.getItem(i);
            if(is != null) {
                String serializedItemStack = new String();
				String isType = String.valueOf(is.getType().getId());
                serializedItemStack += "t@" + isType;
                if(is.getDurability() != 0) {
                    String isDurability = String.valueOf(is.getDurability());
                    serializedItemStack += ":d@" + isDurability;
                }
                if(is.getAmount() != 1) {
                    String isAmount = String.valueOf(is.getAmount());
                    serializedItemStack += ":a@" + isAmount;
                }
                Map<Enchantment,Integer> isEnch = is.getEnchantments();
                if(isEnch.size() > 0) {
                    for(Entry<Enchantment,Integer> ench : isEnch.entrySet()) {
                        serializedItemStack += ":e@" + ench.getKey().getId() + "@" + ench.getValue();
                    }
                }
                serialization += i + "#" + serializedItemStack + ";";
            }
        }
        return serialization;
    }
   
    public static Inventory StringToInventory(String invString) {
        String[] serializedBlocks = invString.split(";");
        String invInfo = serializedBlocks[0];
        Inventory deserializedInventory = Bukkit.getServer().createInventory(null, Integer.valueOf(invInfo));
        for(int i = 1; i < serializedBlocks.length; i++) {
            String[] serializedBlock = serializedBlocks[i].split("#");
            int stackPosition = Integer.valueOf(serializedBlock[0]);
            if(stackPosition >= deserializedInventory.getSize()) {
                continue;
            }
            ItemStack is = null;
            Boolean createdItemStack = false;
            String[] serializedItemStack = serializedBlock[1].split(":");
            for(String itemInfo : serializedItemStack) {
                String[] itemAttribute = itemInfo.split("@");
                if (itemAttribute[0].equals("t")) {
                    is = new ItemStack(Material.getMaterial(Integer.valueOf(itemAttribute[1])));
                    createdItemStack = true;
                }
                else if (itemAttribute[0].equals("d") && createdItemStack) {
                    is.setDurability(Short.valueOf(itemAttribute[1]));
                }
                else if (itemAttribute[0].equals("a") && createdItemStack) {
                    is.setAmount(Integer.valueOf(itemAttribute[1]));
                }
                else if (itemAttribute[0].equals("e") && createdItemStack) {
                    is.addEnchantment(Enchantment.getById(Integer.valueOf(itemAttribute[1])), Integer.valueOf(itemAttribute[2]));
                }
            }
            deserializedInventory.setItem(stackPosition, is);
        }
        return deserializedInventory;
    }
	
}
