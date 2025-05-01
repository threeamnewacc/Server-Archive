package net.badlion.gfactions.listeners;

import net.badlion.gfactions.GFactions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionReverterListener implements Listener {

    private GFactions plugin;

    public PotionReverterListener(GFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.FIRST)
    public void playerHitPlayer(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player player = (Player) e.getDamager();
            // Revert Strength 2 potions
            if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                for (PotionEffect Effect : player.getActivePotionEffects()) {
                    if (Effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                        double division = (Effect.getAmplifier() + 1) * 1.3D + 1.0D;
						if (e.getDamage() / division <= 1.0D) {
					   		e.setDamage((Effect.getAmplifier() + 1) * 3 + 1);
						} else {
							e.setDamage((int)(e.getDamage() / division) + (int)((Effect.getAmplifier() + 1) * 3D));
						}
//                        if (division == 3.6) { // Str 2
//                            //System.out.println("IS STR 2");
//                            double newDamage = (e.getDamage() / division) * 2.5D; // 150% more damage
//                            //System.out.println("New Damage: " + newDamage);
//                            e.setDamage(newDamage);
//                        } else if (division == 2.3) { // Str 1
//                            //System.out.println("IS STR 1");
//                            double newDamage = (e.getDamage() / division) * 1.8D; // 150% more damage
//                            //System.out.println("New Damage: " + newDamage);
//                            e.setDamage(newDamage);
//                        }
                        break;
                    }
                }
            }
        }
    }

	@EventHandler
	public void instantDamageThrown(PotionSplashEvent event) {
		for (PotionEffect potionEffect : event.getPotion().getEffects()) {
			if (potionEffect.getType().equals(PotionEffectType.HARM)) {
				event.setCancelled(true);
			}
		}
	}

	/*@EventHandler
	public void onRegen(EntityRegainHealthEvent event)
	{
		final LivingEntity entity = (LivingEntity)event.getEntity();
		int lvl = 0;
		Collection<PotionEffect> Effects = entity.getActivePotionEffects();
		for (PotionEffect effect : Effects) {
			if ((effect.getType() == PotionEffectType.REGENERATION) || (effect.getType() == PotionEffectType.HEAL))
			{
				lvl = effect.getAmplifier() + 1;
				break;
			}
		}
		if ((event.getRegainReason() == EntityRegainHealthEvent.RegainReason.MAGIC_REGEN) && (event.getAmount() == 1.0D) && (lvl > 0))
		{
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
				public void run()
				{
					if (entity.getMaxHealth() >= entity.getHealth() + 1.0D) {
						entity.setHealth(entity.getHealth() + 1.0D);
					}
				}
			}, 50L / (lvl * 2));
		}
	}*/

}
