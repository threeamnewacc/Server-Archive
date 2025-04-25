package us.zonix.practice.pvpclasses.pvpclasses;

import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.practice.Practice;
import us.zonix.practice.match.Match;
import us.zonix.practice.party.Party;
import us.zonix.practice.pvpclasses.PvPClass;
import us.zonix.practice.pvpclasses.PvPClassHandler;
import us.zonix.practice.pvpclasses.pvpclasses.bard.BardEffect;

public class BardClass extends PvPClass implements Listener {
    public static final Set<PotionEffectType> DEBUFFS = ImmutableSet.of(
        PotionEffectType.POISON, PotionEffectType.SLOW, PotionEffectType.WEAKNESS, PotionEffectType.HARM, PotionEffectType.WITHER
    );
    public final Map<Material, BardEffect> BARD_CLICK_EFFECTS = new HashMap<>();
    public final Map<Material, BardEffect> BARD_PASSIVE_EFFECTS = new HashMap<>();
    private static Map<String, Long> lastEffectUsage = new ConcurrentHashMap<>();
    private static Map<String, Float> energy = new ConcurrentHashMap<>();
    public static final int BARD_RANGE = 20;
    public static final int EFFECT_COOLDOWN = 10000;
    public static final float MAX_ENERGY = 100.0F;
    public static final float ENERGY_REGEN_PER_SECOND = 1.0F;

    public static Map<String, Long> getLastEffectUsage() {
        return lastEffectUsage;
    }

    public static Map<String, Float> getEnergy() {
        return energy;
    }

    public BardClass() {
        super("Bard", 15, "GOLD_", null);
        this.BARD_CLICK_EFFECTS.put(Material.BLAZE_POWDER, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 1), 45));
        this.BARD_CLICK_EFFECTS.put(Material.SUGAR, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.SPEED, 120, 2), 20));
        this.BARD_CLICK_EFFECTS.put(Material.FEATHER, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.JUMP, 100, 6), 25));
        this.BARD_CLICK_EFFECTS.put(Material.IRON_INGOT, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 2), 40));
        this.BARD_CLICK_EFFECTS.put(Material.GHAST_TEAR, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.REGENERATION, 100, 2), 40));
        this.BARD_CLICK_EFFECTS.put(Material.MAGMA_CREAM, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 900, 0), 40));
        this.BARD_CLICK_EFFECTS.put(Material.WHEAT, BardEffect.fromEnergy(25));
        this.BARD_CLICK_EFFECTS.put(Material.SPIDER_EYE, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.WITHER, 100, 1), 35));
        this.BARD_PASSIVE_EFFECTS.put(Material.BLAZE_POWDER, BardEffect.fromPotion(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 0)));
        this.BARD_PASSIVE_EFFECTS.put(Material.SUGAR, BardEffect.fromPotion(new PotionEffect(PotionEffectType.SPEED, 120, 1)));
        this.BARD_PASSIVE_EFFECTS.put(Material.FEATHER, BardEffect.fromPotion(new PotionEffect(PotionEffectType.JUMP, 120, 1)));
        this.BARD_PASSIVE_EFFECTS.put(Material.IRON_INGOT, BardEffect.fromPotion(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 120, 0)));
        this.BARD_PASSIVE_EFFECTS.put(Material.GHAST_TEAR, BardEffect.fromPotion(new PotionEffect(PotionEffectType.REGENERATION, 120, 0)));
        this.BARD_PASSIVE_EFFECTS.put(Material.MAGMA_CREAM, BardEffect.fromPotion(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 120, 0)));
        (new BukkitRunnable() {
            public void run() {
                for (Player player : Practice.getInstance().getServer().getOnlinePlayers()) {
                    if (PvPClassHandler.hasKitOn(player, BardClass.this)) {
                        if (BardClass.energy.containsKey(player.getName())) {
                            if (BardClass.energy.get(player.getName()) != 100.0F) {
                                BardClass.energy.put(player.getName(), Math.min(100.0F, BardClass.energy.get(player.getName()) + 1.0F));
                            }
                        } else {
                            BardClass.energy.put(player.getName(), 0.0F);
                        }
                    }
                }
            }
        }).runTaskTimer(Practice.getInstance(), 15L, 20L);
    }

    @Override
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0), true);
    }

    @Override
    public void tick(Player player) {
        if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        }

        if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        }

        if (!player.hasPotionEffect(PotionEffectType.REGENERATION)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
        }

        if (player.getItemInHand() != null && this.BARD_PASSIVE_EFFECTS.containsKey(player.getItemInHand().getType())) {
            if (player.getItemInHand().getType() == Material.FERMENTED_SPIDER_EYE
                && getLastEffectUsage().containsKey(player.getName())
                && getLastEffectUsage().get(player.getName()) > System.currentTimeMillis()) {
                return;
            }

            this.giveBardEffect(player, this.BARD_PASSIVE_EFFECTS.get(player.getItemInHand().getType()), true, false);
        }

        super.tick(player);
    }

    @Override
    public void remove(Player player) {
        energy.remove(player.getName());

        for (BardEffect bardEffect : this.BARD_CLICK_EFFECTS.values()) {
            bardEffect.getLastMessageSent().remove(player.getName());
        }

        for (BardEffect bardEffect : this.BARD_CLICK_EFFECTS.values()) {
            bardEffect.getLastMessageSent().remove(player.getName());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().name().contains("RIGHT_")
            && event.hasItem()
            && this.BARD_CLICK_EFFECTS.containsKey(event.getItem().getType())
            && PvPClassHandler.hasKitOn(event.getPlayer(), this)
            && energy.containsKey(event.getPlayer().getName())) {
            if (getLastEffectUsage().containsKey(event.getPlayer().getName())
                && getLastEffectUsage().get(event.getPlayer().getName()) > System.currentTimeMillis()
                && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                long millisLeft = getLastEffectUsage().get(event.getPlayer().getName()) - System.currentTimeMillis();
                double value = (double)millisLeft / 1000.0;
                double sec = (double)Math.round(10.0 * value) / 10.0;
                event.getPlayer().sendMessage(ChatColor.RED + "You can't use this for another " + ChatColor.BOLD + sec + ChatColor.RED + " seconds!");
            } else {
                BardEffect bardEffect = this.BARD_CLICK_EFFECTS.get(event.getItem().getType());
                if ((float)bardEffect.getEnergy() > energy.get(event.getPlayer().getName())) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You do not have enough energy to do this! You need " + bardEffect.getEnergy() + " energy.");
                } else {
                    energy.put(event.getPlayer().getName(), energy.get(event.getPlayer().getName()) - (float)bardEffect.getEnergy());
                    boolean negative = bardEffect.getPotionEffect() != null && DEBUFFS.contains(bardEffect.getPotionEffect().getType());
                    getLastEffectUsage().put(event.getPlayer().getName(), System.currentTimeMillis() + 10000L);
                    this.giveBardEffect(event.getPlayer(), bardEffect, !negative, true);
                    if (event.getPlayer().getItemInHand().getAmount() == 1) {
                        event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                        event.getPlayer().updateInventory();
                    } else {
                        event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
                    }
                }
            }
        }
    }

    public void giveBardEffect(Player source, BardEffect bardEffect, boolean friendly, boolean persistOldValues) {
        for (Player player : this.getNearbyPlayers(source, friendly)) {
            if (!PvPClassHandler.hasKitOn(player, this)
                || bardEffect.getPotionEffect() == null
                || !bardEffect.getPotionEffect().getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                if (bardEffect.getPotionEffect() != null) {
                    smartAddPotion(player, bardEffect.getPotionEffect(), persistOldValues, this);
                } else {
                    Material material = source.getItemInHand().getType();
                    this.giveCustomBardEffect(player, material);
                }
            }
        }
    }

    public void giveCustomBardEffect(Player player, Material material) {
        switch (material) {
            case WHEAT:
                for (Player nearbyPlayer : this.getNearbyPlayers(player, true)) {
                    nearbyPlayer.setFoodLevel(20);
                    nearbyPlayer.setSaturation(10.0F);
                }
            case FERMENTED_SPIDER_EYE:
                return;
        }
    }

    public List<Player> getNearbyPlayers(Player player, boolean friendly) {
        List<Player> valid = new ArrayList<>();
        Party sourceTeam = Practice.getInstance().getPartyManager().getParty(player.getUniqueId());
        Match match = Practice.getInstance().getMatchManager().getMatch(player.getUniqueId());
        if (match == null) {
            return valid;
        } else {
            for (Entity entity : player.getNearbyEntities(20.0, 10.0, 20.0)) {
                if (entity instanceof Player) {
                    Player nearbyPlayer = (Player)entity;
                    if (sourceTeam == null) {
                        if (!friendly) {
                            valid.add(nearbyPlayer);
                        }
                    } else {
                        boolean isFriendly = sourceTeam.getMembers().contains(nearbyPlayer.getUniqueId());
                        boolean isOtherTeam = match.getOtherTeam(player).get(0).getPlayers().contains(nearbyPlayer.getUniqueId());
                        if (friendly && isFriendly) {
                            valid.add(nearbyPlayer);
                        } else if (!friendly && !isFriendly && isOtherTeam) {
                            valid.add(nearbyPlayer);
                        }
                    }
                }
            }

            valid.add(player);
            return valid;
        }
    }
}
