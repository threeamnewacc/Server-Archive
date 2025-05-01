package net.badlion.score;

import org.bukkit.plugin.java.JavaPlugin;

public class ScorePlugin extends JavaPlugin {

    private static ScorePlugin plugin;
    private String tag;

    public static ScorePlugin getInstance() {
        return ScorePlugin.plugin;
    }

    public ScorePlugin() {
        ScorePlugin.plugin = this;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public String getTag() {
        return this.tag;
    }

}
