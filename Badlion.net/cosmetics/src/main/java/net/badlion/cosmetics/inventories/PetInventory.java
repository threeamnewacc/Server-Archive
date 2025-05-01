package net.badlion.cosmetics.inventories;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.common.libraries.StringCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.managers.PetManager;
import net.badlion.cosmetics.pets.Pet;
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

public class PetInventory {

    private static SmellyInventory.SmellyInventoryHandler smellyInventoryHandler;

    public static void initialize() {
        PetInventory.smellyInventoryHandler = new PetInventoryScreenHandler();
    }

    public static void openPetInventory(Player player, SmellyInventory smellyInventory, int slot) {
        if (!Cosmetics.getInstance().isPetsEnabled()) {
            player.sendMessage(ChatColor.RED + "Pets are disabled at the moment.");
            return;
        }

        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());

        // Has their data loaded yet?
        if (cosmeticsSettings == null || !cosmeticsSettings.isLoaded()) {
            player.sendMessage(ChatColor.RED + "Your data has not loaded yet, try again in a few seconds.");
            return;
        }

        String currentPetName = "None";
        if (cosmeticsSettings.getActivePet() != null) {
            String petDisplayName = cosmeticsSettings.getPetDisplayName(cosmeticsSettings.getActivePet().getName());
            currentPetName = petDisplayName != null ? petDisplayName : cosmeticsSettings.getActivePet().getName();
        }

        // Recreate the sub inventory every time we open it so we don't have to handle dynamic adding/removing
        Inventory inventory = smellyInventory.createInventory(smellyInventory.getFakeHolder(), PetInventory.smellyInventoryHandler,
                slot, 54, ChatColor.GOLD + "Pets");

        // Fill with owned pets
        int i = 10;
        for (Map.Entry<String, Pet> entry : PetManager.pets.entrySet()) {
            inventory.setItem(i, getPetItemStack(entry.getValue(), player));
            if (i == 25 || i == 16 || i == 34 || i == 43) i += 3;
            else i++;
        }

        // Add despawn pet item
        String petName = "";
        if (cosmeticsSettings.getActivePet() == null)
            petName = "None";
        else {
            int i2 = 0;
            for (char c : cosmeticsSettings.getActivePet().getName().replace('_', ' ').toCharArray()) {
                if (i2 == 0) {
                    petName += Character.toUpperCase(c);
                } else {
                    petName += c;
                }
                if (c == ' ') {
                    i2 = -1;
                }
                i2++;
            }
        }
        inventory.setItem(45, ItemStackUtil.createItem(Material.QUARTZ_BLOCK, ChatColor.GREEN + "Despawn Pet",
                ChatColor.LIGHT_PURPLE + petName));

        BukkitUtil.openInventory(player, inventory);
    }

    public static ItemStack getPetItemStack(Pet pet, Player player) {
        if ((pet.isAllowedForAllPermissions() && player.hasPermission("badlion.allcosmetics")) || CosmeticsManager.getCosmeticsSettings(player.getUniqueId()).hasPet(pet.getName())) {
            ItemStack itemStack = pet.getItemStack(CosmeticsManager.getCosmeticsSettings(player.getUniqueId())).clone();
            ItemMeta meta = itemStack.getItemMeta();
            List<String> lore = meta.getLore();
            lore.add("");
            lore.add(ChatColor.GRAY + "Rarity: " + pet.getCaseItemRarity());
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
            if (CosmeticsManager.getCosmeticsSettings(player.getUniqueId()).getActivePet() == pet) {
                itemStack = Gberry.getGlowItem(itemStack);
            }
            return itemStack;
        } else {
            List<String> lore = pet.getItemStack(CosmeticsManager.getCosmeticsSettings(player.getUniqueId())).getItemMeta().getLore();
            lore.add("");
            lore.add(ChatColor.GRAY + "Find this morph in a cosmetic case!");
            lore.add(ChatColor.GRAY + "Rarity: " + pet.getCaseItemRarity());
            return ItemStackUtil.createItem(Material.INK_SACK, (short) 8, ChatColor.RED + pet.getItemStack(CosmeticsManager.getCosmeticsSettings(player.getUniqueId())).getItemMeta().getDisplayName().substring(2),
                    lore);
        }
    }

    public static class PetInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

        @Override
        public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
            CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());

            // Set as active particle
            if (item.getType() == Material.INK_SACK && item.getData().getData() == (short) 8) {
                if (item.hasItemMeta() && item.getItemMeta().hasLore()
                        && item.getItemMeta().getLore().contains(ChatColor.GOLD + ChatColor.BOLD.toString() + "Special")) {
                    player.sendMessage(ChatColor.RED + "You do not have this pet! Special items cannot be obtained via cases.");
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have this pet! Find it in a cosmetic case!");
                }
                player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "ITEM_BREAK", "ENTITY_ITEM_BREAK"), 1.0f, 1.0f);
                return;
            } else if (item.getType() != Material.QUARTZ_BLOCK) {
                Pet pet = PetManager.getPet(item.getItemMeta().getDisplayName().substring(2).toLowerCase().replace(" ", "_"));

                // Item had a custom name
                if (pet == null) {
                    pet = PetManager.getPet(item.getItemMeta().getLore().get(0).substring(2).toLowerCase().replace(" ", "_"));
                }

                if (pet == cosmeticsSettings.getActivePet()) {
                    return;
                }
                PetManager.spawnPet(player, cosmeticsSettings, pet);
                player.getInventory().setItem(3, ItemStackUtil.createItem(Material.BLAZE_POWDER, ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Despawn Pet"));
            } else {
                if (cosmeticsSettings.getActivePet() != null) {
                    player.sendMessage(ChatColor.YELLOW + "You have despawned " + StringCommon.niceUpperCase(cosmeticsSettings.getActivePet().getName()) + " pet");
                    PetManager.despawnPet(player, cosmeticsSettings);
                } else {
                    player.sendMessage(ChatColor.YELLOW + "You don't have an equipped pet!");
                }
            }

            BukkitUtil.closeInventory(player);
        }

        @Override
        public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

        }

    }
}
