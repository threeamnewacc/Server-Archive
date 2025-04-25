package us.zonix.practice.commands.event;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.events.EventState;
import us.zonix.practice.events.PracticeEvent;
import us.zonix.practice.party.Party;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;
import us.zonix.practice.tournament.Tournament;
import us.zonix.practice.tournament.TournamentState;

public class JoinEventCommand extends Command {
    private final Practice plugin = Practice.getInstance();

    public JoinEventCommand() {
        super("join");
        this.setDescription("Join an event or tournament.");
        this.setUsage(ChatColor.RED + "Usage: /join <id>");
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {
            Player player = (Player)sender;
            if (args.length < 1) {
                player.sendMessage(this.usageMessage);
                return true;
            } else {
                PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
                if (playerData.getPlayerState() != PlayerState.SPAWN) {
                    player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
                    return true;
                } else {
                    boolean inTournament = this.plugin.getTournamentManager().isInTournament(player.getUniqueId());
                    boolean inEvent = this.plugin.getEventManager().getEventPlaying(player) != null;
                    String eventId = args[0].toLowerCase();
                    if (!NumberUtils.isNumber(eventId)) {
                        PracticeEvent event = this.plugin.getEventManager().getByName(eventId);
                        if (inTournament) {
                            player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
                            return true;
                        } else if (event == null) {
                            player.sendMessage(ChatColor.RED + "That event doesn't exist.");
                            return true;
                        } else if (event.getState() != EventState.WAITING) {
                            player.sendMessage(ChatColor.RED + "That event is currently not available.");
                            return true;
                        } else if (event.getPlayers().containsKey(player.getUniqueId())) {
                            player.sendMessage(ChatColor.RED + "You are already in this event.");
                            return true;
                        } else {
                            if (event.getPlayers().size() >= event.getLimit() && !player.hasPermission("practice.tournament.full")) {
                                player.sendMessage(ChatColor.RED + "Sorry! The event is already full.");
                            }

                            event.join(player);
                            return true;
                        }
                    } else if (inEvent) {
                        player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
                        return true;
                    } else if (this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
                        player.sendMessage(ChatColor.RED + "You are currently in a tournament.");
                        return true;
                    } else {
                        int id = Integer.parseInt(eventId);
                        Tournament tournament = this.plugin.getTournamentManager().getTournament(id);
                        if (tournament != null) {
                            if (tournament.getTeamSize() > 1) {
                                Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
                                if (party == null) {
                                    player.sendMessage(ChatColor.RED + "The party size must be of " + tournament.getTeamSize() + " players.");
                                    return true;
                                }

                                if (party.getMembers().size() != tournament.getTeamSize()) {
                                    player.sendMessage(ChatColor.RED + "The party size must be of " + tournament.getTeamSize() + " players.");
                                    return true;
                                }

                                if (tournament.getKitName().equalsIgnoreCase("HCTeams")) {
                                    if (party.getArchers().isEmpty() || party.getBards().isEmpty()) {
                                        player.sendMessage(ChatColor.RED + "You must specify your party's roles.\nUse: /party hcteams");
                                        player.closeInventory();
                                    }

                                    return true;
                                }
                            }

                            if (tournament.getSize() > tournament.getPlayers().size()) {
                                if ((tournament.getTournamentState() == TournamentState.WAITING || tournament.getTournamentState() == TournamentState.STARTING)
                                    && tournament.getCurrentRound() == 1) {
                                    this.plugin.getTournamentManager().joinTournament(id, player);
                                } else {
                                    player.sendMessage(ChatColor.RED + "Sorry! The tournament already started.");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "Sorry! The tournament is already full.");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "That tournament doesn't exist.");
                        }

                        return true;
                    }
                }
            }
        }
    }
}
