package net.badlion.arenapvp.listener;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.kits.Kit;
import net.badlion.arenacommon.kits.KitCommon;
import net.badlion.arenacommon.kits.KitType;
import net.badlion.arenacommon.rulesets.BuildUHCRuleSet;
import net.badlion.arenacommon.rulesets.CustomRuleSet;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.PotPvPPlayer;
import net.badlion.arenapvp.Team;
import net.badlion.arenapvp.TeamStateMachine;
import net.badlion.arenapvp.helper.PlayerHelper;
import net.badlion.arenapvp.helper.SpectatorHelper;
import net.badlion.arenapvp.inventory.CustomKitCreationInventories;
import net.badlion.arenapvp.manager.MatchManager;
import net.badlion.arenapvp.manager.PotPvPPlayerManager;
import net.badlion.arenapvp.matchmaking.Match;
import net.badlion.arenapvp.state.MatchState;
import net.badlion.combattag.events.CombatTagDamageEvent;
import net.badlion.common.libraries.EnumCommon;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerItemsDroppedFromDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MatchListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		PotPvPPlayer potPvPPlayer = PotPvPPlayerManager.getPotPvPPlayer(player.getUniqueId());
		if (potPvPPlayer != null) {
			if (potPvPPlayer.isSelectingKit()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		PotPvPPlayer potPvPPlayer = PotPvPPlayerManager.getPotPvPPlayer(player.getUniqueId());
		if (potPvPPlayer != null) {
			if (potPvPPlayer.isSelectingKit()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerCraftItemEvent(CraftItemEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player player = ((Player) event.getWhoClicked());
			event.setCancelled(true);
			event.setResult(Event.Result.DENY);

			player.sendFormattedMessage("{0}Crafting items is not allowed!", ChatColor.RED);
		}
	}


	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
			Player player = event.getPlayer();

			// KB debug code
			if (player.getName().equals("SmellyPenguin") && player.isFlying()) {
				if (true) return;

				KitRuleSet.KnockbackType.NON_SPEED.setKnockbackFriction(2.0);
				KitRuleSet.KnockbackType.NON_SPEED.setKnockbackHorizontal(0.38);
				KitRuleSet.KnockbackType.NON_SPEED.setKnockbackVertical(0.35);
				KitRuleSet.KnockbackType.NON_SPEED.setKnockbackVerticalLimit(0.4);
				KitRuleSet.KnockbackType.NON_SPEED.setKnockbackExtraHorizontal(0.563);
				KitRuleSet.KnockbackType.NON_SPEED.setKnockbackExtraVertical(0.105);

				KitRuleSet.KnockbackType.SPEED_II.setKnockbackFriction(2.0);
				KitRuleSet.KnockbackType.SPEED_II.setKnockbackHorizontal(0.445);
				KitRuleSet.KnockbackType.SPEED_II.setKnockbackVertical(0.35);
				KitRuleSet.KnockbackType.SPEED_II.setKnockbackVerticalLimit(0.4);
				KitRuleSet.KnockbackType.SPEED_II.setKnockbackExtraHorizontal(0.38);
				KitRuleSet.KnockbackType.SPEED_II.setKnockbackExtraVertical(0.089);

				player.sendMessage(ChatColor.BOLD.toString() + ChatColor.YELLOW + "KB UPDATED!");
				return;
			}

			PotPvPPlayer potPvPPlayer = PotPvPPlayerManager.getPotPvPPlayer(player.getUniqueId());
			Team team = ArenaPvP.getInstance().getPlayerTeam(player);
			Match match = MatchManager.getActiveMatches().get(team);
			if (!event.hasItem()) {
				return;
			}
			if (match != null) {
				if (potPvPPlayer.isSelectingKit()) {
					event.setCancelled(true);

					final int slot = player.getInventory().getHeldItemSlot();


					// Reopen custom kit selector for them if they click the book
					if (match.getKitRuleSet() instanceof CustomRuleSet) {
						if (slot == 0) {
							CustomKitCreationInventories.openCustomKitLoadInventory(player);
							return;
						}
					}

					player.getInventory().setHeldItemSlot(0);

					if (slot == 8) {
						KitCommon.loadDefaultKit(player, match.getKitRuleSet(), true);
						potPvPPlayer.setSelectingKit(false);
						return;
					} else if (slot >= 0 && slot < 5) {
						int kitId = slot;
						if (event.hasItem()) {
							KitType kitType = new KitType(player.getUniqueId().toString(), match.getKitRuleSet().getName());
							Map<KitType, List<Kit>> kitTypeListMap = KitCommon.inventories.get(player.getUniqueId());
							if (kitTypeListMap != null) {
								List<Kit> kits = kitTypeListMap.get(kitType);
								if (kits != null) {
									KitCommon.loadKit(player, match.getKitRuleSet(), kitId);
									potPvPPlayer.setSelectingKit(false);
								}
							}
						}
					}
				}

				if (!match.isStarted()) {
					ItemStack item = event.getItem();
					if (item.getType().isEdible() || item.getType().equals(Material.POTION)) {
						return;
					}
					event.setCancelled(true);
				}
			}
		}
	}


	@EventHandler
	public void onEatGoldenHeadEvent(final PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		if (item.getType().equals(Material.GOLDEN_APPLE)) {
			ItemMeta meta = item.getItemMeta();
			if (meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.GOLD + "Golden Head")) {
				Team team = ArenaPvP.getInstance().getPlayerTeam(player);
				if (MatchManager.getActiveMatches().containsKey(team)) {
					Match match = MatchManager.getActiveMatches().get(team);
					player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100 + 4/*Number of 1/2 hearts to heal*/ * 25, 1), true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerItemDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Team team = ArenaPvP.getInstance().getPlayerTeam(player);
		if (team != null && team.contains(player)) {
			if (MatchManager.getActiveMatches().containsKey(team)) {
				Match match = MatchManager.getActiveMatches().get(team);

				if (team.isParty()) {
					Material type = event.getItemDrop().getItemStack().getType();
					if (type.equals(Material.BOW) || type.equals(Material.FISHING_ROD)
							|| type.equals(Material.WOOD_SWORD) || type.equals(Material.STONE_SWORD)
							|| type.equals(Material.GOLD_SWORD) || type.equals(Material.IRON_SWORD)
							|| type.equals(Material.DIAMOND_SWORD) || type.equals(Material.WOOD_AXE)
							|| type.equals(Material.STONE_AXE) || type.equals(Material.GOLD_AXE)
							|| type.equals(Material.IRON_AXE) || type.equals(Material.DIAMOND_AXE)) {
						event.setCancelled(true);
						return;
					}

					boolean alive = false;
					// Is this player alive?
					if (team.getActivePlayers().contains(player)) {
						// Let them drop it in a teams match, but track so we can remove it later
						if (match.isInProgress() && !match.isOver()) {
							match.getArena().addItemDrop(event.getItemDrop());
						}
					} else {
						event.setCancelled(true);
					}
				} else {
					if (team.contains(player)) {
						Material type = event.getItemDrop().getItemStack().getType();
						if (type.equals(Material.BOW) || type.equals(Material.FISHING_ROD)
								|| type.equals(Material.WOOD_SWORD) || type.equals(Material.STONE_SWORD)
								|| type.equals(Material.GOLD_SWORD) || type.equals(Material.IRON_SWORD)
								|| type.equals(Material.DIAMOND_SWORD) || type.equals(Material.WOOD_AXE)
								|| type.equals(Material.STONE_AXE) || type.equals(Material.GOLD_AXE)
								|| type.equals(Material.IRON_AXE) || type.equals(Material.DIAMOND_AXE)) {
							event.setCancelled(true);
							return;
						}
					}

					event.getItemDrop().remove();

				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Team team = ArenaPvP.getInstance().getPlayerTeam(player);
			if (team.contains(player)) {
				if (MatchManager.getActiveMatches().containsKey(team)) {
					Match match = MatchManager.getActiveMatches().get(team);
					if (!match.isStarted()) {
						event.setCancelled(true);
					}
					// Party stuff
					Entity damager = event.getDamager();
					Player damagePlayer = null;
					if (damager instanceof Projectile) {
						damagePlayer = ((Projectile) damager).getShooter() != null && ((Projectile) damager).getShooter() instanceof Player ? (Player) ((Projectile) damager).getShooter() : null;
					} else if (damager instanceof Player) {
						damagePlayer = (Player) damager;
					}

					// Track last damage
					if (damagePlayer != null) {
						// Don't let allies hurt each other for some reason...
						if (team.contains(damagePlayer) || !match.contains(damagePlayer)) {
							return;
						}

						// Don't track damage that we did to ourselves
						if (!damagePlayer.getUniqueId().equals(player.getUniqueId())) {
							match.putLastDamage(damagePlayer.getUniqueId(), player.getUniqueId(), event.getDamage(), event.getFinalDamage());
						}
					}
				}
			}
		}
	}


	@EventHandler(priority = EventPriority.LAST, ignoreCancelled = true)
	public void onEntityRegen(EntityRegainHealthEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();

		Match match = MatchState.getPlayerMatch(player);
		if (match != null) {
			if (match.getKitRuleSet() instanceof BuildUHCRuleSet) {
				double newHealth = player.getHealth() + event.getAmount();

				// Adding player health and the regain amount can go over the player's max health
				if (newHealth > player.getMaxHealth()) newHealth = player.getMaxHealth();

				match.updateHealthObjective(player, newHealth);
			}
		}
	}

	@EventHandler(priority = EventPriority.LASTER, ignoreCancelled = true)
	public void onEntityDamageLast(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity();
		if (player.getHealth() - event.getFinalDamage() <= 0.0) {
			event.setCancelled(true);
			this.onPlayerDeath(player);
		} else {
			if (event.getFinalDamage() == 0.0) {
				return;
			}
			Match match = MatchState.getPlayerMatch(player);
			if (match != null) {
				if (match.getKitRuleSet() instanceof BuildUHCRuleSet) {
					double newHealth = player.getHealth() - event.getFinalDamage();
					match.updateHealthObjective(player, newHealth);
				}
			}
		}
	}

	// This event should never get called since the entity damage event should block players from dying?
	/*
	@EventHandler
	public void onPlayerDeathBackup(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Gberry.log("BUG", "PLAYER DIED FOR REAL: " + player.getUniqueId());
		onPlayerDeath(player);
	}*/

	public void onPlayerDeath(Player player) {
		Team team = ArenaPvP.getInstance().getPlayerTeam(player);
		if (team.contains(player)) {
			if (MatchManager.getActiveMatches().containsKey(team)) {
				Match match = MatchManager.getActiveMatches().get(team);
				// They "died"
				if (match != null) {
					if (match.isOver()) {
						return;
					}
					try {
						PlayerHelper.showDyingNPC(player);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					match.handleDeath(player);
					if (!match.is1v1() && TeamStateMachine.matchState.contains(player) && !TeamStateMachine.redRoverWaitingState.contains(player)) {
						if (player.getItemOnCursor() != null && !player.getItemOnCursor().getType().equals(Material.AIR)) {
							player.getWorld().dropItem(player.getLocation(), player.getItemOnCursor());
						}
						for (ItemStack item : player.getInventory().getArmorContents()) {
							if (item != null && !item.getType().equals(Material.AIR)) {
								player.getWorld().dropItem(player.getLocation(), item);
							}
						}
						for (ItemStack item : player.getInventory().getContents()) {
							if (item != null && !item.getType().equals(Material.AIR)) {
								player.getWorld().dropItem(player.getLocation(), item);
							}
						}
					}
					// If the match ends when the players death is handled they could already be in spectator so don't clear their inventory
					if (!TeamStateMachine.spectatorState.contains(player)) {
						player.setItemOnCursor(null);
						player.getInventory().clear();
						player.getInventory().setArmorContents(new ItemStack[4]);
					}
					PlayerHelper.healPlayer(player);
					player.getWorld().playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "HURT_FLESH", "ENTITY_PLAYER_HURT"), 1.0F, 1.0F);
					player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 5));
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 5));
					if (!TeamStateMachine.spectatorState.contains(player)) {
						SpectatorHelper.activateSpectateGameMode(player);
					}
				} else {
					player.teleport(ArenaPvP.getInstance().getSpawnLocation());
					PlayerHelper.healPlayer(player);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDropItemsFromDeath(PlayerItemsDroppedFromDeathEvent event) {
		Player player = event.getPlayer();
		Team team = ArenaPvP.getInstance().getPlayerTeam(player);
		if (team != null) {
			if (MatchManager.getActiveMatches().containsKey(team)) {
				Match match = MatchManager.getActiveMatches().get(team);
				// They "died"
				if (match != null) {
					if (match.is1v1()) {
						event.setItemsDroppedOnDeath(new ArrayList<>());
					}
				}
			}
		} else {
			event.setItemsDroppedOnDeath(new ArrayList<>());
		}
	}

	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (event.getEntity() instanceof Player) {
				Team team = ArenaPvP.getInstance().getPlayerTeam(event.getEntity());
				if (team != null) {
					if (team.contains(player)) {
						if (MatchManager.getActiveMatches().containsKey(team)) {
							Match match = MatchManager.getActiveMatches().get(team);
							if (!match.isStarted()) {
								event.setCancelled(true);
							}
							if (System.currentTimeMillis() - match.getStartTime().getMillis() < 2000) {
								event.setCancelled(true);
							}/*else if (game instanceof RedRoverMatch && ((RedRoverMatch) game).isSelectingFighter(((Player) event.getEntity()))) {
						event.setCancelled(true);
                    }*/
						}
					}
				} else {
					// They dont have a team so cancel all damage
					event.setCancelled(true);
				}
			}

		}
	}


	@EventHandler
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		Team team = ArenaPvP.getInstance().getPlayerTeam(player);
		//Game game = GameState.getGroupGame(team);

		// Check to be safe & for red rover matches
		/*if (game instanceof RedRoverMatch) {
		    if (player.getGameMode() != GameMode.SURVIVAL || ((RedRoverMatch) game).isSelectingFighter(player)) {
			    event.setCancelled(true);
			    return;
		    }
	    }*/

		// Remove item from our cache if party match
		/*if ((!team.members().isEmpty() && this.contains(team)) || game instanceof UHCMeetup) {
		    game.getplayersArena().removeItemDrop(event.getItem());
        }*/
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onTeamDamage(EntityDamageByEntityEvent event) {
		Player target = null;
		if (event.getEntity() instanceof Player) {
			target = (Player) event.getEntity();
		}

		// Nothing more to do here
		if (target == null) {
			return;
		}

		Player player = null;
		if (event.getDamager() instanceof Player) {
			player = (Player) event.getDamager();
		} else if (event.getDamager() instanceof Projectile) {
			player = (Player) ((Projectile) event.getDamager()).getShooter();
		}

		Team team = ArenaPvP.getInstance().getPlayerTeam(player);
		if (team != null) {
			if (team.contains(target) && team.isParty()) { // Avoid weird issues where someone can not hurt themselves with an arrow
				if (MatchManager.getActiveMatches().containsKey(team)) {
					Match match = MatchManager.getActiveMatches().get(team);
					if (match != null) {
						if (event.getDamager() instanceof FishHook && match.getKitRuleSet() instanceof BuildUHCRuleSet) {
							// Allow team damage from fish hooks
							return;
						}
						// Friendly fire is enabled, return out
						if (match.isFriendlyFireEnabled()) {
							return;
						}

						if (!(match.getLadderType().equals(ArenaCommon.LadderType.PARTY_FFA))) {
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}


	@EventHandler(priority = EventPriority.LOWEST)
	public void onTeamDamageHorse(EntityDamageByEntityEvent event) {
		Horse horse = null;
		if (event.getEntity() instanceof Horse) {
			horse = (Horse) event.getEntity();
		}

		// Nothing more to do here
		if (horse == null) {
			return;
		}

		Player player = null;
		if (event.getDamager() instanceof Player) {
			player = (Player) event.getDamager();
		} else if (event.getDamager() instanceof Projectile) {
			player = (Player) ((Projectile) event.getDamager()).getShooter();
		}

		if (horse.getPassenger() == null || !(horse.getPassenger() instanceof Player)) return;

		Player passenger = (Player) horse.getPassenger();
		Team team = ArenaPvP.getInstance().getPlayerTeam(player);
		if (team != null) {
			if (team.contains(player) && team.isParty()) {
				if (MatchManager.getActiveMatches().containsKey(team)) {
					// They are on the same team
					if (team.contains(passenger)) {
						event.setCancelled(true);
					}
				}
			}
		}
	}


	@EventHandler
	public void onCombatLoggerDamage(CombatTagDamageEvent event) {
		if (event.getLoggerNPC() == null || event.getLoggerNPC().getPlayer() == null || event.getLoggerNPC().getPlayer().getUniqueId() == null) {
			return;
		}
		Player player = event.getDamager();
		Team team = ArenaPvP.getInstance().getPlayerTeam(player);
		if (team != null) {
			if (team.contains(event.getLoggerNPC().getPlayer().getUniqueId()) && team.isParty()) {
				if (MatchManager.getActiveMatches().containsKey(team)) {
					Match match = MatchManager.getActiveMatches().get(team);
					if (!(match.getLadderType().equals(ArenaCommon.LadderType.PARTY_FFA))) {
						event.setCancelled(true);
					}
				}
			}
		}
	}
}
