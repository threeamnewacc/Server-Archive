package net.badlion.potpvp.arenas;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.Pair;
import net.badlion.potpvp.PotPvP;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class BuildUHCArena extends Arena {

    private Map<Pair, Integer> maxBuildLocations = new HashMap<>();
    private Set<Material> blackListedMaterials = new HashSet<>();
    protected Material walls = Material.WOOL;
    protected Material ceiling = Material.GLASS;

    public BuildUHCArena(String arenaName, Location warp1, Location warp2, String extraData) {
        super(arenaName, warp1, warp2);

        // Try to ready a file from the disk and if not report it as an error
        File jsonFile = new File(PotPvP.getInstance().getDataFolder(), arenaName + ".json");
        if (jsonFile.exists()) {
            try {
                JSONObject jsonObject = (JSONObject) JSONValue.parse(new String(Files.readAllBytes(Paths.get(jsonFile.getAbsolutePath()))));

                for (String keyValue : (List<String>) jsonObject.get("key_values")) {
                    String[] parts = keyValue.split(":");
                    try {

                        Pair key = Pair.fromString(parts[0]);
                        Integer value = Integer.parseInt(parts[1]);

                        this.maxBuildLocations.put(key, value);
                    } catch (NumberFormatException e) {
                        PotPvP.getInstance().getLogger().info("Error reading data for " + arenaName);
                    }
                }
            } catch (IOException e) {
                PotPvP.getInstance().getLogger().info("Error reading " + arenaName);
            }
        } else {
            PotPvP.getInstance().getLogger().info("File missing for BuildUHCArena " + arenaName);
        }

        this.blackListedMaterials.add(Material.GLASS);
        this.blackListedMaterials.add(Material.AIR);
        this.blackListedMaterials.add(Material.LOG);
        this.blackListedMaterials.add(Material.LOG_2);
        this.blackListedMaterials.add(Material.YELLOW_FLOWER);
        this.blackListedMaterials.add(Material.RED_ROSE);
        this.blackListedMaterials.add(Material.BROWN_MUSHROOM);
        this.blackListedMaterials.add(Material.RED_MUSHROOM);
        this.blackListedMaterials.add(Material.DOUBLE_PLANT);
        this.blackListedMaterials.add(Material.LONG_GRASS);
        this.blackListedMaterials.add(Material.LEAVES);
        this.blackListedMaterials.add(Material.LEAVES_2);
    }

    @Override
    public void scan() {
        this.scanWithLocation(this.getWarp1());
    }

    public void scanWithLocation(Location warp) {
        while (warp.getY() < 256 && warp.getBlock().getType() != this.ceiling) {
            warp = warp.add(0, 1, 0);
        }

        if (warp.getY() == 256) {
            throw new RuntimeException("Failed to find glass block for " + this.getArenaName());
        }

        // Some arenas have glass above and not included
        warp = warp.add(0, -1, 0);

        // Find the edges
        int safety = 0;
        Location xMinLoc = warp.clone();
        while (safety < 300 && xMinLoc.getBlock().getType() != this.walls) {
            ++safety;
            xMinLoc.add(-1, 0, 0);
        }

        safety = 0;
        Location xMaxLoc = warp.clone();
        while (safety < 300 && xMaxLoc.getBlock().getType() != this.walls) {
            ++safety;
            xMaxLoc.add(1, 0, 0);
        }

        safety = 0;
        Location zMinLoc = warp.clone();
        while (safety < 300 && zMinLoc.getBlock().getType() != this.walls) {
            ++safety;
            zMinLoc.add(0, 0, -1);
        }

        safety = 0;
        Location zMaxLoc = warp.clone();
        while (safety < 300 && zMaxLoc.getBlock().getType() != this.walls) {
            ++safety;
            zMaxLoc.add(0, 0, 1);
        }

        // Create our internal corners (corners inside the actual arena [not wool])
        int xMin = xMinLoc.getBlockX() + 1;
        int xMax = xMaxLoc.getBlockX() - 1;
        int zMin = zMinLoc.getBlockZ() + 1;
        int zMax = zMaxLoc.getBlockZ() - 1;

        List<String> keyValues = new ArrayList<>();
        for (int x = xMin; x <= xMax; x++) {
            for (int z = zMin; z <= zMax; z++) {
                Block block = xMinLoc.getWorld().getHighestBlockAt(x, z);
                Block under = block.getRelative(0, -1, 0);

                // While the under block isn't something we want
                while (this.blackListedMaterials.contains(under.getType()) && under.getY() > 0) {
                    under = under.getRelative(0, -1, 0);
                }

                if (under.getY() == 0) {
                    throw new RuntimeException("Invalid block found " + under.toString());
                }

                Pair pair = Pair.of(under.getX(), under.getZ());
                Integer max = under.getY() + 4;

                this.maxBuildLocations.put(pair, max);
                keyValues.add(pair.toString() + ":" + max);
            }
        }

        // Write the file to disk
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key_values", keyValues);
        String jsonString = jsonObject.toJSONString();

        File jsonFile = new File(PotPvP.getInstance().getDataFolder(), this.getArenaName() + ".json");
        try {
            FileUtils.write(jsonFile, Gberry.formatJSON(jsonString));
        } catch (IOException e) {
            PotPvP.getInstance().getLogger().info("Failed to write arena string " + this.getArenaName());
        }
    }

	public boolean isAllowedToPlaceBlock(Location location) {
		Pair pair = Pair.of(location.getBlockX(), location.getBlockZ());
		int y = location.getBlockY();
		Integer max = this.maxBuildLocations.get(pair);

		if (max == null) {
			Bukkit.getLogger().info("Missing json position for " + pair + " " + this.getArenaName());
			return false;
		}

		return y <= max;
	}

	public int getMaxBlockYLevel(Location location) {
		Pair pair = Pair.of(location.getBlockX(), location.getBlockZ());
		Integer max = this.maxBuildLocations.get(pair);

		if (max == null) {
			Bukkit.getLogger().info("Missing json position for " + pair + " " + this.getArenaName());
			return -1;
		}

		return max;
	}

}
