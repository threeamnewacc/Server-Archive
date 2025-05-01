package net.badlion.capturetheflag;

import net.badlion.capturetheflag.gamemodes.ClassicGamemode;
import net.badlion.capturetheflag.tasks.CampingListenerTask;
import net.badlion.capturetheflag.tasks.FlagListenerTask;
import net.badlion.capturetheflag.tasks.FlagPointsListenerTask;
import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.Gberry;
import net.badlion.ministats.MiniStats;
import net.badlion.ministats.PlayerData;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.MPGTeam;
import net.badlion.mpg.MPGWorld;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpg.managers.MPGTeamManager;
import net.badlion.mpg.tasks.PreGameCountdownTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CTFGame extends MPGGame {

    public enum CTFGamemode {
        POINTS, CAPTURE
    }

    public CTFGame() {
        this.gamemode = new ClassicGamemode();
    }

    private CTFGamemode ctfGamemode;

    @Override
    public void preGame() {
        this.setGameState(GameState.GAME_COUNTDOWN);

        ConcurrentLinkedQueue<MPGPlayer> mpgPlayers = MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER);
        Iterator it = mpgPlayers.iterator();

        // Assign teams to players
        while (it.hasNext()) {
            CTFPlayer ctfPlayer = (CTFPlayer) it.next();
            if (ctfPlayer.getTeam() != null) {
                continue;
            }

            MPGTeam teamToJoin = null;

            for (MPGTeam mpgTeam: MPGTeamManager.getAllMPGTeams()) {
                if (teamToJoin == null) {
                    teamToJoin = mpgTeam;
                    continue;
                }
                if (mpgTeam.size() < teamToJoin.size()) {
                    teamToJoin = mpgTeam;
                }
            }
            teamToJoin.add(ctfPlayer);
        }

        final Map<UUID, Location> teamLocations = new HashMap<>();

        for  (final MPGTeam mpgTeam: MPGTeamManager.getAllMPGTeams()) {
            ConcurrentLinkedQueue<UUID> teamPlayers = new ConcurrentLinkedQueue<>(mpgTeam.getUUIDs());
            final Iterator teamIt = teamPlayers.iterator();

            new BukkitRunnable() {

                @Override
                public void run() {
                    if (!teamIt.hasNext()) {
                        this.cancel();
                        return;
                    }

                    UUID nextPlayer = (UUID) teamIt.next();
                    MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(nextPlayer);
                    Player player = CTF.getInstance().getServer().getPlayer(nextPlayer);
                    if (player != null && mpgPlayer != null) {
                        player.getInventory().clear();
	                    CTFGame.this.gamemode.getDefaultKit().load(player, true);

                        for (PotionEffect effect : player.getActivePotionEffects()) {
                            player.removePotionEffect(effect.getType());
                        }

                        Location spawnLocation = CTFGame.this.getWorld().getSpawnLocation(mpgTeam);
                        int pos = CTFGame.this.getWorld().getFlagPosition((CTFTeam) mpgTeam);
                        if (pos >= 8) {
                            pos-=16;
                        }
                        float yaw = (float) pos * 180 / 8;
                        Location playerLocation = new Location(spawnLocation.getWorld(),
                                spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ(),
                                yaw, spawnLocation.getPitch());
                        teamLocations.put(mpgPlayer.getUniqueId(), spawnLocation);
                        player.teleport(playerLocation);

                        // MiniStats stuff
                        PlayerData playerData = new PlayerData(player.getUniqueId(), player.getName(), CTFGame.this.getGWorld().getNiceWorldName());
                        MiniStats.getInstance().getPlayerDataListener().getPlayerDataMap().put(player.getUniqueId(), playerData);

                    } else {
                        Bukkit.getLogger().severe("CTF PLAYER WAS NULL, CTF OBJECT PUT INTO SPECTATOR");
                        teamIt.remove();
                        mpgPlayer.setState(MPGPlayer.PlayerState.SPECTATOR);
                    }

                }
            }.runTaskTimer(CTF.getInstance(), 0L, 1L);
        }

        // Start pregame countdown task
        new PreGameCountdownTask(teamLocations).runTaskTimer(MPG.getInstance(), 0L, 1L);

    }

    @Override
    public void startGame() {
        Gberry.broadcastMessageNoBalance(MPG.MPG_PREFIX + ChatColor.GOLD + "GO!");

        Gberry.broadcastSound(EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1F, 1F);

        // Clear random entities
        this.world.clearNonPlayerEntities();

        // Start all the tasks
        new FlagListenerTask().runTaskTimer(CTF.getInstance(), 0L, 1L);
        if (this.ctfGamemode == CTFGamemode.POINTS) {
            new FlagPointsListenerTask().runTaskTimer(CTF.getInstance(), 0L, 20L);
        }
        new CampingListenerTask().runTaskTimer(CTF.getInstance(), 0L, 20L);

    }

	@Override
	public void preDeathMatch() {
		this.setGameState(GameState.DEATH_MATCH);
	}

    @Override
    public void deathMatchCountdown() {
    }

	@Override
	public void deathMatch() {
		// TODO overtime
	}

	@Override
    public CTFWorld getWorld() {
        return (CTFWorld) this.world;
    }

    @Override
    public boolean checkForEndGame() {
        // Check if any team is in the lead when the game is ending
        if (this.getGameState().ordinal() >= GameState.PRE_DEATH_MATCH.ordinal()) {

            CTFTeam winnerTeam = null;
            for (MPGTeam mpgTeam : MPGTeamManager.getAllMPGTeams()) {
                CTFTeam ctfTeam = (CTFTeam) mpgTeam;
                if (winnerTeam == null || ctfTeam.getScore() > winnerTeam.getScore()) {
                    winnerTeam = ctfTeam;
                }
            }

            if (winnerTeam != null) {
                for (MPGTeam mpgTeam : MPGTeamManager.getAllMPGTeams()) {
                    CTFTeam ctfTeam = (CTFTeam) mpgTeam;
                    if (winnerTeam != ctfTeam && winnerTeam.getScore() == ctfTeam.getScore()) {
                        // There's a tie
                        return false;
                    }
                }
                for (UUID ctfPlayer : winnerTeam.getUUIDs()) {
                    this.addWinner(ctfPlayer);
                }
                this.setEndTime(System.currentTimeMillis());
                ConcurrentLinkedQueue<MPGPlayer> mpgPlayers = MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER);
                MPGPlayer mpgPlayer = mpgPlayers.iterator().next();
                Player player = mpgPlayer.getPlayer();

                MiniStats.getInstance().getPlayerDataListener().getPlayerData(mpgPlayer.getUniqueId()).addTotalTimePlayed((System.currentTimeMillis() - mpgPlayer.getStartTime()) / 1000);

                if (player != null) {
                    CTF.getInstance().getCTFGame().setGameState(MPGGame.GameState.POST_GAME);
                    MiniStats.getInstance().stopListening();
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public void setWorld(MPGWorld mpgWorld) {
        CTFWorld ctfWorld = (CTFWorld) mpgWorld;
        super.setWorld(ctfWorld);

        this.ctfGamemode = ctfWorld.getCTFGamemode();
    }

    public CTFGamemode getCTFGamemode() {
        return this.ctfGamemode;
    }

}
