package me.devkevin.practice.general;

import club.inverted.chatcolor.CC;
import lombok.Getter;
import me.devkevin.practice.Practice;
import me.devkevin.practice.util.ItemBuilder;
import me.devkevin.practice.util.inventory.InventoryUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import static me.devkevin.practice.util.PlayerUtil.PLACEHOLDER_ITEM;

/**
 * Copyright 16/12/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
public class GeneralSettingMenu {
    private final Practice plugin = Practice.getInstance();

    private final InventoryUI generalMenu = new InventoryUI(CC.GRAY + "General Setting Menu", 3);

    public GeneralSettingMenu() {
        setupMenu();
    }

    private void setupMenu() {
        for (int i = 0; i < 9 * 3; i++) {
            this.generalMenu.setItem(i, new InventoryUI.EmptyClickableItem(PLACEHOLDER_ITEM));
        }

        generalMenu.setItem(11, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.LEASH)
                .name(CC.YELLOW + "Host Tournament")
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();

                player.performCommand("tournament host");
            }
        });

        generalMenu.setItem(13, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.JUKEBOX)
                .name(CC.YELLOW + "Host Event")
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();

                player.openInventory(plugin.getHostMenu().getHostMenu().getCurrentPage());
            }
        });

        generalMenu.setItem(15, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.CHEST)
                .name(CC.YELLOW + "Settings")
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();

                player.performCommand("poptions");
            }
        });
    }
}
