package net.badlion.bungeelobby.tasks;

import net.badlion.bungeelobby.BungeeLobby;
import net.badlion.bungeelobby.managers.MCPManager;
import net.md_5.bungee.api.ServerPing;
import org.json.simple.JSONObject;

import javax.xml.bind.DatatypeConverter;

public class MCPProxyPingTask implements Runnable {

    @Override
    public void run() {
        // Contact MCP
        JSONObject data = new JSONObject();
        data.put("bungee", BungeeLobby.BUNGEE_NAME);
        JSONObject response = MCPManager.contactMCP(MCPManager.MCP_MESSAGE.BUNGEE_PROXY_PING, data);
        if (response == null) {
            return;
        }

        // Handle colors and newlines
        // Decoding in base64 with http://stackoverflow.com/a/29991733/1247832
        String motd = new String(DatatypeConverter.parseBase64Binary((String) response.get("motd")));
        motd = motd.replaceAll("%newline", "\n").replaceAll("&([0-9a-fA-Fk-rK-R])", "§$1");

        BungeeLobby.motd = motd;
        BungeeLobby.players = new ServerPing.Players(BungeeLobby.getObjectInteger(response.get("max")), BungeeLobby.getObjectInteger(response.get("online")), new ServerPing.PlayerInfo[0]);
    }
}
