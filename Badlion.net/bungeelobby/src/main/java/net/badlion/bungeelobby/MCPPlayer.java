package net.badlion.bungeelobby;

import net.md_5.bungee.api.config.ServerInfo;
import org.json.simple.JSONObject;

import java.util.UUID;

public class MCPPlayer {

    private UUID uuid;
    private ServerInfo approvedServer;

    public MCPPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public ServerInfo getApprovedServer() {
        return approvedServer;
    }

    public void setApprovedServer(ServerInfo approvedServer) {
        this.approvedServer = approvedServer;
    }
}
