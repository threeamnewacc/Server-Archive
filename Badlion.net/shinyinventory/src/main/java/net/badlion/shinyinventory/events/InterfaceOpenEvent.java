package net.badlion.shinyinventory.events;

import net.badlion.shinyinventory.gui.Interface;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class InterfaceOpenEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Interface gui;
    private final Player player;

    public InterfaceOpenEvent(Interface gui, Player player) {
        this.gui = gui;
        this.player = player;
    }

    public Interface getInterface() {
        return this.gui;
    }

    public Player getPlayer() {
        return this.player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
