package me.devkevin.practice.match.history.menu.button;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import club.inverted.chatcolor.CC;
import me.devkevin.practice.match.history.MatchLocatedData;
import me.devkevin.practice.util.ItemBuilder;
import me.devkevin.practice.util.menu.Button;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Copyright 03/01/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@AllArgsConstructor
public class MatchHistoryButton extends Button {

    private final MatchLocatedData locatedData;

    @Override
    public ItemStack getButtonItem(Player player) {
        OfflinePlayer winner = Bukkit.getOfflinePlayer(locatedData.getWinnerUUID());
        OfflinePlayer loser = Bukkit.getOfflinePlayer(locatedData.getLoserUUID());

        List<String> lore = Lists.newArrayList();

        lore.add(CC.translate("&8" + locatedData.getDate()));
        lore.add(CC.STRIKETHROUGH);
        lore.add(CC.translate("&7Kit: &6" + locatedData.getKit()));
        lore.add(CC.translate("&7Duration: " + CC.R + locatedData.getMatchDuration()));
        lore.add(CC.translate("&7Arena: " + CC.R + locatedData.getArenaName()));
        lore.add(CC.STRIKETHROUGH);
        lore.add(CC.translate("&7Elo changes:"));
        lore.add(CC.translate("&8 • &fWinner Elo: &a " + locatedData.getWinnerElo() + " (+" + locatedData.getWinnerEloModifier() + ")"));
        lore.add(CC.translate("&8 • &fLoser Elo: &a " + locatedData.getLoserElo() + " (" + locatedData.getLoserEloModifier() + ")"));
        lore.add(CC.STRIKETHROUGH);
        lore.add(CC.translate("&aClick to view!"));


        return new ItemBuilder(Material.SKULL_ITEM)
                .name(CC.GREEN + winner.getName() + CC.R + " vs. " + CC.RED + loser.getName())
                .owner(winner.getName())
                .durability(3)
                .lore(CC.translate(lore))
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        playNeutral(player);
        player.openInventory(locatedData.getMatchHistoryInvSnap().getWinnerInventory().getCurrentPage());
    }
}

