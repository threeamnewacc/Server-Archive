package cc.patrone.practice.manager.impl;

import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.manager.Manager;
import cc.patrone.practice.manager.ManagerHandler;
import cc.patrone.practice.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

@Getter
public class InventoryManager extends Manager {

    private Inventory unrankedInventory;
    private Inventory rankedInventory;
    private Inventory editKitInventory;
    private Inventory editKitOptionsInventory;
    private Inventory duelInventory;
    private Inventory leaderboardsInventory;

    public InventoryManager(ManagerHandler managerHandler) {
        super(managerHandler);
        createInventories();
    }

    private void createInventories() {
        unrankedInventory = this.managerHandler.getPlugin().getServer().createInventory(null, 9, ChatColor.LIGHT_PURPLE + "Select an Unranked Queue");
        rankedInventory = this.managerHandler.getPlugin().getServer().createInventory(null, 9, ChatColor.LIGHT_PURPLE + "Select a Ranked Queue");
        editKitInventory = this.managerHandler.getPlugin().getServer().createInventory(null, 9, ChatColor.LIGHT_PURPLE + "Select a kit to edit");
        editKitOptionsInventory = this.managerHandler.getPlugin().getServer().createInventory(null, 18, ChatColor.LIGHT_PURPLE + "Edit Kit Options");
        duelInventory = this.managerHandler.getPlugin().getServer().createInventory(null, 9, ChatColor.LIGHT_PURPLE + "Select a Kit");
        leaderboardsInventory = this.managerHandler.getPlugin().getServer().createInventory(null, 9, ChatColor.LIGHT_PURPLE + "Leaderboards");
        createKitEditorInventory();
        createEditKitOptionsInventory();
        createDuelInventory();
    }

    public void updateUnrankedInventory() {
        Arrays.stream(Kit.values()).forEach(k -> unrankedInventory.setItem(k.getId(), new ItemBuilder(k.getDisplay().getType(), k.getUnrankedMatch().size(), (byte) 0).setDurability(k.getDisplay().getDurability()).setName(ChatColor.LIGHT_PURPLE + k.getName()).setLore(ChatColor.DARK_PURPLE + "In queue: " + ChatColor.LIGHT_PURPLE + k.getUnrankedQueue().size(), ChatColor.DARK_PURPLE + "In match: " + ChatColor.LIGHT_PURPLE + k.getUnrankedMatch().size()).toItemStack()));
    }

    public void updateRankedInventory() {
        Arrays.stream(Kit.values()).forEach(k -> rankedInventory.setItem(k.getId(), new ItemBuilder(k.getDisplay().getType(), k.getRankedMatch().size(), (byte) 0).setDurability(k.getDisplay().getDurability()).setName(ChatColor.LIGHT_PURPLE + k.getName()).setLore(ChatColor.DARK_PURPLE + "In queue: " + ChatColor.LIGHT_PURPLE + k.getRankedQueue().size(), ChatColor.DARK_PURPLE + "In match: " + ChatColor.LIGHT_PURPLE + k.getRankedMatch().size()).toItemStack()));
    }

    private void createKitEditorInventory() {
        Arrays.stream(Kit.values()).forEach(k -> editKitInventory.setItem(k.getId(), new ItemBuilder(k.getDisplay()).setName(ChatColor.LIGHT_PURPLE + k.getName()).toItemStack()));
    }

    private void createEditKitOptionsInventory() {
        editKitOptionsInventory.setItem(4, new ItemBuilder(Material.INK_SACK, 1, (byte) 10).setName(ChatColor.GREEN + "Save Kit").toItemStack());
        editKitOptionsInventory.setItem(13, new ItemBuilder(Material.FIREBALL).setName(ChatColor.RED + "Delete Kit").toItemStack());
        editKitOptionsInventory.setItem(17, new ItemBuilder(Material.WOOD_DOOR).setName(ChatColor.YELLOW + "Teleport to Spawn").toItemStack());
    }

    private void createDuelInventory() {
        Arrays.stream(Kit.values()).forEach(k -> duelInventory.setItem(k.getId(), new ItemBuilder(k.getDisplay()).setName(ChatColor.LIGHT_PURPLE + k.getName()).toItemStack()));
    }

}
