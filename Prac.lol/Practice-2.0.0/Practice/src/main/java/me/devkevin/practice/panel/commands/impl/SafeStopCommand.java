package me.devkevin.practice.panel.commands.impl;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchState;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

/**
 * Copyright 15/02/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class SafeStopCommand extends PracticeCommand {

    @Override @Command(name = "safestop", permission = Rank.DEVELOPER, usage = "&cUsage: /safeStop")
    public void onCommand(CommandArgs command) {
        Player sender = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            sender.sendMessage(CC.CHAT_BAR);
            sender.sendMessage(CC.translate("&cYou need to confirm to make this."));
            sender.sendMessage("");
            sender.sendMessage(CC.translate("&cThis command is so powerful (ta' potente), so watch out."));
            sender.sendMessage("");
            sender.sendMessage(CC.translate("&cThis command will stop all queues (players will be not"));
            sender.sendMessage(CC.translate("&cable to join into a queue), all matchs will be"));
            sender.sendMessage(CC.translate("&ccancelled and players will be sent to lobby."));
            sender.sendMessage("");
            sender.sendMessage(CC.translate("&cIf really wanna make this, use /safeStop confirm"));
            sender.sendMessage(CC.CHAT_BAR);
            return;
        }

        if (args[0].equalsIgnoreCase("confirm")) {
            getPlugin().setRestarting(true);
            Thread thread = new Thread(() -> {
                Bukkit.getOnlinePlayers().forEach(target -> {

                    Profile profile = getPlugin().getProfileManager().getProfileData(target.getUniqueId());

                    if (profile == null) {
                        sender.sendMessage(CC.translate("&cPlayer not found!"));
                    } else {
                        if (profile.isFighting()) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    Match match = getPlugin().getMatchManager().getMatchFromUUID(profile.getCurrentMatchID());
                                    match.setMatchState(MatchState.ENDING);
                                    match.getTeams().forEach(team -> team.alivePlayers().forEach(player -> getPlugin().getProfileManager().sendToSpawn(player)));
                                    match.spectatorPlayers().forEach(getPlugin().getMatchManager()::removeSpectator);
                                    getPlugin().getMatchManager().removeMatch(match);
                                    sender.sendMessage(CC.translate("&aSuccessfully cancelled &6" + target.getName() + "'s &amatch."));

                                    match.broadcastMessage(CC.translate("&7&m------------------------------------------------"));
                                    match.broadcastMessage(CC.translate("&c&lMatch Cancelled!"));
                                    match.broadcastMessage(CC.translate("&f ❖ Cancelled by: &c" + sender.getName()));
                                    match.broadcastMessage("");
                                    match.broadcastMessage(CC.translate("&7This will not affect your statistics!"));
                                    match.broadcastMessage(CC.translate("&7&m------------------------------------------------"));


                                    if (match.getKit().isBuild() || match.getKit().isSpleef()) {
                                        match.getArena().addAvailableArena(match.getStandaloneArena());
                                        getPlugin().getArenaManager().removeArenaMatchUUID(match.getStandaloneArena());
                                    }
                                }
                            }.runTask(getPlugin());
                            try {
                                Thread.sleep(1000L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            sender.sendMessage(CC.translate("&c" + target.getName() + " is not in a match!"));
                        }
                    }
                });
                sender.sendMessage(CC.translate("&cDeleting all parties!"));
                getPlugin().getPartyManager().getParties().values().forEach(party -> getPlugin().getPartyManager().leaveParty(Bukkit.getPlayer(party.getLeader())));
                sender.sendMessage(CC.translate("&cDeleted parties!"));
                sender.sendMessage(CC.translate("&cSending users to lobby"));
                Bukkit.getOnlinePlayers().forEach(player -> {
                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(b);
                    try {
                        out.writeUTF("Connect");
                        out.writeUTF("Hub"); // send players to Hub server
                    } catch (Exception ignored) {
                    }
                    player.sendPluginMessage(getPlugin(), "BungeeCord", b.toByteArray());
                });

                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.shutdown();
                    }
                }.runTask(getPlugin());
            });
            thread.setName("Practice SafeStop Match Canceller Worker");
            thread.start();
        }
    }
}
