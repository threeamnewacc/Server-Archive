package net.badlion.gberry.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AsyncDelayedPlayerJoinEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private UUID uuid;
    private String username;
    private Connection connection;
    private List<Runnable> runnables = new ArrayList<>();

    public AsyncDelayedPlayerJoinEvent(Connection connection, UUID uuid, String username) {
        super(true);

        this.connection = connection;
        this.uuid = uuid;
        this.username = username;
    }

    public Connection getConnection() {
        return connection;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public List<Runnable> getRunnables() {
        return this.runnables;
    }

}
