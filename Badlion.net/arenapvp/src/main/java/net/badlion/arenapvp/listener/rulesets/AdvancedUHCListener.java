package net.badlion.arenapvp.listener.rulesets;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenapvp.state.MatchState;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class AdvancedUHCListener implements Listener {

	@EventHandler
	public void healthRegen(EntityRegainHealthEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.advancedUHCRuleSet)) {
				if (e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LAST, ignoreCancelled = true)
	public void onArrowHit(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
			Player damaged = (Player) event.getEntity();
			if (MatchState.playerIsInMatchAndUsingRuleSet(damaged, KitRuleSet.advancedUHCRuleSet)) {
				if (((Arrow) event.getDamager()).getShooter() instanceof Player) {
					Player damager = (Player) ((Arrow) event.getDamager()).getShooter();

					if (damaged != damager && damaged.getHealth() - event.getFinalDamage() > 0) {
						damager.sendFormattedMessage("{0} is now at {1}",
								ChatColor.GOLD + damaged.getDisguisedName() + ChatColor.DARK_AQUA,
								ChatColor.GOLD.toString() + Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2D + " " + MessageUtil.HEART_WITH_COLOR);
					}
				}
			}
		}
	}

	@EventHandler
	public void onEatGoldenHead(PlayerItemConsumeEvent event) {
		ItemStack item = event.getItem();

		final Player player = event.getPlayer();
		if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.advancedUHCRuleSet)) {
			if (item.getType().equals(Material.GOLDEN_APPLE)) {
				BukkitUtil.runTaskNextTick(new Runnable() {
					@Override
					public void run() {
						player.removePotionEffect(PotionEffectType.ABSORPTION);
					}
				});
			}
		}
	}

}
