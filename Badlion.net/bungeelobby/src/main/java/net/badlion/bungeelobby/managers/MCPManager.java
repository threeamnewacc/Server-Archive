package net.badlion.bungeelobby.managers;

import net.badlion.bungeelobby.BungeeLobby;
import net.badlion.bungeelobby.MCPPlayer;
import net.badlion.bungeelobby.util.MCPUtil;
import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.Chat;
import org.json.simple.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class MCPManager {

    public static final int SPAM_TIME_THRESHOLD = 5000;
    public static final int SPAM_COUNT_THRESHOLD = 20;
    public static final JSONObject successResponse = new JSONObject();
    public static final JSONObject failResponse = new JSONObject();

    public enum MCP_MESSAGE {BUNGEE_CHAT_MSG, BUNGEE_PRE_LOGIN, BUNGEE_LOGIN, BUNGEE_POST_LOGIN, BUNGEE_SERVER_CONNECT, BUNGEE_DISCONNECT, BUNGEE_PROXY_PING, BUNGEE_SERVER_SWITCH, BUNGEE_BOOT, BUNGEE_SHUTDOWN, BUNGEE_KEEP_ALIVE, BUNGEE_TAB_COMPLETE, BUNGEE_PLAYER_INFO}

    static {
        MCPManager.successResponse.put("success", "\\o/");
        MCPManager.failResponse.put("error", ":'(");
    }

    public static void startKeepAlive() {
        // Keep Alive + Action Receiver
        BungeeLobby.getInstance().getProxy().getScheduler().schedule(BungeeLobby.getInstance(), new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject data = new JSONObject();
                    data.put("bungee", BungeeLobby.BUNGEE_NAME);
                    JSONObject response = HTTPCommon.executePOSTRequest(BungeeLobby.mcpURL + MCP_MESSAGE.BUNGEE_KEEP_ALIVE.name().toLowerCase().replace("_", "-") + "/" +BungeeLobby.mcpKey, data, BungeeLobby.mcpTimeout);

                    // Timed out and need to re-create bungee information
                    if (response != null && response.equals(MCPManager.failResponse)) {
                        MCPManager.bootMCP();
                        return;
                    }

                    MCPUtil.handleResponse(response);
                } catch (HTTPRequestFailException e) {
                    BungeeLobby.getInstance().getLogger().info("Failed to make keep alive request");
                    BungeeLobby.getInstance().getLogger().info(e.getResponseCode() + "");
                    BungeeLobby.getInstance().getLogger().info(e.getResponse());
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private static final Queue<FailedData> queuedData = new ConcurrentLinkedQueue<>();

    private static final Map<UUID, MCPPlayer> map = new ConcurrentHashMap<>();

    public static MCPPlayer getPlayer(UUID uuid) {
        MCPPlayer mcpPlayer = MCPManager.map.get(uuid);
        if (mcpPlayer == null) {
            mcpPlayer = new MCPPlayer(uuid);
            MCPManager.map.put(uuid, mcpPlayer);
        }

        return mcpPlayer;
    }

    public static MCPPlayer removePlayer(UUID uuid) {
        return MCPManager.map.remove(uuid);
    }

    public static JSONObject bootMCP() {
        JSONObject data = new JSONObject();
        data.put("bungee", BungeeLobby.BUNGEE_NAME);
        return MCPManager.contactMCP(MCP_MESSAGE.BUNGEE_BOOT, data);
    }

    public static JSONObject shutdownMCP() {
        JSONObject data = new JSONObject();
        data.put("bungee", BungeeLobby.BUNGEE_NAME);
        return MCPManager.contactMCP(MCP_MESSAGE.BUNGEE_SHUTDOWN, data);
    }

    private static final Map<UUID, List<Long>> spamMap = new ConcurrentHashMap<>();

    private static boolean isSpammingMessages(UUID uuid) {
        List<Long> list = spamMap.get(uuid);
        if (list == null) {
            list = new ArrayList<>();
            MCPManager.spamMap.put(uuid, list);
        }

        int violations = 0;
        long currentTime = System.currentTimeMillis();

        for (Iterator<Long> iterator = list.iterator(); iterator.hasNext();) {
            Long time = iterator.next();
            if (time + SPAM_TIME_THRESHOLD >= currentTime) {
                ++violations;
            } else {
                iterator.remove();
            }
        }

        return violations >= SPAM_COUNT_THRESHOLD;
    }

    public static JSONObject contactMCP(MCP_MESSAGE msg) {
        return MCPManager.contactMCP(msg, new JSONObject());
    }

    public static JSONObject contactMCP(MCP_MESSAGE msg, JSONObject data) {
        if (data.containsKey("uuid")) {
            UUID uuid = UUID.fromString((String) data.get("uuid"));
            if (MCPManager.isSpammingMessages(uuid) && msg != MCP_MESSAGE.BUNGEE_DISCONNECT) {
                return null;
            }

            // Clean memory
            if (msg == MCP_MESSAGE.BUNGEE_DISCONNECT) {
                MCPManager.spamMap.remove(uuid);
                MCPManager.removePlayer(uuid);
            }
        }

        Iterator<FailedData> it = queuedData.iterator();
        while (it.hasNext()) {
            FailedData oldData = it.next();

            // Did they disconnect from the bungee? If so we can just ignore what they said
            if (oldData.getData().containsKey("uuid")) {
                UUID uuid = UUID.fromString((String) oldData.getData().get("uuid"));
                ProxiedPlayer player = BungeeLobby.getInstance().getProxy().getPlayer(uuid);

                // Remove the msg if they are not trying to disconnect (only thing we care about at this point if they are offline)
                if (player == null && oldData.getMsg() != MCP_MESSAGE.BUNGEE_DISCONNECT) {
                    it.remove();
                    continue;
                }
            }

            try {
                JSONObject response = HTTPCommon.executePOSTRequest(BungeeLobby.mcpURL + oldData.getMsg().name().toLowerCase().replace("_", "-") + "/" + BungeeLobby.mcpKey, oldData.getData(), BungeeLobby.mcpTimeout);

                MCPManager.handleFailedPreviousResponse(oldData.getMsg(), oldData.getData(), response);

                it.remove();
            } catch (HTTPRequestFailException e) {
                BungeeLobby.getInstance().getLogger().info(oldData.getMsg().name().toLowerCase().replace("_", "-"));
                BungeeLobby.getInstance().getLogger().info(oldData.getData().toJSONString());
                BungeeLobby.getInstance().getLogger().info(e.getType().name());
                BungeeLobby.getInstance().getLogger().info(e.getResponseCode() + "");
                BungeeLobby.getInstance().getLogger().info(e.getResponse());
            }
        }

        try {
            JSONObject response = HTTPCommon.executePOSTRequest(BungeeLobby.mcpURL + msg.name().toLowerCase().replace("_", "-") + "/" + BungeeLobby.mcpKey, data, BungeeLobby.mcpTimeout);

            MCPUtil.handleResponse(response);

            return response;
        } catch (HTTPRequestFailException e) {
            BungeeLobby.getInstance().getLogger().info(msg.name().toLowerCase().replace("_", "-"));
            BungeeLobby.getInstance().getLogger().info(data.toJSONString());
            BungeeLobby.getInstance().getLogger().info(e.getType().name());
            BungeeLobby.getInstance().getLogger().info(e.getResponseCode() + "");
            BungeeLobby.getInstance().getLogger().info(e.getResponse());

            // Special errors, we don't let them connect so just bail out
            if (msg == MCP_MESSAGE.BUNGEE_BOOT || msg == MCP_MESSAGE.BUNGEE_PRE_LOGIN || msg == MCP_MESSAGE.BUNGEE_LOGIN || msg == MCP_MESSAGE.BUNGEE_PROXY_PING || msg == MCP_MESSAGE.BUNGEE_KEEP_ALIVE) {
                return null;
            }

            // Timeout errors
            //if (e.getType() == HTTPRequestFailException.REQUEST_FAIL_TYPE.TIMEOUT || e.getResponseCode() == 408 || e.getResponseCode() == 504) {
            // 5/7/
            MCPManager.queuedData.add(new FailedData(msg, data));
            //}
        }

        return null;
    }

    // Helper for things that already failed
    private static void handleFailedPreviousResponse(MCP_MESSAGE msg, JSONObject data, JSONObject response) {
        if (response == null) {
            return;
        }

        if (msg == MCP_MESSAGE.BUNGEE_CHAT_MSG) {
            UUID uuid = UUID.fromString((String) data.get("uuid"));

            ProxiedPlayer player = BungeeLobby.getInstance().getProxy().getPlayer(uuid);
            if (player != null && response.containsKey("send_to_server")) {
                String message = new String(DatatypeConverter.parseBase64Binary((String) response.get("message"))); // Decode
                player.getServer().unsafe().sendPacket(new Chat(message));
            }
        } else if (msg == MCP_MESSAGE.BUNGEE_SERVER_CONNECT) {
            UUID uuid = UUID.fromString((String) data.get("uuid"));

            ProxiedPlayer player = BungeeLobby.getInstance().getProxy().getPlayer(uuid);
            if (player != null && response.containsKey("target")) {
                MCPPlayer mcpPlayer = MCPManager.getPlayer(uuid);

                if (response.containsKey("target")) {
                    mcpPlayer.setApprovedServer(BungeeLobby.getInstance().getProxy().getServerInfo((String) response.get("target")));
                    player.connect(mcpPlayer.getApprovedServer());
                }
            }
        }

        MCPUtil.handleResponse(response);
    }

    private static class FailedData {

        private MCP_MESSAGE msg;
        private JSONObject data;

        public FailedData(MCP_MESSAGE msg, JSONObject data) {
            this.msg = msg;
            this.data = data;
        }

        public MCP_MESSAGE getMsg() {
            return msg;
        }

        public JSONObject getData() {
            return data;
        }

    }

}
