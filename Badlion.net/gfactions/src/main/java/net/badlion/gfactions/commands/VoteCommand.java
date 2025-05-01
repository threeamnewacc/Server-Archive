package net.badlion.gfactions.commands;

import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoteCommand implements CommandExecutor {

    private GFactions plugin;

    public VoteCommand(GFactions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;

            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                @Override
                public void run() {
                    //int numOfVotes = VoteListener.getNumOfVotesForThisMonth(player.getUniqueId().toString());

                    player.sendMessage("§3=§b=§3=§b=§3=§b= " + ChatColor.YELLOW + ChatColor.BOLD + "Voting Prizes" + ChatColor.RESET + " §3=§b=§3=§b=§3=§b=");
                    player.sendMessage(ChatColor.DARK_AQUA + "Vote on www.badlion.net daily on all 5 sites daily to maximize prizes!");
                    player.sendMessage(ChatColor.DARK_AQUA + "Get money for every vote, 2 diamonds, 1 tnt and a chance for bonus prizes:");
                    player.sendMessage(ChatColor.DARK_AQUA + "1/20 Chance for" + ChatColor.AQUA + " $2,500 Extra");
                    player.sendMessage(ChatColor.DARK_AQUA + "1/50 Chance for" + ChatColor.AQUA + " 64 XP Bottles");
                    player.sendMessage(ChatColor.DARK_AQUA + "1/1000 Chance for" + ChatColor.AQUA + " Protection IV Enchanting Book");
                    player.sendMessage(ChatColor.DARK_AQUA + "1/1000 Chance for" + ChatColor.AQUA + " Sharpness V Enchanting Book");
                    player.sendMessage(ChatColor.DARK_AQUA + "1/2000 Chance for" + ChatColor.AQUA + " 5 extra loot items from voting");
                    player.sendMessage(ChatColor.DARK_AQUA + "1/5000 Chance for" + ChatColor.AQUA + " Protection IV Diamond Set");
                    player.sendMessage(ChatColor.DARK_AQUA + "1/10000 Chance for" + ChatColor.AQUA + " God Loot");
                }
            });
        }

        return true;
    }
}
