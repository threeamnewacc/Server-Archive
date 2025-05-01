package net.badlion.skywars.listeners;

import net.badlion.ministats.MiniStats;
import net.badlion.ministats.events.*;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.skywars.SWMiniStatsPlayer;
import net.badlion.skywars.SkyPlayer;
import net.badlion.skywars.SkyWars;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;

import java.sql.SQLException;

public class MiniStatsListener implements Listener {

//    public static Map<UUID, Integer> oldRatings = new HashMap<>();
//    public static Map<UUID, Integer> newRatings = new HashMap<>();
//
//    @EventHandler
//    public void onPlayerKills(MiniPlayerKillEvent event) {
//        PlayerData.PlayerKill playerKill = event.getPlayerKill();
//        playerKill.setOldPlayerELO(MiniStatsListener.oldRatings.get(playerKill.getPlayer().getUniqueId()));
//        playerKill.setNewPlayerELO(MiniStatsListener.newRatings.get(playerKill.getPlayer().getUniqueId()));
//        playerKill.setOldKilledPlayerELO(MiniStatsListener.oldRatings.get(playerKill.getKilledPlayer().getUniqueId()));
//        playerKill.setNewKilledPlayerELO(MiniStatsListener.newRatings.get(playerKill.getKilledPlayer().getUniqueId()));
//    }
//
//    @EventHandler
//    public void onPlayerQuit(MiniPlayerQuitEvent event) {
//        event.getJsonObject().put("killer_old_rating", MiniStatsListener.oldRatings.get(event.getPlayerData().getUniqueId()));
//        event.getJsonObject().put("killer_new_rating", MiniStatsListener.newRatings.get(event.getPlayerData().getUniqueId()));
//    }
//
//    @EventHandler
//    public void onPlayerSuicide(MiniPlayerSuicideEvent event) {
//        event.getJsonObject().put("killer_old_rating", MiniStatsListener.oldRatings.get(event.getPlayerData().getUniqueId()));
//        event.getJsonObject().put("killer_new_rating", MiniStatsListener.newRatings.get(event.getPlayerData().getUniqueId()));
//    }

    @EventHandler(priority=EventPriority.LAST, ignoreCancelled=true)
    public void onPlayerOpenChest(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock() != null) {
                if (event.getClickedBlock().getType() == Material.CHEST) {
                    SkyPlayer skyPlayer = (SkyPlayer) MPGPlayerManager.getMPGPlayer(event.getPlayer().getUniqueId());
                    if (MiniStats.getInstance().getPlayerDataListener().isTrackStats() && skyPlayer.isTrackData()) {
                        skyPlayer.addChestOpened(event.getClickedBlock());
                    }
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.LAST, ignoreCancelled=true)
    public void onPlayerRightClickWithSpawnEgg(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() != null && event.getItem().getType() == Material.MONSTER_EGG) {
                SkyPlayer skyPlayer = (SkyPlayer) MPGPlayerManager.getMPGPlayer(event.getPlayer().getUniqueId());
                if (MiniStats.getInstance().getPlayerDataListener().isTrackStats() && skyPlayer.isTrackData()) {
                    skyPlayer.addSpawnedMob();
                }
            }
        }
    }

    // TODO: Make it so spectators can't get levels from bottles of exp
    @EventHandler(priority=EventPriority.LAST, ignoreCancelled=true)
    public void onPlayerLevelChange(PlayerLevelChangeEvent event) {
        SkyPlayer skyPlayer = (SkyPlayer) MPGPlayerManager.getMPGPlayer(event.getPlayer().getUniqueId());
        if (MiniStats.getInstance().getPlayerDataListener().isTrackStats() && skyPlayer.isTrackData()) {
            skyPlayer.addLevels(event.getNewLevel() - event.getOldLevel());
        }
    }

    @EventHandler(priority=EventPriority.LAST) // Cannot ignore cancelled because by defualt if there is no interacted block it is cancelled
    public void onPlayerShootSnowEgg(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() != null) {
                if (event.getItem().getType() == Material.SNOW_BALL || event.getItem().getType() == Material.EGG) {
                    SkyPlayer skyPlayer = (SkyPlayer) MPGPlayerManager.getMPGPlayer(event.getPlayer().getUniqueId());
                    if (MiniStats.getInstance().getPlayerDataListener().isTrackStats() && skyPlayer.isTrackData()) {
                        skyPlayer.addSnowEggShot();
                    }
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.LAST, ignoreCancelled=true)
    public void arrowHitPlayerEvent(EntityDamageByEntityEvent event) {
        if (MiniStats.getInstance().getPlayerDataListener().isTrackStats() && event.getEntity() instanceof Player
                    && (event.getDamager() instanceof Snowball || event.getDamager() instanceof Egg)) {
            if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
                Player player = (Player) ((Projectile) event.getDamager()).getShooter();

                SkyPlayer skyPlayer = (SkyPlayer) MPGPlayerManager.getMPGPlayer(player.getUniqueId());
                if (skyPlayer.isTrackData()) {
                    skyPlayer.addSnowEggHit();
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.LAST, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent event) {
        SkyPlayer skyPlayer = (SkyPlayer) MPGPlayerManager.getMPGPlayer(event.getPlayer().getUniqueId());
        if (MiniStats.getInstance().getPlayerDataListener().isTrackStats() && skyPlayer.isTrackData()) {
            skyPlayer.addBlockPlace();
        }
    }

    @EventHandler
    public void onPlayerKills(MiniPlayerMatchEvent event) {
        //event.getProfileJSON().put("old_rating", MiniStatsListener.oldRatings.get(event.getPlayerData().getUniqueId()));
        //event.getProfileJSON().put("new_rating", MiniStatsListener.newRatings.get(event.getPlayerData().getUniqueId()));
        // TODO: Fix
        //event.getProfileJSON().put("position", SGPlayerManager.getSGPlayer(event.getPlayerData().getUniqueId()).getPosition());
        event.getProfileJSON().put("start_time", SkyWars.getInstance().getCurrentGame().getStartTime());
    }

    @EventHandler
    public void onPlayerProfileCreated(MiniNewPlayerProfileEvent event) {
        SWMiniStatsPlayer swMiniStatsPlayer;
        try {
            swMiniStatsPlayer = new SWMiniStatsPlayer(event.getRs());
            event.setMiniStatsPlayer(swMiniStatsPlayer);
        } catch (SQLException e) {
            e.printStackTrace();
            event.setError(true);
            return;
        }

        SkyPlayer skyPlayer = (SkyPlayer) MPGPlayerManager.getMPGPlayer(event.getUuid());

        swMiniStatsPlayer.addTier1Opened(skyPlayer.getTier1ChestsOpened());
        swMiniStatsPlayer.addTier2Opened(skyPlayer.getTier2ChestsOpened());
        swMiniStatsPlayer.addLevels(skyPlayer.getLevels());
        swMiniStatsPlayer.addMobsSpawned(skyPlayer.getMobsSpawned());
        swMiniStatsPlayer.addSnowEggHit(skyPlayer.getSnowEggsHit());
        swMiniStatsPlayer.addSnowEggShot(skyPlayer.getSnowEggsShot());
        swMiniStatsPlayer.addBlocksPlaced(skyPlayer.getBlocksPlaced());
    }

    @EventHandler
    public void onPlayerProfileUpdate(MiniPostPlayerProfileDataEvent event) {
        /*int ladderId = Ladder.getLadder(SurvivalGames.getInstance().getGame().getGamemodeString().name(), Ladder.LadderType.FFA).getLadderId();
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
