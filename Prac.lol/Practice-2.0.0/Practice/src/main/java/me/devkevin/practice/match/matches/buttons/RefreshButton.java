package me.devkevin.practice.match.matches.buttons;

import lombok.AllArgsConstructor;
import me.devkevin.practice.util.ItemBuilder;
import me.devkevin.practice.util.menu.Button;
import me.devkevin.practice.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * Copyright 14/04/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@AllArgsConstructor
public class RefreshButton extends Button {
    private final Menu menu;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.CARPET)
                .name("&aRefresh")
                .lore(Arrays.asList("", "&7Click here to update the fights!"))
                .durability(5)
                .hideFlags()
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        playNeutral(player);
        this.menu.updateInventory(player);
    }
}
