package net.badlion.shards;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.badlion.gspigot.ProtocolOutHook;
import net.badlion.gspigot.ProtocolScheduler;
import net.badlion.gspigot.TinyProtocol;
import net.badlion.shards.grpc.ShardInstanceClient;
import net.badlion.shards.grpc.ShardInstanceServer;
import net.badlion.shards.gson.ItemStackTypeAdaptorFactory;
import net.badlion.shards.gson.PotionEffectTypeAdapter;
import net.badlion.shards.listener.BlockListener;
import net.badlion.shards.listener.EntityListener;
import net.badlion.shards.listener.InventoryListener;
import net.badlion.shards.listener.PlayerListener;
import net.badlion.shards.manager.EntitySyncManager;
import net.badlion.shards.manager.PlayerSyncManager;
import net.badlion.shards.type.Border;
import net.minecraft.server.v1_7_R4.PacketPlayInUseEntity;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ShardPlugin extends JavaPlugin {

    private static ShardPlugin plugin;

    private Gson gson;
    private Gson gsonSmall;

    private Conf conf;
    private MasterConf masterConf;

    private ShardInstanceServer shardInstanceServer;

    private Map<String, ShardInstanceClient> shardInstanceClientMap = new HashMap<>();

    private PlayerSyncManager playerSyncManager;
    private EntitySyncManager entitySyncManager;

    private TinyProtocol protocol;

    private String masterServer = null;

    public static ShardPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        ShardPlugin.plugin = this;

        // TODO: TEMP SINCE I NEED TO TEST LOCAL
        this.protocol = this.getServer().getTinyProtocol(this);
        this.protocol.setAllowConnections(true);

        this.gson = new GsonBuilder().registerTypeAdapterFactory(new ItemStackTypeAdaptorFactory()).setPrettyPrinting().create();
        this.gsonSmall = new GsonBuilder().registerTypeAdapter(PotionEffect.class, new PotionEffectTypeAdapter()).registerTypeAdapterFactory(new ItemStackTypeAdaptorFactory()).create();

        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        this.loadConf();

        // Load master config before it is sent out to the slave servers
        if (this.conf.isMaster()) {
            this.loadMasterConf();
        }

        // Figure out what server is master, the master server needs to startup first!
        if (!this.conf.isMaster()) {
            for (Map.Entry<String, String> entry : this.conf.getServers().entrySet()) {
                if (entry.getKey().equals(this.conf.getServerName())) continue;

                ShardInstanceClient client;
                if (this.plugin.getShardInstanceClientMap().containsKey(entry.getKey())) {
                    client = this.plugin.getShardInstanceClientMap().get(entry.getKey());
                } else {
                    String con = entry.getValue();
                    client = new ShardInstanceClient(plugin, con.split(":")[0], Integer.valueOf(con.split(":")[1]));
                    this.plugin.getShardInstanceClientMap().put(entry.getKey(), client);
                }
                if (client.isMasterServer()) {
                    this.masterServer = entry.getKey();
                }
            }

            if (this.masterServer == null) {
                this.getLogger().log(Level.SEVERE, "!!!! No master server was found, the master server must be running BEFORE the slaves !!!!");
                this.getServer().shutdown();
            }
        }

        this.playerSyncManager = new PlayerSyncManager(this);
        this.entitySyncManager = new EntitySyncManager(this);

        // Start up gRPC server
        this.shardInstanceServer = new ShardInstanceServer(this);

        // Register Events
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        this.getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        this.getServer().getPluginManager().registerEvents(new EntityListener(this), this);

        // Register Bungeecord Channel
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // Register the Tiny Protocol packet listener
        ProtocolScheduler.addHook(new ProtocolOutHook() {
            @Override
            public Object handlePacket(Player receiver, Object packet) {
                if (packet instanceof PacketPlayInUseEntity) {

                }

                return packet;
            }

            @Override
            public ProtocolPriority getPriority() {
                return ProtocolPriority.MEDIUM;
            }
        });
    }

    @Override
    public void onDisable() {
        System.err.println("*** Shutting down gRPC player sync server");
        this.shardInstanceServer.stop();
        System.err.println("*** Player sync server shut down");
    }


    public void loadConf() {
        File confFile = new File(getDataFolder(), "conf.json");
        try (FileReader reader = new FileReader(confFile)) {
            this.conf = this.getGson().fromJson(reader, Conf.class);
        } catch (FileNotFoundException ex) {
            // populate conf with defaults and save to file
            this.conf = new Conf();
            this.conf.initDefaults();
            this.saveConf();
            this.getLogger().log(Level.INFO, "Please configure shards and then start the server. ./plugins/Shards/conf.json");
            this.getServer().shutdown();
        } catch (Exception ex) {
            this.getLogger().warning("Failed to load conf.json");
            ex.printStackTrace();
        }
    }

    public void saveConf() {
        File confFile = new File(getDataFolder(), "conf.json");
        try (FileWriter writer = new FileWriter(confFile)) {
            this.getGson().toJson(conf, writer);
        } catch (Exception ex) {
            this.getLogger().warning("Failed to save conf.json");
            ex.printStackTrace();
        }
    }

    public void loadMasterConf() {
        File confFile = new File(getDataFolder(), "master.json");
        try (FileReader reader = new FileReader(confFile)) {
            this.masterConf = this.getGson().fromJson(reader, MasterConf.class);
        } catch (FileNotFoundException ex) {
            // populate conf with defaults and save to file
            this.masterConf = new MasterConf();
            this.masterConf.generateDefaults(100, 500);
            this.saveMasterConf();
        } catch (Exception ex) {
            this.getLogger().warning("Failed to load master.json");
            ex.printStackTrace();
        }
    }

    public void saveMasterConf() {
        File confFile = new File(getDataFolder(), "master.json");
        try (FileWriter writer = new FileWriter(confFile)) {
            this.getGson().toJson(this.masterConf, writer);
        } catch (Exception ex) {
            this.getLogger().warning("Failed to save master.json");
            ex.printStackTrace();
        }
    }


    public Gson getGson() {
        return gson;
    }

    public Gson getGsonSmall() {
        return gsonSmall;
    }

    public Conf getConf() {
        return conf;
    }

    public MasterConf getMasterConf() {
        return masterConf;
    }

    public boolean isMaster() {
        return conf.isMaster();
    }

    public void setMasterConf(MasterConf masterConf) {
        this.masterConf = masterConf;
    }

    public Map<String, ShardInstanceClient> getShardInstanceClientMap() {
        return shardInstanceClientMap;
    }

    public PlayerSyncManager getPlayerSyncManager() {
        return playerSyncManager;
    }

    public EntitySyncManager getEntitySyncManager() {
        return entitySyncManager;
    }

    public TinyProtocol getProtocol() {
        return protocol;
    }

    public String getMasterServer() {
        return masterServer;
    }

    public ShardInstanceClient getShardClient(String serverName) {
        ShardInstanceClient client = null;
        if (this.plugin.getShardInstanceClientMap().containsKey(serverName)) {
            client = this.plugin.getShardInstanceClientMap().get(serverName);
        } else {
            String connectionInfo = plugin.getConf().getServers().get(serverName);
            if (connectionInfo == null) {
                return null;
            }
            client = new ShardInstanceClient(plugin, connectionInfo.split(":")[0], Integer.valueOf(connectionInfo.split(":")[1]));
            this.plugin.getShardInstanceClientMap().put(serverName, client);
        }
        return client;
    }

    public String getShardAt(Location location) {
        for (Map.Entry<String, Border> entry : this.masterConf.getShardBorderMap().entrySet()) {
            if (entry.getValue().isInside(location)) {
                return entry.getKey();
            }
        }
        // Return null if they are no inside a shard, this means the player is in a buffer zone
        return null;
    }

    public List<String> getNearbyShards(Location location, int radius) {
        List<String> shards = new ArrayList<>();

        for (Map.Entry<String, Border> entry : this.masterConf.getShardBorderMap().entrySet()) {

            double distanceX = Math.max(0, Math.max(entry.getValue().getMinX() - location.getX(), location.getX() - entry.getValue().getMaxX()));
            double distanceZ = Math.max(0, Math.max(entry.getValue().getMinZ() - location.getZ(), location.getZ() - entry.getValue().getMaxZ()));

            double distance = Math.sqrt(distanceX*distanceX + distanceZ*distanceZ);

            if (distance <= radius) {
                shards.add(entry.getKey());
            }
        }
        return shards;
    }


    public List<ShardInstanceClient> getNearbyShardClients(Location location, int radius) {
        List<String> shards = this.getNearbyShards(location, radius);

        List<ShardInstanceClient> clients = new ArrayList<>();

        for (String shard : shards) {

            if (shard.equals(this.plugin.getConf().getServerName())) continue;

            clients.add(this.plugin.getShardClient(shard));
        }
        return clients;
    }

    public void sendToServer(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(this.plugin, "BungeeCord", b.toByteArray());
    }
}
