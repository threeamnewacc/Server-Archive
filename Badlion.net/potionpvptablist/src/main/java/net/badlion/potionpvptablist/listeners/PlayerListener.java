package net.badlion.potionpvptablist.listeners;

import net.badlion.potionpvptablist.PotionTabList;
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
		Player player = event.getPlayer();

		// 1.8 Start
		if (player.getClientVersion().ordinal() >= Player.CLIENT_VERSION.V1_8.ordinal()) {
			//player.setPlayerListHeaderFooter(TabListManager.getInstance().getHeader(), TabListManager.getInstance().getFooter());
		}
		// 1.8 End

		// We don't create the tab list here, we create it in the rating retrieved event listener
	}

    @EventHandler
    public void onScoreboardChange(PlayerNewScoreboardEvent event) {
        if (PotionTabList.getInstance().getPotionTabListManager().getTabList(event.getPlayer()) != null) {
            PotionTabList.getInstance().getPotionTabListManager().getTabList(event.getPlayer()).update(true);
        }
    }

	@EventHandler
	public void playerQuitEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		PotionTabList.getInstance().getPotionTabListManager().removeTabList(player);
	}

}