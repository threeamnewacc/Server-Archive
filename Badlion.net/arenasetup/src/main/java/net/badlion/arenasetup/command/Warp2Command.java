package net.badlion.arenasetup.command;

import net.badlion.arenasetup.ArenaSetup;
import net.badlion.arenasetup.SetupSession;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Warp2Command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (!ArenaSetup.getInstance().getSetupSessionMap().containsKey(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "ERROR: You are not setting up an arena!");
                return false;
            }
            SetupSession setupSession = ArenaSetup.getInstance().getSetupSessionMap().get(player.getUniqueId());
            setupSession.setWarp2(player.getLocation());
            player.sendMessage("Set Warp 2.");
        }
        return false;
    }
}
