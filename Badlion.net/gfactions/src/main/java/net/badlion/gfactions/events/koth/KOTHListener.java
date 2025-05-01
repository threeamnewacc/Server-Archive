package net.badlion.gfactions.events.koth;

import com.massivecraft.factions.event.PowerLossEvent;
import net.badlion.gberry.Gberry;
import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.bukkitevents.DeathBanEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KOTHListener implements Listener {
	
	private GFactions plugin;
	
	public KOTHListener(GFactions plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (this.plugin.getKoth() != null) {
			KOTH koth = this.plugin.getKoth();
			Player player = event.getEntity();
			
			// On death lose 30% of points
			if (koth.getMapOfScores().containsKey(player.getName())) {
				koth.getMapOfScores().put(player.getName(), (int)(koth.getMapOfScores().get(player.getName()) * 0.7));
			}
		}
	}

    @EventHandler
    public void onPlayerDeathBanned(DeathBanEvent event) {
        if (this.plugin.getKoth() != null) {
            if (Gberry.isLocationInBetween(this.plugin.getKoth().getArenaLocation1(), this.plugin.getKoth().getArenaLocation2(), event.getPlayer().getLocation())) {
                event.setDeathBanTime(GFactions.plugin.getConfig().getLong("gfactions.koth.deathban_time"));
            }
        }
    }

    @EventHandler
    public void onPlayerLosePower(PowerLossEvent event) {
        if (this.plugin.getKoth() != null) {
            if (Gberry.isLocationInBetween(this.plugin.getKoth().getArenaLocation1(), this.plugin.getKoth().getArenaLocation2(), event.getPlayer().getLocation())) {
                event.setPower(event.getPower() * 0.50);
            }
        }
    }

}
