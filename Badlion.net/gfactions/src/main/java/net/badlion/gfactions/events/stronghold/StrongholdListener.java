package net.badlion.gfactions.events.stronghold;

import com.massivecraft.factions.event.PowerLossEvent;
import net.badlion.gberry.Gberry;
import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.bukkitevents.DeathBanEvent;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gguard.ProtectedRegion;
import net.badlion.smellyloot.SmellyLoot;
import net.badlion.smellyloot.managers.LootManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StrongholdListener implements Listener {

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		// Are they in a stronghold region?
		ProtectedRegion region = GFactions.plugin.getgGuardPlugin().getProtectedRegion(event.getPlayer().getLocation(),
				GFactions.plugin.getgGuardPlugin().getProtectedRegions());
		if (region != null && GFactions.plugin.getStrongholdConfig().getEventRegions().contains(region.getRegionName())) {
			event.getPlayer().teleport(GFactions.plugin.getSpawnLocation());
		}
	}

	@EventHandler
	public void onPlayerToggleKeepDoor(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			for (Keep keep : Keep.getKeeps()) {
				Keep.Door door = keep.getKeepDoors().get(event.getClickedBlock());
				if (door != null) {
                    if (GFactions.plugin.getStronghold() == null) {
                        // Are they a member of the owning faction?
                        if (keep.getOwner() != null && keep.getOwner().getOnlinePlayers().contains(event.getPlayer())) {
                            door.toggle();
                        } else {
                            event.getPlayer().sendMessage(ChatColor.RED + "Only the controlling faction of this keep can control the door!");
                            event.setCancelled(true);
                        }
                        break;
                    } else {
                        event.getPlayer().sendMessage(ChatColor.RED + "The doors cannot be controlled when the Stronghold Event is running");
                        event.setCancelled(true);
                        break;
                    }
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteractLootChestEvent(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.CHEST) {
			Location location = event.getClickedBlock().getLocation();

			// Was this a keep's loot chest?
			for (final Keep keep : Keep.getKeeps()) {
				final Keep.LootChest lootChest = keep.getLootChests().get(location);
				if (lootChest != null) {
					// Drop items from chest
					SmellyLoot.dropItemsFromChest(event);

					// Run task to refill the chest
					int id = new BukkitRunnable() {
						@Override
						public void run() {
							// Is loot enabled?
							if (!GFactions.plugin.getStrongholdConfig().isLootEnabled() || !keep.isLootEnabled()) {
								return; // Don't cancel task because we want to be able to enable it w/o a reboot
							}

							// Random chance to spawn loot
							if (Math.random() <= keep.getTotalPassiveLootDropChance() && keep.getTotalPassiveLootDropChance() != 0) {
								LootManager.dropEventLootChest("stronghold", lootChest.getLocation());
								this.cancel();
							}
						}
					}.runTaskTimer(GFactions.plugin, GFactions.plugin.getStrongholdConfig().getPassiveLootDropInterval() * 20L,
							GFactions.plugin.getStrongholdConfig().getPassiveLootDropInterval() * 20L).getTaskId();


					break;
				}
			}
		}
	}

	@EventHandler
	public void onPlayerPayDeteriorationEvent(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null && (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			Location location = event.getClickedBlock().getLocation();
			for (final Keep keep : Keep.getKeeps()) {
				if (location.equals(keep.getDeteriorationSign())) {
					// Is a stronghold event not running?
					if (GFactions.plugin.getStronghold() == null) {
						// Are they a member of the capping faction?
						if (keep.getOwner().getOnlinePlayers().contains(event.getPlayer())) {
							Player player = event.getPlayer();

							if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
								if (!keep.getDeteriorationApplied()) {
									player.sendMessage(ChatColor.GREEN + "Your faction has paid off all of its deterioration costs for this keep!");
									return;
								}

								if (keep.getDeteriorationMoneyOwed() > 0) {
									player.sendMessage(ChatColor.YELLOW + "Your faction still owes $" + keep.getDeteriorationMoneyOwed() + ".");
								}

								StringBuilder sb = new StringBuilder(ChatColor.YELLOW + "Your faction still owes ");
								if (!keep.getDeteriorationItemsOwed().isEmpty()) {
									for (ItemStack itemType : keep.getDeteriorationItemsOwed().keySet()) {
										sb.append(keep.getDeteriorationItemsOwed().get(itemType));
										sb.append(" ");
										sb.append(itemType.getType().toString().toLowerCase().replaceAll("_", " "));
									}
									sb.append(".");

									player.sendMessage(sb.toString());
								}
							} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
								// Handle money stuff first
								if (keep.getDeteriorationMoneyOwed() > 0) {
									int available = GFactions.plugin.getArchMoney().checkBalance(player.getUniqueId().toString());
									if (available > keep.getDeteriorationMoneyOwed()) {
										GFactions.plugin.getArchMoney().changeBalance(player.getUniqueId().toString(), -1 * keep.getDeteriorationMoneyOwed(), "Deterioration pay");
										player.sendMessage(ChatColor.GREEN + "You have paid off $" + keep.getDeteriorationMoneyOwed() + ".");
										keep.setDeteriorationMoneyOwed(0);
									} else if (available != 0) {
										GFactions.plugin.getArchMoney().changeBalance(player.getUniqueId().toString(), -1 * available, "Deterioration pay");
										player.sendMessage(ChatColor.GREEN + "You have paid off $" + available + ".");
										keep.setDeteriorationMoneyOwed(available);
									}

									// Update database
									if (available != 0) {
										BukkitUtil.runTaskAsync(new Runnable() {
											@Override
											public void run() {
												String query = "UPDATE " + GFactions.PREFIX + "_stronghold_deterioration SET money_owed = ? WHERE stronghold_id = ? AND keep_name = ?;";

												Connection connection = null;
												PreparedStatement ps = null;

												try {
													connection = Gberry.getConnection();
													ps = connection.prepareStatement(query);

													ps.setInt(1, keep.getDeteriorationMoneyOwed());
													ps.setInt(2, GFactions.plugin.getStrongholdConfig().getStrongholdEventId() - 1);
													ps.setString(3, keep.getName());

													Gberry.executeUpdate(connection, ps);
												} catch (SQLException e) {
													e.printStackTrace();
												} finally {
													if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
													if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
												}
											}
										});
									}
								}

								// Pay off what they can
								for (ItemStack itemType : GFactions.plugin.getStrongholdConfig().getDeteriorationItems().keySet()) {
									Integer required = keep.getDeteriorationItemsOwed().get(itemType);

									if (required == null || required == 0) continue;

									int startingRequired = required;
									int firstSlot = player.getInventory().first(itemType.getType());

									// Do they have this type of item?
									while (firstSlot != -1) {
										ItemStack item = player.getInventory().getItem(firstSlot);
										if (item.getDurability() == itemType.getDurability()) {
											if (item.getAmount() >= required) {
												if (item.getAmount() == required) {
													player.getInventory().setItem(firstSlot, null);
												} else {
													item.setAmount(item.getAmount() - required);
												}

												required = 0;

												keep.getDeteriorationItemsOwed().remove(itemType);
												break;
											} else {
												required = required - item.getAmount();

												player.getInventory().setItem(firstSlot, null);
												keep.getDeteriorationItemsOwed().put(itemType, required);

												firstSlot = player.getInventory().first(itemType.getType());
											}
										}
									}

									if (required == 0) {
										player.sendMessage(ChatColor.GREEN + "You have paid off all the "
												+ itemType.getType().toString().toLowerCase().replaceAll("_", " ") + ".");
									} else if (startingRequired - required > 0) {
										player.sendMessage(ChatColor.GREEN + "You have paid off " + (startingRequired - required) + " "
												+ itemType.getType().toString().toLowerCase().replaceAll("_", " ")
												+ ", you still owe " + required + ".");
									}

									// Update database
									if (startingRequired - required > 0) {
										BukkitUtil.runTaskAsync(new Runnable() {
											@Override
											public void run() {
												String query = "UPDATE " + GFactions.PREFIX + "_stronghold_deterioration SET items_owed = ? WHERE stronghold_id = ? AND keep_name = ?;";

												Connection connection = null;
												PreparedStatement ps = null;

												try {
													connection = Gberry.getConnection();
													ps = connection.prepareStatement(query);

													ps.setString(1, GFactions.plugin.getStrongholdConfig().serializeDeteriorationItemsOwed(keep.getDeteriorationItemsOwed()));
													ps.setInt(2, GFactions.plugin.getStrongholdConfig().getStrongholdEventId() - 1);
													ps.setString(3, keep.getName());

													Gberry.executeUpdate(connection, ps);
												} catch (SQLException e) {
													e.printStackTrace();
												} finally {
													if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
													if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
												}
											}
										});
									}
								}
								player.updateInventory();

								keep.checkDeteriorationApplied(player);
							}
						} else {
							event.getPlayer().sendMessage(ChatColor.RED + "Only the controlling faction can pay their deterioration debt!");
						}
					} else {
						event.getPlayer().sendMessage(ChatColor.RED + "Deterioration debt cannot be paid once a stronghold event has started!");
					}
					break;
				}
			}
		}
	}

    @EventHandler
    public void onPlayerDeathBanned(DeathBanEvent event) {
        if (GFactions.plugin.getStronghold() != null) {
            ProtectedRegion protectedRegion = GFactions.plugin.getgGuardPlugin().getProtectedRegion(event.getPlayer().getLocation(), GFactions.plugin.getgGuardPlugin().getProtectedRegions());

            if (protectedRegion != null && protectedRegion.getRegionName().equals("stronghold")) {
                event.setDeathBanTime(GFactions.plugin.getStrongholdConfig().getDeathBanTime());
            }
        }
    }

    @EventHandler
    public void onPlayerLosePower(PowerLossEvent event) {
        if (GFactions.plugin.getStronghold() != null) {
            ProtectedRegion protectedRegion = GFactions.plugin.getgGuardPlugin().getProtectedRegion(event.getPlayer().getLocation(), GFactions.plugin.getgGuardPlugin().getProtectedRegions());

            if (protectedRegion != null && protectedRegion.getRegionName().equals("stronghold")) {
                event.setPower(event.getPower() * 0.50);
            }
        }
    }

}

