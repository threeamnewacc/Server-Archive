package us.zonix.practice.commands;

import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;

public class FlyCommand extends Command {
    private final Practice plugin = Practice.getInstance();

    public FlyCommand() {
        super("fly");
        this.setDescription("Toggles flight.");
        this.setUsage(ChatColor.RED + "Usage: /fly");
        this.setAliases(Arrays.asList("flight"));
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {
            Player player = (Player)sender;
            if (!player.hasPermission("practice.fly")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
                return true;
            } else {
                PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
                if (playerData.getPlayerState() != PlayerState.SPAWN) {
                    player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
                    return true;
                } else {
                    player.setAllowFlight(!player.getAllowFlight());
                    if (player.getAllowFlight()) {
                        player.sendMessage(ChatColor.YELLOW + "Your flight has been enabled.");
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "Your flight has been disabled.");
                    }

                    return true;
                }
            }
        }
    }
}
