package net.badlion.tdm;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.MPGTeam;
import net.badlion.mpg.MPGWorld;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpg.tasks.VoidCheckerTask;
import net.badlion.mpg.tasks.deathmatch.DeathMatchDamageTask;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TDMGame extends MPGGame {

	public TDMGame(MPGWorld world) {
		super(world);

		// Load 60x60 chunk
		for (int x = -30; x <= 30; x++) {
			for (int z = -30; z <= 30; z++) {
				this.getGWorld().getBukkitWorld().loadChunk(x, z);
			}
		}
	}

	@Override
	public void preGame() {
		// Start countdown task
		this.startCountdownTask(MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.GAME_TIME_LIMIT));
	}

	@Override
	public void startGame() {
		// Add listener
		//this.skyWarsGameListener = new SkyWarsGameListener();
		//SkyWars.getInstance().getServer().getPluginManager().registerEvents(this.skyWarsGameListener, SkyWars.getInstance());

		Bukkit.broadcastMessage(MPG.MPG_PREFIX + ChatColor.AQUA + "The game has begun!");
		Gberry.broadcastSound(Sound.NOTE_PLING, 1, 1);

		for (Player pl : Bukkit.getOnlinePlayers()) {
			MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(pl.getUniqueId());
			if (mpgPlayer.getState() != MPGPlayer.PlayerState.PLAYER) {
				continue;
			}

			BukkitUtil.closeInventory(pl);

			// Load kit
			if (mpgPlayer.getKit() != null) {
				mpgPlayer.getKit().load(pl);
			} else {
				this.getGamemode().getDefaultKit().load(pl);
			}
		}
	}

	@Override
	public boolean checkForEndGame() {
		int teamsLeft = 0;

		MPGTeam lastTeam = null;
		for (MPGTeam team : this.teams) {
			if (team.getDeaths() == team.getUUIDs().size()) {
				continue;
			}

			lastTeam = team;
			++teamsLeft;
		}

		if (lastTeam == null) {
			return false;
		}

		// Keep track of winners
		if (teamsLeft <= 1) {
			for (UUID uuid : lastTeam.getUUIDs()) {
				this.addWinner(uuid);
			}
		}

		return teamsLeft <= 1;
	}

	@Override
	public void freezeGame() {
		throw new NotImplementedException();
	}

	@Override
	public void deathMatch() {
		DeathMatchDamageTask.start(20);
	}

	@Override
	public TDMWorld getWorld() {
		return (TDMWorld) this.world;
	}

}
