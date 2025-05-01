package net.badlion.build;

import net.badlion.build.commands.AddArenaCommand;
import net.badlion.build.commands.AddWarpCommand;
import net.badlion.build.listeners.GameModeListener;
import net.badlion.build.tasks.RestartTask;
import net.badlion.build.tasks.SaveAllTask;
import org.bukkit.plugin.java.JavaPlugin;

public class BuildPlugin extends JavaPlugin {

    private static BuildPlugin plugin;
    public static BuildPlugin getInstance() {
        return BuildPlugin.plugin;
    }

    public BuildPlugin() {
        BuildPlugin.plugin = this;
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new GameModeListener(), this);

        new SaveAllTask().runTaskTimer(this, 60 * 20 * 5, 60 * 20 * 5);
        new RestartTask().runTaskTimer(this, 60 * 20, 60 * 20);

        this.getCommand("addarena").setExecutor(new AddArenaCommand());
        this.getCommand("addwarp").setExecutor(new AddWarpCommand());
    }

    @Override
    public void onDisable() {

    }

}
