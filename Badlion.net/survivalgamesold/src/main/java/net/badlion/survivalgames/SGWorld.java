package net.badlion.survivalgames;

import net.badlion.worldrotator.GWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SGWorld {

    private List<Location> spawnLocations = new ArrayList<>();
    private List<Location> deathMatchLocations = new ArrayList<>();
    private List<Location> tier1ChestLocations = new ArrayList<>();
    private List<Location> tier2ChestLocations = new ArrayList<>();
    private GWorld gWorld;
    private Location deathMatchCenterLocation;

    public SGWorld(GWorld gWorld) {
        this.gWorld = gWorld;
    }

    public void load() {
        List<String> spawnLocationsStrings = gWorld.getYml().getStringList("spawn_locations");
        for (String loc : spawnLocationsStrings) {
            String[] locValues = loc.split(" ");
            Location newLoc = new Location(Bukkit.getWorld(this.getgWorld().getInternalName()), 0, 1, 0);
            newLoc.setX(Double.parseDouble(locValues[0]));
            newLoc.setY(Double.parseDouble(locValues[1]));
            newLoc.setZ(Double.parseDouble(locValues[2]));
            newLoc.setYaw(Float.parseFloat(locValues[3]));
            newLoc.setPitch(Float.parseFloat(locValues[4]));
            this.spawnLocations.add(newLoc);
        }
        Collections.shuffle(this.spawnLocations);

        List<String> deathMatchLocationsStrings = gWorld.getYml().getStringList("deathmatch_locations");
        for (String loc : deathMatchLocationsStrings) {
            String[] locValues = loc.split(" ");
            Location newLoc = new Location(Bukkit.getWorld(this.getgWorld().getInternalName()), 0, 1, 0);
            newLoc.setX(Double.parseDouble(locValues[0]));
            newLoc.setY(Double.parseDouble(locValues[1]));
            newLoc.setZ(Double.parseDouble(locValues[2]));
            newLoc.setYaw(Float.parseFloat(locValues[3]));
            newLoc.setPitch(Float.parseFloat(locValues[4]));
            this.deathMatchLocations.add(newLoc);
        }
        Collections.shuffle(this.deathMatchLocations);

        List<String> tier1ChestStrings = gWorld.getYml().getStringList("tier_1_chests");
        for (String loc : tier1ChestStrings) {
            String[] locValues = loc.split(" ");
            Location newLoc = new Location(Bukkit.getWorld(this.getgWorld().getInternalName()), 0, 1, 0);
            newLoc.setX(Integer.parseInt(locValues[0]));
            newLoc.setY(Integer.parseInt(locValues[1]));
            newLoc.setZ(Integer.parseInt(locValues[2]));
            this.tier1ChestLocations.add(newLoc);
        }

        List<String> tier2ChestStrings = gWorld.getYml().getStringList("tier_2_chests");
        for (String loc : tier2ChestStrings) {
            String[] locValues = loc.split(" ");
            Location newLoc = new Location(Bukkit.getWorld(this.getgWorld().getInternalName()), 0, 1, 0);
            newLoc.setX(Integer.parseInt(locValues[0]));
            newLoc.setY(Integer.parseInt(locValues[1]));
            newLoc.setZ(Integer.parseInt(locValues[2]));
            this.tier2ChestLocations.add(newLoc);
            newLoc.getBlock().setType(Material.CHEST);
        }

        if (!this.gWorld.getYml().getBoolean("deathmatch_arena")) {
            String[] locValues = this.gWorld.getYml().getString("deathmatch_center").split(" ");
            this.deathMatchCenterLocation = new Location(Bukkit.getWorld(this.getgWorld().getInternalName()), Double.parseDouble(locValues[0]), Double.parseDouble(locValues[1]), Double.parseDouble(locValues[2]), Float.parseFloat(locValues[3]), Float.parseFloat(locValues[4]));
        }
    }

    public GWorld getgWorld() {
        return gWorld;
    }

    public List<Location> getSpawnLocations() {
        return this.spawnLocations;
    }

    public List<Location> getDeathMatchLocations() {
        return this.deathMatchLocations;
    }

    public List<Location> getTier1ChestLocations() {
        return tier1ChestLocations;
    }

    public List<Location> getTier2ChestLocations() {
        return tier2ChestLocations;
    }

    public Location getDeathMatchCenterLocation() {
        return deathMatchCenterLocation;
    }
}
