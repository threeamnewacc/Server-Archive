package net.badlion.sgrankedmatchmaker;

import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.ministats.MiniStats;
import net.badlion.sgrankedmatchmaker.commands.StatsCommand;
import net.badlion.sgrankedmatchmaker.listeners.LobbyListener;
import net.badlion.sgrankedmatchmaker.managers.MatchMakingManager;
import net.badlion.sgrankedmatchmaker.services.DefaultMatchMakingService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SGRankedMatchMaker extends JavaPlugin {

    public static int PLAYERS_PER_MATCH = 24;

    private static SGRankedMatchMaker plugin;
    public static SGRankedMatchMaker getInstance() {
        return SGRankedMatchMaker.plugin;
    }

    private int numOfRequiredPlayers = 0;
    private String apiURL;
    private String apiKey;

    public SGRankedMatchMaker() {
        SGRankedMatchMaker.plugin = this;
    }

    @Override
    public void onEnable() {
        MiniStats.TAG = "sg";
        this.saveDefaultConfig();

        this.numOfRequiredPlayers = this.getConfig().getInt("min-player-per-match");
        this.apiURL = this.getConfig().getString("api-url");
        this.apiKey = this.getConfig().getString("api-key");

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

	    //this.getCommand("extrasgrankedmatches").setExecutor(new ExtraSGRankedMatchesCommand());
	    //this.getCommand("giftmatches").setExecutor(new GiftMatchesCommand());
	    //this.getCommand("rankedleft").setExecutor(new RankedLeftCommand());
        this.getCommand("stats").setExecutor(new StatsCommand());

	    //this.getServer().getPluginManager().registerEvents(new GiftMatchesCommand(), this);

        this.getServer().getPluginManager().registerEvents(new MatchMakingManager(new DefaultMatchMakingService()), this);
        //this.getServer().getPluginManager().registerEvents(new RankedLeftManager(), this);
        this.getServer().getPluginManager().registerEvents(new LobbyListener(), this);
	    //this.getServer().getPluginManager().registerEvents(new VoteListener(), this);

        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    Gberry.log("SGRMM", "Querying GetRankedPlayers");
                    final JSONObject response = HTTPCommon.executeGETRequest(SGRankedMatchMaker.this.apiURL + "GetRankedPlayers/" + SGRankedMatchMaker.this.apiKey);
                    if (!response.containsKey("players")) {
                        throw new HTTPRequestFailException(409, "No players found.");
                    }

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            MatchMakingManager.handleMap((Map<String, String>) response.get("players"));
                        }

                    }.runTask(SGRankedMatchMaker.plugin);
                } catch (HTTPRequestFailException e) {
                    Bukkit.getLogger().info("Failed to receive RankedPlayers w/ " + e.getResponseCode());
                }
            }

        }.runTaskTimerAsynchronously(SGRankedMatchMaker.plugin, 20, 20);

        new BukkitRunnable() {

            @Override
            public void run() {
                Gberry.broadcastMessageNoBalance(ChatColor.AQUA + "Matchmaking is searching for a match...please wait (min " + SGRankedMatchMaker.this.numOfRequiredPlayers + " players required).");
            }

        }.runTaskTimer(SGRankedMatchMaker.plugin, 5 * 20, 5 * 20);

        Gberry.loggingTags.add("SGRMM");
        Gberry.loggingTags.add("SGRMM2");
        Gberry.loggingTags.add("SGRMM3");
    }

    @Override
    public void onDisable() {

    }

    public int getNumOfRequiredPlayers() {
        return numOfRequiredPlayers;
    }

    public void createNewMatch(final List<UUID> uuids) {
        Gberry.log("SGRMM", "Creating match");

        final JSONObject jsonObject = new JSONObject();

        List<String> stringUUIDS = new ArrayList<>();
        for (UUID uuid : uuids) {
            stringUUIDS.add(uuid.toString());
        }

        jsonObject.put("users", stringUUIDS);
        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    Gberry.log("SGRMM", "PUT RankedMatch");
                    Gberry.log("SGRMM3", "Data " + jsonObject.toJSONString());
                    final JSONObject response = HTTPCommon.executePUTRequest(SGRankedMatchMaker.this.apiURL + "RankedMatch/" + SGRankedMatchMaker.this.apiKey, jsonObject);
                    Gberry.log("SGRMM3", "Response " + response.toJSONString());
                    if (!response.containsKey("server")) {
                        throw new HTTPRequestFailException(409, "Server missing from key");
                    }

                    Gberry.log("SGRMM", "Got server " + response.get("server") + " to use");

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            // Store which server they belong to
                            for (UUID uuid : uuids) {
                                MatchMakingManager.storePlayerServer(uuid, (String) response.get("server"));
                            }
                        }
                    }.runTask(SGRankedMatchMaker.plugin);

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            for (UUID uuid : uuids) {
                                Player p = SGRankedMatchMaker.plugin.getServer().getPlayer(uuid);
                                if (p != null) {
                                    String server = MatchMakingManager.getPlayerServer(uuid);
                                    Gberry.log("SGRMM2", "Got server " + server + " for uuid " + uuid);
                                    if (server != null) {
                                        SGRankedMatchMaker.plugin.sendPlayerToServer(p, server);
                                    }
                                }
                            }
                        }

                    }.runTaskLater(SGRankedMatchMaker.plugin, 20 * 5);
                } catch (HTTPRequestFailException e) {
                    Bukkit.getLogger().info("Exception with HTTP code " + e.getResponseCode());
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            for (UUID uuid : uuids) {
                                Player p = SGRankedMatchMaker.plugin.getServer().getPlayer(uuid);
                                if (p != null) {
                                    p.sendMessage(ChatColor.RED + "Failed to find an open server. Put back into matchmaking.");
                                    MatchMakingManager.getService().addToTopPriority(uuid);
                                }
                            }
                        }

                    }.runTask(SGRankedMatchMaker.plugin);
                }
            }

        }.runTaskAsynchronously(SGRankedMatchMaker.plugin);
    }

    public void sendPlayerToServer(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        Gberry.log("SGRMM2", "Trying to send " + player.getName() + " to " + server);
        try {

	        // Handle ranked left before game starts
	        //RankedLeftManager.updateOrInsertNumOfGames(player);

            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (Exception e) {
            player.kickPlayer("Error when trying to move you to /server " + server);
            return;
        }

        player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Connecting...");
        player.sendPluginMessage(this, "BungeeCord", b.toByteArray());
    }

    public void prepLobby(Player player) {
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getInventory().clear();
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setExhaustion(0);
        player.teleport(new Location(player.getWorld(), 0.5, 71, 0.5, 180, 0));
        player.setGameMode(GameMode.SURVIVAL);
    }
}
