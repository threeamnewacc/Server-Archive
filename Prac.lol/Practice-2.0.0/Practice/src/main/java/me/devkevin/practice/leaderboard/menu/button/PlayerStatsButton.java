package me.devkevin.practice.leaderboard.menu.button;

import club.inverted.chatcolor.CC;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.practice.util.ItemBuilder;
import me.devkevin.practice.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 10/03/2023 @ 14:53
 * PlayerStatsButton / me.devkevin.practice.leaderboard.menu.button / Practice
 */
@AllArgsConstructor
public class PlayerStatsButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = Lists.newArrayList();

        lore.add(CC.MENU_BAR);
        lore.add(CC.GRAY + "Click here to open your");
        lore.add(CC.GRAY + "personal stats profile!");
        lore.add(CC.MENU_BAR);

        CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

        return new ItemBuilder(Material.SKULL_ITEM).name(coreProfile.getGrant().getRank().getColor() + player.getName()).owner(player.getName()).lore(lore).durability(3).build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        player.openInventory(plugin.getStatsMenu().getStatsMenu(player).getCurrentPage());
        playNeutral(player);
    }
}
