package com.massivecraft.factions.type;

import club.minemen.core.util.finalutil.CC;
import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.event.ProtectionTimeCountdownEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class ProtectionTimer extends BukkitRunnable {

	private final HCFactions plugin;
	private final FactionPlayer fplayer;

	@Override
	public void run() {
		if (HCFactions.getInstance().endOfTheWorld) {
			//fplayer.msg(ChatColor.GOLD + "You lost your PvP Protection due to End of the World");
			fplayer.getPlayer().sendFormattedMessage("{0}You have lost your pvp protection due to {1}End of the World{0}.", CC.RED, CC.BD_RED);
			fplayer.removePvpProtection();
			this.cancel();
			return;
		}
		if (fplayer.getPlayer() == null) {
			this.cancel();
			return;
		}
		if (!fplayer.getPlayer().isDead() && fplayer.hasPvpProtection()) {
			long time = fplayer.getPvpProtectionTime();
			time -= 1000;
			ProtectionTimeCountdownEvent event = new ProtectionTimeCountdownEvent(fplayer.getPlayer(), time);
			plugin.getServer().getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				return;
			}
			time = event.getSecondsRemaining();
			fplayer.setPvpProtection(time);
			// Broadcast and save every min.
			if (time % 60_000 == 0) {
				if (time > 0) {
					this.fplayer.getPlayer().sendFormattedMessage("{0}You have {1}{2}{0} of pvp protection left.",
							CC.PRIMARY, CC.SECONDARY, this.plugin.pvpProtectionManager.getProtectionTimeString(this.fplayer.getPlayer()));
				}
			}
		} else {
			this.cancel();
		}
	}
}
