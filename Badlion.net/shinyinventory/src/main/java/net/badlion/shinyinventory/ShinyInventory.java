package net.badlion.shinyinventory;

import net.badlion.shinyinventory.gui.InterfaceManager;
import net.badlion.shinyinventory.listeners.InterfaceListener;
import net.badlion.shinyinventory.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by ShinyDialga45 on 6/20/2015.
 */
public class ShinyInventory extends JavaPlugin {

    private static ShinyInventory instance;

    public void onEnable() {
        instance = this;

        getServer().getPluginManager().registerEvents(new InterfaceListener(), getInstance());

        CommandUtils.registerCommands();
    }

    public void onDisable() {
        InterfaceManager.unregisterInventories();
    }

    public static ShinyInventory getInstance() {
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return CommandUtils.onCommand(sender, cmd, commandLabel, args);
    }

    public void callEvent(Event event) {
        getInstance().getServer().getPluginManager().callEvent(event);
    }

}
