package net.badlion.gfactions.commands;

import net.badlion.gfactions.GFactions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class SlotCommand implements CommandExecutor {

	private GFactions plugin;
    public static Map<UUID, List<String>> slotMap = new HashMap<>();

	public SlotCommand(GFactions plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length < 1) {
            return false;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Shit logic, too tired to care
            List<String> allowedPlayers = slotMap.get(player.getUniqueId());
            if (allowedPlayers != null) {
                if (allowedPlayers.contains(args[0].toLowerCase()) && Bukkit.getServer().getPlayer(args[0]) == null) {
                    allowedPlayers.remove(args[0].toLowerCase());
                    player.sendMessage(ChatColor.GREEN + "Removed " + args[0] + " from reserved slots.");
                } else if (allowedPlayers.contains(args[0].toLowerCase()) && Bukkit.getServer().getPlayer(args[0]) != null) {
                    player.sendMessage(ChatColor.RED + "Cannot remove player when they are online.");
                } else {
                    if (allowedPlayers.size() < 2) {
                        allowedPlayers.add(args[0].toLowerCase());
                        player.sendMessage(ChatColor.GREEN + "Added " + args[0] + " to your reserved slots.");
                    } else {
                        player.sendMessage(ChatColor.RED + "You cannot add another player to your reserved slots before removing another.");
                    }
                }
            } else {
                allowedPlayers = new ArrayList<>();
                slotMap.put(player.getUniqueId(), allowedPlayers);
                allowedPlayers.add(args[0].toLowerCase());
                player.sendMessage(ChatColor.GREEN + "Added " + args[0] + " to your reserved slots.");
            }
        }

		return true;
	}

}
