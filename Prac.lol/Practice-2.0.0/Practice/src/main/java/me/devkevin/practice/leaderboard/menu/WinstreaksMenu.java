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
 * 10/03/2023 @ 15:54
 * WinstreaksMenu / me.devkevin.practice.leaderboard.menu / Practice
 */
public class WinstreaksMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return "Winstreaks Menu";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new WinstreakGlobalButton(Material.SUGAR, "globalWinStreak"));

        buttons.put(57, new LeaderboardButton());
        buttons.put(58, new PlayerStatsButton());
        buttons.put(59, new WinstreakButton());

        AtomicInteger value = new AtomicInteger(19);
        plugin.getKitManager().getKits().stream().sorted(Comparator.comparingInt(Kit::getPriority)).forEach(kit -> {
            buttons.put(value.getAndIncrement(), new WinstreakKitButton(kit));
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
