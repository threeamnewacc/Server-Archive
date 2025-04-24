package me.devkevin.practice.command.general;

import club.inverted.chatcolor.CC;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 28/01/2023 @ 14:51
 * StatsCommand / me.devkevin.practice.command.general / Practice
 */
public class StatsCommand extends PracticeCommand {
    @Override @Command(name = "stats", aliases = {"elo"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.openInventory(getPlugin().getStatsMenu().getStatsMenu(player).getCurrentPage());
            return;
        }

        Player target = getPlugin().getServer().getPlayer(args[0]);

        if (target == null || !target.isOnline()) {
            assert target != null;
            player.sendMessage(CC.RED + "No player matching " + CC.YELLOW + target.getName() + CC.RED + " is connected to this server");
            return;
        }

        player.openInventory(getPlugin().getStatsMenu().getStatsMenu(target).getCurrentPage());
    }
}
