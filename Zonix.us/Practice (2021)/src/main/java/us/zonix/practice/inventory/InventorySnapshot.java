package us.zonix.practice.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.json.simple.JSONObject;
import us.zonix.practice.Practice;
import us.zonix.practice.match.Match;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.util.ItemUtil;
import us.zonix.practice.util.MathUtil;
import us.zonix.practice.util.StringUtil;
import us.zonix.practice.util.inventory.InventoryUI;

public class InventorySnapshot {
    private final InventoryUI inventoryUI;
    private final ItemStack[] originalInventory;
    private final ItemStack[] originalArmor;
    private final UUID snapshotId = UUID.randomUUID();

    public InventorySnapshot(final Player player, Match match) {
        ItemStack[] contents = player.getInventory().getContents();
        ItemStack[] armor = player.getInventory().getArmorContents();
        this.originalInventory = contents;
        this.originalArmor = armor;
        PlayerData playerData = Practice.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
        double health = player.getHealth();
        double food = (double)player.getFoodLevel();
        List<String> potionEffectStrings = new ArrayList<>();

        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            String romanNumeral = MathUtil.convertToRomanNumeral(potionEffect.getAmplifier() + 1);
            String effectName = StringUtil.toNiceString(potionEffect.getType().getName().toLowerCase());
            String duration = MathUtil.convertTicksToMinutes(potionEffect.getDuration());
            potionEffectStrings.add(
                ChatColor.YELLOW.toString()
                    + ChatColor.BOLD
                    + "* "
                    + ChatColor.WHITE
                    + effectName
                    + " "
                    + romanNumeral
                    + ChatColor.GRAY
                    + " ("
                    + duration
                    + ")"
            );
        }

        this.inventoryUI = new InventoryUI(player.getName() + "'s Inventory", true, 6);

        for (int i = 0; i < 9; i++) {
            this.inventoryUI.setItem(i + 27, new InventoryUI.EmptyClickableItem(contents[i]));
            this.inventoryUI.setItem(i + 18, new InventoryUI.EmptyClickableItem(contents[i + 27]));
            this.inventoryUI.setItem(i + 9, new InventoryUI.EmptyClickableItem(contents[i + 18]));
            this.inventoryUI.setItem(i, new InventoryUI.EmptyClickableItem(contents[i + 9]));
        }

        boolean potionMatch = false;
        boolean soupMatch = false;

        for (ItemStack item : match.getKit().getContents()) {
            if (item != null) {
                if (item.getType() == Material.MUSHROOM_SOUP) {
                    soupMatch = true;
                    break;
                }

                if (item.getType() == Material.POTION && item.getDurability() == 16421) {
                    potionMatch = true;
                    break;
                }
            }
        }

        int potCount = 0;
        if (potionMatch) {
            potCount = (int)Arrays.stream(contents).filter(Objects::nonNull).<Short>map(ItemStack::getDurability).filter(d -> d == 16421).count();
            this.inventoryUI
                .setItem(
                    45,
                    new InventoryUI.EmptyClickableItem(
                        ItemUtil.reloreItem(
                            ItemUtil.createItem(Material.POTION, ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Potions", potCount, (short)16421),
                            ChatColor.DARK_PURPLE.toString()
                                + ChatColor.BOLD
                                + "* "
                                + ChatColor.WHITE
                                + "Health Pots: "
                                + ChatColor.GRAY
                                + potCount
                                + " Potion"
                                + (potCount > 1 ? "s" : ""),
                            ChatColor.DARK_PURPLE.toString()
                                + ChatColor.BOLD
                                + "* "
                                + ChatColor.WHITE
                                + "Missed Pots: "
                                + ChatColor.GRAY
                                + playerData.getMissedPots()
                                + " Potion"
                                + (playerData.getMissedPots() > 1 ? "s" : "")
                        )
                    )
                );
        } else if (soupMatch) {
            int soupCount = (int)Arrays.stream(contents)
                .filter(Objects::nonNull)
                .<Material>map(ItemStack::getType)
                .filter(d -> d == Material.MUSHROOM_SOUP)
                .count();
            this.inventoryUI
                .setItem(
                    45,
                    new InventoryUI.EmptyClickableItem(
                        ItemUtil.createItem(
                            Material.MUSHROOM_SOUP,
                            ChatColor.GOLD.toString() + ChatColor.BOLD + "Soups Left: " + ChatColor.WHITE + soupCount,
                            soupCount,
                            (short)16421
                        )
                    )
                );
        }

        double roundedHealth = (double)Math.round(health / 2.0 * 2.0) / 2.0;
        this.inventoryUI
            .setItem(
                49,
                new InventoryUI.EmptyClickableItem(
                    ItemUtil.createItem(
                        Material.SKULL_ITEM, ChatColor.RED.toString() + ChatColor.BOLD + "❤ " + roundedHealth + " HP", (int)Math.round(health / 2.0)
                    )
                )
            );
        double roundedFood = (double)Math.round(food / 2.0 * 2.0) / 2.0;
        this.inventoryUI
            .setItem(
                48,
                new InventoryUI.EmptyClickableItem(
                    ItemUtil.createItem(Material.COOKED_BEEF, ChatColor.RED.toString() + ChatColor.BOLD + roundedFood + " Hunger", (int)Math.round(food / 2.0))
                )
            );
        this.inventoryUI
            .setItem(
                47,
                new InventoryUI.EmptyClickableItem(
                    ItemUtil.reloreItem(
                        ItemUtil.createItem(
                            Material.BREWING_STAND_ITEM, ChatColor.GOLD.toString() + ChatColor.BOLD + "Potion Effects", potionEffectStrings.size()
                        ),
                        potionEffectStrings.toArray(new String[0])
                    )
                )
            );
        this.inventoryUI
            .setItem(
                46,
                new InventoryUI.EmptyClickableItem(
                    ItemUtil.reloreItem(
                        ItemUtil.createItem(Material.CAKE, ChatColor.YELLOW.toString() + ChatColor.BOLD + "Stats"),
                        ChatColor.GOLD.toString()
                            + ChatColor.BOLD
                            + "* "
                            + ChatColor.WHITE
                            + "Longest Combo: "
                            + ChatColor.GRAY
                            + playerData.getLongestCombo()
                            + " Hit"
                            + (playerData.getLongestCombo() > 1 ? "s" : ""),
                        ChatColor.GOLD.toString()
                            + ChatColor.BOLD
                            + "* "
                            + ChatColor.WHITE
                            + "Total Hits: "
                            + ChatColor.GRAY
                            + playerData.getHits()
                            + " Hit"
                            + (playerData.getHits() > 1 ? "s" : ""),
                        ChatColor.GOLD.toString()
                            + ChatColor.BOLD
                            + "* "
                            + ChatColor.WHITE
                            + "Potion Accuracy: "
                            + ChatColor.GRAY
                            + (playerData.getMissedPots() > 0 ? (int)((28.0 - (double)playerData.getMissedPots()) / 28.0 * 100.0) + "%" : "100%")
                    )
                )
            );
        if (!match.isParty()) {
            this.inventoryUI
                .setItem(
                    53,
                    new InventoryUI.AbstractClickableItem(ItemUtil.reloreItem(ItemUtil.createItem(Material.LEVER, ChatColor.RED + "Next Inventory"))) {
                        @Override
                        public void onClick(InventoryClickEvent inventoryClickEvent) {
                            Player clicker = (Player)inventoryClickEvent.getWhoClicked();
                            if (Practice.getInstance().getMatchManager().isRematching(player.getUniqueId())) {
                                clicker.closeInventory();
                                Practice.getInstance()
                                    .getServer()
                                    .dispatchCommand(
                                        clicker, "inventory " + Practice.getInstance().getMatchManager().getRematcherInventory(player.getUniqueId())
                                    );
                            }
                        }
                    }
                );
        }

        for (int i = 36; i < 40; i++) {
            this.inventoryUI.setItem(i, new InventoryUI.EmptyClickableItem(armor[39 - i]));
        }
    }

    public JSONObject toJson() {
        JSONObject object = new JSONObject();
        JSONObject inventoryObject = new JSONObject();

        for (int i = 0; i < this.originalInventory.length; i++) {
            inventoryObject.put(i, this.encodeItem(this.originalInventory[i]));
        }

        object.put("inventory", inventoryObject);
        JSONObject armourObject = new JSONObject();

        for (int i = 0; i < this.originalArmor.length; i++) {
            armourObject.put(i, this.encodeItem(this.originalArmor[i]));
        }

        object.put("armour", armourObject);
        return object;
    }

    private JSONObject encodeItem(ItemStack itemStack) {
        if (itemStack != null && itemStack.getType() != Material.AIR) {
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
        } else {
            return null;
        }
    }

    public InventoryUI getInventoryUI() {
        return this.inventoryUI;
    }

    public ItemStack[] getOriginalInventory() {
        return this.originalInventory;
    }

    public ItemStack[] getOriginalArmor() {
        return this.originalArmor;
    }

    public UUID getSnapshotId() {
        return this.snapshotId;
    }
}
