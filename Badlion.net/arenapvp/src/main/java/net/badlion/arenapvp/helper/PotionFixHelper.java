package net.badlion.arenapvp.helper;

import net.badlion.arenapvp.ArenaPvP;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class PotionFixHelper {

	public static void modifyDamage(Player player, EntityDamageEvent event, int modifier) {
		if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
			for (PotionEffect Effect : player.getActivePotionEffects()) {
				if (Effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
					double Division = (Effect.getAmplifier() + 1) * 1.3D + 1.0D;
					int NewDamage;
					if (event.getDamage() / Division <= 1.0D) {
						NewDamage = (Effect.getAmplifier() + 1) * 3 + 1;
					} else {
						NewDamage = (int) (event.getDamage() / Division) + (int) ((Effect.getAmplifier() + 1) * (modifier * 0.5D));
					}
					event.setDamage(NewDamage);
					break;
				}
			}
		}
	}

	public static void modifyHealPotion(EntityRegainHealthEvent event, int modifier) {
		if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.MAGIC && (event.getAmount() > 1.0D)) {
			event.setAmount(event.getAmount() * (modifier * 0.25D));
		}
	}

	public static void modifyRegenPotion(EntityRegainHealthEvent event) {
		final LivingEntity entity = (LivingEntity) event.getEntity();
		int lvl = 0;
		Collection<PotionEffect> Effects = entity.getActivePotionEffects();
		for (PotionEffect effect : Effects) {
			if ((effect.getType().getName().equals("REGENERATION")) || (effect.getType().getName().equals("HEAL"))) {
				lvl = effect.getAmplifier() + 1;
				break;
			}
		}

		if ((event.getRegainReason() == EntityRegainHealthEvent.RegainReason.MAGIC_REGEN) && (event.getAmount() == 1.0D) && (lvl > 0)) {
			new BukkitRunnable() {
				public void run() {
					// Stop edge case with healing dead players
					if (entity.isDead() || entity.getHealth() <= 0) {
						this.cancel();
						return;
					}

					if (entity.getMaxHealth() >= entity.getHealth() + 1.0D) {
						entity.setHealth(entity.getHealth() + 1.0D);
					}
				}
			}.runTaskLater(ArenaPvP.getInstance(), 50L / (lvl * 2));
		}
	}

}
