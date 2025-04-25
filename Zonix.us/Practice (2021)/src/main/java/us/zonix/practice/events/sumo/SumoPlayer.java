package us.zonix.practice.events.sumo;

import java.util.UUID;
import org.bukkit.scheduler.BukkitTask;
import us.zonix.practice.events.EventPlayer;
import us.zonix.practice.events.PracticeEvent;

public class SumoPlayer extends EventPlayer {
    private SumoPlayer.SumoState state = SumoPlayer.SumoState.WAITING;
    private BukkitTask fightTask;
    private SumoPlayer fighting;

    public SumoPlayer(UUID uuid, PracticeEvent event) {
        super(uuid, event);
    }

    public void setState(SumoPlayer.SumoState state) {
        this.state = state;
    }

    public void setFightTask(BukkitTask fightTask) {
        this.fightTask = fightTask;
    }

    public void setFighting(SumoPlayer fighting) {
        this.fighting = fighting;
    }

    public SumoPlayer.SumoState getState() {
        return this.state;
    }

    public BukkitTask getFightTask() {
        return this.fightTask;
    }

    public SumoPlayer getFighting() {
        return this.fighting;
    }

    public static enum SumoState {
        WAITING,
        PREPARING,
        FIGHTING,
        ELIMINATED;
    }
}
