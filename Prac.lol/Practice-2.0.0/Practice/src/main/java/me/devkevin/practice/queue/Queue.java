package me.devkevin.practice.queue;

import club.inverted.chatcolor.CC;
import lombok.Getter;
import lombok.Setter;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.Practice;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchTeam;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.state.ProfileState;
import me.devkevin.practice.util.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright 11/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
@Setter
public class Queue {

    private final Map<UUID, QueueEntry> queued = new ConcurrentHashMap<>();
    private final Map<UUID, Long> playerQueueTime = new HashMap<>();

    private final Practice plugin = Practice.getInstance();

    private boolean rankedEnabled = true;

    public Queue() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            this.queued.forEach((key, value) -> {
                if (!value.isFound()) {
                    if (value.isParty()) {
                        this.findPartyMatch(plugin.getPartyManager().getParty(key), value.getKitName(), value.getElo(), value.getQueueType());
                    } else {
                        this.findMatch(plugin.getServer().getPlayer(key), value.getKitName(), value.getElo(), value.getQueueType());
                    }
                }
            });
        }, 20L, 20L);
    }

    public void addPlayer(Player player, Profile profile, String kitName, QueueType type) {
        if (type != QueueType.UN_RANKED && !this.rankedEnabled) {
            player.sendMessage(CC.RED + "Ranked is currently disabled until the server restarts.");
            player.closeInventory();
            return;
        }

        profile.setState(ProfileState.QUEUE);

        int elo = (type == QueueType.RANKED) ? profile.getElo(kitName) : 0;

        QueueEntry entry = new QueueEntry(type, kitName, elo, false);

        this.queued.put(profile.getUuid(), entry);
        this.giveQueueItems(player);


        String message = (type != QueueType.UN_RANKED ? CC.YELLOW + "You were added to the " + CC.GOLD + type.getName() + " " + kitName + CC.YELLOW +
                " queue" +
                " with " + CC.GOLD + elo + CC.YELLOW + " elo." :
        CC.YELLOW + "You were added to the " + CC.GOLD + "Unranked " + kitName + CC.YELLOW + " queue.");


        player.sendMessage(message);

        this.playerQueueTime.put(player.getUniqueId(), System.currentTimeMillis());

        if (type.isRanked()) {
            player.sendMessage(CC.YELLOW + "Searching in ELO range " + CC.GOLD
                    + (profile.getEloRange() == -1
                    ? "Unrestricted"
                    : "[" + Math.max(elo - profile.getEloRange() / 2, 0)
                    + " -> " + Math.max(elo + profile.getEloRange() / 2, 0) + "]"));
        }
    }

    private void giveQueueItems(Player player) {
        player.closeInventory();
        player.getInventory().setContents(this.plugin.getHotbarItem().getQueueItems());
        player.updateInventory();
    }

    public QueueEntry getQueueEntry(UUID uuid) {
        return this.queued.get(uuid);
    }

    public long getPlayerQueueTime(UUID uuid) {
        return this.playerQueueTime.get(uuid);
    }

    public int getQueueSize(String ladder, QueueType type) {
        return (int) this.queued.entrySet().stream().filter(entry -> entry.getValue().getQueueType() == type).filter(entry -> entry.getValue().getKitName().equals(ladder)).count();
    }

    public int getQueueSize() {
        return this.queued.entrySet().size();
    }

    public int getQueueSize(QueueType type) {
        return (int) this.queued.entrySet().stream().filter(entry -> entry.getValue().getQueueType() == type).count();
    }

    private void findMatch(Player player, String kitName, int elo, QueueType type) {
        long queueTime = System.currentTimeMillis() - this.playerQueueTime.get(player.getUniqueId());

        if (this.playerQueueTime.containsKey(player.getUniqueId()) && this.playerQueueTime.get(player.getUniqueId()) == null) {
            player.kickPlayer("Player data is null. Please re-connect.");
            player.sendMessage(CC.RED + "Please re-connect.");
            return;
        }

        Profile playerData = this.plugin.getProfileManager().getProfileData(player.getUniqueId());
        if (playerData == null) {
            plugin.getLogger().warning(player.getName() + "'s player data is null" + "(" + this.getClass().getName() + ")");
            return;
        }

        CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

        int eloRange = coreProfile.hasRank(Rank.BASIC) ? playerData.getEloRange() : -1;
        int pingRange = coreProfile.hasRank(Rank.DIAMOND) ? playerData.getPingRange() : -1;
        int seconds = Math.round(queueTime / 1000L);

        if (seconds > 5 && type != QueueType.UN_RANKED) {
            if (pingRange != -1) {
                pingRange += (seconds - 5) * 25;
            }

            if (eloRange != -1) {
                eloRange += seconds * 50;

                if (eloRange >= 3000) {
                    eloRange = 3000;
                } else  {
                    player.sendMessage(
                            CC.YELLOW + "Searching in ELO range "
                                    + CC.GOLD + (eloRange == -1 ? "Unrestricted"
                                    : "[" + Math.max(elo - eloRange / 2, 0) + " -> " +
                                    Math.max(elo + eloRange / 2, 0) + "]"));
                }
            }
        }

        if (eloRange == -1) {
            eloRange = Integer.MAX_VALUE;
        }

        if (pingRange == -1) {
            pingRange = Integer.MAX_VALUE;
        }

        int ping = 0;

        for (Map.Entry<UUID, QueueEntry> queueData : this.queued.entrySet()) {
            UUID opponent = queueData.getKey();
            QueueEntry queueEntry = queueData.getValue();

            if (opponent == player.getUniqueId()) {
                continue;
            }

            if (!queueEntry.getKitName().equals(kitName)) {
                continue;
            }
            if (queueEntry.getQueueType() != type) {
                continue;
            }
            if (queueEntry.isParty()) {
                continue;
            }
            if (queueEntry.isFound()) {
                continue;
            }

            Player opponentPlayer = this.plugin.getServer().getPlayer(opponent);
            Profile opponentProfile = this.plugin.getProfileManager().getProfileData(opponent);

            if (opponentProfile.getState() == ProfileState.FIGHTING) {
                continue;
            }

            if (playerData.getState() == ProfileState.FIGHTING) {
                continue;
            }

            int eloDiff = Math.abs(queueEntry.getElo() - elo);

            if (type.isRanked()) {
                if (eloDiff > eloRange) {
                    continue;
                }

                CoreProfile opponentLOL = LandCore.getInstance().getProfileManager().getProfile(opponent);
                long opponentQueueTime = System.currentTimeMillis() -
                        this.playerQueueTime.get(opponentPlayer.getUniqueId());
                int opponentEloRange = opponentLOL.hasRank(Rank.BASIC) ? opponentProfile.getEloRange() : -1;
                int opponentPingRange = opponentLOL.hasRank(Rank.DIAMOND) ? opponentProfile.getPingRange() : -1;
                int opponentSeconds = Math.round(opponentQueueTime / 1000L);
                if (opponentSeconds > 5) {
                    if (opponentPingRange != -1) {
                        opponentPingRange += (opponentSeconds - 5) * 25;
                    }

                    if (opponentEloRange != -1) {
                        opponentEloRange += opponentSeconds * 50;
                        if (opponentEloRange >= 3000) {
                            opponentEloRange = 3000;
                        }
                    }
                }

                if (opponentEloRange == -1) {
                    opponentEloRange = Integer.MAX_VALUE;
                }

                if (opponentPingRange == -1) {
                    opponentPingRange = Integer.MAX_VALUE;
                }

                if (eloDiff > opponentEloRange) {
                    continue;
                }

                int pingDiff = Math.abs(0 - ping);

                if (type == QueueType.RANKED || type == QueueType.FACTION) {
                    if (pingDiff > opponentPingRange) {
                        continue;
                    }
                    if (pingDiff > pingRange) {
                        continue;
                    }
                }
            }

            Kit kit = plugin.getKitManager().getKit(kitName);
            Arena arena = plugin.getArenaManager().getRandomArena(kit, playerData.getLastArenaPlayed());

            if (arena == null) {
                player.sendMessage(ChatColor.RED + "There are no arenas available for this kit.");
                return;
            }

            if (type.isRanked()) {
                player.sendMessage("");
                player.sendMessage(CC.translate("&6&lRanked Match"));
                player.sendMessage(CC.translate("&7\u25CF &eMap: &6" + arena.getName()));
                player.sendMessage(CC.translate("&7\u25CF &eOpponent: " + LandCore.getInstance().getProfileManager().getProfile(opponentPlayer.getUniqueId()).getGrant().getRank().getColor() + opponentPlayer.getName()));
                player.sendMessage(CC.translate("&7\u25CF &eELO: " + (this.queued.get(player.getUniqueId()).getElo() > this.queued.get(opponentPlayer.getUniqueId()).getElo() ? ChatColor.RED: ChatColor.GREEN) + this.queued.get(opponentPlayer.getUniqueId()).getElo() + ChatColor.GRAY + " (" + (this.queued.get(player.getUniqueId()).getElo() > this.queued.get(opponentPlayer.getUniqueId()).getElo() ? ChatColor.RED + "-" : ChatColor.GREEN + "+") + ((Math.max(this.queued.get(opponentPlayer.getUniqueId()).getElo(), this.queued.get(player.getUniqueId()).getElo()) - Math.min(this.queued.get(opponentPlayer.getUniqueId()).getElo(), this.queued.get(player.getUniqueId()).getElo()))) + ChatColor.GRAY + ")"));
                player.sendMessage(CC.translate("&7\u25CF &ePing: &6" + PlayerUtil.getPing(opponentPlayer) + " ms"));
                player.sendMessage("");

                opponentPlayer.sendMessage("");
                opponentPlayer.sendMessage(CC.translate("&6&lRanked Match"));
                opponentPlayer.sendMessage(CC.translate("&7\u25CF &eMap: &6" + arena.getName()));
                opponentPlayer.sendMessage(CC.translate("&7\u25CF &eOpponent:" + LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId()).getGrant().getRank().getColor() + player.getName()));
                opponentPlayer.sendMessage(CC.translate("&7\u25CF &eELO: " + (this.queued.get(opponentPlayer.getUniqueId()).getElo() > this.queued.get(player.getUniqueId()).getElo() ? ChatColor.RED : ChatColor.GREEN) + this.queued.get(player.getUniqueId()).getElo() + ChatColor.GRAY + " (" + (this.queued.get(opponentPlayer.getUniqueId()).getElo() > this.queued.get(player.getUniqueId()).getElo() ? ChatColor.RED + "-" : ChatColor.GREEN + "+") + ((Math.max(this.queued.get(player.getUniqueId()).getElo(), this.queued.get(opponentPlayer.getUniqueId()).getElo()) - Math.min(this.queued.get(player.getUniqueId()).getElo(), this.queued.get(opponentPlayer.getUniqueId()).getElo()))) + ChatColor.GRAY + ")"));
                opponentPlayer.sendMessage(CC.translate("&7\u25CF &ePing: &6" + PlayerUtil.getPing(player) + " ms"));
                opponentPlayer.sendMessage("");
            } else {
                player.sendMessage("");
                player.sendMessage(CC.translate("&6&lUnRanked Match"));
                player.sendMessage(CC.translate("&7\u25CF &eMap: &6" + arena.getName()));
                player.sendMessage(CC.translate("&7\u25CF &eOpponent: " + LandCore.getInstance().getProfileManager().getProfile(opponentPlayer.getUniqueId()).getGrant().getRank().getColor() + opponentPlayer.getName()));
                player.sendMessage(CC.translate("&7\u25CF &ePing: &6" + PlayerUtil.getPing(opponentPlayer) + " ms"));
                player.sendMessage("");

                opponentPlayer.sendMessage("");
                opponentPlayer.sendMessage(CC.translate("&6&lUnRanked Match"));
                opponentPlayer.sendMessage(CC.translate("&7\u25CF &eMap: &6" + arena.getName()));
                opponentPlayer.sendMessage(CC.translate("&7\u25CF &eOpponent: " + LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId()).getGrant().getRank().getColor() + player.getName()));
                opponentPlayer.sendMessage(CC.translate("&7\u25CF &ePing: &6" + PlayerUtil.getPing(player) + " ms"));
                opponentPlayer.sendMessage("");
            }

            MatchTeam teamA = new MatchTeam(player.getUniqueId(), Collections.singletonList(player.getUniqueId()), 0);
            MatchTeam teamB = new MatchTeam(opponentPlayer.getUniqueId(), Collections.singletonList(opponentPlayer.getUniqueId()), 1);
            Match match = new Match(arena, kit, type, teamA, teamB);

            QueueEntry queueEntry1 = queued.get(player.getUniqueId());
            if (queueEntry1 != null) { // should not be null, but, things happen.gi
                queueEntry1.setFound(true);
            }
            queueEntry.setFound(true);

            this.queued.remove(player.getUniqueId());
            this.queued.remove(opponentPlayer.getUniqueId());

            this.playerQueueTime.remove(player.getUniqueId());
            this.playerQueueTime.remove(opponentPlayer.getUniqueId());

            plugin.getMatchManager().createMatch(match);

            return;
        }
    }

    public void removePlayerFromQueue(Player player) {
        QueueEntry entry = this.queued.get(player.getUniqueId());

        this.queued.remove(player.getUniqueId());

        this.plugin.getProfileManager().sendToSpawnAndResetButNotTP(player);

        player.sendMessage(ChatColor.RED + "You have left the " + entry.getQueueType().getName() + " " + entry.getKitName() + " queue.");
    }

    public void removePartyFromQueue(Party party) {
        QueueEntry entry = this.queued.get(party.getLeader());

        this.queued.remove(party.getLeader());

        party.members().forEach(this.plugin.getProfileManager()::sendToSpawnAndResetButNotTP);

        String type = entry.getQueueType().isRanked() ? "Ranked" : "Unranked";

        party.broadcast(ChatColor.YELLOW + "You party has left the " + type + " " + entry.getKitName() + " queue.");
    }

    private void findPartyMatch(Party partyA, String kitName, int elo, QueueType type) {
        Profile practicePlayerData = this.plugin.getProfileManager().getProfileData(partyA.getLeader());
        UUID opponent = this.queued.entrySet().stream()
                .filter(entry -> entry.getKey() != partyA.getLeader())
                .filter(entry -> this.plugin.getProfileManager().getProfileData(entry.getKey()) != null)
                .filter(entry -> this.plugin.getProfileManager().getProfileData(entry.getKey()).getState() == ProfileState.QUEUE)
                .filter(entry -> entry.getValue().isParty())
                .filter(entry -> entry.getValue().getQueueType() == type)
                .filter(entry -> !type.isRanked())
                .filter(entry -> entry.getValue().getKitName().equals(kitName))
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);

        if (opponent == null) {
            return;
        }

        Profile opponentData = this.plugin.getProfileManager().getProfileData(opponent);
        if (opponentData.getState() == ProfileState.FIGHTING) {
            return;
        }
        if (practicePlayerData.getState() == ProfileState.FIGHTING) {
            return;
        }

        Player leaderA = this.plugin.getServer().getPlayer(partyA.getLeader());
        Player leaderB = this.plugin.getServer().getPlayer(opponent);

        Kit kit = this.plugin.getKitManager().getKit(kitName);
        Arena arena = this.plugin.getArenaManager().getRandomArena(kit, practicePlayerData.getLastArenaPlayed());
        Party partyB = this.plugin.getPartyManager().getParty(opponent);

        List<UUID> playersA = new ArrayList<>(partyA.getMembers());
        List<UUID> playersB = new ArrayList<>(partyB.getMembers());

        MatchTeam teamA = new MatchTeam(leaderA.getUniqueId(), playersA, 0);
        MatchTeam teamB = new MatchTeam(leaderB.getUniqueId(), playersB, 1);

        Match match = new Match(arena, kit, type, teamA, teamB);
        this.plugin.getMatchManager().createMatch(match);

        String party1FoundMatchMessage;
        String party2FoundMatchMessage;

        if (type.isRanked()) {
            party1FoundMatchMessage = CC.YELLOW + "Found ranked match: " + CC.GREEN + leaderA.getName() +
                    "'s party (" +
                    elo + " elo)" + CC.YELLOW + " vs. "
                    + CC.RED + leaderB.getName() + "'s Party (" +
                    this.queued.get(leaderB.getUniqueId()).getElo() + " elo)";
            party2FoundMatchMessage = CC.YELLOW + "Found ranked match: " + CC.GREEN + leaderB.getName() + "'s party ("
                    + this.queued.get(leaderB.getUniqueId()).getElo() + " elo)" + CC.YELLOW +
                    " vs. " +
                    CC.RED + leaderA.getName() + "'s Party (" + elo + " elo)";
        } else {
            party1FoundMatchMessage = CC.YELLOW + "Found unranked match: " + CC.GREEN + leaderA.getName() +
                    "'s party" +
                    CC.YELLOW + " vs. "
                    + CC.RED + leaderB.getName() + "'s party";
            party2FoundMatchMessage = CC.YELLOW + "Found unranked match: " + CC.GREEN + leaderB.getName() +
                    "'s party" +
                    CC.YELLOW + " vs. "
                    + CC.RED + leaderA.getName() + "'s party";
        }

        leaderA.sendMessage(party1FoundMatchMessage);
        leaderB.sendMessage(party2FoundMatchMessage);

        this.queued.remove(partyA.getLeader());
        this.queued.remove(partyB.getLeader());
    }
}
