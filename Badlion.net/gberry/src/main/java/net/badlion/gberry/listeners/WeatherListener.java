package net.badlion.gberry.listeners;

import net.badlion.gberry.Gberry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WeatherListener implements Listener {

	private Gberry plugin;

	public WeatherListener(Gberry plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		if (event.toWeatherState()) {
			// Being set to raining
			event.setCancelled(true);
		}
	}

}
