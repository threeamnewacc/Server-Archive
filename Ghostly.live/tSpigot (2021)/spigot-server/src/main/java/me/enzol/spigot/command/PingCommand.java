package me.enzol.spigot.command;

import me.enzol.spigot.*;
import java.util.*;

import me.enzol.spigot.TrainingSpigot;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.apache.commons.lang3.*;
import org.bukkit.*;
import org.bukkit.craftbukkit.entity.*;

public class PingCommand extends Command
{
    public PingCommand() {
        super("ping", "See the ping of the player or yourself", "/ping" + ChatColor.AQUA + "<player>", Arrays.asList("lag"));
        this.setPermission("spigot.ping");
    }

    @Override
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        Player toCheck;
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You can't check the ping of Console");
                return true;
            }
            toCheck = (Player)sender;
        }
        else {
            toCheck = Bukkit.getPlayer(StringUtils.join(args));
        }
        if (toCheck == null) {
            sender.sendMessage(String.valueOf(ChatColor.GRAY) + "The player called " + ChatColor.AQUA + args[0] + ChatColor.GRAY + " is not online.");
            return true;
        }
        sender.sendMessage(String.valueOf(ChatColor.AQUA) + toCheck.getName() + ChatColor.GRAY + " current ping is " + TrainingSpigot.Dark_Aqua + this.getPing(toCheck) + "ms" + ChatColor.GRAY + ".");
        return true;
    }

    private int getPing(final Player player) {
        return ((CraftPlayer)player).getHandle().ping;
    }
}
