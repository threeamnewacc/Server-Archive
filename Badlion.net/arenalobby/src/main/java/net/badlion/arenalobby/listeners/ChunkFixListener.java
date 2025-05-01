package net.badlion.arenalobby.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Random;

public class ChunkFixListener implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // break out if they are MC 1.7.x players
        if (event.getPlayer().getVersion() <= 5) {
            return;
        }
        // break out if they didnt change block horizontally
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        for (int dx = -16; dx <= 16; dx += 8) {
            for (int dz = -16; dz <= 16; dz += 8) {
                Location loc = event.getTo().clone().add(dx, random.nextInt(16) - 8, dz);
                Block block = loc.getBlock();
                if (block.getType() != Material.AIR) {
                    event.getPlayer().sendBlockChange(loc, 0, (byte) 0);
                    event.getPlayer().sendBlockChange(loc, block.getTypeId(), block.getData());
                }
            }
        }
    }
}
