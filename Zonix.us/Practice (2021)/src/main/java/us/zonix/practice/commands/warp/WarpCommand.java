package us.zonix.practice.commands.warp;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;

public class WarpCommand extends Command {
    private final Practice plugin = Practice.getInstance();

    public WarpCommand() {
        super("spawn");
        this.setDescription("Spawn command.");
        this.setUsage(ChatColor.RED + "Usage: /spawn [args]");
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {
            Player player = (Player)sender;
            if (!player.hasPermission("core.staff")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
                return true;
            } else {
                PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
                if (playerData.getPlayerState() != PlayerState.SPAWN && playerData.getPlayerState() != PlayerState.FFA) {
                    player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
                    return true;
                } else if (args.length == 0) {
                    this.plugin.getPlayerManager().sendToSpawnAndReset(player);
                    return true;
                } else {
                    return true;
                }
            }
        }
    }
}
