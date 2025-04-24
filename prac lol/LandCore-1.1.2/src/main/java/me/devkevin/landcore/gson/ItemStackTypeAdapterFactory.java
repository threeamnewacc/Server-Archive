package me.devkevin.landcore.gson;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.gson.item.EnchantType;
import me.devkevin.landcore.gson.item.ItemMetaType;
import me.devkevin.landcore.gson.item.ItemType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 18/01/2023 @ 15:15
 * ItemStackTypeAdapterFactory / land.pvp.core.gson / LandCore
 */
public class ItemStackTypeAdapterFactory implements TypeAdapterFactory {

    public static ItemType serializeItem(ItemStack itemStack) {
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
                    List<PotionEffect> potionEffects = new ArrayList<>();
                    potionEffects.addAll(potionMeta.getCustomEffects());
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

    public static ItemStack deserializeItem(ItemType item) {
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
                for (PotionEffect effect : item.getMeta().getPotionEffects()) {
                    potionMeta.addCustomEffect(effect, true);
                }
            }
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!ItemStack.class.isAssignableFrom(type.getRawType())) {
            return null;
        }

        return new TypeAdapter<T>() {

            @Override
            public void write(JsonWriter writer, T item) throws IOException {
                if (item == null) {
                    writer.nullValue();
                } else {
                    LandCore.GSON.toJson(serializeItem((ItemStack) item), ItemType.class, writer);
                }
            }

            @Override
            public T read(JsonReader reader) throws IOException {
                if (reader.peek() == JsonToken.NULL) {
                    reader.nextNull();
                    return null;
                } else {
                    return (T) deserializeItem(LandCore.GSON.fromJson(reader, ItemType.class));
                }
            }
        };
    }
}
