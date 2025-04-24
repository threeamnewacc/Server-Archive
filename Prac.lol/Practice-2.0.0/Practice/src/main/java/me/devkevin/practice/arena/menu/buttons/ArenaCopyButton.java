package me.devkevin.practice.arena.menu.buttons;

import lombok.AllArgsConstructor;
import club.inverted.chatcolor.CC;
import me.devkevin.practice.Practice;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.arena.generate.ArenaCopyRemovalRunnable;
import me.devkevin.practice.arena.standalone.StandaloneArena;
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
public class ArenaCopyButton extends Button {

    private final Practice plugin = Practice.getInstance();

    private final int number;
    private final Arena arena;
    private final StandaloneArena arenaCopy;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.PAPER)
                .name("&8" + number)
                .lore(CC.translate(
                        Arrays.asList(
                                "&6Copy Information&f:",
                                " &fParent Arena: &6" + arena.getName() + " &f(&f#" + number + "&f)",
                                " &f1st Spawn: &6" + Math.round(arenaCopy.getA().getX()) + "&f, &6" + Math.round(arenaCopy.getA().getY()) + "&f, &6" + Math.round(arenaCopy.getA().getZ()),
                                " &f2nd Spawn: &6" + Math.round(arenaCopy.getB().getX()) + "&f, &6" + Math.round(arenaCopy.getB().getY()) + "&f, &6" + Math.round(arenaCopy.getB().getZ()),
                                " &fMin Location: &6" + Math.round(arenaCopy.getMin().getX()) + "&f, &6" + Math.round(arenaCopy.getMin().getY()) + "&f, &6" + Math.round(arenaCopy.getMin().getZ()),
                                " &fMax Location: &6" + Math.round(arenaCopy.getMax().getX()) + "&f, &6" + Math.round(arenaCopy.getMax().getY()) + "&f, &6" + Math.round(arenaCopy.getMax().getZ()),
                                " ",
                                "&e&lLEFT-CLICK &eto teleport to this copy!",
                                "&c&lRIGHT-CLICK &cto delete this copy!"
                        ))
                )
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        switch (clickType) {
            case LEFT:
                player.teleport(arenaCopy.getA().toBukkitLocation());
                break;
            case RIGHT:
                new ArenaCopyRemovalRunnable(number, arena, arenaCopy).runTask(this.plugin);
                break;
        }

        player.closeInventory();
    }
}

