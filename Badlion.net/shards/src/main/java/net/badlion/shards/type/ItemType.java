package net.badlion.shards.type;

import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemType {

    private final String type;
    private final short durability;
    private final int amount;
    private final List<EnchantType> enchants;
    private final ItemMetaType meta;

    public ItemType(String type, short durability, int amount, List<EnchantType> enchants, ItemMetaType meta) {
        this.type = type;
        this.durability = durability;
        this.amount = amount;
        this.enchants = enchants;
        this.meta = meta;
    }

    public int getAmount() {
        return amount;
    }

    public ItemMetaType getMeta() {
        return meta;
    }

    public List<EnchantType> getEnchants() {
        return enchants;
    }

    public short getDurability() {
        return durability;
    }

    public String getType() {
        return type;
    }


}
