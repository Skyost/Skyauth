package fr.skyost.auth.tasks;

import fr.skyost.auth.AuthPlugin;

public class SkyauthTasks implements Runnable {

	@Override
	public void run() {
		try {
			AuthPlugin.reload();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
