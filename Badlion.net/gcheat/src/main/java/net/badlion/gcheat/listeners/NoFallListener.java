package net.badlion.gcheat.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class NoFallListener implements Listener {

 	@EventHandler
	public void playerTakeFallDamageEvent(EntityDamageEvent event) {
	    if (event.getEntity() instanceof Player) {
		    if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
			    System.out.println(event.getDamage());
		    }
	    }
    }

}
