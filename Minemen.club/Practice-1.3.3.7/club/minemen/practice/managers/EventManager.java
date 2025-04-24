// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.managers;

import club.minemen.practice.events.EventState;
import java.util.function.Consumer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.WorldCreator;
import java.util.Arrays;
import club.minemen.practice.events.sumo.SumoEvent;
import java.util.HashMap;
import org.bukkit.World;
import club.minemen.practice.Practice;
import club.minemen.practice.events.PracticeEvent;
import java.util.Map;

public class EventManager
{
    private final Map<Class<? extends PracticeEvent>, PracticeEvent> events;
    private final Practice plugin;
    private final World eventWorld;
    
    public EventManager() {
        this.events = new HashMap<Class<? extends PracticeEvent>, PracticeEvent>();
        this.plugin = Practice.getInstance();
        Arrays.asList(SumoEvent.class).forEach(clazz -> this.addEvent(clazz));
        this.eventWorld = this.plugin.getServer().createWorld(new WorldCreator("event"));
        if (this.eventWorld != null) {
            this.plugin.getServer().getWorlds().add(this.eventWorld);
            this.eventWorld.setTime(2000L);
            this.eventWorld.setGameRuleValue("doDaylightCycle", "false");
            this.eventWorld.setGameRuleValue("doMobSpawning", "false");
            this.eventWorld.setStorm(false);
            this.eventWorld.getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
        }
    }
    
    public PracticeEvent getByName(final String name) {
        return this.events.values().stream().filter(event -> event.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
    
    public void hostEvent(final PracticeEvent event, final Player host) {
        event.setState(EventState.WAITING);
        event.setHost(host);
        event.startCountdown();
    }
    
    private void addEvent(final Class<? extends PracticeEvent> clazz) {
        PracticeEvent event = null;
        try {
            event = (PracticeEvent)clazz.newInstance();
        }
        catch (final InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        this.events.put(clazz, event);
    }
    
    public boolean isPlaying(final Player player, final PracticeEvent event) {
        return event.getPlayers().containsKey(player.getUniqueId());
    }
    
    public PracticeEvent getEventPlaying(final Player player) {
        return this.events.values().stream().filter(event -> this.isPlaying(player, event)).findFirst().orElse(null);
    }
    
    public Map<Class<? extends PracticeEvent>, PracticeEvent> getEvents() {
        return this.events;
    }
    
    public Practice getPlugin() {
        return this.plugin;
    }
    
    public World getEventWorld() {
        return this.eventWorld;
    }
}
