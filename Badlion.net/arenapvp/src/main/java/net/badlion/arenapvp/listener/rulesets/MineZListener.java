package net.badlion.arenapvp.listener.rulesets;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenacommon.rulesets.MineZRuleSet;
import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.state.MatchState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MineZListener implements Listener {

	private HashSet<Material> types = new HashSet<>();
	private Map<Material, Integer> mappedStacks = new HashMap<>();

	@EventHandler
	public void healthRegen(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();

			if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.minezRuleSet)) {
				if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		Player player = event.getPlayer();

		if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.minezRuleSet)) {
			if (KitRuleSet.minezRuleSet.canBreakBlock(event.getBlock())) {
				if (event.getBlock().getType() == Material.WEB) {
					event.setCancelled(false);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.minezRuleSet)) {
			if (KitRuleSet.minezRuleSet.canBreakBlock(event.getBlock())) {
				event.setCancelled(false);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onItemStacked(InventoryClickEvent event) {
		// Clicking out of bounds
		if (event.getClickedInventory() == null) {
			return;
		}

		InventoryHolder ih = event.getClickedInventory().getHolder();

		if (ih instanceof Player) {
			final Player player = (Player) ih;

			if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.minezRuleSet)) {
				ItemStack current = event.getCurrentItem();
				ItemStack cursor = event.getCursor();

				if (current != null && cursor != null) {
					// Try something different an less efficient cuz i don't got time for this
					ArenaPvP.getInstance().getServer().getScheduler().runTaskLater(ArenaPvP.getInstance(), new Runnable() {
						public void run() {
							// Initialize temp storage
							ArrayList<Integer> emptySlots = new ArrayList<Integer>();
							Map<Material, Integer> excess = new HashMap<Material, Integer>();
							for (Material m : MineZRuleSet.types) {
								excess.put(m, 0);
							}

							int i = 0;
							for (ItemStack item : player.getInventory()) {
								if (item == null || item.getType() == Material.AIR) {
									emptySlots.add(i++);
									continue;
								}

								// Check ofr anything that shouldn't be stacked past a point
								if (MineZRuleSet.types.contains(item.getType())) {
									if (item.getAmount() > MineZRuleSet.mappedStacks.get(item.getType())) {
										int diff = item.getAmount() - MineZRuleSet.mappedStacks.get(item.getType());
										excess.put(item.getType(), excess.get(item.getType()) + diff);
										item.setAmount(MineZRuleSet.mappedStacks.get(item.getType()));
									}
								}

								i++;
							}

							for (Map.Entry<Material, Integer> entry : excess.entrySet()) {
								if (entry.getValue() > 0) {
									if (emptySlots.size() == 0) {
										continue;
									}

									Integer tmp = emptySlots.remove(0);
									if (tmp != null) {
										// Less than our max value
										if (MineZRuleSet.mappedStacks.get(entry.getKey()) >= entry.getValue()) {
											player.getInventory().setItem(tmp, new ItemStack(entry.getKey(), entry.getValue()));
										} else if (entry.getValue() > MineZRuleSet.mappedStacks.get(entry.getKey())) {
											// Do it as many times as we need to to get rid of this inventory...
											int total = entry.getValue();
											boolean hasDone = false;
											do {
												if (hasDone) {
													if (emptySlots.size() > 0) {
														tmp = emptySlots.remove(0);
													} else {
														return; // glitch?
													}
												}

												// Ok we can start getting new ones on the next iteration
												hasDone = true;

												// Make sure we have a slot
												if (tmp != null) {
													// If we have more than 16 arrows, then only do 16 in this area, if we have less than only do that amount
													int remaining = total > MineZRuleSet.mappedStacks.get(entry.getKey()) ? MineZRuleSet.mappedStacks.get(entry.getKey()) : total;
													player.getInventory().setItem(tmp, new ItemStack(entry.getKey(), remaining));
													total -= remaining;
												} else {
													return; // out of spots?
												}
											} while (total > 0);
										}
									} else {
										// Done..no more slots?
										return;
									}
								}
							}

							player.updateInventory();
						}
					}, 1);
				}
			}
		}
	}

}
