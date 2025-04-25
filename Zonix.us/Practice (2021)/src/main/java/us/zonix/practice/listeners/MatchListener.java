package us.zonix.practice.listeners;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import me.maiko.dexter.profile.Profile;
import me.maiko.dexter.util.CC;
import me.maiko.dexter.util.event.PreShutdownEvent;
import net.edater.spigot.EdaterSpigot;
import net.edater.spigot.knockback.KnockbackProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import us.zonix.practice.CustomLocation;
import us.zonix.practice.Practice;
import us.zonix.practice.event.match.MatchEndEvent;
import us.zonix.practice.event.match.MatchRestartEvent;
import us.zonix.practice.event.match.MatchStartEvent;
import us.zonix.practice.events.EventState;
import us.zonix.practice.events.PracticeEvent;
import us.zonix.practice.inventory.InventorySnapshot;
import us.zonix.practice.kit.Kit;
import us.zonix.practice.match.Match;
import us.zonix.practice.match.MatchState;
import us.zonix.practice.match.MatchTeam;
import us.zonix.practice.party.Party;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;
import us.zonix.practice.runnable.MatchRunnable;
import us.zonix.practice.util.Clickable;
import us.zonix.practice.util.EloUtil;
import us.zonix.practice.util.PlayerUtil;

public class MatchListener implements Listener {
    private final Practice plugin = Practice.getInstance();

    @EventHandler
    public void onMatchStart(MatchStartEvent event) {
        Match match = event.getMatch();
        Kit kit = match.getKit();
        if (!kit.isEnabled()) {
            match.broadcast(ChatColor.RED + "This kit is currently disabled.");
            this.plugin.getMatchManager().removeMatch(match);
        } else {
            if (kit.isBuild() || kit.isSpleef()) {
                if (match.getArena().getAvailableArenas().size() <= 0) {
                    match.broadcast(ChatColor.RED + "There are no arenas available at this moment.");
                    this.plugin.getMatchManager().removeMatch(match);
                    return;
                }

                match.setStandaloneArena(match.getArena().getAvailableArena());
                this.plugin.getArenaManager().setArenaMatchUUID(match.getStandaloneArena(), match.getMatchId());
            }

            Set<Player> matchPlayers = new HashSet<>();
            match.getTeams().forEach(team -> team.alivePlayers().forEach(playerx -> {
                    matchPlayers.add(playerx);
                    this.plugin.getMatchManager().removeMatchRequests(playerx.getUniqueId());
                    PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(playerx.getUniqueId());
                    playerx.setAllowFlight(false);
                    playerx.setFlying(false);
                    playerData.setCurrentMatchID(match.getMatchId());
                    playerData.setTeamID(team.getTeamID());
                    playerData.setMissedPots(0);
                    playerData.setLongestCombo(0);
                    playerData.setCombo(0);
                    playerData.setHits(0);
                    PlayerUtil.clearPlayer(playerx);
                    CustomLocation locationA = match.getStandaloneArena() != null ? match.getStandaloneArena().getA() : match.getArena().getA();
                    CustomLocation locationB = match.getStandaloneArena() != null ? match.getStandaloneArena().getB() : match.getArena().getB();
                    playerx.teleport(team.getTeamID() == 1 ? locationA.toBukkitLocation() : locationB.toBukkitLocation());
                    if (kit.isCombo()) {
                        playerx.setMaximumNoDamageTicks(4);
                    }

                    if (!match.isRedrover()) {
                        this.plugin.getMatchManager().giveKits(playerx, kit);
                        playerData.setPlayerState(PlayerState.FIGHTING);
                    } else {
                        this.plugin.getMatchManager().addRedroverSpectator(playerx, match);
                    }
                }));

            for (Player player : matchPlayers) {
                for (Player online : this.plugin.getServer().getOnlinePlayers()) {
                    online.hidePlayer(player);
                    player.hidePlayer(online);
                }
            }

            for (Player player : matchPlayers) {
                for (Player other : matchPlayers) {
                    player.showPlayer(other);
                }
            }

            new MatchRunnable(match).runTaskTimer(this.plugin, 20L, 20L);
        }
    }

    @EventHandler
    public void onMatchRestart(MatchRestartEvent event) {
        Match match = event.getMatch();

        for (MatchTeam team : match.getTeams()) {
            team.revivePlayers();
        }

        Set<Player> matchPlayers = new HashSet<>();
        match.getTeams()
            .forEach(
                teamx -> teamx.alivePlayers()
                        .forEach(
                            playerx -> {
                                matchPlayers.add(playerx);
                                this.plugin.getMatchManager().removeMatchRequests(playerx.getUniqueId());
                                PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(playerx.getUniqueId());
                                this.plugin.getMatchManager().giveKits(playerx, match.getKit());
                                playerx.setAllowFlight(false);
                                playerx.setFlying(false);
                                playerData.setCurrentMatchID(match.getMatchId());
                                playerData.setTeamID(teamx.getTeamID());
                                playerData.setMissedPots(0);
                                playerData.setLongestCombo(0);
                                playerData.setCombo(0);
                                playerData.setHits(0);
                                PlayerUtil.clearPlayer(playerx);
                                playerData.setPlayerState(PlayerState.FIGHTING);
                                this.plugin.getMatchManager().removeDeathSpectator(match, playerx);
                                CustomLocation locationA = match.getStandaloneArena() != null ? match.getStandaloneArena().getA() : match.getArena().getA();
                                CustomLocation locationB = match.getStandaloneArena() != null ? match.getStandaloneArena().getB() : match.getArena().getB();
                                this.plugin
                                    .getServer()
                                    .getScheduler()
                                    .runTaskLater(
                                        this.plugin,
                                        () -> playerx.teleport(teamx.getTeamID() == 1 ? locationA.toBukkitLocation() : locationB.toBukkitLocation()),
                                        5L
                                    );
                            }
                        )
            );

        for (Player player : matchPlayers) {
            for (Player online : this.plugin.getServer().getOnlinePlayers()) {
                online.hidePlayer(player);
                player.hidePlayer(online);
            }
        }

        for (Player player : matchPlayers) {
            for (Player other : matchPlayers) {
                player.showPlayer(other);
            }
        }

        match.broadcast(ChatColor.GREEN + "Starting next round.");
        match.setMatchState(MatchState.RESTARTING);
        match.setCountdown(6);
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> match.spectatorPlayers().forEach(spectator -> {
                for (Player matchPlayer : matchPlayers) {
                    spectator.showPlayer(matchPlayer);
                }
            }), 45L);
    }

    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        KnockbackProfile knockbackProfile = EdaterSpigot.INSTANCE.getKnockbackHandler().getActiveProfile();
        Match match = event.getMatch();
        Clickable winnerClickable = new Clickable(ChatColor.GREEN + "Winner: ");
        Clickable loserClickable = new Clickable(ChatColor.RED + "Loser: ");
        match.broadcast(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
        match.broadcast(ChatColor.GOLD + "Match Results: " + ChatColor.GRAY + "(Clickable Inventories)");
        match.setMatchState(MatchState.ENDING);
        match.setWinningTeamId(event.getWinningTeam().getTeamID());
        match.setCountdown(2);
        if (match.isPartyMatch() && !match.isFFA() && match.getKit().isHcteams()) {
            Party partyOne = this.plugin.getPartyManager().getParty(match.getTeams().get(0).getLeader());
            Party partyTwo = this.plugin.getPartyManager().getParty(match.getTeams().get(1).getLeader());
            partyOne.getBards().parallelStream().forEach(bardUuid -> this.plugin.getPlayerManager().getPlayerData(bardUuid).incrementPlayedBard());
            partyTwo.getBards().parallelStream().forEach(bardUuid -> this.plugin.getPlayerManager().getPlayerData(bardUuid).incrementPlayedBard());
            partyOne.getArchers().parallelStream().forEach(archerUuid -> this.plugin.getPlayerManager().getPlayerData(archerUuid).incrementPlayedArcher());
            partyTwo.getArchers().parallelStream().forEach(archerUuid -> this.plugin.getPlayerManager().getPlayerData(archerUuid).incrementPlayedArcher());
            event.getLosingTeam().players().forEach(player -> player.setKnockbackProfile(knockbackProfile));
            event.getWinningTeam().players().forEach(player -> player.setKnockbackProfile(knockbackProfile));
        }

        if (match.isFFA()) {
            Player winner = this.plugin.getServer().getPlayer(event.getWinningTeam().getAlivePlayers().get(0));
            event.getWinningTeam()
                .players()
                .forEach(
                    player -> {
                        player.setKnockbackProfile(knockbackProfile);
                        if (!match.hasSnapshot(player.getUniqueId())) {
                            match.addSnapshot(player);
                        }

                        if (player.getUniqueId() == winner.getUniqueId()) {
                            winnerClickable.add(
                                ChatColor.GRAY + player.getName() + " ",
                                ChatColor.GRAY + "Click to view inventory",
                                "/inventory " + match.getSnapshot(player.getUniqueId()).getSnapshotId()
                            );
                        } else {
                            loserClickable.add(
                                ChatColor.GRAY + player.getName() + " ",
                                ChatColor.GRAY + "Click to view inventory",
                                "/inventory " + match.getSnapshot(player.getUniqueId()).getSnapshotId()
                            );
                        }
                    }
                );

            for (InventorySnapshot snapshot : match.getSnapshots().values()) {
                this.plugin.getInventoryManager().addSnapshot(snapshot);
            }

            match.broadcast(winnerClickable);
            match.broadcast(loserClickable);
            match.broadcast(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
        } else if (match.isRedrover()) {
            match.broadcast(ChatColor.GREEN + event.getWinningTeam().getLeaderName() + ChatColor.GRAY + " has won the redrover!");
            event.getLosingTeam().players().forEach(player -> player.setKnockbackProfile(knockbackProfile));
            event.getWinningTeam().players().forEach(player -> player.setKnockbackProfile(knockbackProfile));
        } else {
            Map<UUID, InventorySnapshot> inventorySnapshotMap = new LinkedHashMap<>();
            match.getTeams()
                .forEach(
                    team -> team.players()
                            .forEach(
                                player -> {
                                    if (!match.hasSnapshot(player.getUniqueId())) {
                                        match.addSnapshot(player);
                                    }

                                    player.setKnockbackProfile(knockbackProfile);
                                    Profile profile = Profile.getByUuid(player.getUniqueId());
                                    boolean onWinningTeam = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()).getTeamID()
                                        == event.getWinningTeam().getTeamID();
                                    if (profile != null) {
                                        if (match.getType().isRanked() && onWinningTeam) {
                                            profile.awardCoins(player, 35);
                                            player.sendMessage(CC.PRIMARY + "You have earned 35 coins for playing a rank match!");
                                        } else if (match.getType().isRanked() && !onWinningTeam) {
                                            profile.awardCoins(player, 10);
                                            player.sendMessage(CC.PRIMARY + "You have earned 10 coins for competing in a ranked match!");
                                        }
                                    }

                                    if (profile != null) {
                                        if (!match.getType().isRanked() && onWinningTeam) {
                                            profile.awardCoins(player, 10);
                                            player.sendMessage(CC.PRIMARY + "You have earned 10 coins for playing a unranked match!");
                                        } else if (!match.getType().isRanked() && !onWinningTeam) {
                                            profile.awardCoins(player, 5);
                                            player.sendMessage(CC.PRIMARY + "You have earned 5 coins for competing in a unranked match!");
                                        }
                                    }

                                    inventorySnapshotMap.put(player.getUniqueId(), match.getSnapshot(player.getUniqueId()));
                                    if (onWinningTeam) {
                                        winnerClickable.add(
                                            ChatColor.GRAY + player.getName() + " ",
                                            ChatColor.GRAY + "Click to view inventory",
                                            "/inventory " + match.getSnapshot(player.getUniqueId()).getSnapshotId()
                                        );
                                    } else {
                                        loserClickable.add(
                                            ChatColor.GRAY + player.getName() + " ",
                                            ChatColor.GRAY + "Click to view inventory",
                                            "/inventory " + match.getSnapshot(player.getUniqueId()).getSnapshotId()
                                        );
                                    }

                                    player.setMaximumNoDamageTicks(20);
                                }
                            )
                );

            for (InventorySnapshot snapshot : match.getSnapshots().values()) {
                this.plugin.getInventoryManager().addSnapshot(snapshot);
            }

            match.broadcast(winnerClickable);
            match.broadcast(loserClickable);
            String kitName = match.getKit().getName();
            Player winnerLeader = this.plugin.getServer().getPlayer(event.getWinningTeam().getPlayers().get(0));
            PlayerData winnerLeaderData = this.plugin.getPlayerManager().getPlayerData(winnerLeader.getUniqueId());
            Player loserLeader = this.plugin.getServer().getPlayer(event.getLosingTeam().getPlayers().get(0));
            PlayerData loserLeaderData = this.plugin.getPlayerManager().getPlayerData(loserLeader.getUniqueId());
            if (match.getType().isBoth()) {
                int[] preElo = new int[2];
                int[] newElo = new int[2];
                int winnerElo = 0;
                int loserElo = 0;
                int newWinnerElo = 0;
                int newLoserElo = 0;
                String eloMessage;
                if (event.getWinningTeam().getPlayers().size() == 2) {
                    UUID winnerUUID = Bukkit.getPlayer(event.getWinningTeam().getLeader()) == null
                        ? event.getWinningTeam().getPlayers().get(0)
                        : event.getWinningTeam().getLeader();
                    Player winnerMember = this.plugin.getServer().getPlayer(winnerUUID);
                    PlayerData winnerMemberData = this.plugin.getPlayerManager().getPlayerData(winnerMember.getUniqueId());
                    UUID loserUUID = Bukkit.getPlayer(event.getLosingTeam().getLeader()) == null
                        ? event.getLosingTeam().getPlayers().get(0)
                        : event.getLosingTeam().getLeader();
                    Player loserMember = this.plugin.getServer().getPlayer(loserUUID);
                    PlayerData loserMemberData = this.plugin.getPlayerManager().getPlayerData(loserMember.getUniqueId());
                    winnerElo = winnerMemberData.getPartyElo(kitName);
                    loserElo = loserMemberData.getPartyElo(kitName);
                    preElo[0] = winnerElo;
                    preElo[1] = loserElo;
                    newWinnerElo = EloUtil.getNewRating(winnerElo, loserElo, true);
                    newLoserElo = EloUtil.getNewRating(loserElo, winnerElo, false);
                    newElo[0] = newWinnerElo;
                    newElo[1] = newLoserElo;
                    winnerMemberData.setPartyElo(kitName, newWinnerElo);
                    loserMemberData.setPartyElo(kitName, newLoserElo);
                    winnerLeaderData.incrementRanked();
                    loserLeaderData.incrementRanked();
                    eloMessage = ChatColor.GRAY
                        + "Elo Changes: "
                        + ChatColor.GREEN
                        + winnerLeader.getName()
                        + ", "
                        + winnerMember.getName()
                        + " +"
                        + (newWinnerElo - winnerElo)
                        + " ("
                        + newWinnerElo
                        + ") "
                        + ChatColor.RED
                        + loserLeader.getName()
                        + ", "
                        + loserMember.getName()
                        + "  "
                        + (newLoserElo - loserElo)
                        + " ("
                        + newLoserElo
                        + ")";
                } else {
                    winnerElo = winnerLeaderData.getElo(kitName);
                    loserElo = loserLeaderData.getElo(kitName);
                    preElo[0] = winnerElo;
                    preElo[1] = loserElo;
                    newWinnerElo = EloUtil.getNewRating(winnerElo, loserElo, true);
                    newLoserElo = EloUtil.getNewRating(loserElo, winnerElo, false);
                    newElo[0] = newWinnerElo;
                    newElo[1] = newLoserElo;
                    winnerLeaderData.incrementRanked();
                    loserLeaderData.incrementRanked();
                    eloMessage = ChatColor.GRAY
                        + "Elo Changes: "
                        + ChatColor.GREEN
                        + winnerLeader.getName()
                        + " +"
                        + (newWinnerElo - winnerElo)
                        + " ("
                        + newWinnerElo
                        + ") "
                        + ChatColor.RED
                        + loserLeader.getName()
                        + " "
                        + (newLoserElo - loserElo)
                        + " ("
                        + newLoserElo
                        + ")";
                    winnerLeaderData.setElo(kitName, newWinnerElo);
                    loserLeaderData.setElo(kitName, newLoserElo);
                    winnerLeaderData.setWins(kitName, winnerLeaderData.getWins(kitName) + 1);
                    loserLeaderData.setLosses(kitName, loserLeaderData.getLosses(kitName) + 1);
                }

                match.broadcast(eloMessage);
            }

            winnerLeaderData.incrementUnrankedWins();
            winnerLeaderData.incrementUnranked();
            loserLeaderData.incrementUnranked();
            match.broadcast(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
            this.plugin.getMatchManager().saveRematches(match);
        }
    }

    @EventHandler
    void onPotionSplash(PotionSplashEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player shooter = (Player)event.getEntity().getShooter();
            if (shooter.isSprinting() && event.getIntensity(shooter) > 0.5) {
                event.setIntensity(shooter, 1.0);
            }
        }
    }

    @EventHandler
    public void onPreStopEvent(PreShutdownEvent event) {
        for (Match match : Practice.getInstance().getMatchManager().getMatches().values()) {
            match.setMatchState(MatchState.ENDING);
            match.setCountdown(3);
        }

        for (PracticeEvent practiceEvent : Practice.getInstance().getEventManager().getEvents().values()) {
            if (practiceEvent.getState() == EventState.STARTED) {
                practiceEvent.end();
            }
        }

        for (Integer tournamentId : Practice.getInstance().getTournamentManager().getTournaments().keySet()) {
            Practice.getInstance().getTournamentManager().removeTournament(tournamentId, true);
        }
    }
}
