package us.zonix.hcfactions.profile.kit;

import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.profile.kit.ability.ProfileKitAbility;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.ore.ProfileOreType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.hcfactions.util.player.PlayerUtility;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.kit.ability.ProfileKitAbility;
import us.zonix.hcfactions.profile.ore.ProfileOreType;
import us.zonix.hcfactions.util.player.PlayerUtility;

import java.util.*;

public class ProfileKitListeners implements Listener {

    private static FactionsPlugin main = FactionsPlugin.getInstance();

    public ProfileKitListeners() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : PlayerUtility.getOnlinePlayers() ) {
                    Profile profile = Profile.getByPlayer(player);

                    if (!(profile.getCachedEffects()).isEmpty()) {
                        Iterator<PotionEffect> iterator = profile.getCachedEffects().iterator();
                        while (iterator.hasNext()) {
                            PotionEffect cachedEffect = iterator.next();
                            for (PotionEffect effect : new HashSet<>(player.getActivePotionEffects())) {
                                if (effect.getType().equals(cachedEffect.getType())) {
                                    if (effect.getDuration() <= 20) {

                                        Bukkit.getServer().getScheduler().runTask(FactionsPlugin.getInstance(), new Runnable() {
                                            @Override
                                            public void run() {
                                                player.addPotionEffect(cachedEffect, true);
                                            }
                                        });

                                        iterator.remove();
                                    }
                                }
                            }
                        }
                    }

                    if (profile.getKit() == null) {

                        ProfileKitWarmup kitWarmup = profile.getKitWarmup();
                        if (kitWarmup != null) {
                            if (!(kitWarmup.getKit().isWearingArmor(player))) {
                                profile.setKitWarmup(null);
                                player.sendMessage(main.getLanguageConfig().getString("KIT.CANCELLED").replace("%KIT%", kitWarmup.getKit().getName()));
                            } else {
                                if (kitWarmup.getTimeLeft().equalsIgnoreCase("0.1")) {
                                    profile.setKitWarmup(null);
                                    profile.setKit(kitWarmup.getKit());
                                    if (kitWarmup.getKit() == ProfileKit.BARD) {
                                        profile.setEnergy(new ProfileKitEnergy());
                                    }
                                    player.sendMessage(main.getLanguageConfig().getString("KIT.APPLIED").replace("%KIT%", kitWarmup.getKit().getName()));
                                }
                            }
                            continue;
                        }

                        for (ProfileKit kit : ProfileKit.values()) {
                            if (kit == ProfileKit.DIAMOND) continue;
                            if (kit.isWearingArmor(player)) {
                                if (main.getMainConfig().getBoolean("KIT.WARMUP.ENABLED")) {
                                    profile.setKitWarmup(new ProfileKitWarmup(kit, ProfileKitWarmup.DEFAULT_DURATION));
                                    player.sendMessage(main.getLanguageConfig().getString("KIT.WARMUP").replace("%KIT%", kit.getName()));
                                } else {
                                    profile.setKit(kit);
                                    if (kit == ProfileKit.BARD) {
                                        profile.setEnergy(new ProfileKitEnergy());
                                    }
                                    player.sendMessage(main.getLanguageConfig().getString("KIT.APPLIED").replace("%KIT%", kit.getName()));
                                }
                                break;
                            }
                        }

                        continue;
                    }

                    if (!(profile.getKit().isWearingArmor(player))) {
                        for (ProfileKitPotionEffect effect : profile.getKit().getPotionEffects()) {
                            player.removePotionEffect(effect.getType());
                        }

                        if (profile.getEnergy() != null) {
                            profile.setEnergy(null);
                        }

                        player.sendMessage(main.getLanguageConfig().getString("KIT.REMOVED").replace("%KIT%", profile.getKit().getName()));
                        profile.setKit(null);
                    } else {
                        List<ProfileKitPotionEffect> effects = new ArrayList<>(Arrays.asList(profile.getKit().getPotionEffects()));

                        if (profile.getKit() == ProfileKit.MINER) {
                            int diamondsMined = profile.getOres().get(ProfileOreType.DIAMOND);

                            if (diamondsMined >= 150) {
                                effects.add(new ProfileKitPotionEffect(PotionEffectType.FAST_DIGGING,  3));
                            }

                            if (diamondsMined >= 350) {
                                effects.add(new ProfileKitPotionEffect(PotionEffectType.FIRE_RESISTANCE, 1));
                            }

                            if (diamondsMined >= 500) {
                                effects.add(new ProfileKitPotionEffect(PotionEffectType.FAST_DIGGING, 4));
                            }

                            if (diamondsMined >= 750) {
                                effects.add(new ProfileKitPotionEffect(PotionEffectType.SPEED, 1));
                            }

                            if (diamondsMined >= 1000) {
                                effects.add(new ProfileKitPotionEffect(PotionEffectType.REGENERATION, 1));
                            }

                            if (diamondsMined >= 1250) {
                                effects.add(new ProfileKitPotionEffect(PotionEffectType.SPEED, 2));
                            }

                        }

                        for (ProfileKitPotionEffect effect : effects) {

                            if (effect.getType().equals(PotionEffectType.INVISIBILITY) && profile.getKit().hasAbility(ProfileKitAbility.MINING_INVISIBILITY)) {
                                if (player.getLocation().getBlockY() > 20) {
                                    continue;
                                }
                            }

                            boolean add = true;
                            int toCheck = effect.getType().equals(PotionEffectType.NIGHT_VISION) ? 1210 : 110;
                            for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                                if (potionEffect.getType().equals(effect.getType())) {
                                    if ((potionEffect.getDuration() > toCheck) || (potionEffect.getAmplifier() > (effect.getLevel() - 1) && potionEffect.getDuration() > 10)) {
                                        add = false;
                                        break;
                                    }
                                }
                                add = true;
                            }

                            if (add) {

                                Bukkit.getServer().getScheduler().runTask(FactionsPlugin.getInstance(), new Runnable() {
                                    @Override
                                    public void run() {
                                        player.addPotionEffect(new PotionEffect(effect.getType(), toCheck * 2, effect.getLevel() - 1, true), true);
                                    }
                                });
                            }
                        }
                    }

                }
            }
        }.runTaskTimerAsynchronously(main, 20L, 2L);
    }

}
