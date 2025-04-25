package us.zonix.hcfactions.profile.kit.ability;

import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.factions.Faction;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.kit.ProfileKitCooldown;
import us.zonix.hcfactions.profile.kit.ProfileKitEnergy;
import us.zonix.hcfactions.profile.kit.ProfileKit;
import us.zonix.hcfactions.factions.claims.Claim;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.factions.type.SystemFaction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.hcfactions.util.player.PlayerUtility;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.factions.claims.Claim;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.factions.type.SystemFaction;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.kit.ProfileKit;
import us.zonix.hcfactions.profile.kit.ProfileKitCooldown;
import us.zonix.hcfactions.util.player.PlayerUtility;

import java.util.HashMap;

public class ProfileKitAbilityListeners implements Listener {

    private static FactionsPlugin main = FactionsPlugin.getInstance();

    private static final int ROGUE_FEATHER_JUMP_DURATION = main.getMainConfig().getInt("KIT.ABILITY.ROGUE_FEATHER_JUMP.DURATION");
    private static final int ROGUE_FEATHER_JUMP_COOLDOWN_DURATION = main.getMainConfig().getInt("KIT.ABILITY.ROGUE_FEATHER_JUMP.COOLDOWN_DURATION");

    private static final int ROGUE_SUGAR_SPEED_DURATION = main.getMainConfig().getInt("KIT.ABILITY.ROGUE_SUGAR_SPEED.DURATION");
    private static final int ROGUE_SUGAR_SPEED_COOLDOWN_DURATION = main.getMainConfig().getInt("KIT.ABILITY.ROGUE_SUGAR_SPEED.COOLDOWN_DURATION");

    private static final int SINGLE_SUGAR_SPEED_DURATION = main.getMainConfig().getInt("KIT.ABILITY.SINGLE_SUGAR_SPEED.DURATION");
    private static final int SINGLE_SUGAR_SPEED_COOLDOWN_DURATION = main.getMainConfig().getInt("KIT.ABILITY.SINGLE_SUGAR_SPEED.COOLDOWN_DURATION");

    private static HashMap<ProfileKitAbility, Material> ITEM = new HashMap<>();
    private static HashMap<ProfileKitAbility, Integer> ENERGY_REQUIRED = new HashMap<>();
    private static HashMap<ProfileKitAbility, Integer> DURATION = new HashMap<>();
    private static HashMap<ProfileKitAbility, Integer> LEVEL = new HashMap<>();
    private static HashMap<ProfileKitAbility, Integer> RANGE = new HashMap<>();
    private static HashMap<ProfileKitAbility, PotionEffectType> POTION = new HashMap<>();

    public ProfileKitAbilityListeners() {

        for (String key : main.getMainConfig().getConfiguration().getConfigurationSection("KIT.ABILITY").getKeys(false)) {
            if (key.contains("MULTI")) {
                ProfileKitAbility ability = ProfileKitAbility.valueOf(key);
                System.out.println("ADDED " + ability.name());
                ENERGY_REQUIRED.put(ability, main.getMainConfig().getInt("KIT.ABILITY." + key + ".ENERGY_REQUIRED"));
                DURATION.put(ability, main.getMainConfig().getInt("KIT.ABILITY." + key + ".DURATION"));
                LEVEL.put(ability, main.getMainConfig().getInt("KIT.ABILITY." + key + ".LEVEL"));
                RANGE.put(ability, main.getMainConfig().getInt("KIT.ABILITY." + key + ".RANGE"));
                POTION.put(ability, ability.getPotion());
                ITEM.put(ability, Material.valueOf(main.getMainConfig().getString("KIT.ABILITY." + key + ".ITEM")));
            }
        }

        checkBards();
    }

    private void checkBards() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : PlayerUtility.getOnlinePlayers() ) {
                    Profile profile = Profile.getByPlayer(player);
                    ProfileKit kit = profile.getKit();

                    Claim claim = Claim.getProminentClaimAt(player.getLocation());
                    if (claim != null && claim.getFaction() instanceof SystemFaction && !((SystemFaction) claim.getFaction()).isDeathban()) {
                        continue;
                    }

                    if (profile.getProtection() != null) {
                        continue;
                    }

                    if (kit != null) {
                        for (ProfileKitAbility ability : ITEM.keySet()) {
                            if (ability == ProfileKitAbility.MULTI_SPIDER_WITHER) continue;
                            if (kit.hasAbility(ability) && player.getItemInHand() != null && player.getItemInHand().getType() == ITEM.get(ability)) {

                                int level = LEVEL.get(ability);
                                if (ability == ProfileKitAbility.MULTI_FEATHER_JUMP) {
                                    level = level / 2;
                                } else {
                                    level = level - 2;
                                }

                                PotionEffect toAdd = new PotionEffect(POTION.get(ability), 6 * 20, level);
                                replaceEffect(player, toAdd);

                                PlayerFaction faction = PlayerFaction.getByPlayer(player);
                                if (faction != null) {
                                    for (Player member : faction.getOnlinePlayers()) {
                                        if (member.getName().equals(player.getName())) continue;
                                        Profile memberProfile = Profile.getByPlayer(member);
                                        if (member.getLocation().getWorld().equals(player.getLocation().getWorld())) {
                                            if (member.getLocation().distance(player.getLocation()) <= RANGE.get(ability)) {

                                                claim = Claim.getProminentClaimAt(member.getLocation());

                                                if (claim != null && claim.getFaction() instanceof SystemFaction && !((SystemFaction) claim.getFaction()).isDeathban()) {
                                                    continue;
                                                }

                                                if (memberProfile.getProtection() != null) {
                                                    continue;
                                                }

                                                replaceEffect(member, toAdd);
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }

                }
            }
        }.runTaskTimerAsynchronously(main, 4L, 4L);
    }

    private void replaceEffect(Player player, PotionEffect toAdd) {
        boolean replace = true;

        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            if (potionEffect.getType().equals(toAdd.getType())) {
                if ((potionEffect.getDuration() > 60) || (potionEffect.getAmplifier() > (toAdd.getAmplifier()) && potionEffect.getDuration() > 10)) {
                    replace = false;
                    break;
                }
            }
            replace = true;
        }

        if (replace) {

            Bukkit.getServer().getScheduler().runTask(FactionsPlugin.getInstance(), () -> player.addPotionEffect(toAdd, true));
        }
    }


    @EventHandler
    public void onBardPlayerInteractEvent(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);
        ProfileKit kit = profile.getKit();

        if (event.getItem() != null && event.getItem().getType() == Material.SUGAR) {

            if (kit != null && kit.hasAbility(ProfileKitAbility.SINGLE_SUGAR_SPEED)) {
                ProfileKitCooldown cooldown = profile.getKitCooldownByType(ProfileKitAbility.SINGLE_SUGAR_SPEED);
                if (cooldown != null) {
                    player.sendMessage(main.getLanguageConfig().getString("KIT.COOLDOWN").replace("%TIME%", cooldown.getTimeLeft()));
                } else {

                    if (player.getItemInHand().getAmount() > 1) {
                        player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                    } else {
                        player.setItemInHand(new ItemStack(Material.AIR));
                    }

                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, SINGLE_SUGAR_SPEED_DURATION * 20, main.getMainConfig().getInt("KIT.ABILITY.SINGLE_SUGAR_SPEED.LEVEL") - 1), true);
                    profile.getKitCooldowns().add(new ProfileKitCooldown(ProfileKitAbility.SINGLE_SUGAR_SPEED, SINGLE_SUGAR_SPEED_COOLDOWN_DURATION));
                }
            }

            else if (kit != null && kit.hasAbility(ProfileKitAbility.ROGUE_SUGAR_SPEED)) {
                ProfileKitCooldown cooldown = profile.getKitCooldownByType(ProfileKitAbility.ROGUE_SUGAR_SPEED);
                if (cooldown != null) {
                    player.sendMessage(main.getLanguageConfig().getString("KIT.COOLDOWN").replace("%TIME%", cooldown.getTimeLeft()));
                } else {

                    if (player.getItemInHand().getAmount() > 1) {
                        player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                    } else {
                        player.setItemInHand(new ItemStack(Material.AIR));
                    }

                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, ROGUE_SUGAR_SPEED_DURATION * 20, main.getMainConfig().getInt("KIT.ABILITY.ROGUE_SUGAR_SPEED.LEVEL") - 1), true);
                    profile.getKitCooldowns().add(new ProfileKitCooldown(ProfileKitAbility.ROGUE_SUGAR_SPEED, ROGUE_SUGAR_SPEED_COOLDOWN_DURATION));
                }
            }
        }

        else if (event.getItem() != null && event.getItem().getType() == Material.FEATHER) {

            if (kit != null && kit.hasAbility(ProfileKitAbility.ROGUE_FEATHER_JUMP)) {
                ProfileKitCooldown cooldown = profile.getKitCooldownByType(ProfileKitAbility.ROGUE_FEATHER_JUMP);
                if (cooldown != null) {
                    player.sendMessage(main.getLanguageConfig().getString("KIT.COOLDOWN").replace("%TIME%", cooldown.getTimeLeft()));
                } else {

                    if (player.getItemInHand().getAmount() > 1) {
                        player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                    } else {
                        player.setItemInHand(new ItemStack(Material.AIR));
                    }

                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, ROGUE_FEATHER_JUMP_DURATION * 20, main.getMainConfig().getInt("KIT.ABILITY.ROGUE_FEATHER_JUMP.LEVEL") - 1), true);
                    profile.getKitCooldowns().add(new ProfileKitCooldown(ProfileKitAbility.ROGUE_FEATHER_JUMP, ROGUE_FEATHER_JUMP_COOLDOWN_DURATION));
                }
            }
        }
    }

    @EventHandler
    public void onMultiSugarSpeedInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);
        ProfileKit kit = profile.getKit();
        ProfileKitEnergy energy = profile.getEnergy();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() != null) {

                Claim claim = Claim.getProminentClaimAt(player.getLocation());
                if (claim != null && claim.getFaction() instanceof SystemFaction && !((SystemFaction) claim.getFaction()).isDeathban()) {
                    return;
                }

                for (ProfileKitAbility ability : ITEM.keySet()) {
                    if (ability == ProfileKitAbility.MULTI_CREAM_FIRERES) continue;
                    if (kit != null && kit.hasAbility(ability) && event.getItem().getType() == ITEM.get(ability)) {
                        if (energy != null) {

                            if (profile.getProtection() != null) {
                                player.sendMessage(main.getLanguageConfig().getString("PVP_PROTECTION.HAVE_SELF"));
                                continue;
                            }

                            ProfileKitCooldown cooldown = profile.getKitCooldownByType(ability);
                            if (cooldown != null) {
                                player.sendMessage(main.getLanguageConfig().getString("KIT.COOLDOWN").replace("%TIME%", cooldown.getTimeLeft()));
                                continue;
                            }

                            if (energy.getEnergy() < ENERGY_REQUIRED.get(ability)) {
                                player.sendMessage(main.getLanguageConfig().getString("KIT.NEED_MORE_ENERGY").replace("%MORE%", energy.getFormattedDifference(ENERGY_REQUIRED.get(ability))));
                                return;
                            }

                            if (player.getItemInHand().getAmount() > 1) {
                                player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                            } else {
                                player.setItemInHand(new ItemStack(Material.AIR));
                            }

                            energy.setEnergy(energy.getEnergy() - ENERGY_REQUIRED.get(ability));

                            if (ability != ProfileKitAbility.MULTI_BLAZE_STRENGTH) {

                                for (PotionEffect effect : player.getActivePotionEffects()) {
                                    if (effect.getType().equals(POTION.get(ability))) {
                                        profile.getCachedEffects().add(effect);
                                    }
                                }

                                player.addPotionEffect(new PotionEffect(POTION.get(ability), DURATION.get(ability) * 20, LEVEL.get(ability) - 1), true);
                            }

                            profile.getKitCooldowns().add(new ProfileKitCooldown(ability, 12));

                            PlayerFaction faction = PlayerFaction.getByPlayer(player);
                            if (ability == ProfileKitAbility.MULTI_SPIDER_WITHER) {
                                for (Entity nearby : player.getNearbyEntities(RANGE.get(ability), RANGE.get(ability), RANGE.get(ability))) {
                                    if (nearby instanceof Player) {
                                        Player nearbyPlayer = (Player) nearby;
                                        Profile nearbyProfile = Profile.getByPlayer(nearbyPlayer);

                                        claim = Claim.getProminentClaimAt(nearbyPlayer.getLocation());
                                        if (claim != null && claim.getFaction() instanceof SystemFaction && !((SystemFaction) claim.getFaction()).isDeathban()) {
                                            continue;
                                        }

                                        if (nearbyProfile.getProtection() != null) {
                                            continue;
                                        }

                                        if (faction != null && faction.getOnlinePlayers().contains(nearbyPlayer)) {
                                            continue;
                                        }

                                        for (PotionEffect effect : nearbyPlayer.getActivePotionEffects()) {
                                            if (effect.getType().equals(POTION.get(ability))) {
                                                nearbyProfile.getCachedEffects().add(effect);
                                            }
                                        }

                                        nearbyPlayer.addPotionEffect(new PotionEffect(POTION.get(ability), DURATION.get(ability) * 20, LEVEL.get(ability) - 1), true);
                                    }
                                }
                            } else {
                                if (faction != null) {
                                    for (Player member : faction.getOnlinePlayers()) {
                                        if (member.getName().equals(player.getName())) continue;
                                        Profile memberProfile = Profile.getByPlayer(member);
                                        if (member.getLocation().getWorld().equals(player.getLocation().getWorld())) {
                                            if (member.getLocation().distance(player.getLocation()) <= RANGE.get(ability)) {

                                                claim = Claim.getProminentClaimAt(member.getLocation());
                                                if (claim != null && claim.getFaction() instanceof SystemFaction && !((SystemFaction) claim.getFaction()).isDeathban()) {
                                                    continue;
                                                }

                                                if (memberProfile.getProtection() != null) {
                                                    continue;
                                                }

                                                for (PotionEffect effect : member.getActivePotionEffects()) {
                                                    if (effect.getType().equals(POTION.get(ability))) {
                                                        memberProfile.getCachedEffects().add(effect);
                                                    }
                                                }

                                                member.addPotionEffect(new PotionEffect(POTION.get(ability), DURATION.get(ability) * 20, LEVEL.get(ability) - 1), true);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
