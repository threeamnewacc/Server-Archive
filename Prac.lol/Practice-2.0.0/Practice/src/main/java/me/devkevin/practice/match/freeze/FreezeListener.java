package me.devkevin.practice.match.freeze;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.event.player.PlayerFreezeEvent;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.practice.Practice;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchTeam;
import me.devkevin.practice.match.event.impl.MatchEndEvent;
import me.devkevin.practice.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Copyright 19/02/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class FreezeListener implements Listener {

    private final Practice plugin = Practice.getInstance();

    @EventHandler
    public void onPlayerFrozenWhileInMatchEvent(PlayerFreezeEvent event) {
        Player player = event.getPlayer();

        // Get the Practice Profile.class info
        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        // Get the Core PlayerData.class info
        CoreProfile playerData = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

        // check when player's move is frozen
        if (playerData.isFrozen()) {

            // if player is not in a match continue
            if (profile.getCurrentMatchID() == null) {
                return;
            }

            // Get the match info by profile player
            Match match = this.plugin.getMatchManager().getMatch(profile);
            
            if (match != null) {
                MatchTeam opposingTeam = match.isFFA() ? match.getTeams().get(0) : ((profile.getTeamID() == 0) ? match.getTeams().get(1) : match.getTeams().get(0));
                MatchTeam playerTeam = match.getTeams().get(profile.getTeamID());

                if (match.isParty()) {
                    // Remove match and send to spawn is player is in party
                    this.plugin.getMatchManager().removeFighter(player, profile, false);
                    this.plugin.getProfileManager().sendToSpawn(player);
                } else {
                    // if player is frozen remove both players from the match, the
                    // frozen and the opponent, Remove and cancel the match and no winning/losing neither
                    this.plugin.getServer().getPluginManager().callEvent(new MatchEndEvent(match, opposingTeam, playerTeam));
                    match.broadcastMessage(CC.translate("&cThe match as been canceled due to a player being frozen."));
                }
            }
            // Remove player from the party, don't let frozen player get party
            // invites and request match from other users
            this.plugin.getPartyManager().removePartyInvites(player.getUniqueId());
            this.plugin.getMatchManager().removeMatchRequests(player.getUniqueId());
            this.plugin.getPartyManager().leaveParty(player);
        }
    }
}
