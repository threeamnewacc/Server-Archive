package net.badlion.gfactions.listeners;

import net.badlion.gfactions.GFactions;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;

public class GodItemListener implements Listener {
	
	private GFactions plugin;
	
	public GodItemListener(GFactions plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void godWeaponHit(EntityDamageByEntityEvent event) {
        Entity target = event.getEntity();
        Entity damager = event.getDamager();

        if (damager instanceof Player || damager instanceof Arrow) {
            // Possible god loot effects
            ItemStack item = null;
            if (damager instanceof Player) {
                item = ((Player) damager).getItemInHand();
            } else {
				if (((Arrow)damager).getShooter() instanceof Player) {
                	item = ((Player) ((Arrow) damager).getShooter()).getItemInHand();
				}
            }
            if (item == null) {
                return;
            }

            // Make sure it's a diamond sword or a bow
            if (!item.getType().equals(Material.DIAMOND_SWORD) && !item.getType().equals(Material.BOW)) {
                return;
            }

            ItemMeta meta = item.getItemMeta();

            if (meta == null) {
                return;
            }

            if (meta.getLore() == null) {
                return;
            }

            String[] pieces = meta.getLore().get(meta.getLore().size() - 1).split(" ");
            int bitMask = 0;

            try {
                bitMask = Integer.parseInt(pieces[0]);
            } catch (NumberFormatException e) {
                return; // Not a God item
            }

            // Split numbers off of end and convert
            int[] effectValues = null;
            if (pieces.length == 2) {
                String[] tmp = pieces[1].split(":");
                effectValues = new int[tmp.length];
                for (int i = 0; i < tmp.length; i++) {
                    try {
                        effectValues[i] = Integer.parseInt(tmp[i]);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }

			// Make intellij happy
			if (effectValues == null) {
				return;
			}

            // Ok this is a god loot item, lets figure out what it is
            // First bit: extra damage
            // Second bit: weakness
            // Third bit: slowness
            // Fourth bit: nausea
            // Fifth bit: bleed
            // 10101 <-- bleed, slowness, extra damage

            int i = 0;

            // Extra Damage
            if ((bitMask & 0x1) > 0) {
                event.setDamage(event.getDamage() * (((double) effectValues[i] / 100) + 1));
                i++;
            }

            if ((bitMask & 0x10) > 0) { // Bleed
                if (this.plugin.generateRandomInt(1, 100) <= 3) { // 3% chance
                    if (target instanceof LivingEntity) {
                        boolean flag = false;
                        Collection<PotionEffect> effects = ((LivingEntity) target).getActivePotionEffects();
                        for (PotionEffect effect : effects) {
                            if (effect.getType() == PotionEffectType.POISON) {
                                int remaining = effect.getDuration();
                                int ticksRemaining = remaining * 20;
                                ticksRemaining += effectValues[i] * 20;
                                int amplifier = effect.getAmplifier();
                                ((LivingEntity) target).removePotionEffect(PotionEffectType.POISON);
                                ((LivingEntity) target).addPotionEffect(new PotionEffect(PotionEffectType.POISON, ticksRemaining, amplifier));
                                flag = true;
                                break;
                            }
                        }

                        if (!flag) {
                            ((LivingEntity) target).removePotionEffect(PotionEffectType.POISON);
                            ((LivingEntity) target).addPotionEffect(new PotionEffect(PotionEffectType.POISON, effectValues[i] * 20, 0));
                        }
                    }
                }
            } else if ((bitMask & 0x8) > 0) { // Nausea
                if (this.plugin.generateRandomInt(1, 100) <= 3) { // 3% chance
                    if (target instanceof LivingEntity) {
                        boolean flag = false;
                        Collection<PotionEffect> effects = ((LivingEntity) target).getActivePotionEffects();
                        for (PotionEffect effect : effects) {
                            if (effect.getType() == PotionEffectType.CONFUSION) {
                                int remaining = effect.getDuration();
                                int ticksRemaining = remaining * 20;
                                ticksRemaining += effectValues[i] * 20;
                                int amplifier = effect.getAmplifier();
                                ((LivingEntity) target).removePotionEffect(PotionEffectType.CONFUSION);
                                ((LivingEntity) target).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, ticksRemaining, amplifier));
                                flag = true;
                                break;
                            }
                        }

                        if (!flag) {
                            ((LivingEntity) target).removePotionEffect(PotionEffectType.CONFUSION);
                            ((LivingEntity) target).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, effectValues[i] * 20, 0));
                        }
                    }
                }
            } else if ((bitMask & 0x4) > 0) { // Slowness
                if (this.plugin.generateRandomInt(1, 100) <= 3) { // 3% chance
                    if (target instanceof LivingEntity) {
                        boolean flag = false;
                        Collection<PotionEffect> effects = ((LivingEntity) target).getActivePotionEffects();
                        for (PotionEffect effect : effects) {
                            if (effect.getType() == PotionEffectType.SLOW) {
                                int remaining = effect.getDuration();
                                int ticksRemaining = remaining * 20;
                                ticksRemaining += effectValues[i] * 20;
                                int amplifier = effect.getAmplifier();
                                ((LivingEntity) target).removePotionEffect(PotionEffectType.SLOW);
                                ((LivingEntity) target).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, ticksRemaining, amplifier));
                                flag = true;
                                break;
                            }
                        }

                        if (!flag) {
                            ((LivingEntity) target).removePotionEffect(PotionEffectType.SLOW);
                            ((LivingEntity) target).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, effectValues[i] * 20, 0));
                        }
                    }
                }
            } else if ((bitMask & 0x2) > 0) { // Weakness
                if (this.plugin.generateRandomInt(1, 100) <= 3) { // 3% chance
                    if (target instanceof LivingEntity) {
                        boolean flag = false;
                        Collection<PotionEffect> effects = ((LivingEntity) target).getActivePotionEffects();
                        for (PotionEffect effect : effects) {
                            if (effect.getType() == PotionEffectType.WEAKNESS) {
                                int remaining = effect.getDuration();
                                int ticksRemaining = remaining * 20;
                                ticksRemaining += effectValues[i] * 20;
                                int amplifier = effect.getAmplifier();
                                ((LivingEntity) target).removePotionEffect(PotionEffectType.WEAKNESS);
                                ((LivingEntity) target).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, ticksRemaining, amplifier));
                                flag = true;
                                break;
                            }
                        }

                        if (!flag) {
                            ((LivingEntity) target).removePotionEffect(PotionEffectType.WEAKNESS);
                            ((LivingEntity) target).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, effectValues[i] * 20, 0));
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void hitGodArmorPlayer(EntityDamageByEntityEvent event) {
        final Entity target = event.getEntity();

        if (target instanceof Player) {
            for (ItemStack item : ((Player) target).getInventory().getArmorContents()) {
                ItemMeta meta = item.getItemMeta();

                if (meta == null) {
                    continue;
                }

                if (meta.getLore() == null) {
                    continue;
                }

                String[] pieces = meta.getLore().get(meta.getLore().size() - 1).split(" ");
                int bitMask = 0;

                try {
                    bitMask = Integer.parseInt(pieces[0]);
                } catch (NumberFormatException e) {
                    continue; // Not a God item
                }

                // Split numbers off of end and convert
                int[] effectValues = null;
                if (pieces.length == 2) {
                    String[] tmp = pieces[1].split(":");
                    effectValues = new int[tmp.length];
                    for (int i = 0; i < tmp.length; i++) {
                        try {
                            effectValues[i] = Integer.parseInt(tmp[i]);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            continue;
                        }
                    }
                }

                // Ok this is a god loot item, lets figure out what it is
                // First bit: avoid fall damage if distance under n
                // Second bit: % chance to reflect damage
                // Third bit: % chance to block damage
                // Fourth bit: % chance to avoid weakness
                // Fifth bit: % chance to avoid slowness
                // Sixth bit: % chance to avoid poison
                // Seventh bit: % chance to avoid nausea
                // 0100101 <-- no poison, % chance to block damage, avoid fall damage if distance under n

				// Make intellij happy
				if (effectValues == null) {
					return;
				}

                int i = 0;

                if ((bitMask & 0x40) > 0) { // % chance to avoid nausea
                    if (((Player) target).hasPotionEffect(PotionEffectType.CONFUSION)) {
                        if (this.plugin.generateRandomInt(1, 100) <= effectValues[i]) {
                            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
                                @Override
                                public void run() {
                                    ((Player) target).removePotionEffect(PotionEffectType.CONFUSION);
                                }
                            }, 1L);
                        }
                    }
                } else if ((bitMask & 0x20) > 0) { // % chance to avoid poison
                    if (((Player) target).hasPotionEffect(PotionEffectType.POISON)) {
                        if (this.plugin.generateRandomInt(1, 100) <= effectValues[i]) {
                            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
                                @Override
                                public void run() {
                                    ((Player) target).removePotionEffect(PotionEffectType.POISON);
                                }
                            }, 1L);
                        }
                    }
                } else if ((bitMask & 0x10) > 0) { // % chance to avoid slowness
                    if (((Player) target).hasPotionEffect(PotionEffectType.SLOW)) {
                        if (this.plugin.generateRandomInt(1, 100) <= effectValues[i]) {
                            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
                                @Override
                                public void run() {
                                    ((Player) target).removePotionEffect(PotionEffectType.SLOW);
                                }
                            }, 1L);
                        }
                    }
                } else if ((bitMask & 0x8) > 0) { // % chance to avoid weakness
                   if (((Player) target).hasPotionEffect(PotionEffectType.WEAKNESS)) {
                        if (this.plugin.generateRandomInt(1, 100) <= effectValues[i]) {
                            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
                                @Override
                                public void run() {
                                    ((Player) target).removePotionEffect(PotionEffectType.WEAKNESS);
                                }
                            }, 1L);
                        }
                    }
                }

                i++;
                // % chance to block damage
                if ((bitMask & 0x4) > 0) {
                    if (this.plugin.generateRandomInt(1, 100) <= effectValues[i]) {
                        event.setDamage(0D);
                    }
                    i++;
                }

                // % chance to reflect damage
                if ((bitMask & 0x2) > 0) {
                    if (this.plugin.generateRandomInt(1, 100) <= effectValues[i]) {
                        Entity damager = event.getDamager();
                        if (damager != null && damager instanceof LivingEntity && !damager.isDead()) { // Hit by entity
                            ((LivingEntity) damager).damage(event.getDamage(), target);
                        } else if (damager instanceof Arrow && ((Arrow) damager).getShooter() instanceof LivingEntity && !((LivingEntity)((Arrow) damager).getShooter()).isDead()) { // Hit by arrow
							((LivingEntity)((Arrow) damager).getShooter()).damage(event.getDamage(), target);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void godArmorPlayerFall(EntityDamageEvent event) {
        Entity target = event.getEntity();

        if (target instanceof Player) {
            for (ItemStack item : ((Player) target).getInventory().getArmorContents()) {
                ItemMeta meta = item.getItemMeta();

                if (meta == null) {
                    continue;
                }

                if (meta.getLore() == null) {
                    continue;
                }

                String[] pieces = meta.getLore().get(meta.getLore().size() - 1).split(" ");
                int bitMask = 0;

                try {
                    bitMask = Integer.parseInt(pieces[0]);
                } catch (NumberFormatException e) {
                    continue; // Not a God item
                }

                // Split numbers off of end and convert
                int[] effectValues = null;
                if (pieces.length == 2) {
                    String[] tmp = pieces[1].split(":");
                    effectValues = new int[tmp.length];
                    for (int i = 0; i < tmp.length; i++) {
                        try {
                            effectValues[i] = Integer.parseInt(tmp[i]);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            continue;
                        }
                    }
                }

                // Ok this is a god loot item, lets figure out what it is
                // First bit: avoid fall damage if distance under n
                // Second bit: % chance to reflect damage
                // Third bit: % chance to block damage
                // Fourth bit: % chance to avoid weakness
                // Fifth bit: % chance to avoid slowness
                // Sixth bit: % chance to avoid poison
                // Seventh bit: % chance to avoid nausea
                // 0100101 <-- no poison, % chance to block damage, avoid fall damage if distance under n

				// Just incase NPE
				if (effectValues == null) {
					return;
				}

                // No fall damage if fall distance < intensity
				if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
					if ((bitMask & 0x1) > 0) {
						if (target.getFallDistance() <= effectValues[effectValues.length - 1]) {
							event.setDamage(0D);
						}
					}
				}
            }
        }
    }

}
