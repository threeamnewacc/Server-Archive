package com.massivecraft.factions.type;

import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.Conf;
import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.WorldBorder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.ParseException;
import java.util.Calendar;

public class WBScheduleTask extends BukkitRunnable {

	private int lastMinute = -1;

	private void run(WBScheduleItem item) {
		BorderData borderData = WorldBorder.plugin.GetWorldBorder("world");
		int xRad = borderData.getRadiusX();
		int zRad = borderData.getRadiusZ();
		borderData.setData(0, 0, xRad + item.getDistance(), zRad + item.getDistance(), false);
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(ChatColor.GREEN + "The World Border has expanded " + ChatColor.GOLD + item.getDistance() + ChatColor.GREEN + " blocks!");
		Bukkit.broadcastMessage(ChatColor.GREEN + "It is now at " + ChatColor.GOLD + borderData.getRadius() + ChatColor.GREEN + " radius from spawn.");
		Bukkit.broadcastMessage("");
	}

	@Override
	public void run() {
		Calendar now = Calendar.getInstance();
		int minute = now.get(Calendar.MINUTE);
		if (minute != lastMinute) {
			lastMinute = minute;
			for (WBScheduleItem item : Conf.worldBorderExpandSchedule) {
				try {
					if (item.matches(now)) {
						run(item);
					}
				} catch (ParseException ex) {
					HCFactions.getInstance().getLogger().warning("ParseException in world border scheduler: " + ex.getMessage());
				}
			}
		}
	}
}
