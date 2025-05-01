package net.badlion.gfactions.commands;

import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class VanishCommand implements CommandExecutor {

	//public static HashSet<Player> vanishedPlayers = new HashSet<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.spigot().getCollidesWithEntities()) {
                player.sendMessage(ChatColor.GREEN + "Disabled Vanish");
                player.spigot().setCollidesWithEntities(true);
                player.setGameMode(GameMode.SURVIVAL);

                // Show players
                for (Player pl : GFactions.plugin.getServer().getOnlinePlayers()) {
                    pl.showPlayer(player);
                }
            } else {
                player.sendMessage(ChatColor.GREEN + "Enabled Vanish");
                player.spigot().setCollidesWithEntities(false);
                player.setGameMode(GameMode.CREATIVE);

                // Hide players
                for (Player pl : GFactions.plugin.getServer().getOnlinePlayers()) {
                    pl.hidePlayer(player);

                    if (!pl.spigot().getCollidesWithEntities()) {
                        player.hidePlayer(pl);
                    }
                }
            }
        }
		return true;
	}
}
