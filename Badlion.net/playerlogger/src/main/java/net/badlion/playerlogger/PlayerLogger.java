package net.badlion.playerlogger;

import net.badlion.gberry.Gberry;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.badlion.playerlogger.listeners.PlayerDeathListener;
import net.badlion.playerlogger.listeners.PlayerJoinListener;

public class PlayerLogger extends JavaPlugin {
	
	private FileConfiguration fileConfiguration;
	private String serverString;
	
	@Override
	public void onEnable() {
		this.saveDefaultConfig();

		this.serverString = this.getConfig().getString("playerlogger.server");
		if (this.serverString.equalsIgnoreCase("default")) {
			Bukkit.getLogger().severe("Default server name, disabling.");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		// Listeners
		if (this.getConfig().getBoolean("playerlogger.track-deaths")) {
			this.getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
		}

		if (this.getConfig().getBoolean("playerlogger.track-ips")) {
			this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
		}

		Gberry.enableAsyncLoginEvent = true;
	}
	
	@Override
	public void onDisable() {
	}
	
	public long toLongIp(String ipAddress) {
		String[] addrArray = ipAddress.split("\\.");

	    long ipDecimal = 0;

	    for (int i = 0; i < addrArray.length; i++) {

	        int power = 3 - i;
	        ipDecimal += ((Integer.parseInt(addrArray[i]) % 256 * Math.pow(256, power)));
	    }
	    
	    return ipDecimal;
	}

	public String getServerString() {
		return serverString;
	}

	public void setServerString(String server) {
		this.serverString = server;
	}

}
