package cc.patrone.practice;

import cc.patrone.core.CorePlugin;
import cc.patrone.practice.command.*;
import cc.patrone.practice.command.management.*;
import cc.patrone.practice.listener.PlayerListener;
import cc.patrone.practice.manager.ManagerHandler;
import cc.patrone.practice.tab.TabAdapter;
import cc.patrone.practice.task.InventoryTask;
import cc.patrone.practice.task.LeaderboardTask;
import cc.patrone.practice.task.ScoreboardTask;
import cc.patrone.practice.util.ScoreHelper;
import io.github.thatkawaiisam.ziggurat.Ziggurat;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

@Getter
public class PracticePlugin extends JavaPlugin {

    @Getter
    private static PracticePlugin instance;

    private CorePlugin corePlugin;
    private ManagerHandler managerHandler;

    @Override
    public void onEnable() {
        instance = this;

        getConfig().options().copyDefaults(true);
        saveConfig();

        if (getServer().getPluginManager().getPlugin("Core") == null) {
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        corePlugin = (CorePlugin) getServer().getPluginManager().getPlugin("Core");

        managerHandler = new ManagerHandler(this);
        managerHandler.registerManagers();

        registerListeners(
                new PlayerListener(this)
        );

        this.getServer().getOnlinePlayers().stream().forEach(p -> {
            ScoreHelper.createScore(p);
            this.managerHandler.getProfileManager().addPlayer(p);
        });

        Ziggurat ziggurat = new Ziggurat(this, new TabAdapter(this));
        ziggurat.setHook(true);

        corePlugin.registerCommands(
                new ToggleScoreboardCommand(this),
                new SetSpawnCommand(this),
                new SetEditorCommand(this),
                new ArenaCommand(this),
                new KitCommand(this),
                new DuelCommands(this),
                new BuildModeCommand(this),
                new SpectateCommand(this),
                new InventoryCommand(this),
                new TimeCommands(),
                new LeaderboardsCommand(this)
        );

        registerTasks(
                new ScoreboardTask(this),
                new InventoryTask(this)
        );

        new LeaderboardTask(this).runTaskTimerAsynchronously(this, 0L, 12000L);
    }

    @Override
    public void onDisable() {
        managerHandler.save();
    }

    private void registerTasks(BukkitRunnable... bukkitRunnables) {
        Arrays.stream(bukkitRunnables).forEach(b -> b.runTaskTimerAsynchronously(this, 0L, 0L));
    }
}
