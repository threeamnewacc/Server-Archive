package net.badlion.mpg.bukkitevents;

import net.badlion.mpg.kits.MPGKit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KitLoadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private MPGKit kit;

    public KitLoadEvent(Player player, MPGKit kit) {
        this.player = player;
        this.kit = kit;
    }

    public Player getPlayer() {
        return player;
    }

    public MPGKit getKit() {
        return kit;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
