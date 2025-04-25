package us.zonix.practice.commands.event;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.events.PracticeEvent;
import us.zonix.practice.tournament.Tournament;

public class LeaveEventCommand extends Command {
    private final Practice plugin = Practice.getInstance();

    public LeaveEventCommand() {
        super("leave");
        this.setDescription("Leave an event or tournament.");
        this.setUsage(ChatColor.RED + "Usage: /leave");
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {
            Player player = (Player)sender;
            boolean inTournament = this.plugin.getTournamentManager().isInTournament(player.getUniqueId());
            boolean inEvent = this.plugin.getEventManager().getEventPlaying(player) != null;
            if (inEvent) {
                this.leaveEvent(player);
            } else if (inTournament) {
                this.leaveTournament(player);
            } else {
                player.sendMessage(ChatColor.RED + "There is nothing to leave.");
            }

            return true;
        }
    }

    private void leaveTournament(Player player) {
        Tournament tournament = this.plugin.getTournamentManager().getTournament(player.getUniqueId());
        if (tournament != null) {
            this.plugin.getTournamentManager().leaveTournament(player);
        }
    }

    private void leaveEvent(Player player) {
        PracticeEvent event = this.plugin.getEventManager().getEventPlaying(player);
        if (event == null) {
            player.sendMessage(ChatColor.RED + "That event doesn't exist.");
        } else if (!this.plugin.getEventManager().isPlaying(player, event)) {
            player.sendMessage(ChatColor.RED + "You are not in an event.");
        } else {
            event.leave(player);
        }
    }
}
