package net.badlion.tournament.commands;

import net.badlion.tournament.TournamentPlugin;
import net.badlion.tournament.TournamentStateMachine;
import net.badlion.tournament.bracket.filter.ActiveUUIDFilter;
import net.badlion.tournament.bracket.tree.bracket.SeriesNode;
import net.badlion.tournament.teams.Team;
import net.badlion.tournament.tournaments.Tournament;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class ReadyCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        UUID uuid = ((Player)sender).getUniqueId();
        Team team = TournamentStateMachine.getTeam(uuid);
        Tournament tournament = TournamentPlugin.getInstance().getTournament(uuid);

        List<SeriesNode> nodes = tournament.getBracket().search(new ActiveUUIDFilter(tournament.getBracket(), uuid));
        for (SeriesNode node : nodes) {
            tournament.getBracket().readyTeam(node, team);
            sender.sendMessage(ChatColor.GREEN + "Marked as ready for tournament: " + tournament.getName());
        }
        return true;
    }

}
