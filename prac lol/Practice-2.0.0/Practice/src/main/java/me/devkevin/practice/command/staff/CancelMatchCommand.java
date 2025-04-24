package me.devkevin.practice.command.staff;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.arena.chunk.manager.ChunkRestorationManager;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchState;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Copyright 12/02/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class CancelMatchCommand extends PracticeCommand {

    @Override @Command(name = "cancelmatch", permission = Rank.ADMIN, usage = "&cUsage: /cancelmatch <player>")
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(CC.translate(command.getCommand().getUsage()));
            return;
        }

        Profile profile = getPlugin().getProfileManager().getProfileData(player.getUniqueId());
        Player target = Bukkit.getPlayer(args[0]);
        Match match = getPlugin().getMatchManager().getMatch(target.getUniqueId());
        Profile profileTarget = getPlugin().getProfileManager().getProfileData(target.getUniqueId());

        if (args.length == 1) {
            if (profileTarget == null) {
                player.sendMessage(CC.translate("&cPlayer not found."));
                return;
            }

            if (profileTarget.isFighting()) {
                if (!match.isPartyMatch()) {
                    match.setMatchState(MatchState.ENDING);
                    match.getTeams().forEach(team -> team.alivePlayers().forEach(targets -> getPlugin().getProfileManager().sendToSpawn(targets)));
                    match.spectatorPlayers().forEach(getPlugin().getMatchManager()::removeSpectator);
                    profile.getCachedPlayer().clear();

                    match.broadcastMessage("");
                    match.broadcastMessage(
                            CC.translate(
                                    "&fThe match &fwas &ccancelled &fby " + LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId()).getGrant().getRank().getColor() + player.getName()

                            ));
                    match.broadcastMessage("");

                    if (match.getKit().isBuild() || match.getKit().isSpleef()) {
                        ChunkRestorationManager.getIChunkRestoration().reset(match.getStandaloneArena());
                        match.getArena().addAvailableArena(match.getStandaloneArena());
                        getPlugin().getArenaManager().removeArenaMatchUUID(match.getStandaloneArena());
                    }
                } else {
                    player.sendMessage(CC.translate("&cThis command can only be used on 1v1 matches."));
                }
            } else {
                player.sendMessage(CC.translate("&c" + target.getName() + " is not in a match."));
            }
        }
    }
}
