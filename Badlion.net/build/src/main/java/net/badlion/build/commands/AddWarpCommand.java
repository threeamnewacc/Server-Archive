package net.badlion.build.commands;

import net.badlion.build.BuildPlugin;
import net.badlion.build.managers.ArenaManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddWarpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, final String[] args) {
        final Player player = (Player) sender;
        BuildPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(BuildPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                ArenaManager.addWarp(args[0], player);
            }
        });

        return true;
    }


}
