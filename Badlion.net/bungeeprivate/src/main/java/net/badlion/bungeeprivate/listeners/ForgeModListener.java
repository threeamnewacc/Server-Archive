package net.badlion.bungeeprivate.listeners;

import net.badlion.bungeelobby.BungeeLobby;
import net.badlion.bungeeprivate.BungeePrivatePlugin;
import net.badlion.common.GetCommon;
import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.simple.JSONObject;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ForgeModListener implements Listener {

    private Map<UUID, Map<String, String>> detectedMods = new ConcurrentHashMap<>();
    private Map<String, String> bannedMods = new ConcurrentHashMap<>();

    public ForgeModListener() {
        BungeeCord.getInstance().getScheduler().schedule(BungeePrivatePlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                String query = "SELECT * FROM forge_blacklisted_mods;";

                Connection connection = null;
                PreparedStatement ps = null;
                ResultSet rs = null;

                Map<String, String> bannedMods = new ConcurrentHashMap<>();

                try {
                    connection = BungeeLobby.getSlowConnection();
                    ps = connection.prepareStatement(query);

                    rs = ps.executeQuery();
                    while (rs.next()) {
                        bannedMods.put(rs.getString("mod_name"), rs.getString("mod_version"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if (ps != null) {
                        try {
                            ps.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }

                ForgeModListener.this.bannedMods = bannedMods;
            }
        }, 10, 60, TimeUnit.SECONDS);
    }

    @EventHandler
    public void onPlayerLogin(PostLoginEvent event) {
        this.detectedMods.put(event.getPlayer().getUniqueId(), new ConcurrentHashMap<String, String>());
    }

    @EventHandler
    public void onPlayerServerChange(ServerConnectedEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (player instanceof UserConnection) {
            UserConnection connection = (UserConnection) player;
            if (connection.getServer() != null) {
                // Force send the handshake each time
                connection.getForgeClientHandler().sendHandshakePacket();
            }

            Map<String, String> mods = connection.getModList();
            Map<String, String> currentTrackedMods = this.detectedMods.get(player.getUniqueId());

            for (Map.Entry<String, String> entry : mods.entrySet()) {
                if (!currentTrackedMods.containsKey(entry.getKey())) {
                    // Store in DB and keep track for our cache
                    this.storeForgeModResult(connection, entry.getKey(), entry.getValue());
                    currentTrackedMods.put(entry.getKey(), entry.getValue());

                    // Do we need to alert GCheat as to what is going on
                    String version = this.bannedMods.get(entry.getKey());
                    if (version != null && (version.equalsIgnoreCase("*") || version.equalsIgnoreCase(entry.getValue()))) {
                        this.sendToGCheatServers(player.getUniqueId(), entry.getKey(), entry.getValue());
                    }
                }
            }
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerDisconnectEvent event) {
        this.detectedMods.remove(event.getPlayer().getUniqueId());
    }

    private void sendToGCheatServers(final UUID uuid, final String mod, final String version) {
        BungeeCord.getInstance().getScheduler().runAsync(BungeePrivatePlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject data = new JSONObject();
                    List<String> args = Arrays.asList("GCheat", "ForgeMod", uuid.toString(), mod, version);
                    data.put("sync_event", args);

                    HTTPCommon.executePOSTRequest("http://" + GetCommon.getIpForDB() + ":10111/WebSync/5mPkwHY9xxLUMVwmCCZK3whzjsWMjyBC", data);
                } catch (HTTPRequestFailException e) {
                    // Do nothing
                }
            }
        });
    }

    private void storeForgeModResult(final UserConnection userConnection, final String mod, final String version) {
        BungeeCord.getInstance().getScheduler().runAsync(BungeePrivatePlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                String query = "UPDATE forge_mod_logins SET num_of_logins = num_of_logins + 1, last_login = ? WHERE uuid = ? AND ip = ? and mod_name = ? and mod_version = ?;\n";
                query += "INSERT INTO forge_mod_logins (uuid, ip, mod_name, mod_version, first_login, last_login, num_of_logins) SELECT ?, ?, ?, ?, ?, ?, ? WHERE NOT EXISTS " +
                                 "(SELECT 1 FROM forge_mod_logins WHERE uuid = ? AND ip = ? and mod_name = ? and mod_version = ?);";

                Connection connection = null;
                PreparedStatement ps = null;

                try {
                    connection = BungeeLobby.getSlowConnection();
                    ps = connection.prepareStatement(query);

                    long longIp = BungeeLobby.toLongIP(userConnection.getAddress().getAddress().getAddress());
                    Timestamp ts = new java.sql.Timestamp(new DateTime(DateTimeZone.UTC).getMillis());

                    // Store uuid longIP etc
                    ps.setTimestamp(1, ts);
                    ps.setString(2, userConnection.getUniqueId().toString());
                    ps.setLong(3, longIp);
                    ps.setString(4, mod);
                    ps.setString(5, version);
                    ps.setString(6, userConnection.getUniqueId().toString());
                    ps.setLong(7, longIp);
                    ps.setString(8, mod);
                    ps.setString(9, version);
                    ps.setTimestamp(10, ts);
                    ps.setTimestamp(11, ts);
                    ps.setInt(12, 1);
                    ps.setString(13, userConnection.getUniqueId().toString());
                    ps.setLong(14, longIp);
                    ps.setString(15, mod);
                    ps.setString(16, version);

                    ps.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
                    if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
                }
            }
        });
    }

}
