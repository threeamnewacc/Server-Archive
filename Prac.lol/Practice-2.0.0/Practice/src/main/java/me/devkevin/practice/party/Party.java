package me.devkevin.practice.party;

import club.inverted.chatcolor.CC;
import lombok.Data;
import me.devkevin.practice.Practice;
import me.devkevin.practice.match.MatchTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Copyright 28/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Data
public class Party {
    private final Practice plugin = Practice.getInstance();

    private Map<UUID, String> kits = new HashMap<>();

    private final UUID leader;
    private final Set<UUID> members = new HashSet<>();
    private int limit = 15;
    private boolean open;
    private BukkitTask broadcastTask;

    private List<MatchTeam> matchTeams = new ArrayList<>();

    private List<UUID> bards = new ArrayList<>();
    private List<UUID> archers = new ArrayList<>();

    public Party(UUID leader) {
        this.leader = leader;
        this.members.add(leader);
    }

    public void addMember(UUID uuid) {
        this.members.add(uuid);
    }

    public void removeMember(UUID uuid) {
        this.members.remove(uuid);
    }

    public void broadcast(String message) {
        this.members().forEach(member -> member.sendMessage(message));
    }

    public boolean isArcherFull() {
        return !getArchers().isEmpty();
    }

    public boolean isBardFull() {
        return !getBards().isEmpty();
    }

    public void removeClass(UUID uuid) {
        this.bards.remove(uuid);
        this.archers.remove(uuid);
    }

    public void addArcher(UUID uuid) {
        this.bards.remove(uuid);
        if (getArchers().size() != 0) {
            this.removeClass(uuid);
        } else {
            this.archers.add(uuid);
        }
    }

    public void addBard(UUID uuid) {
        this.archers.remove(uuid);
        if (getBards().size() != 0) {
            this.addArcher(uuid);
        } else {
            this.bards.add(uuid);
        }
    }

    public List<UUID> getPartySplitTeam(UUID uuid) {
        List<UUID> uuids = new ArrayList<>();
        for (UUID pUuid : findTeam(uuid).getAlivePlayers()) {
            if (pUuid != uuid) {
                uuids.add(pUuid);
            }
        }

        return uuids;
    }

    private MatchTeam findTeam(UUID uuid) {
        MatchTeam team = null;
        for (MatchTeam matchTeam : matchTeams) {
            if (matchTeam.getPlayers().contains(uuid)) {
                team = matchTeam;
            }
        }

        return team;
    }

    public MatchTeam findOpponent(UUID uuid) {
        MatchTeam team = null;
        for (MatchTeam matchTeam : matchTeams) {
            if (!matchTeam.getPlayers().contains(uuid)) {
                team = matchTeam;
            }
        }

        return team;
    }

    public MatchTeam[] splits() {
        matchTeams.clear();

        List<UUID> teamA = new ArrayList<>();
        List<UUID> teamB = new ArrayList<>();

        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (UUID member : this.members) {
            if (teamA.size() == teamB.size()) {
                if (random.nextBoolean()) {
                    teamA.add(member);
                } else {
                    teamB.add(member);
                }
            } else {
                if (teamA.size() < teamB.size()) {
                    teamA.add(member);
                } else {
                    teamB.add(member);
                }
            }
        }

        MatchTeam team1 = new MatchTeam(teamA.get(0), teamA, 0);
        MatchTeam team2 = new MatchTeam(teamB.get(0), teamB, 1);
        matchTeams.add(team1);
        matchTeams.add(team2);

        return new MatchTeam[] {
                team1,
                team2
        };
    }

    public List<UUID> getPartyMembersExcludeMember(UUID uuid) {
        return members.stream().filter(m -> m != uuid).collect(Collectors.toList());
    }

    public List<UUID> getPartyMembersExcludeLeader() {
        return members.stream().filter(m -> !this.leader.equals(m)).collect(Collectors.toList());
    }

    public Stream<Player> members() {
        return this.members.stream().map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull);
    }

    public class PartyTask extends BukkitRunnable {

        @Override
        public void run() {
            if (isOpen()) {
                Bukkit.broadcastMessage(CC.translate(""));
            }
        }
    }
}
