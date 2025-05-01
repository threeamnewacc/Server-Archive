package net.badlion.gfactions.commands.events;

import net.badlion.gfactions.events.DragonEvent;
import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.tasks.dragon.KillDragonTask;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DragonEventCommand implements CommandExecutor {

    private GFactions plugin;

    public DragonEventCommand(GFactions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        DragonEvent dragonEvent = new DragonEvent(this.plugin);

        KillDragonTask killDragonTask = new KillDragonTask(this.plugin);
        killDragonTask.runTaskLater(this.plugin, 72000L);

        dragonEvent.startEvent();

        this.plugin.setDragonEvent(dragonEvent);
        this.plugin.setKillDragonTask(killDragonTask);
        return true;
    }

}
