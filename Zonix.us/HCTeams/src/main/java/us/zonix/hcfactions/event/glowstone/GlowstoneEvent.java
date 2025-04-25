package us.zonix.hcfactions.event.glowstone;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.event.EventManager;
import us.zonix.hcfactions.event.utils.Cuboid;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import us.zonix.hcfactions.event.Event;
import us.zonix.hcfactions.util.LocationSerialization;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.event.EventManager;
import us.zonix.hcfactions.event.utils.Cuboid;
import us.zonix.hcfactions.util.LocationSerialization;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class GlowstoneEvent implements Event {

    private static FactionsPlugin main = FactionsPlugin.getInstance();
    private static EventManager manager = EventManager.getInstance();

    @Getter private final UUID uuid;
    @Getter private final String name;
    @Getter private final Cuboid cuboid;
    @Getter @Setter private boolean active;

    public GlowstoneEvent(UUID uuid, String name, Cuboid cuboid) {
        this.uuid = uuid;
        this.name = name;
        this.cuboid = cuboid;
        this.active = false;

        manager.getEvents().add(this);
    }


    public GlowstoneEvent(String name, Cuboid cuboid) {
        this(UUID.randomUUID(), name, cuboid);
    }

    public void start() {

        if(cuboid == null) return;

        for (Block block : cuboid){
            block.setType(Material.GLOWSTONE);
        }

        Bukkit.broadcastMessage(main.getLanguageConfig().getString("GLOWSTONE.START").replace("%GLOWSTONE%", name));
    }

    @Override
    public List<String> getScoreboardText() {
        return new ArrayList<>();
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void remove() {
        main.getFactionsDatabase().getGlowstone().deleteOne(eq("uuid", this.uuid.toString()));
        manager.getEvents().remove(this);
    }

    public static void load() {
        MongoCollection collection = main.getFactionsDatabase().getGlowstone();
        MongoCursor cursor = collection.find().iterator();
        while (cursor.hasNext()) {
            Document document = (Document) cursor.next();
            UUID uuid = UUID.fromString(document.getString("uuid"));
            String name = document.getString("name");

            JsonArray zoneArray = new JsonParser().parse(document.getString("zone")).getAsJsonArray();
            Location firstLocation = LocationSerialization.deserializeLocation(zoneArray.get(0).getAsString());
            Location secondLocation = LocationSerialization.deserializeLocation(zoneArray.get(1).getAsString());

            Cuboid zone = new Cuboid(firstLocation, secondLocation);
            new GlowstoneEvent(uuid, name, zone);
        }
    }

    public void save() {
        MongoCollection collection = main.getFactionsDatabase().getGlowstone();

        Document document = new Document();
        document.put("uuid", uuid.toString());
        document.put("name", name);

        JsonArray zoneArray = new JsonArray();
        zoneArray.add(new JsonPrimitive(LocationSerialization.serializeLocation(cuboid.getLocationOne())));
        zoneArray.add(new JsonPrimitive(LocationSerialization.serializeLocation(cuboid.getLocationTwo())));

        document.put("zone", zoneArray.toString());

        collection.replaceOne(eq("uuid", uuid.toString()), document, new UpdateOptions().upsert(true));
    }

}
