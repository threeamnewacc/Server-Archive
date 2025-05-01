package net.badlion.mpg.listeners;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.NameTagUtil;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.List;
import java.util.UUID;

public class GlobalListener implements Listener {

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player);

		// Is the player connecting while the game is counting down?
		if (MPG.GAME_TYPE == MPG.GameType.PARTY && MPG.getInstance().getMPGGame().getGameState() == MPGGame.GameState.GAME_COUNTDOWN) {
			if (mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
				// Is this a clan game?
				if (MPG.getInstance().getMPGGame().isClanGame()) {
					String ownClanName = mpgPlayer.getTeam().getClanName();
					String otherClanName = ownClanName.equals(MPG.getInstance().getMPGGame().getSenderClanName())
							? MPG.getInstance().getMPGGame().getTargetClanName() : MPG.getInstance().getMPGGame().getSenderClanName();

					String message = ChatColor.AQUA + "Your clan " + ChatColor.GOLD + ownClanName + ChatColor.AQUA
							+ " is fighting against " + ChatColor.GOLD + otherClanName + ChatColor.AQUA + "!";

					// Send them a message telling them who their teammate(s) is/are
					player.sendMessage(message);
				} else {
					String message = ChatColor.YELLOW + "Your team prefix is " + mpgPlayer.getTeam().getPrefix()
							+ ChatColor.YELLOW + " and your teammate";

					StringBuilder sb = new StringBuilder();
					List<UUID> teamUUIDs = mpgPlayer.getTeam().getUUIDs();

					for (UUID teamUUID : teamUUIDs) {
						// .equals() needed here
						if (!player.getUniqueId().equals(teamUUID)) {
							MPGPlayer teamMPGPlayer = MPGPlayerManager.getMPGPlayer(teamUUID);
							sb.append(mpgPlayer.getTeam().getColor());
							sb.append(teamMPGPlayer.getUsername());
							sb.append(ChatColor.YELLOW);
							sb.append(", ");
						}
					}

					String s = sb.toString();
					s = s.substring(0, s.length() - 2) + ".";

					if (teamUUIDs.size() > 2) {
						message += "'s are ";
					} else {
						message += " is ";
					}

					message += s;

					// Send them a message telling them who their teammate(s) is/are
					player.sendMessage(message);
				}
			}
		}

		// Has the game started?
		if (MPG.getInstance().getMPGGame().getGameState().ordinal() > MPGGame.GameState.GAME_COUNTDOWN.ordinal()) {
			// Are we using custom name tags for this game?
			if (MPG.GAME_TYPE == MPG.GameType.PARTY && MPG.getInstance().getBooleanOption(MPG.ConfigFlag.TEAM_NAME_TAGS)) {
				BukkitUtil.runTaskLater(new Runnable() {
					@Override
					public void run() {
						NameTagUtil.sendPlayerAllNameTags(player);
					}
				}, 2L);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getPlayer().isOp()) return;

		Material type = event.getClickedBlock().getType();
		if (type == Material.BEACON || type == Material.DRAGON_EGG) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onChunkUnloadEvent(ChunkUnloadEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerFishEvent(PlayerFishEvent event) {
		if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
			event.setCancelled(true);
			event.setExpToDrop(0);
		}
	}

	@EventHandler
	public void onPlayerExpChangeEvent(PlayerExpChangeEvent event) {
		event.setAmount(0);
	}

}
