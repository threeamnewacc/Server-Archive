package me.devkevin.practice.profile.menu;

import club.inverted.chatcolor.CC;
import lombok.Getter;
import me.devkevin.landcore.LandCore;
import me.devkevin.practice.Practice;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.util.ItemBuilder;
import me.devkevin.practice.util.inventory.InventoryUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 08/02/2023 @ 3:20
 * StatsMenu / me.devkevin.practice.elo.menu / Practice
 */
@Getter
public class StatsMenu {
    private final Practice plugin = Practice.getInstance();

    public InventoryUI getStatsMenu(Player target) {
        InventoryUI menu = new InventoryUI(LandCore.getInstance().getProfileManager().getProfile(target.getUniqueId()).getGrant().getRank().getColor() + target.getName() + CC.YELLOW + "'s stats", 2);

        Profile profile = getPlugin().getProfileManager().getProfileData(target.getUniqueId());

        for (Kit kit : getPlugin().getKitManager().getKits()) {
            List<String> lore = new ArrayList<>();
            String kitName = kit.getName();

            final double wins = profile.getGlobalWins();
            final double losses = profile.getGlobalLosses();
            final double wlr = wins == 0 || losses == 0 ? 0.0 : wins / losses;

            lore.add(CC.YELLOW + "Elo: " + CC.LIGHT_PURPLE + profile.getElo(kitName));
            lore.add(CC.YELLOW + "Wins: " + CC.LIGHT_PURPLE + profile.getWins(kitName));
            lore.add(CC.YELLOW + "Losses: " + CC.LIGHT_PURPLE + profile.getLosses(kitName));
            lore.add(CC.YELLOW + "W/L Ratio: " + CC.LIGHT_PURPLE + wlr);

            menu.addItem(new InventoryUI.AbstractClickableItem(new ItemBuilder(kit.getIcon().getType()).name(CC.LIGHT_PURPLE + kit.getName())
                    .durability(kit.getIcon().getDurability())
                    .lore(lore).build()) {
                @Override
                public void onClick(InventoryClickEvent event) {

                }
            });
        }

        return menu;
    }
}
