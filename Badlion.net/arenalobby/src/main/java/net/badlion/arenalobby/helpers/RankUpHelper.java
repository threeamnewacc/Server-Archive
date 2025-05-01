package net.badlion.arenalobby.helpers;

import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.managers.ArenaSettingsManager;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.FireWorkUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.gberry.utils.RatingUtil;
import net.badlion.libs.com.google.common.cache.Cache;
import net.badlion.libs.com.google.common.cache.CacheBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RankUpHelper {

	private static Cache<UUID, GainLosePoints> gainedLostPointsMap = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build();

	public static void addGainedLostPoints(JSONObject jsonObject) {
		UUID uuid = UUID.fromString((String) jsonObject.get("uuid"));
		String type = (String) jsonObject.get("type");
		Long points = (Long) jsonObject.get("points");
		if (type.equalsIgnoreCase("win")) {
			RankUpHelper.gainedLostPointsMap.put(uuid, new GainLosePoints(PointType.WIN, points.intValue()));
		} else if (type.equalsIgnoreCase("loss")) {
			RankUpHelper.gainedLostPointsMap.put(uuid, new GainLosePoints(PointType.LOSS, points.intValue()));
		}
	}

	public static void handlePlayerJoin(Player player) {
		RankUpHelper.gainedLostPointsMap.cleanUp();

		if (ArenaSettingsManager.getSettings(player).showsTitles()) {
			if (RankUpHelper.gainedLostPointsMap.asMap().containsKey(player.getUniqueId())) {
				
				GainLosePoints title = RankUpHelper.gainedLostPointsMap.asMap().remove(player.getUniqueId());
				player.sendTitle(new ComponentBuilder(title.buildString()).bold(true).create());
				player.setTitleTimes(0, 40, 10);
			}
		}
	}

	public enum PointType {
		WIN(ChatColor.GREEN + "Won"), LOSS(ChatColor.RED + "Lost");

		private String name;

		PointType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	private static class GainLosePoints {

		private PointType pointType;
		private int points;

		GainLosePoints(PointType pointType, int points) {
			this.pointType = pointType;
			this.points = points;
		}

		public String buildString() {
			return ChatColor.GOLD + "You " + pointType.getName() + ChatColor.GOLD + " " + points + " points";
		}

	}


	public static void handleRankedUp(final Player player, final RatingUtil.Rank newRank) {
		// TODO: Add broadcast
		//Gberry.broadcastMessage(ChatColor.GOLD + player.getDisguisedName() + " has ranked up to " + newRank.getChatColor() + newRank.getName() + "!");

		for (int i = 0; i < 40; i++) {
			final int finalI = i;
			new BukkitRunnable() {
				@Override
				public void run() {

					// Make sure they are online
					if (player == null || !player.isOnline()) {
						return;
					}

					Material material = newRank.getType();
					if (finalI % 5 == 0) {
						FireWorkUtil.shootFirework(player.getLocation(), Arrays.asList(newRank.getColors()));
					}

					for (int i = 1; i <= 4; i++) {
						if (newRank.getName().contains("Platinum")) {
							material = Material.IRON_INGOT;
						}
						ItemStack itemStack = ItemStackUtil.createItem(material, String.valueOf(Math.random() * 100.0D));
						if (newRank.getName().contains("Platinum")) {
							itemStack = Gberry.getGlowItem(itemStack);
						}
						Item item = player.getWorld().dropItem(player.getEyeLocation().add(0.0D, 1.0D, 0.0D), itemStack);
						item.setVelocity(new Vector(Math.random() - Math.random(), 0.5D, Math.random() - Math.random()));

						// 4 second life
						item.setAge(5920);

						item.setPickupDelay(Integer.MAX_VALUE);
					}
				}
			}.runTaskLater(ArenaLobby.getInstance(), i * 2);
		}
	}
}
