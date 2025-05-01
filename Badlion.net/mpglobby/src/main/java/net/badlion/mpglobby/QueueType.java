package net.badlion.mpglobby;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class QueueType {

	public enum GameType {
		FFA,
		PARTY;
	}

	private static final List<QueueType> queueTypes = new ArrayList<>();

	private String name;
	private GameType gameType;
	private String ladder;

	private Material itemMaterial;

	private Block sign;
	private ItemStack item;

	private int inQueuePlayerCount = 0;
	private int inGamePlayerCount = 0;

	public QueueType(String name, GameType gameType, String ladder, Material itemMaterial, Location signLocation) {
		this.name = name;
		this.gameType = gameType;
		this.ladder = ladder;

		this.itemMaterial = itemMaterial;

		// Make sure the sign chunk is loaded
		signLocation.getChunk().load();

		this.sign = signLocation.getBlock();

		QueueType.queueTypes.add(this);
	}

	@Override
	public String toString() {
		return this.name;
	}

	public static List<QueueType> values() {
		return QueueType.queueTypes;
	}

	public String getName() {
		return this.name;
	}

	public GameType getGameType() {
		return this.gameType;
	}

	public String getLadder() {
		return this.ladder;
	}

	public Material getItemMaterial() {
		return this.itemMaterial;
	}

	public Block getSign() {
		return this.sign;
	}

	public ItemStack getItem() {
		return this.item;
	}

	public void setItem(ItemStack item) {
		this.item = item;

		// Update lore
		ItemStackUtil.setLore(QueueType.this.item, ChatColor.GOLD.toString() + QueueType.this.inQueuePlayerCount + " in queue",
				"", ChatColor.YELLOW + "Click to join");
	}

	public int getInQueuePlayerCount() {
		return this.inQueuePlayerCount;
	}

	public int getInGamePlayerCount() {
		return this.inGamePlayerCount;
	}

	/**
	 * Note: This is always called ASYNCHRONOUSLY.
	 */
	public void setPlayerCount(int inQueuePlayerCount, int inGamePlayerCount) {
		this.inQueuePlayerCount = inQueuePlayerCount;
		this.inGamePlayerCount = inGamePlayerCount;

		BukkitUtil.runTask(new Runnable() {
			@Override
			public void run() {
				// Update player count in item lore
				ItemStackUtil.setLore(QueueType.this.item, ChatColor.GOLD.toString() + QueueType.this.inQueuePlayerCount + " in queue",
						ChatColor.GOLD.toString() + QueueType.this.inGamePlayerCount + " in game", "", ChatColor.YELLOW + "Click to join");

				Sign sign = (Sign) QueueType.this.sign.getState();

				// Update player count for sign
				sign.setLine(0, ChatColor.AQUA + QueueType.this.name);
				sign.setLine(1, ChatColor.DARK_GREEN + "" + QueueType.this.inQueuePlayerCount + " in queue");
				sign.setLine(2, ChatColor.DARK_GREEN + "" + QueueType.this.inGamePlayerCount + " in game");
				sign.setLine(3, ChatColor.GOLD + "Click to join");

				sign.update();
			}
		});
	}

}