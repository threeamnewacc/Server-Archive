package net.badlion.shards.type;

import java.util.List;

public class ItemMetaType {
    private String displayName;
    private List<String> lore;
    private List<EnchantType> storedEnchants;
    private Integer repairCost;
    private Integer leatherArmorColor;
    private List<PotionEffectType> potionEffects;

    public Integer getLeatherArmorColor() {
        return leatherArmorColor;
    }

    public Integer getRepairCost() {
        return repairCost;
    }

    public List<EnchantType> getStoredEnchants() {
        return storedEnchants;
    }

    public List<PotionEffectType> getPotionEffects() {
        return potionEffects;
    }

    public List<String> getLore() {
        return lore;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setLeatherArmorColor(Integer leatherArmorColor) {
        this.leatherArmorColor = leatherArmorColor;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public void setPotionEffects(List<PotionEffectType> potionEffects) {
        this.potionEffects = potionEffects;
    }

    public void setRepairCost(Integer repairCost) {
        this.repairCost = repairCost;
    }

    public void setStoredEnchants(List<EnchantType> storedEnchants) {
        this.storedEnchants = storedEnchants;
    }
}
