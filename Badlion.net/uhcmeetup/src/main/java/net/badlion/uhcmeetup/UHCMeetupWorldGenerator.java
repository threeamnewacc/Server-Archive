package net.badlion.uhcmeetup;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.mpg.tasks.MatchmakingMCPListener;
import net.badlion.worldborder.WorldFillerTaskCompleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public class UHCMeetupWorldGenerator extends BukkitRunnable implements Listener {

	private World world;

	private boolean isGenerating = false;

	public UHCMeetupWorldGenerator() {
		// Fail-safe
		this.deleteDirectory(new File("worlds/" + UHCMeetupWorld.WORLD_NAME));

		// Register listener
		UHCMeetup.getInstance().getServer().getPluginManager().registerEvents(this, UHCMeetup.getInstance());
	}

	@EventHandler
	public void onWorldFinishGeneration(WorldFillerTaskCompleteEvent event) {
		// Add the glass walls
		this.addGlassBorder();


		// Start matchmaking MCP listener in 5 seconds
		BukkitUtil.runTaskLater(new Runnable() {
			@Override
			public void run() {
				UHCMeetup.getInstance().getServer().getPluginManager().registerEvents(new MatchmakingMCPListener(), UHCMeetup.getInstance());
			}
		}, 100L);
	}

	@Override
	public void run() {
		// Don't let one start if one is generating already
		if (this.isGenerating) {
			return;
		}

		this.generateNewWorld();
	}

	private void generateNewWorld() {
		// Just to avoid any weird bukkit race conditions
		this.isGenerating = true;

		WorldCreator worldCreator = new WorldCreator(UHCMeetupWorld.WORLD_NAME);

		// Another fail safe
		try {
			this.world = UHCMeetup.getInstance().getServer().createWorld(worldCreator);
		} catch (Exception e) {
			Bukkit.getLogger().info("World NPE when trying to generate map.");
			Bukkit.getServer().unloadWorld(this.world, false);

			this.deleteDirectory(new File("worlds", UHCMeetupWorld.WORLD_NAME));

			this.isGenerating = false;
			return;
		}

		int waterCount = 0;

		Bukkit.getLogger().info("Loaded a new world.");
		boolean flag = false;
		for (int i = -UHCMeetupWorld.WORLD_RADIUS; i <= UHCMeetupWorld.WORLD_RADIUS; ++i) {
			boolean isInvalid = false;
			for (int j = -UHCMeetupWorld.WORLD_RADIUS; j <= UHCMeetupWorld.WORLD_RADIUS; j++) {
				boolean isCenter = i >= -100 && i <= 100 && j >= -100 && j <= 100;
				if (isCenter) {
					Block block = this.world.getHighestBlockAt(i, j).getLocation().add(0, -1, 0).getBlock();
					if (block.getType() == Material.STATIONARY_WATER || block.getType() == Material.WATER || block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA) {
						++waterCount;
					}
				}

				if (waterCount >= 1000) {
					Bukkit.getLogger().info("Invalid center, too much water/lava.");
					isInvalid = true;
					break;
				}
			}

			if (isInvalid) {
				flag = true;
				Bukkit.getLogger().info("Invalid biome2");
				break;
			}
		}

		// TODO: TESTING
		//if (flag) flag = false;

		// Actually got this far...we have a valid world, generate the rest
		if (flag) {
			Bukkit.getLogger().info("Failed to find a good seed (" + this.world.getSeed() + ").");
			Bukkit.getServer().unloadWorld(this.world, false);

			this.deleteDirectory(new File("worlds", UHCMeetupWorld.WORLD_NAME));

			this.isGenerating = false;
			return;
		} else {
			Bukkit.getLogger().info("Found a good seed (" + this.world.getSeed() + ").");
			this.cancel();
		}

		// Create Lock
		File lock = new File("worlds", UHCMeetupWorld.WORLD_NAME + "/gen.lock");
		try {
			lock.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
			return;
		}

		this.isGenerating = true;

		// Start the worldborder stuff now
		UHCMeetup.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb shape square");
		UHCMeetup.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb " + UHCMeetupWorld.WORLD_NAME + " set " + UHCMeetupWorld.WORLD_RADIUS + " 0 0");
		UHCMeetup.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb " + UHCMeetupWorld.WORLD_NAME + " fill 5000");
		UHCMeetup.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb fill confirm");
	}

	private boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	private void addGlassBorder() {
		new BukkitRunnable() {

			private World world = Bukkit.getServer().getWorld(UHCMeetupWorld.WORLD_NAME);

			private int counter = -UHCMeetupWorld.WORLD_RADIUS - 1;
			private boolean phase1 = false;
			private boolean phase2 = false;
			private boolean phase3 = false;

			@Override
			public void run() {
				if (!this.phase1) {
					int maxCounter = this.counter + 500;
					int x = -UHCMeetupWorld.WORLD_RADIUS - 1;
					for (int z = this.counter; z <= UHCMeetupWorld.WORLD_RADIUS && this.counter <= maxCounter; z++, this.counter++) {
						Block highestBlock = this.world.getHighestBlockAt(x, z);

						// Ignore non-solid blocks
						while (!highestBlock.getType().isSolid()
								|| highestBlock.getType() == Material.LEAVES || highestBlock.getType() == Material.LEAVES_2) {
							highestBlock = highestBlock.getRelative(0, -1, 0);
						}

						int y = highestBlock.getY() + 1;
						for (int i = y; i < 200; i++) {
							Block block = this.world.getBlockAt(x, i, z);

							block.setType(Material.GLASS);
							block.setData((byte) 0);
						}
					}

					if (this.counter >= UHCMeetupWorld.WORLD_RADIUS) {
						this.counter = -UHCMeetupWorld.WORLD_RADIUS - 1;
						this.phase1 = true;
					}

					return;
				}

				if (!this.phase2) {
					int maxCounter = this.counter + 500;
					int x = UHCMeetupWorld.WORLD_RADIUS;
					for (int z = this.counter; z <= UHCMeetupWorld.WORLD_RADIUS && this.counter <= maxCounter; z++, this.counter++) {
						Block highestBlock = this.world.getHighestBlockAt(x, z);

						// Ignore non-solid blocks
						while (!highestBlock.getType().isSolid()
								|| highestBlock.getType() == Material.LEAVES || highestBlock.getType() == Material.LEAVES_2) {
							highestBlock = highestBlock.getRelative(0, -1, 0);
						}

						int y = highestBlock.getY() + 1;
						for (int i = y; i < 200; i++) {
							Block block = this.world.getBlockAt(x, i, z);

							block.setType(Material.GLASS);
							block.setData((byte) 0);
						}
					}

					if (this.counter >= UHCMeetupWorld.WORLD_RADIUS) {
						this.counter = -UHCMeetupWorld.WORLD_RADIUS - 1;
						this.phase2 = true;
					}

					return;
				}

				if (!this.phase3) {
					int maxCounter = this.counter + 500;
					int z = -UHCMeetupWorld.WORLD_RADIUS - 1;
					for (int x = this.counter; x <= UHCMeetupWorld.WORLD_RADIUS && this.counter <= maxCounter; x++, this.counter++) {
						if (x == UHCMeetupWorld.WORLD_RADIUS || x == -UHCMeetupWorld.WORLD_RADIUS - 1) {
							continue;
						}

						Block highestBlock = this.world.getHighestBlockAt(x, z);

						// Ignore non-solid blocks
						while (!highestBlock.getType().isSolid()
								|| highestBlock.getType() == Material.LEAVES || highestBlock.getType() == Material.LEAVES_2) {
							highestBlock = highestBlock.getRelative(0, -1, 0);
						}

						int y = highestBlock.getY() + 1;
						for (int i = y; i < 200; i++) {
							Block block = this.world.getBlockAt(x, i, z);

							block.setType(Material.GLASS);
							block.setData((byte) 0);
						}
					}

					if (this.counter >= UHCMeetupWorld.WORLD_RADIUS) {
						this.counter = -UHCMeetupWorld.WORLD_RADIUS - 1;
						this.phase3 = true;
					}

					return;
				}


				int maxCounter = this.counter + 500;
				int z = UHCMeetupWorld.WORLD_RADIUS;
				for (int x = this.counter; x <= UHCMeetupWorld.WORLD_RADIUS && this.counter <= maxCounter; x++, this.counter++) {
					if (x == UHCMeetupWorld.WORLD_RADIUS || x == -UHCMeetupWorld.WORLD_RADIUS - 1) {
						continue;
					}

					Block highestBlock = this.world.getHighestBlockAt(x, z);

					// Ignore non-solid blocks
					while (!highestBlock.getType().isSolid()
							|| highestBlock.getType() == Material.LEAVES || highestBlock.getType() == Material.LEAVES_2) {
						highestBlock = highestBlock.getRelative(0, -1, 0);
					}

					int y = highestBlock.getY() + 1;
					for (int i = y; i < 200; i++) {
						Block block = this.world.getBlockAt(x, i, z);

						block.setType(Material.GLASS);
						block.setData((byte) 0);
					}
				}

				if (this.counter >= UHCMeetupWorld.WORLD_RADIUS) {
					this.cancel();
				}
			}
		}.runTaskTimer(UHCMeetup.getInstance(), 0L, 1L);
	}


}
