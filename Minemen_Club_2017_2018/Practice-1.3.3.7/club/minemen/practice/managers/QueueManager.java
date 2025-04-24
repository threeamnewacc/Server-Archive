// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.managers;

import java.util.function.Consumer;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.function.Function;
import club.minemen.practice.party.Party;
import club.minemen.practice.arena.Arena;
import club.minemen.practice.kit.Kit;
import java.util.Iterator;
import club.minemen.core.mineman.Mineman;
import club.minemen.practice.match.Match;
import club.minemen.practice.match.MatchTeam;
import java.util.Collections;
import club.minemen.core.rank.Rank;
import club.minemen.core.CorePlugin;
import club.minemen.practice.player.PlayerState;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.queue.QueueType;
import club.minemen.practice.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import club.minemen.practice.Practice;
import club.minemen.practice.queue.QueueEntry;
import java.util.UUID;
import java.util.Map;

public class QueueManager
{
    private final Map<UUID, QueueEntry> queued;
    private final Map<UUID, Long> playerQueueTime;
    private final Practice plugin;
    private boolean rankedEnabled;
    
    public QueueManager() {
        this.queued = new ConcurrentHashMap<UUID, QueueEntry>();
        this.playerQueueTime = new HashMap<UUID, Long>();
        this.plugin = Practice.getInstance();
        this.rankedEnabled = true;
        this.plugin.getServer().getScheduler().runTaskTimer((Plugin)this.plugin, () -> this.queued.forEach((key, value) -> {
            if (value.isParty()) {
                this.findMatch(this.plugin.getPartyManager().getParty(key), value.getKitName(), value.getElo(), value.getQueueType());
            }
            else {
                this.findMatch(this.plugin.getServer().getPlayer(key), value.getKitName(), value.getElo(), value.getQueueType());
            }
        }), 20L, 20L);
    }
    
    public void addPlayerToQueue(final Player player, final PlayerData playerData, final String kitName, final QueueType type) {
        if (type != QueueType.UNRANKED && !this.rankedEnabled) {
            player.sendMessage(CC.RED + "Ranked is currently disabled until the server restarts.");
            player.closeInventory();
            return;
        }
        playerData.setPlayerState(PlayerState.QUEUE);
        final int elo = (type == QueueType.RANKED) ? playerData.getElo(kitName) : ((type == QueueType.PREMIUM) ? playerData.getPremiumElo() : 0);
        final QueueEntry entry = new QueueEntry(type, kitName, elo, false);
        this.queued.put(playerData.getUniqueId(), entry);
        this.giveQueueItems(player);
        player.sendMessage((type != QueueType.UNRANKED) ? (CC.PRIMARY + "You were added to the " + CC.SECONDARY + type.getName() + " " + kitName + CC.PRIMARY + " queue with " + CC.SECONDARY + elo + CC.PRIMARY + " elo.") : (CC.PRIMARY + "You were added to the " + CC.SECONDARY + "Unranked " + kitName + CC.PRIMARY + " queue."));
        this.playerQueueTime.put(player.getUniqueId(), System.currentTimeMillis());
        if (!this.findMatch(player, kitName, elo, type) && type.isRanked()) {
            player.sendMessage(CC.SECONDARY + "Searching in ELO range " + CC.PRIMARY + ((playerData.getEloRange() == -1) ? "Unrestricted" : ("[" + Math.max(elo - playerData.getEloRange() / 2, 0) + " -> " + Math.max(elo + playerData.getEloRange() / 2, 0) + "]")));
        }
    }
    
    private void giveQueueItems(final Player player) {
        player.closeInventory();
        player.getInventory().setContents(this.plugin.getItemManager().getQueueItems());
        player.updateInventory();
    }
    
    public QueueEntry getQueueEntry(final UUID uuid) {
        return this.queued.get(uuid);
    }
    
    public long getPlayerQueueTime(final UUID uuid) {
        return this.playerQueueTime.get(uuid);
    }
    
    public int getQueueSize(final String ladder, final QueueType type) {
        return (int)this.queued.entrySet().stream().filter(entry -> entry.getValue().getQueueType() == type).filter(entry -> entry.getValue().getKitName().equals(ladder)).count();
    }
    
    private boolean findMatch(final Player player, final String kitName, final int elo, final QueueType type) {
        final long queueTime = System.currentTimeMillis() - this.playerQueueTime.get(player.getUniqueId());
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            this.plugin.getLogger().warning(player.getName() + "'s player data is null");
            return false;
        }
        final Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
        int eloRange = mineman.hasRank(Rank.CLUBBER) ? playerData.getEloRange() : -1;
        int pingRange = mineman.hasRank(Rank.PARTYMAN) ? playerData.getPingRange() : -1;
        final int seconds = Math.round((float)(queueTime / 1000L));
        if (seconds > 5 && type != QueueType.UNRANKED) {
            if (pingRange != -1) {
                pingRange += (seconds - 5) * 25;
            }
            if (eloRange != -1) {
                eloRange += seconds * 50;
                if (eloRange >= 3000) {
                    eloRange = 3000;
                }
                else {
                    player.sendMessage(CC.SECONDARY + "Searching in ELO range " + CC.PRIMARY + ((eloRange == -1) ? "Unrestricted" : ("[" + Math.max(elo - eloRange / 2, 0) + " -> " + Math.max(elo + eloRange / 2, 0) + "]")));
                }
            }
        }
        if (eloRange == -1) {
            eloRange = Integer.MAX_VALUE;
        }
        if (pingRange == -1) {
            pingRange = Integer.MAX_VALUE;
        }
        final int ping = 0;
        for (final UUID opponent : this.queued.keySet()) {
            if (opponent == player.getUniqueId()) {
                continue;
            }
            final QueueEntry queueEntry = this.queued.get(opponent);
            if (!queueEntry.getKitName().equals(kitName)) {
                continue;
            }
            if (queueEntry.getQueueType() != type) {
                continue;
            }
            if (queueEntry.isParty()) {
                continue;
            }
            final Player opponentPlayer = this.plugin.getServer().getPlayer(opponent);
            final int eloDiff = Math.abs(queueEntry.getElo() - elo);
            final PlayerData opponentData = this.plugin.getPlayerManager().getPlayerData(opponent);
            if (type.isRanked()) {
                if (eloDiff > eloRange) {
                    continue;
                }
                final Mineman opponentMineman = CorePlugin.getInstance().getPlayerManager().getPlayer(opponent);
                final long opponentQueueTime = System.currentTimeMillis() - this.playerQueueTime.get(opponentPlayer.getUniqueId());
                int opponentEloRange = opponentMineman.hasRank(Rank.CLUBBER) ? opponentData.getEloRange() : -1;
                int opponentPingRange = opponentMineman.hasRank(Rank.PARTYMAN) ? opponentData.getPingRange() : -1;
                final int opponentSeconds = Math.round((float)(opponentQueueTime / 1000L));
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
                final int pingDiff = Math.abs(0 - ping);
                if (type == QueueType.RANKED) {
                    if (pingDiff > opponentPingRange) {
                        continue;
                    }
                    if (pingDiff > pingRange) {
                        continue;
                    }
                }
                else if (type == QueueType.PREMIUM && pingDiff > 50) {
                    continue;
                }
            }
            final Kit kit = this.plugin.getKitManager().getKit(kitName);
            final Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
            String playerFoundMatchMessage;
            String matchedFoundMatchMessage;
            if (type.isRanked()) {
                playerFoundMatchMessage = CC.PRIMARY + "Found " + type.getName().toLowerCase() + " match: " + CC.GREEN + player.getName() + " (" + elo + " elo)" + CC.PRIMARY + " vs. " + CC.RED + opponentPlayer.getName() + " (" + this.queued.get(opponentPlayer.getUniqueId()).getElo() + " elo)";
                matchedFoundMatchMessage = CC.PRIMARY + "Found " + type.getName().toLowerCase() + " match: " + CC.GREEN + opponentPlayer.getName() + " (" + this.queued.get(opponentPlayer.getUniqueId()).getElo() + " elo)" + CC.PRIMARY + " vs. " + CC.RED + player.getName() + " (" + elo + " elo)";
            }
            else {
                playerFoundMatchMessage = CC.PRIMARY + "Found unranked match: " + CC.GREEN + player.getName() + CC.PRIMARY + " vs. " + CC.RED + opponentPlayer.getName();
                matchedFoundMatchMessage = CC.PRIMARY + "Found unranked match: " + CC.GREEN + opponentPlayer.getName() + CC.PRIMARY + " vs. " + CC.RED + player.getName();
            }
            if (type == QueueType.PREMIUM) {
                playerData.setPremiumMatchesPlayed(playerData.getPremiumMatchesPlayed() + 1);
                opponentData.setPremiumMatchesPlayed(opponentData.getPremiumMatchesPlayed() + 1);
            }
            player.sendMessage(playerFoundMatchMessage);
            opponentPlayer.sendMessage(matchedFoundMatchMessage);
            final MatchTeam teamA = new MatchTeam(player.getUniqueId(), Collections.singletonList(player.getUniqueId()), Collections.singletonList(this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()).getMinemanID()), 0);
            final MatchTeam teamB = new MatchTeam(opponentPlayer.getUniqueId(), Collections.singletonList(opponentPlayer.getUniqueId()), Collections.singletonList(this.plugin.getPlayerManager().getPlayerData(opponentPlayer.getUniqueId()).getMinemanID()), 1);
            final Match match = new Match(arena, kit, type, new MatchTeam[] { teamA, teamB });
            this.plugin.getMatchManager().createMatch(match);
            this.queued.remove(player.getUniqueId());
            this.queued.remove(opponentPlayer.getUniqueId());
            this.playerQueueTime.remove(player.getUniqueId());
            return true;
        }
        return false;
    }
    
    public void removePlayerFromQueue(final Player player) {
        final QueueEntry entry = this.queued.get(player.getUniqueId());
        this.queued.remove(player.getUniqueId());
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
        player.sendMessage(CC.PRIMARY + "You were removed from the " + CC.SECONDARY + entry.getQueueType().getName() + " " + entry.getKitName() + CC.PRIMARY + " queue.");
    }
    
    public void addPartyToQueue(final Player leader, final Party party, final String kitName, final QueueType type) {
        if (type.isRanked() && !this.rankedEnabled) {
            leader.sendMessage(CC.RED + "Ranked is currently disabled until the server restarts.");
            leader.closeInventory();
        }
        else if (party.getMembers().size() != 2) {
            leader.sendMessage(CC.RED + "You can only join the queue with 2 players in your party.");
            leader.closeInventory();
        }
        else {
            party.getMembers().stream().map((Function<? super Object, ?>)this.plugin.getPlayerManager()::getPlayerData).forEach(member -> member.setPlayerState(PlayerState.QUEUE));
            final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(leader.getUniqueId());
            final int elo = type.isRanked() ? playerData.getPartyElo(kitName) : -1;
            this.queued.put(playerData.getUniqueId(), new QueueEntry(type, kitName, elo, true));
            this.giveQueueItems(leader);
            party.broadcast(type.isRanked() ? (CC.PRIMARY + "Your party was added to the " + type.getName().toLowerCase() + " " + CC.SECONDARY + kitName + CC.PRIMARY + " queue with " + CC.SECONDARY + elo + CC.PRIMARY + " elo.") : (CC.PRIMARY + "Your party was added to the unranked " + CC.SECONDARY + kitName + CC.PRIMARY + " queue."));
            this.playerQueueTime.put(party.getLeader(), System.currentTimeMillis());
            this.findMatch(party, kitName, elo, type);
        }
    }
    
    private void findMatch(final Party partyA, final String kitName, final int elo, final QueueType type) {
        final long queueTime = System.currentTimeMillis() - this.playerQueueTime.get(partyA.getLeader());
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(partyA.getLeader());
        int eloRange = playerData.getEloRange();
        final int seconds = Math.round((float)(queueTime / 1000L));
        if (seconds > 5 && type.isRanked()) {
            eloRange += seconds * 50;
            if (eloRange >= 1000) {
                eloRange = 1000;
            }
            partyA.broadcast(CC.SECONDARY + "Searching in ELO range " + CC.PRIMARY + "[" + (elo - eloRange / 2) + " -> " + (elo + eloRange / 2) + "]");
        }
        final int finalEloRange = eloRange;
        final UUID opponent = this.queued.entrySet().stream().filter(entry -> entry.getKey() != partyA.getLeader()).filter(entry -> entry.getValue().isParty()).filter(entry -> entry.getValue().getQueueType() == type).filter(entry -> !type.isRanked() || Math.abs(entry.getValue().getElo() - elo) < finalEloRange).filter(entry -> entry.getValue().getKitName().equals(kitName)).map((Function<? super Object, ? extends UUID>)Map.Entry::getKey).findFirst().orElse(null);
        if (opponent == null) {
            return;
        }
        final Player leaderA = this.plugin.getServer().getPlayer(partyA.getLeader());
        final Player leaderB = this.plugin.getServer().getPlayer(opponent);
        final Party partyB = this.plugin.getPartyManager().getParty(opponent);
        final Kit kit = this.plugin.getKitManager().getKit(kitName);
        final Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
        String partyAFoundMatchMessage;
        String partyBFoundMatchMessage;
        if (type.isRanked()) {
            partyAFoundMatchMessage = CC.PRIMARY + "Found ranked match: " + CC.GREEN + leaderA.getName() + "'s party (" + elo + " elo)" + CC.PRIMARY + " vs. " + CC.RED + leaderB.getName() + "'s Party (" + this.queued.get(leaderB.getUniqueId()).getElo() + " elo)";
            partyBFoundMatchMessage = CC.PRIMARY + "Found ranked match: " + CC.GREEN + leaderB.getName() + "'s party (" + this.queued.get(leaderB.getUniqueId()).getElo() + " elo)" + CC.PRIMARY + " vs. " + CC.RED + leaderA.getName() + "'s Party (" + elo + " elo)";
        }
        else {
            partyAFoundMatchMessage = CC.PRIMARY + "Found unranked match: " + CC.GREEN + leaderA.getName() + "'s party" + CC.PRIMARY + " vs. " + CC.RED + leaderB.getName() + "'s party";
            partyBFoundMatchMessage = CC.PRIMARY + "Found unranked match: " + CC.GREEN + leaderB.getName() + "'s party" + CC.PRIMARY + " vs. " + CC.RED + leaderA.getName() + "'s party";
        }
        partyA.broadcast(partyAFoundMatchMessage);
        partyB.broadcast(partyBFoundMatchMessage);
        final List<UUID> playersA = new ArrayList<UUID>(partyA.getMembers());
        final List<UUID> playersB = new ArrayList<UUID>(partyB.getMembers());
        final List<Integer> playerIdsA = new ArrayList<Integer>();
        final List<Integer> playerIdsB = new ArrayList<Integer>();
        playersA.forEach(uuid -> playerIdsA.add(this.plugin.getPlayerManager().getPlayerData(uuid).getMinemanID()));
        playersB.forEach(uuid -> playerIdsB.add(this.plugin.getPlayerManager().getPlayerData(uuid).getMinemanID()));
        final MatchTeam teamA = new MatchTeam(leaderA.getUniqueId(), playersA, playerIdsA, 0);
        final MatchTeam teamB = new MatchTeam(leaderB.getUniqueId(), playersB, playerIdsB, 1);
        final Match match = new Match(arena, kit, type, new MatchTeam[] { teamA, teamB });
        this.plugin.getMatchManager().createMatch(match);
        this.queued.remove(partyA.getLeader());
        this.queued.remove(partyB.getLeader());
    }
    
    public void removePartyFromQueue(final Party party) {
        final QueueEntry entry = this.queued.get(party.getLeader());
        this.queued.remove(party.getLeader());
        party.members().forEach(this.plugin.getPlayerManager()::sendToSpawnAndReset);
        final String type = entry.getQueueType().isRanked() ? "Ranked" : "Unranked";
        party.broadcast(CC.PRIMARY + "Your party was removed from the " + CC.SECONDARY + type + " " + entry.getKitName() + CC.PRIMARY + " queue.");
    }
    
    public boolean isRankedEnabled() {
        return this.rankedEnabled;
    }
    
    public void setRankedEnabled(final boolean rankedEnabled) {
        this.rankedEnabled = rankedEnabled;
    }
}
