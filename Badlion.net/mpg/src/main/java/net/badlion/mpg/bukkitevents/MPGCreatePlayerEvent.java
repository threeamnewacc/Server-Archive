package net.badlion.mpg.bukkitevents;

import net.badlion.mpg.MPGPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MPGCreatePlayerEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private MPGPlayer mpgPlayer;

    public MPGCreatePlayerEvent(Player player) {
        this.player = player;
    }

	public Player getPlayer() {
        return this.player;
    }

    public MPGPlayer getMPGPlayer() {
        return this.mpgPlayer;
    }

    public void setMpgPlayer(MPGPlayer mpgPlayer) {
        this.mpgPlayer = mpgPlayer;
    }

    public HandlerList getHandlers() {
        return MPGCreatePlayerEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return MPGCreatePlayerEvent.handlers;
    }

}
