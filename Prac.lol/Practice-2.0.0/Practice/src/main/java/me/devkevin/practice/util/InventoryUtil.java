package me.devkevin.practice.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Copyright 02/01/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class InventoryUtil {

    public static ItemStack[] fixInventoryOrder(ItemStack[] source) {
        ItemStack[] fixed = new ItemStack[36];

        System.arraycopy(source, 0, fixed, 27, 9);
        System.arraycopy(source, 9, fixed, 0, 27);

        return fixed;
    }

    public static String serializeInventory(ItemStack[] source) {
        StringBuilder builder = new StringBuilder();

        for (ItemStack itemStack : source) {
            builder.append(serializeItemStack(itemStack));
            builder.append(";");
        }

        return builder.toString();
    }

    public static ItemStack[] deserializeInventory(String source) {
        List<ItemStack> items = new ArrayList<>();
        String[] split = source.split(";");

        for (String piece : split) {
            items.add(deserializeItemStack(piece));
        }

        return items.toArray(new ItemStack[0]);
    }

    public static String serializeItemStack(ItemStack item) {
        StringBuilder builder = new StringBuilder();

        if (item == null) {
            return "null";
        }

        String isType = String.valueOf(item.getType().getId());
        builder.append("t@").append(isType);

        if (item.getDurability() != 0) {
            String isDurability = String.valueOf(item.getDurability());
            builder.append(":d@").append(isDurability);
        }

        if (item.getAmount() != 1) {
            String isAmount = String.valueOf(item.getAmount());
            builder.append(":a@").append(isAmount);
        }

        Map<Enchantment, Integer> isEnchanted = item.getEnchantments();
        if (isEnchanted.size() > 0) {
            for (Map.Entry<Enchantment, Integer> enchantment : isEnchanted.entrySet()) {
                builder.append(":e@").append(enchantment.getKey().getId()).append("@").append(enchantment.getValue());
            }
        }

        if (item.hasItemMeta()) {
            ItemMeta imeta = item.getItemMeta();
            if (imeta.hasDisplayName()) {
                builder.append(":dn@").append(imeta.getDisplayName());
            }

            if (imeta.hasLore()) {
                builder.append(":l@").append(imeta.getLore());
            }
        }

        return builder.toString();
    }

    public static ItemStack deserializeItemStack(String in) {
        ItemStack item = null;
        ItemMeta meta = null;

        if (in.equals("null")) {
            return new ItemStack(Material.AIR);
        }

        String[] split = in.split(":");
        for (String itemInfo : split) {
            String[] itemAttribute = itemInfo.split("@");
            String s2 = itemAttribute[0];

            switch (s2) {
                case "t":
                    item = new ItemStack(Material.getMaterial(Integer.parseInt(itemAttribute[1])));
                    meta = item.getItemMeta();
                    break;
                case "d":
                    if (item != null) {
                        item.setDurability(Short.parseShort(itemAttribute[1]));
                        break;
                    }
                    break;
                case "a":
                    if (item != null) {
                        item.setAmount(Integer.parseInt(itemAttribute[1]));
                        break;
                    }
                    break;
                case "e":
                    if (item != null) {
                        item.addUnsafeEnchantment(Enchantment.getById(Integer.parseInt(itemAttribute[1])), Integer.parseInt(itemAttribute[2]));
                        break;
                    }
                    break;
                case "dn":
                    if (meta != null) {
                        meta.setDisplayName(itemAttribute[1]);
                        break;
                    }
                    break;
                case "l":
                    itemAttribute[1] = itemAttribute[1].replace("[", "");
                    itemAttribute[1] = itemAttribute[1].replace("]", "");
                    List<String> lore = Arrays.asList(itemAttribute[1].split(","));
                    for (int x = 0; x < lore.size(); ++x) {
                        String s = lore.get(x);
                        if (s != null) {
                            if (s.toCharArray().length != 0) {
                                if (s.charAt(0) == ' ') {
                                    s = s.replaceFirst(" ", "");
                                }
                                lore.set(x, s);
                            }
                        }
                    }
                    if (meta != null) {
                        meta.setLore(lore);
                        break;
                    }

                    break;
            }
        }

        if (meta != null && (meta.hasDisplayName() || meta.hasLore())) {
            item.setItemMeta(meta);
        }

        return item;
    }

    private static int in = 0;
    private static int bk = 0;

    public static String itemToBase64(ItemStack[] items) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(items.length);
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static ItemStack[] itemFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}

