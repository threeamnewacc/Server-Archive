package net.badlion.survivalgames;

//import com.trc202.CombatTag.CombatTag;

import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.gpermissions.GPermissions;
import net.badlion.ministats.MiniStats;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.survivalgames.commands.*;
import net.badlion.survivalgames.inventories.ServerSelectorInventory;
import net.badlion.survivalgames.inventories.SkullPlayerInventory;
import net.badlion.survivalgames.listeners.*;
import net.badlion.survivalgames.managers.RankedLeftManager;
import net.badlion.survivalgames.managers.RatingManager;
import net.badlion.survivalgames.managers.SGMapManager;
import net.badlion.survivalgames.managers.SGPlayerManager;
import net.badlion.worldrotator.WorldRotator;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class SurvivalGames extends JavaPlugin {

    public static String SG_PREFIX = ChatColor.GOLD + "" + ChatColor.BOLD + "[" + ChatColor.RESET + ChatColor.DARK_AQUA + "BadlionSG" + ChatColor.GOLD + "" + ChatColor.BOLD + "] " + ChatColor.RESET;
    private static SurvivalGames plugin;
    private static SGGame game;

    final private Set<UUID> rankedUUIDs = new HashSet<>();
    final private List<String> rankedUsernames = new ArrayList<>();
    private List<Integer> ratings = new ArrayList<>();

    public static SurvivalGames getInstance() {
        return SurvivalGames.plugin;
    }

    public void setGame(SGGame game) {
        SurvivalGames.game = game;
    }

    public SGGame getGame() {
        return SurvivalGames.game;
    }

    public enum SGState {
        PRE_START, VOTING, START_COUNTDOWN, STARTED, DEATH_MATCH_COUNTDOWN, DEATH_MATCH, END
    }

    private SGState state = SGState.PRE_START;

    private MiniStats miniStats;
    private WorldRotator worldRotator;
    //private CombatTag combatTag;

    private ItemStack[] spectatorItems = new ItemStack[36];

    public SurvivalGames() {
        SurvivalGames.plugin = this;
    }

    private String apiURL;
    private String apiKey;
    private String sigURL;
    private String sigKey;
    private String unrankedURL;
    private String unrankedKey;
    private UUID serverUUID;

    private boolean contactAPI = true;

    private String serverType = "NA";

    private static Set<String> whiteListedPlayers = new HashSet<>(); // List of NAMES for players that have been granted access to the server

    @Override
    public void onEnable() {
	    SmellyInventory.initialize(this, true);

        this.saveDefaultConfig();

        this.contactAPI = this.getConfig().getBoolean("sg.api", true);
        this.serverType = this.getConfig().getString("sg.loc", "NA");
        this.serverUUID = UUID.randomUUID();
        /*this.apiURL = this.getConfig().getString("api-url");
        this.apiKey = this.getConfig().getString("api-key");
        this.sigURL = this.getConfig().getString("sig-url");
        this.sigKey = this.getConfig().getString("sig-key");
        this.unrankedURL = this.getConfig().getString("unranked-url");
        this.unrankedKey = this.getConfig().getString("unranked-key");*/

        this.apiURL = "http://127.0.0.1:10011/";
        this.apiKey = this.serverType.equals("NA") ? "T2dBwvnOhWBmgamcYvqtbWS5VSbn5nW3" : this.serverType.equals("AU") ? "T2dBwvnOhWBmgamcYvqtbWS5VSbn5nW3"  : "T2dBwvnOhWBmgamcYvqtbWS5VSbn5nW3";
        this.sigURL = "http://158.69.52.59:20011/";
        this.sigKey = "yEc2auJuGnkw7uNVcRwPZPQcaSNW92Hu";
        this.unrankedURL = "http://127.0.0.1:10012/";
        this.unrankedKey = this.serverType.equals("NA") ? "CgQgKWAx654N7DiSIyLh23eAQJkO1IYs" : this.serverType.equals("AU") ? "CgQgKWAx654N7DiSIyLh23eAQJkO1IYs" : "CgQgKWAx654N7DiSIyLh23eAQJkO1IYs";

        this.miniStats = (MiniStats) this.getServer().getPluginManager().getPlugin("MiniStats");
        this.worldRotator = (WorldRotator) this.getServer().getPluginManager().getPlugin("WorldRotator");
        //this.combatTag = (CombatTag) this.getServer().getPluginManager().getPlugin("CombatTag");

        Gberry.loggingTags.add("SG");
        Gberry.loggingTags.add("SGRanked");
        Gberry.loggingTags.add("SGRanked2");
        Gberry.loggingTags.add("RATING");

	    // Set ministats player creator
	    MiniStats.getInstance().setMiniStatsPlayerCreator(new SGMiniStatsPlayer.SGMiniStatsPlayerCreator());

        Ladder.initialize();

        // Initialize Managers
        SGMapManager.initialize();
        new SGPlayerManager();
        new RatingManager();
        SGPlayerManager.initialize();

	    // Inventories YAY
        ServerSelectorInventory.initialize();
	    SkullPlayerInventory.initialize();

        // Listeners
        this.getServer().getPluginManager().registerEvents(new AlivePlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new MiniStatsListener(), this);
        this.getServer().getPluginManager().registerEvents(new DeathMatchListener(), this);
        this.getServer().getPluginManager().registerEvents(new GlobalListener(), this);
        this.getServer().getPluginManager().registerEvents(new LobbyListener(), this);
        this.getServer().getPluginManager().registerEvents(new PreGameListener(), this);
        this.getServer().getPluginManager().registerEvents(new RankedLeftManager(), this);
        this.getServer().getPluginManager().registerEvents(new SpectatorListener(), this);

        //BungeeCord
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // Register Commands
        this.getCommand("configmap").setExecutor(new ConfigMapCommand());
	    this.getCommand("debug").setExecutor(new DebugCommand());
        this.getCommand("stats").setExecutor(new StatsCommand());
	    this.getCommand("vote").setExecutor(new VoteCommand());
        this.getCommand("wl").setExecutor(new WhitelistCommand());

	    this.spectatorItems[0] = new ItemStack(Material.COMPASS);

	    ItemStack clock = new ItemStack(Material.WATCH);
	    ItemMeta clockMeta = clock.getItemMeta();
	    clockMeta.setDisplayName(ChatColor.AQUA + "Alive Players");
	    clock.setItemMeta(clockMeta);
	    this.spectatorItems[1] = clock;

        ItemStack unrankedSGItem = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta unrankedSGMeta = unrankedSGItem.getItemMeta();
        unrankedSGMeta.setDisplayName(ChatColor.BLUE + ChatColor.BOLD.toString() + "Unranked SG");
        unrankedSGItem.setItemMeta(unrankedSGMeta);

        ItemStack rankedSGItem = new ItemStack(Material.FISHING_ROD);
        ItemMeta rankedSGMeta = rankedSGItem.getItemMeta();
        rankedSGMeta.setDisplayName(ChatColor.BLUE + ChatColor.BOLD.toString() + (Gberry.serverName.toLowerCase().startsWith("rsg") ? "NA " : "EU ") + "Ranked SG");
        rankedSGItem.setItemMeta(rankedSGMeta);

        this.spectatorItems[8] = unrankedSGItem;

        MiniStats.TAG = "sg";
        MiniStats.TABLE_NAME = "sg_ministats";

        this.getServer().setMaxPlayers(24);

        if (this.contactAPI) {
            this.getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("server_name", Gberry.serverName.toUpperCase());

                        if (SurvivalGames.this.getState() == SGState.PRE_START) {
                            jsonObject.put("status", "waiting");
                        } else if (SurvivalGames.this.getState() == SGState.VOTING) {
                            jsonObject.put("status", "voting");
                        } else {
                            jsonObject.put("status", "match");
                        }

                        jsonObject.put("player_count", SurvivalGames.getInstance().getServer().getOnlinePlayers().size());
                        final JSONObject response = HTTPCommon.executePUTRequest(SurvivalGames.this.unrankedURL + "KeepAlive/" + SurvivalGames.this.getServerUUID().toString() + "/" + SurvivalGames.this.unrankedKey, jsonObject);
                        SurvivalGames.getInstance().getServer().getScheduler().runTask(SurvivalGames.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                // Clear the inventory
                                ServerSelectorInventory.unrankedSGInventory.clear();

                                int counter = 0;
                                if (response.containsKey("servers")) {
                                    for (Map<String, Object> server : (List<Map<String, Object>>) response.get("servers")) {
                                        if (counter < 54) {
                                            ServerSelectorInventory.unrankedSGInventory.addItem(
                                                                                                       ServerSelectorInventory.createUnrankedSGItem((String) server.get("server_name"),
                                                                                                                                                           (long) server.get("player_count"), (String) server.get("status")));
                                        }

                                        counter++;
                                    }
                                }
                            }
                        });
                    } catch (HTTPRequestFailException e) {
                        Bukkit.getLogger().info("Failed to reach keep alive with error " + e.getResponseCode());
                    } catch (Exception e) {
                        // I'm being lazy about CME's
                        e.printStackTrace();
                    }
                }
            }, 0, 3 * 20);
        }
    }

    @Override
    public void onDisable() {

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

    public SGState getState() {
        return state;
    }

    public void setState(SGState state) {
        Gberry.log("SG", "Switched state to " + state.name());

        this.state = state;
    }

    public MiniStats getMiniStats() {
        return miniStats;
    }

    public WorldRotator getWorldRotator() {
        return worldRotator;
    }

    public ItemStack[] getSpectatorItems() {
        return spectatorItems;
    }

    public boolean addRankedUUID(UUID uuid) {
        synchronized (this.rankedUUIDs) {
            return this.rankedUUIDs.add(uuid);
        }
    }

    public boolean containsRankedUUID(UUID uuid) {
        synchronized (this.rankedUUIDs) {
            return this.rankedUUIDs.contains(uuid);
        }
    }

    public void sendPlayerToServer(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(this, "BungeeCord", b.toByteArray());
    }

    public void addMuteBanPerms(Player player) {
        if (player.hasPermission("badlion.sgmod")) {
            GPermissions.giveModPermissions(player);
        } else if (player.hasPermission("badlion.sgtrial")) {
            GPermissions.giveTrialPermissions(player);
        }
    }

    public boolean getApi() {
        return this.contactAPI;
    }

    public String getApiURL() {
        return apiURL;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getSigURL() {
        return sigURL;
    }

    public String getSigKey() {
        return sigKey;
    }

    public UUID getServerUUID() {
        return serverUUID;
    }

    //public CombatTag getCombatTag() {
    //    return combatTag;
    //}


    public Set<UUID> getRankedUUIDs() {
        return rankedUUIDs;
    }

    public List<Integer> getRatings() {
        return ratings;
    }

    public void setRatings(List<Integer> ratings) {
        this.ratings = ratings;
    }

    // TODO: Fix this shit
    public static void addWhiteListedPlayer(String name) {
        SurvivalGames.whiteListedPlayers.add(name);
    }

    public static boolean isInWhitelist(String name){
        return SurvivalGames.whiteListedPlayers.contains(name);
    }

}
