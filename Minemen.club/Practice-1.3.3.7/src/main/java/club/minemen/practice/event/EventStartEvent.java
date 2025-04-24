package club.minemen.practice.event;

import club.minemen.core.event.BaseEvent;
import club.minemen.practice.events.PracticeEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventStartEvent extends BaseEvent {
	private final PracticeEvent event;
}
