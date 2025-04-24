package club.minemen.classes;

import club.minemen.classes.listener.PlayerListener;
import club.minemen.classes.manager.ClassManager;
import club.minemen.classes.manager.EffectManager;
import club.minemen.classes.playerclass.IPlayerClass;
import club.minemen.classes.task.PlayerTickTask;
import club.minemen.clublibrary.scoreboard.ClubScoreboardHandler;
import club.minemen.core.util.finalutil.CC;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 12/16/2017
 */
public final class ClassesPlugin extends JavaPlugin {

	@Getter
	private static ClassesPlugin instance;

	@Getter
	private ClassManager classManager;
	@Getter
	private EffectManager effectManager;

	@Override
	public void onEnable() {
		instance = this;

		this.classManager = new ClassManager();
		this.effectManager = new EffectManager();

		this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		new PlayerTickTask(this).runTaskTimer(this, 1L, 1L);

		ClubScoreboardHandler.getScoreboardConfiguration().getScoreGetters().add(player -> {
			List<String> scores = new ArrayList<>();

			IPlayerClass playerClass = this.getClassManager().getPlayersClass(player);
			if (playerClass != null) {
				scores.add(CC.B_GOLD + "Class: " + CC.RESET + playerClass.getName());
			}

			return scores;
		});
	}

	@Override
	public void onDisable() {

	}
}
