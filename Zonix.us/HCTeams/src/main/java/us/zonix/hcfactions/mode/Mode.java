package us.zonix.hcfactions.mode;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import us.zonix.hcfactions.FactionsPlugin;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bson.Document;
import us.zonix.hcfactions.profile.cooldown.ProfileCooldownType;
import us.zonix.hcfactions.util.DateUtil;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.util.DateUtil;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;

public class Mode {

    private static final DecimalFormat SECONDS_FORMATTER = new DecimalFormat("#0.0");
    private static FactionsPlugin main = FactionsPlugin.getInstance();
    private static Set<Mode> modes = new HashSet<>();

    @Getter private final String name;
    @Getter @Setter private boolean active;
    @Getter @Setter private long startingTime;
    @Getter @Setter private ModeType modeType;

    public Mode(String name, boolean active, long startingTime, ModeType modeType) {
        this.name = name;
        this.active = active;
        this.startingTime = startingTime;
        this.modeType = modeType;

        modes.add(this);
    }

    public void save() {
        MongoCollection collection = main.getFactionsDatabase().getModes();

        Document document = new Document();
        document.put("name", name);
        document.put("active", active);
        document.put("startingTime", startingTime);
        document.put("modeType", modeType.name());

        collection.replaceOne(eq("name", name), document, new UpdateOptions().upsert(true));
    }

    public static void load() {
        MongoCollection collection = main.getFactionsDatabase().getModes();
        MongoCursor cursor = collection.find().iterator();

        while (cursor.hasNext()) {
            Document document = (Document) cursor.next();
            String name = document.getString("name");
            boolean active = document.getBoolean("active");
            long startingTime = document.getLong("startingTime");
            ModeType modeType = ModeType.valueOf(document.getString("modeType"));

            new Mode(name, active, startingTime, modeType);
        }
    }

    public String getTimeLeft() {
        long time = this.startingTime - System.currentTimeMillis() + FactionsPlugin.getInstance().getMainConfig().getInt("SOTW.DURATION");

        if (time >= 3600000) {
            return DateUtil.formatTime(time);
        } else if (time >= 60000) {
            return DateUtil.formatTime(time);
        } else {
            return SECONDS_FORMATTER.format(((time) / 1000.0f)) + "s";
        }

    }

    public List<String> getScoreboardText() {
        List<String> toReturn = new ArrayList<>();

        for (String line : main.getScoreboardConfig().getStringList("PLACE_HOLDER.SOTW")) {
            line = line.replace("%SOTW%", name.toUpperCase());
            line = line.replace("%TIME%", getTimeLeft());
            toReturn.add(line);
        }

        return toReturn;
    }

    public boolean isSOTWActive() {
        return this.modeType == ModeType.SOTW && this.isActive() && this.startingTime != 0 && !this.elapsed(this.getStartingTime(), FactionsPlugin.getInstance().getMainConfig().getInt("SOTW.DURATION"));
    }

    public static Mode getByName(String name) {
        for (Mode mode : modes) {
            if (mode.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase())) {
                return mode;
            }
        }

        return null;
    }

    private boolean elapsed(long from, long required) {
        return System.currentTimeMillis() - from > required;
    }

    public static Set<Mode> getModes() {
        return modes;
    }

}
