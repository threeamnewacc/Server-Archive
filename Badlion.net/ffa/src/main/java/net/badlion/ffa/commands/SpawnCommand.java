package net.badlion.ffa.commands;

import net.badlion.ffa.FFA;
import net.badlion.ffa.gamemodes.SGGamemode;
import net.badlion.ffa.gamemodes.UHCGamemode;
import net.badlion.ffa.listeners.EnderPearlListener;
import net.badlion.ffa.listeners.MultiKillListener;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

	public static final int COMBAT_TAG_TIME = 30000;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) return true;

		Player player = (Player) sender;

		Long lastDamageTime = MultiKillListener.getInstance().getLastDamageTime(player.getUniqueId());

		if (lastDamageTime != null && lastDamageTime + SpawnCommand.COMBAT_TAG_TIME >= System.currentTimeMillis()) {
			long timeRemaining = lastDamageTime + SpawnCommand.COMBAT_TAG_TIME - System.currentTimeMillis();

			player.sendMessage(ChatColor.RED + "Cannot use /spawn when in combat. You have "
					+ ((double) Math.round(((double) timeRemaining / 1000) * 10) / 10) + " seconds remaining.");
		} else {
			MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player);

			// Is this player a spectator?
			if (mpgPlayer.getState() == MPGPlayer.PlayerState.SPECTATOR) {
				// Transition them to PLAYER state
				mpgPlayer.setState(MPGPlayer.PlayerState.PLAYER);
			} else {
				// Teleport this player to spawn
				FFA.getInstance().prepPlayerForSpawn(player);

				player.teleport(FFA.getInstance().getFFAGame().getWorld().getSpawnLocation());
			}

			if (FFA.FFA_GAMEMODE instanceof SGGamemode || FFA.FFA_GAMEMODE instanceof UHCGamemode) {
				// Allow them to use pearls again
				EnderPearlListener.getInstance().setHasTakenFallDamage(player.getUniqueId(), false);
			}
		}

		return true;
	}

}
