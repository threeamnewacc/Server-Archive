package me.devkevin.practice.events;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventPlayer {
	private final UUID uuid;
	private final PracticeEvent event;
}
