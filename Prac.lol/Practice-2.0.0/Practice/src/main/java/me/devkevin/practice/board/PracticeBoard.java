package me.devkevin.practice.board;

import club.inverted.chatcolor.CC;
import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.BoardAdapter;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import lombok.Getter;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.Practice;
import me.devkevin.practice.PracticeCache;
import me.devkevin.practice.PracticeLang;
import me.devkevin.practice.hcf.classes.Archer;
import me.devkevin.practice.hcf.classes.Bard;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchTeam;
import me.devkevin.practice.match.timer.impl.EnderpearlTimer;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.state.ProfileState;
import me.devkevin.practice.queue.QueueEntry;
import me.devkevin.practice.queue.QueueType;
import me.devkevin.practice.tournament.Tournament;
import me.devkevin.practice.util.Animation;
import me.devkevin.practice.util.MathUtil;
import me.devkevin.practice.util.PlayerUtil;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

/**
 * Copyright 10/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class PracticeBoard implements BoardAdapter {

    /**
     * Don't fucking change anything of the boards cause u can fuck up that nigger I had 5 hours to figure it out well
     */

    @Getter private final Practice plugin = Practice.getInstance();

    public String getTitle(Player player) {
        return CC.GOLD + "Practice " + CC.GRAY + "[East]";
    }

    @Override
    public List<String> getScoreboard(Player player, Board board, Set<BoardCooldown> cooldowns) {
        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

        if (profile == null) {
            this.plugin.getLogger().warning(player.getName() + "'s player data is null");
            return null;
        }

        if (!profile.getOptions().isScoreboard())  {
            return null;
        }

        final List<String> strings = new ArrayList<>();

        // spawn board
        if (profile.isInSpawn()) {
            strings.add(PracticeLang.line);
            strings.add(CC.YELLOW + "Online: " + CC.GOLD +  PracticeCache.getInstance().getOnlinePlayers() + CC.GRAY + " (" + PracticeCache.getInstance().getQueueingPlayers() + ")");
            strings.add(CC.YELLOW + "Playing: " + CC.GOLD + PracticeCache.getInstance().getPlayingPlayers());

            strings.add("");

            if (profile.getState() != ProfileState.EVENT && this.plugin.getTournamentManager().getTournaments().size() >= 1) {

                for (Tournament tournament : this.plugin.getTournamentManager().getTournaments().values()) {
                    strings.add(CC.YELLOW + "Tournament " + CC.GRAY + tournament.getTeamSize() + "v" + tournament.getTeamSize());
                    strings.add(CC.GRAY + "● " + CC.YELLOW + " Ladder: " + CC.GOLD + tournament.getKitName());
                    strings.add(CC.GRAY + "● " + CC.YELLOW + " Stage: " + CC.GOLD + tournament.getCurrentRound() + CC.YELLOW + " Round");
                    strings.add(CC.GRAY + "● " + CC.YELLOW + " Playing: " + CC.GOLD + tournament.getPlayers().size() + "/" + tournament.getSize());

                    int countdown = tournament.getCountdown();

                    if (countdown > 0 && countdown <= 30) {
                        strings.add(CC.GRAY + "● " + CC.YELLOW + " Starting: " + CC.GOLD + countdown + CC.R + "'s");
                    }
                    strings.add("");
                }
            }

            Animation animation = Animation.getAnimation(player.getUniqueId(), "footer");
            if (animation != null) {
                strings.add(CC.translate(animation.getLine()));
            } else {
                strings.add(CC.GRAY + CC.I + "inverted.club");
            }
            strings.add(PracticeLang.line);
        }

        // queue board
        if (profile.isInQueue()) {
            QueueEntry queueEntry = party == null ? plugin.getQueue().getQueueEntry(player.getUniqueId()) :
                    plugin.getQueue().getQueueEntry(party.getLeader());
            strings.add(PracticeLang.line);
            strings.add(CC.YELLOW + "Online: " + CC.GOLD +  PracticeCache.getInstance().getOnlinePlayers() + CC.GRAY + " (" + PracticeCache.getInstance().getQueueingPlayers() + ")");
            strings.add(CC.YELLOW + "Playing: " + CC.GOLD + PracticeCache.getInstance().getPlayingPlayers());
            strings.add("");
            strings.add(CC.YELLOW + "Queue:");
            strings.add(CC.GOLD + queueEntry.getQueueType().getName() + " " + queueEntry.getKitName());

            if (queueEntry.getQueueType() != QueueType.UN_RANKED) {
                CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

                long queueTime = System.currentTimeMillis() -
                        (party == null ? this.plugin.getQueue().getPlayerQueueTime(player
                                .getUniqueId())
                                : this.plugin.getQueue().getPlayerQueueTime(party.getLeader()));

                int eloRange = coreProfile.hasRank(Rank.BASIC) ? profile.getEloRange() : -1;
                int seconds = Math.round(queueTime / 1000L);
                if (seconds > 5) {
                    if (eloRange != -1) {
                        eloRange += seconds * 50;
                        if (eloRange >= 3000) {
                            eloRange = 3000;
                        }
                    }
                }

                int elo;
                if (queueEntry.getQueueType() == QueueType.RANKED) {
                    elo = profile.getElo(queueEntry.getKitName());

                    strings.add(CC.YELLOW + "ELO range:");
                    strings.add(CC.GRAY + (eloRange == -1 ? "Unrestricted" :
                            "[" + Math.max(elo - eloRange / 2, 0) +
                                    " -> " + Math.max(elo + eloRange / 2, 0) + "]"));
                }
            }

            strings.add("");
            Animation animation = Animation.getAnimation(player.getUniqueId(), "footer");
            if (animation != null) {
                strings.add(CC.translate(animation.getLine()));
            } else {
                strings.add(CC.GRAY + CC.I + "inverted.club");
            }
            strings.add(PracticeLang.line);
        }

        // match board
        if (profile.isFighting()) {
            Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
            Player opponent = null;

            if (match == null) {
                strings.add("");
                strings.add(CC.GRAY + "Finding match " + match.getInfoAnimation());
                strings.add("");
            }

            if (!match.isFFA() && !match.isParty()) {
                opponent = match.getTeams().get(0).getPlayers().get(0) == player.getUniqueId()
                        ? this.plugin.getServer().getPlayer(match.getTeams().get(1).getPlayers().get(0))
                        : this.plugin.getServer().getPlayer(match.getTeams().get(0).getPlayers().get(0));

                if (opponent == null) {
                    // no board
                    return null;
                }

                if (match.isStarting()) {
                    strings.add(PracticeLang.line);
                    strings.add(CC.YELLOW + "Kit: " + CC.GOLD + match.getKit().getName());
                    strings.add(CC.YELLOW + "Arena id: " + CC.GOLD + match.getArena().getName());
                    strings.add("");
                    strings.add(CC.YELLOW + "Starting in " + CC.GOLD + match.getCountdown());
                    strings.add("");
                    Animation animation = Animation.getAnimation(player.getUniqueId(), "footer");
                    if (animation != null) {
                        strings.add(CC.translate(animation.getLine()));
                    } else {
                        strings.add("inverted.club");
                    }
                    strings.add(PracticeLang.line);
                }

                if (match.isFighting()) {
                    if (match.getKit().isBoxing()) {
                        MatchTeam playerTeam = match.getTeams().get(profile.getTeamID());
                        MatchTeam enemyTeam = playerTeam == match.getTeams().get(0) ? match.getTeams().get(1) : match.getTeams().get(0);
                        Profile opponentPlayerData = this.plugin.getProfileManager().getProfileData(enemyTeam.getPlayers().get(0));

                        int yourHits = profile.getHits(), theirHits = opponentPlayerData.getHits();
                        int yourCombos = profile.getCombo(), theirCombos = opponentPlayerData.getCombo();

                        strings.add(PracticeLang.line);
                        strings.add(CC.YELLOW + "Hits: " + (((yourHits - theirHits < theirHits - yourHits ? "&c(" + (yourHits - theirHits) + ")" : "&a(+" + (yourHits - theirHits) + ")"))) + ((yourCombos < 2 && theirCombos < 2) ? "" : yourCombos > theirCombos ? " &a" + yourCombos + " Combo" : " &c" + theirCombos + " Combo"));
                        strings.add(CC.YELLOW + " You: " + CC.GOLD + yourHits);
                        strings.add(CC.YELLOW + " Them: " + CC.GOLD + theirHits);
                        strings.add("");
                        strings.add(CC.YELLOW + "Your Ping: " + CC.GOLD + PlayerUtil.getPing(player) + " ms");
                        strings.add(CC.YELLOW + "Their Ping: " + CC.GOLD + PlayerUtil.getPing(opponent) + " ms");
                        strings.add("");
                    }
                    else {
                        strings.add(PracticeLang.line);
                        strings.add(CC.YELLOW + "Your Ping: " + CC.GOLD + PlayerUtil.getPing(player) + " ms");
                        strings.add(CC.YELLOW + "Their Ping: " + CC.GOLD + PlayerUtil.getPing(opponent) + " ms");
                        strings.add("");
                    }

                    Animation animation = Animation.getAnimation(player.getUniqueId(), "footer");
                    if (animation != null) {
                        strings.add(CC.translate(animation.getLine()));
                    } else {
                        strings.add(CC.GRAY + CC.I + "inverted.club");
                    }
                    strings.add(PracticeLang.line);
                }

                if (match.isEnding()) {
                    strings.add(PracticeLang.line);
                    strings.add(CC.GRAY + "Match Ended.");
                    strings.add("");
                    Animation animation = Animation.getAnimation(player.getUniqueId(), "footer");
                    if (animation != null) {
                        strings.add(CC.translate(animation.getLine()));
                    } else {
                        strings.add(CC.GRAY + CC.I + "inverted.club");
                    }
                    strings.add(PracticeLang.line);
                }
            }
            else if (match.isParty() && !match.isFFA()) {
                MatchTeam opposingTeam = match.isFFA() ? match.getTeams().get(0) : (profile.getTeamID() == 0 ? match.getTeams().get(1) : match.getTeams().get(0));
                MatchTeam playerTeam = match.getTeams().get(profile.getTeamID());

                //This is how we know it's a 4v4 or not
                if (opposingTeam.getPlayers().size() == 4 && playerTeam.getPlayers().size() == 4) {
                    Player teammate = this.plugin.getServer().getPlayer(
                            playerTeam.getPlayers().get(0) == player.getUniqueId() ? playerTeam.getPlayers().get(1) :
                                    playerTeam.getPlayers().get(0));
                    strings.add(PracticeLang.line);
                    strings.add(CC.YELLOW + "Teammates:");
                    if (teammate != null) {
                        if (playerTeam.getAlivePlayers().contains(teammate.getUniqueId())) {
                            strings.add(CC.GOLD + " " + teammate.getName() + CC.RED + " (" +
                                    MathUtil.roundToHalves(teammate.getHealth() / 2.0D) + " ❤)");

                            boolean potionMatch = false;
                            boolean soupMatch = false;

                            for (ItemStack item : match.getKit().getContents()) {
                                if (item == null) {
                                    continue;
                                }
                                if (item.getType() == Material.MUSHROOM_SOUP) {
                                    soupMatch = true;
                                    break;
                                } else if (item.getType() == Material.POTION && item.getDurability() == (short) 16421) {
                                    potionMatch = true;
                                    break;
                                }
                            }

                            if (potionMatch) {
                                int potCount = (int) Arrays.stream(teammate.getInventory().getContents()).filter(Objects::nonNull)
                                        .map(ItemStack::getDurability).filter(d -> d == 16421).count();

                                if (potCount <= 3) {
                                    strings.add(CC.translate(" &5" + potCount + " &fpots"));
                                } else if (potCount <= 8) {
                                    strings.add(CC.translate(" &5" + potCount + " &fpots"));
                                } else if (potCount <= 12) {
                                    strings.add(CC.translate(" &6" + potCount + " &fpots"));
                                } else if (potCount <= 20) {
                                    strings.add(CC.translate(" &e" + potCount + " &fpots"));
                                }
                            } else if (soupMatch) {
                                int soupCount = (int) Arrays.stream(teammate.getInventory().getContents()).filter(Objects::nonNull).map(ItemStack::getType)
                                        .filter(d -> d == Material.MUSHROOM_SOUP).count();

                                strings.add(" " + CC.YELLOW + soupCount + " soups");
                            }
                        } else {
                            strings.add(CC.GOLD + " " + teammate.getName() + CC.D_RED + " (✘)");
                        }
                    }
                    if (opposingTeam.getAlivePlayers().size() > 0) {
                        strings.add(PracticeLang.line);
                        strings.add(CC.YELLOW + "Opponents: ");
                        for (UUID opponent2 : opposingTeam.getAlivePlayers()) {
                            strings.add(LandCore.getInstance().getProfileManager().getProfile(opponent2).getGrant().getRank().getColor() + " " + this.plugin.getServer().getPlayer(opponent2).getName());
                        }
                        strings.add("");

                        if (match.isPartyMatch()) {
                            if (match.getKit().getName().equalsIgnoreCase("HCF")) {

                                strings.add(CC.AQUA + "Class: " + CC.GRAY + getClassName(player.getUniqueId()));

                                if (!hasExpiredEnderCooldown(player)) {
                                    strings.add(CC.translate("&5EnderPearl: " + CC.GRAY + getEnderPearlRemain(player)));
                                }

                                if (getClassName(player.getUniqueId()).equalsIgnoreCase("Bard")) {

                                    strings.add(CC.translate("&eEnergy: " + CC.GRAY + Bard.getEnergy(player)));
                                }

                                strings.add("");
                            }
                        }

                        Animation animation = Animation.getAnimation(player.getUniqueId(), "footer");
                        if (animation != null) {
                            strings.add(CC.translate(animation.getLine()));
                        } else {
                            strings.add(CC.GRAY + CC.I + "inverted.club");
                        }
                        strings.add(PracticeLang.line);
                    }
                } else {
                    //Not a 4v4, don't fuck up the board just show the amount of opponents
                    strings.add(PracticeLang.line);
                    strings.add(CC.YELLOW + "Teammates: " + CC.GOLD + playerTeam.getAlivePlayers().size() + "/" + playerTeam.getAlivePlayers().size());
                    strings.add(CC.YELLOW + "Opponents: " + CC.GOLD + opposingTeam.getAlivePlayers().size() + "/" + opposingTeam.getAlivePlayers().size());
                    strings.add("");

                    if (match.isPartyMatch()) {
                        if (match.getKit().getName().equalsIgnoreCase("HCF")) {

                            strings.add(CC.AQUA + "Class: " + CC.GRAY + getClassName(player.getUniqueId()));

                            if (!hasExpiredEnderCooldown(player)) {
                                strings.add(CC.translate("&5EnderPearl: " + CC.GRAY + getEnderPearlRemain(player)));
                            }

                            if (getClassName(player.getUniqueId()).equalsIgnoreCase("Bard")) {

                                strings.add(CC.translate("&eEnergy: " + CC.GRAY + Bard.getEnergy(player)));
                            }

                            strings.add("");
                        }
                    }

                    Animation animation = Animation.getAnimation(player.getUniqueId(), "footer");
                    if (animation != null) {
                        strings.add(CC.translate(animation.getLine()));
                    } else {
                        strings.add(CC.GRAY + CC.I + "inverted.club");
                    }
                    strings.add(PracticeLang.line);
                }
            } else if (match.isFFA()) {
                strings.add(PracticeLang.line);
                strings.add(CC.YELLOW + "Opponents: " + CC.GOLD + (match.getTeams().get(0).getAlivePlayers().size() - 1));
                strings.add("");

                if (match.isPartyMatch()) {
                    if (match.getKit().getName().equalsIgnoreCase("HCF")) {

                        strings.add(CC.AQUA + "Class: " + CC.GRAY + getClassName(player.getUniqueId()));

                        if (!hasExpiredEnderCooldown(player)) {
                            strings.add(CC.translate("&5EnderPearl: " + CC.GRAY + getEnderPearlRemain(player)));
                        }

                        if (getClassName(player.getUniqueId()).equalsIgnoreCase("Bard")) {

                            strings.add(CC.translate("&eEnergy: " + CC.GRAY + Bard.getEnergy(player)));
                        }

                        strings.add("");
                    }
                }

                Animation animation = Animation.getAnimation(player.getUniqueId(), "footer");
                if (animation != null) {
                    strings.add(CC.translate(animation.getLine()));
                } else {
                    strings.add(CC.GRAY + CC.I + "inverted.club");
                }
                strings.add(PracticeLang.line);
            }
        }

        // party board
        if (profile.isInParty()) {
            if (party != null) {
                strings.add(PracticeLang.line);
                strings.add(CC.YELLOW + "Online: " + CC.GOLD +  PracticeCache.getInstance().getOnlinePlayers() + CC.GRAY + " (" + PracticeCache.getInstance().getQueueingPlayers() + ")");
                strings.add(CC.YELLOW + "Playing: " + CC.GOLD + PracticeCache.getInstance().getPlayingPlayers());

                strings.add("");

                strings.add("");
                strings.add(CC.YELLOW + "Party " + CC.GOLD + "(" + party.getMembers().size() + " Player" + (party.getMembers().size() == 1 ? "" : "s") + ")");
                strings.add(" " + CC.GRAY + "● " + " " + CC.YELLOW + "Leader: " + LandCore.getInstance().getProfileManager().getProfile(party.getLeader()).getGrant().getRank().getColor() + Bukkit.getPlayer(party.getLeader()).getName());

                Animation animation = Animation.getAnimation(player.getUniqueId(), "footer");
                if (animation != null) {
                    strings.add(CC.translate(animation.getLine()));
                } else {
                    strings.add(CC.GRAY + CC.I + "inverted.club");
                }
                strings.add(PracticeLang.line);
            }
        }

        // spectating board
        if (profile.isSpectating()) {
            Match match = this.plugin.getMatchManager().getSpectatingMatch(player.getUniqueId());

            strings.add(PracticeLang.line);
            strings.add(CC.YELLOW + "Kit: " + CC.GOLD + (match.getKit() == null ? "Unknown" : match.getKit().getName()));
            if (match.isStarting()) {
                strings.add("");
                strings.add(CC.translate(CC.YELLOW + "Starting in " + CC.GOLD + match.getCountdown()));
                strings.add("");
            } else {
                strings.add("");
                strings.add(CC.YELLOW + "Duration: " + CC.GOLD + match.getDuration());
                strings.add("");
            }

            Player playerSpectate;
            Player opponentPlayerSpectate;

            if (!match.isPartyMatch() && match.isFFA()) {
                playerSpectate = this.plugin.getServer().getPlayer(match.getTeams().get(0).getPlayers().get(0));
                opponentPlayerSpectate = this.plugin.getServer().getPlayer(match.getTeams().get(1).getPlayers().get(0));

                strings.add(LandCore.getInstance().getProfileManager().getProfile(playerSpectate.getUniqueId()).getGrant().getRank().getColor() + playerSpectate.getName() + CC.RED + "(" + PlayerUtil.getPing(playerSpectate) + "ms");
                strings.add(CC.GOLD + "vs");
                strings.add(LandCore.getInstance().getProfileManager().getProfile(opponentPlayerSpectate.getUniqueId()).getGrant().getRank().getColor() + opponentPlayerSpectate.getName() + CC.RED + "(" + PlayerUtil.getPing(opponentPlayerSpectate) + "ms");
                strings.add("");
            } else if (match.isPartyMatch() && !match.isFFA()) {
                playerSpectate = this.plugin.getServer().getPlayer(match.getTeams().get(0).getPlayers().get(0));
                opponentPlayerSpectate = this.plugin.getServer().getPlayer(match.getTeams().get(1).getPlayers().get(0));

                strings.add(LandCore.getInstance().getProfileManager().getProfile(playerSpectate.getUniqueId()).getGrant().getRank().getColor() + playerSpectate.getName() + "'s Team " + CC.RED + "(" + PlayerUtil.getPing(playerSpectate) + "ms");
                strings.add(CC.GOLD + "vs");
                strings.add(LandCore.getInstance().getProfileManager().getProfile(opponentPlayerSpectate.getUniqueId()).getGrant().getRank().getColor() + opponentPlayerSpectate.getName() + "'s Team " + CC.RED + "(" + PlayerUtil.getPing(opponentPlayerSpectate) + "ms");
                strings.add("");
            } else if (match.isFFA()) {
                int alive = (match.getTeams().get(0).getAlivePlayers().size() - 1);
                strings.add(CC.YELLOW + "Remaining: " + CC.GOLD + (match.getTeams().get(0).getAlivePlayers().size()) + " player" + (alive == 1 ? "" : "s"));
                strings.add("");
            }

            Animation animation = Animation.getAnimation(player.getUniqueId(), "footer");
            if (animation != null) {
                strings.add(CC.translate(animation.getLine()));
            } else {
                strings.add(CC.GRAY + CC.I + "inverted.club");
            }
            strings.add(PracticeLang.line);
        }

        // event board
        if (profile.isInEvent()) {
            return null;
        }

        // editor board
        if (profile.isEditing()) {
            strings.add(PracticeLang.line);
            strings.add(CC.R + "Your are in Editing state.");
            strings.add("");
            Animation animation = Animation.getAnimation(player.getUniqueId(), "footer");
            if (animation != null) {
                strings.add(CC.translate(animation.getLine()));
            } else {
                strings.add(CC.GRAY + CC.I + "inverted.club");
            }
            strings.add(PracticeLang.line);
        }

        if (profile.isInStaffMode()) {
            strings.add(PracticeLang.line);
            strings.add(CC.translate("&6State&7: &a" + profile.getState()));
            strings.add(CC.translate("&6Online Players&7: &f" + PracticeCache.getInstance().getOnlinePlayers()));
            strings.add(CC.translate("&6Online in Staff Mode&7: &f" + PracticeCache.getInstance().getOnlineStaffModePlayers()));
            strings.add("");
            strings.add(CC.translate("&6Following&7: &f" + (profile.isFollowing() ? Bukkit.getPlayer(profile.getFollowingId()).getName() : "&cNone")));
            strings.add("");
            Animation animation = Animation.getAnimation(player.getUniqueId(), "footer");
            if (animation != null) {
                strings.add(CC.translate(animation.getLine()));
            } else {
                strings.add(CC.GRAY + CC.I + "inverted.club");
            }
            strings.add(PracticeLang.line);
        }
        return strings;
    }


    @Override
    public void onScoreboardCreate(Player player, Scoreboard scoreboard) {
        if (scoreboard != null) {
            Team red = scoreboard.getTeam("red");
            if (red == null) {
                red = scoreboard.registerNewTeam("red");
            }

            Team green = scoreboard.getTeam("green");
            if (green == null) {
                green = scoreboard.registerNewTeam("green");
            }

            Team blue = scoreboard.getTeam("blue");
            if (blue == null) {
                blue = scoreboard.registerNewTeam("blue");
            }

            Team tagged = scoreboard.getTeam("tagged");
            if (tagged == null) {
                tagged = scoreboard.registerNewTeam("tagged");
            }

            red.setPrefix(String.valueOf(ChatColor.RED));
            green.setPrefix(String.valueOf(ChatColor.GREEN));
            blue.setPrefix(String.valueOf(ChatColor.BLUE));
            blue.setPrefix(String.valueOf(ChatColor.YELLOW));

            Profile practicePlayerData = plugin.getProfileManager().getProfileData(player.getUniqueId());
            if (practicePlayerData.getState() != ProfileState.FIGHTING) {
                Objective objective = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);
                if (objective != null) {
                    objective.unregister();
                }

                for (String entry : red.getEntries()) red.removeEntry(entry);
                for (String entry : green.getEntries()) green.removeEntry(entry);
                for (String entry : blue.getEntries()) blue.removeEntry(entry);

                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (online == null) return;

                    Team spawn = scoreboard.getTeam(online.getName());
                    if (spawn == null) {
                        spawn = scoreboard.registerNewTeam(online.getName());
                    }

                    if (online == player) {
                        spawn.setPrefix(LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId()).getGrant().getRank().getColor());
                    } else {
                        spawn.setPrefix(LandCore.getInstance().getProfileManager().getProfile(online.getUniqueId()).getGrant().getRank().getColor());
                    }

                    String onlinePlayer = online.getName();
                    if (spawn.hasEntry(onlinePlayer)) {
                        continue;
                    }
                    spawn.addEntry(onlinePlayer);

                    return;
                }

                return;
            }

            Match match = plugin.getMatchManager().getMatch(player.getUniqueId());
            if (match.getKit().getName().equals("BuildUHC") && match.getKit().getName().equals("Invaded") && match.getKit().getName().equals("Strategy")) {
                Objective objective = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);
                if (objective == null) {
                    objective = player.getScoreboard().registerNewObjective("showhealth", "health");
                }

                objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
                objective.setDisplayName(CC.RED + StringEscapeUtils.unescapeJava("\u2764"));
                objective.getScore(player.getName()).setScore((int) Math.floor(player.getHealth()));
            }

            for (MatchTeam team : match.getTeams()) {
                for (UUID teamUUID : team.getAlivePlayers()) {
                    Player teamPlayer = plugin.getServer().getPlayer(teamUUID);
                    if (teamPlayer != null) {
                        String teamPlayerName = teamPlayer.getName();

                        if (team.getTeamID() == practicePlayerData.getTeamID() && !match.isFFA()) {
                            if (green.hasEntry(teamPlayerName)) {
                                continue;
                            }
                            green.addEntry(teamPlayerName);
                        } else {

                            if (match.isPartyMatch()) {
                                if (match.getKit().getName().equalsIgnoreCase("HCF")) {
                                    if (Archer.TAGGED != null) {
                                        if (tagged.hasEntry(teamPlayerName)) {
                                            continue;
                                        }
                                        tagged.addEntry(teamPlayerName);
                                    }
                                }
                            } else if (red.hasEntry(teamPlayerName)) {
                                continue;
                            }
                            red.addEntry(teamPlayerName);
                        }
                    }
                }
            }
        }
    }

    private String getClassName(UUID uuid) {
        Party party = this.plugin.getPartyManager().getParty(uuid);

        if (party.getBards().contains(uuid)) {
            return "Bard";
        } else if (party.getArchers().contains(uuid)) {
            return "Archer";
        } else {
            return "Diamond";
        }
    }

    private boolean hasExpiredEnderCooldown(Player player) {
        long cooldown = Practice.getInstance().getTimerManager().getTimer(EnderpearlTimer.class).getRemaining(player);
        return !(cooldown > 0);
    }

    private String getEnderPearlRemain(Player player) {
        if (hasExpiredEnderCooldown(player)) return "Expired";
        long cooldown = Practice.getInstance().getTimerManager().getTimer(EnderpearlTimer.class).getRemaining(player);

        return DurationFormatUtils.formatDurationWords(cooldown, true, true)
                .replace("seconds", "")
                .replace("second", "")
                .replace(" ", "");
    }
}
