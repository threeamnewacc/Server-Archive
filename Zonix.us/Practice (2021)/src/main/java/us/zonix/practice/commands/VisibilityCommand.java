package us.zonix.practice.commands;

import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;

public class VisibilityCommand extends Command {
    private final Practice plugin = Practice.getInstance();

    public VisibilityCommand() {
        super("visibility");
        this.setDescription("Toggles visibility.");
        this.setUsage(ChatColor.RED + "Usage: /visibility");
        this.setAliases(Arrays.asList("visibility"));
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {
            Player player = (Player)sender;
            if (!player.hasPermission("practice.visibility")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
                return true;
            } else {
                PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
                if (playerData.getPlayerState() != PlayerState.SPAWN) {
                    player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
                    return true;
                } else {
                    playerData.getOptions().setVisibility(!playerData.getOptions().isVisibility());
                    this.plugin
                        .getServer()
                        .getOnlinePlayers()
                        .forEach(
                            p -> {
                                boolean playerSeen = playerData.getOptions().isVisibility()
                                    && player.hasPermission("practice.visibility")
                                    && Practice.getInstance().getPlayerManager().getPlayerData(player.getUniqueId()).getPlayerState() == PlayerState.SPAWN;
                                boolean pSeen = playerData.getOptions().isVisibility()
                                    && player.hasPermission("practice.visibility")
                                    && Practice.getInstance().getPlayerManager().getPlayerData(p.getUniqueId()).getPlayerState() == PlayerState.SPAWN;
                                if (playerSeen) {
                                    p.showPlayer(player);
                                } else {
                                    p.hidePlayer(player);
                                }

                                if (pSeen) {
                                    player.showPlayer(p);
                                } else {
                                    player.hidePlayer(p);
                                }
                            }
                        );
                    player.sendMessage(ChatColor.YELLOW + "You have toggled the visibility.");
                    return true;
                }
            }
        }
    }
}
