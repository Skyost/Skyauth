package com.skyost.auth.utils;

public class PasswordUtils {
	
	
	public static String encrypt(String password) {
		String crypto = "";
		for(int i = 0; i < password.length(); i++)  {
			int c = password.charAt(i)^48;  
			crypto = crypto + (char)c; 
		}
		return crypto;
	}
	
	public static String decrypt(String password) {
		String crypto = "";
		for(int i = 0; i < password.length(); i++)  {
			int c = password.charAt(i)^48;  
			crypto=crypto + (char)c; 
		}
		return crypto;
	}
}
