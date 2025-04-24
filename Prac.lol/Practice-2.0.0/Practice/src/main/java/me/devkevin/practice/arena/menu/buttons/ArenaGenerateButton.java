package me.devkevin.practice.arena.menu.buttons;

import lombok.AllArgsConstructor;
import club.inverted.chatcolor.CC;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.util.ItemBuilder;
import me.devkevin.practice.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * Copyright 31/12/2021 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@AllArgsConstructor
public class ArenaGenerateButton extends Button {

    private final Arena arena;
    private final int currentCopyAmount;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.PAPER)
                .name("&aCreate " + currentCopyAmount + " Arena Copies")
                .lore(CC.translate(
                        Arrays.asList(
                                " ",
                                "&fClicking here will generate &6&l" + currentCopyAmount,
                                "&farenas for the map &6" + arena.getName() + "&f!",
                                " ",
                                "&a&lLEFT-CLICK &ato generate arenas")
                ))
                .amount(currentCopyAmount)
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        player.performCommand("arena generate " + arena.getName() + " " + currentCopyAmount);

        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&6&lGENERATING ARENAS&6..."));
        player.sendMessage(CC.translate(" "));
        player.sendMessage(CC.translate("&6&luDrop Practice &fis currently generating copies for:"));
        player.sendMessage(CC.translate(" &6&l▸ &fArena: &6" + arena.getName()));
        player.sendMessage(CC.translate(" &6&l▸ &fCopies: &6" + currentCopyAmount));
        player.sendMessage(CC.translate(" "));
        player.sendMessage(CC.translate("&f&oYou can check the progress in console."));
        player.sendMessage(CC.CHAT_BAR);

        player.closeInventory();
    }
}

