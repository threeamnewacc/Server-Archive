package us.zonix.practice.event;

import us.zonix.practice.events.PracticeEvent;

public class EventStartEvent extends BaseEvent {
    private final PracticeEvent event;

    public PracticeEvent getEvent() {
        return this.event;
    }

    public EventStartEvent(PracticeEvent event) {
        this.event = event;
    }
}
