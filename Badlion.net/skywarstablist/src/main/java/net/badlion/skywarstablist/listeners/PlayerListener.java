package net.badlion.skywarstablist.listeners;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.mpg.bukkitevents.MPGPlayerStateChangeEvent;
import net.badlion.skywarstablist.SWTabList;
import net.badlion.tablist.TabListManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerNewScoreboardEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerJoinEvent(PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		// 1.8 Start
		// TODO: TEST HEADERS/FOOTERS FOR 1.9, MAKE SURE CLIENT_VERSION IS FIXED
		if (player.getClientVersion().ordinal() >= Player.CLIENT_VERSION.V1_8.ordinal()) {
			player.sendHeaderAndFooter(TabListManager.getInstance().getHeader(), TabListManager.getInstance().getFooter());
		}
		// 1.8 End

		TabListManager.getInstance().getListCommandHandler().checkIfStaffJoined(player);

		// Update player list
		BukkitUtil.runTaskLater(new Runnable() {
			@Override
			public void run() {
				SWTabList.getInstance().getSWTabListManager().updatePlayerList();
			}
		}, 2L);

		// We don't create the tab list here, we create it in the rating retrieved event listener
		SWTabList.getInstance().getSWTabListManager().createTabList(event.getPlayer());
	}

	@EventHandler
	public void onScoreboardChange(PlayerNewScoreboardEvent event) {
		if (SWTabList.getInstance().getSWTabListManager().getTabList(event.getPlayer()) != null) {
			SWTabList.getInstance().getSWTabListManager().getTabList(event.getPlayer()).update(true);
		}
	}

	@EventHandler
	public void playerDeathEvent(MPGPlayerStateChangeEvent event) {
		// Update player list
		BukkitUtil.runTaskNextTick(new Runnable() {
			@Override
			public void run() {
				SWTabList.getInstance().getSWTabListManager().updatePlayerList();
			}
		});
	}

	@EventHandler
	public void playerQuitEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		SWTabList.getInstance().getSWTabListManager().removeTabList(player);

		TabListManager.getInstance().getListCommandHandler().checkIfStaffLeft(player);

		// Update player list
		//if(SGPlayerManager.getSGPlayer(player.getUniqueId()).getState() == SGPlayer.State.SPECTATOR) {
			BukkitUtil.runTaskNextTick(new Runnable() {
				@Override
				public void run() {
					SWTabList.getInstance().getSWTabListManager().updatePlayerList();
				}
			});
		//}
	}

}
