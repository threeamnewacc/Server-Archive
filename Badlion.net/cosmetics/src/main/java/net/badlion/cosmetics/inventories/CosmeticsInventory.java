package net.badlion.cosmetics.inventories;

import net.badlion.common.libraries.StringCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellycases.CaseTier;
import net.badlion.smellycases.managers.CaseDataManager;
import net.badlion.smellycases.managers.CaseManager;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CosmeticsInventory implements Listener {

    private static SmellyInventory.SmellyInventoryHandler cosmeticInventoryHandler;

    private static ItemStack openCosmeticInventoryItem;

    public static void initialize() {
        CosmeticsInventory.cosmeticInventoryHandler = new CosmeticsInventory.CosmeticInventoryScreenHandler();

        CosmeticsInventory.openCosmeticInventoryItem = ItemStackUtil.createItem(Material.ENDER_CHEST, ChatColor.GREEN + ChatColor.BOLD.toString() + "Cosmetics",
                ChatColor.GREEN + "Right click with this item", ChatColor.GREEN + "in hand to see our", ChatColor.GREEN + "server cosmetics.");

    }

    public static void openCosmeticInventory(Player player) {
        // Are all cosmetics disabled?
        if (!Cosmetics.getInstance().isArrowTrailsEnabled() && Cosmetics.getInstance().isGadgetsEnabled() &&
                Cosmetics.getInstance().isMorphsEnabled() && Cosmetics.getInstance().isParticlesEnabled() &&
                Cosmetics.getInstance().isPetsEnabled()) {
            player.sendMessage(ChatColor.RED + "Cosmetics are disabled at the moment.");
            return;
        }

        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());

        if (cosmeticsSettings == null || !cosmeticsSettings.isLoaded()) {
            player.sendMessage(ChatColor.RED + "Your data has not loaded yet, try again in a few seconds.");
            return;
        }

        SmellyInventory cosmeticsInventory = new SmellyInventory(CosmeticsInventory.cosmeticInventoryHandler,
                45, ChatColor.GOLD + "Cosmetics");

        // Pets
        cosmeticsInventory.getMainInventory().setItem(11, getActiveCosmeticItem(ItemStackUtil.createItem(Material.LEASH, ChatColor.GREEN + ChatColor.BOLD.toString() + "Pets",
                ChatColor.GRAY + "Click to choose a pet!",
                "", ChatColor.GREEN + "Active Pet", ChatColor.GRAY + getActiveCosmeticName(cosmeticsSettings, "pets")), "pets", player));

        // Morphs
        cosmeticsInventory.getMainInventory().setItem(13, getActiveCosmeticItem(ItemStackUtil.createItem(Material.MONSTER_EGG, ChatColor.GREEN + ChatColor.BOLD.toString() + "Morphs",
                ChatColor.GRAY + "Click to morph into a mob!",
                "", ChatColor.GREEN + "Active Morph", ChatColor.GRAY + getActiveCosmeticName(cosmeticsSettings, "morphs")), "morphs", player));

        // Gadgets
        cosmeticsInventory.getMainInventory().setItem(15, getActiveCosmeticItem(ItemStackUtil.createItem(Material.ENDER_PEARL, ChatColor.GREEN + ChatColor.BOLD.toString() + "Gadgets",
                ChatColor.GRAY + "Click to equip a gadget!",
                "", ChatColor.GREEN + "Active Gadget", ChatColor.GRAY + getActiveCosmeticName(cosmeticsSettings, "gadgets")), "gadgets", player));

        // Trails
        cosmeticsInventory.getMainInventory().setItem(29, getActiveCosmeticItem(ItemStackUtil.createItem(Material.BLAZE_POWDER, ChatColor.GREEN + ChatColor.BOLD.toString() + "Trails",
                ChatColor.GRAY + "Click to equip a trail!",
                "", ChatColor.GREEN + "Active Trail", ChatColor.GRAY + getActiveCosmeticName(cosmeticsSettings, "particles")), "particles", player));

        // Arrow Trails
        cosmeticsInventory.getMainInventory().setItem(31, getActiveCosmeticItem(ItemStackUtil.createItem(Material.BOW, ChatColor.GREEN + ChatColor.BOLD.toString() + "Arrow Trails",
                ChatColor.GRAY + "Click to equip an arrow trail!",
                "", ChatColor.GREEN + "Active Arrow Trail", ChatColor.GRAY + getActiveCosmeticName(cosmeticsSettings, "arrow_trails")), "arrow_trails", player));

        // Rod Trails
        cosmeticsInventory.getMainInventory().setItem(33, getActiveCosmeticItem(ItemStackUtil.createItem(Material.FISHING_ROD, ChatColor.GREEN + ChatColor.BOLD.toString() + "Rod Trails",
                ChatColor.GRAY + "Click to equip an rod trail!",
                "", ChatColor.GREEN + "Active Rod Trail", ChatColor.GRAY + getActiveCosmeticName(cosmeticsSettings, "rod_trails")), "rod_trails", player));

        // Cosmetics Cases
        cosmeticsInventory.getMainInventory().setItem(36, ItemStackUtil.createItem(Material.CHEST, ChatColor.GREEN + ChatColor.BOLD.toString() + "Cosmetic Case",
                ChatColor.GRAY + "Click to open a cosmetic case!",
                "", ChatColor.GREEN + "Cosmetics Cases", ChatColor.GRAY + String.valueOf(CaseDataManager.getRemainingCases(player, Gberry.ServerType.LOBBY))));

        BukkitUtil.openInventory(player, cosmeticsInventory.getMainInventory());
    }

    private static String getActiveCosmeticName(CosmeticsManager.CosmeticsSettings cosmeticsSettings, String type) {
        if (cosmeticsSettings == null)
            return null;
        String cosmeticName = "None";
        switch (type) {
            case "pets":
                if (cosmeticsSettings.getActivePet() != null) {
                    if (Cosmetics.getInstance().isPetsEnabled()) {
                        cosmeticName = cosmeticsSettings.getActivePet().getName();
                    } else {
                        cosmeticName = "Disabled";
                    }
                }
                break;
            case "arrow_trails":
                if (cosmeticsSettings.getActiveArrowTrail() != null) {
                    if (Cosmetics.getInstance().isArrowTrailsEnabled()) {
                        cosmeticName = cosmeticsSettings.getActiveArrowTrail().getName();
                    } else {
                        cosmeticName = "Disabled";
                    }
                }
                break;
            case "rod_trails":
                if (cosmeticsSettings.getActiveRodTrail() != null) {
                    if (Cosmetics.getInstance().isRodTrailsEnabled()) {
                        cosmeticName = cosmeticsSettings.getActiveRodTrail().getName();
                    } else {
                        cosmeticName = "Disabled";
                    }
                }
                break;
            case "gadgets":
                if (cosmeticsSettings.getActiveGadget() != null) {
                    if (Cosmetics.getInstance().isGadgetsEnabled()) {
                        cosmeticName = cosmeticsSettings.getActiveGadget().getName();
                    } else {
                        cosmeticName = "Disabled";
                    }
                }
                break;
            case "morphs":
                if (cosmeticsSettings.getActiveMorph() != null) {
                    if (Cosmetics.getInstance().isMorphsEnabled()) {
                        cosmeticName = cosmeticsSettings.getActiveMorph().getName();
                    } else {
                        cosmeticName = "Disabled";
                    }
                }
                break;
            case "particles":
                if (cosmeticsSettings.getActiveParticle() != null) {
                    if (Cosmetics.getInstance().isParticlesEnabled()) {
                        cosmeticName = cosmeticsSettings.getActiveParticle().getName();
                    } else {
                        cosmeticName = "Disabled";
                    }
                }
                break;
        }
        return StringCommon.niceUpperCase(cosmeticName.replace("_trail", "").replace("_morph", "").replace("_arrow_trail", "").replace("_rod_trail", ""));
    }

    private static ItemStack getActiveCosmeticItem(ItemStack itemStack, String type, Player player) {
        if (!getActiveCosmeticName(CosmeticsManager.getCosmeticsSettings(player.getUniqueId()), type).equals("None")) {
            itemStack = Gberry.getGlowItem(itemStack);
        }
        return itemStack;
    }

    public static ItemStack getOpenCosmeticInventoryItem() {
        return openCosmeticInventoryItem;
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getItem() != null && event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()
                && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                && event.getItem().getItemMeta().getDisplayName().equals(CosmeticsInventory.getOpenCosmeticInventoryItem().getItemMeta().getDisplayName())) {
            event.setCancelled(true);
            player.updateInventory();

            // Open up the cosmetic inventory
            CosmeticsInventory.openCosmeticInventory(player);
        }
    }

    public static class CosmeticInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

        @Override
        public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, final Player player, InventoryClickEvent event, ItemStack item, int slot) {
            if (item.getType() == Material.MONSTER_EGG) {
                MorphInventory.openMorphInventory(player, fakeHolder.getSmellyInventory(), slot);
            } else if (item.getType() == Material.BLAZE_POWDER) {
                ParticleInventory.openParticleInventory(player, fakeHolder.getSmellyInventory(), slot);
            } else if (item.getType() == Material.LEASH) {
                PetInventory.openPetInventory(player, fakeHolder.getSmellyInventory(), slot);
            } else if (item.getType() == Material.ENDER_PEARL) {
                GadgetInventory.openGadgetInventory(player, fakeHolder.getSmellyInventory(), slot);
            } else if (item.getType() == Material.BOW) {
                ArrowTrailInventory.openArrowTrailInventory(player, fakeHolder.getSmellyInventory(), slot);
            } else if (item.getType() == Material.FISHING_ROD) {
                RodTrailInventory.openRodTrailInventory(player, fakeHolder.getSmellyInventory(), slot);
            } else if (item.getType() == Material.CHEST) {
                CaseManager.openCase(player, CaseTier.NORMAL, Gberry.ServerType.LOBBY);
            }
        }

        @Override
        public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

        }

    }

}
