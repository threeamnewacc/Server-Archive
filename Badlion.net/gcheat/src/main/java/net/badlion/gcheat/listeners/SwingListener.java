package net.badlion.gcheat.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.gcheat.GCheat;
import net.badlion.gcheat.bukkitevents.GCheatGameEndEvent;
import net.badlion.gcheat.bukkitevents.GCheatGameStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAttackEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SwingListener implements Listener {

	private Map<UUID, Long> hitTimes = new HashMap<>();

	private Map<UUID, SwingTracker> playerSwingTrackers = new HashMap<>();

	@EventHandler
	public void onGCheatGameStartEvent(GCheatGameStartEvent event) {
		this.createNewSwingTracker(event.getPlayer().getUniqueId(), event.getData());
	}

	@EventHandler
	public void onGCheatGameEndEvent(GCheatGameEndEvent event) {
		SwingTracker swingTracker = this.getSwingTracker(event.getPlayer());
		if (swingTracker != null) {
			// Set data now if the server sends data at the end
			if (event.getData() != null) {
				swingTracker.setData(event.getData());

				// Add this to our records to flush
				GCheat.plugin.addSwingRecord(swingTracker);

				this.playerSwingTrackers.remove(event.getPlayer().getUniqueId());

				// Print out this data for the tournament server
				if (Gberry.serverName.toLowerCase().contains("tournament")) {
					Bukkit.getLogger().info("[GCheat Swings] " + event.getPlayer().getName() + " " + swingTracker.getSwingHitPercentage()
							+ "% Total Hits: " + swingTracker.getHits() + " " + event.getPlayer().getUniqueId());
				}
			}
		} else {
			//throw new IllegalStateException("GCheat swing tracker null for player " + event.getPlayer().getName());
		}
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		SwingTracker swingTracker = this.getSwingTracker(event.getPlayer());
		if (swingTracker != null && this.isUsingSword(event.getPlayer())
				&& (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
			swingTracker.addSwing();

			//this.hitTimes.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
		}
	}

	@EventHandler
	public void onPlayerDamageEvent(PlayerAttackEvent event) {
		SwingTracker swingTracker = this.getSwingTracker(event.getPlayer());
		if (swingTracker != null && this.isUsingSword(event.getPlayer())) {
			swingTracker.addHit();

			/*Long time = this.hitTimes.remove(event.getPlayer().getUniqueId());
			if (System.currentTimeMillis() > time + 10) {
				event.getPlayer().sendMessage("ADDING CUSTOM HIT");
				swingTracker.addSwing();
			}*/
		}

		// do "random" bans while attacking for some things
		if (Math.random() < 1.0 / 250.0) {
			String client = BungeeCordListener.getClientType(event.getPlayer());
			if (VapeListener.vapers.contains(event.getPlayer().getUniqueId())) {
				client = "Hacked Client Type E";
			}
			if (client != null) {
				GCheat.plugin.logMessage(event.getPlayer(), event.getPlayer().getName() + " was caught using " + client);
				if (GCheat.bannedUUIDS.add(event.getPlayer().getUniqueId())) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + event.getPlayer().getName() + " [GCheat] Unfair Advantage");
				}
			}
		}
	}

	private void createNewSwingTracker(UUID uuid, Map<String, Object> data) {
		this.playerSwingTrackers.put(uuid, new SwingTracker(uuid, data));
	}

	private SwingTracker getSwingTracker(Player player) {
		return this.playerSwingTrackers.get(player.getUniqueId());
	}

	private boolean isUsingSword(Player player) {
		ItemStack itemInHand = player.getItemInHand();
		if (itemInHand != null) {
			switch (itemInHand.getType()) {
				case WOOD_SWORD:
				case STONE_SWORD:
				case GOLD_SWORD:
				case IRON_SWORD:
				case DIAMOND_SWORD:
					return true;
			}
		}

		return false;
	}

	public class SwingTracker {

		private UUID uuid;

		private int hits = 0;
		private int swings = 0;

		private Map<String, Object> data;

		public SwingTracker(UUID uuid, Map<String, Object> data) {
			this.uuid = uuid;

			this.data = data;
		}

		public UUID getUniqueId() {
			return uuid;
		}

		public void addHit() {
			this.hits++;
		}

		public int getHits() {
			return hits;
		}

		public void addSwing() {
			this.swings++;
		}

		public int getSwings() {
			return swings;
		}

		public double getSwingHitPercentage() {
			if (this.swings == 0) {
				return 0;
			}

			return (this.hits / (double) this.swings) * 100D;
		}

		public Map<String, Object> getData() {
			return data;
		}

		public void setData(Map<String, Object> data) {
			this.data = data;
		}

	}

}
