package net.kohi.vaultbattle.type;

import net.kohi.vaultbattle.manager.GameManager;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMap {

    private String mapName = "EXAMPLE";

    private String worldName = "EXAMPLE";

    //Time untill the vaultbattle break default 10 min
    private int wallsBreakTime = 1200;

    private int maxBuildHeight = 90;

    private Map<TeamColor, SimpleLocation> teamSpawns = new HashMap<>();
    private Map<TeamColor, Region> banks = new HashMap<>();

    //Walls that will break after a set amount of time.
    private List<Region> walls = new ArrayList<>();


    public Location getTeamSpawn(TeamColor teamColor) {
        SimpleLocation loc = teamSpawns.get(teamColor);
        return loc.toLocation(GameManager.gameMapWorld);
    }

    public boolean isValid() {
        if (teamSpawns.keySet().size() == 4) {
            if (banks.keySet().size() == 4) {
                if (walls.size() >= 4) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getMapName() {
        return this.mapName;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public int getWallsBreakTime() {
        return this.wallsBreakTime;
    }

    public int getMaxBuildHeight() {
        return this.maxBuildHeight;
    }

    public Map<TeamColor, SimpleLocation> getTeamSpawns() {
        return this.teamSpawns;
    }

    public Map<TeamColor, Region> getBanks() {
        return this.banks;
    }

    public List<Region> getWalls() {
        return this.walls;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public void setWallsBreakTime(int wallsBreakTime) {
        this.wallsBreakTime = wallsBreakTime;
    }

    public void setMaxBuildHeight(int maxBuildHeight) {
        this.maxBuildHeight = maxBuildHeight;
    }

    public void setTeamSpawns(Map<TeamColor, SimpleLocation> teamSpawns) {
        this.teamSpawns = teamSpawns;
    }

    public void setBanks(Map<TeamColor, Region> banks) {
        this.banks = banks;
    }

    public void setWalls(List<Region> walls) {
        this.walls = walls;
    }
}
