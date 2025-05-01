package net.badlion.shards;

import java.util.HashMap;
import java.util.Map;

public class Conf {

    private boolean master = false;

    private int port = 50051;

    // Server name and ip:port
    private Map<String, String> servers = new HashMap<>();

    private String serverName = "SET_SERVER_NAME_PLS";

    public void initDefaults(){
        servers.put("test1", "127.0.0.1:50051");
        servers.put("test2", "127.0.0.1:50052");
    }

    public String getServerName() {
        return serverName;
    }

    public Map<String, String> getServers() {
        return servers;
    }

    public int getPort() {
        return port;
    }

    public boolean isMaster() {
        return master;
    }
}
