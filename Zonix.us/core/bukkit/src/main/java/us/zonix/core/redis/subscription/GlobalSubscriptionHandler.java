package us.zonix.core.redis.subscription;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.core.CorePlugin;
import us.zonix.core.api.request.PlayerRequest;
import us.zonix.core.profile.Profile;
import us.zonix.core.punishment.Punishment;
import us.zonix.core.punishment.PunishmentType;
import us.zonix.core.rank.Rank;
import us.zonix.core.server.ServerData;
import us.zonix.core.server.ServerState;
import us.zonix.core.server.ServerType;
import us.zonix.core.shared.redis.subscription.JedisSubscriptionHandler;
import us.zonix.core.symbols.Symbol;
import us.zonix.core.util.CC;
import us.zonix.core.util.Clickable;
import us.zonix.core.util.UUIDType;

import java.util.UUID;

public class GlobalSubscriptionHandler implements JedisSubscriptionHandler<JsonObject> {

    private static final String REPORT_FORMAT = CC.GRAY + "[" + CC.AQUA + "Report" + CC.GRAY + "] (" + CC.AQUA + "{server}" + CC.GRAY + ")" + CC.GRAY + ": " + CC.RESET + "{name}" + CC.GRAY + " reported " + CC.RESET + "{target}" + CC.GRAY + " - " + CC.YELLOW + "{reason}";
    private static final String REQUEST_FORMAT = CC.GRAY + "[" + CC.AQUA + "Request" + CC.GRAY + "] (" + CC.AQUA + "{server}" + CC.GRAY + ")" + CC.GRAY + ": " + CC.RESET + "{name}" + CC.GRAY + " - " + CC.YELLOW + "{reason}";
    private static final String STAFF_CHAT_FORMAT = CC.GRAY + "[" + CC.LIGHT_PURPLE + "Staff" + CC.GRAY + "] {name}" + CC.RESET + ": {message}";
    private static final String STAFF_JOIN_FORMAT = CC.GRAY + "[" + CC.LIGHT_PURPLE + "Staff" + CC.GRAY + "] {name}" + CC.GRAY + " joined {server}";

    @Override
    public void handleMessage(JsonObject object) {
        String type = object.get("type").getAsString();
        JsonObject data = object.get("data").getAsJsonObject();

        if (type.equalsIgnoreCase("punishment")) {
            Punishment punishment = Punishment.fromJson(data);
            punishment.announce(data.get("name").getAsString(), data.get("sender").getAsString(), data.get("silent").getAsBoolean(), punishment.isRemoved());

            UUID uuid = UUIDType.fromString(data.get("uuid").getAsString());

            Profile profile = Profile.getByUuidIfAvailable(uuid);

            if (profile == null) {
                return;
            }

            if (punishment.isRemoved()) {
                profile.getPunishments().removeIf((other) -> {
                    return other.getId() == punishment.getId();
                });
            }

            profile.getPunishments().add(punishment);

            Player player = Bukkit.getPlayer(punishment.getUuid());

            if (player != null && !punishment.isRemoved() && punishment.getType() != PunishmentType.MUTE) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.kickPlayer(punishment.getType().getMessage());

                        if (punishment.getType() == PunishmentType.BLACKLIST) {
                            for (Player other : Bukkit.getOnlinePlayers()) {
                                if (other.getAddress().getAddress().getHostAddress().equals(player.getAddress().getAddress().getHostAddress())) {
                                    other.kickPlayer(punishment.getType().getMessage());
                                }
                            }
                        }
                    }
                }.runTask(CorePlugin.getInstance());
            }
        } else if (type.equalsIgnoreCase("rank")) {
            UUID uuid = UUIDType.fromString(data.get("uuid").getAsString());
            Rank rank = Rank.valueOf(data.get("rank").getAsString());

            Profile profile = Profile.getByUuidIfAvailable(uuid);

            if (profile != null && profile.getRank() != rank) {
                profile.setRank(rank);

                Symbol symbol = Symbol.getDefaultSymbolByRank(profile.getRank());
                profile.setSymbol(symbol);

                CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(new PlayerRequest.UpdateSymbolRequest(profile.getUuid(), symbol));

                Player player = Bukkit.getPlayer(profile.getUuid());

                if (player != null) {
                    player.sendMessage(ChatColor.GREEN + "Your rank has been updated to " + rank.getName() + ".");
                    profile.updateTabList(rank);

                    if (CorePlugin.getInstance().getServerType() == ServerType.HUB) {
                        profile.setDonatorArmor();
                    }
                }

                Bukkit.getServer().getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), profile::save);
            }
        } else if (type.equalsIgnoreCase("staffchat")) {
            String name = data.get("name").getAsString();
            Rank rank = Rank.valueOf(data.get("rank").getAsString());
            String message = data.get("message").getAsString();

            String toSend = STAFF_CHAT_FORMAT
                    .replace("{name}", rank.getColor() + name)
                    .replace("{message}", message);

            for (Player player : Bukkit.getOnlinePlayers()) {
                Profile profile = Profile.getByUuidIfAvailable(player.getUniqueId());

                if (profile != null && profile.getRank().isAboveOrEqual(Rank.TRIAL_MOD)) {
                    player.sendMessage(toSend);
                }
            }
        } else if (type.equalsIgnoreCase("staffjoin")) {
            String name = data.get("name").getAsString();
            Rank rank = Rank.valueOf(data.get("rank").getAsString());
            String server = data.get("server").getAsString();

            String toSend = STAFF_JOIN_FORMAT
                    .replace("{name}", rank.getColor() + name)
                    .replace("{server}", server);

            for (Player player : Bukkit.getOnlinePlayers()) {
                Profile profile = Profile.getByUuidIfAvailable(player.getUniqueId());

                if (profile != null && profile.getRank().isAboveOrEqual(Rank.TRIAL_MOD)) {
                    player.sendMessage(toSend);
                }
            }
        } else if (type.equalsIgnoreCase("announce")) {
            String name = data.get("name").getAsString();
            String game = data.get("game").getAsString();
            String server = data.get("server").getAsString();

            StringBuilder message = new StringBuilder(CC.GRAY)
                    .append("[")
                    .append(CC.RED)
                    .append("Announce")
                    .append(CC.GRAY)
                    .append("] ")
                    .append(name)
                    .append(CC.RESET)
                    .append(" is playing ")
                    .append(CC.RED)
                    .append(CC.BOLD)
                    .append(game)
                    .append(CC.RESET)
                    .append(" on ")
                    .append(CC.RED)
                    .append(CC.BOLD)
                    .append(server)
                    .append(CC.RESET)
                    .append(". ")
                    .append(CC.GRAY)
                    .append("[")
                    .append(CC.GREEN)
                    .append("Click to play")
                    .append(CC.GRAY)
                    .append("]");

            Clickable clickable = new Clickable(message.toString(), CC.YELLOW + "Click to join!", "/joinqueue " + server);

            for (Player player : Bukkit.getOnlinePlayers()) {
                clickable.sendToPlayer(player);
            }
        } else if (type.equalsIgnoreCase("whitelist")) {
            String action = data.get("action").getAsString();
            String target = data.get("target").getAsString();

            if (action.equalsIgnoreCase("add")) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(target);

                if (player != null) {
                    CorePlugin.getInstance().getServer().getWhitelistedPlayers().add(player);
                    CorePlugin.getInstance().getServer().reloadWhitelist();
                }
            } else if (action.equalsIgnoreCase("remove")) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(target);

                if (player != null) {
                    CorePlugin.getInstance().getServer().getWhitelistedPlayers().remove(player);
                    CorePlugin.getInstance().getServer().reloadWhitelist();
                }
            } else if (action.equalsIgnoreCase("off")) {
                if (target.equalsIgnoreCase(CorePlugin.getInstance().getServerId())) {
                    CorePlugin.getInstance().getServer().setWhitelist(false);
                }
            } else if (action.equalsIgnoreCase("on")) {
                if (target.equalsIgnoreCase(CorePlugin.getInstance().getServerId())) {
                    CorePlugin.getInstance().getServer().setWhitelist(true);
                }
            }

            CorePlugin.getInstance().getLogger().info("[Whitelist] " + target + " -> " + action + ".");
        } else if (type.equalsIgnoreCase("report")) {
            String name = data.get("name").getAsString();
            String server = object.get("server").getAsString();
            String target = data.get("target").getAsString();
            String message = data.get("message").getAsString();

            for (Player player : Bukkit.getOnlinePlayers()) {
                Profile profile = Profile.getByUuidIfAvailable(player.getUniqueId());

                if (profile != null && profile.getRank().isAboveOrEqual(Rank.TRIAL_MOD)) {
                    String toSend = REPORT_FORMAT
                            .replace("{server}", server)
                            .replace("{name}", name)
                            .replace("{target}", target)
                            .replace("{reason}", message);
                    player.sendMessage(toSend);
                }
            }
        } else if (type.equalsIgnoreCase("request")) {
            String name = data.get("name").getAsString();
            String server = object.get("server").getAsString();
            String message = data.get("message").getAsString();

            for (Player player : Bukkit.getOnlinePlayers()) {
                Profile profile = Profile.getByUuidIfAvailable(player.getUniqueId());

                if (profile != null && profile.getRank().isAboveOrEqual(Rank.TRIAL_MOD)) {
                    String toSend = REQUEST_FORMAT
                            .replace("{server}", server)
                            .replace("{name}", name)
                            .replace("{reason}", message);
                    player.sendMessage(toSend);
                }
            }
        } else if (type.equalsIgnoreCase("server-data")) {
            String serverName = data.get("server-name").getAsString();

            ServerData serverData = CorePlugin.getInstance().getRedisManager().getServers().get(serverName);

            if (serverData == null) {
                serverData = new ServerData();
            }

            // do this for non synced plugin files
            ServerType serverType;
            ServerState serverState;

            if (data.has("server-type")) {
                serverType = ServerType.valueOf(data.get("server-type").getAsString());
            } else {
                serverType = ServerType.OTHER;
            }

            if (data.has("server-state")) {
                serverState = ServerState.valueOf(data.get("server-state").getAsString());
            } else {
                serverState = ServerState.ACTIVE;
            }

            int playersOnline = data.get("player-count").getAsInt();
            int maxPlayers = data.get("player-max").getAsInt();
            boolean whitelisted = data.get("whitelisted").getAsBoolean();

            serverData.setServerName(serverName);
            serverData.setServerType(serverType);
            serverData.setServerState(serverState);
            serverData.setOnlinePlayers(playersOnline);
            serverData.setMaxPlayers(maxPlayers);
            serverData.setWhitelisted(whitelisted);
            serverData.setLastUpdate(System.currentTimeMillis());

            CorePlugin.getInstance().getRedisManager().getServers().put(serverName, serverData);
        }
    }

}