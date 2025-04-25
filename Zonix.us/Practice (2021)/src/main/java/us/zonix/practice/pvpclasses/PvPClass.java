package us.zonix.practice.pvpclasses;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.beans.ConstructorProperties;
import java.util.List;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.zonix.practice.Practice;
import us.zonix.practice.match.Match;
import us.zonix.practice.party.Party;
import us.zonix.practice.player.PlayerState;
import us.zonix.practice.pvpclasses.pvpclasses.ArcherClass;
import us.zonix.practice.pvpclasses.pvpclasses.BardClass;

public abstract class PvPClass implements Listener {
    String name;
    int warmup;
    String armorContains;
    List<Material> consumables;
    private static final Table<UUID, PotionEffectType, PotionEffect> restores = HashBasedTable.create();

    public String getName() {
        return this.name;
    }

    public int getWarmup() {
        return this.warmup;
    }

    public String getArmorContains() {
        return this.armorContains;
    }

    public List<Material> getConsumables() {
        return this.consumables;
    }

    public PvPClass(String name, int warmup, String armorContains, List<Material> consumables) {
        this.name = name;
        this.warmup = warmup;
        this.armorContains = armorContains;
        this.consumables = consumables;
    }

    public void apply(Player player) {
    }

    public void tick(Player player) {
    }

    public void remove(Player player) {
    }

    public boolean canApply(Player player) {
        if (Practice.getInstance().getPlayerManager().getPlayerData(player.getUniqueId()).getPlayerState() != PlayerState.FIGHTING) {
            return false;
        } else {
            Party party = Practice.getInstance().getPartyManager().getParty(player.getUniqueId());
            Match match = Practice.getInstance().getMatchManager().getMatch(player.getUniqueId());
            if (match == null) {
                return false;
            } else if (!match.getKit().isHcteams()) {
                return false;
            } else {
                if (party != null) {
                    if (this instanceof ArcherClass && party.getArchers().contains(player.getUniqueId())) {
                        return true;
                    }

                    if (this instanceof BardClass && party.getBards().contains(player.getUniqueId())) {
                        return true;
                    }
                }

                return false;
            }
        }
    }

    public static void removeInfiniteEffects(Player player) {
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            if (potionEffect.getDuration() > 1000000) {
                player.removePotionEffect(potionEffect.getType());
            }
        }
    }

    public boolean itemConsumed(Player player, Material type) {
        return true;
    }

    public boolean qualifies(PlayerInventory armor) {
        return armor.getHelmet() != null
            && armor.getChestplate() != null
            && armor.getLeggings() != null
            && armor.getBoots() != null
            && armor.getHelmet().getType().name().startsWith(this.armorContains)
            && armor.getChestplate().getType().name().startsWith(this.armorContains)
            && armor.getLeggings().getType().name().startsWith(this.armorContains)
            && armor.getBoots().getType().name().startsWith(this.armorContains);
    }

    public static void smartAddPotion(Player player, PotionEffect potionEffect, boolean persistOldValues, PvPClass pvpClass) {
        setRestoreEffect(player, potionEffect);
    }

    public static void setRestoreEffect(Player player, PotionEffect effect) {
        boolean shouldCancel = true;

        for (PotionEffect active : player.getActivePotionEffects()) {
            if (active.getType().equals(effect.getType())) {
                if (effect.getAmplifier() < active.getAmplifier()) {
                    return;
                }

                if (effect.getAmplifier() == active.getAmplifier()
                    && 0 < active.getDuration()
                    && (effect.getDuration() <= active.getDuration() || effect.getDuration() - active.getDuration() < 10)) {
                    return;
                }

                restores.put(player.getUniqueId(), active.getType(), active);
                shouldCancel = false;
            }
        }

        player.addPotionEffect(effect, true);
        if (shouldCancel && effect.getDuration() > 120 && effect.getDuration() < 9600) {
            restores.remove(player.getUniqueId(), effect.getType());
        }
    }

    @EventHandler(
        ignoreCancelled = true,
        priority = EventPriority.MONITOR
    )
    public void onPotionEffectExpire(PotionEffectExpireEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof Player) {
            Player player = (Player)livingEntity;
            PotionEffect previous = (PotionEffect)restores.remove(player.getUniqueId(), event.getEffect().getType());
            if (previous != null && previous.getDuration() < 1000000) {
                event.setCancelled(true);
                player.addPotionEffect(previous, true);
            }
        }
    }

    public static class SavedPotion {
        PotionEffect potionEffect;
        long time;
        private boolean perm;

        @ConstructorProperties({"potionEffect", "time", "perm"})
        public SavedPotion(PotionEffect potionEffect, long time, boolean perm) {
            this.potionEffect = potionEffect;
            this.time = time;
            this.perm = perm;
        }

        public PotionEffect getPotionEffect() {
            return this.potionEffect;
        }

        public long getTime() {
            return this.time;
        }

        public boolean isPerm() {
            return this.perm;
        }
    }
}
