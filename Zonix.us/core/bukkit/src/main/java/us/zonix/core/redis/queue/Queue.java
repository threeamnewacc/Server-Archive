package us.zonix.core.redis.queue;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.core.CorePlugin;
import us.zonix.core.profile.Profile;
import us.zonix.core.punishment.Punishment;
import us.zonix.core.punishment.PunishmentType;
import us.zonix.core.rank.Rank;
import us.zonix.core.shared.redis.JedisPublisher;
import us.zonix.core.shared.redis.JedisSubscriber;
import us.zonix.core.shared.redis.subscription.JedisSubscriptionHandler;
import us.zonix.core.util.BungeeUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Queue {

    private final Gson gson;

    private final CorePlugin plugin;

    @Getter
    private volatile Map<UUID, Integer> players;
    String serverName;

    private final JedisSubscriber<String> queueSubscriber;
    private final JedisPublisher<String> queuePublisher;

    public Queue(CorePlugin plugin, String serverName) {
        this.plugin = plugin;
        this.serverName = serverName;
        this.gson = new Gson();
        this.players = new HashMap<>();

        this.queueSubscriber = new JedisSubscriber<>(this.plugin.getJedisSettings(), "queue-" + this.serverName.toLowerCase().replace("-", "_"), String.class, new QueueSubscriptionHandler());
        this.queuePublisher = new JedisPublisher<>(this.plugin.getJedisSettings(), "queue-" + this.serverName.toLowerCase().replace("-", "_"));

        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {

            for (Map.Entry<UUID, Integer> next : this.players.entrySet()) {
                Player player = this.plugin.getServer().getPlayer(next.getKey());

                if (player != null && next.getValue() != -1) {
                    player.sendMessage(ChatColor.YELLOW + "You are #" + next.getValue() + " in the " + ChatColor.GOLD + serverName + ChatColor.YELLOW + " queue.");
                    player.sendMessage(ChatColor.AQUA.toString() + "Skip the queue by purchasing a rank at store.zonix.us");
                }

            }

        }, 200L, 200L);
    }

    public void runServerStatus() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {

            final ServerInfo info = new ServerInfo(true, plugin.getServer().hasWhitelist(), plugin.getServer().getOnlinePlayers().size(), plugin.getServer().getMaxPlayers());
            queuePublisher.writeDirectly("serverInfo`" + gson.toJson(info));

        }, 20L, 20L);
    }

    public boolean contains(Player player) {
        return this.players.containsKey(player.getUniqueId());
    }

    public void clear() {
        this.players.clear();
    }

    private int position(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile == null) {
            return 0;
        }

        if (profile.getRank().isAboveOrEqual(Rank.DEVELOPER)) {
            return 30;
        } else if (profile.getRank().isAboveOrEqual(Rank.MEDIA_OWNER)) {
            return 25;
        } else if (profile.getRank().isAboveOrEqual(Rank.TRIAL_MOD)) {
            return 20;
        } else if (profile.getRank().isAboveOrEqual(Rank.MEDIA)) {
            return 15;
        } else if (profile.getRank().isAboveOrEqual(Rank.BUILDER)) {
            return 10;
        } else if (profile.getRank().isAboveOrEqual(Rank.ZONIX)) {
            return 9;
        } else if (profile.getRank().isAboveOrEqual(Rank.EMERALD)) {
            return 8;
        } else if (profile.getRank().isAboveOrEqual(Rank.PLATINUM)) {
            return 6;
        } else if (profile.getRank().isAboveOrEqual(Rank.GOLD)) {
            return 7;
        } else if (profile.getRank().isAboveOrEqual(Rank.SILVER)) {
            return 5;
        } else if (profile.getRank().isAboveOrEqual(Rank.DEFAULT)) {
            return 4;
        }

        return 0;
    }

    public String getPosition(Player player) {
        final Integer position = this.players.get(player.getUniqueId());
        return (position == null || position == -1) ? "?" : String.valueOf(position);
    }

    public void addToQueue(Player player, boolean bypass) {

        Profile profile = Profile.getByUuidIfAvailable(player.getUniqueId());

        if (profile == null) {
            return;
        }

        Punishment ban = profile.getBannedPunishment();

        if (ban != null) {
            player.sendMessage(ban.getType().getMessage());
            return;
        }

        if (profile.isBlacklisted()) {
            player.sendMessage(PunishmentType.BLACKLIST.getMessage());
            return;
        }

        if (profile.getAlts().size() == 0) {
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, profile::loadProfileAlts);
        }

        for (UUID uuid : profile.getAlts()) {
            if (!uuid.equals(player.getUniqueId())) {

                Profile altProfile = Profile.getByUuid(uuid);
                Punishment bannedAlt = altProfile.getBannedPunishment();

                if (bannedAlt != null) {
                    player.sendMessage(bannedAlt.getType().getMessage());
                    return;
                }

                if (altProfile.isBlacklisted()) {
                    player.sendMessage(PunishmentType.BLACKLIST.getMessage());
                    return;
                }

            }
        }

        PlayerData data = new PlayerData(player.getUniqueId(), this.position(player), profile.getRank().getName(), this.serverName.replace("-", "_"));
        this.queuePublisher.writeDirectly("add`" + this.gson.toJson(data) + "`" + this.position(player) + "`" + bypass);
    }

    public void removeFromQueue(Player player) {

        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile == null) {
            return;
        }

        this.queuePublisher.writeDirectly("remove`" + player.getUniqueId());
    }

    public String getServerName() {

        if (serverName.toLowerCase().equalsIgnoreCase("practice-us")) {
            return "Practice US";
        } else if (serverName.toLowerCase().equalsIgnoreCase("practice-eu")) {
            return "Practice EU";
        } else if (serverName.toLowerCase().equalsIgnoreCase("practice-sa")) {
            return "Practice SA";
        } else if (serverName.toLowerCase().equalsIgnoreCase("practice-au")) {
            return "Practice AU";
        } else if (serverName.toLowerCase().equalsIgnoreCase("practice-as")) {
            return "Practice AS";
        } else if (serverName.toLowerCase().equalsIgnoreCase("kitmap-us")) {
            return "KitMap US";
        } else if (serverName.toLowerCase().equalsIgnoreCase("hcf-us")) {
            return "HCF US";
        }

        return serverName;
    }

    private class ServerInfo {
        private final boolean online;
        private final boolean whitelisted;
        private final int onlinePlayers;
        private final int maxPlayers;

        public ServerInfo(final boolean online, final boolean whitelisted, final int onlinePlayers, final int maxPlayers) {
            this.online = online;
            this.whitelisted = whitelisted;
            this.onlinePlayers = onlinePlayers;
            this.maxPlayers = maxPlayers;
        }
    }

    private class PlayerData {
        private final UUID id;
        private final int priority;
        private final String group;
        private final String server;

        public PlayerData(final UUID id, final int priority, final String group, final String server) {
            this.id = id;
            this.priority = priority;
            this.group = group;
            this.server = server;
        }
    }

    private class QueueSubscriptionHandler implements JedisSubscriptionHandler<String> {

        @Override
        public void handleMessage(String message) {

            final JsonParser parser = new JsonParser();
            final String[] messageSplit = message.split("`");
            final String command = messageSplit[0];

            if (command.equals("info")) {

                final JsonObject info = parser.parse(messageSplit[1]).getAsJsonObject();

                final Map<UUID, Integer> newPositions = new HashMap<UUID, Integer>();

                info.getAsJsonArray("players").forEach(element -> {
                    JsonObject object = element.getAsJsonObject();
                    newPositions.put(UUID.fromString(object.get("id").getAsString()), object.get("position").getAsInt());
                });

                players.clear();
                players.putAll(newPositions);
            } else if (command.equals("send")) {
                Player player = Bukkit.getPlayer(UUID.fromString(messageSplit[1]));

                if (player != null) {
                    BungeeUtil.sendToServer(player, serverName);
                }

            }

        }
    }


}