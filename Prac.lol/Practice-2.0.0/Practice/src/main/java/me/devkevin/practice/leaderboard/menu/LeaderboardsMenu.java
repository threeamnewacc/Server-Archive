package me.devkevin.practice.leaderboard.menu;

import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.leaderboard.menu.button.*;
import me.devkevin.practice.util.menu.Button;
import me.devkevin.practice.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 10/03/2023 @ 14:46
 * LeaderboardsMenu / me.devkevin.practice.leaderboard.menu / Practice
 */
public class LeaderboardsMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return "Leaderboards Menu";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new LeaderboardGlobalButton(Material.SUGAR, "globalElo", "GLOBAL"));

        buttons.put(57, new LeaderboardButton());
        buttons.put(58, new PlayerStatsButton());
        buttons.put(59, new WinstreakButton());

        AtomicInteger value = new AtomicInteger(19);
        plugin.getKitManager().getKits().stream().filter(Kit::isRanked).sorted(Comparator.comparingInt(Kit::getPriority)).forEach(kit -> {
            buttons.put(value.getAndIncrement(), new KitStatsButton(kit));
            if (value.get() == 26) {
                value.set(28);
            }
            if (value.get() == 35) {
                value.set(37);
            }
        });

        return buttons;
    }

    @Override
    public int getSize() {
        return 9 * 7;
    }
}
