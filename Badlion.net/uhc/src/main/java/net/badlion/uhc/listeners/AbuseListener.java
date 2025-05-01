package net.badlion.uhc.listeners;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.tasks.GameTimeTask;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.*;

public class AbuseListener implements Listener {

	public static final int TOO_MANY_DIAMONDS_MINED_IN_10_MINUTES = 10;
	public static final int TOO_MANY_GOLD_MINED_IN_10_MINUTES = 45;

    private Map<UUID, Long> lastDiamondWarning = new HashMap<>();
    private Map<UUID, Long> lastGoldWarning = new HashMap<>();

	private Map<UUID, HashSet<Long>> mapOfGoldMined = new HashMap<>();
	private Map<UUID, HashSet<Long>> mapOfDiamondMined = new HashMap<>();

	private Set<UUID> minedDiamonds = new HashSet<>();

	@EventHandler
	public void onPlayerMine(final BlockBreakEvent event) {
		final Player player = event.getPlayer();

		if (event.getBlock().getType() != Material.DIAMOND_ORE && event.getBlock().getType() != Material.GOLD_BLOCK) {
			return;
		}

		if (event.getBlock().getType() == Material.DIAMOND_ORE) {
			HashSet<Long> list = this.mapOfDiamondMined.get(player.getUniqueId());
			if (list == null) {
				list = new HashSet<>();
				this.mapOfDiamondMined.put(player.getUniqueId(), list);
			}

			HashSet<Long> copy = new HashSet<>(list);
			for (Long time : copy) {
				// Been 10 min
				if (time + 600000 < System.currentTimeMillis()) {
					list.remove(time);
				}
			}

			// If they mined a diamond within 20 mins, notify staff
			if (!minedDiamonds.contains(player.getUniqueId()) && GameTimeTask.getNumOfSeconds() <= 60 * 5) {
				minedDiamonds.add(player.getUniqueId());
				BadlionUHC.getInstance().getServer().dispatchCommand(BadlionUHC.getInstance().getServer().getConsoleSender(),
						"miningnotification [WARNING]: " + player.getDisguisedName() + " found diamonds before 5 minutes!");
			}

			list.add(System.currentTimeMillis());

			// Too much?
			if (list.size() >= AbuseListener.TOO_MANY_DIAMONDS_MINED_IN_10_MINUTES) {
                Long lastTime = this.lastDiamondWarning.get(player.getUniqueId());

                if (lastTime == null || lastTime + 30000 < System.currentTimeMillis()) {
                    this.lastDiamondWarning.put(player.getUniqueId(), System.currentTimeMillis());
				    BadlionUHC.getInstance().getServer().dispatchCommand(BadlionUHC.getInstance().getServer().getConsoleSender(),
					    	"miningnotification [WARNING]: " + player.getDisguisedName() + " is mining diamonds quickly!");
                }
			}
		} else if (event.getBlock().getType() == Material.GOLD_ORE) {
			HashSet<Long> list = this.mapOfGoldMined.get(player.getUniqueId());
			if (list == null) {
				list = new HashSet<>();
				this.mapOfGoldMined.put(player.getUniqueId(), list);
			}

			HashSet<Long> copy = new HashSet<>(list);
			for (Long time : copy) {
				// Been 10 min
				if (time + 600000 < System.currentTimeMillis()) {
					list.remove(time);
				}
			}

			list.add(System.currentTimeMillis());

			// Too much?
			if (list.size() >= AbuseListener.TOO_MANY_GOLD_MINED_IN_10_MINUTES) {
                Long lastTime = this.lastGoldWarning.get(player.getUniqueId());

                if (lastTime == null || lastTime + 30000 < System.currentTimeMillis()) {
                    this.lastGoldWarning.put(player.getUniqueId(), System.currentTimeMillis());
                    BadlionUHC.getInstance().getServer().dispatchCommand(BadlionUHC.getInstance().getServer().getConsoleSender(),
                            "miningnotification [WARNING]: " + player.getDisguisedName() + " is mining gold quickly!");
                }
			}
		}
	}

}
