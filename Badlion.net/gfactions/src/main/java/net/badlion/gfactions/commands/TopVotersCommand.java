package net.badlion.gfactions.commands;

import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.listeners.VoteListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class TopVotersCommand implements CommandExecutor {

    private GFactions plugin;

    public TopVotersCommand(GFactions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;

            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                @Override
                public void run() {
                    Map<String, Integer> topVoters = VoteListener.getTopVotersOfMonth();

                    player.sendMessage(ChatColor.RED + "--------------Top Voters of the Month-------------");
					if (topVoters == null) {
						player.sendMessage(ChatColor.GOLD + "No voters this month.");
					} else {
						// Go and showcase each person who voted
						for (Map.Entry<String, Integer> entry : topVoters.entrySet()) {
							player.sendMessage(ChatColor.GOLD + entry.getKey() + ChatColor.GREEN + " has voted " + ChatColor.AQUA + entry.getValue() + ChatColor.GREEN + " times this month.");
						}
					}
                    player.sendMessage(ChatColor.RED + "--------------------------------------------------");
                }
            });
        }

        return true;
    }
}
