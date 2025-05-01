package net.badlion.gfactions.listeners;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import net.badlion.gfactions.managers.FactionManager;
import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.events.Tower;
import net.badlion.gberry.Gberry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Random;

public class TowerListener implements Listener {

	private GFactions plugin;
	private Random randomGenerator;
	private HashSet<Player> playersToIgnore;

	public TowerListener(GFactions plugin) {
		this.plugin = plugin;
		this.randomGenerator = new Random();
		this.playersToIgnore = new HashSet<Player>();
	}

	@EventHandler
	public void onPressurePlatePressed(PlayerInteractEvent event) {
		if (this.plugin.getTower() != null) {
			if (event.getAction().equals(Action.PHYSICAL)) {
				if (event.getClickedBlock().getType() == Material.STONE_PLATE) {
					Tower tower = this.plugin.getTower();
					Player player = event.getPlayer();
					// Skip since we just tp'd them
					if (this.playersToIgnore.contains(player)) {
						this.playersToIgnore.remove(player);
						return;
					}
					Location location = player.getLocation();
					// If they are in a tower
					if ((location.getX() >= tower.getBottomLeftCornerLocation().getX() && location.getX() <= tower.getTopRightCornerLocation().getX()) &&
							(location.getY() >= tower.getBottomLeftCornerLocation().getY() && location.getY() <= tower.getTopRightCornerLocation().getY()) &&
							(location.getZ() >= tower.getBottomLeftCornerLocation().getZ() && location.getZ() <= tower.getTopRightCornerLocation().getZ())) {
						int option = this.randomGenerator.nextInt(100);
						if (option < 50) {
							// do nothing
						} else if (option >= 50 && option < 60) {
							if (option >= 50 && option < 53) {
								player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 0));
								player.sendMessage(ChatColor.GOLD + "You have been " + ChatColor.RED + "blinded " + ChatColor.GOLD + "for " + ChatColor.AQUA + "5" + ChatColor.GOLD + " seconds.");
							} else if (option >= 53 && option < 57) {
								player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 5 * 20, 0));
								player.sendMessage(ChatColor.GOLD + "You have been " + ChatColor.RED + "weakened " + ChatColor.GOLD + "for " + ChatColor.AQUA + "5" + ChatColor.GOLD + " seconds.");
							} else {
								player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 5 * 20, 0));
								player.sendMessage(ChatColor.GOLD + "You have been " + ChatColor.RED + "confusion " + ChatColor.GOLD + "for " + ChatColor.AQUA + "5" + ChatColor.GOLD + " seconds.");
							}
						} else if (option >= 60 && option < 65) {
							location.setYaw(location.getYaw() + 180);
							player.teleport(location);
							this.playersToIgnore.add(player);
							player.sendMessage(ChatColor.GOLD + "You have been turned around.");
						} else if (option >= 65 && option < 100) {
							if (option >= 65 && option < 85) {
								location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
								location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
							} else if (option >= 85 && option < 95) {
								location.getWorld().spawnEntity(location, EntityType.SPIDER);
							} else {
								location.getWorld().spawnEntity(location, EntityType.CREEPER);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void playerOpenTowerChest(final PlayerInteractEvent e) {
		if (this.plugin.getTower() != null && !this.plugin.getTower().isClaimed()) {
			if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if (e.getClickedBlock() != null && e.getClickedBlock().getType().equals(Material.CHEST)) {
					if (this.plugin.getTower().getChestLocation().equals(e.getClickedBlock().getLocation())) { // It is the tower chest
						final ItemStack[] contents = ((Chest) e.getClickedBlock().getState()).getBlockInventory().getContents();
						e.setCancelled(true);
						e.setUseInteractedBlock(Event.Result.DENY);
						this.plugin.getTower().setClaimed(true); // Stops them from being able to claim reward twice if async thread lags
						this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
							@Override
							public void run() {
								// Give them rewards
								for (ItemStack reward : contents) {
									// Avoid NPE
									if (reward == null || reward.getType() == Material.AIR) {
										continue;
									}

									//plugin.getAuction().insertHeldAuctionItem(e.getPlayer().getUniqueId().toString(), reward); TODO
								}

								e.getPlayer().sendMessage(ChatColor.GREEN + "Use \"/claim\" to claim your Insanity Tower rewards!");
								Gberry.broadcastMessage(ChatColor.GOLD + e.getPlayer().getName() + " has claimed the Tower.");

								final Faction faction = FPlayers.i.get(e.getPlayer()).getFaction();
								Bukkit.getLogger().info("~Faction " + faction.getId() + faction.getTag() + " won Tower!");

								if (!faction.getId().equals("0")) {
									TowerListener.this.plugin.getServer().getScheduler().runTaskAsynchronously(TowerListener.this.plugin, new Runnable() {
										@Override
										public void run() {
											FactionManager.addStatToFaction("towers", faction);
										}
									});
								}

								plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
									@Override
									public void run() {
										plugin.getTower().despawn();
									}
								});
							}
						});
					}
				}
			}
		}
	}

	/*@EventHandler(ignoreCancelled=true)
	public void onInventoryClick(final InventoryClickEvent event) {
		if (this.plugin.getTower() != null) {
			if (!(event.getWhoClicked() instanceof Player)) return; //Make sure it was a player.
			//if (event.getSlotType() != SlotType.CONTAINER) return; //Make sure it is the chest's inventory.
			if (event.getInventory().getType() == InventoryType.CHEST) {
				final Player player = (Player) event.getWhoClicked();
				if (event.getRawSlot() > event.getView().getTopInventory().getSize()) {
					// Some weird edge case where they can shift click items into the chest
				} else {
					// Moving from top inventory to bottom (Chest to Player)
					if (event.getSlot() < 0) {
						return;
					}
					if (event.getInventory().getItem(event.getSlot()) != null) { //Make sure it was a withdrawal.
						InventoryHolder ih = event.getInventory().getHolder();
						if (ih instanceof Chest){
							Location chestLoc = ((Chest)ih).getLocation();
							if (chestLoc.equals(this.plugin.getTower().getChestLocation())) {
								Gberry.broadcastMessage(ChatColor.GOLD + player.getName() + " has claimed the Tower.");
								this.plugin.getTower().despawn();
							}
						}
					}
				}
			}
		}
	}*/

}
