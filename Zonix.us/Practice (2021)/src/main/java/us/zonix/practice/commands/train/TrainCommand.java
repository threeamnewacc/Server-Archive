package us.zonix.practice.commands.train;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;

public class TrainCommand extends Command {
    private final Practice plugin = Practice.getInstance();

    public TrainCommand() {
        super("train");
        this.setDescription("Train with a bot.");
        this.setUsage(ChatColor.RED + "Usage: /train");
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {
            Player player = (Player)sender;
            if (this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null) {
                player.sendMessage(ChatColor.RED + "You are currently in a tournament.");
                return true;
            } else {
                PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
                if (playerData.getPlayerState() != PlayerState.SPAWN) {
                    player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
                    return true;
                } else {
                    player.openInventory(this.plugin.getInventoryManager().getTrainInventory().getCurrentPage());
                    return true;
                }
            }
        }
    }
}
