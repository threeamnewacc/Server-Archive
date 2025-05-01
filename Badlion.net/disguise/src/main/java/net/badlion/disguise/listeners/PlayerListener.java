package net.badlion.disguise.listeners;

import net.badlion.disguise.DisguisedPlayer;
import net.badlion.disguise.managers.DisguiseManager;
import net.badlion.gberry.managers.UserDataManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

	@EventHandler(priority = EventPriority.FIRST)
	public void onPlayerJoinEvent(final PlayerJoinEvent event) {
		Player player = event.getPlayer();

		// Check if they're supposed to be disguised

		// Get player's user data
		UserDataManager.UserData userData = UserDataManager.getUserData(player);

		// Check that they have disguise settings before trying to read values from it
		if (userData.getDisguiseSettings().isEmpty()) {
			return;
		}

		if (!userData.getDisguiseSettings().containsKey("is_disguised")) {
			return;
		}

		// Check to see if this player is supposed to be disguised
		if ((boolean) userData.getDisguiseSettings().get("is_disguised")) {
			// Get the player's disguise name
			String disguiseName = (String) userData.getDisguiseSettings().get("disguise_name");

			// Get player's skin information
			String skinTexture = (String) userData.getDisguiseSettings().get("skin_texture");
			String skinSignature = (String) userData.getDisguiseSettings().get("skin_signature");

			DisguiseManager.storeDisguisePlayer(new DisguisedPlayer(player.getUniqueId(), player.getName(), disguiseName));

			// Disguise the player internally
			player.disguise(disguiseName, skinTexture, skinSignature);

			// Verify that this player has disguise permissions (in case their rank changed)
			// Undisguise them after we disguise so that the proper information can be stored into the DisguiseManager
			if (!player.hasPermission("badlion.disguise")) {
				DisguiseManager.undisguisePlayer(player, false);

				player.sendMessage(ChatColor.RED + "Your rank has changed and as a result you have been undisguised.");
			}
		}
	}

}
