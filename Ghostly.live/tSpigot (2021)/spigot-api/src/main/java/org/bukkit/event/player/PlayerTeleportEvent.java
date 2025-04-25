package org.bukkit.event.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Holds information for player teleport events
 */
public class PlayerTeleportEvent extends PlayerMoveEvent {
	
    private static HandlerList handlers = new HandlerList();
    private TeleportCause cause = TeleportCause.UNKNOWN;
    private boolean isTaliban;

    public PlayerTeleportEvent(Player player, Location from, Location to) {
        super(player, from, to);
    }

    public PlayerTeleportEvent(Player player, Location from, Location to, TeleportCause cause) {
        this(player, from, to);
        this.cause = cause;
    }
    
    public PlayerTeleportEvent(Player player, Location from, Location to, boolean isTaliban) {
        super(player, from, to);
		this.cause = TeleportCause.UNKNOWN;
        this.cause = TeleportCause.ENDER_PEARL;
        this.isTaliban = isTaliban;
    }

    /**
     * Gets the cause of this teleportation event
     *
     * @return Cause of the event
     */
    public TeleportCause getCause() {
        return cause;
    }
    
    public boolean isTaliban() {
		return isTaliban;
	}

    public enum TeleportCause {
        /**
         * Indicates the teleporation was caused by a player throwing an Ender
         * Pearl
         */
        ENDER_PEARL,
        /**
         * Indicates the teleportation was caused by a player executing a
         * command
         */
        COMMAND,
        /**
         * Indicates the teleportation was caused by a plugin
         */
        PLUGIN,
        /**
         * Indicates the teleportation was caused by a player entering a
         * Nether portal
         */
        NETHER_PORTAL,
        /**
         * Indicates the teleportation was caused by a player entering an End
         * portal
         */
        END_PORTAL,
        /**
         * Indicates the teleportation was caused by a player teleporting to a
         * Entity/Player via the specatator menu
         */
        SPECTATE,
        /**
         * Indicates the teleportation was caused by an event not covered by
         * this enum
         */
        UNKNOWN;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
