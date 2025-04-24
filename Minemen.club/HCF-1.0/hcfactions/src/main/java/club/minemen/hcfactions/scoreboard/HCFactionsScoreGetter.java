package club.minemen.hcfactions.scoreboard;

import club.minemen.clublibrary.ClubLibraryPlugin;
import club.minemen.clublibrary.scoreboard.ClubScoreboardHandler;
import club.minemen.clublibrary.scoreboard.ScoreGetter;
import club.minemen.clublibrary.scoreboard.ScoreboardConfiguration;
import club.minemen.clublibrary.scoreboard.def.LineScoreGetter;
import club.minemen.core.util.finalutil.CC;
import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.type.StuckTask;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 1/1/2018
 */
@RequiredArgsConstructor
public class HCFactionsScoreGetter implements ScoreGetter {

	private final HCFactions plugin;

	public static ScoreboardConfiguration getConfiguration() {
		ScoreboardConfiguration configuration = new ScoreboardConfiguration();

		String mapNumber = Conf.map + "";
		if(mapNumber.endsWith(".0")) {
			mapNumber = mapNumber.substring(0, mapNumber.length() - 2);
		}

		configuration.setTitle(CC.B_SECONDARY + "HCFactions " + CC.GRAY + "▏" + CC.WHITE + " Map " + mapNumber);

		configuration.getScoreGetters().add(new HCFactionsScoreGetter(HCFactions.getInstance()));

		new BukkitRunnable() {
			@Override
			public void run() {
				sortScoreGetters();
			}
		}.runTaskLater(ClubLibraryPlugin.getInstance(), 20L);

		return configuration;
	}

	private static void sortScoreGetters() {
		// This shit is aids I know
		// If someone wants to make this better, go for it!
		List<ScoreGetter> scoreGetters = new ArrayList<>();

		ScoreGetter hcf = null, events = null, classes = null;
		for (ScoreGetter scoreGetter : ClubScoreboardHandler.getScoreboardConfiguration().getScoreGetters()) {
			Plugin plugin = JavaPlugin.getProvidingPlugin(scoreGetter.getClass());
			if (scoreGetter instanceof HCFactionsScoreGetter) {
				hcf = scoreGetter;
			} else if (plugin.getName().toLowerCase().contains("events")) {
				events = scoreGetter;
			} else if (plugin.getName().toLowerCase().contains("classes")) {
				classes = scoreGetter;
			}
		}

		scoreGetters.add(new LineScoreGetter(20));
		if (events != null) {
			scoreGetters.add(events);
		}
		if (classes != null) {
			scoreGetters.add(classes);
		}
		if (hcf != null) {
			scoreGetters.add(hcf);
		}
		scoreGetters.add(new LineScoreGetter(20));

		ClubScoreboardHandler.getScoreboardConfiguration().getScoreGetters().clear();
		ClubScoreboardHandler.getScoreboardConfiguration().getScoreGetters().addAll(scoreGetters);
	}

	@Override
	public List<String> getScore(Player player) {
		List<String> scores = new ArrayList<>();

		FactionPlayer factionPlayer = FPlayers.getInstance().getByName(player.getName());
		if (factionPlayer.hasPvpProtection()) {
			long pvpProtectionTimeLeft = factionPlayer.getPvpProtectionTime();
			long seconds = pvpProtectionTimeLeft / 1000 % 60;
			long minutes = pvpProtectionTimeLeft / 1000 / 60;
			scores.add(CC.B_GREEN + "PvP Prot: " + ChatColor.WHITE + String.format("%02d:%02d", minutes, seconds));
		}

		if (factionPlayer.isPvpTagged()) {
			long seconds = Math.max(factionPlayer.getPvpTagRemaining(), 0) / 1000;
			scores.add(CC.B_RED + "PvP Tag: " + ChatColor.WHITE + String.format("%d", seconds));
		}

		StuckTask stuckTask = HCFactions.getInstance().getStuckTasks().get(player);
		if(stuckTask != null) {
			scores.add(CC.B_GOLD + "Stuck: " + ChatColor.WHITE + stuckTask.getCountdown());
		}

		return scores;
	}
}
