package net.badlion.tournament.commands;

import net.badlion.clans.managers.ClanManager;
import net.badlion.clans.managers.ClanManager.Clan;
import net.badlion.clans.managers.ClanManager.ClanMember;
import net.badlion.clans.managers.ClanManager.CLAN_RANK;
import net.badlion.tournament.TournamentPlugin;
import net.badlion.tournament.TournamentStateMachine;
import net.badlion.tournament.teams.DefaultTeam;
import net.badlion.tournament.teams.Team;
import net.badlion.tournament.tournaments.Tournament;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TeamCommand implements CommandExecutor {

    private HashMap<UUID, List<Team>> invitations = new HashMap<>();
    private List<Team> teams = new ArrayList<>();

    public boolean onCommand(final CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("You can only use this command in-game");
            return true;
        }

        if (!TournamentPlugin.getInstance().isTeamsEnabled()) {
            sender.sendMessage(ChatColor.RED + "This command has been disabled");
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("create")) { //tournament start
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();

            if (TournamentStateMachine.getTeam(uuid) != null) {
                sender.sendMessage(ChatColor.RED + "You are already on a team! Leave it with /team leave <name>");
                return true;
            }

            String name = args.length >= 2 ? args[1] : sender.getName();
            DefaultTeam team = new DefaultTeam(name, uuid);
            TournamentStateMachine.storeTeam(uuid, team);
            sender.sendMessage(ChatColor.GREEN + "Team " + team.getName() + " has been created!");
        } else if (args.length >= 2 && args[0].equalsIgnoreCase("invite")) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();

            if (!TournamentPlugin.getInstance().getInvitationType().equalsIgnoreCase("all") &&
                    !TournamentPlugin.getInstance().getInvitationType().equalsIgnoreCase("regular")) {
                sender.sendMessage(ChatColor.RED + "Regular invitations are disabled!");
                return true;
            }

            if (TournamentStateMachine.getTeam(uuid) == null) {
                sender.sendMessage(ChatColor.RED + "You aren't on a team!");
                return true;
            }

            if (!TournamentStateMachine.getTeam(uuid).getRole(uuid).equalsIgnoreCase("leader")) {
                sender.sendMessage(ChatColor.RED + "You aren't the leader of your team!");
                return true;
            }

            if (TournamentStateMachine.getTeam(uuid).getUUIDs().size() >= TournamentPlugin.getInstance().getMaxTeamSize()) {
                sender.sendMessage(ChatColor.RED + "Your team has too many members!");
                return true;
            }

            if (Bukkit.getPlayer(args[1]) == null) {
                sender.sendMessage(ChatColor.RED + args[1] + " isn't online!");
                return true;
            }

            Player targetPlayer;

            if (args[1].contains("-") && args[1].length() > 16) {
                targetPlayer = Bukkit.getPlayer(UUID.fromString(args[1]));
            } else {
                targetPlayer = Bukkit.getPlayer(args[1]);
            }

            UUID targetUUID = player.getUniqueId();

            if (TournamentStateMachine.getTeam(targetUUID) != null) {
                sender.sendMessage(ChatColor.RED + args[1] + " is already on a team!");
                return true;
            }

            player.sendMessage(ChatColor.AQUA + "You have invited " + targetPlayer.getName() + " to team " + TournamentStateMachine.getTeam(uuid).getName());
            targetPlayer.sendMessage(ChatColor.AQUA + "You have been invited to team " + TournamentStateMachine.getTeam(uuid).getName() + ". Accept with /team join " + TournamentStateMachine.getTeam(uuid).getName());
        } else if (args.length >= 2 && args[0].equalsIgnoreCase("remove")) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();

            if (TournamentStateMachine.getTeam(uuid) == null) {
                sender.sendMessage(ChatColor.RED + "You aren't on a team!");
                return true;
            }

            if (!TournamentStateMachine.getTeam(uuid).getRole(uuid).equalsIgnoreCase("leader")) {
                sender.sendMessage(ChatColor.RED + "You aren't the leader of your team!");
                return true;
            }

            if (Bukkit.getPlayer(args[1]) == null) {
                sender.sendMessage(ChatColor.RED + "That player isn't online!");
                return true;
            }

            Player targetPlayer = Bukkit.getPlayer(args[1]);
            UUID targetUUID = player.getUniqueId();

            if (!TournamentStateMachine.getTeam(targetUUID).equals(TournamentStateMachine.getTeam(uuid))) {
                sender.sendMessage(ChatColor.RED + "They aren't on your team!");
                return true;
            }

            TournamentStateMachine.getTeam(uuid).removeMember(uuid);
            TournamentStateMachine.removeTeam(uuid);
            player.sendMessage(ChatColor.AQUA + "You have removed " + targetPlayer.getName() + " from team " + TournamentStateMachine.getTeam(uuid).getName());
        } else if (args.length >= 2 && args[0].equalsIgnoreCase("join")) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();

            if (invitations.get(uuid) == null || invitations.get(uuid).size() == 0) {
                sender.sendMessage(ChatColor.RED + "You have no pending invitations!");
                return true;
            }

            if (TournamentStateMachine.getTeam(args[1]) == null) {
                sender.sendMessage(ChatColor.RED + "That team doesn't exist!");
                return true;
            }

            if (!invitations.get(uuid).contains(TournamentStateMachine.getTeam(args[1]))) {
                sender.sendMessage(ChatColor.RED + "You have no invitation to that team!");
                return true;
            }

            if (TournamentStateMachine.getTeam(uuid) != null) {
                sender.sendMessage(ChatColor.RED + "You are already on a team!");
                return true;
            }

            if (TournamentStateMachine.getTeam(uuid).getUUIDs().size() >= TournamentPlugin.getInstance().getMaxTeamSize()) {
                sender.sendMessage(ChatColor.RED + "That team has too many members!");
                return true;
            }

            TournamentStateMachine.getTeam(args[1]).addMember(uuid);
            List<Team> invitedTeams = invitations.get(uuid);
            invitedTeams.clear();
            player.sendMessage(ChatColor.AQUA + "You have joined team " + TournamentStateMachine.getTeam(uuid).getName());
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("leave")) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();

            if (TournamentStateMachine.getTeam(uuid) == null) {
                sender.sendMessage(ChatColor.RED + "You aren't on a team!");
                return true;
            }

            if (TournamentStateMachine.getTeam(uuid).getRole(uuid).equalsIgnoreCase("leader")) {
                sender.sendMessage(ChatColor.RED + "Please use /team disband " + TournamentStateMachine.getTeam(uuid).getName());
                return true;
            }

            TournamentStateMachine.getTeam(uuid).removeMember(uuid);
            TournamentStateMachine.removeTeam(uuid);
            player.sendMessage(ChatColor.AQUA + "You have left team " + TournamentStateMachine.getTeam(uuid).getName());
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("disband")) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();

            if (TournamentStateMachine.getTeam(uuid) == null) {
                sender.sendMessage(ChatColor.RED + "You aren't on a team!");
                return true;
            }

            if (!TournamentStateMachine.getTeam(uuid).getRole(uuid).equalsIgnoreCase("leader")) {
                sender.sendMessage(ChatColor.RED + "You aren't the leader of your team!");
                return true;
            }

            for (Tournament tournament : TournamentPlugin.getInstance().getTournaments()) {
                if (tournament.getTeams().contains(TournamentStateMachine.getTeam(uuid)) && tournament.isActive()) {
                    sender.sendMessage(ChatColor.RED + "You can't disband your team at this point!");
                    return true;
                }
            }

            for (UUID uuid1 : TournamentStateMachine.getTeam(uuid).getUUIDs()) {
                TournamentStateMachine.getTeam(uuid).removeMember(uuid1);
            }
            TournamentStateMachine.getTeam(uuid).getUUIDs().clear();
            sender.sendMessage(ChatColor.GREEN + "Team " + TournamentStateMachine.getTeam(uuid).getName() + " has been disbanded!");
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("clan")) {
            final Player player = (Player) sender;
            final UUID uuid = player.getUniqueId();
            new BukkitRunnable() {
                public void run() {
                    ClanMember senderMember = ClanManager.getClanMember(uuid);

                    if (!TournamentPlugin.getInstance().getInvitationType().equalsIgnoreCase("all") &&
                            !TournamentPlugin.getInstance().getInvitationType().equalsIgnoreCase("clan") &&
                            !TournamentPlugin.getInstance().getInvitationType().equalsIgnoreCase("clans")) {
                        sender.sendMessage(ChatColor.RED + "Clan invitations are disabled!");
                        return;
                    }

                    if (senderMember == null) {
                        sender.sendMessage(ChatColor.RED + "You aren't in a clan!");
                        return;
                    }

                    Clan clan = senderMember.getClan();
                    if (clan == null) {
                        sender.sendMessage(ChatColor.RED + "You aren't in a clan!");
                        return;
                    }

                    CLAN_RANK rank = ClanManager.getClanMember(uuid).getRank();
                    if (rank != null && (rank.equals(CLAN_RANK.LEADER) || rank.equals(CLAN_RANK.OFFICER))) {
                        sender.sendMessage(ChatColor.RED + "You aren't in a high enough position in your clan! You are " + rank.toString());
                        return;
                    }

                    for (ClanMember member : clan.getClanMembers()) {
                        if (!member.equals(senderMember)) {
                            player.performCommand("team invite " + member.getUuid());
                        }
                    }

                    sender.sendMessage(ChatColor.GREEN + "Clan members from " + clan.getName() + " have been invited!");
                }
            }.runTaskAsynchronously(TournamentPlugin.getInstance());
        }
        return true;
    }

}
