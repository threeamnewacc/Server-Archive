package net.badlion.build.commands;

import net.badlion.build.BuildPlugin;
import net.badlion.build.managers.ArenaManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddArenaCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, final String[] args) {
        final Player player = (Player) sender;

        String warp1 = "";
        String warp2 = "";
        if (args.length >= 3) {
            warp1 = args[2];
            if (args.length == 4) {
                warp2 = args[3];
            } else {
                return true;
            }
        } else {
            return true;
        }

        final String warp1Final = warp1;
        final String warp2Final = warp2;
        BuildPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(BuildPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                ArenaManager.addArena(player, args[0], args[1], warp1Final, warp2Final);
            }
        });

        return true;
    }

}
