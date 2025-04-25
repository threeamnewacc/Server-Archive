package us.zonix.practice.events.woolmixup;

import java.util.UUID;
import us.zonix.practice.events.EventPlayer;
import us.zonix.practice.events.PracticeEvent;

public class WoolMixUpPlayer extends EventPlayer {
    private WoolMixUpPlayer.WoolMixUpState state = WoolMixUpPlayer.WoolMixUpState.WAITING;

    public WoolMixUpPlayer(UUID uuid, PracticeEvent event) {
        super(uuid, event);
    }

    public void setState(WoolMixUpPlayer.WoolMixUpState state) {
        this.state = state;
    }

    public WoolMixUpPlayer.WoolMixUpState getState() {
        return this.state;
    }

    public static enum WoolMixUpState {
        LOBBY,
        WAITING,
        INGAME;
    }
}
