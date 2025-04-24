package me.devkevin.practice.match.history;

import lombok.Getter;
import club.inverted.chatcolor.CC;
import me.devkevin.practice.Practice;
import me.devkevin.practice.match.history.menu.MatchHistoryMenu;
import me.devkevin.practice.util.ItemBuilder;
import me.devkevin.practice.util.ItemUtil;
import me.devkevin.practice.util.inventory.InventoryUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Copyright 04/01/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
public class MatchHistoryInvSnap {

    private InventoryUI winnerInventory;
    private InventoryUI loserInventory;

    public MatchHistoryInvSnap(MatchLocatedData locatedData) {
        winnerInv(locatedData);
        loserInv(locatedData);
    }

    private void winnerInv(MatchLocatedData locatedData) {
        ItemStack[] contents = locatedData.getWinnerContents();
        ItemStack[] armor = locatedData.getWinnerArmor();
        this.winnerInventory = new InventoryUI(Bukkit.getOfflinePlayer(locatedData.getWinnerUUID()).getName() + "'s Inventory", true, 6);

        for (int i = 0; i < 9; i++) {
            this.winnerInventory.setItem(i + 27, new InventoryUI.EmptyClickableItem(contents[i]));
            this.winnerInventory.setItem(i + 18, new InventoryUI.EmptyClickableItem(contents[i + 27]));
            this.winnerInventory.setItem(i + 9, new InventoryUI.EmptyClickableItem(contents[i + 18]));
            this.winnerInventory.setItem(i, new InventoryUI.EmptyClickableItem(contents[i + 9]));
        }

        for (int i = 36; i < 40; i++) {
            this.winnerInventory.setItem(i, new InventoryUI.EmptyClickableItem(armor[39 - i]));
        }

        this.winnerInventory.setItem(45, new InventoryUI.AbstractClickableItem(ItemUtil.reloreItem(ItemUtil.createItem(Material.BED, CC.RED + "Go back"))) {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                Player clicker = (Player) inventoryClickEvent.getWhoClicked();

                List<MatchLocatedData> matchHistory = Practice.getInstance().getMatchLocatedData().getMatchesByUser(clicker.getUniqueId());
                new MatchHistoryMenu(clicker.getUniqueId(), matchHistory).openMenu(clicker);
            }
        });
        this.winnerInventory.setItem(49, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.DIAMOND_SWORD)
                .name(CC.GREEN + "Damage Dealt")
                .lore(CC.GRAY + "Hits: " + CC.R + locatedData.getHitsWinner())
                .lore(CC.GRAY + "Longest Combo: " + CC.R + locatedData.getLongestComboWinner())
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {

            }
        });
        this.winnerInventory.setItem(50, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.POTION).durability(16421)
                .name(CC.GREEN + "Potion dealt")
                .lore(CC.GRAY + "Potions Thrown: " + CC.R + locatedData.getThrownPotsWinner())
                .lore(CC.GRAY + "Missed Pots: " + CC.R + locatedData.getMissedPotsWinner())
                .lore(CC.GRAY + "Potion Accuracy: " + CC.R + locatedData.getPotionAccuracyWinner())
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {

            }
        });

        this.winnerInventory.setItem(53, new InventoryUI.AbstractClickableItem(ItemUtil.reloreItem(ItemUtil.createItem(Material.CHEST, CC.translate("&6View " + Bukkit.getOfflinePlayer(locatedData.getLoserUUID()).getName() +  " inventory")), CC.translate("&a swap player"))) {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                Player clicker = (Player) inventoryClickEvent.getWhoClicked();
                clicker.openInventory(loserInventory.getCurrentPage());
            }
        });
    }

    private void loserInv(MatchLocatedData locatedData) {
        ItemStack[] contents = locatedData.getLoserContents();
        ItemStack[] armor = locatedData.getLoserArmor();
        this.loserInventory = new InventoryUI(Bukkit.getOfflinePlayer(locatedData.getLoserUUID()).getName() + "'s Inventory", true, 6);

        for (int i = 0; i < 9; i++) {
            this.loserInventory.setItem(i + 27, new InventoryUI.EmptyClickableItem(contents[i]));
            this.loserInventory.setItem(i + 18, new InventoryUI.EmptyClickableItem(contents[i + 27]));
            this.loserInventory.setItem(i + 9, new InventoryUI.EmptyClickableItem(contents[i + 18]));
            this.loserInventory.setItem(i, new InventoryUI.EmptyClickableItem(contents[i + 9]));
        }
        for (int i = 36; i < 40; i++) {
            this.loserInventory.setItem(i, new InventoryUI.EmptyClickableItem(armor[39 - i]));
        }

        this.loserInventory.setItem(45, new InventoryUI.AbstractClickableItem(ItemUtil.reloreItem(ItemUtil.createItem(Material.BED, CC.RED + "Go back"))) {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                Player clicker = (Player) inventoryClickEvent.getWhoClicked();

                List<MatchLocatedData> matchHistory = Practice.getInstance().getMatchLocatedData().getMatchesByUser(clicker.getUniqueId());
                new MatchHistoryMenu(clicker.getUniqueId(), matchHistory).openMenu(clicker);
            }
        });
        this.loserInventory.setItem(49, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.DIAMOND_SWORD)
                .name(CC.GREEN + "Damage Dealt")
                .lore(CC.GRAY + "Hits: " + CC.R + locatedData.getHitsLoser())
                .lore(CC.GRAY + "Longest Combo: " + CC.R + locatedData.getLongestComboLoser())
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {

            }
        });
        this.loserInventory.setItem(50, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.POTION).durability(16421)
                .name(CC.GREEN + "Potion dealt")
                .lore(CC.GRAY + "Potions Thrown: " + CC.R + locatedData.getThrownPotsLoser())
                .lore(CC.GRAY + "Missed Pots: " + CC.R + locatedData.getMissedPotsLoser())
                .lore(CC.GRAY + "Potion Accuracy: " + CC.R + locatedData.getPotionAccuracyLoser())
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {

            }
        });
        this.loserInventory.setItem(53, new InventoryUI.AbstractClickableItem(ItemUtil.reloreItem(ItemUtil.createItem(Material.CHEST, CC.translate("&6View " + Bukkit.getOfflinePlayer(locatedData.getWinnerUUID()).getName() +  " inventory")), CC.translate("&a swap player"))) {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                Player clicker = (Player) inventoryClickEvent.getWhoClicked();
                clicker.openInventory(winnerInventory.getCurrentPage());
            }
        });
    }
}
