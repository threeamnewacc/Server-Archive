package us.zonix.practice.board.adapter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import me.maiko.dexter.Dexter;
import me.maiko.dexter.profile.Profile;
import me.maiko.dexter.rank.Rank;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import us.zonix.practice.Practice;
import us.zonix.practice.board.Board;
import us.zonix.practice.board.BoardAdapter;
import us.zonix.practice.bots.ZonixBot;
import us.zonix.practice.events.PracticeEvent;
import us.zonix.practice.match.Match;
import us.zonix.practice.match.MatchTeam;
import us.zonix.practice.party.Party;
import us.zonix.practice.player.EloRank;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;
import us.zonix.practice.pvpclasses.pvpclasses.BardClass;
import us.zonix.practice.queue.QueueEntry;
import us.zonix.practice.settings.item.ProfileOptionsItemState;
import us.zonix.practice.tournament.Tournament;
import us.zonix.practice.util.PlayerUtil;
import us.zonix.practice.util.StatusCache;
import us.zonix.practice.util.TimeUtil;
import us.zonix.practice.util.TimeUtils;

public class PracticeBoard implements BoardAdapter {
    private static final String LINE = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------";
    private final Practice plugin = Practice.getInstance();

    @Override
    public String getTitle(Player player) {
        return ChatColor.translateAlternateColorCodes('&', "&4&lZonix &7┃ &fNA");
    }

    @Override
    public void preLoop() {
    }

    @Override
    public List<String> getScoreboard(Player player, Board board) {
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            this.plugin.getLogger().warning(player.getName() + "'s player data is null");
            return null;
        } else if (playerData.getOptions().getScoreboard() == ProfileOptionsItemState.DISABLED) {
            return null;
        } else {
            switch (playerData.getPlayerState()) {
                case LOADING:
                case EDITING:
                case SPAWN:
                case EVENT:
                    return this.getLobbyBoard(player, false);
                case QUEUE:
                    return this.getLobbyBoard(player, true);
                case FIGHTING:
                    return this.getGameBoard(player);
                case SPECTATING:
                    return this.getSpectatorBoard(player);
                case FFA:
                    return this.getFFABoard(player);
                case TRAINING:
                    return this.getTrainingBoard(player);
                default:
                    return null;
            }
        }
    }

    private List<String> getTrainingBoard(Player player) {
        List<String> strings = new LinkedList<>();
        if (this.plugin.getBotManager().isTraining(player)) {
            ZonixBot zonixBot = this.plugin.getBotManager().getBotFromPlayer(player);
            if (zonixBot != null) {
                strings.add(LINE);
                strings.add("&cKit&f: &7" + zonixBot.getKit().getName());
                strings.add("&cDifficulty&f: &7" + StringUtils.capitalize(zonixBot.getBotDifficulty().name().toLowerCase()));
                strings.add(LINE);
            }
        }

        return strings;
    }

    private List<String> getLobbyBoard(Player player, boolean queuing) {
        List<String> strings = new LinkedList<>();
        strings.add(LINE);
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        PracticeEvent event = this.plugin.getEventManager().getEventPlaying(player);
        if (this.plugin.getEventManager().getSpectators().containsKey(player.getUniqueId())) {
            event = this.plugin.getEventManager().getSpectators().get(player.getUniqueId());
        }

        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (event == null) {
            strings.add("&cOnline&7: &f" + Bukkit.getOnlinePlayers().size());
            strings.add("&cFighting&7: &f" + StatusCache.getInstance().getFighting());
            if (System.currentTimeMillis() < this.plugin.getEventManager().getCooldown()) {
                strings.add("&cCooldown&7: &f" + TimeUtil.convertToFormat(this.plugin.getEventManager().getCooldown()));
            }
        }

        if (queuing) {
            strings.add(LINE);
            QueueEntry queueEntry = party == null
                ? this.plugin.getQueueManager().getQueueEntry(player.getUniqueId())
                : this.plugin.getQueueManager().getQueueEntry(party.getLeader());
            strings.add("&4In Queue" + (queueEntry.isBestOfThree() ? " &7(Best of 5)" : ""));
            strings.add("&c * &f" + queueEntry.getKitName() + " " + queueEntry.getQueueType().getName());
        }

        if (party != null) {
            strings.add(LINE);
            strings.add("&4Party &7(" + party.getMembers().size() + " Player" + (party.getMembers().size() == 1 ? "" : "s") + ")");
            strings.add(" &c* &fLeader&7: " + Bukkit.getPlayer(party.getLeader()).getName());
        }

        if (event != null) {
            strings.add(ChatColor.DARK_RED + "Event " + ChatColor.GRAY + "(" + event.getName() + ")");
            strings.addAll(event.getScoreboardLines(player));
        }

        if (playerData.getPlayerState() != PlayerState.EVENT && this.plugin.getTournamentManager().getTournaments().size() >= 1) {
            for (Tournament tournament : this.plugin.getTournamentManager().getTournaments().values()) {
                strings.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");
                strings.add(ChatColor.DARK_RED + "Tournament " + ChatColor.GRAY + "[" + tournament.getTeamSize() + "v" + tournament.getTeamSize() + "]");
                strings.add(" &c* &fLadder§7: " + tournament.getKitName());
                strings.add(" &c* &fStage§7: Round #" + tournament.getCurrentRound());
                strings.add(" &c* &fPlayers§7: " + tournament.getPlayers().size() + "/" + tournament.getSize());
                strings.add(" &c* &fID§7: " + tournament.getId());
                int countdown = tournament.getCountdown();
                if (countdown > 0 && countdown <= 30) {
                    strings.add(ChatColor.RED.toString() + ChatColor.BOLD + " * " + ChatColor.RED + "Starting§7: " + ChatColor.WHITE + countdown + "s");
                }
            }
        }

        if (player.hasMetadata("modmode")) {
            strings.add(ChatColor.DARK_RED + "Silent Mode");
        }

        if (Dexter.getInstance().getShutdownTask() != null) {
            strings.add(ChatColor.RED.toString() + "Reboot§7: " + ChatColor.WHITE + Dexter.getInstance().getShutdownTask().getSecondsUntilShutdown() + "s");
        }

        strings.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");
        return strings;
    }

    @Override
    public void onScoreboardCreate(Player player, Scoreboard scoreboard) {
        Team red = scoreboard.getTeam("red");
        if (red == null) {
            red = scoreboard.registerNewTeam("red");
        }

        Team green = scoreboard.getTeam("green");
        if (green == null) {
            green = scoreboard.registerNewTeam("green");
        }

        for (Rank rank : Dexter.getInstance().getRankManager().getRanks()) {
            Team rankTeam = scoreboard.getTeam(rank.getId());
            if (rankTeam == null) {
                rankTeam = scoreboard.registerNewTeam(rank.getId());
            }

            rankTeam.setPrefix(rank.getGameColor());
        }

        red.setPrefix(ChatColor.RED.toString());
        green.setPrefix(ChatColor.GREEN.toString());
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getPlayerState() != PlayerState.FIGHTING) {
            for (String entry : red.getEntries()) {
                red.removeEntry(entry);
            }

            for (String entry : green.getEntries()) {
                green.removeEntry(entry);
            }

            for (Rank rank : Dexter.getInstance().getRankManager().getRanks()) {
                Team rankTeam = scoreboard.getTeam(rank.getId());

                for (Player online : Bukkit.getOnlinePlayers()) {
                    Profile onlineProfile = Profile.getByUuidIfAvailable(online.getUniqueId());
                    if (onlineProfile != null && onlineProfile.getRank() == rank && !rankTeam.hasEntry(online.getName())) {
                        rankTeam.addEntry(online.getName());
                    }
                }
            }
        } else {
            Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());

            for (MatchTeam team : match.getTeams()) {
                for (UUID teamUUID : team.getAlivePlayers()) {
                    Player teamPlayer = this.plugin.getServer().getPlayer(teamUUID);
                    if (teamPlayer != null) {
                        String teamPlayerName = teamPlayer.getName();
                        if (team.getTeamID() == playerData.getTeamID() && !match.isFFA()) {
                            if (!green.hasEntry(teamPlayerName)) {
                                green.addEntry(teamPlayerName);
                            }
                        } else if (!red.hasEntry(teamPlayerName)) {
                            red.addEntry(teamPlayerName);
                        }
                    }
                }
            }

            if (playerData.getPlayerState() != PlayerState.FIGHTING) {
                for (Rank rank : Dexter.getInstance().getRankManager().getRanks()) {
                    Team rankTeam = scoreboard.getTeam(rank.getId());

                    for (Player onlinex : Bukkit.getOnlinePlayers()) {
                        Profile onlineProfile = Profile.getByUuidIfAvailable(onlinex.getUniqueId());
                        if (onlineProfile != null && onlineProfile.getRank() == rank && !rankTeam.hasEntry(onlinex.getName())) {
                            rankTeam.addEntry(onlinex.getName());
                        }
                    }
                }
            }
        }
    }

    private List<String> getGameBoard(Player player) {
        List<String> strings = new LinkedList<>();
        Match match = null;
        if (this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()).getPlayerState() == PlayerState.SPECTATING) {
            match = this.plugin.getMatchManager().getSpectatingMatch(player.getUniqueId());
        } else if (this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()).getPlayerState() == PlayerState.FIGHTING) {
            match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
        }

        if (match == null) {
            strings.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");
            strings.add(ChatColor.RED.toString() + "Finding match info...");
            strings.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");
            return strings;
        } else {
            strings.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");
            strings.add(ChatColor.RED.toString() + "Ladder§7: " + ChatColor.WHITE + (match.getKit() == null ? "Unknown" : match.getKit().getName()));
            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            Player opponentPlayer = null;
            if (!match.isPartyMatch() && !match.isFFA()) {
                opponentPlayer = match.getTeams().get(0).getPlayers().get(0) == player.getUniqueId()
                    ? this.plugin.getServer().getPlayer(match.getTeams().get(1).getPlayers().get(0))
                    : this.plugin.getServer().getPlayer(match.getTeams().get(0).getPlayers().get(0));
                if (opponentPlayer == null) {
                    return this.getLobbyBoard(player, false);
                }

                MatchTeam opposingTeam = match.getTeams().get(0).getPlayers().get(0) == player.getUniqueId()
                    ? match.getTeams().get(1)
                    : match.getTeams().get(0);
                MatchTeam playerTeam = match.getTeams().get(0).getPlayers().get(0) == player.getUniqueId() ? match.getTeams().get(0) : match.getTeams().get(1);
                strings.add(ChatColor.RED.toString() + "Opponent§7: " + ChatColor.RESET.toString() + ChatColor.WHITE + opponentPlayer.getName());
                if (match.isBestOfThree()) {
                    strings.add(ChatColor.RED.toString() + "Your Wins§7: " + ChatColor.WHITE + playerTeam.getMatchWins());
                    strings.add(ChatColor.RED.toString() + "Opponent Wins§7: " + ChatColor.WHITE + opposingTeam.getMatchWins());
                }
            } else if (match.isPartyMatch() && !match.isFFA()) {
                MatchTeam opposingTeam = match.isFFA()
                    ? match.getTeams().get(0)
                    : (playerData.getTeamID() == 0 ? match.getTeams().get(1) : match.getTeams().get(0));
                MatchTeam playerTeam = match.getTeams().get(playerData.getTeamID());
                strings.add(ChatColor.GREEN.toString() + "Your Team§7: " + ChatColor.WHITE + playerTeam.getAlivePlayers().size() + " alive");
                strings.add(ChatColor.DARK_RED.toString() + "Opponent§7: " + ChatColor.WHITE + opposingTeam.getAlivePlayers().size() + " alive");
                if (match.isBestOfThree()) {
                    strings.add(ChatColor.RED.toString() + "Team Wins§7: " + ChatColor.WHITE + playerTeam.getMatchWins());
                    strings.add(ChatColor.RED.toString() + "Opponent Wins§7: " + ChatColor.WHITE + opposingTeam.getMatchWins());
                }

                if (match.getKit().isHcteams()
                    && this.plugin.getPartyManager().getParty(player.getUniqueId()) != null
                    && this.plugin.getPartyManager().getParty(player.getUniqueId()).getBards().contains(player.getUniqueId())
                    && playerTeam.getAlivePlayers().contains(player.getUniqueId())) {
                    strings.add(
                        ChatColor.RED.toString()
                            + "Bard Energy§7: "
                            + ChatColor.RESET.toString()
                            + ChatColor.WHITE
                            + BardClass.getEnergy().get(player.getName())
                    );
                }
            } else if (match.isFFA()) {
                int alive = match.getTeams().get(0).getAlivePlayers().size() - 1;
                strings.add(
                    ChatColor.RED.toString()
                        + "Remaining§7: "
                        + ChatColor.WHITE
                        + match.getTeams().get(0).getAlivePlayers().size()
                        + " player"
                        + (alive == 1 ? "" : "s")
                );
            }

            if (opponentPlayer != null && !match.isPartyMatch() && !match.isFFA() && match.getType().isBoth()) {
                PlayerData opponentData = this.plugin.getPlayerManager().getPlayerData(opponentPlayer.getUniqueId());
                if (opponentData != null) {
                    String[] oppEloRank = EloRank.getRankByElo(opponentData.getElo(match.getKit().getName())).name().split("_");
                    String oppEloRankText = StringUtils.capitalize(oppEloRank[0].toLowerCase()) + " " + oppEloRank[1];
                    strings.add(ChatColor.RED.toString() + "Elo§7: " + ChatColor.WHITE + oppEloRankText);
                }
            }

            if (playerData != null
                && playerData.getOptions().getScoreboard() == ProfileOptionsItemState.SHOW_PING
                && opponentPlayer != null
                && !match.isPartyMatch()
                && !match.isFFA()) {
                PlayerData opponentData = this.plugin.getPlayerManager().getPlayerData(opponentPlayer.getUniqueId());
                if (opponentData != null) {
                    strings.add(" ");
                    strings.add(
                        ChatColor.RED.toString()
                            + "Ping§7: "
                            + ChatColor.GREEN
                            + PlayerUtil.getPing(player)
                            + "ms"
                            + ChatColor.GRAY
                            + " ┃ "
                            + ChatColor.RED
                            + PlayerUtil.getPing(opponentPlayer)
                            + "ms"
                    );
                }
            }

            if (match.getStartTime() != null) {
                String duration = TimeUtils.formatLongIntoMMSS(ChronoUnit.SECONDS.between(match.getStartTime().toInstant(), Instant.now()));
                strings.add(ChatColor.RED.toString() + "Duration§7: " + ChatColor.WHITE + duration);
            }

            strings.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");
            return strings;
        }
    }

    private List<String> getSpectatorBoard(Player spectator) {
        List<String> strings = new LinkedList<>();
        Match match = this.plugin.getMatchManager().getSpectatingMatch(spectator.getUniqueId());
        if (match == null) {
            PracticeEvent<?> practiceEvent = this.plugin.getEventManager().getSpectatingEvent(spectator.getUniqueId());
            if (practiceEvent == null) {
                strings.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");
                strings.add(ChatColor.RED.toString() + "Finding event info...");
                strings.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");
                return strings;
            } else {
                strings.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");
                strings.add(ChatColor.DARK_RED + "Event " + ChatColor.GRAY + "(" + practiceEvent.getName() + ")");
                strings.addAll(practiceEvent.getScoreboardSpectator(spectator));
                strings.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");
                return strings;
            }
        } else {
            strings.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");
            strings.add(ChatColor.RED.toString() + "Ladder§7: " + ChatColor.WHITE + (match.getKit() == null ? "Unknown" : match.getKit().getName()));
            strings.add(" ");
            Player player = null;
            Player opponentPlayer = null;
            if (!match.isPartyMatch() && !match.isFFA()) {
                player = this.plugin.getServer().getPlayer(match.getTeams().get(0).getPlayers().get(0));
                opponentPlayer = this.plugin.getServer().getPlayer(match.getTeams().get(1).getPlayers().get(0));
                strings.add(
                    Profile.getByUuidIfAvailable(player.getUniqueId()).getRank().getGameColor()
                        + player.getName()
                        + ChatColor.RED
                        + " ("
                        + PlayerUtil.getPing(player)
                        + "ms)"
                );
                strings.add(ChatColor.RED + "vs");
                strings.add(
                    Profile.getByUuidIfAvailable(opponentPlayer.getUniqueId()).getRank().getGameColor()
                        + opponentPlayer.getName()
                        + ChatColor.RED
                        + " ("
                        + PlayerUtil.getPing(opponentPlayer)
                        + "ms)"
                );
            } else if (match.isPartyMatch() && !match.isFFA()) {
                player = this.plugin.getServer().getPlayer(match.getTeams().get(0).getPlayers().get(0));
                opponentPlayer = this.plugin.getServer().getPlayer(match.getTeams().get(1).getPlayers().get(0));
                strings.add(
                    Profile.getByUuidIfAvailable(player.getUniqueId()).getRank().getGameColor()
                        + player.getName()
                        + "'s Team"
                        + ChatColor.RED
                        + " ("
                        + PlayerUtil.getPing(player)
                        + "ms)"
                );
                strings.add(ChatColor.RED + "vs");
                strings.add(
                    Profile.getByUuidIfAvailable(opponentPlayer.getUniqueId()).getRank().getGameColor()
                        + opponentPlayer.getName()
                        + "'s Team"
                        + ChatColor.RED
                        + " ("
                        + PlayerUtil.getPing(opponentPlayer)
                        + "ms)"
                );
            } else if (match.isFFA()) {
                int alive = match.getTeams().get(0).getAlivePlayers().size() - 1;
                strings.add(
                    ChatColor.RED.toString()
                        + "Remaining§7: "
                        + ChatColor.WHITE
                        + match.getTeams().get(0).getAlivePlayers().size()
                        + " player"
                        + (alive == 1 ? "" : "s")
                );
            }

            if (match.getStartTime() != null) {
                strings.add(" ");
                String duration = TimeUtils.formatLongIntoMMSS(ChronoUnit.SECONDS.between(match.getStartTime().toInstant(), Instant.now()));
                strings.add(ChatColor.RED.toString() + "Duration§7: " + ChatColor.WHITE + duration);
            }

            strings.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");
            return strings;
        }
    }

    private List<String> getFFABoard(Player player) {
        List<String> strings = new LinkedList<>();
        strings.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");
        strings.add(ChatColor.RED.toString() + "Players§7: " + ChatColor.WHITE + this.plugin.getFfaManager().getTotalPlaying());
        strings.add(
            ChatColor.RED.toString() + "Kills§7: " + ChatColor.WHITE + this.plugin.getFfaManager().getKillStreakTracker().getOrDefault(player.getUniqueId(), 0)
        );
        strings.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");
        return strings;
    }

    @Override
    public long getInterval() {
        return 1L;
    }
}
