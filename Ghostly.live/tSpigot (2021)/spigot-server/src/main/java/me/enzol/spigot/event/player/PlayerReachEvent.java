package me.enzol.spigot.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import net.minecraft.server.Entity;

public class PlayerReachEvent extends PlayerEvent {
	
    private static HandlerList handlers = new HandlerList();
    
    private double distanceSqrt;
    private double distance;
    
    public PlayerReachEvent(Player player, double distanceSqrt) {
        super(player);
        
        this.distanceSqrt = distanceSqrt;
        this.distance = Entity.invSqrt(distanceSqrt);
    }
    
    /**
     * Get the reach distance-squared of the players.
     * 
     * @return the distance squared.
     * */
    public double getDistanceSqrt() {
        return this.distanceSqrt;
    }
    
    /**
     * Get the reach distance of the players.
     * 
     * @return the distance.
     * */
    public double getDistance() {
        return this.distance; 
    }
    
    @Override
    public HandlerList getHandlers() {
        return PlayerReachEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerReachEvent.handlers;
    }
}
