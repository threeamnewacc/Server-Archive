package me.devkevin.practice.leaderboard.menu.button;

import club.inverted.chatcolor.CC;
import lombok.AllArgsConstructor;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.leaderboard.Leaderboard;
import me.devkevin.practice.util.ItemBuilder;
import me.devkevin.practice.util.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 10/03/2023 @ 15:56
 * WinstreakKitButton / me.devkevin.practice.leaderboard.menu.button / Practice
 */
@AllArgsConstructor
public class WinstreakKitButton extends Button {

    private Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {

        List<String> lore = new ArrayList<>();
        AtomicInteger lineNum = new AtomicInteger();

        lore.add(CC.MENU_BAR);

        List<Leaderboard> winStreaks = new ArrayList<>(plugin.getLeaderboardManager().getSortedKitLeaderboards(kit, "winstreak"));
        for (Leaderboard winStreak : winStreaks) {
            lineNum.getAndIncrement();

            lore.add(CC.YELLOW + "#" + lineNum + " " + CC.GOLD + winStreak.getName() + CC.GRAY + " (" + winStreak.getWinStreak() + ")");
        }

        lore.add(CC.MENU_BAR);

        return new ItemBuilder(kit.getIcon().getType()).name(CC.translate(CC.GOLD + kit.getName() + CC.GRAY + " (Top 10)")).lore(lore).durability(kit.getIcon().getDurability()).hideFlags().build();
    }
}
