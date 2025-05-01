package net.badlion.survivalgames.listeners;

import net.badlion.ministats.PlayerData;
import net.badlion.ministats.events.MiniPlayerKillEvent;
import net.badlion.ministats.events.MiniPlayerMatchEvent;
import net.badlion.ministats.events.MiniPlayerQuitEvent;
import net.badlion.ministats.events.MiniPlayerSuicideEvent;
import net.badlion.ministats.events.MiniPostPlayerProfileDataEvent;
import net.badlion.survivalgames.SurvivalGames;
import net.badlion.survivalgames.managers.SGPlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MiniStatsListener implements Listener {

    public static Map<UUID, Integer> oldRatings = new HashMap<>();
    public static Map<UUID, Integer> newRatings = new HashMap<>();

    @EventHandler
    public void onPlayerKills(MiniPlayerKillEvent event) {
        PlayerData.PlayerKill playerKill = event.getPlayerKill();
        playerKill.setOldPlayerELO(MiniStatsListener.oldRatings.get(playerKill.getKillerUUID()));
        playerKill.setNewPlayerELO(MiniStatsListener.newRatings.get(playerKill.getKillerUUID()));
        playerKill.setOldKilledPlayerELO(MiniStatsListener.oldRatings.get(playerKill.getKilledUUID()));
        playerKill.setNewKilledPlayerELO(MiniStatsListener.newRatings.get(playerKill.getKilledUUID()));
    }

    @EventHandler
    public void onPlayerQuit(MiniPlayerQuitEvent event) {
        event.getJsonObject().put("killer_old_rating", MiniStatsListener.oldRatings.get(event.getPlayerData().getUniqueId()));
        event.getJsonObject().put("killer_new_rating", MiniStatsListener.newRatings.get(event.getPlayerData().getUniqueId()));
    }

    @EventHandler
    public void onPlayerSuicide(MiniPlayerSuicideEvent event) {
        event.getJsonObject().put("killer_old_rating", MiniStatsListener.oldRatings.get(event.getPlayerData().getUniqueId()));
        event.getJsonObject().put("killer_new_rating", MiniStatsListener.newRatings.get(event.getPlayerData().getUniqueId()));
    }

    @EventHandler
    public void onPlayerKills(MiniPlayerMatchEvent event) {
        event.getProfileJSON().put("old_rating", MiniStatsListener.oldRatings.get(event.getPlayerData().getUniqueId()));
        event.getProfileJSON().put("new_rating", MiniStatsListener.newRatings.get(event.getPlayerData().getUniqueId()));
        event.getProfileJSON().put("position", SGPlayerManager.getSGPlayer(event.getPlayerData().getUniqueId()).getPosition());
        event.getProfileJSON().put("start_time", SurvivalGames.getInstance().getGame().getStartTime());
    }

    @EventHandler
    public void onPlayerProfileUpdate(MiniPostPlayerProfileDataEvent event) {
        /*int ladderId = Ladder.getLadder(SurvivalGames.getInstance().getGame().getGameMode().name(), Ladder.LadderType.FFA).getLadderId();
        int rating = MiniStatsListener.newRatings.get(event.getPlayerData().getUuid());

        JSONObject sigJSON = new JSONObject();
        sigJSON.put("name", event.getPlayerData().getUsername());
        sigJSON.put("uuid", event.getPlayerData().getUuid().toString());
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
