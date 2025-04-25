package us.zonix.hcfactions.event.koth;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.crate.Crate;
import us.zonix.hcfactions.event.Event;
import us.zonix.hcfactions.event.EventManager;
import us.zonix.hcfactions.event.EventZone;
import us.zonix.hcfactions.util.DateUtil;
import us.zonix.hcfactions.util.LocationSerialization;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class KothEvent implements Event {

    private static final DecimalFormat SECONDS_FORMATTER = new DecimalFormat("#0.0");
    private static FactionsPlugin main = FactionsPlugin.getInstance();
    private static EventManager manager = EventManager.getInstance();

    @Getter private final UUID uuid;
    @Getter private final String name;
    @Getter private final int height;
    @Getter private final EventZone zone;
    @Getter @Setter private long time;
    @Getter @Setter private long capTime;
    @Getter @Setter private long grace;
    @Getter @Setter private boolean active;
    @Getter @Setter private Player cappingPlayer;

    public KothEvent(UUID uuid, String name, int height, EventZone zone) {
        this.uuid = uuid;
        this.name = name;
        this.zone = zone;
        this.height = height;

        manager.getEvents().add(this);
    }


    public KothEvent(String name, int height, EventZone zone) {
        this(UUID.randomUUID(), name, height, zone);
    }

    public void start(long capTime) {
        this.capTime = capTime;
        this.active = true;

        Bukkit.broadcastMessage(main.getLanguageConfig().getString("KOTH.START").replace("%KOTH%", name).replace("%TIME%", getTimeLeft()) + ChatColor.GRAY + " (" + (FactionsPlugin.getInstance().isKitmapMode() ? getKitMapKothLocation(this) : getKothLocation(this)) + ")");
    }

    public void stop(boolean force) {

        if (force || cappingPlayer == null) {
            Bukkit.broadcastMessage(main.getLanguageConfig().getString("KOTH.STOP").replace("%KOTH%", name).replace("%TIME%", getTimeLeft()));
        } else {
            Bukkit.broadcastMessage(main.getLanguageConfig().getString("KOTH.STOP_WINNER").replace("%KOTH%", name).replace("%TIME%", getTimeLeft()).replace("%PLAYER%", cappingPlayer.getName()));
            Crate crate = Crate.getByName("KOTH");

            if(crate != null) {
                this.cappingPlayer.sendMessage(ChatColor.GREEN + "You have received (1) KOTH Crate Key.");
                this.cappingPlayer.getInventory().addItem(crate.getKey(1));
            }
        }

        this.active = false;
        this.capTime = 0;
        this.time = 0;
        this.cappingPlayer = null;
    }

    public boolean isGrace() {
        return time + grace > System.currentTimeMillis();
    }

    public boolean isFinished() {
        return active && time + capTime - (System.currentTimeMillis() + 1000) <= 0 && cappingPlayer != null;
    }

    public void setCappingPlayer(Player player) {
        if (player == null) {
            Bukkit.broadcastMessage(main.getLanguageConfig().getString("KOTH.KNOCKED").replace("%KOTH%", name).replace("%TIME%", getTimeLeft()).replace("%PLAYER%", cappingPlayer.getName()));

            grace = 5000;
            time = System.currentTimeMillis();
        } else {
            Bukkit.broadcastMessage(main.getLanguageConfig().getString("KOTH.CONTESTED").replace("%KOTH%", name).replace("%TIME%", getTimeLeft()).replace("%PLAYER%", player.getName()));
        }

        this.cappingPlayer = player;
        this.time = System.currentTimeMillis();
    }

    public long getDecisecondsLeft() {
        if (cappingPlayer == null) {
            return capTime / 100;
        } else {
            return (time + capTime - System.currentTimeMillis()) / 100;
        }
    }

    public String getTimeLeft() {

        long millis = 0;

        if(cappingPlayer == null) {
            millis = capTime;
        } else {
            millis = time + capTime - System.currentTimeMillis();
        }

        if (millis >= 3600000) {
            return DateUtil.formatTime(millis);
        } else if (millis >= 60000) {
            return DateUtil.formatTime(millis);
        } else {
            return SECONDS_FORMATTER.format(((millis) / 1000.0f)) + "s";
        }
    }

    @Override
    public List<String> getScoreboardText() {
        List<String> toReturn = new ArrayList<>();

        for (String line : main.getScoreboardConfig().getStringList("PLACE_HOLDER.KOTH")) {
            line = line.replace("%KOTH%", name);
            line = line.replace("%TIME%", getTimeLeft());

            toReturn.add(line);
        }

        return toReturn;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void remove() {
        main.getFactionsDatabase().getKoths().deleteOne(eq("uuid", this.uuid.toString()));
        manager.getEvents().remove(this);
    }

    public static void load() {
        MongoCollection collection = main.getFactionsDatabase().getKoths();
        MongoCursor cursor = collection.find().iterator();
        while (cursor.hasNext()) {
            Document document = (Document) cursor.next();
            UUID uuid = UUID.fromString(document.getString("uuid"));
            String name = document.getString("name");
            int height = document.getInteger("height");

            JsonArray zoneArray = new JsonParser().parse(document.getString("zone")).getAsJsonArray();
            Location firstLocation = LocationSerialization.deserializeLocation(zoneArray.get(0).getAsString());
            Location secondLocation = LocationSerialization.deserializeLocation(zoneArray.get(1).getAsString());

            EventZone zone = new EventZone(firstLocation, secondLocation); zone.setHeight(height);
            new KothEvent(uuid, name, height, zone);
        }
    }

    public void save() {
        MongoCollection collection = main.getFactionsDatabase().getKoths();

        Document document = new Document();
        document.put("uuid", uuid.toString());
        document.put("name", name);
        document.put("height", height);

        JsonArray zoneArray = new JsonArray();
        zoneArray.add(new JsonPrimitive(LocationSerialization.serializeLocation(zone.getFirstLocation())));
        zoneArray.add(new JsonPrimitive(LocationSerialization.serializeLocation(zone.getSecondLocation())));

        document.put("zone", zoneArray.toString());

        collection.replaceOne(eq("uuid", uuid.toString()), document, new UpdateOptions().upsert(true));
    }

    private String getKothLocation(KothEvent event) {
        return (event.getZone().getCenter().getX() < 0 ? "-500" : "500") + ", " + event.getZone().getCenter().getBlockY() + ", " + (event.getZone().getCenter().getZ() < 0 ? "-500" : "500");
    }

    private String getKitMapKothLocation(KothEvent event) {
        return (event.getZone().getCenter().getX() < 0 ? "-250" : "250") + ", " + event.getZone().getCenter().getBlockY() + ", " + (event.getZone().getCenter().getZ() < 0 ? "-250" : "250");
    }

}
