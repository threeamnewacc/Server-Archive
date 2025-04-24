package club.minemen.classes.listener;

import club.minemen.classes.ClassesPlugin;
import club.minemen.classes.playerclass.IPlayerClass;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @since 12/31/2017
 */
@RequiredArgsConstructor
public final class PlayerListener implements Listener {

	private final ClassesPlugin plugin;

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		// Remove the players class if they have one enabled and remove the effect
		IPlayerClass playerClass = this.plugin.getClassManager().getPlayersClass(player);
		if (playerClass != null) {
			playerClass.onDequip(player);
			this.plugin.getClassManager().setPlayersClass(player, null);
		}

	}

}
