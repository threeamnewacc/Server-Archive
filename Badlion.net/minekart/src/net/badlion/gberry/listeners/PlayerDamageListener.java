package net.badlion.gberry.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.tinywebteam.badlion.MineKart;
import com.tinywebteam.badlion.Racer;

public class PlayerDamageListener implements Listener {
	
	private MineKart plugin;
	
	public PlayerDamageListener(MineKart plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		// Don't let any damage happen
		event.setCancelled(true);
		event.getEntity().setFireTicks(0);
	}
	
	@EventHandler
	public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
		
		if (event.getDamager() instanceof Snowball) {
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				Racer racer = this.plugin.getPlayerToRacer().get(player);
				// Shouldn't ever not be null..
				if (racer != null) {
					// GET SLOWED SON
					racer.getHorse().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 2));
					player.sendMessage(ChatColor.BLUE + "Hit by snowball.  Slowed down,");
				}
			} else if (event.getEntity() instanceof Horse) {
				Player player = (Player) event.getEntity().getPassenger();
				Racer racer = this.plugin.getPlayerToRacer().get(player);
				// Shouldn't ever not be null..
				if (racer != null) {
					// GET SLOWED SON
					racer.getHorse().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 2));
					player.sendMessage(ChatColor.BLUE + "Hit by snowball.  Slowed down,");
				}
			}
		}
	}

}
