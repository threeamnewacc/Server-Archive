package com.massivecraft.factions.cmd.serveradmin;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;

public class CmdButcher extends FCommand {

	public CmdButcher() {
		this.aliases.add("butcher");
		this.permission = Permission.BUTCHER.node;
	}

	@Override
	public void perform() {
		int count = 0;
		int killed = 0;
		for (World world : Bukkit.getWorlds()) {
			for (Entity entity : world.getEntities()) {
				count++;
				if (entity instanceof Animals || entity instanceof Monster) {
					FLocation flocation = new FLocation(entity.getLocation());
					if (!Board.getFactionAt(flocation).isNormal()) {
						entity.remove();
						killed++;
					}
				}
			}
		}
		msg("<instance>Killed " + killed + " mobs outside faction land.");
		msg("<instance>Saved " + (count - killed) + " mobs from being butchered.");
	}

}
