package cc.patrone.practice.tasks;

import zone.potion.utils.message.CC;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.managers.QueueManager;
import cc.patrone.practice.player.PracticeProfile;
import cc.patrone.practice.queue.QueueEntry;
import cc.patrone.practice.utils.StringUtil;
import cc.patrone.practice.wrapper.EloWrapper;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class QueueSearchTask extends BukkitRunnable {
	private final PracticePlugin plugin;
	private final QueueEntry entry;
	private int range = 50;
	private int ticksElapsed;

	@Override
	public void run() {
		QueueManager queueManager = plugin.getQueueManager();

		if (queueManager.queueSearch(entry, range)) {
			cancel();
		} else if (ticksElapsed % 60 == 0) {
			int elo = entry.getElo();
			PracticeProfile profile = entry.getProfile();
			Player player = profile.asPlayer();
			String msg;

			if (range > 1500) {
				msg = CC.RED + "Could not find a suitable match.";
				queueManager.dequeueTeam(player, profile);
				cancel();
			} else {
				msg = CC.PRIMARY + "Searching in ELO range " + CC.SECONDARY + (elo - range < EloWrapper.MININMUM_ELO ? EloWrapper.MININMUM_ELO
						: StringUtil.formatNumberWithCommas(elo - range)) + CC.PRIMARY + " - " + CC.SECONDARY
						+ StringUtil.formatNumberWithCommas(elo + range) + CC.PRIMARY + "...";
				range += 50;
			}

			if (entry.isParty()) {
				profile.getParty().broadcast(msg);
			} else {
				plugin.getCustomScoreboard().forceUpdate(player);
				player.sendMessage(msg);
			}
		}

		ticksElapsed += 5L;
	}

	public int[] getRange() {
		int elo = entry.getElo();

		return new int[]{
				(elo - range) + 50 < EloWrapper.MININMUM_ELO ? EloWrapper.MININMUM_ELO : (elo - range) + 50,
				(elo + range) - 50
		};
	}

	public void start() {
		runTaskTimer(plugin, 0L, 5L);
	}
}
