package us.zonix.core.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.event.BaseEvent;

@Getter
@RequiredArgsConstructor
public class RankChangeEvent extends BaseEvent {

	private final Player player;
	private final Rank rank;

}
