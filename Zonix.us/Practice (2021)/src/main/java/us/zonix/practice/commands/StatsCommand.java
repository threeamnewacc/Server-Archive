package us.zonix.practice.commands;

import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.kit.Kit;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.util.StringUtil;

public class StatsCommand extends Command {
    private final Practice plugin = Practice.getInstance();

    public StatsCommand() {
        super("stats");
        this.setAliases(Arrays.asList("elo", "statistics"));
        this.setUsage(ChatColor.RED + "Usage: /stats [player]");
    }

    public boolean execute(CommandSender sender, String s, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(((Player)sender).getUniqueId());
                sender.sendMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + sender.getName() + "'s Statistics");
                sender.sendMessage(
                    ChatColor.RED
                        + "Global"
                        + ChatColor.GRAY
                        + ": "
                        + ChatColor.YELLOW
                        + playerData.getGlobalStats("ELO")
                        + " ELO "
                        + ChatColor.GRAY
                        + "┃ "
                        + ChatColor.GREEN
                        + playerData.getGlobalStats("WINS")
                        + " Wins "
                        + ChatColor.GRAY
                        + "┃ "
                        + ChatColor.GOLD
                        + playerData.getGlobalStats("LOSSES")
                        + " Losses"
                );

                for (Kit kit : this.plugin.getKitManager().getKits()) {
                    sender.sendMessage(
                        ChatColor.RED
                            + kit.getName()
                            + ChatColor.GRAY
                            + ": "
                            + ChatColor.YELLOW
                            + playerData.getElo(kit.getName())
                            + " ELO "
                            + ChatColor.GRAY
                            + "┃ "
                            + ChatColor.GREEN
                            + playerData.getWins(kit.getName())
                            + " Wins "
                            + ChatColor.GRAY
                            + "┃ "
                            + ChatColor.GOLD
                            + playerData.getLosses(kit.getName())
                            + " Losses"
                    );
                }
            }

            sender.sendMessage(ChatColor.RED + "Check out other players using /stats [player]");
            return true;
        } else {
            Player target = this.plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
                return true;
            } else {
                PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());
                sender.sendMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + target.getName() + "'s Statistics");
                sender.sendMessage(
                    ChatColor.RED
                        + "Global"
                        + ChatColor.GRAY
                        + ": "
                        + ChatColor.YELLOW
                        + playerData.getGlobalStats("ELO")
                        + " ELO "
                        + ChatColor.GRAY
                        + "┃ "
                        + ChatColor.GREEN
                        + playerData.getGlobalStats("WINS")
                        + " Wins "
                        + ChatColor.GRAY
                        + "┃ "
                        + ChatColor.GOLD
                        + playerData.getGlobalStats("LOSSES")
                        + " Losses"
                );

                for (Kit kit : this.plugin.getKitManager().getKits()) {
                    sender.sendMessage(
                        ChatColor.RED
                            + kit.getName()
                            + ChatColor.GRAY
                            + ": "
                            + ChatColor.YELLOW
                            + playerData.getElo(kit.getName())
                            + " ELO "
                            + ChatColor.GRAY
                            + "┃ "
                            + ChatColor.GREEN
                            + playerData.getWins(kit.getName())
                            + " Wins "
                            + ChatColor.GRAY
                            + "┃ "
                            + ChatColor.GOLD
                            + playerData.getLosses(kit.getName())
                            + " Losses"
                    );
                }

                return true;
            }
        }
    }
}
