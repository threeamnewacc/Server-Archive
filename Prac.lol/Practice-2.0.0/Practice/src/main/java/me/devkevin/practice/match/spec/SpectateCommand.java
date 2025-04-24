package me.devkevin.practice.match.spec;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.LandCore;
import me.devkevin.practice.Practice;
import me.devkevin.practice.PracticeLang;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.events.PracticeEvent;
import me.devkevin.practice.events.lms.LMSEvent;
import me.devkevin.practice.events.sumo.SumoEvent;
import me.devkevin.practice.events.tntrun.TntRunEvent;
import me.devkevin.practice.events.tnttag.TNTTagEvent;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchTeam;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.profile.state.ProfileState;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Copyright 10/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class SpectateCommand extends PracticeCommand {

    private final Practice plugin = Practice.getInstance();

    @Command(name = "spectate", aliases = "spec", inGameOnly = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            player.sendMessage(CC.RED + "/spectate <player>");
            return;
        }
        me.devkevin.practice.profile.Profile profileData = this.plugin.getProfileManager().getProfileData(player.getUniqueId());
        Party party = this.plugin.getPartyManager().getParty(profileData.getUuid());

        if (party != null || (profileData.getState() != ProfileState.SPAWN && profileData.getState() != ProfileState.SPECTATING)) {
            player.sendMessage(CC.RED + "You can't execute that command in your current state.");
            return;
        }

        Player target = this.plugin.getServer().getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(String.format(PracticeLang.PLAYER_NOT_FOUND, args[0]));
            return;
        }
        me.devkevin.practice.profile.Profile targetData = this.plugin.getProfileManager().getProfileData(target.getUniqueId());

        if (targetData.getState() == ProfileState.EVENT) {
            PracticeEvent event = this.plugin.getEventManager().getEventPlaying(target);

            if(event == null) {
                player.sendMessage(CC.RED + "That player is currently not in an event.");
                return;
            }

            if (event instanceof SumoEvent) {
                player.performCommand("eventspectate Sumo");
            } else if(event instanceof TNTTagEvent) {
                player.performCommand("eventspectate TNTTag");
            } else if(event instanceof TntRunEvent) {
                player.performCommand("eventspectate TNTRun");
            } else if (event instanceof LMSEvent) {
                player.performCommand("eventspectate LMS");
            }
            return;
        }

        if (targetData.getState() != ProfileState.FIGHTING) {
            player.sendMessage(CC.RED + "That player is not fighting.");
            return;
        }

        Match targetMatch = this.plugin.getMatchManager().getMatch(targetData);

        if (!targetMatch.isParty()) {
            if (!targetData.getOptions().isSpectators() && !player.hasPermission("practice.staff")) {
                player.sendMessage(CC.RED + "That player has ignored spectators.");
                return;
            }

            MatchTeam team = targetMatch.getTeams().get(0);
            MatchTeam team2 = targetMatch.getTeams().get(1);

            me.devkevin.practice.profile.Profile otherPlayerData = this.plugin.getProfileManager().getProfileData(team.getPlayers().get(0) == target.getUniqueId() ? team2.getPlayers().get(0) : team.getPlayers().get(0));

            if (otherPlayerData == null && !otherPlayerData.getOptions().isSpectators() && !player.hasPermission("practice.staff")) {
                player.sendMessage(CC.RED + "That player has ignored spectators.");
                return;
            }
        }
        if (profileData.getState() == ProfileState.SPECTATING) {
            Match match = this.plugin.getMatchManager().getSpectatingMatch(player.getUniqueId());

            if (match.equals(targetMatch)) {
                player.sendMessage(CC.RED + "You are already spectating this player.");
                return;
            }
            match.removeSpectator(player.getUniqueId());
        }
        player.sendMessage(CC.GREEN + "You are spectating " + LandCore.getInstance().getProfileManager().getProfile(target.getUniqueId()).getGrant().getRank().getColor() + target.getName() + CC.GREEN + ".");
        this.plugin.getMatchManager().addSpectator(player, profileData, target, targetMatch);
    }
}
