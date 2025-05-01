package net.badlion.bungeelobby.listeners;

import net.badlion.bungeelobby.BungeeLobby;
import net.badlion.bungeelobby.MCPPlayer;
import net.badlion.bungeelobby.managers.MCPManager;
import net.badlion.bungeelobby.util.MCPUtil;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.packet.Chat;
import net.md_5.bungee.protocol.packet.TabCompleteRequest;
import net.md_5.bungee.protocol.packet.TabCompleteResponse;
import org.json.simple.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.util.List;

public class MCPListener implements Listener {

    @EventHandler
    public void onChat(final ChatEvent event) {
        if (event.getSender() instanceof ProxiedPlayer) {
            // Always cancel. Just emulate Bungee and do it ourselves
            event.setCancelled(true);

            // Allow real commands for Bungee to go through first
            if (event.getMessage().startsWith("/")) {
                boolean result = BungeeCord.getInstance().getPluginManager().dispatchCommand((ProxiedPlayer) event.getSender(), event.getMessage().substring(1));

                if (result) {
                    return;
                }
            }

            final ProxiedPlayer player = (ProxiedPlayer) event.getSender();
            BungeeLobby.getInstance().getProxy().getScheduler().runAsync(BungeeLobby.getInstance(), new Runnable() {
                @Override
                public void run() {
                    // Contact MCP about the command being received
                    JSONObject data = new JSONObject();
                    data.put("bungee", BungeeLobby.BUNGEE_NAME);
                    data.put("uuid", player.getUniqueId().toString());
                    data.put("username", player.getName());
                    data.put("message", DatatypeConverter.printBase64Binary(event.getMessage().getBytes())); // Encode
                    data.put("ip", BungeeLobby.toLongIP(player.getPendingConnection().getAddress().getAddress().getAddress()));
                    data.put("version", player.getPendingConnection().getVersion());

                    JSONObject response = MCPManager.contactMCP(MCPManager.MCP_MESSAGE.BUNGEE_CHAT_MSG, data);
                    if (response == null) {
                        event.setCancelled(true);
                        player.sendMessage(new ComponentBuilder("Error with Badlion Mainframe. Preventing commands/chat message until fixed").create());
                        return;
                    }

                    // If we need to forward the message to the server (command in game or chat)
                    if (response.containsKey("send_to_server")) {
                        String msg = new String(DatatypeConverter.parseBase64Binary((String) response.get("message"))); // Decode
                        player.getServer().unsafe().sendPacket(new Chat(msg));
                    }
                }
            });
        }
    }

    @EventHandler
    public void onPreLogin(final PreLoginEvent event) {
        event.registerIntent(BungeeLobby.getInstance());

        BungeeLobby.getInstance().getProxy().getScheduler().runAsync(BungeeLobby.getInstance(), new Runnable() {
            @Override
            public void run() {
                try {
                    // Contact MCP
                    JSONObject data = new JSONObject();
                    data.put("bungee", BungeeLobby.BUNGEE_NAME);
                    //data.put("uuid", event.getConnection().getUniqueId().toString()); // NOT AVAILABLE YET
                    data.put("username", event.getConnection().getName());
                    data.put("ip", BungeeLobby.toLongIP(event.getConnection().getAddress().getAddress().getAddress()));
                    data.put("version", event.getConnection().getVersion());

                    JSONObject response = MCPManager.contactMCP(MCPManager.MCP_MESSAGE.BUNGEE_PRE_LOGIN, data);
                    if (response == null) {
                        event.setCancelled(true);
                        event.setCancelReason(ChatColor.RED + "Error with Badlion Mainframe. Preventing logins until fixed");
                        return;
                    }

                    if (response.containsKey("cancel")) {
                        event.setCancelled(true);

                        if (response.containsKey("cancel_reason")) {
                            event.setCancelReason(MCPUtil.convertLegacyText((String) response.get("cancel_reason")));
                        }
                    }
                } finally {
                    event.completeIntent(BungeeLobby.getInstance());
                }
            }
        });
    }

    @EventHandler
    public void onLogin(final LoginEvent event) {
        event.registerIntent(BungeeLobby.getInstance());

        BungeeLobby.getInstance().getProxy().getScheduler().runAsync(BungeeLobby.getInstance(), new Runnable() {
            @Override
            public void run() {
                try {
                    // Contact MCP
                    JSONObject data = new JSONObject();
                    data.put("bungee", BungeeLobby.BUNGEE_NAME);
                    data.put("uuid", event.getConnection().getUniqueId().toString());
                    data.put("username", event.getConnection().getName());
                    data.put("ip", BungeeLobby.toLongIP(event.getConnection().getAddress().getAddress().getAddress()));
                    data.put("version", event.getConnection().getVersion());

                    JSONObject response = MCPManager.contactMCP(MCPManager.MCP_MESSAGE.BUNGEE_LOGIN, data);
                    if (response == null) {
                        event.setCancelled(true);
                        event.setCancelReason(ChatColor.RED + "Error with Badlion Mainframe. Preventing logins until fixed");
                        return;
                    }

                    if (response.containsKey("cancel")) {
                        event.setCancelled(true);

                        if (response.containsKey("cancel_reason")) {
                            event.setCancelReason(MCPUtil.convertLegacyText((String) response.get("cancel_reason")));
                        }
                    }
                } finally {
                    event.completeIntent(BungeeLobby.getInstance());
                }
            }
        });
    }

    @EventHandler
    public void onPostLoginEvent(final PostLoginEvent event) {
        BungeeLobby.getInstance().getProxy().getScheduler().runAsync(BungeeLobby.getInstance(), new Runnable() {
            @Override
            public void run() {
                // Contact MCP and tell it that this person is logged in
                JSONObject data = new JSONObject();
                data.put("bungee", BungeeLobby.BUNGEE_NAME);
                data.put("uuid", event.getPlayer().getUniqueId().toString());
                data.put("username", event.getPlayer().getName());
                data.put("ip", BungeeLobby.toLongIP(event.getPlayer().getAddress().getAddress().getAddress()));
                data.put("version", event.getPlayer().getPendingConnection().getVersion());

                MCPManager.contactMCP(MCPManager.MCP_MESSAGE.BUNGEE_POST_LOGIN, data);
            }
        });
    }

    @EventHandler
    public void onPlayerDisconnect(final PlayerDisconnectEvent event) {
        BungeeLobby.getInstance().getProxy().getScheduler().runAsync(BungeeLobby.getInstance(), new Runnable() {
            @Override
            public void run() {
                // Contact MCP and let them know this person disconnected
                JSONObject data = new JSONObject();
                data.put("bungee", BungeeLobby.BUNGEE_NAME);
                data.put("uuid", event.getPlayer().getUniqueId().toString());
                data.put("username", event.getPlayer().getName());
                data.put("ip", BungeeLobby.toLongIP(event.getPlayer().getAddress().getAddress().getAddress()));
                data.put("version", event.getPlayer().getPendingConnection().getVersion());

                MCPManager.contactMCP(MCPManager.MCP_MESSAGE.BUNGEE_DISCONNECT, data);
            }
        });
    }

    @EventHandler
    public void onProxyPing(final ProxyPingEvent event) {
        ServerPing old = event.getResponse();
        ServerPing reply = new ServerPing();

        reply.setDescription(BungeeLobby.motd);
        reply.setPlayers(BungeeLobby.players);

        reply.setFavicon(old.getFaviconObject());
        reply.setVersion(old.getVersion());

        event.setResponse(reply);
    }

    @EventHandler
    public void onServerConnect(final ServerConnectEvent event) {
        // Be clever here, check if we just permitted them to go to someplace
        final MCPPlayer mcpPlayer = MCPManager.getPlayer(event.getPlayer().getUniqueId());

        if (mcpPlayer.getApprovedServer() != null && mcpPlayer.getApprovedServer().equals(event.getTarget())) {
            mcpPlayer.setApprovedServer(null);
            return;
        }

        event.setCancelled(true);

        final String existingServer;
        if (event.getPlayer().getServer() != null) {
            existingServer = event.getPlayer().getServer().getInfo().getName();
        } else {
            existingServer = "null";
        }

        BungeeLobby.getInstance().getProxy().getScheduler().runAsync(BungeeLobby.getInstance(), new Runnable() {
            @Override
            public void run() {
                // Contact MCP
                JSONObject data = new JSONObject();
                data.put("bungee", BungeeLobby.BUNGEE_NAME);
                data.put("uuid", event.getPlayer().getUniqueId().toString());
                data.put("username", event.getPlayer().getName());
                data.put("ip", BungeeLobby.toLongIP(event.getPlayer().getAddress().getAddress().getAddress()));
                data.put("version", event.getPlayer().getPendingConnection().getVersion());
                data.put("existing_server", existingServer);
                data.put("target", event.getTarget().getName());

                JSONObject response = MCPManager.contactMCP(MCPManager.MCP_MESSAGE.BUNGEE_SERVER_CONNECT, data);
                if (response == null) {
                    event.getPlayer().sendMessage(new ComponentBuilder("Error with Badlion Mainframe. Preventing server connections until it is resolved.").color(ChatColor.RED).create());
                    event.getPlayer().sendMessage(new ComponentBuilder("We will automatically retry to connect you to the server when the Badlion Mainframe is online").color(ChatColor.RED).create());
                    return;
                }

                // Set server for second pass
                if (response.containsKey("target")) {
                    mcpPlayer.setApprovedServer(BungeeLobby.getInstance().getProxy().getServerInfo((String) response.get("target")));
                    event.getPlayer().connect(mcpPlayer.getApprovedServer());
                }
            }
        });
    }

    @EventHandler
    public void onServerSwitch(final ServerSwitchEvent event) {
        BungeeLobby.getInstance().getProxy().getScheduler().runAsync(BungeeLobby.getInstance(), new Runnable() {
            @Override
            public void run() {
                // Contact MCP
                JSONObject data = new JSONObject();
                data.put("bungee", BungeeLobby.BUNGEE_NAME);
                data.put("uuid", event.getPlayer().getUniqueId().toString());
                data.put("username", event.getPlayer().getName());
                data.put("ip", BungeeLobby.toLongIP(event.getPlayer().getAddress().getAddress().getAddress()));
                data.put("version", event.getPlayer().getPendingConnection().getVersion());
                data.put("server", event.getPlayer().getServer().getInfo().getName());

                MCPManager.contactMCP(MCPManager.MCP_MESSAGE.BUNGEE_SERVER_SWITCH, data);
            }
        });
    }

    @EventHandler
    public void onTabComplete(final TabCompleteEvent event) {
        if (event.getCursor().startsWith("/") && event.getSender() instanceof ProxiedPlayer) {
            event.setCancelled(true);

            final ProxiedPlayer player = (ProxiedPlayer) event.getSender();

            BungeeLobby.getInstance().getProxy().getScheduler().runAsync(BungeeLobby.getInstance(), new Runnable() {
                @Override
                public void run() {
                    // Contact MCP
                    JSONObject data = new JSONObject();
                    data.put("bungee", BungeeLobby.BUNGEE_NAME);
                    data.put("uuid", player.getUniqueId().toString());
                    data.put("username", player.getName());
                    data.put("ip", BungeeLobby.toLongIP(player.getAddress().getAddress().getAddress()));
                    data.put("version", player.getPendingConnection().getVersion());
                    data.put("message", DatatypeConverter.printBase64Binary(event.getCursor().getBytes())); // Encode

                    JSONObject response = MCPManager.contactMCP(MCPManager.MCP_MESSAGE.BUNGEE_TAB_COMPLETE, data);
                    if (response == null) {
                        return;
                    }

                    if (response.containsKey("suggestions")) {
                        List<String> suggestions = (List<String>) response.get("suggestions");
                        if (!suggestions.isEmpty()) {
                            player.getPendingConnection().unsafe().sendPacket(new TabCompleteResponse(suggestions));
                        }
                    } else {
                        // Send Tab request to server
                        event.getReceiver().unsafe().sendPacket(event.getTabCompleteRequest());
                    }
                }
            });
        }
    }

}
