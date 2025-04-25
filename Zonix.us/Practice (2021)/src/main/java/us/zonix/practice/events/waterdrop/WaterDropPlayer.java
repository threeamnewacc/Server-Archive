package us.zonix.practice.events.waterdrop;

import java.util.UUID;
import us.zonix.practice.events.EventPlayer;
import us.zonix.practice.events.PracticeEvent;

public class WaterDropPlayer extends EventPlayer {
    private WaterDropPlayer.WaterDropState state = WaterDropPlayer.WaterDropState.LOBBY;

    public WaterDropPlayer(UUID uuid, PracticeEvent event) {
        super(uuid, event);
    }

    public void setState(WaterDropPlayer.WaterDropState state) {
        this.state = state;
    }

    public WaterDropPlayer.WaterDropState getState() {
        return this.state;
    }

    public static enum WaterDropState {
        LOBBY,
        JUMPING,
        NEXT_ROUND,
        ELIMINATED;
    }
}
