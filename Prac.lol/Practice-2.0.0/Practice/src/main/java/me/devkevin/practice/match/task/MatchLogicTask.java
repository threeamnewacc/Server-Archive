package me.devkevin.practice.match.task;

import club.inverted.chatcolor.CC;
import me.devkevin.practice.Practice;
import me.devkevin.practice.arena.chunk.manager.ChunkRestorationManager;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchState;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Copyright 29/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class MatchLogicTask extends BukkitRunnable {

    private final Practice plugin = Practice.getInstance();
    private Match match;

    public MatchLogicTask(Match match) {
        this.match = match;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        switch (match.getMatchState()) {
            case STARTING:
                if (this.match.decrementCountdown() == 0) {
                    this.match.setMatchState(MatchState.FIGHTING);
                    this.match.broadcastWithSound(CC.GOLD + "The match has started!", Sound.FIREWORK_BLAST);

                    if (this.match.getKit().isSumo()) {
                        match.broadcastMessage("");
                        match.broadcastMessage(CC.YELLOW + CC.BOLD + "Sumo");
                        match.broadcastMessage(CC.YELLOW + " Knock your opponents off the platform!");
                        match.broadcastMessage("");
                        return;
                    }

                    if (this.match.getKit().isBoxing()) {
                        match.broadcastMessage("");
                        match.broadcastMessage(CC.YELLOW + CC.BOLD + "Boxing");
                        match.broadcastMessage(CC.YELLOW + " First to 100 hits wins!");
                        match.broadcastMessage("");
                        return;
                    }

                    if (this.match.getKit().getName().equals("BuildUHC")) {
                        match.broadcastMessage("");
                        match.broadcastMessage(CC.YELLOW + CC.BOLD + "Build UHC");
                        match.broadcastMessage(CC.YELLOW + " Eliminate your opponents! (Diamond");
                        match.broadcastMessage(CC.YELLOW + "Gear, Bow, Blocks, Gapples)");
                        match.broadcastMessage("");
                        return;
                    }

                    if (this.match.getKit().getName().equals("NoDebuff")) {
                        match.broadcastMessage("");
                        match.broadcastMessage(CC.YELLOW + CC.BOLD + "NoDebuff");
                        match.broadcastMessage(CC.YELLOW + " Eliminate your opponents! (Diamond");
                        match.broadcastMessage(CC.YELLOW + "Gear, Heal Potions)");
                        match.broadcastMessage("");
                        return;
                    }

                    this.match.setStartTimestamp(System.currentTimeMillis());
                } else {
                    this.match.broadcastWithSound(CC.YELLOW + "The match starts in " + CC.GOLD
                            + this.match.getCountdown() + CC.YELLOW + " second(s)...", Sound.CLICK);
                }
                break;
            case FIGHTING:
                match.incrementDuration();
                break;
            case ENDING:
                if (this.match.decrementCountdown() == 0) {
                    this.plugin.getTournamentManager().removeTournamentMatch(this.match);

                    this.match.getRunnables().forEach(id -> this.plugin.getServer().getScheduler().cancelTask(id));

                    this.match.getEntitiesToRemove().forEach(Entity::remove);

                    this.match.getTeams().forEach(team -> team.alivePlayers().forEach(player -> Practice.getInstance().getProfileManager().sendToSpawn(player)));

                    this.match.spectatorPlayers().forEach(this.plugin.getMatchManager()::removeSpectator);

                    this.match.getPlacedBlockLocations().forEach(location -> location.getBlock().setType(Material.AIR));

                    this.match.getPlacedBlockLocations().forEach(location -> location.getBlock().setType(Material.AIR));


                    BlockState blockState;
                    while ((blockState = this.match.getOriginalBlockChanges().pollLast()) != null) {
                        blockState.getLocation().getBlock().setType(blockState.getType());
                        blockState.update(true, false);
                    }

                    if (this.match.getKit().isBuild() || this.match.getKit().isSpleef()) {
                        ChunkRestorationManager.getIChunkRestoration().reset(match.getStandaloneArena());
                        this.match.getArena().addAvailableArena(this.match.getStandaloneArena());
                        this.plugin.getArenaManager().removeArenaMatchUUID(this.match.getStandaloneArena());
                    }

                    this.plugin.getMatchManager().removeMatch(this.match);
                    this.cancel();
                }
                break;
        }
    }
}
