package com.massivecraft.factions.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class FactionsServerListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginDisable(PluginDisableEvent event) {
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginEnable(PluginEnableEvent event) {
	}
}
