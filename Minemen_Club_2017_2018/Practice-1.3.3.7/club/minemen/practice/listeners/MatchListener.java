// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.listeners;

import club.minemen.practice.match.MatchTeam;
import java.util.Map;
import club.minemen.practice.queue.QueueType;
import club.minemen.practice.util.EloUtil;
import java.util.LinkedHashMap;
import club.minemen.practice.inventory.InventorySnapshot;
import java.util.UUID;
import club.minemen.practice.match.MatchState;
import club.minemen.core.clickable.Clickable;
import club.minemen.practice.event.match.MatchEndEvent;
import org.bukkit.event.EventHandler;
import club.minemen.core.util.CustomLocation;
import club.minemen.practice.player.PlayerData;
import java.util.Iterator;
import java.util.Set;
import club.minemen.practice.kit.Kit;
import club.minemen.practice.match.Match;
import org.bukkit.plugin.Plugin;
import club.minemen.practice.runnable.MatchRunnable;
import club.minemen.practice.player.PlayerState;
import club.minemen.practice.util.PlayerUtil;
import org.bukkit.entity.Player;
import java.util.HashSet;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.event.match.MatchStartEvent;
import club.minemen.practice.Practice;
import org.bukkit.event.Listener;

public class MatchListener implements Listener
{
    private final Practice plugin;
    
    public MatchListener() {
        this.plugin = Practice.getInstance();
    }
    
    @EventHandler
    public void onMatchStart(final MatchStartEvent event) {
        final Match match = event.getMatch();
        final Kit kit = match.getKit();
        if (!kit.isEnabled()) {
            match.broadcast(CC.RED + "This kit is currently disabled, try another kit.");
            this.plugin.getMatchManager().removeMatch(match);
            return;
        }
        if (kit.isBuild() || kit.isSpleef()) {
            if (match.getArena().getAvailableArenas().size() <= 0) {
                match.broadcast(CC.RED + "There are no arenas available.");
                this.plugin.getMatchManager().removeMatch(match);
                return;
            }
            match.setStandaloneArena(match.getArena().getAvailableArena());
            this.plugin.getArenaManager().setArenaMatchUUID(match.getStandaloneArena(), match.getMatchId());
        }
        final Set<Player> matchPlayers = new HashSet<Player>();
        Player player = null;
        match.getTeams().forEach(team -> team.alivePlayers().forEach(player -> {
            matchPlayers.add(player);
            this.plugin.getMatchManager().removeMatchRequests(player.getUniqueId());
            final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            player.setAllowFlight(false);
            player.setFlying(false);
            playerData.setCurrentMatchID(match.getMatchId());
            playerData.setTeamID(team.getTeamID());
            playerData.setMissedPots(0);
            playerData.setLongestCombo(0);
            playerData.setCombo(0);
            playerData.setHits(0);
            PlayerUtil.clearPlayer(player);
            final CustomLocation locationA = (match.getStandaloneArena() != null) ? match.getStandaloneArena().getA() : match.getArena().getA();
            final CustomLocation locationB = (match.getStandaloneArena() != null) ? match.getStandaloneArena().getB() : match.getArena().getB();
            player.teleport((team.getTeamID() == 1) ? locationA.toBukkitLocation() : locationB.toBukkitLocation());
            if (kit.isCombo()) {
                player.setMaximumNoDamageTicks(3);
            }
            if (!match.isRedrover()) {
                this.plugin.getMatchManager().giveKits(player, kit);
                playerData.setPlayerState(PlayerState.FIGHTING);
            }
            else {
                this.plugin.getMatchManager().addRedroverSpectator(player, match);
            }
        }));
        final Iterator<Player> iterator = matchPlayers.iterator();
        while (iterator.hasNext()) {
            player = iterator.next();
            for (final Player online : this.plugin.getServer().getOnlinePlayers()) {
                online.hidePlayer(player);
                player.hidePlayer(online);
            }
        }
        final Iterator<Player> iterator3 = matchPlayers.iterator();
        while (iterator3.hasNext()) {
            player = iterator3.next();
            for (final Player other : matchPlayers) {
                player.showPlayer(other);
            }
        }
        new MatchRunnable(match).runTaskTimer((Plugin)this.plugin, 20L, 20L);
    }
    
    @EventHandler
    public void onMatchEnd(final MatchEndEvent event) {
        final Match match = event.getMatch();
        final Clickable inventories = new Clickable(CC.PRIMARY + "Inventories: ");
        match.setMatchState(MatchState.ENDING);
        match.setWinningTeamId(event.getWinningTeam().getTeamID());
        match.setCountdown(4);
        if (match.isFFA()) {
            final Player winner = this.plugin.getServer().getPlayer((UUID)event.getWinningTeam().getAlivePlayers().get(0));
            final String winnerMessage = CC.PRIMARY + "Winner: " + CC.SECONDARY + winner.getName();
            event.getWinningTeam().players().forEach(player -> {
                if (!match.hasSnapshot(player.getUniqueId())) {
                    match.addSnapshot(player);
                }
                inventories.add(((player.getUniqueId() == winner.getUniqueId()) ? CC.GREEN : CC.RED) + player.getName() + " ", CC.PRIMARY + "View Inventory", "/inv " + match.getSnapshot(player.getUniqueId()).getSnapshotId());
                return;
            });
            for (final InventorySnapshot snapshot : match.getSnapshots().values()) {
                this.plugin.getInventoryManager().addSnapshot(snapshot);
            }
            match.broadcast(winnerMessage);
            match.broadcast(inventories);
        }
        else if (match.isRedrover()) {
            match.broadcast(CC.SECONDARY + event.getWinningTeam().getLeaderName() + CC.PRIMARY + " has won the redrover!");
        }
        else {
            final Map<UUID, InventorySnapshot> inventorySnapshotMap = new LinkedHashMap<UUID, InventorySnapshot>();
            match.getTeams().forEach(team -> team.players().forEach(player -> {
                if (!match.hasSnapshot(player.getUniqueId())) {
                    match.addSnapshot(player);
                }
                inventorySnapshotMap.put(player.getUniqueId(), match.getSnapshot(player.getUniqueId()));
                final boolean onWinningTeam = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()).getTeamID() == event.getWinningTeam().getTeamID();
                inventories.add((onWinningTeam ? CC.GREEN : CC.RED) + player.getName() + " ", CC.PRIMARY + "View inventory", "/inv " + match.getSnapshot(player.getUniqueId()).getSnapshotId());
            }));
            for (final InventorySnapshot snapshot2 : match.getSnapshots().values()) {
                this.plugin.getInventoryManager().addSnapshot(snapshot2);
            }
            final String winnerMessage = CC.PRIMARY + (match.isParty() ? "Winning Team: " : "Winner: ") + CC.SECONDARY + event.getWinningTeam().getLeaderName();
            match.broadcast(winnerMessage);
            match.broadcast(inventories);
            if (match.getType().isRanked()) {
                final String kitName = match.getKit().getName();
                final Player winnerLeader = this.plugin.getServer().getPlayer((UUID)event.getWinningTeam().getPlayers().get(0));
                final PlayerData winnerLeaderData = this.plugin.getPlayerManager().getPlayerData(winnerLeader.getUniqueId());
                final Player loserLeader = this.plugin.getServer().getPlayer((UUID)event.getLosingTeam().getPlayers().get(0));
                final PlayerData loserLeaderData = this.plugin.getPlayerManager().getPlayerData(loserLeader.getUniqueId());
                final int[] preElo = new int[2];
                final int[] newElo = new int[2];
                String eloMessage;
                if (event.getWinningTeam().getPlayers().size() == 2) {
                    final Player winnerMember = this.plugin.getServer().getPlayer((UUID)event.getWinningTeam().getPlayers().get(1));
                    final PlayerData winnerMemberData = this.plugin.getPlayerManager().getPlayerData(winnerMember.getUniqueId());
                    final Player loserMember = this.plugin.getServer().getPlayer((UUID)event.getLosingTeam().getPlayers().get(1));
                    final PlayerData loserMemberData = this.plugin.getPlayerManager().getPlayerData(loserMember.getUniqueId());
                    final int winnerElo = winnerLeaderData.getPartyElo(kitName);
                    final int loserElo = loserLeaderData.getPartyElo(kitName);
                    preElo[0] = winnerElo;
                    preElo[1] = loserElo;
                    final int newWinnerElo = EloUtil.getNewRating(winnerElo, loserElo, true);
                    final int newLoserElo = EloUtil.getNewRating(loserElo, winnerElo, false);
                    newElo[0] = newWinnerElo;
                    newElo[1] = newLoserElo;
                    winnerMemberData.setPartyElo(kitName, newWinnerElo);
                    loserMemberData.setPartyElo(kitName, newLoserElo);
                    eloMessage = CC.AQUA + "Updated Elo: " + CC.GREEN + winnerLeader.getName() + ", " + winnerMember.getName() + " " + newWinnerElo + " (+" + (newWinnerElo - winnerElo) + ") " + CC.RED + loserLeader.getName() + ", " + loserMember.getName() + " " + newLoserElo + " (" + (newLoserElo - loserElo) + ")";
                }
                else {
                    int winnerElo;
                    int loserElo;
                    if (match.getType() == QueueType.RANKED) {
                        winnerElo = winnerLeaderData.getElo(kitName);
                        loserElo = loserLeaderData.getElo(kitName);
                    }
                    else {
                        winnerElo = winnerLeaderData.getPremiumElo();
                        loserElo = loserLeaderData.getPremiumElo();
                    }
                    preElo[0] = winnerElo;
                    preElo[1] = loserElo;
                    final int newWinnerElo = EloUtil.getNewRating(winnerElo, loserElo, true);
                    final int newLoserElo = EloUtil.getNewRating(loserElo, winnerElo, false);
                    newElo[0] = newWinnerElo;
                    newElo[1] = newLoserElo;
                    eloMessage = CC.AQUA + "Updated Elo: " + CC.GREEN + winnerLeader.getName() + " " + newWinnerElo + " (+" + (newWinnerElo - winnerElo) + ") " + CC.RED + loserLeader.getName() + " " + newLoserElo + " (" + (newLoserElo - loserElo) + ")";
                    if (match.getType() == QueueType.RANKED) {
                        winnerLeaderData.setElo(kitName, newWinnerElo);
                        loserLeaderData.setElo(kitName, newLoserElo);
                        winnerLeaderData.setWins(kitName, winnerLeaderData.getWins(kitName) + 1);
                        loserLeaderData.setLosses(kitName, loserLeaderData.getLosses(kitName) + 1);
                    }
                    else {
                        winnerLeaderData.setPremiumElo(newWinnerElo);
                        loserLeaderData.setPremiumElo(newLoserElo);
                        winnerLeaderData.setPremiumWins(winnerLeaderData.getPremiumWins() + 1);
                        loserLeaderData.setPremiumLosses(loserLeaderData.getPremiumLosses() + 1);
                    }
                }
                match.broadcast(eloMessage);
            }
            this.plugin.getMatchManager().saveRematches(match);
        }
    }
}
