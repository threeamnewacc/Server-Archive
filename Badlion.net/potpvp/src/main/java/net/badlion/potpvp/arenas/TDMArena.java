package net.badlion.potpvp.arenas;

import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.tdm.TDMGame;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class TDMArena extends Arena {

	private Map<ChatColor, Location> spawnLocations = new HashMap<>();

	public TDMArena(String arenaName, Location warp1, Location warp2) {
		super(arenaName, warp1, warp2);

		// We can use a normal Arena for this, but this is
		// easier and slightly faster

		for (int i = 0; i < TDMGame.TEAM_COLORS.length; i++) {
			this.spawnLocations.put(TDMGame.TEAM_COLORS[i],
					ArenaManager.getWarp(this.getArenaName() + "-" + TDMGame.TEAM_COLORS[i].name().toLowerCase()));
		}
	}

	public Location getSpawnLocation(TDMGame.TDMTeam tdmTeam) {
		return this.spawnLocations.get(tdmTeam.getColor());
	}

}
