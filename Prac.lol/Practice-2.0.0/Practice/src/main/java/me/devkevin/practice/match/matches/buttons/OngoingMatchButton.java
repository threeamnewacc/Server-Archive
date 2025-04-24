package me.devkevin.practice.match.matches.buttons;

import club.inverted.chatcolor.CC;
import lombok.AllArgsConstructor;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.util.ItemBuilder;
import me.devkevin.practice.util.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 14/04/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@AllArgsConstructor
public class OngoingMatchButton extends Button {
    private final Match match;

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = new ArrayList<>();

        lore.add(CC.MENU_BAR);
        lore.add("&a" + this.match.getType().getName());
        lore.add("");
        lore.add("&eKit -> &a" + this.match.getKit().getName());
        lore.add("&eArena -> &a" + this.match.getArena().getName());
        lore.add("&eDuration -> &a" + this.match.getDuration());
        lore.add("&eSpectators -> &a" + this.match.getSpectators().size());
        lore.add("");
        lore.add("&a» Click to spectate «");
        lore.add(CC.MENU_BAR);

        return new ItemBuilder(this.match.getKit().getIcon())
                .name("&a" + this.match.getTeams().get(0).getLeaderName() + " &7vs " + "&c" + this.match.getTeams().get(1).getLeaderName())
                .lore(lore)
                .hideFlags()
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        player.performCommand("spec " + this.match.getTeams().get(0).getLeaderName());
    }
}
