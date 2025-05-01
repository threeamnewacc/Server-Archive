package net.badlion.arenalobby.managers;

import net.badlion.arenacommon.settings.ArenaSettings;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.gberry.events.FinishedUserDataEvent;
import net.badlion.gberry.managers.UserDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class ArenaSettingsManager implements Listener {

	private static Map<UUID, ArenaSettings> settingsMap = new ConcurrentHashMap();


	@EventHandler
	public void onUserDataLoaded(final FinishedUserDataEvent event) {
		Bukkit.getLogger().log(Level.INFO, "Finished loading user data: " + event.getUuid().toString());
		if (UserDataManager.getUserData(event.getUuid()) != null) {
			// Load their settings, if their user data has no settings for arena it loads the default arena settings object
			final ArenaSettings settings = ArenaLobby.GSON.fromJson(UserDataManager.getUserData(event.getUuid()).getArenaSettings().toJSONString(), ArenaSettings.class);
			settingsMap.put(event.getUuid(), settings);

			// TODO: Fire off arena settings loaded event
			Player player = Bukkit.getPlayer(event.getUuid());
			if (player != null) {
				if (!settings.showsPlayersInLobby()) {
					VisibilityManager.hideEveryone(player);
				}
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		ArenaSettings settings = ArenaSettingsManager.getSettings(player);

		// Note: It could be possible for the settings to load slow or fast so make sure we hide the players if thats what the player wants.

		// If they dont want to see players in the lobby
		if (!settings.showsPlayersInLobby()) {
			// They are not already hiding everyone
			if(!VisibilityManager.contains(player)){
				// Hide everyone
				VisibilityManager.hideEveryone(player);
			}
		}
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		settingsMap.remove(event.getPlayer().getUniqueId());
	}


	public static ArenaSettings getSettings(Player player) {
		if (!ArenaSettingsManager.settingsMap.containsKey(player.getUniqueId())) {
			ArenaSettingsManager.settingsMap.put(player.getUniqueId(), new ArenaSettings());
		}
		return ArenaSettingsManager.settingsMap.get(player.getUniqueId());
	}

	public static ArenaSettings getSettings(UUID playerId) {
		if (!ArenaSettingsManager.settingsMap.containsKey(playerId)) {
			ArenaSettingsManager.settingsMap.put(playerId, new ArenaSettings());
		}
		return ArenaSettingsManager.settingsMap.get(playerId);
	}


	public static void saveSettings(UUID playerId) {
		ArenaSettings settings = getSettings(playerId);
		try {
			// Convert our settings into a json object
			JSONParser parser = new JSONParser();
			JSONObject settingsObject = (JSONObject) parser.parse(ArenaLobby.GSON.toJson(settings));

			// Update the table with our settings
			UserDataManager.getUserData(playerId).setArenaSettings(settingsObject, true);
			settings.setHasChanged(false);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static void saveSettings(UUID playerId, ArenaSettings settings, UserDataManager.UserData userdata) {
		try {
			// Convert our settings into a json object
			JSONParser parser = new JSONParser();
			JSONObject settingsObject = (JSONObject) parser.parse(ArenaLobby.GSON.toJson(settings));

			// Update the table with our settings
			userdata.setArenaSettings(settingsObject, true);
			settings.setHasChanged(false);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
