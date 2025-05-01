package net.badlion.survivalgames.tasks;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.Gberry;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.tasks.GameTimeTask;
import net.badlion.survivalgames.SurvivalGames;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SupplyDropTask extends BukkitRunnable {

	private static final int DROP_TIME = 60;

	private static SupplyDropTask instance;

	private final List<Integer> startedSupplyDrops = new ArrayList<>();
	private final Map<Integer, Boolean> supplyDropTimes = new LinkedHashMap<>();

	public SupplyDropTask() {
		SupplyDropTask.instance = this;

		// Times are in seconds and must be added in ascending order
		// Note: Make sure times don't overlap when countering in drop time
		this.supplyDropTimes.put(210, false);
		this.supplyDropTimes.put(600, false);
	}

	@Override
	public void run() {
		if (MPG.getInstance().getMPGGame().getGameState() == MPGGame.GameState.GAME) {
			for (Map.Entry<Integer, Boolean> dropTime : this.supplyDropTimes.entrySet()) {
				if (!this.startedSupplyDrops.contains(dropTime.getKey())
						&& GameTimeTask.getInstance().getTotalSeconds() == dropTime.getKey() - SupplyDropTask.DROP_TIME) {
					// Get a random supply drop location
					Location location = SurvivalGames.getInstance().getSGGame().getWorld().getRandomSupplyDropLocation();

					// Spawn particles
					for (int i = 0; i < 10; i++) {
						for (int j = 0; j < 10; j++) {
							for (int k = 0; k < 10; k++) {
								Location clone = location.clone();
								clone.getWorld().playEffect(clone.add(0.02D * i, 0.02D * j, 0.02D * k), Effect.TILE_DUST, 2);
							}
						}
					}

					// Play sounds
					location.getWorld().playSound(location, EnumCommon.getEnumValueOf(Sound.class, "FIREWORK_LAUNCH", "ENTITY_FIREWORK_LAUNCH"), 0.8F, (float) (0.3D + Math.random() * 0.4D));
					location.getWorld().playSound(location, EnumCommon.getEnumValueOf(Sound.class, "FIREWORK_LAUNCH", "ENTITY_FIREWORK_LAUNCH"), 0.8F, (float) (0.7D + Math.random() * 0.4D));
					location.getWorld().playSound(location, EnumCommon.getEnumValueOf(Sound.class, "FIREWORK_LAUNCH", "ENTITY_FIREWORK_LAUNCH"), 0.8F, (float) (0.3D + Math.random() * 0.4D));

					// Spawn beacon
					location.getBlock().setType(Material.BEACON);

					Gberry.broadcastMessageNoBalance(MPG.MPG_PREFIX + ChatColor.YELLOW + "A supply drop will drop on the beacon at "
							+ ChatColor.GREEN + "(" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ()
							+ ")" + ChatColor.YELLOW + " in " + SupplyDropTask.DROP_TIME + " seconds!");

					// Start drop supply drop task
					long taskInterval = (SupplyDropTask.DROP_TIME * 20L) / (SupplyDropTask.DROP_TIME / 15);
					new DropSupplyDropTask(dropTime.getKey(), location).runTaskTimer(SurvivalGames.getInstance(), taskInterval, taskInterval);

					this.startedSupplyDrops.add(dropTime.getKey());
					break;
				}
			}
		} else {
			this.cancel();
		}
	}

	public static SupplyDropTask getInstance() {
		return SupplyDropTask.instance;
	}

	public boolean haveAllSupplyDropsDropped() {
		for (Boolean dropped : this.supplyDropTimes.values()) {
			if (!dropped) return false;
		}

		return true;
	}

	public Map<Integer, Boolean> getSupplyDropTimes() {
		return this.supplyDropTimes;
	}

	private class DropSupplyDropTask extends BukkitRunnable {

		private int dropTime;
		private Location location;

		private int messagesSent = 0;

		public DropSupplyDropTask(int dropTime, Location location) {
			this.dropTime = dropTime;
			this.location = location;
		}

		@Override
		public void run() {
			if (MPG.getInstance().getMPGGame() == null
					|| MPG.getInstance().getMPGGame().getGameState().ordinal() >= MPGGame.GameState.DEATH_MATCH_COUNTDOWN.ordinal()) {
				this.cancel();
				return;
			}

			if (this.messagesSent < (SupplyDropTask.DROP_TIME / 15) - 1) {
				int seconds = ((SupplyDropTask.DROP_TIME / 15) - this.messagesSent - 1) * 15;

				Gberry.broadcastMessageNoBalance(MPG.MPG_PREFIX + ChatColor.YELLOW + "A supply drop will drop on the beacon at "
						+ ChatColor.GREEN + "(" + this.location.getBlockX() + ", " + this.location.getBlockY() + ", "
						+ this.location.getBlockZ() + ")" + ChatColor.YELLOW + " in " + seconds + " seconds!");

				this.messagesSent++;
				return;
			}

			// Spawn particles
			for (int i = 0; i < 5; i++) {      // TODO: REDUCE THE NUMBER OF EFFECTS WE PLAY HERE AND BELOW?
				for (int j = 0; j < 5; j++) {
					for (int k = 0; k < 5; k++) {
						Location clone = this.location.clone();
						clone.getWorld().playEffect(clone.add(0.02D * i, 0.02D * j, 0.02D * k), Effect.TILE_BREAK, 2);
						clone.getWorld().playEffect(clone.add(0.8D * i, 0.8D * j, 0.8D * k), Effect.EXPLOSION_LARGE, 2);
					}
				}
			}

			// Play sounds
			this.location.getWorld().playSound(this.location, EnumCommon.getEnumValueOf(Sound.class, "EXPLODE", "ENTITY_GENERIC_EXPLODE"), 0.5F, (float) (0.25D + Math.random() * 0.4D));
			this.location.getWorld().playSound(this.location, EnumCommon.getEnumValueOf(Sound.class, "EXPLODE", "ENTITY_GENERIC_EXPLODE"), 0.5F, (float) (0.25D + Math.random() * 0.4D));
			this.location.getWorld().playSound(this.location, EnumCommon.getEnumValueOf(Sound.class, "EXPLODE", "ENTITY_GENERIC_EXPLODE"), 0.5F, (float) (0.25D + Math.random() * 0.4D));

			// Create a supply drop chest
			SupplyDrop supplyDrop = new SupplyDrop(this.location);
			SurvivalGames.getInstance().getSGGame().addSupplyDropChest(this.location, supplyDrop);

			Gberry.broadcastMessageNoBalance(MPG.MPG_PREFIX + ChatColor.YELLOW + "A supply drop has dropped at "
					+ ChatColor.GREEN + "(" + this.location.getBlockX() + ", " + this.location.getBlockY() + ", "
					+ this.location.getBlockZ() + ")" + ChatColor.YELLOW + "!");

			// Mark time as passed
			SupplyDropTask.this.supplyDropTimes.put(this.dropTime, true);

			// Cancel task
			this.cancel();
		}

	}

	public class SupplyDrop {

		private static final int CLICKS_TO_OPEN = 50;
		private static final long CLICK_DELAY = 100L;

		private long lastClick = 0L;
		private int clicksToOpen = SupplyDrop.CLICKS_TO_OPEN;

		private Location location;
		private Inventory inventory;

		public SupplyDrop(Location location) {
			this.location = location;

			// Set beacon block to ender chest
			location.getBlock().setType(Material.ENDER_CHEST);

			// Create a tier 3 inventory
			this.inventory = SurvivalGames.getInstance().getServer().
					createInventory(null, 27, ChatColor.GOLD + ChatColor.BOLD.toString() + "Supply Drop (Tier 3)");

			// Fill inventory
			SurvivalGames.getInstance().getSGGame().fillChest(this.inventory, 3);
		}

		public void clickChest() {
			if (System.currentTimeMillis() >= this.lastClick + SupplyDrop.CLICK_DELAY) {
				// Decrement clicks
				this.clicksToOpen--;

				// Need more clicks still?
				if (this.clicksToOpen > 0) {
					// Play sound
					this.location.getWorld().playSound(this.location, EnumCommon.getEnumValueOf(Sound.class, "ITEM_BREAK", "ENTITY_ITEM_BREAK"), 1.4F, 1.4F);
					//this.location.getWorld().playSound(this.location, Sound.ITEM_BREAK, 1.4F, 1.4F);

					// Set time clicked
					this.lastClick = System.currentTimeMillis();
				} else {
					// Spawn particles
					for (int i = 0; i < 5; i++) {
						for (int j = 0; j < 5; j++) {
							Location clone = this.location.clone();
							clone.getWorld().playEffect(clone.add(0.02D * i, 0, 0.02D * j), Effect.TILE_BREAK, 155);
						}
					}

					// Play sound
					this.location.getWorld().playSound(this.location, EnumCommon.getEnumValueOf(Sound.class, "ITEM_BREAK", "ENTITY_ITEM_BREAK"), 1.4F, 0.4F);
					this.location.getWorld().playSound(this.location, EnumCommon.getEnumValueOf(Sound.class, "ITEM_BREAK", "ENTITY_ITEM_BREAK"), 1.4F, 0.4F);
					this.location.getWorld().playSound(this.location, EnumCommon.getEnumValueOf(Sound.class, "ITEM_BREAK", "ENTITY_ITEM_BREAK"), 1.4F, 0.4F);

					// Set block to chest
					this.location.getBlock().setType(Material.CHEST);
				}
			}
		}

		public Location getLocation() {
			return location;
		}

		public Inventory getInventory() {
			return inventory;
		}

	}

}
