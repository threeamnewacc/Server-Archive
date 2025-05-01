package net.badlion.arenapvp.arenas;

import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.Game;
import net.badlion.arenapvp.manager.ArenaManager;
import net.badlion.arenapvp.matchmaking.Match;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.blocks.CraftMassBlockUpdate;
import net.badlion.gberry.utils.blocks.MassBlockUpdate;
import org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SkyWarsArena extends Arena {

	protected Material walls = Material.WOOL;
	protected Material ceiling = Material.GLASS;

	private List<Location> locationsToDestroy = new ArrayList<>();
	private List<Location> locationsToDestroyCopy = new ArrayList<>();
	private Map<Location, Material> locationToMaterial = new HashMap<>();
	private Map<Location, Byte> locationToData = new HashMap<>();

	private BukkitTask destroyArenaTask;
	private BukkitTask warningTask;

	private Match match;

	private final File skyWarsDir = new File(ArenaPvP.getInstance().getDataFolder(), "Skywars/");


	public SkyWarsArena(String arenaName, String schematicName, Location warp1, Location warp2, String extraData, String arenaTypes) {
		super(arenaName, schematicName, warp1, warp2, arenaTypes);

		// Try to ready a file from the disk and if not report it as an error
		File jsonFile = new File(ArenaPvP.getInstance().getDataFolder(), schematicName + ".json");
		if (jsonFile.exists()) {
			try {
				JSONObject jsonObject = (JSONObject) JSONValue.parse(new String(Files.readAllBytes(Paths.get(jsonFile.getAbsolutePath()))));

				for (String locationString : (List<String>) jsonObject.get("blocks_to_destroy")) {
					Location location = Gberry.parseLocation(locationString);
					this.locationsToDestroy.add(location);
					this.locationsToDestroyCopy.add(location);
					this.locationToMaterial.put(location, location.getBlock().getType());
					this.locationToData.put(location, location.getBlock().getData());
				}
			} catch (IOException e) {
				ArenaPvP.getInstance().getLogger().info("Error reading " + arenaName);
			}
		} else {
			ArenaPvP.getInstance().getLogger().info("File missing for SkyWarsArena " + arenaName);
		}
	}

	@Override
	public void scan() {
		Location warp = this.getWarp1Origin();
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

		// Get locations of our middle island corners
		Location lowerMiddle = ArenaManager.getWarp(this.getArenaName() + "-ml");
		Location upperMiddle = ArenaManager.getWarp(this.getArenaName() + "-ul");

		if (lowerMiddle == null || upperMiddle == null) {
			throw new RuntimeException("Error for upper or lower for " + this.getArenaName());
		}

		List<String> blocksToBlowAway = new ArrayList<>();
		for (int x = xMin; x <= xMax; x++) {
			for (int z = zMin; z <= zMax; z++) {
				for (int y = 0; y <= 255; y++) {
					Block block = xMinLoc.getWorld().getBlockAt(x, y, z);
					if (block != null && block.getType() != Material.AIR) {
						if (block.getType() == Material.GLASS || block.getType() == Material.STAINED_GLASS) {
							break;
						}

						if (!Gberry.isLocationInBetween(lowerMiddle, upperMiddle, block.getLocation())) {
							blocksToBlowAway.add(Gberry.getLocationString(block.getLocation().clone().subtract(getOrigin().getBlockX(), getOrigin().getBlockY(), getOrigin().getBlockZ())));
						}
					}
				}
			}
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("blocks_to_destroy", blocksToBlowAway);
		String jsonString = jsonObject.toJSONString();

		File jsonFile = new File(ArenaPvP.getInstance().getDataFolder(), this.getSchematicName() + ".json");
		try {
			FileUtils.write(jsonFile, Gberry.formatJSON(jsonString));
		} catch (IOException e) {
			ArenaPvP.getInstance().getLogger().info("Failed to write arena string " + this.getArenaName());
		}
	}

	/* TODO: Redo this if we ever add skywars
	public void toggleBeingUsed() {
		if (this.beingUsed) {
			// Possible this happens at the same time
			new CleanArenaTask(this).runTaskTimer(ArenaPvP.getInstance(), 60L, 1L);

			this.rebuild();
			this.match = null;
		} else {
			Gberry.log("ARENA", "Arena " + this.getArenaName() + " is being toggled from false to true");

			this.beingUsed = true;
		}
	}*/

	@Override
	public void rebuild() {
		if (this.destroyArenaTask != null) {
			this.destroyArenaTask.cancel();
			this.destroyArenaTask = null;
		}

		if (this.warningTask != null) {
			this.warningTask.cancel();
			this.warningTask = null;
		}

		new RebuildArenaTask().runTaskTimer(ArenaPvP.getInstance(), 5, 1L);

		this.locationsToDestroy = new ArrayList<>(this.locationsToDestroyCopy);
	}

	public void startArenaUse(Game game) {
		this.destroyArenaTask = new DestroyArenaTask().runTaskTimer(ArenaPvP.getInstance(), 60 * 20, 1);

		this.match = (Match) game;

		this.warningTask = new BukkitRunnable() {
			public void run() {
				SkyWarsArena.this.match.broadcastMessage(ChatColor.DARK_AQUA + "Spawn Islands will be destroyed in 10 seconds.");
			}
		}.runTaskLater(ArenaPvP.getInstance(), 50 * 20);
	}

	private class DestroyArenaTask extends BukkitRunnable {

		private List<Location> blocksToDestroy;
		private Iterator<Location> iterator;
		private boolean initialized = false;

		@Override
		public void run() {
			if (!this.initialized) {
				this.initialized = true;
				this.blocksToDestroy = new ArrayList<>(SkyWarsArena.this.locationsToDestroy);
				this.iterator = this.blocksToDestroy.iterator();

				Gberry.log("LAG", "Arena " + SkyWarsArena.this.getArenaName() + " spawn island destruction has begun");
				SkyWarsArena.this.match.broadcastMessage(ChatColor.DARK_AQUA + "The spawn islands have begun falling apart.");
				return;
			}

			MassBlockUpdate massBlockUpdate = new CraftMassBlockUpdate(ArenaPvP.getInstance().getSpawnLocation().getWorld());
			massBlockUpdate.setRelightingStrategy(MassBlockUpdate.RelightingStrategy.NEVER);
			//massBlockUpdate.setMaxRelightTimePerTick(2, TimeUnit.MILLISECONDS);

			while (this.iterator.hasNext()) {
				Location location = this.iterator.next();

				location.getBlock().setType(Material.AIR);
				//massBlockUpdate.setBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ(), 0);
			}

			//massBlockUpdate.notifyClients();

			SkyWarsArena.this.destroyArenaTask = null;

			this.cancel();
		}

	}

	private class RebuildArenaTask extends BukkitRunnable {

		private Set<Location> locations;
		private Iterator<Location> iterator;
		private boolean initialized = false;

		@Override
		public void run() {
			if (!this.initialized) {
				this.locations = new HashSet<>(SkyWarsArena.this.locationToMaterial.keySet());
				this.iterator = this.locations.iterator();
				this.initialized = true;
				Gberry.log("LAG", "Arena " + SkyWarsArena.this.getArenaName() + " rebuilding task has begun");
				return;
			}

			MassBlockUpdate massBlockUpdate = new CraftMassBlockUpdate(ArenaPvP.getInstance().getSpawnLocation().getWorld());
			massBlockUpdate.setRelightingStrategy(MassBlockUpdate.RelightingStrategy.HYBRID);
			massBlockUpdate.setMaxRelightTimePerTick(2, TimeUnit.MILLISECONDS);

			while (this.iterator.hasNext()) {
				Location location = this.iterator.next();
				Material material = SkyWarsArena.this.locationToMaterial.get(location);
				Byte b = SkyWarsArena.this.locationToData.get(location);

				massBlockUpdate.setBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ(), material.getId(), b);
			}

			massBlockUpdate.notifyClients();

			this.cancel();
		}

	}

	public void addToDestroy(Location location) {
		this.locationsToDestroy.add(location);
	}

}
