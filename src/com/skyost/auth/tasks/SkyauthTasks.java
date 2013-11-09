package com.skyost.auth.tasks;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map.Entry;

import com.skyost.auth.AuthPlugin;

public class SkyauthTasks implements Runnable {
	
	private Statement stat;
	private final ArrayList<String> arrayData = new ArrayList<String>();
	
	public SkyauthTasks(Statement stat) {
		this.stat = stat;
	}

	@Override
	public void run() {
		try {
			if(AuthPlugin.config.MySQL_Use) {
				ResultSet rs = stat.executeQuery("SELECT Account, Password, Code FROM Skyauth_Data");
				while(rs.next()) {
					arrayData.add(0, rs.getString("Password"));
					arrayData.add(1, rs.getString("Code"));
					AuthPlugin.data.put(rs.getString("Account"), arrayData);
				}
				for(Entry<String, ArrayList<String>> entry : AuthPlugin.data.entrySet()) {
					stat.execute("INSERT INTO Skyauth_Data(User, Password, Code) VALUES('" + entry.getKey() + "', '" + entry.getValue().get(0) + "', '" + entry.getValue().get(1) + "')");
				}
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

}
