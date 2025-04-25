package us.zonix.practice.events.parkour;

import java.util.UUID;
import us.zonix.practice.CustomLocation;
import us.zonix.practice.events.EventPlayer;
import us.zonix.practice.events.PracticeEvent;

public class ParkourPlayer extends EventPlayer {
    private ParkourPlayer.ParkourState state = ParkourPlayer.ParkourState.WAITING;
    private CustomLocation lastCheckpoint;
    private int checkpointId;

    public ParkourPlayer(UUID uuid, PracticeEvent event) {
        super(uuid, event);
    }

    public void setState(ParkourPlayer.ParkourState state) {
        this.state = state;
    }

    public void setLastCheckpoint(CustomLocation lastCheckpoint) {
        this.lastCheckpoint = lastCheckpoint;
    }

    public void setCheckpointId(int checkpointId) {
        this.checkpointId = checkpointId;
    }

    public ParkourPlayer.ParkourState getState() {
        return this.state;
    }

    public CustomLocation getLastCheckpoint() {
        return this.lastCheckpoint;
    }

    public int getCheckpointId() {
        return this.checkpointId;
    }

    public static enum ParkourState {
        WAITING,
        INGAME;
    }
}
