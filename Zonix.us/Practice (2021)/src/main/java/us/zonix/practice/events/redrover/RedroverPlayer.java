package us.zonix.practice.events.redrover;

import java.util.UUID;
import org.bukkit.scheduler.BukkitTask;
import us.zonix.practice.events.EventPlayer;
import us.zonix.practice.events.PracticeEvent;

public class RedroverPlayer extends EventPlayer {
    private RedroverPlayer.RedroverState state = RedroverPlayer.RedroverState.WAITING;
    private RedroverPlayer fightPlayer;
    private BukkitTask fightTask;

    public RedroverPlayer(UUID uuid, PracticeEvent event) {
        super(uuid, event);
    }

    public void setState(RedroverPlayer.RedroverState state) {
        this.state = state;
    }

    public void setFightPlayer(RedroverPlayer fightPlayer) {
        this.fightPlayer = fightPlayer;
    }

    public void setFightTask(BukkitTask fightTask) {
        this.fightTask = fightTask;
    }

    public RedroverPlayer.RedroverState getState() {
        return this.state;
    }

    public RedroverPlayer getFightPlayer() {
        return this.fightPlayer;
    }

    public BukkitTask getFightTask() {
        return this.fightTask;
    }

    public static enum RedroverState {
        WAITING,
        PREPARING,
        FIGHTING;
    }
}
