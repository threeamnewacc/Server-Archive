package me.devkevin.practice.arena.menu.buttons;

import lombok.AllArgsConstructor;
import club.inverted.chatcolor.CC;
import me.devkevin.practice.Practice;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.arena.menu.ArenaCopyMenu;
import me.devkevin.practice.arena.menu.ArenaGenerationMenu;
import me.devkevin.practice.util.ItemBuilder;
import me.devkevin.practice.util.menu.Button;
import org.bukkit.Bukkit;
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
public class ArenaButton extends Button {

    private final Arena arena;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.valueOf(arena.getIcon()))
                .durability(arena.getIconData())
                .name("&6" + arena.getName())
                .lore(CC.translate(
                        Arrays.asList(
                                " ",
                                "&6Arena Information&f:",
                                " &fState: " + (arena.isEnabled() ? "&aEnabled" : "&cDisabled"),
                                " &fType: &6" + (arena.getAvailableArenas().size() == 0 ? "Shared" : "Standalone"),
                                " &fCopies: &6" + (arena.getStandaloneArenas().size() == 0 ? "Not Required." : arena.getStandaloneArenas().size()),
                                " &fAvailable: &6" + (arena.getAvailableArenas().size() == 0 ? +1 : arena.getAvailableArenas().size()),
                                " ",
                                (arena.getStandaloneArenas().size() == 0 ? "&4&l&mMIDDLE-CLICK &4&mto see arena copies" : "&6&lMIDDLE-CLICK &6to see arena copies"),
                                "&e&lLEFT-CLICK &eto teleport to arena",
                                "&6&lRIGHT CLICK &6to generate standalone arenas")
                        )
                )
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        playSuccess(player);
        switch (clickType) {
            case LEFT:
                player.teleport(arena.getA().toBukkitLocation());
                break;
            case RIGHT:
                Bukkit.getScheduler().runTaskLaterAsynchronously(Practice.getInstance(), () -> new ArenaGenerationMenu(arena).openMenu(player), 1L);
                break;
            case MIDDLE:
                if (arena.getStandaloneArenas().size() >= 1) {
                    Bukkit.getScheduler().runTaskLaterAsynchronously(Practice.getInstance(), () -> new ArenaCopyMenu(arena).openMenu(player), 1L);
                }
                break;
        }

        player.closeInventory();
    }
}

