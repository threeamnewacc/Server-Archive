package me.devkevin.practice.match.duel.command;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.LandCore;
import me.devkevin.practice.Practice;
import me.devkevin.practice.PracticeLang;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchRequest;
import me.devkevin.practice.match.MatchTeam;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.party.manager.PartyManager;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.state.ProfileState;
import me.devkevin.practice.queue.QueueType;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Copyright 09/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class AcceptCommand extends PracticeCommand {

    private final Practice plugin = Practice.getInstance();

    @Command(name = "accept", inGameOnly = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(CC.RED + "/accept <player>");
            return;
        }

        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        if (profile.getState() != ProfileState.SPAWN && profile.getState() != ProfileState.PARTY) {
            player.sendMessage(CC.RED + "Unable to accept a duel within your duel.");
            return;
        }

        Player target = this.plugin.getServer().getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(String.format(PracticeLang.PLAYER_NOT_FOUND, args[0]));
            return;
        }

        if (player.getName().equals(target.getName())) {
            player.sendMessage(CC.RED + "You can't duel yourself.");
            return;
        }

        Profile targetProfile = this.plugin.getProfileManager().getProfileData(target.getUniqueId());

        if (targetProfile.isBusy()) {
            player.sendMessage(CC.RED + "That player is busy.");
            return;
        }

        if (targetProfile.isBusy()) {
            player.sendMessage(CC.RED + "That player is busy.");
            return;
        }

        if (this.plugin.getTournamentManager().isInTournament(target.getUniqueId())) {
            player.sendMessage(CC.RED + "That player is busy.");
            return;
        }
        if (this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
            player.sendMessage(CC.RED + "That player is busy.");
            return;
        }


        MatchRequest request = this.plugin.getMatchManager().getMatchRequest(target.getUniqueId(), player.getUniqueId());

        if (args.length > 1) {
            Kit kit = this.plugin.getKitManager().getKit(args[1]);

            if (kit != null) {
                request = this.plugin.getMatchManager().getMatchRequest(target.getUniqueId(), player.getUniqueId(), kit.getName());
            }
        }
        if (request == null) {
            player.sendMessage(CC.RED + "You do not have any pending requests.");
            return;
        }
        if (request.getRequester().equals(target.getUniqueId())) {
            List<UUID> playersA = new ArrayList<>();
            List<UUID> playersB = new ArrayList<>();

            PartyManager partyManager = this.plugin.getPartyManager();

            Party party = partyManager.getParty(player.getUniqueId());
            Party targetParty = partyManager.getParty(target.getUniqueId());

            if (request.isParty()) {
                if (party != null && targetParty != null && partyManager.isLeader(target.getUniqueId()) && partyManager.isLeader(target.getUniqueId())) {
                    playersA.addAll(party.getMembers());
                    playersB.addAll(targetParty.getMembers());
                } else {
                    player.sendMessage(CC.RED + "That player is not a party leader.");
                    return;
                }
            } else {
                if (party == null && targetParty == null) {
                    playersA.add(player.getUniqueId());
                    playersB.add(target.getUniqueId());
                } else {
                    player.sendMessage(CC.RED + "That player is already in a party.");
                    return;
                }
            }

            Kit kit = this.plugin.getKitManager().getKit(request.getKitName());

            MatchTeam teamA = new MatchTeam(target.getUniqueId(), playersB, 0);
            MatchTeam teamB = new MatchTeam(player.getUniqueId(), playersA, 1);

            Match match = new Match(request.getArena(), kit, QueueType.UN_RANKED, teamA, teamB);

            Player leaderA = this.plugin.getServer().getPlayer(teamA.getLeader());
            Player leaderB = this.plugin.getServer().getPlayer(teamB.getLeader());

            match.broadcastMessage(CC.YELLOW + "Starting duel. " +
                    CC.GRAY + "(" + LandCore.getInstance().getProfileManager().getProfile(leaderA.getUniqueId()).getGrant().getRank().getColor() + leaderA.getName() + CC.GRAY + " vs " + LandCore.getInstance().getProfileManager().getProfile(leaderB.getUniqueId()).getGrant().getRank().getColor() + leaderB.getName() + CC.GRAY + ") in " + CC.GOLD + match.getArena().getName());

            this.plugin.getMatchManager().createMatch(match);
        }
    }
}
