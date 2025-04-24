// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.board;

import java.util.function.Function;
import org.bukkit.inventory.ItemStack;
import java.util.function.Predicate;
import java.util.Objects;
import java.util.Arrays;
import org.bukkit.Material;
import club.minemen.practice.util.MathUtil;
import club.minemen.practice.match.Match;
import java.util.Iterator;
import org.bukkit.scoreboard.Team;
import java.util.UUID;
import club.minemen.practice.match.MatchTeam;
import club.minemen.practice.player.PlayerState;
import org.bukkit.scoreboard.Scoreboard;
import club.minemen.core.mineman.Mineman;
import club.minemen.practice.queue.QueueEntry;
import club.minemen.practice.tournament.Tournament;
import club.minemen.practice.party.Party;
import club.minemen.core.rank.Rank;
import club.minemen.core.CorePlugin;
import club.minemen.practice.queue.QueueType;
import java.util.LinkedList;
import club.minemen.practice.player.PlayerData;
import java.util.List;
import club.minemen.core.board.Board;
import club.minemen.core.util.finalutil.CC;
import org.bukkit.entity.Player;
import club.minemen.practice.Practice;
import club.minemen.core.board.BoardAdapter;

public class PracticeBoard implements BoardAdapter
{
    private final Practice plugin;
    private final String footer;
    private int fighters;
    
    public PracticeBoard() {
        this.plugin = Practice.getInstance();
        this.footer = this.plugin.getMainConfig().getConfig().getString("footer");
    }
    
    public String getTitle(final Player player) {
        return CC.BD_PURPLE + "PRACTICE";
    }
    
    public void preLoop() {
        this.fighters = this.plugin.getMatchManager().getFighters();
    }
    
    public List<String> getScoreboard(final Player player, final Board board) {
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            this.plugin.getLogger().warning(player.getName() + "'s player data is null");
            return null;
        }
        if (!playerData.isScoreboardEnabled()) {
            return null;
        }
        switch (playerData.getPlayerState()) {
            case LOADING:
            case EDITING:
            case FFA:
            case SPAWN:
            case SPECTATING: {
                return this.getLobbyBoard(player, false);
            }
            case QUEUE: {
                return this.getLobbyBoard(player, true);
            }
            case FIGHTING: {
                return this.getGameBoard(player);
            }
            default: {
                return null;
            }
        }
    }
    
    private List<String> getLobbyBoard(final Player player, final boolean queuing) {
        final List<String> strings = new LinkedList<String>();
        strings.add(CC.GRAY + CC.STRIKE_THROUGH + "-------------------");
        strings.add(CC.RESET + "Online: " + CC.PRIMARY + this.plugin.getServer().getOnlinePlayers().size());
        strings.add(CC.RESET + "Playing: " + CC.PRIMARY + this.fighters);
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        final Tournament tournament = this.plugin.getTournamentManager().getTournament(player.getUniqueId());
        if (tournament != null) {
            strings.add(CC.RED + " ");
            strings.add(CC.RESET + "Tournament:");
            strings.add(CC.PRIMARY + tournament.getTeamSize() + "v" + tournament.getTeamSize() + " " + tournament.getKitName());
            strings.add(CC.RESET + "Round: " + CC.PRIMARY + tournament.getCurrentRound());
            strings.add(CC.RESET + "Players: " + CC.PRIMARY + tournament.getPlayers().size() + "/" + CC.PRIMARY + tournament.getSize());
        }
        else if (party != null) {
            strings.add(CC.RED + " ");
            strings.add(CC.RESET + "Party:");
            strings.add(CC.RESET + "Leader: " + CC.PRIMARY + this.plugin.getServer().getPlayer(party.getLeader()).getName());
            strings.add(CC.RESET + "Members: " + CC.PRIMARY + party.getMembers().size());
        }
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (queuing) {
            strings.add(CC.RED + " ");
            final QueueEntry queueEntry = (party == null) ? this.plugin.getQueueManager().getQueueEntry(player.getUniqueId()) : this.plugin.getQueueManager().getQueueEntry(party.getLeader());
            strings.add(CC.RESET + "Queue:");
            strings.add(CC.PRIMARY + queueEntry.getQueueType().getName() + " " + queueEntry.getKitName());
            if (queueEntry.getQueueType() != QueueType.UNRANKED) {
                final Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
                final long queueTime = System.currentTimeMillis() - ((party == null) ? this.plugin.getQueueManager().getPlayerQueueTime(player.getUniqueId()) : this.plugin.getQueueManager().getPlayerQueueTime(party.getLeader()));
                int eloRange = mineman.hasRank(Rank.CLUBBER) ? playerData.getEloRange() : -1;
                final int seconds = Math.round((float)(queueTime / 1000L));
                if (seconds > 5 && eloRange != -1) {
                    eloRange += seconds * 50;
                    if (eloRange >= 3000) {
                        eloRange = 3000;
                    }
                }
                if (queueEntry.getQueueType() == QueueType.RANKED) {
                    final int elo = playerData.getElo(queueEntry.getKitName());
                }
                else {
                    final int elo = playerData.getPremiumElo();
                }
                strings.add(CC.RESET + "ELO range:");
                int elo;
                strings.add(CC.PRIMARY + ((eloRange == -1) ? "Unrestricted" : ("[" + Math.max(elo - eloRange / 2, 0) + " -> " + Math.max(elo + eloRange / 2, 0) + "]")));
            }
        }
        else {
            strings.add(CC.RESET + " ");
            final int maxMatches = this.plugin.getPlayerManager().getPremiumMatches(player.getUniqueId());
            if (maxMatches == 1337) {
                strings.add(CC.RESET + "Premium Matches:");
                strings.add(CC.PRIMARY + "Unlimited");
            }
            else {
                strings.add(CC.RESET + "Premium Matches: " + CC.PRIMARY + playerData.getPremiumMatches());
            }
        }
        strings.add(CC.RED + " ");
        strings.add(CC.PRIMARY + ((this.footer != null) ? this.footer : "minemen.club"));
        strings.add(CC.GRAY + CC.STRIKE_THROUGH + "-------------------");
        return strings;
    }
    
    public void onScoreboardCreate(final Player player, final Scoreboard scoreboard) {
        Team red = scoreboard.getTeam("red");
        if (red == null) {
            red = scoreboard.registerNewTeam("red");
        }
        Team green = scoreboard.getTeam("green");
        if (green == null) {
            green = scoreboard.registerNewTeam("green");
        }
        red.setPrefix(CC.RED);
        green.setPrefix(CC.GREEN);
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getPlayerState() != PlayerState.FIGHTING) {
            for (final String entry : red.getEntries()) {
                red.removeEntry(entry);
            }
            for (final String entry : green.getEntries()) {
                green.removeEntry(entry);
            }
            return;
        }
        final Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
        for (final MatchTeam team : match.getTeams()) {
            for (final UUID teamUUID : team.getAlivePlayers()) {
                final Player teamPlayer = this.plugin.getServer().getPlayer(teamUUID);
                if (teamPlayer != null) {
                    final String teamPlayerName = teamPlayer.getName();
                    if (team.getTeamID() == playerData.getTeamID() && !match.isFFA()) {
                        if (green.hasEntry(teamPlayerName)) {
                            continue;
                        }
                        green.addEntry(teamPlayerName);
                    }
                    else {
                        if (red.hasEntry(teamPlayerName)) {
                            continue;
                        }
                        red.addEntry(teamPlayerName);
                    }
                }
            }
        }
    }
    
    private List<String> getGameBoard(final Player player) {
        final List<String> strings = new LinkedList<String>();
        strings.add(CC.GRAY + CC.STRIKE_THROUGH + "-------------------");
        final Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
        strings.add(CC.RESET + "Ladder: " + CC.PRIMARY + match.getKit().getName());
        Player opponentPlayer = null;
        if (!match.isParty() && !match.isFFA()) {
            opponentPlayer = ((match.getTeams().get(0).getPlayers().get(0) == player.getUniqueId()) ? this.plugin.getServer().getPlayer((UUID)match.getTeams().get(1).getPlayers().get(0)) : this.plugin.getServer().getPlayer((UUID)match.getTeams().get(0).getPlayers().get(0)));
            if (opponentPlayer == null) {
                return this.getLobbyBoard(player, false);
            }
            strings.add(CC.RESET + "Opponent: " + CC.PRIMARY + opponentPlayer.getName());
        }
        else if (match.isParty() && !match.isFFA()) {
            final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            final MatchTeam opposingTeam = match.isFFA() ? match.getTeams().get(0) : ((playerData.getTeamID() == 0) ? match.getTeams().get(1) : match.getTeams().get(0));
            final MatchTeam playerTeam = match.getTeams().get(playerData.getTeamID());
            if (opposingTeam.getPlayers().size() == 2 && playerTeam.getPlayers().size() == 2) {
                final Player teammate = this.plugin.getServer().getPlayer((playerTeam.getPlayers().get(0) == player.getUniqueId()) ? playerTeam.getPlayers().get(1) : playerTeam.getPlayers().get(0));
                strings.add(CC.GOLD + " ");
                strings.add(CC.RESET + "Teammates:");
                if (teammate != null) {
                    if (playerTeam.getAlivePlayers().contains(teammate.getUniqueId())) {
                        strings.add(CC.RESET + " " + teammate.getName() + CC.PRIMARY + " (" + MathUtil.roundToHalves(teammate.getHealth() / 2.0) + " \u2764)");
                        boolean potionMatch = false;
                        boolean soupMatch = false;
                        for (final ItemStack item : match.getKit().getContents()) {
                            if (item != null) {
                                if (item.getType() == Material.MUSHROOM_SOUP) {
                                    soupMatch = true;
                                    break;
                                }
                                if (item.getType() == Material.POTION && item.getDurability() == 16421) {
                                    potionMatch = true;
                                    break;
                                }
                            }
                        }
                        if (potionMatch) {
                            final int potCount = (int)Arrays.stream(teammate.getInventory().getContents()).filter(Objects::nonNull).map((Function<? super ItemStack, ?>)ItemStack::getDurability).filter(d -> d == 16421).count();
                            strings.add(" " + CC.PRIMARY + potCount + " pots");
                        }
                        else if (soupMatch) {
                            final int soupCount = (int)Arrays.stream(teammate.getInventory().getContents()).filter(Objects::nonNull).map((Function<? super ItemStack, ?>)ItemStack::getType).filter(d -> d == Material.MUSHROOM_SOUP).count();
                            strings.add(" " + CC.PRIMARY + soupCount + " soups");
                        }
                    }
                    else {
                        strings.add(CC.RESET + " " + teammate.getName() + CC.D_RED + " (\u2718)");
                    }
                }
                if (opposingTeam.getAlivePlayers().size() > 0) {
                    strings.add(CC.RESET + "Opponents: ");
                    for (final UUID opponent : opposingTeam.getAlivePlayers()) {
                        strings.add(CC.PRIMARY + " " + this.plugin.getServer().getPlayer(opponent).getName());
                    }
                }
            }
            else {
                strings.add(CC.RESET + "Teammates: " + CC.PRIMARY + playerTeam.getAlivePlayers().size());
                strings.add(CC.RESET + "Opponents: " + CC.PRIMARY + opposingTeam.getAlivePlayers().size());
            }
        }
        else if (match.isFFA()) {
            strings.add(CC.RESET + "Opponents: " + CC.PRIMARY + (match.getTeams().get(0).getAlivePlayers().size() - 1));
        }
        strings.add(CC.RED + " ");
        strings.add(CC.GOLD + " ");
        strings.add(CC.PRIMARY + this.footer);
        strings.add(CC.GRAY + CC.STRIKE_THROUGH + "-------------------");
        return strings;
    }
    
    public long getInterval() {
        return 20L;
    }
}
