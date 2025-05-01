package net.badlion.gfactions.tasks;

import com.google.common.collect.ImmutableList;
import net.badlion.gfactions.GFactions;
import net.badlion.gguard.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class RoadSpeedTask extends BukkitRunnable {

    public void run() {
        final ImmutableList<Player> players = ImmutableList.copyOf(Bukkit.getOnlinePlayers());
        final int size = players.size();
        final int diff = (int) Math.ceil((double) players.size() / 20D);

        new BukkitRunnable() {
            private int start = 0;
            private int end = size;

            public void run() {
                if (start >= end) {
                    this.cancel();
                    return;
                }

                int localEnd = start + diff;
                for (int i = start; i < localEnd; i++, start++) {
                    // Bail out
                    if (i >= end) {
                        this.cancel();
                        return;
                    }

                    Player player = players.get(i);
                    ProtectedRegion region = GFactions.plugin.getgGuardPlugin().getProtectedRegion(player.getLocation(), GFactions.plugin.getgGuardPlugin().getProtectedRegions());
                    if (region != null && region.getRegionName().contains("road")) {
                        boolean found = false;
                        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                            if (potionEffect.getType().equals(PotionEffectType.SPEED)) {
                                if (potionEffect.getDuration() < 150) {
                                    player.removePotionEffect(PotionEffectType.SPEED);
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0));
                                    found = true;
                                    break;
                                }
                            }
                        }

                        if (!found) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0));
                        }
                    }

                    ItemStack[] armor = player.getInventory().getArmorContents();
                    if (armor[3] != null && armor[3].getType() == Material.IRON_HELMET
                                && armor[2] != null && armor[2].getType() == Material.IRON_CHESTPLATE
                                && armor[1] != null && armor[1].getType() == Material.IRON_LEGGINGS
                                && armor[0] != null && armor[0].getType() == Material.IRON_BOOTS) {
                        boolean found = false;
                        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                            if (potionEffect.getType().equals(PotionEffectType.FAST_DIGGING)) {
                                if (potionEffect.getDuration() < 150) {
                                    player.removePotionEffect(PotionEffectType.FAST_DIGGING);
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 200, 1));
                                    found = true;
                                    break;
                                }
                            }
                        }

                        if (!found) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 200, 0));
                        }
                    }
                }
            }
        }.runTaskTimer(GFactions.plugin, 0, 1);
    }

}
