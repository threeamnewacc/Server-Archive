package net.badlion.cosmetics.inventories;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.common.libraries.StringCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.managers.ParticleManager;
import net.badlion.cosmetics.particles.OwnerTrail;
import net.badlion.cosmetics.particles.Particle;
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

public class ParticleInventory {

    private static SmellyInventory.SmellyInventoryHandler smellyInventoryHandler;

    public static void initialize() {
        ParticleInventory.smellyInventoryHandler = new ParticleInventoryScreenHandler();
    }

    public static void openParticleInventory(Player player, SmellyInventory smellyInventory, int slot) {
        if (!Cosmetics.getInstance().isParticlesEnabled()) {
            player.sendMessage(ChatColor.RED + "Particles are disabled at the moment.");
            return;
        }

        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());

        // Has their data loaded yet?
        if (!cosmeticsSettings.isLoaded()) {
            player.sendMessage(ChatColor.RED + "Your data has not loaded yet, try again in a few seconds.");
            return;
        }

        // Recreate the sub inventory every time we open it so we don't have to handle dynamic adding/removing
        Inventory inventory = smellyInventory.createInventory(smellyInventory.getFakeHolder(), ParticleInventory.smellyInventoryHandler,
                slot, 54, ChatColor.GOLD + "Trails");

        // Fill with particles
        int i = 10;
        for (Map.Entry<String, Particle> entry : ParticleManager.particles.entrySet()) {
            // If the owner trail isn't enabled, and it's trying to get it, don't let it
            if (entry.getKey().equals("owner_trail") && !OwnerTrail.isEnabled()) {
                continue;
            }

            if (i != 46) {
                inventory.setItem(i, getParticleItemStack(entry.getValue(), player));
            } else {
                inventory.setItem(49, getParticleItemStack(entry.getValue(), player));
            }

            if (i == 25 || i == 16 || i == 34 || i == 43) {
                i += 3;
            } else {
                i++;
            }
        }

        // Add unequip particles and arrow trails items
        String particleName = "";
        if (cosmeticsSettings.getActiveParticle() == null)
            particleName = "None";
        else {
            int i2 = 0;
            for (char c : cosmeticsSettings.getActiveParticle().getName().replace('_', ' ').toCharArray()) {
                if (i2 == 0) {
                    particleName += Character.toUpperCase(c);
                } else {
                    particleName += c;
                }
                if (c == ' ')
                    i2 = -1;
                i2++;
            }
        }
        inventory.setItem(45, ItemStackUtil.createItem(Material.QUARTZ_BLOCK, ChatColor.GREEN + "Unequip Trail",
                ChatColor.LIGHT_PURPLE + particleName));

        BukkitUtil.openInventory(player, inventory);
    }

    public static ItemStack getParticleItemStack(Particle particle, Player player) {
        if ((particle.isAllowedForAllPermissions() && player.hasPermission("badlion.allcosmetics")) || CosmeticsManager.getCosmeticsSettings(player.getUniqueId()).hasParticle(particle)) {
            ItemStack itemStack = particle.getItemStack().clone();
            ItemMeta meta = itemStack.getItemMeta();
            List<String> lore = meta.getLore();
            lore.add("");
            lore.add(ChatColor.GRAY + "Rarity: " + particle.getCaseItemRarity());
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
            if (CosmeticsManager.getCosmeticsSettings(player.getUniqueId()).getActiveParticle() == particle) {
                itemStack = Gberry.getGlowItem(itemStack);
            }
            return itemStack;
        } else {
            List<String> lore = particle.getItemStack().getItemMeta().getLore();
            lore.add("");
            lore.add(ChatColor.GRAY + "Find this particle in a cosmetic case!");
            lore.add(ChatColor.GRAY + "Rarity: " + particle.getCaseItemRarity());
            return ItemStackUtil.createItem(Material.INK_SACK, (short) 8, ChatColor.RED + particle.getItemStack().getItemMeta().getDisplayName().substring(2),
                    lore);
        }
    }

    public static class ParticleInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

        @Override
        public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
            CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());

            // Set as active particle
            if (item.getType() == Material.INK_SACK && item.getData().getData() == (short) 8) {
                if (item.hasItemMeta() && item.getItemMeta().hasLore()
                        && item.getItemMeta().getLore().contains(ChatColor.GOLD + ChatColor.BOLD.toString() + "Special")) {
                    player.sendMessage(ChatColor.RED + "You do not have this particle! Special items cannot be obtained via cases.");
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have this particle! Find it in a cosmetic case!");
                }
                player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "ITEM_BREAK", "ENTITY_ITEM_BREAK"), 1.0f, 1.0f);
                return;
            } else if (item.getType() == Material.QUARTZ_BLOCK) {
                if (cosmeticsSettings.getActiveParticle() != null) {
                    player.sendMessage(ChatColor.YELLOW + "You have unequipped " + StringCommon.niceUpperCase(cosmeticsSettings.getActiveParticle().getName()));
                    cosmeticsSettings.setActiveParticle(null, true);
                } else {
                    player.sendMessage(ChatColor.YELLOW + "You don't have an equipped trail!");
                }
            } else {
                Particle particle = ParticleManager.getParticle(item.getItemMeta().getDisplayName().substring(2).replace(" ", "_").toLowerCase());
                cosmeticsSettings.setActiveParticle(particle, true);

                player.sendMessage(ChatColor.GREEN + "Equipped " + StringCommon.cleanEnum(particle.getName()));
            }

            BukkitUtil.closeInventory(player);
        }

        @Override
        public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

        }

    }
}
