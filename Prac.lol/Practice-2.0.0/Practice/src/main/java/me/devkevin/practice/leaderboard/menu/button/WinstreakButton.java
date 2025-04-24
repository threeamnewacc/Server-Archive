package me.devkevin.practice.leaderboard.menu.button;

import club.inverted.chatcolor.CC;
import com.google.common.collect.Lists;
import me.devkevin.practice.leaderboard.menu.WinstreaksMenu;
import me.devkevin.practice.util.ItemBuilder;
import me.devkevin.practice.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 10/03/2023 @ 15:52
 * WinstreakButton / me.devkevin.practice.leaderboard.menu.button / Practice
 */
public class WinstreakButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = Lists.newArrayList();

        lore.add(CC.MENU_BAR);
        lore.add(CC.GRAY + "Click here to open the");
        lore.add(CC.GRAY + "winstreak leaderboards!");
        lore.add(CC.MENU_BAR);

        return new ItemBuilder(Material.CARPET)
                .name(CC.GOLD + "Winstreak Leaderboards")
                .lore(lore)
                .durability(14)
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        new WinstreaksMenu().openMenu(player);
        playNeutral(player);
    }
}
