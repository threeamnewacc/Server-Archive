package cc.patrone.practice.match;

import cc.patrone.practice.arena.Arena;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MatchRequest {
	private final UUID requester;
	private final UUID requested;
	private final String kitName;
	private final Arena arena;
	private final boolean party;
}
