package net.badlion.shards.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MasterSyncEvent extends Event {

    private String from;
    private String data;

    private String response = "{}";

    public MasterSyncEvent(String from, String data){
        this.from = from;
        this.data = data;
    }

    public String getFrom() {
        return from;
    }

    public String getData() {
        return data;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    @Override
    public HandlerList getHandlers() {
        return null;
    }

}
