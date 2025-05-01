package net.badlion.cosmetics.inventories;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.common.libraries.StringCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.arrowtrails.ArrowTrail;
import net.badlion.cosmetics.managers.ArrowTrailManager;
import net.badlion.cosmetics.managers.CosmeticsManager;
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

public class ArrowTrailInventory {

    public static SmellyInventory.SmellyInventoryHandler smellyInventoryHandler;

    public static void initialize() {
        ArrowTrailInventory.smellyInventoryHandler = new ArrowTrailInventoryScreenHandler();
    }

    public static void openArrowTrailInventory(Player player, SmellyInventory smellyInventory, int slot) {
        if (!Cosmetics.getInstance().isArrowTrailsEnabled()) {
            player.sendMessage(ChatColor.RED + "Arrow Trails are disabled at the moment.");
            return;
        }

        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());

        // Has their data loaded yet?
        if (cosmeticsSettings == null || !cosmeticsSettings.isLoaded()) {
            player.sendMessage(ChatColor.RED + "Your data has not loaded yet, try again in a few seconds.");
            return;
        }

        // Recreate the sub inventory every time we open it so we don't have to handle dynamic adding/removing
        Inventory inventory = smellyInventory.createInventory(smellyInventory.getFakeHolder(), ArrowTrailInventory.smellyInventoryHandler,
                slot, 54, ChatColor.GOLD + "Arrow Trails");

        // Fill with arrowtrails
        int i = 10;
        for (Map.Entry<String, ArrowTrail> entry : ArrowTrailManager.arrowtrails.entrySet()) {
            if (i != 46) inventory.setItem(i, getArrowTrailItemStack(entry.getValue(), player));
            else inventory.setItem(49, getArrowTrailItemStack(entry.getValue(), player));
            if (i == 25 || i == 16 || i == 34 || i == 43) i += 3;
            else i++;
        }

        // Add unequip arrowtrails item
        String arrowTrailName = "";
        if (cosmeticsSettings.getActiveArrowTrail() == null)
            arrowTrailName = "None";
        else {
            int i2 = 0;
            for (char c : cosmeticsSettings.getActiveArrowTrail().getName().replace('_', ' ').toCharArray()) {
                if (i2 == 0) {
                    arrowTrailName += Character.toUpperCase(c);
                } else {
                    arrowTrailName += c;
                }
                if (c == ' ')
                    i2 = -1;
                i2++;
            }
        }
        inventory.setItem(45, ItemStackUtil.createItem(Material.QUARTZ_BLOCK, ChatColor.GREEN + "Unequip Arrow Trail",
                ChatColor.LIGHT_PURPLE + arrowTrailName));

        BukkitUtil.openInventory(player, inventory);
    }

    public static ItemStack getArrowTrailItemStack(ArrowTrail arrowtrail, Player player) {
        if ((arrowtrail.isAllowedForAllPermissions() && player.hasPermission("badlion.allcosmetics")) || CosmeticsManager.getCosmeticsSettings(player.getUniqueId()).hasArrowTrail(arrowtrail)) {
            ItemStack itemStack = arrowtrail.getItemStack().clone();
            ItemMeta meta = itemStack.getItemMeta();
            List<String> lore = meta.getLore();
            lore.add("");
            lore.add(ChatColor.GRAY + "Rarity: " + arrowtrail.getCaseItemRarity());
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
            if (CosmeticsManager.getCosmeticsSettings(player.getUniqueId()).getActiveArrowTrail() == arrowtrail) {
                itemStack = Gberry.getGlowItem(itemStack);
            }
            return itemStack;
        } else {
            List<String> lore = arrowtrail.getItemStack().getItemMeta().getLore();
            lore.add("");
            lore.add(ChatColor.GRAY + "Find this arrow trail in a cosmetic case!");
            lore.add(ChatColor.GRAY + "Rarity: " + arrowtrail.getCaseItemRarity());
            return ItemStackUtil.createItem(Material.INK_SACK, (short) 8, ChatColor.RED + arrowtrail.getItemStack().getItemMeta().getDisplayName().substring(2),
                    lore);
        }
    }

    public static class ArrowTrailInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

        @Override
        public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
            CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());

            // Set as active arrowtrail
            if (item.getType() == Material.INK_SACK && item.getData().getData() == (short) 8) {
                if (item.hasItemMeta() && item.getItemMeta().hasLore()
                        && item.getItemMeta().getLore().contains(ChatColor.GOLD + ChatColor.BOLD.toString() + "Special")) {
                    player.sendMessage(ChatColor.RED + "You do not have this arrow trail! Special items cannot be obtained via cases.");
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have this arrow trail! Find it in a cosmetic case!");
                }
                player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "ITEM_BREAK", "ENTITY_ITEM_BREAK"), 1.0f, 1.0f);
                return;
            } else if (item.getType() != Material.QUARTZ_BLOCK) {
                ArrowTrail arrowtrail = ArrowTrailManager.getArrowTrail(ChatColor.stripColor(item.getItemMeta().getDisplayName()).replace(" ", "_").toLowerCase());
                cosmeticsSettings.setActiveArrowTrail(arrowtrail, true);

                player.sendMessage(ChatColor.GREEN + "Equipped " + StringCommon.cleanEnum(arrowtrail.getName()));
                if (Gberry.serverType == Gberry.ServerType.LOBBY) {
                    // Bow for testing particles
                    ItemStack bow = ItemStackUtil.createItem(Material.BOW, ChatColor.GOLD + ChatColor.BOLD.toString() + "Particle Bow");
                    ItemMeta bowM = bow.getItemMeta();
                    bowM.spigot().setUnbreakable(true);
                    bow.setItemMeta(bowM);

                    player.getInventory().setItem(7, Gberry.getGlowItem(bow));
                    player.getInventory().setItem(9, new ItemStack(Material.ARROW));
                }
            } else {
                if (cosmeticsSettings.getActiveArrowTrail() != null) {
                    player.sendMessage(ChatColor.YELLOW + "You have unequipped " + StringCommon.niceUpperCase(cosmeticsSettings.getActiveArrowTrail().getName()));
                    cosmeticsSettings.setActiveArrowTrail(null, true);
                } else {
                    player.sendMessage(ChatColor.YELLOW + "You don't have an equipped arrow trail!");
                }
            }

            BukkitUtil.closeInventory(player);
        }

        @Override
        public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

        }
    }
}
