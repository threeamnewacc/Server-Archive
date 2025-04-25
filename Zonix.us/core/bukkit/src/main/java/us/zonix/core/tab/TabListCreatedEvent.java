package us.zonix.core.tab;

import org.bukkit.entity.Player;
import us.zonix.core.util.event.PlayerEvent;

public class TabListCreatedEvent extends PlayerEvent {

	public TabListCreatedEvent(Player player) {
		super(player);
	}
}
