package net.badlion.arenasetup.command;

import net.badlion.arenasetup.ArenaSetup;
import net.badlion.arenasetup.manager.ArenaManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteArenaCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(args.length != 1){
                player.sendMessage("/delarena <arena_name>");
                return false;
            }
            ArenaManager.deleteArena(player, args[0]);
        }
        return false;
    }

}
