package me.devkevin.practice.tournament.team;

import lombok.Getter;
import me.devkevin.practice.match.team.KillableTeam;
import me.devkevin.practice.party.Party;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Copyright 19/03/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
public class TournamentTeam extends KillableTeam {
    private final Map<UUID, String> playerNames = new HashMap<>();

    public TournamentTeam(UUID leader, List<UUID> players) {
        super(leader, players);

        for (UUID playerUUID : players) {
            Party party  = this.plugin.getPartyManager().getParty(playerUUID);
            if (party == null) {
                this.playerNames.put(playerUUID, this.plugin.getServer().getPlayer(playerUUID).getName());
            }
        }
    }

    public void broadcastMessage(String message) {
        this.alivePlayers().forEach(player -> player.sendMessage(message));
    }

    public String getPlayerName(UUID uuid) {
        return this.playerNames.get(uuid);
    }
}
