package net.badlion.ministats.managers;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import net.badlion.common.libraries.ThreadCommon;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.DynamoDBManager;
import net.badlion.ministats.Game;
import net.badlion.ministats.MiniStats;
import net.badlion.ministats.MiniStatsPlayer;
import net.badlion.ministats.PlayerData;
import net.badlion.ministats.events.MiniMatchEvent;
import net.badlion.ministats.events.MiniPlayerKillEvent;
import net.badlion.ministats.events.MiniPlayerMatchEvent;
import net.badlion.ministats.events.MiniPlayerQuitEvent;
import net.badlion.ministats.events.MiniPlayerStatsSaveEvent;
import net.badlion.ministats.events.MiniPlayerSuicideEvent;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseManager {

    public static int NUM_OF_THREADS = 20;
    public static int CURRENT_DATA_VERSION = 4;

    public static void savePlayerData(PlayerData playerData, Connection connection) {
        if (playerData == null) {
            return;
        }

        Bukkit.getLogger().info("Processing player " + playerData.getUniqueId());
        MiniStatsPlayer miniStatsPlayer = DatabaseManager.getPlayerStats(connection, playerData.getUniqueId());
        if (miniStatsPlayer == null) {
            return;
        }

        // Update KDA
        //double kda;
        //if (finalData.get("deaths") == 0L) {
        //    kda = ((long) finalData.get("kills") + (long) finalData.get("assists"));
        //} else if ((long) finalData.get("kills") + (long) finalData.get("assists") == 0) {
        //    kda = 0;
        //} else {
        //    kda = ((long) finalData.get("kills") + (long) finalData.get("assists")) / ((Long) finalData.get("deaths")).floatValue();
        //}

        miniStatsPlayer.updateWithPlayerData(playerData);

	    // Call MiniPlayerSaveStatsEvent
	    MiniPlayerStatsSaveEvent event = new MiniPlayerStatsSaveEvent(miniStatsPlayer);
	    MiniStats.getInstance().getServer().getPluginManager().callEvent(event);

	    // Was the player stats save event cancelled?
	    if (event.isCancelled()) {
		    return;
	    }

        //String totalPlayerData = finalData.toJSONString();
        //System.out.println("Total Player Data: " + totalPlayerData);
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(miniStatsPlayer.getUpdateQuery());
            miniStatsPlayer.setPreparedStatementParams(ps);
            Gberry.executeUpdate(connection, ps);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(ps);
        }
    }

    public static MiniStatsPlayer getPlayerStats(Connection connection, UUID uuid) {
        String query = "SELECT * FROM " + MiniStats.TABLE_NAME + " WHERE uuid = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid.toString());
            rs = Gberry.executeQuery(connection, ps);

            // Try to see if we have a record
            if (!rs.next()) {
                rs.close();
                rs = null;
            }

            MiniStatsPlayer miniStatsPlayer = MiniStats.getInstance().getMiniStatsPlayerCreator().createMiniStatsPlayer(uuid, rs);

            if (miniStatsPlayer == null) {
                Bukkit.getLogger().info("Unable to create MiniStatsPlayer for " + uuid);
                return null;
            }

            return miniStatsPlayer;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            Gberry.closeComponents(rs, ps);
        }
    }

    // DYNAMODB STUFF

    public static int saveMatchData(final Game game) {
        // Blast through and save all the players
        final List<List<PlayerData>> lists = new ArrayList<>();
        for (int j = 0; j < DatabaseManager.NUM_OF_THREADS; j++) {
            lists.add(new ArrayList<PlayerData>());
        }

        int i = 0;
        for (PlayerData playerData : MiniStats.getInstance().getPlayerDataListener().getPlayerDataMap().values()) {
            // Track whether they won or lost
            if (game.containsWinner(playerData.getUniqueId())) {
                playerData.setWonGame(true);
            } else {
                playerData.setWonGame(false);
            }

            if (++i % DatabaseManager.NUM_OF_THREADS == 0) {
                i = 0;
            }

            lists.get(i).add(playerData);
        }

        ThreadCommon.callThreads(DatabaseManager.NUM_OF_THREADS, new ThreadCommon.ThreadRunnable() {
            @Override
            public void run() {
                Connection connection = null;

                try {
                    connection = Gberry.getConnection();

                    for (PlayerData playerData : lists.get(this.getThreadId())) {
                        DatabaseManager.savePlayerData(playerData, connection);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

	    String matchId = MiniStats.MATCH_ID;
	    if (matchId == null) {
		    matchId = UUID.randomUUID().toString();
	    }

	    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	    final String timestamp = new DateTime(DateTimeZone.UTC).toString(formatter);

        Bukkit.getLogger().log(Level.INFO, "[DYNAMODB] Starting adding items to tables...");
	    StaticCredentialsProvider credentialsProvider = new StaticCredentialsProvider(new BasicAWSCredentials("AKIAJAKVB7EATW3TIXQA", "JMzfs8RpHGzuvwd+STzIA7wDPkwPgaIubgxlTfld"));

	    AmazonDynamoDBClient amazonDynamoDBClient = new AmazonDynamoDBClient(credentialsProvider.getCredentials());
	    amazonDynamoDBClient.setRegion(com.amazonaws.regions.Region.getRegion(Regions.US_EAST_1));

	    DynamoDB dynamo = new DynamoDB(amazonDynamoDBClient);

	    // Match data table
	    Table matchDataTable = dynamo.getTable("match_data");

	    Item item = new Item()
			    .withPrimaryKey("match_id", matchId)
			    .withString("timestamp", timestamp)
			    .withString("match_type", MiniStats.TAG)
			    .withString("game_type", MiniStats.TYPE)
			    .withInt("season", MiniStats.SEASON)
                .withString("start_time", String.valueOf(game.getStartTime()))
                .withString("end_time", String.valueOf(game.getEndTime()))
                .withInt("version", DatabaseManager.CURRENT_DATA_VERSION)
                .withString("map", game.getGWorld() != null ? game.getGWorld().getInternalName() : "None")
                .withJSON("data", DatabaseManager.getMatchJsonData(game).toJSONString());
	    PutItemOutcome putItemOutcome = matchDataTable.putItem(item);

	    Bukkit.getLogger().log(Level.INFO, "[DYNAMODB] Added match data to the table...");


	    List<Item> playerDataItems = new ArrayList<>();

        // Go through each player, add some statistics, and then store their individual stats
        for (PlayerData playerData : MiniStats.getInstance().getPlayerDataListener().getPlayerDataMap().values()) {
            // Always pass null so we have nothing from the actual DB
            MiniStatsPlayer miniStatsPlayer = MiniStats.getInstance().getMiniStatsPlayerCreator().createMiniStatsPlayer(playerData.getUniqueId(), null);

            if (miniStatsPlayer == null) {
                Bukkit.getLogger().info("Unable to create MiniStatsPlayer for " + playerData.getUniqueId());
	            continue;
            }

            // Get the MiniStats player and hook in the rest of the data
            miniStatsPlayer.updateWithPlayerData(playerData);

            // For any other junk the plugins need to add in
            JSONObject jsonMatchPlayer = new JSONObject();

            MiniStats.getInstance().getServer().getPluginManager().callEvent(new MiniPlayerMatchEvent(playerData, jsonMatchPlayer));

            Item playerDataItem = new Item()
                    .withPrimaryKey("user_id", playerData.getUniqueId().toString())
		            .withString("timestamp", timestamp)
                    .withString("match_id", matchId)
                    .withString("match_type", MiniStats.TAG)
                    .withString("game_type", MiniStats.TYPE)
                    .withString("start_time", String.valueOf(game.getStartTime()))
                    .withString("end_time", String.valueOf(game.getEndTime()))
                    .withInt("version", DatabaseManager.CURRENT_DATA_VERSION)
                    .withString("map", game.getGWorld() != null ? game.getGWorld().getInternalName() : "None")
                    .withBoolean("win", game.getWinners().contains(playerData.getUniqueId()))
                    .withInt("season", MiniStats.SEASON)
                    .withString("username", playerData.getUsername())
                    .withDouble("kdr", miniStatsPlayer.getKdr())
                    .withInt("kills", miniStatsPlayer.getKills())
                    .withInt("deaths", miniStatsPlayer.getDeaths())
                    .withJSON("data", jsonMatchPlayer.toJSONString());
            playerDataItems.add(playerDataItem);
        }

	    try {
		    Bukkit.getLogger().log(Level.INFO, "[DYNAMODB] Trying to add player data items: " + playerDataItems.size());
		    DynamoDBManager.batchPutItems("player_data", playerDataItems, dynamo);
	    } catch (InterruptedException e) {
		    e.printStackTrace();
	    }

	    Bukkit.getLogger().log(Level.INFO, "[DYNAMODB] Finished adding items.");

	    return 1;
    }

    private static JSONObject getMatchJsonData(Game game) {
        JSONObject jsonMatch = new JSONObject();
        jsonMatch.put("type", MiniStats.TAG + "_match");
        jsonMatch.put("match_start_time", game.getStartTime());
        jsonMatch.put("match_end_time", game.getEndTime());

        List<String> uuids = new ArrayList<>();

        for (PlayerData playerData : MiniStats.getInstance().getPlayerDataListener().getPlayerDataMap().values()) {
            uuids.add(playerData.getUniqueId().toString());
        }

        jsonMatch.put("uuids", uuids);

        List<String> winners = new ArrayList<>();

        for (UUID playerId : game.getWinners()) {
            winners.add(playerId.toString());
        }
        jsonMatch.put("winners", winners);

        Set<UUID> deadPlayers = new HashSet<>();

        jsonMatch.put("kill_death_data", DatabaseManager.getKillsData(game, deadPlayers));
        jsonMatch.put("suicide_data", DatabaseManager.getSuicideDatas(game, deadPlayers));
        jsonMatch.put("dc_data", DatabaseManager.getDcPlayerDatas(game, deadPlayers));


        jsonMatch.put("player_data", DatabaseManager.getPlayerDataMap(game));

        // Match Event
        MiniStats.getInstance().getServer().getPluginManager().callEvent(new MiniMatchEvent(jsonMatch));

        return jsonMatch;
    }


    private static List<JSONObject> getKillsData(Game game, Set<UUID> deadPlayers) {
        List<JSONObject> kills = new ArrayList<>();

        // Go through each player, add some statistics, and then store their individual stats
        for (PlayerData playerData : MiniStats.getInstance().getPlayerDataListener().getPlayerDataMap().values()) {
            // Add every kill that this player had
            for (PlayerData.PlayerKill playerKill : playerData.getPlayerKills()) {
                MiniStats.getInstance().getServer().getPluginManager().callEvent(new MiniPlayerKillEvent(playerKill));

                JSONObject jsonMatchKillDeath = new JSONObject();
                jsonMatchKillDeath.put("type", MiniStats.TAG + "_match_kill_death");
                jsonMatchKillDeath.put("killer_uuid", playerData.getUniqueId().toString());
                jsonMatchKillDeath.put("killer_username", playerData.getUsername());
                jsonMatchKillDeath.put("dead_uuid", playerKill.getKilledUUID().toString());
                jsonMatchKillDeath.put("dead_username", playerKill.getKilledUsername());
                jsonMatchKillDeath.put("cause", playerKill.getCause());
                jsonMatchKillDeath.put("kill_time", playerKill.getTimestamp());
                jsonMatchKillDeath.put("killer_old_rating", playerKill.getOldPlayerELO());
                jsonMatchKillDeath.put("killer_new_rating", playerKill.getNewPlayerELO());
                jsonMatchKillDeath.put("dead_old_rating", playerKill.getOldKilledPlayerELO());
                jsonMatchKillDeath.put("dead_new_rating", playerKill.getNewKilledPlayerELO());
                jsonMatchKillDeath.put("killer_weapon_type", playerKill.getKillerWeaponType().toString());
                jsonMatchKillDeath.put("season", MiniStats.SEASON);

                deadPlayers.add(playerKill.getKilledUUID());

                List<UUID> uuidList = new ArrayList<>();
                uuidList.add(playerKill.getKilledUUID());
                uuidList.add(playerData.getUniqueId());

                Gberry.addPlayerPotionEffects(jsonMatchKillDeath, uuidList, "totalPotionEffects", playerKill.getPotionEffectsOnDeath());
                Gberry.addPlayerItems(jsonMatchKillDeath, uuidList, "totalArmor", playerKill.getArmorOnDeath());
                Gberry.addPlayerItems(jsonMatchKillDeath, uuidList, "totalInventory", playerKill.getItemsOnDeath());

                Map<String, Object> foodMap = new HashMap<>();
                for (UUID uuid : uuidList) {
                    foodMap.put(uuid.toString(), playerKill.getFoodOnDeath().get(uuid.toString()));
                }
                jsonMatchKillDeath.put("foodMap", foodMap);

                Map<String, Object> healthMap = new HashMap<>();
                for (UUID uuid : uuidList) {
                    healthMap.put(uuid.toString(), playerKill.getHealthOnDeath().get(uuid.toString()));
                }
                jsonMatchKillDeath.put("healthMap", healthMap);

                kills.add(jsonMatchKillDeath);
            }
        }
        return kills;
    }


    private static List<JSONObject> getSuicideDatas(Game game, Set<UUID> deadPlayers) {
        List<JSONObject> suicides = new ArrayList<>();

        for (PlayerData playerData : MiniStats.getInstance().getPlayerDataListener().getPlayerDataMap().values()) {
            if (playerData.getSuicide() != null) {
                JSONObject jsonSuicide = new JSONObject();
                jsonSuicide.put("uuid", playerData.getUniqueId().toString());
                jsonSuicide.put("username", playerData.getUsername());
                jsonSuicide.put("cause", playerData.getSuicide().getCause());
                jsonSuicide.put("kill_time", playerData.getSuicide().getTimestamp());
                jsonSuicide.put("season", MiniStats.SEASON);

                deadPlayers.add(playerData.getUniqueId());

                List<UUID> uuidList = new ArrayList<>();
                uuidList.add(playerData.getUniqueId());

                Map<String, Collection<PotionEffect>> potions = new HashMap<>();
                potions.put(playerData.getUniqueId().toString(), playerData.getSuicide().getPotionEffects());

                Map<String, ItemStack[]> inventories = new HashMap<>();
                inventories.put(playerData.getUniqueId().toString(), playerData.getSuicide().getItems());

                Map<String, ItemStack[]> armors = new HashMap<>();
                armors.put(playerData.getUniqueId().toString(), playerData.getSuicide().getArmor());

                Gberry.addPlayerPotionEffects(jsonSuicide, uuidList, "totalPotionEffects", potions);
                Gberry.addPlayerItems(jsonSuicide, uuidList, "totalArmor", armors);
                Gberry.addPlayerItems(jsonSuicide, uuidList, "totalInventory", inventories);

                Map<String, Object> foodMap = new HashMap<>();
                foodMap.put(playerData.getUniqueId().toString(), playerData.getSuicide().getFood());
                jsonSuicide.put("foodMap", foodMap);

                Map<String, Object> healthMap = new HashMap<>();
                healthMap.put(playerData.getUniqueId().toString(), playerData.getSuicide().getHealth());
                jsonSuicide.put("healthMap", healthMap);

                MiniStats.getInstance().getServer().getPluginManager().callEvent(new MiniPlayerSuicideEvent(playerData, jsonSuicide));

                suicides.add(jsonSuicide);
            }
        }
        return suicides;
    }

    private static List<JSONObject> getDcPlayerDatas(Game game, Set<UUID> deadPlayers) {
        List<JSONObject> dcs = new ArrayList<>();

        for (PlayerData playerData : MiniStats.getInstance().getPlayerDataListener().getPlayerDataMap().values()) {
            // This player dc'd
            if (playerData.getKilledBy().size() == 0 && !deadPlayers.contains(playerData.getUniqueId()) && !playerData.isWonGame()) {
                JSONObject jsonMatchQuit = new JSONObject();
                jsonMatchQuit.put("uuid", playerData.getUniqueId().toString());
                jsonMatchQuit.put("username", playerData.getUsername());
                jsonMatchQuit.put("dc_time", playerData.getLastTimeOnline());
                jsonMatchQuit.put("season", MiniStats.SEASON);

                List<UUID> uuidList = new ArrayList<>();
                uuidList.add(playerData.getUniqueId());

                // Prevent stuff from breaking...
                if (playerData.getPotionEffects() == null) {
                    continue;
                }

                Map<String, Collection<PotionEffect>> potions = new HashMap<>();
                potions.put(playerData.getUniqueId().toString(), playerData.getPotionEffects());

                Map<String, ItemStack[]> inventories = new HashMap<>();
                inventories.put(playerData.getUniqueId().toString(), playerData.getItems());

                Map<String, ItemStack[]> armors = new HashMap<>();
                armors.put(playerData.getUniqueId().toString(), playerData.getArmor());

                Gberry.addPlayerPotionEffects(jsonMatchQuit, uuidList, "totalPotionEffects", potions);
                Gberry.addPlayerItems(jsonMatchQuit, uuidList, "totalArmor", armors);
                Gberry.addPlayerItems(jsonMatchQuit, uuidList, "totalInventory", inventories);

                Map<String, Object> foodMap = new HashMap<>();
                foodMap.put(playerData.getUniqueId().toString(), playerData.getFood());
                jsonMatchQuit.put("foodMap", foodMap);

                Map<String, Object> healthMap = new HashMap<>();
                healthMap.put(playerData.getUniqueId().toString(), playerData.getHealth());
                jsonMatchQuit.put("healthMap", healthMap);

                MiniStats.getInstance().getServer().getPluginManager().callEvent(new MiniPlayerQuitEvent(playerData, jsonMatchQuit));

                dcs.add(jsonMatchQuit);
            }
        }
        return dcs;
    }

    private static Map<String, JSONObject> getPlayerDataMap(Game game) {
        Map<String, JSONObject> playerDatas = new HashMap<>();


        // Go through each player, add some statistics, and then store their individual stats
        for (PlayerData playerData : MiniStats.getInstance().getPlayerDataListener().getPlayerDataMap().values()) {
            // Always pass null so we have nothing from the actual DB
            MiniStatsPlayer miniStatsPlayer = MiniStats.getInstance().getMiniStatsPlayerCreator().createMiniStatsPlayer(playerData.getUniqueId(), null);

            if (miniStatsPlayer == null) {
                Bukkit.getLogger().info("Unable to create MiniStatsPlayer for " + playerData.getUniqueId());
	            continue;
            }

            // Get the MiniStats player and hook in the rest of the data
            miniStatsPlayer.updateWithPlayerData(playerData);

            // Store match information
            JSONObject playerMatchData = miniStatsPlayer.toJSONObject();

	        MiniStats.getInstance().getServer().getPluginManager().callEvent(new MiniPlayerMatchEvent(playerData, playerMatchData));

            playerMatchData.put("kdr", miniStatsPlayer.getKdr());

            JSONObject jsonMatchPlayer = new JSONObject();
            jsonMatchPlayer.put("username", playerData.getUsername());
            jsonMatchPlayer.put("data", playerMatchData);

            MiniStats.getInstance().getServer().getPluginManager().callEvent(new MiniPlayerMatchEvent(playerData, jsonMatchPlayer));

            playerDatas.put(playerData.getUniqueId().toString(), jsonMatchPlayer);
        }

        return playerDatas;
    }

    public static void patchJSON(JSONObject oldJSON, JSONObject newJSON) {
        for (String key : (Set<String>) newJSON.keySet()) {
            // No key found in old
            if (!oldJSON.containsKey(key)) {
                oldJSON.put(key, newJSON.get(key));
            } else if (oldJSON.get(key) instanceof JSONObject) {
                // Recursively scan
                DatabaseManager.patchJSON((JSONObject) oldJSON.get(key), (JSONObject) newJSON.get(key));
            }
        }
    }

}