package net.badlion.mpg.commands;

import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.MPGTeam;
import net.badlion.mpg.bukkitevents.teams.TeamListEvent;
import net.badlion.mpg.commands.teams.AcceptCommandHandler;
import net.badlion.mpg.commands.teams.DenyCommandHandler;
import net.badlion.mpg.commands.teams.InviteCommandHandler;
import net.badlion.mpg.commands.teams.KickCommandHandler;
import net.badlion.mpg.commands.teams.LeaveCommandHandler;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpg.managers.MPGTeamManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You can only use this command in-game");
			return true;
		}

		if (!MPG.getInstance().getBooleanOption(MPG.ConfigFlag.TEAM_MANAGEMENT)) {
			sender.sendMessage(ChatColor.RED + "Team management is not allowed in this game!");
			return true;
		}

		Player player = (Player) sender;
		MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer((player).getUniqueId());
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("leave")) {
				LeaveCommandHandler.handleLeaveCommand(player);
			} else if (args[0].equalsIgnoreCase("invite")) {
				InviteCommandHandler.handleInviteCommand(player, args);
			} else if (args[0].equalsIgnoreCase("kick")) {
				KickCommandHandler.handleKickCommand(player, args);
			} else if (args[0].equalsIgnoreCase("accept")) {
				AcceptCommandHandler.handleAcceptCommand(player);
			} else if (args[0].equalsIgnoreCase("deny")) {
				DenyCommandHandler.handleDenyCommand(player);
			} else if (args[0].equalsIgnoreCase("list")) {
                TeamListEvent teamListEvent = new TeamListEvent(mpgPlayer);
                MPG.getInstance().getServer().getPluginManager().callEvent(teamListEvent);
                if (teamListEvent.isCancelled()) {
                    return true;
                }

				if (args.length == 2) {
					UUID uuid = MPG.getInstance().getUUID(args[1]);
					if (uuid == null) {
						sender.sendMessage(ChatColor.RED + "Player not found.");
						return true;
					}

					MPGPlayer mpgPlayer2 = MPGPlayerManager.getMPGPlayer(uuid);
					if (mpgPlayer2 == null) {
						sender.sendMessage(ChatColor.RED + "Player not found.");
						return true;
					}

					MPGTeam team = mpgPlayer2.getTeam();

					StringBuilder sb = new StringBuilder();
					sb.append(ChatColor.YELLOW);
					sb.append("Players: ");

					for (UUID uuid2 : team.getUUIDs()) {
						Player pl = MPG.getInstance().getServer().getPlayer(uuid2);
						if (pl != null) {
							sb.append(ChatColor.GREEN);
							sb.append(pl.getDisguisedName());
						} else {
							sb.append(ChatColor.RED);
							sb.append(MPG.getInstance().getUsername(uuid2));
						}

						sb.append(", ");
					}

					// Cut off end
					sb.setLength(sb.length() - 2);

					sender.sendMessage(sb.toString());
				} else {
					StringBuilder sb = new StringBuilder();

					sb.append(ChatColor.YELLOW);
					sb.append("You have the following teammates: ");

					for (UUID uuid : mpgPlayer.getTeam().getUUIDs()) {
						String username = MPG.getInstance().getUsername(uuid);
						sb.append(username);
						sb.append(", ");
					}

					// Cut off end
					sb.setLength(sb.length() - 2);

					sender.sendMessage(sb.toString());
				}
			} else if (args[0].equals("force") && sender.isOp()) {
				if (args.length != 3) {
					sender.sendMessage(ChatColor.RED + "Invalid usage of /team force");
				} else {
					Player pf = MPG.getInstance().getServer().getPlayer(args[1]);
					Player pt = MPG.getInstance().getServer().getPlayer(args[2]);

					if (pf == null || pt == null) {
						sender.sendMessage(ChatColor.RED + "One of the players is not online.");
						return true;
					}

					MPGPlayer from = MPGPlayerManager.getMPGPlayer(pf.getUniqueId());
					MPGPlayer to = MPGPlayerManager.getMPGPlayer(pt.getUniqueId());

					if (from.getTeam().getTeamSize() == 1) {
						MPGTeamManager.removeTeam(from.getTeam());
					}

					from.setInvitedTeam(null);
					from.setTeam(to.getTeam());
					to.getTeam().add(from);
					sender.sendMessage(ChatColor.GREEN + "Forced " + args[1] + " to team " + args[2]);
				}
			} else {
				this.teamHelpMenu(sender);
			}
		} else {
			this.teamHelpMenu(sender);
		}
		return true;
	}

	private void teamHelpMenu(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "Custom teams are enabled and you can use the following commands.");
		sender.sendMessage(ChatColor.YELLOW + "/team invite [name] - Invite a member to your team.");
		sender.sendMessage(ChatColor.YELLOW + "/team accept - Accept an invite from a team leader.");
		sender.sendMessage(ChatColor.YELLOW + "/team deny - Deny an invite from a team leader.");
		sender.sendMessage(ChatColor.YELLOW + "/team kick [name] - (Leader Only) Kick a member from your team.");
		sender.sendMessage(ChatColor.YELLOW + "/team leave - Leave a team.");
		sender.sendMessage(ChatColor.YELLOW + "/team list - List your teammates");
		sender.sendMessage(ChatColor.YELLOW + "/team list <player> - List another player's teammates");
	}

}
