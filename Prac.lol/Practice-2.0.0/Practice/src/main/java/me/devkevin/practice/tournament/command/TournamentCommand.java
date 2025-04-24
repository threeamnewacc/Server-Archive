package me.devkevin.practice.tournament.command;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.Practice;
import me.devkevin.practice.PracticeLang;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchTeam;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.state.ProfileState;
import me.devkevin.practice.tournament.Tournament;
import me.devkevin.practice.tournament.state.TournamentState;
import me.devkevin.practice.util.Clickable;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Copyright 19/03/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class TournamentCommand extends PracticeCommand {
    private final Practice plugin = Practice.getInstance();

    @Override @Command(name = "tournament", usage = "&cUsage: /tournament [args]")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());
        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());
        if (args.length == 0) {
            if (coreProfile.hasStaff()) {
                player.sendMessage(" ");
                player.sendMessage(CC.translate("&e&lTournament Help&7:"));
                player.sendMessage(CC.translate(" &7" + PracticeLang.VERTICAL_BAR + " &e/tournament host"));
                player.sendMessage(CC.translate(" &7" + PracticeLang.VERTICAL_BAR + " &e/tournament stop"));
                player.sendMessage(CC.translate(" &7" + PracticeLang.VERTICAL_BAR + " &e/tournament alert"));
                player.sendMessage(CC.translate(" &7" + PracticeLang.VERTICAL_BAR + " &e/tournament forcestart"));
                player.sendMessage(" ");
                player.sendMessage(CC.translate(" &7" + PracticeLang.VERTICAL_BAR + " &e/tournament join"));
                player.sendMessage(CC.translate(" &7" + PracticeLang.VERTICAL_BAR + " &e/tournament leave"));
                player.sendMessage(CC.translate(" &7" + PracticeLang.VERTICAL_BAR + " &e/tournament status"));
                player.sendMessage(" ");
            } else {
                player.sendMessage(" ");
                player.sendMessage(CC.translate("&e&lTournament Help&7:"));
                player.sendMessage(CC.translate(" &7" + PracticeLang.VERTICAL_BAR + " &e/tournament join"));
                player.sendMessage(CC.translate(" &7" + PracticeLang.VERTICAL_BAR + " &e/tournament leave"));
                player.sendMessage(CC.translate(" &7" + PracticeLang.VERTICAL_BAR + " &e/tournament status"));
                player.sendMessage(" ");
            }
            return;
        }

        if (coreProfile.hasRank(Rank.ADMIN)) {
            switch (args[0].toLowerCase()) {
                case "host":
                    player.openInventory(this.plugin.getTournamentHostMenu().getInventoryUI().getCurrentPage());
                    break;
                case "stop":
                    if (args.length == 1) {
                        Tournament tournament = this.plugin.getTournamentManager().getTournament();
                        if (tournament != null) {
                            try {
                                tournament.getPlayers().forEach(uuid -> {
                                    Player tournamentPlayer = this.plugin.getServer().getPlayer(uuid);
                                    if (tournamentPlayer != null) {
                                        this.plugin.getTournamentManager().leaveTournament(player);
                                    }
                                });
                            } catch (Exception ignored) {
                            }

                            tournament.getPlayers().clear();
                            tournament.getMatches().clear();

                            this.plugin.getTournamentManager().removeTournament();
                            player.sendMessage(CC.RED + "Successfully stopped tournament!");
                        } else {
                            player.sendMessage(CC.RED + "This tournament does not exist.");
                        }
                    } else {
                        player.sendMessage(CC.RED + "Usage: /tournament stop");
                    }
                    break;
                case "alert":
                    if (args.length == 1) {
                        Tournament tournament = this.plugin.getTournamentManager().getTournament();
                        if (tournament != null) {
                            String toSend = CC.translate("&e" + tournament.getKitName()
                                    + " &8[&e" + tournament.getTeamSize() + "v&e" + tournament.getTeamSize() + "&8]"
                                    + " &7is starting soon. " + "&a[Click to Join]");

                            Clickable clickable = new Clickable(toSend, CC.translate("&7Click to join this tournament."), "/tournament join");

                            Bukkit.getServer().getOnlinePlayers()
                                    .stream().filter(tournamentPlayer -> !tournament.getPlayers().contains(tournamentPlayer.getUniqueId()))
                                    .collect(Collectors.toList()).forEach(clickable::sendToPlayer);
                        }
                    } else {
                        player.sendMessage(CC.RED + "Usage: /tournament alert");
                    }
                    break;
                case "forcestart":
                    if (args.length == 1) {
                        Tournament tournament = this.plugin.getTournamentManager().getTournament();
                        if (tournament != null) {
                            if (tournament.getTournamentState() == TournamentState.WAITING) {
                                tournament.setCountdown(11);
                                tournament.setTournamentState(TournamentState.STARTING);
                            } else if (tournament.getTournamentState() == TournamentState.STARTING || tournament.getTournamentState() == TournamentState.FIGHTING) {
                                player.sendMessage(CC.translate("&cYou cannot force-start the tournament because it has already started!"));
                            }
                        } else {
                            player.sendMessage(CC.translate("&cThere are no available tournaments."));
                        }
                    } else {
                        player.sendMessage(CC.RED + "Usage: /tournament forcestart");
                    }
                    break;
                case "join":
                    if (joinTournamentArg(args, player, profile)) return;
                    break;
                case "leave":
                    if (args.length == 1) {
                        boolean inTournament = this.plugin.getTournamentManager().isInTournament(player.getUniqueId());
                        if (inTournament) {
                            this.leaveTournament(player);
                        }
                    } else {
                        player.sendMessage(CC.RED + "Usage: /tournament leave");
                    }
                    break;
                case "status":
                    if (statusTournamentArg(args, player)) return;
                    break;
                default:
                    break;
            }
        } else {
            switch (args[0].toLowerCase()) {
                case "join":
                    if (joinTournamentArg(args, player, profile)) return;
                    break;
                case "leave":
                    if (args.length == 1) {
                        boolean inTournament = this.plugin.getTournamentManager().isInTournament(player.getUniqueId());
                        if (inTournament) {
                            this.leaveTournament(player);
                        }
                    } else {
                        player.sendMessage(CC.RED + "Usage: /tournament leave");
                    }
                    break;
                case "status":
                    if (statusTournamentArg(args, player)) return;
                    break;
                default:
                    break;
            }
        }
    }

    private boolean statusTournamentArg(String[] args, Player player) {
        if (args.length == 1) {
            if (this.plugin.getTournamentManager().getTournaments().size() == 0) {
                player.sendMessage(CC.RED + "There is no available tournaments.");
                return true;
            }

            for (Tournament tournament : this.plugin.getTournamentManager().getTournaments().values()) {
                if (tournament == null) {
                    player.sendMessage(CC.RED + "This tournament doesn't exist.");
                    return true;
                }

                player.sendMessage(CC.DARK_GRAY + CC.STRIKETHROUGH + "----------------------------------------------------");
                player.sendMessage(" ");
                player.sendMessage(CC.translate("&eTournament &7(&e" + tournament.getTeamSize() + "v&e" + tournament.getTeamSize() + "&7) &e" + tournament.getKitName()));

                if (tournament.getMatches().size() == 0) {
                    player.sendMessage(CC.RED + "There are no available tournament matches.");
                    player.sendMessage(" ");
                    player.sendMessage(CC.DARK_GRAY + CC.STRIKETHROUGH + "----------------------------------------------------");
                    return true;
                }

                for (UUID matchUUID : tournament.getMatches()) {
                    Match match = this.plugin.getMatchManager().getMatchFromUUID(matchUUID);

                    MatchTeam teamA = match.getTeams().get(0);
                    MatchTeam teamB = match.getTeams().get(1);

                    boolean teamAParty = teamA.getAlivePlayers().size() > 1;
                    boolean teamBParty = teamB.getAlivePlayers().size() > 1;
                    String teamANames = (teamAParty ? LandCore.getInstance().getProfileManager().getProfile(teamA.getLeader()).getGrant().getRank().getColor() + teamA.getLeaderName() + "'s Party" : LandCore.getInstance().getProfileManager().getProfile(teamA.getLeader()).getGrant().getRank().getColor() + teamA.getLeaderName());
                    String teamBNames = (teamBParty ? LandCore.getInstance().getProfileManager().getProfile(teamB.getLeader()).getGrant().getRank().getColor() + teamB.getLeaderName() + "'s Party" : LandCore.getInstance().getProfileManager().getProfile(teamB.getLeader()).getGrant().getRank().getColor() + teamB.getLeaderName());

                    Clickable clickable = new Clickable(CC.translate("&f&a" + teamANames + "&7 vs &c" + teamBNames + " &f&a[Click to Spectate]"),
                            CC.GRAY + "Click to spectate",
                            "/spectate " + teamA.getLeaderName());

                    clickable.sendToPlayer(player);
                }

                player.sendMessage(" ");
                player.sendMessage(CC.DARK_GRAY + CC.STRIKETHROUGH + "----------------------------------------------------");
            }
        } else {
            player.sendMessage(CC.RED + "Usage: /tournament status");
        }

        return false;
    }

    private boolean joinTournamentArg(String[] args, Player player, Profile profile) {
        if (args.length == 1) {
            if (profile.getState() != ProfileState.SPAWN) {
                player.sendMessage(PracticeLang.ERROR_STATE);
                return true;
            }

            if (this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
                player.sendMessage(CC.RED + "You are currently in a tournament.");
                return true;
            }

            Tournament tournament = this.plugin.getTournamentManager().getTournament();
            if (tournament != null) {
                if (tournament.isTeamTournament()) {
                    if (this.plugin.getPartyManager().getParty(player.getUniqueId()) == null) {
                        player.sendMessage(CC.RED + "The party size must be of " + tournament.getTeamSize() + " players.");
                        return true;
                    }
                }

                Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
                if (party != null && party.getMembers().size() != tournament.getTeamSize()) {
                    player.sendMessage(CC.RED + "The party size must be of " + tournament.getTeamSize() + " players.");
                    return true;
                }

                if (tournament.getSize() > tournament.getPlayers().size()) {
                    if ((tournament.getTournamentState() == TournamentState.WAITING || tournament.getTournamentState() == TournamentState.STARTING) && tournament.getCurrentRound() == 1) {
                        this.plugin.getTournamentManager().joinTournament(0, player);
                    } else {
                        player.sendMessage(CC.RED + "Sorry! The tournament already started.");
                    }
                } else {
                    player.sendMessage(CC.RED + "Sorry! The tournament is already full.");
                }
            } else {
                player.sendMessage(CC.RED + "There are no tournaments being hosted currently!");
            }
        } else {
            player.sendMessage(CC.RED + "Usage: /tournament join");
        }
        return false;
    }

    private void leaveTournament(Player player) {
        Tournament tournament = this.plugin.getTournamentManager().getTournament(player.getUniqueId());
        if (tournament != null) {
            this.plugin.getTournamentManager().leaveTournament(player);
        }
    }
}
