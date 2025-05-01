package net.badlion.arenapvp.listener.rulesets;

import net.badlion.arenacommon.event.KitLoadEvent;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenacommon.util.ItemStackUtil;
import net.badlion.arenapvp.state.MatchState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.inventory.ItemStack;

public class SGListener implements Listener {

	@EventHandler
	public void onKitLoadEvent(KitLoadEvent event) {
		if (KitRuleSet.sgRuleSet == event.getKitRuleSet()) {
			for (ItemStack itemStack : event.getPlayer().getInventory().getContents()) {
				if (itemStack == null) continue;

				if (itemStack.getType() == Material.FISHING_ROD) {
					itemStack.setDurability((short) 0);
				} else if (itemStack.getType() == Material.FLINT_AND_STEEL) {
					// Reset durability since now we do the 1/3rd thing
					itemStack.setDurability((short) 0);
				}
			}

			ItemStackUtil.addUnbreakingToArmor(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.LAST, ignoreCancelled = true)
	public void onBlockIgniteEvent(BlockIgniteEvent event) {
		if (event.getCause() != BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) return;

		Player player = event.getPlayer();

		if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.sgRuleSet)) {
			ItemStack item = player.getItemInHand();

			// Two uses per flint and steel, reduce durability by 32 per use
			item.setDurability((short) (item.getDurability() + 32));
		}
	}


}
