package net.badlion.gberry.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.tinywebteam.badlion.MineKart;
import com.tinywebteam.badlion.Racer;

public class ProjectileLaunchListener implements Listener {

	private MineKart plugin;
	
	public ProjectileLaunchListener(MineKart plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		if (event.getEntity() instanceof ThrownPotion) {
			ThrownPotion thrownPotion = (ThrownPotion) event.getEntity();
			List<PotionEffect> potionEffects = (List<PotionEffect>) thrownPotion.getEffects();
			for (PotionEffect potionEffect : potionEffects) {
				if (potionEffect.getType().equals(PotionEffectType.SPEED)) {
					if (event.getEntity().getShooter() instanceof Player) {
						Player player = (Player) event.getEntity().getShooter();
						Racer racer = this.plugin.getPlayerToRacer().get(player);
						// Shouldn't ever not be null..
						if (racer != null) {
							// GET SLOWED SON
							racer.getHorse().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 5), true);
						}
					}
					break;
				} else if (potionEffect.getType().equals(PotionEffectType.FIRE_RESISTANCE)) {
					if (event.getEntity().getShooter() instanceof Player) {
						Player player = (Player) event.getEntity().getShooter();
						Racer racer = this.plugin.getPlayerToRacer().get(player);
						// Shouldn't ever not be null..
						if (racer != null) {
							// GET SLOWED SON
							ArrayList<Racer> racers = racer.getRace().getRacersStillInRace();
							for (Racer r : racers) {
								if (r.equals(racer)) {
									continue;
								}
								
								r.getPlayer().sendMessage(ChatColor.RED + "You have been slowed for 5 seconds by a global slow potion.");
								r.getHorse().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 3), true);
							}
						}
					}
					break;
				}
			}
			event.getEntity().remove();
		}
	}
	
}
