package club.minemen.practice.queue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class QueueEntry {

	private final QueueType queueType;
	private final String kitName;

	private final int elo;

	private final boolean party;

}
