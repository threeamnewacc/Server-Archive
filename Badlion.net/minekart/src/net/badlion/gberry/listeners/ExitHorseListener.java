package net.badlion.gberry.listeners;

import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;

import com.tinywebteam.badlion.MineKart;

public class ExitHorseListener implements Listener {
	
	private MineKart plugin;
	
	public ExitHorseListener(MineKart plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onExitHorse(VehicleExitEvent event) {
		if (event.getVehicle() instanceof Horse) {
			Horse horse = (Horse) event.getVehicle();
			if (this.plugin.getAllowToEject().contains(horse)) {
				// Allow it because they fell off map
				this.plugin.getAllowToEject().remove(horse);
				return;
			}
			if (horse.getPassenger() instanceof Player) {
				Player player = (Player) horse.getPassenger();
				if (this.plugin.getPlayerToRacer().containsKey(player)) {
					// F*CK UR SH*T NIGGA, DID I GIVE YOU PERMISSION TO GET OUT OF THE F*CKING HORSE!?
					event.setCancelled(true);
				}
			}
		}
	}
}
