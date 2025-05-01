package net.kohi.vaultbattle.command;

import com.sk89q.minecraft.util.commands.CommandException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandValidation {

    public static Player requirePlayer(CommandSender sender) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException("This command is only for players.");
        }
        return (Player) sender;
    }

    public static Player targetPlayer(String name) throws CommandException {
        Player target = Bukkit.getPlayer(name);
        if (target == null) {
            throw new CommandException("Player not found.");
        }
        return target;
    }

    public static void canSee(Player sender, Player target) throws CommandException {
        if (!sender.canSee(target)) {
            throw new CommandException("Player not found.");
        }
    }

    public static void notNull(Object obj, String message) throws CommandException {
        if (obj == null) {
            throw new CommandException(message);
        }
    }
}
