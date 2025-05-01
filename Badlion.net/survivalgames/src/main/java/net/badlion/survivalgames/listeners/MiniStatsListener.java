package net.badlion.survivalgames.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.ministats.MiniStatsPlayer;
import net.badlion.ministats.PlayerData;
import net.badlion.ministats.events.MiniPlayerKillEvent;
import net.badlion.ministats.events.MiniPlayerMatchEvent;
import net.badlion.ministats.events.MiniPlayerQuitEvent;
import net.badlion.ministats.events.MiniPlayerSuicideEvent;
import net.badlion.ministats.events.MiniPostPlayerProfileDataEvent;
import net.badlion.ministats.managers.DatabaseManager;
import net.badlion.mpg.MPG;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.survivalgames.SGPlayer;
import net.badlion.survivalgames.SurvivalGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.sql.Connection;
import java.sql.SQLException;

public class MiniStatsListener implements Listener {

	@EventHandler
	public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
		SGPlayer sgPlayer = (SGPlayer) MPGPlayerManager.getMPGPlayer(event.getUniqueId());

		// SGPlayer is null if player wasn't whitelisted
		if (sgPlayer == null) return;

		Connection connection = null;

		try {
			connection = Gberry.getConnection();

			MiniStatsPlayer stats = DatabaseManager.getPlayerStats(connection, event.getUniqueId());

			sgPlayer.setTotalKills(stats.getKills());
			sgPlayer.setGamesWon(stats.getWins());
			sgPlayer.setGamesLost(stats.getLosses());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(connection);
		}
	}

    @EventHandler
    public void onMiniPlayerKillEvent(MiniPlayerKillEvent event) {
        PlayerData.PlayerKill playerKill = event.getPlayerKill();
        playerKill.setOldPlayerELO(SurvivalGames.getInstance().getSGGame().getPlayerRating(playerKill.getKillerUUID()));
        playerKill.setOldKilledPlayerELO(SurvivalGames.getInstance().getSGGame().getPlayerRating(playerKill.getKilledUUID()));
    }

    @EventHandler
    public void onMiniPlayerQuitEvent(MiniPlayerQuitEvent event) {
        event.getJsonObject().put("killer_old_rating", SurvivalGames.getInstance().getSGGame().getPlayerRating(event.getPlayerData().getUniqueId()));
    }

    @EventHandler
    public void onMiniPlayerSuicideEvent(MiniPlayerSuicideEvent event) {
        event.getJsonObject().put("killer_old_rating", SurvivalGames.getInstance().getSGGame().getPlayerRating(event.getPlayerData().getUniqueId()));
    }

    @EventHandler
    public void onMiniPlayerMatchEvent(MiniPlayerMatchEvent event) {
        SGPlayer sgPlayer = (SGPlayer) MPGPlayerManager.getMPGPlayer(event.getPlayerData().getUniqueId());
        event.getProfileJSON().put("old_rating", SurvivalGames.getInstance().getSGGame().getPlayerRating(event.getPlayerData().getUniqueId()));
        event.getProfileJSON().put("position", sgPlayer.getPosition());
        event.getProfileJSON().put("start_time", MPG.getInstance().getMPGGame().getStartTime());

        event.getProfileJSON().put("tier1_opened", sgPlayer.getNumberTierChestsOpened(1));
        event.getProfileJSON().put("tier2_opened", sgPlayer.getNumberTierChestsOpened(2));
	    event.getProfileJSON().put("supply_drops_opened", sgPlayer.getNumberSupplyDropsOpened());
    }

	// TODO: SIGNATURES???
    @EventHandler
    public void onPlayerProfileUpdate(MiniPostPlayerProfileDataEvent event) {
        /*int ladderId = Ladder.getLadder(SurvivalGames.getInstance().getGame().getGamemode().name(), Ladder.LadderType.FFA).getLadderId();
        int rating = MiniStatsListener.newRatings.get(event.getPlayerData().getUniqueId());

        JSONObject sigJSON = new JSONObject();
        sigJSON.put("name", event.getPlayerData().getUsername());
        sigJSON.put("uuid", event.getPlayerData().getUniqueId().toString());
        sigJSON.put("ranking", RatingManager.getUserRank(rating, ladderId));
        sigJSON.put("rating", rating);
        sigJSON.put("kills", event.getProfileJSON().get("kills"));
        sigJSON.put("wins", event.getProfileJSON().get("wins"));
        sigJSON.put("deaths", event.getProfileJSON().get("losses"));
        sigJSON.put("damage_dealt", (int) (double) event.getProfileJSON().get("damageDealt"));
        sigJSON.put("damage_taken", (int) (double) event.getProfileJSON().get("damageTaken"));
        sigJSON.put("time", (int) (((long) event.getProfileJSON().get("timePlayed")) / (1000 * 60)));

        /Bukkit.getLogger().info(sigJSON.toJSONString());
        try {
            HTTPCommon.executePUTRequest(SurvivalGames.getInstance().getSigURL() + "MCSGImageUpdate/" + ladderId + "/" + SurvivalGames.getInstance().getSigKey(), sigJSON);
        } catch (HTTPRequestFailException e) {
            SurvivalGames.getInstance().getServer().getLogger().info("Failed to save image with code " + e.getResponseCode());
        }*/
    }

}
