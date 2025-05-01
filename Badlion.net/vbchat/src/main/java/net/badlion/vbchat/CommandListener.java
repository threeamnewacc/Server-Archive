package net.badlion.vbchat;

import net.kohi.vaultbattle.VaultBattlePlugin;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

    @EventHandler
    public void channelCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().equalsIgnoreCase("/ch t")) {
            if (VaultBattlePlugin.getPlugin().getPlayerDataManager().get(event.getPlayer()).isPickingTeam()) {
                event.getPlayer().sendMessage(ChatColor.RED + "You are not in a team!");
                event.setCancelled(true);
            }
        }
    }

}
