package us.zonix.practice.events.oitc;

import java.util.UUID;
import org.bukkit.scheduler.BukkitTask;
import us.zonix.practice.events.EventPlayer;
import us.zonix.practice.events.PracticeEvent;

public class OITCPlayer extends EventPlayer {
    private OITCPlayer.OITCState state = OITCPlayer.OITCState.WAITING;
    private int score = 0;
    private int lives = 5;
    private BukkitTask respawnTask;
    private OITCPlayer lastKiller;

    public OITCPlayer(UUID uuid, PracticeEvent event) {
        super(uuid, event);
    }

    public void setState(OITCPlayer.OITCState state) {
        this.state = state;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void setRespawnTask(BukkitTask respawnTask) {
        this.respawnTask = respawnTask;
    }

    public void setLastKiller(OITCPlayer lastKiller) {
        this.lastKiller = lastKiller;
    }

    public OITCPlayer.OITCState getState() {
        return this.state;
    }

    public int getScore() {
        return this.score;
    }

    public int getLives() {
        return this.lives;
    }

    public BukkitTask getRespawnTask() {
        return this.respawnTask;
    }

    public OITCPlayer getLastKiller() {
        return this.lastKiller;
    }

    public static enum OITCState {
        WAITING,
        PREPARING,
        FIGHTING,
        RESPAWNING,
        ELIMINATED;
    }
}
