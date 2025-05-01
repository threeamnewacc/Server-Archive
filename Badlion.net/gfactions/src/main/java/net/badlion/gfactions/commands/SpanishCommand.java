package net.badlion.gfactions.commands;

import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpanishCommand implements CommandExecutor {

    private GFactions plugin;

    public SpanishCommand(GFactions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("GFactions.mod")) {
                if (args.length == 1) {
                    Player beaner = this.plugin.getServer().getPlayer(args[0]);
                    if (beaner != null) {
                        // MOVE THEM TO THE FUCKING MEXI CHANEL
                        beaner.performCommand("ch join e");
                        beaner.sendMessage(ChatColor.YELLOW + "Has sido movido al canal espanol");
                        player.sendMessage(ChatColor.GREEN + "Player has been moved to Spanish Channel.");
                    } else {
                        player.sendMessage(ChatColor.RED + "Player is offline or nonexistant.");
                    }
                }
            } else {
                player.performCommand("ch join e");
                player.sendMessage(ChatColor.YELLOW + "Has sido movido al canal espanol");
            }
        }

        return true;
    }

}
