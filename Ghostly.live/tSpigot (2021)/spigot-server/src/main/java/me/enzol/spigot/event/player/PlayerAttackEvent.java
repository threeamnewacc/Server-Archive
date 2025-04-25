package me.enzol.spigot.event.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerAttackEvent extends PlayerEvent {
	
    private static HandlerList handlers = new HandlerList();
    
    private Entity entity;
    
    public PlayerAttackEvent(Entity entity, Player player) {
        super(player);
        
        this.entity = entity;
    }
    
    /**
     * Returns the Entity involved in this event
     * 
     * @return Entity who is involved in this event
     * */
    public Entity getEntity() {
		return entity;
	}
    
    @Override
    public HandlerList getHandlers() {
        return PlayerAttackEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerAttackEvent.handlers;
    }
}
