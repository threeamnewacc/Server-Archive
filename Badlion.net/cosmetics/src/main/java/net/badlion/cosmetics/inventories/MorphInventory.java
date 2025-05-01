package net.badlion.cosmetics.inventories;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.common.libraries.StringCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.managers.MorphManager;
import net.badlion.cosmetics.morphs.Morph;
import net.badlion.cosmetics.utils.MorphUtil;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MorphInventory {

    private static SmellyInventory.SmellyInventoryHandler smellyInventoryHandler;

    public static void initialize() {
        MorphInventory.smellyInventoryHandler = new MorphInventoryScreenHandler();
    }

    public static void openMorphInventory(Player player, SmellyInventory smellyInventory, int slot) {
        if (!Cosmetics.getInstance().isMorphsEnabled()) {
            player.sendMessage(ChatColor.RED + "Morphs are disabled at the moment.");
            return;
        }

        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());

        // Has their data loaded yet?
        if (cosmeticsSettings == null || !cosmeticsSettings.isLoaded()) {
            player.sendMessage(ChatColor.RED + "Your data has not loaded yet, try again in a few seconds.");
            return;
        }

        // Recreate the sub inventory every time we open it so we don't have to handle dynamic adding/removing
        Inventory inventory = smellyInventory.createInventory(smellyInventory.getFakeHolder(), MorphInventory.smellyInventoryHandler,
                slot, 54, ChatColor.GOLD + "Morphs");

        // Fill with owned morphs
        int i = 10;
        for (Map.Entry<String, Morph> entry : MorphManager.getMorphs().entrySet()) {
            inventory.setItem(i, MorphInventory.getMorphItemStack(entry.getValue(), player));
            if (i == 25 || i == 16 || i == 34 || i == 43) i += 3;
            else i++;
        }

        // Add unequip morphs item
        String morphName = "";
        if (cosmeticsSettings.getActiveMorph() == null) {
            morphName = "None";
        } else {
            int i2 = 0;
            for (char c : cosmeticsSettings.getActiveMorph().getName().replace('_', ' ').toCharArray()) {
                if (i2 == 0) {
                    morphName += Character.toUpperCase(c);
                } else {
                    morphName += c;
                }
                if (c == ' ')
                    i2 = -1;
                i2++;
            }
        }

        inventory.setItem(45, ItemStackUtil.createItem(Material.QUARTZ_BLOCK, ChatColor.GREEN + "Unequip Morph",
                ChatColor.LIGHT_PURPLE + morphName));

        BukkitUtil.openInventory(player, inventory);
    }

    public static ItemStack getMorphItemStack(Morph morph, Player player) {
        if ((morph.isAllowedForAllPermissions() && player.hasPermission("badlion.allcosmetics")) || CosmeticsManager.getCosmeticsSettings(player.getUniqueId()).hasMorph(morph)) {
            ItemStack itemStack = morph.getItemStack().clone();
            ItemMeta meta = itemStack.getItemMeta();
            List<String> lore = meta.getLore();
            lore.add("");
            lore.add(ChatColor.GRAY + "Rarity: " + morph.getCaseItemRarity());
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
            if (CosmeticsManager.getCosmeticsSettings(player.getUniqueId()).getActiveMorph() == morph) {
                itemStack = Gberry.getGlowItem(itemStack);
            }
            return itemStack;
        } else {
            List<String> lore = morph.getItemStack().getItemMeta().getLore();
            lore.add("");
            lore.add(ChatColor.GRAY + "Find this morph in a cosmetic case!");
            lore.add(ChatColor.GRAY + "Rarity: " + morph.getCaseItemRarity());
            return ItemStackUtil.createItem(Material.INK_SACK, (short) 8, ChatColor.RED + morph.getItemStack().getItemMeta().getDisplayName().substring(2),
                    lore);
        }
    }

    public static class MorphInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

        @Override
        public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
            CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());

            // Set as active morph
            if (item.getType() == Material.INK_SACK && item.getData().getData() == (short) 8) {
                if (item.hasItemMeta() && item.getItemMeta().hasLore()
                        && item.getItemMeta().getLore().contains(ChatColor.GOLD + ChatColor.BOLD.toString() + "Special")) {
                    player.sendMessage(ChatColor.RED + "You do not have this morph! Special items cannot be obtained via cases.");
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have this morph! Find it in a cosmetic case!");
                }
                player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "ITEM_BREAK", "ENTITY_ITEM_BREAK"), 1.0f, 1.0f);
                return;
            } else if (item.getType() != Material.QUARTZ_BLOCK) {
                Morph morph = MorphManager.getMorph(item.getItemMeta().getDisplayName().substring(2).replace(" ", "_").toLowerCase());
                morph.setMorph(player);
                cosmeticsSettings.setActiveMorph(morph, true);

                if (morph.getMorphType() == MorphUtil.MorphType.WITHER_SKELETON) {
                    player.setWalkSpeed(0.6f);
                } else {
                    player.setWalkSpeed(0.2f);
                }

                player.sendMessage(ChatColor.GREEN + "Equipped " + StringCommon.cleanEnum(morph.getName()));

                player.getInventory().setItem(4,
                        ItemStackUtil.createItem(
                                Material.MONSTER_EGG, ChatColor.RED + ChatColor.BOLD.toString() + "Morph Ability",
                                Arrays.asList(
                                        ChatColor.GREEN + "Right/left click with this",
                                        ChatColor.GREEN + "item in hand to use your",
                                        ChatColor.GREEN + "active morph abilities."
                                )
                        )
                );
                player.getInventory().setItem(2, ItemStackUtil.createItem(Material.ANVIL, ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Remove Morph"));
                player.setAllowFlight(false);
            } else {
                if (cosmeticsSettings.getActiveMorph() != null) {
                    player.sendMessage(ChatColor.YELLOW + "You have unequipped " + StringCommon.niceUpperCase(cosmeticsSettings.getActiveMorph().getName()));
                    cosmeticsSettings.getActiveMorph().removeMorph(player);
                    cosmeticsSettings.setActiveMorph(null, true);
                } else {
                    player.sendMessage(ChatColor.YELLOW + "You don't have an equipped morph!");
                }
            }

            BukkitUtil.closeInventory(player);
        }

        @Override
        public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

        }

    }

}
