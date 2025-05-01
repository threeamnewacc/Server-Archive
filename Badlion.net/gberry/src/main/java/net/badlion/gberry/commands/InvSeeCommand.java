package net.badlion.gberry.commands;

import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InvSeeCommand implements CommandExecutor {

    private Gberry plugin;

    public InvSeeCommand(Gberry plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (sender instanceof Player) {
            if (strings.length != 1) {
                return false;
            }

            Player p = this.plugin.getServer().getPlayerExact(strings[0]);
            if (p != null) {
                ((Player) sender).openInventory(p.getInventory());

                // Add to our internal list
                this.plugin.getInvSeeing().add((Player) sender);
            } else {
                sender.sendMessage(ChatColor.RED + "That player is not online!");
            }
        } else {
            sender.sendMessage("This command can only be used in-game!");
        }
        return true;
    }

}
