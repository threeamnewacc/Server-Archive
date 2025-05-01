package net.badlion.gfactions.listeners;

import net.badlion.gfactions.GFactions;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Map;

public class EXPListener implements Listener {

	private GFactions plugin;

	public EXPListener(GFactions plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		int xp = event.getDroppedExp();
		int newXP = xp + xp; // 2x
		if (event.getEntity().getKiller() != null) {
			Player player = event.getEntity().getKiller();
			Map<Enchantment, Integer> enchantments = player.getItemInHand().getEnchantments();
			if (enchantments.containsKey(Enchantment.LOOT_BONUS_MOBS)) {
				int multiplier = enchantments.get(Enchantment.LOOT_BONUS_MOBS);
				newXP += xp * multiplier;
			}
		}
		event.setDroppedExp(newXP);
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		int xp = event.getExpToDrop();
		int newXP = xp;// + xp; // 2x
		if (event.getPlayer() != null) {
			Player player = event.getPlayer();
			if (player.getItemInHand() != null) {
				Map<Enchantment, Integer> enchantments = player.getItemInHand().getEnchantments();
				if (enchantments.containsKey(Enchantment.LOOT_BONUS_BLOCKS)) {
					int multiplier = enchantments.get(Enchantment.LOOT_BONUS_BLOCKS);
					// Only certain ores do we give extra XP
					if (event.getBlock().getType() == Material.LAPIS_ORE || event.getBlock().getType() == Material.REDSTONE_ORE
								|| event.getBlock().getType() == Material.EMERALD_ORE || event.getBlock().getType() == Material.DIAMOND_ORE) {
						newXP += xp * multiplier;
					}
				}
			}
		}
		event.setExpToDrop(newXP);
	}

}
