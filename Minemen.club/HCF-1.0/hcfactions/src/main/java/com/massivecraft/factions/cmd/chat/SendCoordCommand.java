package com.massivecraft.factions.cmd.chat;

import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Role;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SendCoordCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Player only command noob.");
			return false;
		}

		Player player = (Player) sender;
		FactionPlayer factionPlayer = FPlayers.getInstance().get(player);

		String locationString = player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ();
		if (factionPlayer.getFaction() == null || factionPlayer.getFaction().isNone() || !factionPlayer.getFaction().isNormal()) {
			factionPlayer.msg(ChatColor.GREEN + "You are at: " + locationString);
			return false;
		}

		String fullFactionMessage = String.format("%s(%sFC%s) %s: %s", ChatColor.YELLOW, ChatColor.DARK_GREEN,
				ChatColor.YELLOW, ChatColor.DARK_GREEN + factionChatRank(player) + player.getName(), ChatColor.GREEN + "I am at: " + locationString);

		// Send the message
		factionPlayer.getFaction().getOnlinePlayers().forEach(meme -> meme.sendMessage(fullFactionMessage));

		return false;
	}

	private String factionChatRank(Player player) {
		FactionPlayer fplayer = FPlayers.getInstance().get(player);
		if (fplayer.getRole() == Role.ADMIN) {
			return "**";
		} else if (fplayer.getRole() == Role.MODERATOR) {
			return "*";
		} else {
			return "";
		}
	}

}
