package net.badlion.gfactions.commands;

import net.badlion.gfactions.commands.admin.DelHomeCommand;
import net.badlion.gfactions.commands.admin.HomesCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class AdminCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can exectue these commands.");
            return true;
        }

        Player player = (Player) sender;

        if (args[0].equals("homes")) {
            return HomesCommand.execute(player, Arrays.copyOfRange(args, 1, args.length));
        } else if (args[0].equals("delhome")) {
            return DelHomeCommand.execute(player, Arrays.copyOfRange(args, 1, args.length));
        } else if (args[0].equals("list")) {
            sender.sendMessage(ChatColor.AQUA + "/admin homes [name]");
            sender.sendMessage(ChatColor.AQUA + "/admin delhome [username] [home_name]");
        }

        return true;
    }

}
