package net.badlion.shards;

import net.badlion.shards.tasks.GetShardInfoTask;
import net.badlion.shards.tasks.SendShardInfoTask;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ShardPlugin extends JavaPlugin {

    private static ShardPlugin plugin;
    public static ShardPlugin getInstance() {
        return ShardPlugin.plugin;
    }

    private static ConcurrentLinkedQueue<ShardRequest> operations = new ConcurrentLinkedQueue<>();

    private String apiURL;
    private String apiKey;

    public ShardPlugin() {

    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        new GetShardInfoTask().runTaskTimerAsynchronously(ShardPlugin.plugin, 1, 1);
        new SendShardInfoTask().runTaskTimerAsynchronously(ShardPlugin.plugin, 1, 1);

        this.apiURL = this.getConfig().getString("api-url");
        this.apiKey = this.getConfig().getString("api-key");
    }

    @Override
    public void onDisable() {

    }

    public static void addToQueue(ShardRequest shardRequest) {
        ShardPlugin.operations.add(shardRequest);
    }

    public String getApiURL() {
        return apiURL;
    }

    public String getApiKey() {
        return apiKey;
    }
}
