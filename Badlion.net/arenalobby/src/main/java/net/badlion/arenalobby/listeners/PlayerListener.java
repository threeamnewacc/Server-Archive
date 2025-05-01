package net.badlion.arenalobby.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.FinishedUserDataEvent;
import net.badlion.gberry.managers.UserDataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

	@EventHandler(priority = EventPriority.LAST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		UserDataManager.UserData userData = UserDataManager.getUserData(player);
		if (userData == null) {
			return;
		}

		this.tryEnableFlight(player, userData);
	}

	@EventHandler
	public void onFinishedUserData(FinishedUserDataEvent event) {
		Player player = Bukkit.getPlayer(event.getUuid());

		if (player == null || Gberry.isPlayerOnline(player)) {
			// The player join event will hook them up with the stuff they need since their user data loaded before they joined?
			return;
		}

		UserDataManager.UserData userData = UserDataManager.getUserData(player);

		this.tryEnableFlight(player, userData);
	}


	public void tryEnableFlight(Player player, UserDataManager.UserData userData) {
		if (!player.hasPermission("badlion.staff") && !player.hasPermission("badlion.donatorplus")) {
			return;
		}

		/* Can't fly when morphed (We dont need this on arena lobby do we??)
		if (CosmeticsManager.getCosmeticsSettings(player.getUniqueId()).getActiveMorph() != null) {
			player.sendMessage(ChatColor.RED + "You can't fly when morphed!");
			return;
		}
		*/

		if (userData.isLobbyFlight()) {
			player.sendFormattedMessage("{0}Flight mode enabled. Use {1} to toggle flight.", ChatColor.GREEN, "/fly");
			player.setAllowFlight(true);
		}
	}

}
