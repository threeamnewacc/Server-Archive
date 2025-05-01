package net.badlion.gcheat.listeners;

import net.badlion.gcheat.GCheat;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BlockGlitchListener implements Listener {

    private static final int BLOCK_BREAK_MILLIS = 1000; // time after trying to break a block that a player can not attack
    private final Set<UUID> pendingTp = new HashSet<>();
    private final Map<UUID, Long> blockBreakTime = new HashMap<>();

    @EventHandler(priority = EventPriority.LASTEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.isCancelled() || event.getPlayer().getGameMode() == GameMode.CREATIVE || !event.getBlock().getType().isSolid()) {
            return;
        }
        final Player player = event.getPlayer();
        final Location loc = event.getPlayer().getLocation();
        // if the player placed a block somewhat under them and they don't have a pending teleport already
        if (loc.getY() > event.getBlock().getY() + 1 && pendingTp.add(player.getUniqueId())) {
            // teleport them back to their location 10 ticks later
            new BukkitRunnable() {
                @Override
                public void run() {
                    // make sure not to tp them to a different world or more than 10 blocks distance
                    if (!player.isDead() && player.getWorld() == loc.getWorld() && player.getLocation().distanceSquared(loc) < 10 * 10) {
                        player.teleport(loc);
                        player.sendFormattedMessage("{0}Block glitching is not allowed!", ChatColor.RED);
                    }
                    pendingTp.remove(player.getUniqueId());
                }
            }.runTaskLater(GCheat.plugin, 10);
        }
    }

    @EventHandler(priority = EventPriority.LASTEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isCancelled() || event.getPlayer().getGameMode() == GameMode.CREATIVE || !event.getBlock().getType().isSolid()) {
            return;
        }
        long now = System.currentTimeMillis();
        Iterator<Long> iter = blockBreakTime.values().iterator();
        while (iter.hasNext()) {
            if (now - iter.next() > BLOCK_BREAK_MILLIS) {
                iter.remove();
            }
        }
        blockBreakTime.put(event.getPlayer().getUniqueId(), now);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        // prevent breaking a block client side to attack an entity behind it
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getDamager();
        if (hasRecentBlockBreak(player)) {
            event.setCancelled(true);
            player.sendFormattedMessage("{0}Block glitching is not allowed!", ChatColor.RED);
            // TODO: throw a gcheat event? Pretty handy to have on factions
        }
    }

    @EventHandler
    public void onRightClickEntity(PlayerInteractEntityEvent event) {
        // prevent breaking a block client side to right click an entity behind it
        // for example with vehicles with could be a way to glitch into bases
        if (hasRecentBlockBreak(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendFormattedMessage("{0}Block glitching is not allowed!", ChatColor.RED);
        }
    }

    private boolean hasRecentBlockBreak(Player player) {
        Long time = blockBreakTime.get(player.getUniqueId());
        if (time == null) {
            return false;
        }
        if (System.currentTimeMillis() - time < BLOCK_BREAK_MILLIS) {
            return true;
        }
        blockBreakTime.remove(player.getUniqueId());
        return false;
    }
}
