package net.badlion.potpvp.tasks.lms;

import net.badlion.potpvp.events.LastManStanding;
import net.badlion.potpvp.states.matchmaking.events.LMSState;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class LMSInvisibilityTask extends BukkitRunnable {
	
	private LastManStanding match;
	
	public LMSInvisibilityTask(LastManStanding match) {
		this.match = match;
	}
	
	@Override
	public void run() {
		if (this.match != null) {
			for (Player player : this.match.getPlayers()) {
                if (!LMSState.isNaked(player)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 60 * 5, 1), true); // 5 min
                }
			}
		}
	}

}
