package us.zonix.core.redis;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import redis.clients.jedis.Jedis;
import us.zonix.core.CorePlugin;
import us.zonix.core.punishment.Punishment;
import us.zonix.core.rank.Rank;
import us.zonix.core.redis.subscription.GlobalSubscriptionHandler;
import us.zonix.core.redis.subscription.WeebCatcherSubscriptionHandler;
import us.zonix.core.server.ServerData;
import us.zonix.core.shared.redis.JedisPublisher;
import us.zonix.core.shared.redis.JedisSubscriber;
import us.zonix.core.util.Clickable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoreRedisManager {

    private static final Pattern CLICKABLE_PATTERN = Pattern.compile("(.*)(\\{clickable::command=\"(.*)\"})(.*)(\\{/clickable})(.*)");

    private final CorePlugin plugin;

    @Getter
    private final Map<String, ServerData> servers;
    @Getter
    private final HashSet<UUID> staffChat;
    @Getter
    @Setter
    private boolean chatSilenced;
    @Getter
    @Setter
    private long chatSlowDownTime;

    private final JedisSubscriber<JsonObject> messagesSubscriber;
    private final JedisPublisher<JsonObject> messagesPublisher;

    public CoreRedisManager(CorePlugin plugin) {
        this.plugin = plugin;
        this.servers = new HashMap<>();
        this.staffChat = new HashSet<>();
        this.chatSilenced = false;
        this.chatSlowDownTime = 0L;

        new JedisSubscriber<>(this.plugin.getJedisSettings(), "client-bans", JsonObject.class, new WeebCatcherSubscriptionHandler());
        this.messagesSubscriber = new JedisSubscriber<>(this.plugin.getJedisSettings(), "global-messages", JsonObject.class, new GlobalSubscriptionHandler());
        this.messagesPublisher = new JedisPublisher<>(this.plugin.getJedisSettings(), "global-messages");
    }

    private JsonObject generateMessage(String type, JsonObject data) {
        JsonObject object = new JsonObject();
        object.addProperty("server", this.plugin.getServerId());
        object.addProperty("type", type);
        object.add("data", data);
        return object;
    }

    private void write(JsonObject publish) {
        this.messagesPublisher.write(publish);
    }

    public void writePunishment(Punishment punishment, String name, String sender, boolean silent) {
        JsonObject object = punishment.toJson();
        object.addProperty("name", name);
        object.addProperty("sender", sender);
        object.addProperty("silent", silent);

        this.write(this.generateMessage("punishment", object));
    }

    public void writeServer(JsonObject object) {
        this.write(this.generateMessage("server-data", object));
    }

    public void writeRank(UUID uuid, Rank rank) {
        JsonObject object = new JsonObject();
        object.addProperty("uuid", uuid.toString());
        object.addProperty("rank", rank.name());

        this.write(this.generateMessage("rank", object));
    }

    public void writeAnnounce(String name, String game, String server) {
        JsonObject object = new JsonObject();
        object.addProperty("name", name);
        object.addProperty("game", game);
        object.addProperty("server", server);

        this.write(this.generateMessage("announce", object));
    }

    public void writeStaffChat(String name, Rank rank, String message) {
        JsonObject object = new JsonObject();
        object.addProperty("name", name);
        object.addProperty("rank", rank.name());
        object.addProperty("message", message);

        this.write(this.generateMessage("staffchat", object));
    }

    public void writeStaffJoin(String name, Rank rank, String server) {
        JsonObject object = new JsonObject();
        object.addProperty("name", name);
        object.addProperty("rank", rank.name());
        object.addProperty("server", server);

        this.write(this.generateMessage("staffjoin", object));
    }

    public void writeRequest(String server, String name, String message) {
        JsonObject object = new JsonObject();
        object.addProperty("name", name);
        object.addProperty("server", server);
        object.addProperty("message", message);

        this.write(this.generateMessage("request", object));
    }

    public void writeReport(String server, String name, String target, String message) {
        JsonObject object = new JsonObject();
        object.addProperty("name", name);
        object.addProperty("server", server);
        object.addProperty("target", target);
        object.addProperty("message", message);

        this.write(this.generateMessage("report", object));
    }

    public void writeWhitelist(String action, String target) {
        JsonObject object = new JsonObject();
        object.addProperty("action", action);
        object.addProperty("target", target);

        this.write(this.generateMessage("whitelist", object));
    }

    public ServerData getServerDataByName(String name) {
        if (this.getServers().size() == 0) {
            return null;
        }

        for (String serverKey : this.getServers().keySet()) {
            if (serverKey.equalsIgnoreCase(name)) {
                return this.getServers().get(serverKey);
            } else {
                if (this.getServers().get(serverKey).getServerName().equalsIgnoreCase(name)) {
                    return this.getServers().get(serverKey);
                }
            }
        }

        return null;
    }

    public int getTotalPlayersOnline() {

        int count = 0;

        for (String serverKey : this.getServers().keySet()) {
            if (this.getServers().containsKey(serverKey)) {
                count += this.getServers().get(serverKey).getOnlinePlayers();
            }
        }

        count += getAsiaPlayerCount();

        return count;
    }

    public int getAsiaPlayerCount() {

        int count = 0;

        Jedis jedis = null;
        try {
            jedis = new Jedis(this.messagesSubscriber.getJedisSettings().getAddress(), this.messagesSubscriber.getJedisSettings().getPort());

            if (this.messagesSubscriber.getJedisSettings().hasPassword()) {
                jedis.auth(this.messagesSubscriber.getJedisSettings().getPassword());

            }

            Set<String> users = jedis.smembers("proxy:" + "zonix-as2" + ":usersOnline");
            count += users.size();
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return count;
    }

    private static BaseComponent[] parseMessage(String message) {
        Clickable clickable = new Clickable();

        Matcher clickableMatcher = CLICKABLE_PATTERN.matcher(message);

        if (clickableMatcher.matches()) {
            clickable.add(clickableMatcher.group(1));
            clickable.add(clickableMatcher.group(4), null, clickableMatcher.group(3));
            clickable.add(clickableMatcher.group(6));
        }
        else {
            clickable.add(message);
        }

        return clickable.asComponents();
    }

}