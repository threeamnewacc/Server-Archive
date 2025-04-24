package com.massivecraft.factions.listeners;

import club.minemen.core.util.finalutil.CC;
import club.minemen.hcfactions.HCFactions;
import club.minemen.hcfactions.combat.CombatTagManager;
import club.minemen.hcfactions.combat.LoggerNPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.ArrayList;
import java.util.List;

public class RebootListener implements Listener {

	@EventHandler
	public void onServerCommand(ServerCommandEvent event) {
		if (event.getCommand().toLowerCase().startsWith("/stop") || event.getCommand().toLowerCase().startsWith("stop")) {
			reboot();
		}
	}

	private void reboot() {
		HCFactions.rebooting = true;
		List<LoggerNPC> loggers = new ArrayList<>(CombatTagManager.getInstance().getAllLoggers());
		for (LoggerNPC loggerNPC : loggers) {
			loggerNPC.remove(LoggerNPC.REMOVE_REASON.REJOIN);
		}
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			player.kickPlayer(CC.GOLD + "Server rebooting.");
		}
	}
}

