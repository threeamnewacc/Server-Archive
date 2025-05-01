package net.badlion.gberry.listeners;

import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.HorseJumpEvent;

import com.tinywebteam.badlion.MineKart;

public class HorseJumpListener implements Listener {
	
	private MineKart plugin;
	
	public HorseJumpListener(MineKart plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onExitHorse(HorseJumpEvent event) {
		Horse horse = event.getEntity();
		if (horse.getPassenger() instanceof Player) {
			Player player = (Player) horse.getPassenger();
			if (this.plugin.getPlayerToRacer().containsKey(player)) {
				// F*CK UR SH*T NIGGA, DID I GIVE YOU PERMISSION TO GET OUT OF THE F*CKING HORSE!?
				event.setCancelled(true);
			}
		}
	}
}
