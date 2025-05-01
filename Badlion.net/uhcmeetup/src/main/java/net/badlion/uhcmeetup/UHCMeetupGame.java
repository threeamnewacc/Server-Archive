package net.badlion.uhcmeetup;

import net.badlion.combattag.CombatTagPlugin;
import net.badlion.combattag.LoggerNPC;
import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.Gberry;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.MPGTeam;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpg.managers.MPGTeamManager;
import net.badlion.mpg.tasks.PreGameCountdownTask;
import net.badlion.uhcmeetup.gamemodes.ClassicGamemode;
import net.badlion.uhcmeetup.listeners.PreGameListener;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UHCMeetupGame extends MPGGame {

	private Listener preGameListener;

    public UHCMeetupGame(UHCMeetupWorld mpgWorld) {
	    super(new ClassicGamemode(), mpgWorld);

	    // Set region flags
	    this.region.setAllowBrokenBlocks(true);
	    this.region.setAllowCreatureSpawn(true);
	    this.region.setAllowCreeperBlockDamage(false);
	    this.region.setAllowFire(true);
	    this.region.setAllowFireIgniteByPlayer(true);
	    this.region.setAllowIceMelt(false);
	    this.region.setAllowPistonUsage(false);
	    this.region.setAllowPlacedBlocks(true);
	    this.region.setAllowTNTBlockDamage(false);
	    this.region.setChangeMobDamageToPlayer(false);
	    this.region.setAllowBlockMovement(true);
	    this.region.setAllowedBucketPlacements(true);
	    this.region.setDamageMultiplier(0.5);
	    this.region.setAllowEnderPearls(false);
	    this.region.setAllowEndermanMoveBlocks(false);
	    this.region.setAllowPlantGrowth(false);
	    this.region.setAllowPlantSpread(false);
	    this.region.setAllowFireSpread(true);
	    this.region.setAllowHangingItems(false);
	    this.region.setAllowItemInteraction(true);
	    this.region.setAllowChestInteraction(false);
	    this.region.setAllowBlockChangesByEntities(false);
	    this.region.setAllowLeafDecay(false);
	    this.region.setOverrideChestUsage(true);
	    this.region.setHealPlayers(false);
	    this.region.setFeedPlayers(false);
	    this.region.setAllowPotionEffects(false);
	    this.region.setAllowPlayerPickupItems(true);
	    this.region.setAllowPVP(true);
    }

	@Override
	public void preGame() {
		this.preGameListener = new PreGameListener();
		UHCMeetup.getInstance().getServer().getPluginManager().registerEvents(this.preGameListener, UHCMeetup.getInstance());

		final Map<UUID, Location> playerLocations = new HashMap<>();

		final List<MPGTeam> teams = MPGTeamManager.getAllMPGTeams();

		final Iterator<MPGTeam> it = teams.iterator();

		// Teleport one player per pick
		new BukkitRunnable() {
			private int i = 0;

			@Override
			public void run() {
				if (!it.hasNext()) {
					// Start pregame countdown task
					new PreGameCountdownTask(playerLocations).runTaskTimer(MPG.getInstance(), 0L, 1L);

					// Set to countdown state
					UHCMeetupGame.this.setGameState(GameState.GAME_COUNTDOWN);

					this.cancel();
					return;
				}

				MPGTeam team = it.next();
				Location spawnLocation = UHCMeetupGame.this.getWorld().getSpawnLocation(this.i++);

				for (UUID uuid : team.getUUIDs()) {
					playerLocations.put(uuid, spawnLocation);
				}
			}
		}.runTaskTimer(UHCMeetup.getInstance(), 0L, 1L);
	}
	@Override
	public void startGame() {
		// Disable old listener
		HandlerList.unregisterAll(this.preGameListener);

		Gberry.broadcastMessageNoBalance(MPG.MPG_PREFIX + ChatColor.GOLD + "GO!");

		Gberry.broadcastSound(EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1F, 1F);

		// Clear random entities
		this.world.clearNonPlayerEntities();

		// Give all logger NPCs the kit selection items
		for (LoggerNPC loggerNPC : CombatTagPlugin.getInstance().getAllLoggers()) {
			UHCMeetup.getInstance().giveBuildUHCKitSelectionItems(loggerNPC.getUUID(), null);
		}
	}

	@Override
	public void preDeathMatch() {

	}

	@Override
	public void deathMatchCountdown() {

	}

	@Override
	public void deathMatch() {

	}

	@Override
	public boolean checkForEndGame() {
		ConcurrentLinkedQueue<MPGPlayer> onlineSGPlayers = MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER);
		ConcurrentLinkedQueue<MPGPlayer> offlineSGPlayers = MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.DC);

		int numberOfPlayersToEndGame = 1;

		if (MPG.GAME_TYPE == MPG.GameType.PARTY) {
			numberOfPlayersToEndGame = MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.PLAYERS_PER_TEAM);
		}

		// Do we have few enough people to end the game?
		if (onlineSGPlayers.size() + offlineSGPlayers.size() == numberOfPlayersToEndGame) {
			MPGPlayer mpgPlayer;

			// Get the first player
			if (!onlineSGPlayers.isEmpty()) {
				mpgPlayer = onlineSGPlayers.iterator().next();
			} else {
				mpgPlayer = offlineSGPlayers.iterator().next();
			}

			MPGTeam team = mpgPlayer.getTeam();

			// Do additional checks for the team if this is a party game
			if (MPG.GAME_TYPE == MPG.GameType.PARTY) {
				// Make sure that all alive players are part of this team
				for (MPGPlayer onlineSGPlayer : onlineSGPlayers) {
					if (team != onlineSGPlayer.getTeam()) {
						return false;
					}
				}

				for (MPGPlayer offlineSGPlayer : offlineSGPlayers) {
					if (team != offlineSGPlayer.getTeam()) {
						return false;
					}
				}
			}

			for (UUID uuid : team.getUUIDs()) {
				// Add this player to the winners list
				this.addWinner(uuid);
			}

			return true;
		}

		return false;
	}

    @Override
    public UHCMeetupWorld getWorld() {
        return (UHCMeetupWorld) this.world;
    }

}
