package com.massivecraft.factions.listeners;

import club.minemen.hcfactions.HCFactions;
import club.minemen.hcfactions.utils.EntityUtil;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FactionDTREvent;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.MiscUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;

public class FactionsEntityListener implements Listener {

	private static final Set<PotionEffectType> badPotionEffects = new LinkedHashSet<PotionEffectType>(Arrays.asList(PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION, PotionEffectType.HARM, PotionEffectType.HUNGER, PotionEffectType.POISON, PotionEffectType.SLOW, PotionEffectType.SLOW_DIGGING, PotionEffectType.WEAKNESS, PotionEffectType.WITHER));

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		FactionPlayer fplayer = FPlayers.getInstance().get(player);

		if (!fplayer.getFaction().isNormal()) {
			return; // Only regular player factions use dtr
		}

		double loss = 1.0;
		if (Conf.worldDtrLoss.containsKey(player.getWorld().getName())) {
			loss = Conf.worldDtrLoss.get(player.getWorld().getName());
		}

		FactionDTREvent dtrEvent = new FactionDTREvent(player, fplayer.getFactionId(), loss);
		Bukkit.getServer().getPluginManager().callEvent(dtrEvent);
		fplayer.getFaction().alterDtr(-(dtrEvent.getDtr()));

		fplayer.getFaction().setDtrRegenCooldown(System.currentTimeMillis() + Conf.dtrDeathRegenCooldown * 60000);

		Faction myFaction = fplayer.getFaction();
		myFaction.msg("<gold>Member death: <red>%s <gold>DTR = %.2f / %.2f", fplayer.getName(), myFaction.getDtr(), myFaction.getMaxDtr());
		if (myFaction.isRaidable()) {
			myFaction.msg("<red><b>Your faction is RAIDABLE!");
		}
		HCFactions.getInstance().getLogger().log(Level.INFO, "[DTR CHANGE] {0}: {1} died, dtr is now {2}/{3}", new Object[]{myFaction.getTag(), fplayer.getName(), myFaction.getDtr(), myFaction.getMaxDtr()});
	}

	/**
	 * Who can I hurt? I can never hurt members or allies. I can always hurt
	 * enemies. I can hurt neutrals as long as they are outside their own
	 * territory.
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (Conf.safeZonePreventDamageToHorses && event.getEntity() instanceof Horse) {
			Horse horse = (Horse) event.getEntity();
			if (horse.isTamed() && Board.getFactionAt(new FLocation(horse.getLocation())).isSafeZone()) {
				if (event instanceof EntityDamageByEntityEvent) {
					Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
					if (damager instanceof Player) {
						FactionPlayer attacker = FPlayers.getInstance().get((Player) damager);
						if (attacker == null || !attacker.isAdminBypassing()) {
							event.setCancelled(true);
						}
					} else {
						event.setCancelled(true);
					}
				}
			}
		} else if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent) event;
			if (!this.canDamagerHurtDamagee(sub, false)) {
				event.setCancelled(true);
			}
		} else if (Conf.safeZonePreventAllDamageToPlayers && isPlayerInSafeZone(event.getEntity())) {
			// Players can not take any damage in a Safe Zone
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamageByEntityMonitor(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		if (Conf.worldsNoPvpTag.contains(event.getEntity().getWorld().getName())) {
			return;
		}
		Entity damager = event.getDamager();
		if (damager instanceof Projectile) {
			if (((Projectile) damager).getShooter() instanceof Entity) {
				damager = (Entity) ((Projectile) damager).getShooter();
			}
		}
		if (!(damager instanceof Player)) {
			return;
		}
		FactionPlayer attacker = FPlayers.getInstance().get((Player) damager);
		FactionPlayer victim = FPlayers.getInstance().get((Player) event.getEntity());
		if (attacker == victim) {
			return;
		}
		Relation rel = victim.getRelationTo(attacker);
		if (rel == Relation.NEUTRAL) {
			victim.pvpTag(Conf.pvpTagHurt);
			attacker.pvpTag(Conf.pvpTagAttack);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplode(EntityExplodeEvent event) {
		if (event.isCancelled()) {
			return;
		}

		// we dont want any block damage from explosions
		event.setCancelled(true);
		return;
		/*
		Location loc = event.getLocation();
        Entity boomer = event.getEntity();
        Faction faction = Board.getFactionAt(new FLocation(loc));

        if (faction.noExplosionsInTerritory()) {
            // faction is peaceful and has explosions set to disabled
            event.setCancelled(true);
            return;
        }

        boolean online = faction.hasPlayersOnline();

        if (boomer instanceof Creeper && ((faction.isNone() && Conf.wildernessBlockCreepers && !Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName())) || (faction.isNormal() && (online ? Conf.territoryBlockCreepers : Conf.territoryBlockCreepersWhenOffline)) || (faction.isWarZone() && Conf.warZoneBlockCreepers) || faction.isSafeZone())) {
            // creeper which needs prevention
            event.setCancelled(true);
        } else if ( // it's a bit crude just using fireball protection for Wither boss too, but I'd rather not add in a whole new set of xxxBlockWitherExplosion or whatever
                (boomer instanceof Fireball || boomer instanceof WitherSkull || boomer instanceof Wither) && ((faction.isNone() && Conf.wildernessBlockFireballs && !Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName())) || (faction.isNormal() && (online ? Conf.territoryBlockFireballs : Conf.territoryBlockFireballsWhenOffline)) || (faction.isWarZone() && Conf.warZoneBlockFireballs) || faction.isSafeZone())) {
            // ghast fireball which needs prevention
            event.setCancelled(true);
        } else if ((boomer instanceof TNTPrimed || boomer instanceof ExplosiveMinecart) && ((faction.isNone() && Conf.wildernessBlockTNT && !Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName())) || (faction.isNormal() && (online ? Conf.territoryBlockTNT : Conf.territoryBlockTNTWhenOffline)) || (faction.isWarZone() && Conf.warZoneBlockTNT) || (faction.isSafeZone() && Conf.safeZoneBlockTNT))) {
            // TNT which needs prevention
            event.setCancelled(true);
        } else if ((boomer instanceof TNTPrimed || boomer instanceof ExplosiveMinecart) && Conf.handleExploitTNTWaterlog) {
            // TNT in water/lava doesn't normally destroy any surrounding blocks, which is usually desired behavior, but...
            // this change below provides workaround for waterwalling providing perfect protection,
            // and makes cheap (non-obsidian) TNT cannons require minor maintenance between shots
            Block center = loc.getBlock();
            if (center.isLiquid()) {
                // a single surrounding block in all 6 directions is broken if the material is weak enough
                List<Block> targets = new ArrayList<Block>();
                targets.add(center.getRelative(0, 0, 1));
                targets.add(center.getRelative(0, 0, -1));
                targets.add(center.getRelative(0, 1, 0));
                targets.add(center.getRelative(0, -1, 0));
                targets.add(center.getRelative(1, 0, 0));
                targets.add(center.getRelative(-1, 0, 0));
                for (Block target : targets) {
                    int id = target.getTypeId();
                    // ignore air, bedrock, water, lava, obsidian, enchanting table, etc.... too bad we can't getAll a blast resistance value through Bukkit yet
                    if (id != 0 && (id < 7 || id > 11) && id != 49 && id != 90 && id != 116 && id != 119 && id != 120 && id != 130) {
                        target.breakNaturally();
                    }
                }
            }
        }*/
	}

	// mainly for flaming arrows; don't want allies or people in safe zones to be ignited even after damage event is cancelled
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityCombustByEntity(EntityCombustByEntityEvent event) {
		if (event.isCancelled()) {
			return;
		}

		// TODO: this even needed?
		EntityDamageByEntityEvent sub = new EntityDamageByEntityEvent(event.getCombuster(), event.getEntity(), EntityDamageEvent.DamageCause.FIRE, 0);
		if (!this.canDamagerHurtDamagee(sub, false)) {
			event.setCancelled(true);
		}
		sub = null;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPotionSplashEvent(PotionSplashEvent event) {

		// see if the potion has a harmful effect
		boolean badPotion = false;
		for (PotionEffect effect : event.getPotion().getEffects()) {
			if (badPotionEffects.contains(effect.getType())) {
				badPotion = true;
				break;
			}
		}
		if (!badPotion) {
			return;
		}

		boolean shooterSafezone = false;
		if (event.getPotion().getShooter() instanceof Player) {
			if (Board.getFactionAt(new FLocation(((Player) event.getPotion().getShooter()).getLocation())).noPvPInTerritory()) {
				shooterSafezone = true;
			}
		}

		// cancel potion splashing on players that are in safezone, or if shooter is in safezone
		for (LivingEntity target : event.getAffectedEntities()) {
			if (target instanceof Player && (shooterSafezone || Board.getFactionAt(new FLocation(target.getLocation())).noPvPInTerritory())) {
				event.setIntensity(target, 0.0);
			}
		}
	}

	public boolean isPlayerInSafeZone(Entity damagee) {
		if (!(damagee instanceof Player)) {
			return false;
		}
		if (Board.getFactionAt(new FLocation(damagee.getLocation())).isSafeZone()) {
			return true;
		}
		return false;
	}

	public boolean canDamagerHurtDamagee(EntityDamageByEntityEvent sub, boolean notify) {
		Entity damager = sub.getDamager();
		Entity damagee = sub.getEntity();

		if (!(damagee instanceof Player)) {
			return true;
		}

		Player player = (Player) damagee;

		// hack, no notify if the player is in no damage time, prevents spamming of notifications
		if ((float) player.getNoDamageTicks() > (float) player.getMaximumNoDamageTicks() / 2.0F && sub.getDamage() <= player.getLastDamage()) {
			notify = false;
		}

		FactionPlayer defender = FPlayers.getInstance().get((Player) damagee);

		if (defender == null) // || defender.getPlayer() == null)
		{
			return true;
		}

		Location defenderLoc = damagee.getLocation();
		Faction defLocFaction = Board.getFactionAt(new FLocation(defenderLoc));

		// for damage caused by projectiles, getDamager() returns the projectile... what we need to know is the source
		if (damager instanceof Projectile) {
			if (((Projectile) damager).getShooter() instanceof Entity) {
				damager = (Entity) ((Projectile) damager).getShooter();
			}
		}

		if (damager == damagee) // ender pearl usage and other self-inflicted damage
		{
			return true;
		}

		// Players can not take attack damage in a SafeZone, or possibly peaceful territory
		if (defLocFaction.noPvPInTerritory()) {
			if (damager instanceof Player) {
				if (notify) {
					FactionPlayer attacker = FPlayers.getInstance().get((Player) damager);
					attacker.msg("<instance>You can't hurt other players in " + (defLocFaction.isSafeZone() ? "a SafeZone." : "peaceful territory."));
				}
				return false;
			}
			return !defLocFaction.noMonstersInTerritory();
		}

		if (!(damager instanceof Player)) {
			return true;
		}

		FactionPlayer attacker = FPlayers.getInstance().get((Player) damager);

		if (attacker == null || attacker.getPlayer() == null) {
			return true;
		}

		if (Conf.playersWhoBypassAllProtection.contains(attacker.getName())) {
			return true;
		}

		Faction locFaction = Board.getFactionAt(new FLocation(attacker));

		// so we know from above that the defender isn't in a safezone... what about the attacker, sneaky dog that he might be?
		if (locFaction.noPvPInTerritory()) {
			if (notify) {
				attacker.msg("<instance>You can't hurt other players while you are in " + (locFaction.isSafeZone() ? "a SafeZone." : "peaceful territory."));
			}
			return false;
		}

		if (locFaction.isWarZone() && Conf.warZoneFriendlyFire) {
			return true;
		}

		if (Conf.worldsIgnorePvP.contains(defenderLoc.getWorld().getName())) {
			return true;
		}

		Faction defendFaction = defender.getFaction();
		Faction attackFaction = attacker.getFaction();

		if (defendFaction.isPeaceful()) {
			if (notify) {
				attacker.msg("<instance>You can't hurt players who are in a peaceful faction.");
			}
			return false;
		} else if (attackFaction.isPeaceful()) {
			if (notify) {
				attacker.msg("<instance>You can't hurt players while you are in a peaceful faction.");
			}
			return false;
		}

		Relation relation = defendFaction.getRelationTo(attackFaction);

		// Players without faction may be hurt anywhere
		if (!defender.hasFaction()) {
			return true;
		}

		// You can never hurt faction members or allies
		if (relation.isMember()) {
			if (notify) {
				attacker.msg("<instance>You can't hurt %s<instance>.", defender.describeTo(attacker));
			}
			return false;
		}
		if (relation.isAlly()) {
			if (Conf.canHurtAllies) {
				if (notify) {
					attacker.msg("<instance>Be careful, that's your ally: %s<instance>.", defender.describeTo(attacker));
				}
			} else {
				if (notify) {
					attacker.msg("<instance>You can't hurt %s<instance>.", defender.describeTo(attacker));
				}
				return false;
			}
		}

		return true;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.isCancelled() || event.getLocation() == null || event.getSpawnReason() == SpawnReason.CUSTOM) {
			return;
		}

		Faction faction = Board.getFactionAt(new FLocation(event.getLocation()));

		if (Conf.safeZoneNerfedCreatureTypes.contains(event.getEntityType()) && faction.noMonstersInTerritory()) {
			event.setCancelled(true);
		}

		if (faction.isNormal()) {
			event.getEntity().setCanPickupItems(false);
			if (event.getEntity().getType() == EntityType.ZOMBIE || event.getEntity().getType() == EntityType.SKELETON) {
				EntityUtil.clearAIExceptFloat((Creature) event.getEntity());
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityTarget(EntityTargetEvent event) {

		// if there is a target
		Entity target = event.getTarget();
		if (target == null) {
			return;
		}

		final Entity entity = event.getEntity();

		Faction at = Board.getFactionAt(new FLocation(target.getLocation()));

		// in case the target is in a safe zone.
		if (Conf.safeZoneNerfedCreatureTypes.contains(MiscUtil.creatureTypeFromEntity(entity)) && at.noMonstersInTerritory()) {
			event.setCancelled(true);
		}

		if (at.isNormal()) {
			if (entity instanceof LivingEntity) {
				((LivingEntity) entity).setCanPickupItems(false);
			}
			EntityType type = entity.getType();
			if (type == EntityType.ZOMBIE || type == EntityType.SKELETON || type == EntityType.SPIDER || type == EntityType.CAVE_SPIDER) {
				event.setCancelled(true);
				if (type == EntityType.ZOMBIE || type == EntityType.SKELETON) {
					// remove target and goal selectors on a task, this is required or the server crashes
					new BukkitRunnable() {
						@Override
						public void run() {
							EntityUtil.clearAIExceptFloat((Creature) entity);
						}
					}.runTask(HCFactions.getInstance());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPaintingBreak(HangingBreakEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (event.getCause() == RemoveCause.EXPLOSION) {
			Location loc = event.getEntity().getLocation();
			Faction faction = Board.getFactionAt(new FLocation(loc));
			if (faction.noExplosionsInTerritory()) {
				// faction is peaceful and has explosions set to disabled
				event.setCancelled(true);
				return;
			}

			boolean online = faction.hasPlayersOnline();

			if ((faction.isNone() && !Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName()) && (Conf.wildernessBlockCreepers || Conf.wildernessBlockFireballs || Conf.wildernessBlockTNT)) || (faction.isNormal() && (online ? (Conf.territoryBlockCreepers || Conf.territoryBlockFireballs || Conf.territoryBlockTNT) : (Conf.territoryBlockCreepersWhenOffline || Conf.territoryBlockFireballsWhenOffline || Conf.territoryBlockTNTWhenOffline))) || (faction.isWarZone() && (Conf.warZoneBlockCreepers || Conf.warZoneBlockFireballs || Conf.warZoneBlockTNT)) || faction.isSafeZone()) {
				// explosion which needs prevention
				event.setCancelled(true);
			}
		}

		if (!(event instanceof HangingBreakByEntityEvent)) {
			return;
		}

		Entity breaker = ((HangingBreakByEntityEvent) event).getRemover();
		if (!(breaker instanceof Player)) {
			return;
		}

		if (!FactionsBlockListener.playerCanBuildDestroyBlock((Player) breaker, event.getEntity().getLocation(), "remove paintings", false)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPaintingPlace(HangingPlaceEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if (!FactionsBlockListener.playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), "place paintings", false)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Entity entity = event.getEntity();

		// for now, only interested in Enderman and Wither boss tomfoolery
		if (!(entity instanceof Enderman) && !(entity instanceof Wither)) {
			return;
		}

		Location loc = event.getBlock().getLocation();

		if (entity instanceof Enderman) {
			if (stopEndermanBlockManipulation(loc)) {
				event.setCancelled(true);
			}
		} else if (entity instanceof Wither) {
			Faction faction = Board.getFactionAt(new FLocation(loc));
			// it's a bit crude just using fireball protection, but I'd rather not add in a whole new set of xxxBlockWitherExplosion or whatever
			if ((faction.isNone() && Conf.wildernessBlockFireballs && !Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName())) || (faction.isNormal() && (faction.hasPlayersOnline() ? Conf.territoryBlockFireballs : Conf.territoryBlockFireballsWhenOffline)) || (faction.isWarZone() && Conf.warZoneBlockFireballs) || faction.isSafeZone()) {
				event.setCancelled(true);
			}
		}
	}

	private boolean stopEndermanBlockManipulation(Location loc) {
		if (loc == null) {
			return false;
		}
		// quick check to see if all Enderman deny options are enabled; if so, no need to check location
		if (Conf.wildernessDenyEndermanBlocks && Conf.territoryDenyEndermanBlocks && Conf.territoryDenyEndermanBlocksWhenOffline && Conf.safeZoneDenyEndermanBlocks && Conf.warZoneDenyEndermanBlocks) {
			return true;
		}

		FLocation fLoc = new FLocation(loc);
		Faction claimFaction = Board.getFactionAt(fLoc);

		if (claimFaction.isNone()) {
			return Conf.wildernessDenyEndermanBlocks;
		} else if (claimFaction.isNormal()) {
			return claimFaction.hasPlayersOnline() ? Conf.territoryDenyEndermanBlocks : Conf.territoryDenyEndermanBlocksWhenOffline;
		} else if (claimFaction.isSafeZone()) {
			return Conf.safeZoneDenyEndermanBlocks;
		} else if (claimFaction.isWarZone()) {
			return Conf.warZoneDenyEndermanBlocks;
		}

		return false;
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (Conf.safeZonePreventHungerLoss && event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (event.getFoodLevel() < player.getFoodLevel()) {
				if (isPlayerInSafeZone(player)) {
					event.setCancelled(true);
				}
			}
		}
	}

    /*
     @EventHandler
     public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
     {
     if (event.getRightClicked() instanceof Horse)
     {
     Horse horse = (Horse) event.getRightClicked();
     if (Board.getFactionAt(new FLocation(horse.getLocation())).isSafeZone())
     {
     if (horse.isTamed() && horse.getOwner() != event.getPlayer())
     {
     FactionPlayer fp = FPlayers.getInstance().getAll(event.getPlayer());
     if (fp == null || !fp.isAdminBypassing())
     {
     event.getPlayer().sendMessage(ChatColor.RED + "Sorry this horse belongs to " + horse.getOwner().getName());
     event.setCancelled(true);
     }
     }
     }
     }
     }
     */
}
