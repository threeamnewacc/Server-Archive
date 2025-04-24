package me.devkevin.practice.hcf.manager;

import lombok.Getter;
import me.devkevin.practice.hcf.HCFClass;
import me.devkevin.practice.hcf.event.ArmorClassEquipEvent;
import me.devkevin.practice.hcf.event.ArmorClassUnequipEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 25/02/2023 @ 0:08
 * HCFManager / me.devkevin.practice.hcf.manager / Practice
 */
@Getter
public class HCFManager {
    private final List<HCFClass> classes = new ArrayList<>();
    private final Map<UUID, HCFClass> equippedClassMap = new HashMap<>();

    public HCFClass getHCFClass(Player player) {
        return equippedClassMap.getOrDefault(player.getUniqueId(), null);
    }

    public void setEquippedClass(Player player, HCFClass hcfClass) {
        if (hcfClass == null) {
            HCFClass equipped = equippedClassMap.remove(player.getUniqueId());

            if (equipped != null) {
                equipped.onUnEquip(player);
                Bukkit.getPluginManager().callEvent(new ArmorClassUnequipEvent(player, equipped));
            }
        } else if (hcfClass.onEquip(player) && hcfClass != getHCFClass(player)) {
            equippedClassMap.put(player.getUniqueId(), hcfClass);
            Bukkit.getPluginManager().callEvent(new ArmorClassEquipEvent(player, hcfClass));
        }
    }
}
