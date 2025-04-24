package com.massivecraft.factions.cmd.serveradmin;

import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.WorldBorder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;


public class CmdEotw extends FCommand {


	public CmdEotw() {
		this.aliases.add("eotw");
		this.permission = Permission.ADMIN.node;
	}

	@Override
	public void perform() {
		if (!sender.isOp()) {
			return;
		}
		HCFactions.getInstance().endOfTheWorld = true;
		// TODO: Set world border to 4000
		// TODO: Add a check that stops end portals from being used

		int count = 0;
		int killed = 0;
		for (World world : Bukkit.getWorlds()) {
			for (Entity entity : world.getEntities()) {
				count++;
				if (entity instanceof Cow || entity instanceof Sheep || entity instanceof Pig
						|| entity instanceof Chicken || entity instanceof Creeper || entity instanceof Zombie
						|| entity instanceof Skeleton || entity instanceof Spider || entity instanceof CaveSpider) {
					entity.remove();
					killed++;
				}
			}
		}
		msg("<instance>Killed " + killed + " mobs.");
		msg("<instance>" + (count - killed) + " mobs were not killed.");

		//Claim everything as warzone
		Conf.warzoneOutside.clear();
		Conf.warzoneOutside.put("world", 0);

		BorderData borderData = WorldBorder.plugin.GetWorldBorder("world");
		borderData.setData(0, 0, 4000, false);
		msg("World border is now 4000 radius.");
	}
}
