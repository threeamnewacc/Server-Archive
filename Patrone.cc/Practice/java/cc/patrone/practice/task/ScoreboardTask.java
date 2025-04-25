package cc.patrone.practice.task;

import cc.patrone.practice.PracticePlugin;
import lombok.AllArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class ScoreboardTask extends BukkitRunnable {

    private PracticePlugin plugin;

    public void run() {
        this.plugin.getServer().getOnlinePlayers().stream().forEach(p -> this.plugin.getManagerHandler().getScoreboardManager().update(p));
    }
}
