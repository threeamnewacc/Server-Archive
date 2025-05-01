package net.badlion.gfactions.tasks;

import net.badlion.gfactions.Battle;
import net.badlion.gfactions.managers.BattleManager;
import net.badlion.gfactions.FightParticipant;
import net.badlion.gfactions.GFactions;
import org.bukkit.scheduler.BukkitRunnable;

public class BattleEndCheckerTask extends BukkitRunnable {

	private GFactions plugin;

	public BattleEndCheckerTask(GFactions plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		for (final Battle battle : BattleManager.battles) {
	   		// Go through all the participants and check to see if any of them have been attacked in the past 60 seconds
			boolean flag = true;
			for (FightParticipant fightParticipant : battle.getParticipants().values()) {
				if (fightParticipant.getLastHitTime() != 0 && fightParticipant.getLastHitTime() + 60000 > System.currentTimeMillis()) {
					flag = false;
				}
			}

			// Fight is over...store data?
			if (flag) {
				this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
					@Override
					public void run() {

					}
				});
			}
		}
	}

}
