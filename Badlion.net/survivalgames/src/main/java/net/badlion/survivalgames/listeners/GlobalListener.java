package net.badlion.survivalgames.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.survivalgames.SurvivalGames;
import net.badlion.survivalgames.managers.SGSidebarManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.joda.time.DateTimeZone;

public class GlobalListener implements Listener {

	@EventHandler
	public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
		// Grab ratings for anyone who joined who isn't a player (we pre-load ratings of players that are playing in the game))
		SurvivalGames.getInstance().getSGGame().getDBUserRatings(event.getUniqueId());

		// Do this in here because we register the listener after game starts and doing this async in constructor is messy
		try {
			SGSidebarManager.getPlayerTimeZones().put(event.getUniqueId(), DateTimeZone.forID(Gberry.getTimeZone(event.getAddress())));
		} catch (IllegalArgumentException e) {
			SGSidebarManager.getPlayerTimeZones().put(event.getUniqueId(), DateTimeZone.forID("EST"));
		}
	}

    @EventHandler
    public void onPlayerJoinEvent(final PlayerJoinEvent event) {
	    event.getPlayer().sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));
	    event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Welcome to " + ChatColor.AQUA + "Badlion Survival Games 2.0" + ChatColor.DARK_GREEN + ".");
	    event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Please report any issues on the forums.");
	    event.getPlayer().sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));
    }

    @EventHandler
    public void onChunkLoadEvent(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (!(entity instanceof Player) && entity instanceof LivingEntity) {
                entity.remove();
            }
        }
    }

	@EventHandler
	public void onVehicleDestroyEvent(VehicleDestroyEvent event) {
		if (event.getVehicle() instanceof Boat) {
			// Cancel event to stop breaking and dropping of items
			event.setCancelled(true);

			// Remove the vehicle ourselves
			event.getVehicle().remove();
		}
	}

}
