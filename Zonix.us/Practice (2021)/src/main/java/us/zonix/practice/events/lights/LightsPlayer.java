package us.zonix.practice.events.lights;

import java.util.UUID;
import us.zonix.practice.events.EventPlayer;
import us.zonix.practice.events.PracticeEvent;

public class LightsPlayer extends EventPlayer {
    private LightsPlayer.LightsState state = LightsPlayer.LightsState.WAITING;

    public LightsPlayer(UUID uuid, PracticeEvent event) {
        super(uuid, event);
    }

    public void setState(LightsPlayer.LightsState state) {
        this.state = state;
    }

    public LightsPlayer.LightsState getState() {
        return this.state;
    }

    public static enum LightsState {
        LOBBY,
        WAITING,
        INGAME;
    }
}
