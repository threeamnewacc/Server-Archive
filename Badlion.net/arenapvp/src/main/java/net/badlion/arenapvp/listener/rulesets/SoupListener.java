package net.badlion.arenapvp.listener.rulesets;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenapvp.state.MatchState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SoupListener implements Listener {

	@EventHandler
	public void onPlayerDrinkSoupEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.ironSoupRuleSet)) {
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (event.getItem() != null && event.getItem().getType() == Material.MUSHROOM_SOUP) {
					if (!player.isDead()) {
						if (player.getHealth() < 20) {
							if (player.getHealth() + 7 < 20) {
								player.setHealth(player.getHealth() + 7);
								player.getItemInHand().setType(Material.BOWL);
								player.getItemInHand().setItemMeta(null);
							} else {
								player.setHealth(20);
								player.getItemInHand().setType(Material.BOWL);
								player.getItemInHand().setItemMeta(null);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onHealthRegenEvent(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();

			if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.ironSoupRuleSet)) {
				if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();

			if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.ironSoupRuleSet)) {
				event.setCancelled(true);
			}
		}
	}

}
