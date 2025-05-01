package net.badlion.banmanager.events;

import net.badlion.banmanager.BanManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PunishedPlayerEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private UUID uuid;
	private BanManager.PUNISHMENT_TYPE type;

	public PunishedPlayerEvent(UUID uuid, BanManager.PUNISHMENT_TYPE type) {
		this.uuid = uuid;
		this.type = type;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public UUID getUuid() {
		return uuid;
	}

	public BanManager.PUNISHMENT_TYPE getPunishmentType() {
		return type;
	}

	public HandlerList getHandlers() {
		return handlers;
	}


}
