package net.badlion.potpvp.arenas;

import net.badlion.gberry.Gberry;
import net.badlion.potpvp.events.KOTH;
import net.badlion.potpvp.managers.ArenaManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class KOTHArena extends Arena {

    private Location minCorner;
    private Location maxCorner;

    private Map<ChatColor, Location> spawnLocations = new HashMap<>();

    public KOTHArena(String arenaName, Location warp1, Location warp2, String extraData) {
        super(arenaName, warp1, warp2);

        JSONObject jsonObject = (JSONObject) JSONValue.parse(extraData);
        for (Map.Entry<String, String> entry : (Set<Map.Entry<String, String>>) jsonObject.entrySet()) {
            if (entry.getKey().equals("mincorner")) {
                this.minCorner = Gberry.parseLocation(entry.getValue());
            } else if (entry.getKey().equals("maxcorner")) {
                this.maxCorner = Gberry.parseLocation(entry.getValue());
            }
        }

	    for (int i = 0; i < KOTH.TEAM_COLORS.length; i++) {
		    this.spawnLocations.put(KOTH.TEAM_COLORS[i], ArenaManager.getWarp(this.getArenaName() + "-" + (i + 1)));
	    }
    }

    public Location getMinCorner() {
        return minCorner;
    }

    public Location getMaxCorner() {
        return maxCorner;
    }

    public Location getSpawnLocation(ChatColor teamColor) {
	    return this.spawnLocations.get(teamColor);
    }

}
