package us.zonix.practice.commands.management;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.kit.Kit;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.util.StringUtil;

public class ResetStatsCommand extends Command {
    private final Practice plugin = Practice.getInstance();

    public ResetStatsCommand() {
        super("reset");
        this.setUsage(ChatColor.RED + "Usage: /reset [player]");
    }

    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player)commandSender;
            if (!player.isOp()) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
                return true;
            }
        }

        if (args.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "Usage: /reset <player>");
            return true;
        } else {
            Player target = this.plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                commandSender.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
                return true;
            } else {
                PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());

                for (Kit kit : this.plugin.getKitManager().getKits()) {
                    playerData.setElo(kit.getName(), 1000);
                    playerData.setLosses(kit.getName(), 0);
                    playerData.setWins(kit.getName(), 0);
                }

                commandSender.sendMessage(ChatColor.GREEN + target.getName() + "'s stats have been wiped.");
                return true;
            }
        }
    }
}
