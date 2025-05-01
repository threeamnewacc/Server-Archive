package net.badlion.gfactions.listeners;

import org.bukkit.Material;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;

import net.badlion.gfactions.GFactions;

public class DragonListener implements Listener {
	
	private GFactions plugin;
	
	public DragonListener(GFactions plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void enderDragonEnterPortalEvent(EntityPortalEvent e) {
		// Don't let the Ender Dragon enter the end portal...
		if (e.getEntityType().equals(EntityType.ENDER_DRAGON)) {
			e.setCancelled(true);
		}
	}

    @EventHandler
    public void dontTouchMyGodDamnEgg(PlayerInteractEvent e) {
        if (e.getClickedBlock() != null && e.getClickedBlock().getType().equals(Material.DRAGON_EGG)) {
            e.setCancelled(true);
        }
    }

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EnderDragon) {
			// Ok this is our dragon event
			this.plugin.getDragonEvent().endEvent(true, entity.getLocation(), ((EnderDragon) entity).getKiller());
		}
	}
	
	@EventHandler
	public void onPortalCreate(EntityCreatePortalEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }
	
	@EventHandler
	public void onDragonDamage(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		if (damager instanceof EnderDragon) {
			// Adjust dragon's damage according to it's health
			double percentHealth = ((EnderDragon) damager).getHealth() / ((EnderDragon) damager).getMaxHealth();
			if (percentHealth > 0.75) {
				event.setDamage(event.getDamage() * 1.86);
			} else if (percentHealth > 0.5) {
				event.setDamage(event.getDamage() * 3);
			} else if (percentHealth > 0.25) {
				event.setDamage(event.getDamage() * 5);
			} else {
				event.setDamage(event.getDamage() * 7.42);
			}
		}
	}

    @EventHandler
    public void onEntityExplodeEnvironment(EntityExplodeEvent e) {
        if (e.getEntity() instanceof EnderDragon) {
            e.blockList().clear();
        }
    }

}
