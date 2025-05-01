package net.badlion.smellycases.commands;

import net.badlion.common.libraries.StringCommon;
import net.badlion.gberry.Gberry;
import net.badlion.smellycases.SmellyCases;
import net.badlion.smellycases.managers.CaseDataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GiveCaseCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 7) { // /givecases <player> <type> <total> <rare> <super_rare> <legendary> <transaction_id>
			Player player = SmellyCases.getInstance().getServer().getPlayerExact(args[0]);

			if (player == null) {
				sender.sendMessage(ChatColor.RED + "Player is not online!");
				return true;
			}

			Gberry.ServerType server;
			try {
				server = Gberry.ServerType.valueOf(args[1]);
			} catch (IllegalArgumentException e) {
				sender.sendMessage(ChatColor.RED + "Invalid case type.");
				return true;
			}

			int cases, rareCases, superRareCases, legendaryCases;
			try {
				cases = Integer.valueOf(args[2]);
				rareCases = Integer.valueOf(args[4]);
				superRareCases = Integer.valueOf(args[5]);
				legendaryCases = Integer.valueOf(args[6]);
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "Invalid number!");
				return true;
			}

			CaseDataManager.giveCases(player.getUniqueId(), server, cases, rareCases, superRareCases, legendaryCases, args[4]);

			// Send message to command sender
			sender.sendMessage(ChatColor.GREEN + "Gave " + cases + " (" + rareCases + ") " + server.getName() + " cases to " + player.getName());
			return true;
		} else if (args.length == 8) { // /givecases uuid <uuid> <type> <total> <rare> <super_rare> <legendary> <transaction_id>
			final UUID uuid = StringCommon.uuidFromStringWithoutDashes(args[1]);
			String transactionID = args[7];

			Gberry.ServerType server;
			try {
				server = Gberry.ServerType.valueOf(args[2]);
			} catch (IllegalArgumentException e) {
				sender.sendMessage(ChatColor.RED + "Invalid case type.");
				return true;
			}

			int cases, rareCases, superRareCases, legendaryCases;
			try {
				cases = Integer.valueOf(args[3]);
				rareCases = Integer.valueOf(args[4]);
				superRareCases = Integer.valueOf(args[5]);
				legendaryCases = Integer.valueOf(args[6]);
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "Invalid number!");
				return true;
			}

			if (args[0].equals("uuid")) {
				CaseDataManager.giveCases(uuid, server, cases, rareCases, superRareCases, legendaryCases, transactionID);
				sender.sendMessage(ChatColor.GREEN + "Gave " + cases + " " + server.getName() + " cases to " + uuid.toString());
			} else if (args[0].equals("remove")) {
				CaseDataManager.removeCases(uuid, server, transactionID);
				sender.sendMessage(ChatColor.GREEN + "Removed " + cases + " " + server.getName() + " cases from " + uuid.toString());
			} else if (args[0].equals("offline")) {
				CaseDataManager.giveCasesOffline(uuid, server, cases, rareCases, superRareCases, legendaryCases, transactionID);
				sender.sendMessage(ChatColor.GREEN + "Gave " + cases + " " + server.getName() + " cases to " + uuid.toString());
			}
			return true;
		}
		return false;
	}
}
