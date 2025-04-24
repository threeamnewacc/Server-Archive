package me.devkevin.practice.hcf.classes;

import club.inverted.chatcolor.CC;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import me.devkevin.practice.Practice;
import me.devkevin.practice.hcf.HCFClass;
import me.devkevin.practice.hcf.effects.BardData;
import me.devkevin.practice.hcf.effects.EffectData;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchTeam;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.state.ProfileState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 25/02/2023 @ 0:32
 * Bard / me.devkevin.practice.hcf.classes / Practice
 */
public class Bard extends HCFClass {

    private final Practice plugin = Practice.getInstance();

    public static TObjectLongMap<UUID> cooldowns = new TObjectLongHashMap<>();
    public static long buff_cooldown = TimeUnit.SECONDS.toMillis(10L);

    public static long held_reapply_ticks = 20L;
    public static final Map<UUID, BardData> bardDataMap = new HashMap<>();
    public static Map<Material, EffectData> bardEffects = new EnumMap<>(Material.class);

    public Bard() {
        super("Bard");

        this.getEffects().add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        this.getEffects().add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
        this.getEffects().add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));

        bardEffects.put(Material.FERMENTED_SPIDER_EYE, new EffectData(60, new PotionEffect(PotionEffectType.INVISIBILITY, 120, 1), new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0)));
        bardEffects.put(Material.WHEAT, new EffectData(35, new PotionEffect(PotionEffectType.SATURATION, 120, 1), new PotionEffect(PotionEffectType.SATURATION, 100, 0)));
        bardEffects.put(Material.SUGAR, new EffectData(25, new PotionEffect(PotionEffectType.SPEED, 120, 2), new PotionEffect(PotionEffectType.SPEED, 100, 1)));
        bardEffects.put(Material.BLAZE_POWDER, new EffectData(50, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 1), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0)));
        bardEffects.put(Material.IRON_INGOT, new EffectData(35, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 2), new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0)));
        bardEffects.put(Material.GHAST_TEAR, new EffectData(45, new PotionEffect(PotionEffectType.REGENERATION, 60, 2), new PotionEffect(PotionEffectType.REGENERATION, 100, 0)));
        bardEffects.put(Material.FEATHER, new EffectData(30, new PotionEffect(PotionEffectType.JUMP, 120, 5), new PotionEffect(PotionEffectType.JUMP, 100, 0)));
        bardEffects.put(Material.SPIDER_EYE, new EffectData(50, new PotionEffect(PotionEffectType.WITHER, 100, 1), null));
        bardEffects.put(Material.MAGMA_CREAM, new EffectData(10, new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 900, 0), new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 120, 0)));
    }

    public static double getEnergy(Player player) {
        BardData bardData = bardDataMap.get(player.getUniqueId());
        return (bardData == null) ? 0.0 : bardData.getEnergy();
    }

    @Override
    public boolean canApply(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet == null || helmet.getType() != Material.GOLD_HELMET) {
            return false;
        }

        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate == null || chestplate.getType() != Material.GOLD_CHESTPLATE) {
            return false;
        }

        ItemStack leggings = player.getInventory().getLeggings();
        if (leggings == null || leggings.getType() != Material.GOLD_LEGGINGS) {
            return false;
        }

        ItemStack boots = player.getInventory().getBoots();
        return !(boots == null || boots.getType() != Material.GOLD_BOOTS);
    }

    @Override
    public boolean onEquip(Player player) {
        for (PotionEffect effect : getEffects()) {
            player.addPotionEffect(effect, true);
        }

        BardData bardData = new BardData();
        bardDataMap.put(player.getUniqueId(), bardData);
        bardData.startEnergyTracking();

        bardData.heldTask = new BukkitRunnable() {
            int lastEnergy;

            public void run() {
                ItemStack held = player.getItemInHand();
                if (held != null) {
                    EffectData bardEffect = bardEffects.get(held.getType());
                    if (bardEffect == null) {
                        return;
                    }

                    getNearbyPlayers(player, true).forEach(bardPlayer -> {
                        Practice.getInstance().getEffectRestorer().setRestoreEffect(bardPlayer, bardEffect.heldable);
                    });
                }

                int energy = (int) getEnergy(player);
                if (energy != 0 && energy != lastEnergy && (energy % 10 == 0 || lastEnergy - energy - 1 > 0 || energy == BardData.MAX_ENERGY)) {
                    lastEnergy = energy;

                    player.sendMessage(CC.translate("&eBard Energy: &d" + energy));
                }
            }
        }.runTaskTimerAsynchronously(this.plugin, 0L, held_reapply_ticks);

        return true;
    }

    private void clearBardData(UUID uuid) {
        BardData bardData = bardDataMap.remove(uuid);
        if (bardData != null && bardData.getHeldTask() != null) {
            bardData.getHeldTask().cancel();
        }
    }

    public long getEnergyMillis(Player player) {
        synchronized (bardDataMap) {
            BardData bardData = bardDataMap.get(player.getUniqueId());
            return (bardData == null) ? 0L : bardData.getEnergyMillis();
        }
    }

    public double setEnergy(Player player, double energy) {
        BardData bardData = bardDataMap.get(player.getUniqueId());
        if (bardData == null) {
            return 0.0;
        }

        bardData.setEnergy(energy);
        return bardData.getEnergy();
    }

    public List<Player> getNearbyPlayers(Player player, boolean friend) {
        List<Player> players = new ArrayList<>();
        Profile practicePlayerData = this.plugin.getProfileManager().getProfileData(player.getUniqueId());
        if (practicePlayerData.getState() != ProfileState.FIGHTING) {
            return players;
        }
        if (!practicePlayerData.isInParty()) {
            return players;
        }

        Match match = this.plugin.getMatchManager().getMatch(practicePlayerData.getUuid());
        MatchTeam opposingTeam = match.isFFA() ? match.getTeams().get(0) : (practicePlayerData.getTeamID() == 0 ? match.getTeams().get(1) : match.getTeams().get(0));
        MatchTeam playerTeam = match.getTeams().get(practicePlayerData.getTeamID());
        if (friend) {
            playerTeam.getAlivePlayers().forEach(uuid -> {
                players.add(Bukkit.getPlayer(uuid));
            });
            players.add(player);
        } else {
            opposingTeam.getAlivePlayers().forEach(uuid -> {
                players.add(Bukkit.getPlayer(uuid));
            });
        }

        return players;
    }

    @Override
    public void onUnEquip(Player player) {
        for (PotionEffect effect : getEffects()) {
            player.removePotionEffect(effect.getType());
        }

        clearBardData(player.getUniqueId());
    }

    @Override
    public void onQuit(Player player) {
        clearBardData(player.getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        this.clearBardData(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        HCFClass equipped = this.plugin.getHcfManager().getHCFClass(player);
        if (equipped == null || !equipped.equals(this)) {
            return;
        }

        UUID uuid = player.getUniqueId();
        long lastMessage = cooldowns.get(uuid);
        long millis = System.currentTimeMillis();

        if (lastMessage != cooldowns.getNoEntryValue() && lastMessage - millis > 0L) {
            return;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem()) {
            return;
        }

        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || (!event.isCancelled() && action == Action.RIGHT_CLICK_BLOCK)) {
            Player player = event.getPlayer();
            ItemStack stack = event.getItem();
            EffectData bardEffect = bardEffects.get(stack.getType());
            if (bardEffect == null || bardEffect.clickable == null) {
                return;
            }

            event.setUseItemInHand(Event.Result.DENY);
            BardData bardData = bardDataMap.get(player.getUniqueId());
            if (bardData != null) {
                if (!this.canUseBardEffect(player, bardData, bardEffect, true)) {
                    return;
                }
                if (stack.getAmount() > 1) {
                    stack.setAmount(stack.getAmount() - 1);
                } else {
                    player.setItemInHand(new ItemStack(Material.AIR, 1));
                }

                Profile data = this.plugin.getProfileManager().getProfileData(player.getUniqueId());
                if (data != null && !bardEffect.clickable.getType().equals(PotionEffectType.SLOW_DIGGING)) {
                    getNearbyPlayers(player, true).stream().filter(player1 -> player1.getUniqueId() != player.getUniqueId()).forEach(bardPlayer -> {
                        this.plugin.getEffectRestorer().setRestoreEffect(bardPlayer, bardEffect.clickable);
                    });
                } else if (data != null && bardEffect.clickable.getType().equals(PotionEffectType.SLOW_DIGGING)) {
                    getNearbyPlayers(player, false).forEach(player1 -> {
                        this.plugin.getEffectRestorer().setRestoreEffect(player1, bardEffect.clickable);
                    });
                } else if (bardEffect.clickable.getType().equals(PotionEffectType.SLOW_DIGGING)) {
                    Collection<Entity> nearbyEntities = player.getNearbyEntities(10.0, 10.0, 10.0);
                    for (Entity nearby : nearbyEntities) {
                        if (nearby instanceof Player && !player.equals(nearby)) {
                            Player target = (Player) nearby;
                            this.plugin.getEffectRestorer().setRestoreEffect(target, bardEffect.clickable);
                        }
                    }
                }
                if (data != null && !bardEffect.clickable.getType().equals(PotionEffectType.SLOW)) {
                    getNearbyPlayers(player, true).stream().filter(player1 -> player1.getUniqueId() != player.getUniqueId()).forEach(bardPlayer -> {
                        this.plugin.getEffectRestorer().setRestoreEffect(bardPlayer, bardEffect.clickable);
                    });
                } else if (data != null && bardEffect.clickable.getType().equals(PotionEffectType.SLOW)) {
                    getNearbyPlayers(player, false).stream().filter(player1 -> player1.getUniqueId() != player.getUniqueId()).forEach(bardPlayer -> {
                        this.plugin.getEffectRestorer().setRestoreEffect(bardPlayer, bardEffect.clickable);
                    });
                } else if (bardEffect.clickable.getType().equals(PotionEffectType.SLOW)) {
                    Collection<Entity> nearbyEntities = player.getNearbyEntities(15.0, 15.0, 15.0);
                    for (Entity nearby : nearbyEntities) {
                        if (nearby instanceof Player && !player.equals(nearby)) {
                            Player target = (Player) nearby;
                            this.plugin.getEffectRestorer().setRestoreEffect(target, bardEffect.clickable);
                        }
                    }
                }
                if (data != null && !bardEffect.clickable.getType().equals(PotionEffectType.WITHER)) {
                    getNearbyPlayers(player, true).stream().filter(player1 -> player1.getUniqueId() != player.getUniqueId()).forEach(bardPlayer -> {
                        this.plugin.getEffectRestorer().setRestoreEffect(bardPlayer, bardEffect.clickable);
                    });
                } else if (data != null && bardEffect.clickable.getType().equals(PotionEffectType.WITHER)) {
                    getNearbyPlayers(player, false).stream().filter(player1 -> player1.getUniqueId() != player.getUniqueId()).forEach(bardPlayer -> {
                        this.plugin.getEffectRestorer().setRestoreEffect(bardPlayer, bardEffect.clickable);
                    });
                } else if (bardEffect.clickable.getType().equals(PotionEffectType.WITHER)) {
                    Collection<Entity> nearbyEntities = player.getNearbyEntities(25.0, 25.0, 25.0);
                    for (Entity nearby : nearbyEntities) {
                        if (nearby instanceof Player && !player.equals(nearby)) {
                            Player target = (Player) nearby;
                            this.plugin.getEffectRestorer().setRestoreEffect(target, bardEffect.clickable);
                        }
                    }
                }

                this.plugin.getEffectRestorer().setRestoreEffect(player, bardEffect.clickable);
                bardData.setBuffCooldown(buff_cooldown);
                this.setEnergy(player, bardData.getEnergy() - bardEffect.energyCost);

                player.sendMessage(CC.translate("&cYou have just used a &fBard Buff &cthat cost you " + bardEffect.energyCost + " &cof your Energy."));
            }
        }
    }

    private boolean canUseBardEffect(Player player, BardData bardData, EffectData bardEffect, boolean sendFeedback) {
        String errorFeedback = null;
        double currentEnergy = bardData.getEnergy();
        if (bardEffect.energyCost > currentEnergy) {
            errorFeedback = CC.translate("&cYou do not have enough energy for this! You need " + bardEffect.energyCost + " energy, but you only have " + currentEnergy);
        }

        long remaining = bardData.getRemainingBuffDelay() / 1000L;
        if (remaining > 0L) {
            errorFeedback = CC.RED + "You cannot use this for another " + CC.BOLD + remaining + CC.RED + " seconds.";
        }
        if (sendFeedback && errorFeedback != null) {
            player.sendMessage(errorFeedback);
        }

        return errorFeedback == null;
    }
}
