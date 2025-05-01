package net.badlion.gguard.listeners;

import net.badlion.gguard.GGuard;
import net.badlion.gguard.ProtectedRegion;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DamageListener implements Listener {

	private Set<EntityType> mobEntities = new HashSet<>();
	
	public DamageListener() {
		this.mobEntities.add(EntityType.ZOMBIE);
		this.mobEntities.add(EntityType.SKELETON);
		this.mobEntities.add(EntityType.CREEPER);
		this.mobEntities.add(EntityType.SILVERFISH);
		this.mobEntities.add(EntityType.SPIDER);
		this.mobEntities.add(EntityType.ENDER_DRAGON);
		this.mobEntities.add(EntityType.WOLF);
		this.mobEntities.add(EntityType.BLAZE);
		this.mobEntities.add(EntityType.CAVE_SPIDER);
		this.mobEntities.add(EntityType.ENDERMAN);
		this.mobEntities.add(EntityType.GHAST);
		this.mobEntities.add(EntityType.SLIME);
		this.mobEntities.add(EntityType.PIG_ZOMBIE);
		this.mobEntities.add(EntityType.WITCH);
		this.mobEntities.add(EntityType.WITHER);
		this.mobEntities.add(EntityType.IRON_GOLEM);
		this.mobEntities.add(EntityType.MAGMA_CUBE);
		this.mobEntities.add(EntityType.GIANT);
		
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onDamageEvent(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		Entity attacker = event.getDamager();
		ProtectedRegion region = GGuard.getInstance().getProtectedRegion(entity.getLocation(), GGuard.getInstance().getProtectedRegions());
		if (region != null && region.isChangeMobDamageToPlayer()) {
			// Increase mob damage to players
			// Target is a player && attacker is not a player && not(attacker is a projectile && a player shot it)
			if (entity instanceof Player && !(attacker instanceof Player) && !((attacker instanceof Projectile) && ((Projectile) attacker).getShooter() instanceof Player)) {
				event.setDamage(event.getDamage() * region.getDamageMultiplier());
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onMobDamage(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		Entity attacker = event.getDamager();
		ProtectedRegion region = GGuard.getInstance().getProtectedRegion(entity.getLocation(), GGuard.getInstance().getProtectedRegions());
		if (region != null && !region.isAllowMobDamage()) {
			// Check if this is a mob or a projectile of a mob
			if (attacker instanceof Projectile && ((Projectile) attacker).getShooter() instanceof LivingEntity) {
				LivingEntity shooter = (LivingEntity) ((Projectile) attacker).getShooter();
				if (this.mobEntities.contains(shooter.getType())) {
					event.setCancelled(true);
					event.setDamage(0);
				}
			} else if (this.mobEntities.contains(attacker.getType())) {
				event.setCancelled(true);
				event.setDamage(0);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onWitchPotionHit(PotionSplashEvent event) {
		if (event.getEntity().getShooter() instanceof LivingEntity) {
			if (((LivingEntity) event.getEntity().getShooter()).getType() == EntityType.WITCH) {
				// Check to see if the affected entity is a player and they are in a protection region
				Collection<LivingEntity> entities = event.getAffectedEntities();
				for (Entity entity : entities) {
					if (entity instanceof Player) {
						ProtectedRegion region = GGuard.getInstance().getProtectedRegion(entity.getLocation(), GGuard.getInstance().getProtectedRegions());
						if (region != null && !region.isAllowMobDamage()) {
							event.setIntensity((LivingEntity) entity, 0.0);
						}
					}
				}
			}
		}
	}

}
