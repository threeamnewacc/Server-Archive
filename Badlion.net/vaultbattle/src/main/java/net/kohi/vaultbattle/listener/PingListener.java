package net.kohi.vaultbattle.listener;

import net.kohi.vaultbattle.VaultBattlePlugin;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class PingListener implements Listener {

    private static Integer countdown;
    private final VaultBattlePlugin plugin;

    public PingListener(VaultBattlePlugin plugin) {
        this.plugin = plugin;
    }

    public static void setCountdown(Integer countdown) {
        PingListener.countdown = countdown;
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        if (plugin.getGameManager().getPhase() == null) {
            return;
        }
        switch (plugin.getGameManager().getPhase()) {
            case PRE_START:
                if (countdown != null) {
                    event.setMotd(ChatColor.GREEN + "Starting in\n" + ChatColor.GREEN + countdown);
                } else {
                    event.setMotd(ChatColor.GREEN + "Join Now!");
                }
                break;
            case STARTED:
                if (!plugin.getGameManager().isWallsDropped()) {
                    event.setMotd(ChatColor.BLUE + "In Progress\n" + ChatColor.GREEN + "Joinable!");
                } else {
                    event.setMotd(ChatColor.BLUE + "In Progress");
                }
                break;
            case WON:
                event.setMotd(ChatColor.YELLOW + "Rebooting");
                break;
        }
    }
}
