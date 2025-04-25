package us.zonix.practice.player;

import net.edater.spigot.handler.PacketHandler;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.practice.Practice;

public class CPSHandler implements PacketHandler {
    private final Practice plugin;

    public CPSHandler(final Practice plugin) {
        this.plugin = plugin;
        (new BukkitRunnable() {
            public void run() {
                for (PlayerData current : plugin.getPlayerManager().getAllData()) {
                    current.setCps(current.getCurrentCps());
                    current.setCurrentCps(0);
                }
            }
        }).runTaskTimerAsynchronously(plugin, 0L, 20L);
    }

    public void handleReceivedPacket(PlayerConnection playerConnection, Packet packet) {
        if (packet instanceof PacketPlayInArmAnimation) {
            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(playerConnection.getPlayer().getUniqueId());
            playerData.setCurrentCps(playerData.getCurrentCps() + 1);
        }
    }

    public void handleSentPacket(PlayerConnection playerConnection, Packet packet) {
    }
}
