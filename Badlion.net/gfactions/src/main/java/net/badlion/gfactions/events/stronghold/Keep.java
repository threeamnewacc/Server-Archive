package net.badlion.gfactions.events.stronghold;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import net.badlion.gfactions.GFactions;
import net.badlion.smellyloot.managers.LootManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Lever;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Keep {

	private static List<Keep> keeps = new ArrayList<>();

	private String name;
	private boolean capped = false;
	private boolean lootEnabled;

	private Faction previousOwner;
	private Faction owner;
	private Boolean deteriorationApplied;
	private int deteriorationMoneyOwed;
	private Map<ItemStack, Integer> deteriorationItemsOwed = new HashMap<>();

	private Location corner1;
	private Location corner2;
	private Location deteriorationSign;

	private double totalPassiveLootDropChance;

	private Map<Location, LootChest> lootChests = new HashMap<>();
	private List<Integer> lootChestRefillTasks = new ArrayList<>();
	private Map<Block, Door> keepDoors = new HashMap<>();

	private KeepTrackerTask keepTrackerTask;

	public Keep(String name, boolean lootEnabled, double passiveLootDropChanceModifiersSum,
	            Location corner1, Location corner2, Location deteriorationSign) {
		this.name = name;
		this.lootEnabled = lootEnabled;

		this.totalPassiveLootDropChance = passiveLootDropChanceModifiersSum;

		this.corner1 = corner1;
		this.corner2 = corner2;
		this.deteriorationSign = deteriorationSign;

		Keep.keeps.add(this);
	}

	public void addDoor(Material material, byte data, Block lever1, Block lever2, List<Block> blocks) {
		try {
			// Check if they are levers
			Lever leverData = (Lever) lever1.getState().getData();
			leverData = (Lever) lever2.getState().getData();

			// Make sure the lever is set to the off state
			lever1.setData((byte) (lever1.getData() & ~0x8));
			lever2.setData((byte) (lever2.getData() & ~0x8));
		} catch (ClassCastException exception) {
			GFactions.plugin.getLogger().severe("Invalid lever coordinates specified for keep " + this.name + ". Fix config and reload to fix error!!!");
			GFactions.plugin.getLogger().severe("Invalid lever coordinates specified for keep " + this.name + ". Fix config and reload to fix error!!!");
			GFactions.plugin.getLogger().severe("Invalid lever coordinates specified for keep " + this.name + ". Fix config and reload to fix error!!!");
		}

		try {
			// Make sure the door is closed by default
			for (Block block : blocks) {
				block.setType(material);
				block.setData(data);
			}
		} catch (IndexOutOfBoundsException exception) {
			GFactions.plugin.getLogger().severe("No door location specified or invalid coordinates specified for keep " + this.name + ". " +
					"Fix config and reload to fix error!!!");
			GFactions.plugin.getLogger().severe("No door location specified or invalid coordinates specified for keep " + this.name + ". " +
					"Fix config and reload to fix error!!!");
			GFactions.plugin.getLogger().severe("No door location specified or invalid coordinates specified for keep " + this.name + ". " +
					"Fix config and reload to fix error!!!");
		}

		Door door = new Door(material, data, blocks);
		this.keepDoors.put(lever1, door);
		this.keepDoors.put(lever2, door);
	}

	public void addLootChest(Location location) {
		this.lootChests.put(location, new LootChest(location));
	}

	public void startKeepTrackerTask() {
		this.keepTrackerTask = new KeepTrackerTask(this);
		this.keepTrackerTask.runTaskTimer(GFactions.plugin, 0L, 20L);
	}

	public String getName() {
		return name;
	}

	public boolean isCapped() {
		return capped;
	}

	public void setCapped(boolean capped) {
		this.capped = capped;
	}

	public boolean isLootEnabled() {
		return lootEnabled;
	}

	public Faction getPreviousOwner() {
		return previousOwner;
	}

	public Faction getOwner() {
		return owner;
	}

	public void setPreviousOwner(Integer factionId) {
		// Check if last capper faction still exists
		if (factionId != null) {
			this.previousOwner = Factions.i.get(factionId + "");
			this.owner = this.previousOwner;
			if (owner != null) {
				this.totalPassiveLootDropChance = this.totalPassiveLootDropChance
						+ GFactions.plugin.getStrongholdConfig().getPassiveLootDropChanceOverExtendedBuff();
			} else {
				// Update config and clear the owner if faction was disbanded or something
				GFactions.plugin.getStrongholdConfig().setValue("keeps." + this.name + ".balance.last_capper_faction_id", 0);
			}
		} else {
			this.previousOwner = null;
			this.owner = null;
		}
	}

	public void setOwner(Faction owner) {
		this.owner = owner;

		// Calculate total passive loot drop chance
		if (this.owner != null) {
			this.totalPassiveLootDropChance = GFactions.plugin.getStrongholdConfig().getPassiveLootDropChanceModifiersSum()
					+ GFactions.plugin.getStrongholdConfig().getPassiveLootDropChanceOverExtendedBuff();
		} else {
			this.totalPassiveLootDropChance = GFactions.plugin.getStrongholdConfig().getPassiveLootDropChanceModifiersSum();
		}

		// Capper changed, update the capture length modifiers for every keep
		// Sometimes we only need to update a few of them, but update all because easier and may be less laggy
		for (Keep keep : Keep.getKeeps()) {
			if (keep.getKeepTrackerTask() != null) {
				keep.getKeepTrackerTask().updateCaptureLengthModifiers();
			}
		}

		// Update config
		String factionId = this.owner != null ? this.owner.getId() : "0";
		GFactions.plugin.getStrongholdConfig().setValue("keeps." + this.name + ".balance.last_capper_faction_id", factionId);
	}

	public Boolean getDeteriorationApplied() {
		return deteriorationApplied;
	}

	public void checkDeteriorationApplied(Player player) {
		// Deterioration boolean check
		if (this.deteriorationItemsOwed.isEmpty() && this.deteriorationMoneyOwed == 0) {
			this.deteriorationApplied = false;

			if (player != null) {
				player.sendMessage(ChatColor.GREEN + "Your faction has paid off all of its deterioration costs for this keep!");
			}
		} else {
			this.deteriorationApplied = true;

			if (player != null) {
				player.sendMessage(ChatColor.YELLOW + "Your faction still owes deterioration costs for this keep!");
			}
		}
	}

	public int getDeteriorationMoneyOwed() {
		return deteriorationMoneyOwed;
	}

	public void setDeteriorationMoneyOwed(int deteriorationMoneyOwed) {
		this.deteriorationMoneyOwed = deteriorationMoneyOwed;
	}

	public Map<ItemStack, Integer> getDeteriorationItemsOwed() {
		return deteriorationItemsOwed;
	}

	public void setDeteriorationItemsOwed(Map<ItemStack, Integer> deteriorationItemsOwed) {
		this.deteriorationItemsOwed = deteriorationItemsOwed;
	}

	public Location getCorner1() {
		return corner1;
	}

	public Location getCorner2() {
		return corner2;
	}

	public Location getDeteriorationSign() {
		return deteriorationSign;
	}

	public double getTotalPassiveLootDropChance() {
		return totalPassiveLootDropChance;
	}

	public Map<Location, LootChest> getLootChests() {
		return lootChests;
	}

	public List<Integer> getLootChestRefillTasks() {
		return lootChestRefillTasks;
	}

	public Map<Block, Door> getKeepDoors() {
		return keepDoors;
	}

	public KeepTrackerTask getKeepTrackerTask() {
		return keepTrackerTask;
	}

	public void setKeepTrackerTask(KeepTrackerTask keepTrackerTask) {
		this.keepTrackerTask = keepTrackerTask;
	}

	public class Door {

		private boolean closed = true;

		private Material material;
		private byte data;

		private List<Block> blocks;

		public Door(Material material, byte data, List<Block> blocks) {
			this.material = material;
			this.data = data;

			this.blocks = blocks;
		}

		public void toggle() { // TODO: CAN DO A MATERIAL/DATA CHECK HERE FOR MORE ELABORATE DOOR DESIGNS
			if (this.closed) {
				for (Block block : this.blocks) {
					block.setType(Material.AIR);
				}
			} else {
				for (Block block : this.blocks) {
					block.setType(this.material);
					block.setData(this.data);
				}
			}

			this.closed = !this.closed;
		}

		public boolean isClosed() {
			return closed;
		}

	}

	public class LootChest {

		private Location location;

		public LootChest(Location location) {
			this.location = location;

			// Fill the loot chest
			LootManager.dropEventLootChest("stronghold", this.location);
		}

		public Location getLocation() {
			return location;
		}

	}

	public static List<Keep> getKeeps() {
		return Keep.keeps;
	}

	public static void flushKeeps() {
		for (Keep keep : Keep.keeps) {
			if (keep.getKeepTrackerTask() != null) {
				keep.getKeepTrackerTask().cancel();
			}

			// Cancel all loot chest refill tasks
			for (Integer taskId : keep.getLootChestRefillTasks()) {
				GFactions.plugin.getServer().getScheduler().cancelTask(taskId);
			}
		}

		Keep.keeps.clear();
	}

}
