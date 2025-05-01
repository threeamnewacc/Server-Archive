package net.badlion.uhc.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.gberry.utils.MessageUtil;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.events.UHCTeleportPlayerLocationEvent;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.util.GoldenHeadUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Iterator;
import java.util.List;

public class MainListener implements Listener {

    @EventHandler
    public void onTeleport(UHCTeleportPlayerLocationEvent event) {
        event.getPlayer().setWalkSpeed(0.2f); // Removes speed from wither morph
    }

	@EventHandler
	public void playerInteract(PlayerInteractEvent event) {
		// Disable interacting with hoppers, damn abusing kids -.-
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.HOPPER) {
			event.getPlayer().sendMessage(ChatColor.RED + "You can not use hoppers.");
			event.setCancelled(true);
			return;
		}

		if (BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.COUNTDOWN
                && BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.STARTED
                && event.getAction().equals(Action.PHYSICAL)
                && event.getClickedBlock() != null
				&& event.getClickedBlock().getType().equals(Material.GOLD_PLATE)
                && event.getClickedBlock().getY() == 168) {
		 	if (!BadlionUHC.getInstance().getCompletedParkour().contains(event.getPlayer().getDisguisedName())) {

			    BadlionUHC.getInstance().getCompletedParkour().add(event.getPlayer().getDisguisedName());
			    String message = ChatColor.GOLD + "Congratulations to " + ChatColor.YELLOW
					    + event.getPlayer().getDisguisedName() + ChatColor.GOLD + " for completing HalfCreeper's parkour! ";
			    switch (Gberry.generateRandomInt(0, 11)) {
				    case 0:
					    message += ChatColor.GREEN + "That is value!";
					    break;
				    case 1:
					    message += ChatColor.GREEN + "Your parents would be proud!";
					    break;
				    case 2:
					    message += ChatColor.GREEN + "You ARE the sharpest tool in the shed!";
					    break;
				    case 3:
					    message += ChatColor.GREEN + "A dream Smelly would never fulfill.";
					    break;
				    case 4:
					    message += ChatColor.GREEN + "You're the alpha of the herd!";
					    break;
				    case 5:
					    message += ChatColor.GREEN + "You are a living legend!";
					    break;
				    case 6:
					    message += ChatColor.GREEN + "Only completed it once? 4Head";
					    break;
				    case 7:
					    message += ChatColor.GREEN + "Oh well, better than nothing!";
					    break;
				    case 8:
					    message += ChatColor.GREEN + "That's one way to spend your spare time!";
					    break;
				    case 9:
					    message += ChatColor.GREEN + "You are a god among mortals! PogChamp";
					    break;
				    case 10:
					    message += ChatColor.GREEN + "My nan could do it with her eyes closed! MingLee";
					    break;
				    case 11:
					    message += ChatColor.GREEN + "Now let's see you do it without flying.";
					    break;
			    }

			    Gberry.broadcastMessage(message);
		    }
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void entityDamageEvent(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player && event.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
            if (BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.IPVP.name()).getValue() != null
                        && !((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.IPVP.name()).getValue())) { // Is iPvP disabled?
                if (!BadlionUHC.getInstance().isPVP()) { // Is PvP not enabled yet?
                    ((Player) event.getEntity()).sendMessage(ChatColor.RED + "IPvP isn't allowed until PvP is enabled!");
                    event.setCancelled(true);
                }
            }
        }
	}

	@EventHandler
	public void blockIgnite(BlockIgniteEvent event) {
        if (BadlionUHC.getInstance().getState().ordinal() < BadlionUHC.BadlionUHCState.STARTED.ordinal()) {
            return;
        }

        if (BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.IPVP.name()).getValue() != null
                && !((boolean)BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.IPVP.name()).getValue())) { // Is iPvP disabled?
	        if (!BadlionUHC.getInstance().isPVP()) { // Is PvP not enabled yet?
		        // They might be trying to light a nether portal
		        if (event.getBlock().getRelative(0, -1, 0).getType() == Material.OBSIDIAN) {
			        return;
		        }

                if (event.getPlayer() != null) {
                    event.getPlayer().sendMessage(ChatColor.RED + "IPvP isn't allowed until PvP is enabled!");
                }
				event.setCancelled(true);
			}
		}
	}

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (BadlionUHC.getInstance().getState().ordinal() < BadlionUHC.BadlionUHCState.STARTED.ordinal()) {
            event.setCancelled(true);
        }
    }

	@EventHandler
	public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
		// Is iPvP disabled?
        if (BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.IPVP.name()).getValue() != null
                && !((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.IPVP.name()).getValue())) {
	        // Is PvP not enabled yet?
			if (!BadlionUHC.getInstance().isPVP()) {
				// Are they trying to place down lava?
				if (event.getBucket().equals(Material.LAVA_BUCKET)) {
                    event.getPlayer().sendMessage(ChatColor.RED + "IPvP isn't allowed until PvP is enabled!");
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onNetherPortalEnterEvent(PlayerPortalEvent event) {
		Player player = event.getPlayer();

		if (BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.NETHER.name()).getValue() != null
				&& !((boolean)BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.NETHER.name()).getValue())) {
			if (event.getTo().getWorld().getName().equals(BadlionUHC.UHCWORLD_NETHER_NAME)) {
				player.sendMessage(ChatColor.RED + "The Nether is disabled!");
				event.setCancelled(true);
			}
		} else {
			UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(player.getUniqueId());

			// Is a spectator trying to go into the nether?
			if (uhcPlayer.getState() == UHCPlayer.State.SPEC) {
				player.sendMessage(ChatColor.RED + "You cannot go into the Nether!");
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPortalCreate(PortalCreateEvent event) {
		if (BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.NETHER.name()).getValue() != null
				&& !((boolean)BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.NETHER.name()).getValue())) {
			event.setCancelled(true);
		} else if (!BadlionUHC.getInstance().isPVP()) {
			// Fail safe
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onFNSPlace(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getItem() != null) {
				if (event.getItem().getType() == Material.FLINT_AND_STEEL) {
					if (!BadlionUHC.getInstance().isPVP()) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(ChatColor.RED + "You can't place Flint and Steel until PVP is enabled.");
					}
				}
			}
		}
	}

    @EventHandler(priority = EventPriority.LAST)
    public void healthRegen(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                event.setCancelled(true);
            } else {
				final Player player = (Player) event.getEntity();

				Bukkit.getScheduler().runTaskLater(BadlionUHC.getInstance(), new Runnable() {
					@Override
					public void run() {
						UHCPlayerManager.updateHealthScores(player);
					}
				}, 1L);
			}

	        // Team health sharing enabled?
	        /*if (BadlionUHC.getInstance().getTeamHealthShare() != null && BadlionUHC.getInstance().getTeamHealthShare()) {
		        final UHCTeam team = BadlionUHC.getInstance().getPlayers().get(p.getName()).getTeam();

		        Bukkit.getScheduler().runTaskLater(BadlionUHC.getInstance(), new Runnable() {
			        @Override
			        public void run() {
				        // Update health for leader
				        if (!team.isLeader(p)) {
					        team.getLeader().setHealth(p.getHealth());

					        BadlionUHC.getInstance().getScoreboard().getObjective(DisplaySlot.PLAYER_LIST).getScore(team.getLeader().getPlayerListName()).setScore((int) Math.ceil(team.getLeader().getHealth() * BadlionUHC.getInstance().getScoreboardHealthScale()));
					        BadlionUHC.getInstance().getScoreboard().getObjective(DisplaySlot.BELOW_NAME).getScore(team.getLeader().getName()).setScore((int) Math.ceil(team.getLeader().getHealth() * BadlionUHC.getInstance().getScoreboardHealthScale()));
				        }

				        // Update health for rest of team members
				        for (Player member : team.getPlayers()) {
					        if (p != member) {
						        member.setHealth(p.getHealth());

						        BadlionUHC.getInstance().getScoreboard().getObjective(DisplaySlot.PLAYER_LIST).getScore(member.getPlayerListName()).setScore((int) Math.ceil(member.getHealth() * BadlionUHC.getInstance().getScoreboardHealthScale()));
						        BadlionUHC.getInstance().getScoreboard().getObjective(DisplaySlot.BELOW_NAME).getScore(member.getName()).setScore((int) Math.ceil(member.getHealth() * BadlionUHC.getInstance().getScoreboardHealthScale()));
					        }
				        }
			        }
		        }, 1L);
	        }*/
        }
    }

    @EventHandler
    public void prepareItemCraftEvent(PrepareItemCraftEvent event) {
        if (event.getRecipe().getResult().getType() == Material.GOLDEN_APPLE) {
            if (BadlionUHC.getInstance().containsMaterial(event.getInventory().getMatrix(), Material.GOLD_NUGGET)) {
                event.getInventory().setResult(new ItemStack(Material.AIR));
            } else if (BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.GODAPPLES.name()).getValue() != null
                    && !((boolean)BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.GODAPPLES.name()).getValue())
                    && event.getRecipe().getResult().getDurability() == 1) {
                event.getInventory().setResult(new ItemStack(Material.AIR));
            } else {
	            if (BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.GOLDENHEADS.name()).getValue() != null
			            && !((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.GOLDENHEADS.name()).getValue())
			            && event.getRecipe().getResult().getItemMeta().hasDisplayName()
			            && event.getRecipe().getResult().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Golden Head")) {
		            event.getInventory().setResult(new ItemStack(Material.AIR));
	            } else if (BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.GOLDENHEADSSTACK.name()).getValue() != null
			            && !((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.GOLDENHEADSSTACK.name()).getValue())) {
		        	ItemStack item = event.getInventory().getResult();
		            ItemMeta itemMeta = item.getItemMeta();
		            List<String> lore = itemMeta.getLore();
		            ItemStack skull = event.getInventory().getMatrix()[4];
		            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		            lore.add(ChatColor.GOLD + skullMeta.getOwner() + "'s head");
		            itemMeta.setLore(lore);
		            item.setItemMeta(itemMeta);
	            }
            }
        } else if (event.getRecipe().getResult().getType() == Material.SPECKLED_MELON) {
            if (BadlionUHC.getInstance().containsMaterial(event.getInventory().getMatrix(), Material.GOLD_NUGGET)) {
                event.getInventory().setResult(new ItemStack(Material.AIR));
            }
        } else if (event.getRecipe().getResult().getType() == Material.GOLDEN_CARROT) {
            if (BadlionUHC.getInstance().containsMaterial(event.getInventory().getMatrix(), Material.GOLD_NUGGET)) {
                event.getInventory().setResult(new ItemStack(Material.AIR));
            }
        }
    }

    @EventHandler(priority = EventPriority.FINAL, ignoreCancelled = true)
    public void playerDamaged(EntityDamageEvent event) {
	    if (event.getEntity() instanceof Player) {
		    final Player player = (Player) event.getEntity();

		    if (event.getDamage() == 0D) return;

		    Bukkit.getScheduler().runTaskLater(BadlionUHC.getInstance(), new Runnable() {
			    @Override
			    public void run() {
				    UHCPlayerManager.updateHealthScores(player);
			    }
		    }, 1L);
	    }
    }

    @EventHandler
    public void enderpearlDamage(EntityDamageByEntityEvent event) {
        if (BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.STARTED) {
            if (BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.PEARLS.name()).getValue() != null
		            && !((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.PEARLS.name()).getValue())) {
                if (event.getDamager().getType().equals(EntityType.ENDER_PEARL) && event.getEntity() instanceof Player) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onGhastDeathEvent(EntityDeathEvent event) {
        if (event.getEntity() instanceof Ghast) {
            Iterator<ItemStack> it = event.getDrops().iterator();
            while (it.hasNext()) {
                ItemStack drop = it.next();
                if (drop.getType() == Material.GHAST_TEAR) {
                    it.remove();
                }
            }

            event.getDrops().add(new ItemStack(Material.GOLD_INGOT));
        }
    }

    @EventHandler
    public void onPotionSplashEvent(PotionSplashEvent event) {
        Potion potion = Potion.fromDamage(event.getPotion().getItem().getDurability() & 0x3F);
        if (potion.getType().equals(PotionType.STRENGTH)) {
	        if ((!((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.STR.name()).getValue()))
			        || (potion.getLevel() > 1 && !((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.STR2.name()).getValue()))) {
		        event.setCancelled(true);
		        return;
	        }

	        if (potion.getLevel() > 1) {
		        for (LivingEntity entity : event.getAffectedEntities()) {
			        potion.setLevel(1);
			        potion.apply(event.getEntity().getItem());
		        }
	        }
        } else if (potion.getType().equals(PotionType.INVISIBILITY)) {
	        if (!((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.INVISIBILITY.name()).getValue())) {
		        event.setCancelled(true);
	        }
        }
    }

    @EventHandler
    public void onPotionConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (player.getItemInHand().getType() == Material.POTION) {
            if (player.getItemInHand().getDurability() != 0) {
	            Potion potion = Potion.fromItemStack(player.getItemInHand());
	            if (potion.getType().equals(PotionType.STRENGTH)) {
		            if ((!((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.STR.name()).getValue()))
				            || (potion.getLevel() > 1 && !((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.STR2.name()).getValue()))) {
			            event.setCancelled(true);
			            event.setItem(ItemStackUtil.EMPTY_ITEM);
			            player.setItemInHand(ItemStackUtil.EMPTY_ITEM);
			            player.sendMessage(ChatColor.RED + "Strength " + (potion.getLevel()) + " is not enabled.");
			            return;
		            }

		            if (potion.getLevel() > 1) {
			            potion.setLevel(1);
			            potion.apply(player.getItemInHand());
		            }
	            } else if (potion.getType().equals(PotionType.INVISIBILITY)) {
		            if (!((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.INVISIBILITY.name()).getValue())) {
			            event.setCancelled(true);
			            event.setItem(ItemStackUtil.EMPTY_ITEM);
			            player.setItemInHand(ItemStackUtil.EMPTY_ITEM);
			            player.sendMessage(ChatColor.RED + "Invisibility is not enabled.");
		            }
	            }
            }
        }
    }

    @EventHandler
    public void onEatGoldenHeadEvent(final PlayerItemConsumeEvent event) {
        if (BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.STARTED) {
            if (!((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.ABSORPTION.name()).getValue())) {
                BadlionUHC.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(BadlionUHC.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        event.getPlayer().removePotionEffect(PotionEffectType.ABSORPTION);
                    }
                });
            }

            if ((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.GOLDENHEADS.name()).getValue()) {
	            Player player = event.getPlayer();
	            ItemStack item = event.getItem();
	            if (item.getType().equals(Material.GOLDEN_APPLE)) {
		            ItemMeta meta = item.getItemMeta();
		            if (meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.GOLD + "Golden Head")) {
			            // Add potion effect
			            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, BadlionUHC.HEAD_HALF_HEARTS_TO_HEAL * 25, 1), true);
		            }
	            }
            }
        } else { // For practice
	        Player player = event.getPlayer();

	        ItemStack item = event.getItem();
	        if (item.getType().equals(Material.GOLDEN_APPLE)) {
		        ItemMeta meta = item.getItemMeta();
		        if (meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.GOLD + "Golden Head")) {
			        // Add potion effect
			        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, BadlionUHC.HEAD_HALF_HEARTS_TO_HEAL * 25, 1), true);
		        }
	        }
        }
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        if (BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.STARTED) {
            if ((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.GOLDENHEADS.name()).getValue()) {
                GoldenHeadUtils.makeHeadStakeForPlayer(event.getEntity());
            }
        }
    }

    @EventHandler
    public void potionBrewing(final InventoryClickEvent event) {
        if (BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.STARTED) {
            if (event.getInventory().getType().equals(InventoryType.BREWING)) { // Brewing Inventory
                if ((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.STR.name()).getValue()) {
                    if (!((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.STR2.name()).getValue())) {
                        for (ItemStack invItem : event.getInventory().getContents()) {
                            if (invItem == null) continue;

                            if (invItem.getDurability() != 0 && invItem.getType() == Material.POTION && Potion.fromItemStack(invItem) != null
                                    && Potion.fromItemStack(invItem).getType().equals(PotionType.STRENGTH)) { // Strength potion in brewing stand
                                if (event.isShiftClick() && event.getCurrentItem() != null && event.getCurrentItem().getType().equals(Material.GLOWSTONE_DUST)) {
                                    event.setCancelled(true);
                                    return;
                                }
                                if (event.getRawSlot() == 3) {
                                    if (event.getCursor() != null && event.getCursor().getType().equals(Material.GLOWSTONE_DUST)) {
                                        event.setCancelled(true);
                                        return;
                                    }
                                    if (event.getRawSlot() == 3 && event.getHotbarButton() > -1 && event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) != null
                                            && event.getWhoClicked().getInventory().getItem(event.getHotbarButton()).getType().equals(Material.GLOWSTONE_DUST)) {
                                        event.setCancelled(true);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (event.isShiftClick() && event.getCurrentItem() != null && event.getCurrentItem().getType().equals(Material.BLAZE_POWDER)) {
                        event.setCancelled(true);
                        return;
                    }
                    if (event.getRawSlot() == 3) {
                        if (event.getCursor() != null && event.getCursor().getType().equals(Material.BLAZE_POWDER)) {
                            event.setCancelled(true);
                            return;
                        }
                        if (event.getRawSlot() == 3 && event.getHotbarButton() > -1 && event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) != null
                                && event.getWhoClicked().getInventory().getItem(event.getHotbarButton()).getType().equals(Material.BLAZE_POWDER)) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
                if (!((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.INVISIBILITY.name()).getValue())) {
                    if (event.isShiftClick() && event.getCurrentItem() != null && event.getCurrentItem().getType().equals(Material.GOLDEN_CARROT)) {
                        event.setCancelled(true);
                        return;
                    }
                    if (event.getRawSlot() == 3) {
                        if (event.getCursor() != null && event.getCursor().getType().equals(Material.GOLDEN_CARROT)) {
                            event.setCancelled(true);
                            return;
                        }
                        if (event.getRawSlot() == 3 && event.getHotbarButton() > -1 && event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) != null
                                && event.getWhoClicked().getInventory().getItem(event.getHotbarButton()).getType().equals(Material.GOLDEN_CARROT)) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void inventoryDrag(InventoryDragEvent event) {
        if (BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.STARTED) {
            if (event.getInventory().getType().equals(InventoryType.BREWING)) { // Brewing Inventory
                if ((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.STR.name()).getValue()) {
                    if ((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.STR2.name()).getValue()) {
                        for (ItemStack invItem : event.getInventory().getContents()) {
                            if (invItem == null) continue;

                            if (invItem.getDurability() != 0 && invItem.getType() == Material.POTION
                                    && Potion.fromItemStack(invItem).getType().equals(PotionType.STRENGTH)) { // Strength potion in brewing stand
                                if (event.getOldCursor().getType().equals(Material.GLOWSTONE_DUST) && event.getRawSlots().contains(new Integer(3))) {
                                    event.setCancelled(true);
                                    return;
                                }
                            }
                        }
                    }
                } else {
                    if (event.getOldCursor().getType().equals(Material.BLAZE_POWDER) && event.getRawSlots().contains(new Integer(3))) {
                        event.setCancelled(true);
                        return;
                    }
                }

                if (!((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.INVISIBILITY.name()).getValue())) {
                    if (event.getOldCursor().getType().equals(Material.GOLDEN_CARROT) && event.getRawSlots().contains(new Integer(3))) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.LAST, ignoreCancelled=true)
    public void onArrowHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
            Player damaged = (Player) event.getEntity();

            if (((Arrow) event.getDamager()).getShooter() instanceof Player) {
                Player damager = (Player) ((Arrow) event.getDamager()).getShooter();

	            if (damaged != damager && damaged.getHealth() - event.getFinalDamage() > 0) {
                    damager.sendMessage(ChatColor.GOLD + damaged.getDisguisedName() + ChatColor.DARK_AQUA + " is now at " + ChatColor.GOLD + Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2D + " " + MessageUtil.HEART_WITH_COLOR);
                }
            }
        }
    }

    @EventHandler
    public void onHorseRegainHealth(EntityRegainHealthEvent event) {
        if (BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.STARTED) {
            if (event.getEntity() instanceof Horse) {
                // If horse regen is disabled
                if (!((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.HORSE_REGEN.name()).getValue())) {
                    event.setCancelled(true);
                }
            }
        }
    }

	@EventHandler
	public void onCraftHopper(CraftItemEvent event) {
		if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.HOPPER) {
			// Disable the crafting of hoppers
			event.setCancelled(true);
			event.setCurrentItem(null);
			event.setCursor(null);
			event.setResult(Event.Result.DENY);
		}
	}

}
