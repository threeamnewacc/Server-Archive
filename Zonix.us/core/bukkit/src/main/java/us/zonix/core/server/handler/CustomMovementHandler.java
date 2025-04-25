package us.zonix.core.server.handler;

import club.minemen.spigot.handler.MovementHandler;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import us.zonix.core.CorePlugin;

public class CustomMovementHandler implements MovementHandler {

    @Override
    public void handleUpdateLocation(Player player, Location to, Location from, PacketPlayInFlying packetPlayInFlying) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (player.getLocation().getBlockY() <= 0 && CorePlugin.getInstance().getSpawnLocation() != null) {
            player.teleport(CorePlugin.getInstance().getSpawnLocation());
        }

        if (player.isOnGround()) {
            player.setAllowFlight(true);
            player.setFlying(false);
        }
    }

    @Override
    public void handleUpdateRotation(Player player, Location location, Location location1, PacketPlayInFlying packetPlayInFlying) {

    }

}
