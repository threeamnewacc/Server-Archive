package us.zonix.hcfactions.event;

import org.bukkit.Location;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.event.glowstone.GlowstoneEvent;
import us.zonix.hcfactions.event.schedule.ScheduleHandler;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class EventManager {

    private static EventManager instance = new EventManager(FactionsPlugin.getInstance());
    @Getter private final FactionsPlugin main;
    @Getter private final List<Event> events;

    public EventManager(FactionsPlugin main) {
        this.main = main;
        this.events = new ArrayList<>();
        ScheduleHandler.setSchedules(this, this.main.getKothScheduleConfig());
        new EventTimer(this.main);
    }

    public Event getByName(String name) {
        for (Event event : events) {
            if (event.getName().equalsIgnoreCase(name)) {
                return event;
            }
        }
        return null;
    }

    public GlowstoneEvent getGlowstoneEvent(Location location) {
        for(Event event : this.events) {
            if(event != null && event instanceof GlowstoneEvent) {

                GlowstoneEvent glowstoneEvent = (GlowstoneEvent) event;

                if(glowstoneEvent.getCuboid() != null && glowstoneEvent.getCuboid().contains(location)) {
                    return glowstoneEvent;
                }
            }
        }

        return null;
    }

    public static EventManager getInstance() {
        return instance;
    }
}
