package net.badlion.gberry.utils.gson;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.potion.PotionEffect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemStackTypeAdaptorFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(final Gson gson, TypeToken<T> type) {
        if (!ItemStack.class.isAssignableFrom(type.getRawType())) {
            return null;
        }

        return new TypeAdapter<T>() {

            @Override
            public void write(JsonWriter writer, T item) throws IOException {
                if (item == null) {
                    writer.nullValue();
                } else {
                    gson.toJson(serializeItem((ItemStack) item), ItemType.class, writer);
                }
            }

            @Override
            public T read(JsonReader reader) throws IOException {
                if (reader.peek() == JsonToken.NULL) {
                    reader.nextNull();
                    return null;
                } else {
                    return (T) deserializeItem((ItemType) gson.fromJson(reader, ItemType.class));
                }
            }
        };
    }

    public ItemType serializeItem(ItemStack itemStack) {
        Preconditions.checkNotNull(itemStack);

        ItemType item;
        ItemMeta itemStackMeta;
        ItemMetaType itemMeta = null;
        List<EnchantType> itemEnchants = null;

        if (itemStack.hasItemMeta()) {
            itemStackMeta = itemStack.getItemMeta();
            itemMeta = new ItemMetaType();

            if (itemStackMeta.hasDisplayName()) {
                itemMeta.setDisplayName(itemStackMeta.getDisplayName());
            }

            if (itemStackMeta.hasLore()) {
                itemMeta.setLore(itemStackMeta.getLore());
            }

            if (itemStackMeta instanceof EnchantmentStorageMeta) {
                EnchantmentStorageMeta esm = (EnchantmentStorageMeta) itemStackMeta;
                if (esm.hasStoredEnchants()) {
                    List<EnchantType> storedEnchants = new ArrayList<>();
                    for (Map.Entry<Enchantment, Integer> entry : esm.getStoredEnchants().entrySet()) {
                        storedEnchants.add(new EnchantType(entry.getKey().getName(), entry.getValue()));
                    }
                    itemMeta.setStoredEnchants(storedEnchants);
                }
            }

            if (itemStackMeta instanceof Repairable) {
                itemMeta.setRepairCost(((Repairable) itemStackMeta).getRepairCost());
            }

            if (itemStackMeta instanceof LeatherArmorMeta) {
                itemMeta.setLeatherArmorColor(((LeatherArmorMeta) itemStackMeta).getColor().asRGB());
            }

            if (itemStackMeta instanceof PotionMeta) {
                PotionMeta potionMeta = (PotionMeta) itemStackMeta;
                if (potionMeta.hasCustomEffects()) {
                    List<PotionEffectType> potionEffects = new ArrayList<>();
                    for (PotionEffect effect : potionMeta.getCustomEffects()) {
                        potionEffects.add(new PotionEffectType(effect.getType().getName(), effect.getDuration(), effect.getAmplifier()));
                    }
                    itemMeta.setPotionEffects(potionEffects);
                }
            }
        }

        for (Map.Entry<Enchantment, Integer> e : itemStack.getEnchantments().entrySet()) {
            if (itemEnchants == null) {
                itemEnchants = new ArrayList<>();
            }
            itemEnchants.add(new EnchantType(e.getKey().getName(), e.getValue()));
        }

        item = new ItemType(itemStack.getType().toString(), itemStack.getDurability(), itemStack.getAmount(),
                itemEnchants, itemMeta);

        return item;
    }

    public ItemStack deserializeItem(ItemType item) {
        Preconditions.checkNotNull(item);

        ItemStack itemStack = new ItemStack(Material.matchMaterial(item.getType()), item.getAmount());
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemStack.setDurability(item.getDurability());

        if (item.getEnchants() != null) {
            for (EnchantType enchant : item.getEnchants()) {
                itemMeta.addEnchant(Enchantment.getByName(enchant.getType()), enchant.getTier(), true);
            }
        }

        if (item.getMeta() != null) {
            if (item.getMeta().getDisplayName() != null) {
                itemMeta.setDisplayName(item.getMeta().getDisplayName());
            }

            if (item.getMeta().getLore() != null) {
                itemMeta.setLore(item.getMeta().getLore());
            }

            if (itemMeta instanceof EnchantmentStorageMeta && item.getMeta().getStoredEnchants() != null) {
                EnchantmentStorageMeta esm = (EnchantmentStorageMeta) itemMeta;
                for (EnchantType enchant : item.getMeta().getStoredEnchants()) {
                    esm.addStoredEnchant(Enchantment.getByName(enchant.getType()), enchant.getTier(), true);
                }
            }

            if (itemMeta instanceof Repairable && item.getMeta().getRepairCost() != null) {
                ((Repairable) itemMeta).setRepairCost(item.getMeta().getRepairCost());
            }

            if (itemMeta instanceof LeatherArmorMeta && item.getMeta().getLeatherArmorColor() != null) {
                ((LeatherArmorMeta) itemMeta).setColor(Color.fromRGB(item.getMeta().getLeatherArmorColor()));
            }

            if (itemMeta instanceof PotionMeta && item.getMeta().getPotionEffects() != null) {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                for (PotionEffectType effect : item.getMeta().getPotionEffects()) {
                    org.bukkit.potion.PotionEffectType type = org.bukkit.potion.PotionEffectType.getByName(effect.getType());
                    potionMeta.addCustomEffect(new PotionEffect(type, effect.getDuration(), effect.getAmplifier()), true);
                }
            }
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    // ItemStack Classes to cleanly convert to and from an itemstack

    class ItemType {

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

    class ItemMetaType {

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

    class EnchantType {

        private final String type;
        private final int tier;

        public EnchantType(String type, int tier) {
            this.type = type;
            this.tier = tier;
        }

        public String getType() {
            return type;
        }

        public int getTier() {
            return tier;
        }
    }

    class PotionEffectType {

        private final String type;
        private final int duration;
        private final int amplifier;

        public PotionEffectType(String type, int duration, int amplifier) {
            this.type = type;
            this.duration = duration;
            this.amplifier = amplifier;
        }

        public int getDuration() {
            return duration;
        }

        public int getAmplifier() {
            return amplifier;
        }

        public String getType() {
            return type;
        }
    }

}
