package me.devkevin.practice.hcf.effects;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.devkevin.practice.Practice;
import me.devkevin.practice.hcf.HCFClass;
import me.devkevin.practice.hcf.classes.Bard;
import me.devkevin.practice.hcf.event.ArmorClassUnequipEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 25/02/2023 @ 0:27
 * EffectRestorer / me.devkevin.practice.hcf.effects / Practice
 */
public class EffectRestorer implements Listener {

    private final Table<UUID, PotionEffectType, PotionEffect> restorableEffectsTable;

    public EffectRestorer() {
        this.restorableEffectsTable = HashBasedTable.create();
        Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onArmorClassUnequip(ArmorClassUnequipEvent event) {
        this.restorableEffectsTable.rowKeySet().remove(event.getPlayer().getUniqueId());
    }

    public void setRestoreEffect(Player player, PotionEffect effect) {
        boolean shouldCancel = true;

        for (PotionEffect active : player.getActivePotionEffects()) {
            if (!active.getType().equals(effect.getType())) continue;
            if (effect.getAmplifier() < active.getAmplifier()) return;
            if (effect.getAmplifier() == active.getAmplifier() && effect.getDuration() < active.getDuration()) return;

            this.restorableEffectsTable.put(player.getUniqueId(), active.getType(), active);
            shouldCancel = false;

            break;
        }

        Bukkit.getScheduler().runTask(Practice.getInstance(), () -> player.addPotionEffect(effect, true));

        if (shouldCancel && effect.getDuration() > Bard.held_reapply_ticks && effect.getDuration() < HCFClass.DEFAULT_MAX_DURATION) {
            this.restorableEffectsTable.remove(player.getUniqueId(), effect.getType());
        }
    }
}
