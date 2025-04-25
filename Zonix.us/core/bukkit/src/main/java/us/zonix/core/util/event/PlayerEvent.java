package us.zonix.core.util.event;

import java.util.UUID;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PlayerEvent extends BaseEvent {
	private Player player;

	public PlayerEvent(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public UUID getUniqueId() {
		return player.getUniqueId();
	}
}
