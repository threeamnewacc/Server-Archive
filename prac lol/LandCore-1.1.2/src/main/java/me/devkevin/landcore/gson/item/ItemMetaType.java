package me.devkevin.landcore.gson.item;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.potion.PotionEffect;

import java.util.List;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 18/01/2023 @ 15:17
 * ItemMetaType / land.pvp.core.gson.item / LandCore
 */
@Getter
@Setter
public class ItemMetaType {
    private String displayName;
    private List<String> lore;
    private List<EnchantType> storedEnchants;
    private Integer repairCost;
    private Integer leatherArmorColor;
    private List<PotionEffect> potionEffects;
}
