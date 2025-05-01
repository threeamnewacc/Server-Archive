package net.badlion.shards;

public class ShardAPI {

    private static ShardPlugin plugin;

    public ShardAPI(ShardPlugin plugin) {
        ShardAPI.plugin = plugin;
    }

    public static boolean isMaster() {
        return ShardAPI.plugin.isMaster();
    }

    /**
     * Must be run ASYNC
     *
     * @param data - Use Json. Data sent to the master server, will show up in the MasterSyncEvent on the master server.
     * @return Json String - response from the MasterSyncEvent
     */
    public static String sendToMaster(String data) throws Exception {
        if (ShardAPI.isMaster()) {
            throw new Exception("You can not send data to master from the master server.");
        }

        return ShardAPI.plugin.getShardClient(ShardAPI.plugin.getMasterServer()).syncMasterPlugin(data);
    }

    /**
     * Shutdown a shard
     *
     * @param serverName - Server to shutdown
     */
    public static void shutdownShard(String serverName) throws Exception {
        if (ShardAPI.isMaster()) {
            throw new Exception("You can only shutdown shards from the master server.");
        }

        ShardAPI.plugin.getShardClient(serverName).sendShutdownServer();
    }


}
