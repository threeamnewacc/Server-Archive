package net.badlion.cosmetics.inventories;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.common.libraries.StringCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.gadgets.Gadget;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.managers.GadgetManager;
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

public class GadgetInventory {

    private static SmellyInventory.SmellyInventoryHandler smellyInventoryHandler;

    public static void initialize() {
        GadgetInventory.smellyInventoryHandler = new GadgetInventoryScreenHandler();
    }

    public static void openGadgetInventory(Player player, SmellyInventory smellyInventory, int slot) {
        if (!Cosmetics.getInstance().isGadgetsEnabled()) {
            player.sendMessage(ChatColor.RED + "Gadgets are disabled at the moment.");
            return;
        }

        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());

        // Has their data loaded yet?
        if (cosmeticsSettings == null || !cosmeticsSettings.isLoaded()) {
            player.sendMessage(ChatColor.RED + "Your data has not loaded yet, try again in a few seconds.");
            return;
        }

        // Recreate the sub inventory every time we open it so we don't have to handle dynamic adding/removing
        Inventory inventory = smellyInventory.createInventory(smellyInventory.getFakeHolder(), GadgetInventory.smellyInventoryHandler,
                slot, 54, ChatColor.GOLD + "Gadgets");

        // Fill with gadgets
        int i = 10;
        int e = 1;
        for (Map.Entry<String, Gadget> entry : GadgetManager.getGadgets().entrySet()) {
            inventory.setItem(i, getGadgetItemStack(entry.getValue(), player));
            if (i == 25 || i == 16 || i == 34 || i == 43) i += 3;
            else i++;
            e++;
        }

        // Add unequip gadgets item
        String gadgetName = "";
        if (cosmeticsSettings.getActiveGadget() == null)
            gadgetName = "None";
        else {
            gadgetName = StringCommon.niceUpperCase(cosmeticsSettings.getActiveGadget().getName());
        }
        inventory.setItem(45, ItemStackUtil.createItem(Material.QUARTZ_BLOCK, ChatColor.GREEN + "Unequip Gadget",
                ChatColor.LIGHT_PURPLE + gadgetName));

        BukkitUtil.openInventory(player, inventory);
    }

    public static ItemStack getGadgetItemStack(Gadget gadget, Player player) {
        if ((gadget.isAllowedForAllPermissions() && player.hasPermission("badlion.allcosmetics")) || CosmeticsManager.getCosmeticsSettings(player.getUniqueId()).hasGadget(gadget)) {
            ItemStack itemStack = gadget.getItemStack().clone();
            ItemMeta meta = itemStack.getItemMeta();
            List<String> lore = meta.getLore();
            lore.add("");
            lore.add(ChatColor.GRAY + "Rarity: " + gadget.getCaseItemRarity());
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
            if (CosmeticsManager.getCosmeticsSettings(player.getUniqueId()).getActiveGadget() == gadget) {
                itemStack = Gberry.getGlowItem(itemStack);
            }
            return itemStack;
        } else {
            List<String> lore = gadget.getItemStack().getItemMeta().getLore();
            lore.add("");
            lore.add(ChatColor.GRAY + "Find this gadget in a cosmetic case!");
            lore.add(ChatColor.GRAY + "Rarity: " + gadget.getCaseItemRarity());
            return ItemStackUtil.createItem(Material.INK_SACK, (short) 8, ChatColor.RED + gadget.getItemStack().getItemMeta().getDisplayName().substring(2), lore);
        }
    }

    public static class GadgetInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

        @Override
        public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
            CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());

            // Set as active gadget
            if (item.getType() == Material.INK_SACK && item.getData().getData() == (short) 8) {
                if (item.hasItemMeta() && item.getItemMeta().hasLore()
                        && item.getItemMeta().getLore().contains(ChatColor.GOLD + ChatColor.BOLD.toString() + "Special")) {
                    player.sendMessage(ChatColor.RED + "You do not have this gadget! Special items cannot be obtained via cases.");
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have this gadget! Find it in a cosmetic case!");
                }
                player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "ITEM_BREAK", "ENTITY_ITEM_BREAK"), 1.0f, 1.0f);
                return;
            } else if (item.getType() != Material.QUARTZ_BLOCK) {
                Gadget gadget = GadgetManager.getGadget(item.getItemMeta().getDisplayName().substring(2).replace(" ", "_").toLowerCase());
                gadget.giveGadget(player);
                player.updateInventory();
                cosmeticsSettings.setActiveGadget(gadget);

                if (!gadget.getName().equals("tnt")) {
                    player.sendMessage(ChatColor.GREEN + "Equipped " + StringCommon.cleanEnum(gadget.getName()));
                } else {
                    player.sendMessage(ChatColor.GREEN + "Equipped TNT");
                }
            } else {
                if (cosmeticsSettings.getActiveGadget() != null) {
                    player.sendMessage(ChatColor.YELLOW + "You have unequipped " + StringCommon.niceUpperCase(cosmeticsSettings.getActiveGadget().getName()));
                    cosmeticsSettings.setActiveGadget(null);
                } else {
                    player.sendMessage(ChatColor.YELLOW + "You don't have an equipped gadget!");
                }
            }

            BukkitUtil.closeInventory(player);
        }

        @Override
        public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

        }

    }

}
