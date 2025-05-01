package net.badlion.essentials;

import net.badlion.essentials.commands.home.DeleteHomeCommand;
import net.badlion.essentials.commands.home.HomeCommand;
import net.badlion.essentials.commands.home.ListHomesCommand;
import net.badlion.essentials.commands.home.SetHomeCommand;
import net.badlion.essentials.commands.warp.DeleteWarpCommand;
import net.badlion.essentials.commands.warp.SetWarpCommand;
import net.badlion.essentials.commands.warp.WarpCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class EssentialsPlugin extends JavaPlugin {

    private static EssentialsPlugin plugin;
    public static EssentialsPlugin getInstance() {
        return EssentialsPlugin.plugin;
    }

    private static String prefix = "";
    public static String getPrefix() {
        return EssentialsPlugin.prefix;
    }

    public EssentialsPlugin() {
        EssentialsPlugin.plugin = this;
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        EssentialsPlugin.prefix = this.getConfig().getString("db_prefix");

        if (EssentialsPlugin.prefix.equalsIgnoreCase("default")) {
            this.getServer().getLogger().info("=====MISSING ESSENTIALS PREFIX=====");
            this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "stop");
            return;
        }

        this.getCommand("delhome").setExecutor(new DeleteHomeCommand());
        this.getCommand("home").setExecutor(new HomeCommand());
        this.getCommand("listhomes").setExecutor(new ListHomesCommand());
        this.getCommand("sethome").setExecutor(new SetHomeCommand());
        this.getCommand("delwarp").setExecutor(new DeleteWarpCommand());
        this.getCommand("setwarp").setExecutor(new SetWarpCommand());
        this.getCommand("warp").setExecutor(new WarpCommand());
    }

    @Override
    public void onDisable() {

    }



}
