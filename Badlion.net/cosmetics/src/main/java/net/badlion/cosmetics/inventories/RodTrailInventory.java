package net.badlion.cosmetics.inventories;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.common.libraries.StringCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.managers.RodTrailManager;
import net.badlion.cosmetics.rodtrails.RodTrail;
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

import java.util.List;
import java.util.Map;

public class RodTrailInventory {

    public static SmellyInventory.SmellyInventoryHandler smellyInventoryHandler;

    public static void initialize() {
        RodTrailInventory.smellyInventoryHandler = new RodTrailInventoryScreenHandler();
    }

    public static void openRodTrailInventory(Player player, SmellyInventory smellyInventory, int slot) {
        if (!Cosmetics.getInstance().isRodTrailsEnabled()) {
            player.sendMessage(ChatColor.RED + "Rod Trails are disabled at the moment.");
            return;
        }

        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());

        // Has their data loaded yet?
        if (cosmeticsSettings == null || !cosmeticsSettings.isLoaded()) {
            player.sendMessage(ChatColor.RED + "Your data has not loaded yet, try again in a few seconds.");
            return;
        }

        // Recreate the sub inventory every time we open it so we don't have to handle dynamic adding/removing
        Inventory inventory = smellyInventory.createInventory(smellyInventory.getFakeHolder(), RodTrailInventory.smellyInventoryHandler,
                slot, 54, ChatColor.GOLD + "Rod Trails");

        // Fill with rodtrails
        int i = 10;
        for (Map.Entry<String, RodTrail> entry : RodTrailManager.rodtrails.entrySet()) {
            if (i != 46) {
                inventory.setItem(i, getRodTrailItemStack(entry.getValue(), player));
            } else {
                inventory.setItem(49, getRodTrailItemStack(entry.getValue(), player));
            }

            if (i == 25 || i == 16 || i == 34 || i == 43) {
                i += 3;
            } else {
                i++;
            }
        }

        // Add unequip rodtrails item
        String rodTrailName = "";
        if (cosmeticsSettings.getActiveRodTrail() == null)
            rodTrailName = "None";
        else {
            int i2 = 0;
            for (char c : cosmeticsSettings.getActiveRodTrail().getName().replace('_', ' ').toCharArray()) {
                if (i2 == 0) {
                    rodTrailName += Character.toUpperCase(c);
                } else {
                    rodTrailName += c;
                }
                if (c == ' ')
                    i2 = -1;
                i2++;
            }
        }
        inventory.setItem(45, ItemStackUtil.createItem(Material.QUARTZ_BLOCK, ChatColor.GREEN + "Unequip Rod Trail",
                ChatColor.LIGHT_PURPLE + rodTrailName));

        BukkitUtil.openInventory(player, inventory);
    }

    public static ItemStack getRodTrailItemStack(RodTrail rodtrail, Player player) {
        if ((rodtrail.isAllowedForAllPermissions() && player.hasPermission("badlion.allcosmetics")) || CosmeticsManager.getCosmeticsSettings(player.getUniqueId()).hasRodTrail(rodtrail)) {
            ItemStack itemStack = rodtrail.getItemStack().clone();
            ItemMeta meta = itemStack.getItemMeta();
            List<String> lore = meta.getLore();
            lore.add("");
            lore.add(ChatColor.GRAY + "Rarity: " + rodtrail.getCaseItemRarity());
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
            if (CosmeticsManager.getCosmeticsSettings(player.getUniqueId()).getActiveRodTrail() == rodtrail) {
                itemStack = Gberry.getGlowItem(itemStack);
            }
            return itemStack;
        } else {
            List<String> lore = rodtrail.getItemStack().getItemMeta().getLore();
            lore.add("");
            lore.add(ChatColor.GRAY + "Find this rod trail in a cosmetic case!");
            lore.add(ChatColor.GRAY + "Rarity: " + rodtrail.getCaseItemRarity());
            return ItemStackUtil.createItem(Material.INK_SACK, (short) 8, ChatColor.RED + rodtrail.getItemStack().getItemMeta().getDisplayName().substring(2),
                    lore);
        }
    }

    public static class RodTrailInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

        @Override
        public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
            CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());

            // Set as active rodtrail
            if (item.getType() == Material.INK_SACK && item.getData().getData() == (short) 8) {
                if (item.hasItemMeta() && item.getItemMeta().hasLore()
                        && item.getItemMeta().getLore().contains(ChatColor.GOLD + ChatColor.BOLD.toString() + "Special")) {
                    player.sendMessage(ChatColor.RED + "You do not have this rod trail! Special items cannot be obtained via cases.");
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have this rod trail! Find it in a cosmetic case!");
                }
                player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "ITEM_BREAK", "ENTITY_ITEM_BREAK"), 1.0f, 1.0f);
                return;
            } else if (item.getType() != Material.QUARTZ_BLOCK) {
                RodTrail rodtrail = RodTrailManager.getRodTrail(ChatColor.stripColor(item.getItemMeta().getDisplayName()).replace(" ", "_").toLowerCase());
                cosmeticsSettings.setActiveRodTrail(rodtrail, true);

                player.sendMessage(ChatColor.GREEN + "Equipped " + StringCommon.cleanEnum(rodtrail.getName()));
            } else {
                if (cosmeticsSettings.getActiveRodTrail() != null) {
                    player.sendMessage(ChatColor.YELLOW + "You have unequipped " + StringCommon.niceUpperCase(cosmeticsSettings.getActiveRodTrail().getName()));
                    cosmeticsSettings.setActiveRodTrail(null, true);
                } else {
                    player.sendMessage(ChatColor.YELLOW + "You don't have an equipped rod trail!");
                }
            }

            BukkitUtil.closeInventory(player);
        }

        @Override
        public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

        }
    }
}
