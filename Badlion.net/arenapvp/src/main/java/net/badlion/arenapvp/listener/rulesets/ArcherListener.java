package net.badlion.arenapvp.listener.rulesets;

import net.badlion.arenacommon.event.KitLoadEvent;
import net.badlion.arenacommon.rulesets.BuildUHCRuleSet;
import net.badlion.arenacommon.rulesets.CustomRuleSet;
import net.badlion.arenacommon.rulesets.DiamondOCNRuleSet;
import net.badlion.arenacommon.rulesets.IronBuildUHCRuleSet;
import net.badlion.arenacommon.rulesets.IronOCNRuleSet;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenacommon.rulesets.SGRuleSet;
import net.badlion.arenacommon.util.ItemStackUtil;
import net.badlion.arenapvp.state.MatchState;
import net.badlion.gberry.utils.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;

public class ArcherListener implements Listener {

	// NOTE: THIS RUNS FOR EVERY KIT LOADED
	// NOTE: THIS RUNS FOR EVERY KIT LOADED
	@EventHandler
	public void onGlobalKitLoadEvent(KitLoadEvent event) {
		if (!(event.getKitRuleSet() instanceof CustomRuleSet)) {
			boolean arrowsFound = false;
			for (ItemStack itemStack : event.getPlayer().getInventory().getContents()) {
				if (itemStack == null) continue;

				// Arrows
				if (itemStack.getType() == Material.ARROW) {
					if (event.getKitRuleSet() instanceof SGRuleSet) {
						if (!arrowsFound) {
							arrowsFound = true;

							itemStack.setAmount(8);
						} else {
							event.getPlayer().getInventory().remove(itemStack);
						}
					} else if (event.getKitRuleSet() instanceof IronOCNRuleSet) {
						if (!arrowsFound) {
							arrowsFound = true;

							itemStack.setAmount(32);
						} else {
							event.getPlayer().getInventory().remove(itemStack);
						}
					} else if (event.getKitRuleSet() instanceof DiamondOCNRuleSet) {
						if (!arrowsFound) {
							arrowsFound = true;

							itemStack.setAmount(32);
						} else {
							event.getPlayer().getInventory().remove(itemStack);
						}
					} else if (event.getKitRuleSet() instanceof BuildUHCRuleSet) {
						if (!arrowsFound) {
							arrowsFound = true;

							itemStack.setAmount(64);
						} else {
							event.getPlayer().getInventory().remove(itemStack);
						}
					} else if (event.getKitRuleSet() instanceof IronBuildUHCRuleSet) {
						if (!arrowsFound) {
							arrowsFound = true;

							itemStack.setAmount(20);
						} else {
							event.getPlayer().getInventory().remove(itemStack);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onKitLoadEvent(KitLoadEvent event) {
		if (KitRuleSet.archerRuleSet == event.getKitRuleSet()) {
			for (ItemStack itemStack : event.getPlayer().getInventory().getContents()) {
				if (itemStack == null) continue;

				if (itemStack.getType() == Material.BOW) {
					ItemStackUtil.addUnbreaking(itemStack);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerFall(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
				if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.archerRuleSet)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerHeal(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.archerRuleSet)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LAST, ignoreCancelled = true)
	public void onArrowHit(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player damaged = (Player) event.getEntity();
			if (MatchState.playerIsInMatchAndUsingRuleSet(damaged, KitRuleSet.archerRuleSet)) {

				if (event.getDamager() instanceof Arrow) {
					if (((Arrow) event.getDamager()).getShooter() instanceof Player) {
						Player damager = (Player) ((Arrow) event.getDamager()).getShooter();

						// Calculate damage
						double damage;
						double distance = damager.getLocation().distance(damaged.getLocation());
						if (distance > 40) {
							damage = 7.0; // 3.50 hearts
						} else if (distance > 30) {
							damage = 6.0; // 3.00 hearts
						} else if (distance > 22) {
							damage = 5.5; // 2.75 hearts
						} else if (distance > 13) {
							damage = 3.0; // 1.50 hearts
						} else if (distance > 5) {
							damage = 2.0; // 1.00 hearts
						} else {
							damage = 0.5; // 0.25 hearts
						}

						// Was bow not fully charged?
						if (!((Arrow) event.getDamager()).isCritical()) {
							damage *= 0.55D;
						}

						event.setDamage(damage);

						if (damaged != damager && damaged.getHealth() - event.getFinalDamage() > 0) {
							damager.sendMessage(ChatColor.GOLD + damaged.getDisguisedName() + ChatColor.DARK_AQUA + " is now at " + ChatColor.GOLD +
									Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2D + " " + MessageUtil.HEART_WITH_COLOR);
						}
					}
				} else {
					if (event.getDamager() instanceof Player) {
						((Player) event.getDamager()).sendFormattedMessage("{0}Cannot punch other players while in archer.", ChatColor.RED);
					}
					event.setCancelled(true);
				}
			}
		}
	}


}