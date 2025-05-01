package net.badlion.potpvp.rulesets;

import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.bukkitevents.KitLoadEvent;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.ladders.MatchLadder;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.matchmaking.QueueService;
import net.badlion.potpvp.states.matchmaking.GameState;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.TippedArrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class ArcherRuleSet extends KitRuleSet {

    public ArcherRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.BOW), ArenaManager.ArenaType.ARCHER, false, false);

	    this.is1_9Compatible = true;

	    // Enable in duels
	    this.enabledInDuels = true;

        Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.OneVsOneRanked, true, true));
        Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.TwoVsTwoRanked, true, true));

	    // Speed 1
	    this.potionEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));

	    // Create default armor kit
	    this.defaultArmorKit[3] = new ItemStack(Material.LEATHER_HELMET);
	    this.defaultArmorKit[2] = new ItemStack(Material.LEATHER_CHESTPLATE);
	    this.defaultArmorKit[1] = new ItemStack(Material.LEATHER_LEGGINGS);
	    this.defaultArmorKit[0] = new ItemStack(Material.LEATHER_BOOTS);

	    // Create default inventory kit
	    this.defaultInventoryKit[0] = new ItemStack(Material.BOW);
        this.defaultInventoryKit[0].addEnchantment(Enchantment.ARROW_INFINITE, 1);
        this.defaultInventoryKit[0].addEnchantment(Enchantment.ARROW_KNOCKBACK, 2);
	    ItemStackUtil.addUnbreaking(this.defaultInventoryKit[0]);

	    this.defaultInventoryKit[1] = new ItemStack(Material.ARROW);
        this.defaultInventoryKit[3] = new ItemStack(Material.ENDER_PEARL, 3);
        this.defaultInventoryKit[8] = new ItemStack(Material.COOKED_BEEF, 64);

	    if (PotPvP.getInstance().getServer().getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
		    this.defaultInventoryKit[3] = new ItemStack(Material.TIPPED_ARROW);
		    PotionMeta potionMeta = (PotionMeta) this.defaultInventoryKit[3].getItemMeta();
		    potionMeta.setBasePotionData(new PotionData(PotionType.POISON));
		    this.defaultInventoryKit[3].setItemMeta(potionMeta);

		    this.defaultInventoryKit[5] = new ItemStack(Material.TIPPED_ARROW);
		    potionMeta = (PotionMeta) this.defaultInventoryKit[5].getItemMeta();
		    potionMeta.setBasePotionData(new PotionData(PotionType.SPEED, false, true));
		    this.defaultInventoryKit[5].setItemMeta(potionMeta);
	    }

        // Initialize info signs
	    this.info2Sign[0] = "================";
	    this.info2Sign[1] = "§5Archer";
	    this.info2Sign[2] = "";
		this.info2Sign[3] = "================";

	    this.info4Sign[0] = "§dLeather Armor";
	    this.info4Sign[1] = "No Enchants";
	    this.info4Sign[2] = "";
	    this.info4Sign[3] = "";

	    this.info5Sign[0] = "§dBow";
	    this.info5Sign[1] = "Infinity";
        this.info5Sign[2] = "Punch II";
        this.info5Sign[3] = "";

	    this.info6Sign[0] = "§dPotions";
	    this.info6Sign[1] = "Speed I";
        this.info6Sign[2] = "";
        this.info6Sign[3] = "";

	    this.info8Sign[0] = "§dOther";
        this.info8Sign[1] = "3 Enderpearls";
	    this.info8Sign[2] = "64 Steak";
	    this.info8Sign[3] = "";

	    // Knockback changes
	    this.knockbackFriction = 2.0;
	    this.knockbackHorizontal = 0.34;
	    this.knockbackVertical = 0.34;
	    this.knockbackVerticalLimit = 0.4;
	    this.knockbackExtraHorizontal = 0.425;
	    this.knockbackExtraVertical = 0.085;
    }

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

				// Tipped arrows
				if (PotPvP.getInstance().getServer().getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
					if (itemStack.getType() == Material.TIPPED_ARROW) {
						// Remove this item if this is not the archer kit
						if (!(event.getKitRuleSet() instanceof ArcherRuleSet)) {
							event.getPlayer().getInventory().remove(itemStack);
						} else {
							// Make sure they only have ONE arrow
							itemStack.setAmount(1);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onKitLoadEvent(KitLoadEvent event) {
		if (this == event.getKitRuleSet()) {
			for (ItemStack itemStack : event.getPlayer().getInventory().getContents()) {
				if (itemStack == null) continue;

				if (itemStack.getType() == Material.BOW) {
					ItemStackUtil.addUnbreaking(itemStack);
				}
			}
		}
	}

    @EventHandler(priority= EventPriority.MONITOR)
    public void onPlayerFall(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Group group = PotPvP.getInstance().getPlayerGroup(event.getEntity());

            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerHeal(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            Group group = PotPvP.getInstance().getPlayerGroup(event.getEntity());

            if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
                event.setCancelled(true);
            }
        }
    }

	@EventHandler(priority= EventPriority.LAST, ignoreCancelled=true)
	public void onArrowHit(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player damaged = (Player) event.getEntity();
			Group group = PotPvP.getInstance().getPlayerGroup(damaged);

			if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
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
							damager.sendMessage(ChatColor.GOLD + damaged.getName() + ChatColor.DARK_AQUA + " is now at " + ChatColor.GOLD +
									Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2D + ChatColor.DARK_RED + " ♥");
						}

						if (PotPvP.getInstance().getServer().getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
							// Is this the speed 2 tipped arrow?
							Arrow arrow = ((Arrow) event.getDamager());
							if (arrow instanceof TippedArrow) {
								TippedArrow tippedArrow = (TippedArrow) arrow;
								if (tippedArrow.getBasePotionData().getType() == PotionType.SPEED) {
									// Override and add 2 minutes of speed 2
									damaged.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1600, 1), true);
								}
							}
						}
					}
				} else if (event.getDamager() instanceof Player) {
					// Cast to Player because of 1.7 vs 1.9 Spigot
					((Player) event.getDamager()).sendMessage(ChatColor.RED + "Cannot punch other players while in archer.");
					event.setCancelled(true);
				}
			}
		}
	}

}