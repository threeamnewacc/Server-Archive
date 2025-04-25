package us.zonix.practice.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.arena.Arena;
import us.zonix.practice.kit.Kit;
import us.zonix.practice.match.Match;
import us.zonix.practice.match.MatchTeam;
import us.zonix.practice.party.Party;
import us.zonix.practice.player.EloRank;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;
import us.zonix.practice.queue.QueueEntry;
import us.zonix.practice.queue.QueueType;

public class QueueManager {
    private final Map<UUID, QueueEntry> queued = new ConcurrentHashMap<>();
    private final Map<UUID, Long> playerQueueTime = new HashMap<>();
    private final Practice plugin = Practice.getInstance();
    private boolean rankedEnabled = true;

    public QueueManager() {
        this.plugin
            .getServer()
            .getScheduler()
            .runTaskTimer(
                this.plugin,
                () -> this.queued
                        .forEach(
                            (key, value) -> {
                                if (value.isParty()) {
                                    this.findMatch(
                                        this.plugin.getPartyManager().getParty(key),
                                        value.getKitName(),
                                        value.getElo(),
                                        value.getQueueType(),
                                        value.isBestOfThree()
                                    );
                                } else {
                                    this.findMatch(
                                        this.plugin.getServer().getPlayer(key), value.getKitName(), value.getElo(), value.getQueueType(), value.isBestOfThree()
                                    );
                                }
                            }
                        ),
                20L,
                20L
            );
    }

    public void addPlayerToQueue(Player player, PlayerData playerData, String kitName, QueueType type, boolean bestOfThree) {
        if (type != QueueType.UNRANKED && !this.rankedEnabled) {
            player.closeInventory();
        } else {
            playerData.setPlayerState(PlayerState.QUEUE);
            int elo = type.isBoth() ? playerData.getElo(kitName) : 0;
            QueueEntry entry = new QueueEntry(type, kitName, bestOfThree, elo, false);
            this.queued.put(playerData.getUniqueId(), entry);
            this.giveQueueItems(player);
            String unrankedMessage = ChatColor.YELLOW + "You have been added to the " + ChatColor.GOLD + "Unranked " + kitName + ChatColor.YELLOW + " queue.";
            String[] eloRank = EloRank.getRankByElo(elo).name().split("_");
            String eloRankText = StringUtils.capitalize(eloRank[0].toLowerCase()) + " " + eloRank[1];
            String rankedMessage = ChatColor.YELLOW
                + "You have been added to the "
                + ChatColor.GOLD
                + "Ranked "
                + kitName
                + ChatColor.YELLOW
                + " queue. "
                + ChatColor.GRAY
                + "["
                + ChatColor.WHITE
                + eloRankText
                + ChatColor.GRAY
                + " ❘ "
                + ChatColor.GREEN
                + elo
                + ChatColor.GRAY
                + "]";
            String premiumMessage = ChatColor.YELLOW
                + "You have been added to the "
                + ChatColor.GOLD
                + "Premium "
                + kitName
                + ChatColor.YELLOW
                + " queue. "
                + ChatColor.GRAY
                + "["
                + ChatColor.WHITE
                + eloRankText
                + ChatColor.GRAY
                + " ❘ "
                + ChatColor.GREEN
                + elo
                + ChatColor.GRAY
                + "]";
            player.sendMessage(type == QueueType.UNRANKED ? unrankedMessage : (type == QueueType.PREMIUM ? premiumMessage : rankedMessage));
            this.playerQueueTime.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    private void giveQueueItems(Player player) {
        player.closeInventory();
        player.getInventory().setContents(this.plugin.getItemManager().getQueueItems());
        player.updateInventory();
    }

    public QueueEntry getQueueEntry(UUID uuid) {
        return this.queued.get(uuid);
    }

    public long getPlayerQueueTime(UUID uuid) {
        return this.playerQueueTime.get(uuid);
    }

    public int getQueueSize(String ladder, QueueType type) {
        return (int)this.queued
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().getQueueType() == type)
            .filter(entry -> entry.getValue().getKitName().equals(ladder))
            .count();
    }

    private boolean findMatch(Player player, String kitName, int elo, QueueType type, boolean bestOfThree) {
        long queueTime = System.currentTimeMillis() - this.playerQueueTime.get(player.getUniqueId());
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            this.plugin.getLogger().warning(player.getName() + "'s player data is null");
            return false;
        } else {
            int eloRange = playerData.getEloRange();
            EloRank eloRank = EloRank.getRankByElo(elo);
            int pingRange = player.hasPermission("practice.pingmatching") ? playerData.getPingRange() : -1;
            int seconds = Math.round((float)(queueTime / 1000L));
            if (seconds > 5 && type != QueueType.UNRANKED) {
                if (pingRange != -1) {
                    pingRange += (seconds - 5) * 25;
                }

                if (eloRange != -1) {
                    eloRange += seconds * 50;
                    if (eloRange >= eloRank.getEloRange()) {
                        eloRange = eloRank.getEloRange();
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
            Iterator var14 = this.queued.keySet().iterator();

            Player opponentPlayer;
            while (true) {
                if (!var14.hasNext()) {
                    return false;
                }

                UUID opponent = (UUID)var14.next();
                if (opponent != player.getUniqueId()) {
                    QueueEntry queueEntry = this.queued.get(opponent);
                    if (queueEntry.getKitName().equals(kitName)
                        && queueEntry.getQueueType() == type
                        && queueEntry.isBestOfThree() == bestOfThree
                        && !queueEntry.isParty()) {
                        opponentPlayer = this.plugin.getServer().getPlayer(opponent);
                        PlayerData opponentData = this.plugin.getPlayerManager().getPlayerData(opponent);
                        if (opponentData.getPlayerState() != PlayerState.FIGHTING && playerData.getPlayerState() != PlayerState.FIGHTING) {
                            EloRank opponentEloRank = EloRank.getRankByElo(opponentData.getElo(kitName));
                            int eloDiff = Math.abs(queueEntry.getElo() - elo);
                            if (!type.isBoth()) {
                                break;
                            }

                            if (eloDiff <= eloRange) {
                                long opponentQueueTime = System.currentTimeMillis() - this.playerQueueTime.get(opponentPlayer.getUniqueId());
                                int opponentEloRange = opponentData.getEloRange();
                                int opponentPingRange = player.hasPermission("practice.pingmatching") ? opponentData.getPingRange() : -1;
                                int opponentSeconds = Math.round((float)(opponentQueueTime / 1000L));
                                if (opponentSeconds > 5) {
                                    if (opponentPingRange != -1) {
                                        opponentPingRange += (opponentSeconds - 5) * 25;
                                    }

                                    if (opponentEloRange != -1) {
                                        opponentEloRange += opponentSeconds * 50;
                                        if (opponentEloRange >= opponentEloRank.getEloRange()) {
                                            opponentEloRange = opponentEloRank.getEloRange();
                                        }
                                    }
                                }

                                if (opponentEloRange == -1) {
                                    opponentEloRange = Integer.MAX_VALUE;
                                }

                                if (opponentPingRange == -1) {
                                    opponentPingRange = Integer.MAX_VALUE;
                                }

                                if (eloDiff <= opponentEloRange) {
                                    int pingDiff = Math.abs(0 - ping);
                                    if (type != QueueType.RANKED && type != QueueType.PREMIUM || pingDiff <= opponentPingRange && pingDiff <= pingRange) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Kit kit = this.plugin.getKitManager().getKit(kitName);
            Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
            String playerFoundMatchMessage;
            String matchedFoundMatchMessage;
            if (type.isBoth()) {
                String[] playerEloRank = EloRank.getRankByElo(this.queued.get(player.getUniqueId()).getElo()).name().split("_");
                String playerEloRankText = StringUtils.capitalize(playerEloRank[0].toLowerCase()) + " " + playerEloRank[1];
                playerFoundMatchMessage = ChatColor.RED
                    + player.getName()
                    + ChatColor.GRAY
                    + " ["
                    + ChatColor.WHITE
                    + playerEloRankText
                    + " ❘ "
                    + this.queued.get(player.getUniqueId()).getElo()
                    + ChatColor.GRAY
                    + "]";
                String[] matchedEloRank = EloRank.getRankByElo(this.queued.get(opponentPlayer.getUniqueId()).getElo()).name().split("_");
                String matchedEloRankText = StringUtils.capitalize(matchedEloRank[0].toLowerCase()) + " " + matchedEloRank[1];
                matchedFoundMatchMessage = ChatColor.RED
                    + opponentPlayer.getName()
                    + ChatColor.GRAY
                    + " ["
                    + ChatColor.WHITE
                    + matchedEloRankText
                    + " ❘ "
                    + this.queued.get(opponentPlayer.getUniqueId()).getElo()
                    + ChatColor.GRAY
                    + "]";
            } else {
                playerFoundMatchMessage = ChatColor.RED + player.getName() + ".";
                matchedFoundMatchMessage = ChatColor.RED + opponentPlayer.getName() + ".";
            }

            player.sendMessage(ChatColor.YELLOW + "Starting duel against " + matchedFoundMatchMessage);
            opponentPlayer.sendMessage(ChatColor.YELLOW + "Starting duel against " + playerFoundMatchMessage);
            MatchTeam teamA = new MatchTeam(player.getUniqueId(), Collections.singletonList(player.getUniqueId()), 0);
            MatchTeam teamB = new MatchTeam(opponentPlayer.getUniqueId(), Collections.singletonList(opponentPlayer.getUniqueId()), 1);
            Match match = new Match(arena, kit, type, bestOfThree, teamA, teamB);
            this.plugin.getMatchManager().createMatch(match);
            this.queued.remove(player.getUniqueId());
            this.queued.remove(opponentPlayer.getUniqueId());
            this.playerQueueTime.remove(player.getUniqueId());
            return true;
        }
    }

    public void removePlayerFromQueue(Player player) {
        QueueEntry entry = this.queued.get(player.getUniqueId());
        this.queued.remove(player.getUniqueId());
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
        player.sendMessage(ChatColor.RED + "You have left the " + entry.getQueueType().getName() + " " + entry.getKitName() + " queue.");
    }

    public void addPartyToQueue(Player leader, Party party, String kitName, QueueType type, boolean bestOfThree) {
        if (type.isRanked() && !this.rankedEnabled) {
            leader.closeInventory();
        } else if (party.getMembers().size() != 2) {
            leader.sendMessage(ChatColor.RED + "There must be at least 2 players in your party to do this.");
            leader.closeInventory();
        } else {
            party.getMembers().stream().map(this.plugin.getPlayerManager()::getPlayerData).forEach(member -> member.setPlayerState(PlayerState.QUEUE));
            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(leader.getUniqueId());
            int elo = type.isRanked() ? playerData.getPartyElo(kitName) : -1;
            this.queued.put(playerData.getUniqueId(), new QueueEntry(type, kitName, bestOfThree, elo, true));
            this.giveQueueItems(leader);
            String unrankedMessage = ChatColor.YELLOW
                + "Your party has been added to the "
                + ChatColor.GREEN
                + "Unranked 2v2 "
                + kitName
                + ChatColor.YELLOW
                + " queue.";
            String rankedMessage = ChatColor.YELLOW
                + "Your party has been added to the "
                + ChatColor.GREEN
                + "Ranked 2v2 "
                + kitName
                + ChatColor.YELLOW
                + " queue with "
                + ChatColor.GREEN
                + elo
                + " elo"
                + ChatColor.YELLOW
                + ".";
            party.broadcast(type.isRanked() ? rankedMessage : unrankedMessage);
            this.playerQueueTime.put(party.getLeader(), System.currentTimeMillis());
            this.findMatch(party, kitName, elo, type, bestOfThree);
        }
    }

    private void findMatch(Party partyA, String kitName, int elo, QueueType type, boolean bestOfThree) {
        if (!this.playerQueueTime.containsKey(partyA.getLeader())) {
            System.out.println("Is not contained found..");
        } else {
            long queueTime = System.currentTimeMillis() - this.playerQueueTime.get(partyA.getLeader());
            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(partyA.getLeader());
            if (playerData == null) {
                System.out.println("Null was found..");
            } else {
                int eloRange = playerData.getEloRange();
                int seconds = Math.round((float)(queueTime / 1000L));
                if (seconds > 5 && type.isRanked()) {
                    eloRange += seconds * 50;
                    if (eloRange >= 1000) {
                        eloRange = 1000;
                    }
                }

                int finalEloRange = eloRange;
                UUID opponent = this.queued
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getKey() != partyA.getLeader())
                    .filter(entry -> this.plugin.getPlayerManager().getPlayerData(entry.getKey()).getPlayerState() == PlayerState.QUEUE)
                    .filter(entry -> entry.getValue().isParty())
                    .filter(entry -> entry.getValue().getQueueType() == type)
                    .filter(entry -> !type.isRanked() || Math.abs(entry.getValue().getElo() - elo) < finalEloRange)
                    .filter(entry -> entry.getValue().getKitName().equals(kitName))
                    .map(Entry::getKey)
                    .findFirst()
                    .orElse(null);
                if (opponent == null) {
                    System.out.println("None found..");
                } else {
                    PlayerData opponentData = this.plugin.getPlayerManager().getPlayerData(opponent);
                    if (opponentData.getPlayerState() != PlayerState.FIGHTING) {
                        if (playerData.getPlayerState() != PlayerState.FIGHTING) {
                            Player leaderA = this.plugin.getServer().getPlayer(partyA.getLeader());
                            Player leaderB = this.plugin.getServer().getPlayer(opponent);
                            Party partyB = this.plugin.getPartyManager().getParty(opponent);
                            Kit kit = this.plugin.getKitManager().getKit(kitName);
                            Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
                            String partyAFoundMatchMessage;
                            String partyBFoundMatchMessage;
                            if (type.isRanked()) {
                                partyAFoundMatchMessage = ChatColor.GREEN
                                    + leaderB.getName()
                                    + "'s Party"
                                    + ChatColor.YELLOW
                                    + " with "
                                    + ChatColor.GREEN
                                    + ""
                                    + this.queued.get(leaderB.getUniqueId()).getElo()
                                    + " elo";
                                partyBFoundMatchMessage = ChatColor.GREEN
                                    + leaderA.getName()
                                    + "'s Party"
                                    + ChatColor.YELLOW
                                    + " with "
                                    + ChatColor.GREEN
                                    + ""
                                    + this.queued.get(leaderA.getUniqueId()).getElo()
                                    + " elo";
                            } else {
                                partyAFoundMatchMessage = ChatColor.GREEN + leaderB.getName() + ChatColor.YELLOW + "'s Party.";
                                partyBFoundMatchMessage = ChatColor.GREEN + leaderA.getName() + ChatColor.YELLOW + "'s Party.";
                            }

                            partyA.broadcast(ChatColor.YELLOW + "Starting duel against " + partyAFoundMatchMessage);
                            partyB.broadcast(ChatColor.YELLOW + "Starting duel against " + partyBFoundMatchMessage);
                            List<UUID> playersA = new ArrayList<>(partyA.getMembers());
                            List<UUID> playersB = new ArrayList<>(partyB.getMembers());
                            MatchTeam teamA = new MatchTeam(leaderA.getUniqueId(), playersA, 0);
                            MatchTeam teamB = new MatchTeam(leaderB.getUniqueId(), playersB, 1);
                            Match match = new Match(arena, kit, type, bestOfThree, teamA, teamB);
                            this.plugin.getMatchManager().createMatch(match);
                            this.queued.remove(partyA.getLeader());
                            this.queued.remove(partyB.getLeader());
                        }
                    }
                }
            }
        }
    }

    public void removePartyFromQueue(Party party) {
        QueueEntry entry = this.queued.get(party.getLeader());
        this.queued.remove(party.getLeader());
        party.members().forEach(this.plugin.getPlayerManager()::sendToSpawnAndReset);
        String type = entry.getQueueType().isRanked() ? "Ranked" : "Unranked";
        party.broadcast(
            ChatColor.GREEN.toString() + ChatColor.BOLD + "[*] " + ChatColor.YELLOW + "You party has left the " + type + " " + entry.getKitName() + " queue."
        );
    }

    public boolean isRankedEnabled() {
        return this.rankedEnabled;
    }

    public void setRankedEnabled(boolean rankedEnabled) {
        this.rankedEnabled = rankedEnabled;
    }
}
