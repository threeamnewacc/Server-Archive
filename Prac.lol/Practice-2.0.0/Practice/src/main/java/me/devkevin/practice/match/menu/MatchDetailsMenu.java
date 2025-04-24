package me.devkevin.practice.match.menu;

import club.inverted.chatcolor.CC;
import lombok.Getter;
import me.devkevin.practice.Practice;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.util.ItemBuilder;
import me.devkevin.practice.util.ItemUtil;
import me.devkevin.practice.util.MathUtil;
import me.devkevin.practice.util.PlayerUtil;
import me.devkevin.practice.util.inventory.InventoryUI;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * Copyright 07/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
public class MatchDetailsMenu {

    private final Practice plugin = Practice.getInstance();

    private final InventoryUI inventoryUI;
    private final ItemStack[] inventory;
    private final ItemStack[] armor;

    @Getter private final UUID snapshotId = UUID.randomUUID();

    public MatchDetailsMenu(Player player, Match match) {
        ItemStack[] contents = player.getInventory().getContents();
        ItemStack[] armor = player.getInventory().getArmorContents();

        this.inventory = contents;
        this.armor = armor;

        Profile profile = plugin.getProfileManager().getProfileData(player.getUniqueId());

        double health = player.getHealth();
        double food = player.getFoodLevel();

        List<String> potionEffectStrings = new ArrayList<>();

        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            String romanNumeral = MathUtil.convertToRomanNumeral(potionEffect.getAmplifier() + 1);
            String effectName = PlayerUtil.toNiceString(potionEffect.getType().getName().toLowerCase());
            String duration = MathUtil.convertTicksToMinutes(potionEffect.getDuration());

            effectName = effectName.replace('_', ' ');
            effectName = effectName.substring(0, 1).toUpperCase() + effectName.substring(1);

            potionEffectStrings.add(CC.YELLOW + effectName + " " + romanNumeral + CC.GRAY + " (" + duration + ")");
        }

        this.inventoryUI = new InventoryUI("Inventory of " + player.getName(), true, 6);

        for (int i = 0; i < 9; i++) {
            this.inventoryUI.setItem(i + 27, new InventoryUI.EmptyClickableItem(contents[i]));
            this.inventoryUI.setItem(i + 18, new InventoryUI.EmptyClickableItem(contents[i + 27]));
            this.inventoryUI.setItem(i + 9, new InventoryUI.EmptyClickableItem(contents[i + 18]));
            this.inventoryUI.setItem(i, new InventoryUI.EmptyClickableItem(contents[i + 9]));
        }

        boolean potionMatch = false;
        boolean soupMatch = false;

        for (ItemStack item : match.getKit().getContents()) {
            if (item == null) {
                continue;
            }
            if (item.getType() == Material.MUSHROOM_SOUP) {
                soupMatch = true;
                break;
            } else if (item.getType() == Material.POTION && item.getDurability() == (short) 16421) {
                potionMatch = true;
                break;
            }
        }

        if (potionMatch) {
            int potCount = (int) Arrays.stream(contents).filter(Objects::nonNull).map(ItemStack::getDurability).filter(d -> d == 16421).count();

            this.inventoryUI.setItem(49, new InventoryUI.EmptyClickableItem(
                    new ItemBuilder(Material.POTION)
                            .name(CC.YELLOW + "Health Potins: " + CC.GOLD + potCount)
                            .amount(potCount).durability(16421)
                            .lore(CC.YELLOW + "Potions Thrown: " + CC.GOLD + profile.getPotionsThrown())
                            .lore(CC.YELLOW + "Missed Potions: " + CC.GOLD + profile.getPotionsMissed())
                            .lore(CC.YELLOW + "Potions Wasted: " + CC.GOLD + profile.getWastedHP())
                            .lore(CC.YELLOW + "Potion Accuracy: " + CC.GOLD + (profile.getPotionsMissed() > 0 ? (int) ((( 28.0 - (double) profile.getPotionsMissed()) / 28.0) * 100.0) + "%" : "100%"))
                            .build()));

        } else if (soupMatch) {
            int soupCount = (int) Arrays.stream(contents).filter(Objects::nonNull).map(ItemStack::getType).filter(d -> d == Material.MUSHROOM_SOUP).count();

            this.inventoryUI.setItem(49, new InventoryUI.EmptyClickableItem(ItemUtil.createItem(
                    Material.MUSHROOM_SOUP,CC.YELLOW + "Soups Left: " + CC.GOLD + soupCount, soupCount, (short) 16421)));
        }

        final double roundedHealth = Math.round(health / 2.0 * 2.0) / 2.0;

        this.inventoryUI.setItem(45,
                new InventoryUI.EmptyClickableItem(ItemUtil.createItem(Material.SKULL_ITEM, CC.YELLOW + "Health: " + CC.GOLD + roundedHealth + "/10.0 " + StringEscapeUtils.unescapeJava("\u2764"), (int) Math.round(health / 2.0D))));

        final double roundedFood = Math.round(health / 2.0 * 2.0) / 2.0;

        this.inventoryUI.setItem(46,
                new InventoryUI.EmptyClickableItem(ItemUtil.createItem(Material.COOKED_BEEF, CC.YELLOW + "Hunger: " + CC.GOLD + roundedFood + "/10.0", (int) Math.round(food / 2.0D))));

        this.inventoryUI.setItem(47,
                new InventoryUI.EmptyClickableItem(ItemUtil.reloreItem(
                        ItemUtil.createItem(Material.BREWING_STAND_ITEM, CC.YELLOW + "Potion Effects", potionEffectStrings.size())
                        , potionEffectStrings.toArray(new String[]{}))));

        this.inventoryUI.setItem(48, new InventoryUI.EmptyClickableItem(
                new ItemBuilder(Material.DIAMOND_SWORD).name(CC.YELLOW + "Statistics").
                        lore(CC.YELLOW + "Longest Combo: " + CC.GOLD + profile.getLongestCombo() + " Hit" + (profile.getLongestCombo() > 1 ? "s" : "")).
                        lore(CC.YELLOW + "Total Hits: " + CC.GOLD + profile.getHits() + " Hit" + (profile.getHits() > 1 ? "s" : "")).
                        lore(CC.YELLOW + "Critical Hits: " + CC.GOLD + profile.getCriticalHits()).
                        lore(CC.YELLOW + "Blocked Hits: " + CC.GOLD + profile.getBlockedHits())
                        .build()));

        if (!match.isParty()) {
            this.inventoryUI.setItem(53, new InventoryUI.AbstractClickableItem(
                    ItemUtil.reloreItem(ItemUtil.createItem(Material.PAPER, CC.YELLOW + "Opponent's Inventory"), CC.GRAY + "See your opponent's inventory", CC.GRAY + "and combat details.")) {
            @Override
                public void onClick(InventoryClickEvent inventoryClickEvent) {
                    Player clicker = (Player) inventoryClickEvent.getWhoClicked();

                    final UUID uuid = plugin.getMatchManager().getRematcherInventory(player.getUniqueId());

                    if (uuid == null) {
                        clicker.sendMessage(CC.RED + "The inventory you're currently viewing has expired.");
                        clicker.closeInventory();
                        return;
                    }

                    clicker.performCommand("inventory " + uuid);
                }
            });
        }

        for (int i = 36; i < 40; i++) {
            this.inventoryUI.setItem(i, new InventoryUI.EmptyClickableItem(armor[39 - i]));
        }
    }

    public JSONObject toJson() {
        JSONObject object = new JSONObject();

        JSONObject inventoryObject = new JSONObject();
        for (int i = 0; i < this.inventory.length; i++) {
            inventoryObject.put(i, this.encodeItem(this.inventory[i]));
        }
        object.put("inventory", inventoryObject);

        JSONObject armourObject = new JSONObject();
        for (int i = 0; i < this.armor.length; i++) {
            armourObject.put(i, this.encodeItem(this.armor[i]));
        }
        object.put("armour", armourObject);

        return object;
    }

    private JSONObject encodeItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return null;
        }

        JSONObject object = new JSONObject();
        object.put("material", itemStack.getType().name());
        object.put("durability", itemStack.getDurability());
        object.put("amount", itemStack.getAmount());

        JSONObject enchants = new JSONObject();
        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            enchants.put(enchantment.getName(), itemStack.getEnchantments().get(enchantment));
        }
        object.put("enchants", enchants);

        return object;
    }
}
