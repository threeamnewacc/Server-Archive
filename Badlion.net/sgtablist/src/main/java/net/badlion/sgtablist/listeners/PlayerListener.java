package net.badlion.sgtablist.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.mpg.MPG;
import net.badlion.sgtablist.SGTabList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerNewScoreboardEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

	@EventHandler(priority = EventPriority.LASTEST)
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		// Is the player still online?
		if (!Gberry.isPlayerOnline(player)) return;

		// Did the game not load yet?
		if (MPG.getInstance().getServerState() == MPG.ServerState.LOADING) return;

		ScoreboardUtil.resetScoreboard(player);

		// 1.8 Start
		if (player.getClientVersion().ordinal() >= Player.CLIENT_VERSION.V1_8.ordinal()) {
			//player.setPlayerListHeaderFooter(TabListManager.getInstance().getHeader(), TabListManager.getInstance().getFooter());
		}
		// 1.8 End

		// Update player list
		BukkitUtil.runTaskLater(new Runnable() {
			@Override
			public void run() {
				SGTabList.getInstance().getSGTabListManager().updatePlayerList();
			}
		}, 2L);

		// Create the tab list
		SGTabList.getInstance().getSGTabListManager().createTabList(event.getPlayer());
	}

	@EventHandler
	public void onPlayerNewScoreboardEvent(PlayerNewScoreboardEvent event) {
		if (SGTabList.getInstance().getSGTabListManager().getTabList(event.getPlayer()) != null) {
			SGTabList.getInstance().getSGTabListManager().getTabList(event.getPlayer()).update(true);
		}
	}

	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		// Update player list
		BukkitUtil.runTaskNextTick(new Runnable() {
			@Override
			public void run() {
				SGTabList.getInstance().getSGTabListManager().updatePlayerList();
			}
		});
	}

	@EventHandler
	public void playerQuitEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		SGTabList.getInstance().getSGTabListManager().removeTabList(player);

		// Update player list
		//if (SGPlayerManager.getSGPlayer(player.getUniqueId()).getState() == SGPlayer.State.SPECTATOR) {
			BukkitUtil.runTaskNextTick(new Runnable() {
				@Override
				public void run() {
					SGTabList.getInstance().getSGTabListManager().updatePlayerList();
				}
			});
		//}
	}

}
