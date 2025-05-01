package net.badlion.arenapvp.listener.rulesets;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenapvp.state.MatchState;
import net.badlion.gberry.utils.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class UHCListener implements Listener {

	@EventHandler(priority = EventPriority.LAST, ignoreCancelled = true)
	public void onArrowHit(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
			Player damaged = (Player) event.getEntity();

			if (MatchState.playerIsInMatchAndUsingRuleSet(damaged, KitRuleSet.uhcRuleSet)) {
				if (((Arrow) event.getDamager()).getShooter() instanceof Player) {
					Player damager = (Player) ((Arrow) event.getDamager()).getShooter();

					if (damaged != damager && damaged.getHealth() - event.getFinalDamage() > 0) {
						damager.sendMessage(ChatColor.GOLD + damaged.getDisguisedName() + ChatColor.DARK_AQUA + " is now at " + ChatColor.GOLD +
								Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2D + " " + MessageUtil.HEART_WITH_COLOR);
					}
				}
			}
		}
	}

	@EventHandler
	public void onHealthRegenEvent(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();

			if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.uhcRuleSet)) {
				if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
					event.setCancelled(true);
				}
			}
		}
	}


}
