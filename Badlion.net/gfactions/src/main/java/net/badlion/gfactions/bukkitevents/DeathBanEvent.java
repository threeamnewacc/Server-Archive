package net.badlion.gfactions.bukkitevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DeathBanEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private long deathBanTime;

    public DeathBanEvent(Player player, long deathBanTime) {
        super(true);
        this.player = player;
        this.deathBanTime = deathBanTime; // millis
    }

    public Player getPlayer() {
        return player;
    }

    public long getDeathBanTime() {
        return deathBanTime;
    }

    public void setDeathBanTime(long deathBanTime) {
        this.deathBanTime = deathBanTime;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
