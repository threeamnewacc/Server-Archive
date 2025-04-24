package me.devkevin.practice.match.timer;

import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Data;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Data
public class TimerManager implements Listener {

	@Getter
	private final Set<Timer> timers = new LinkedHashSet<>();

	private final JavaPlugin plugin;

	public TimerManager(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	public void registerTimer(Timer timer) {
		this.timers.add(timer);
		if (timer instanceof Listener) {
			this.plugin.getServer().getPluginManager().registerEvents((Listener) timer, this.plugin);
		}
	}

	public void unregisterTimer(Timer timer) {
		this.timers.remove(timer);
	}

	public <T extends Timer> T getTimer(Class<T> timerClass) {
		for (Timer timer : this.timers) {
			if (timer.getClass().equals(timerClass)) {
				return (T) timer;
			}
		}

		return null;
	}
}