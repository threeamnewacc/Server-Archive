// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.listeners;

import org.bukkit.inventory.ItemStack;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.PreChunkLoadEvent;
import java.util.UUID;
import java.util.Iterator;
import club.minemen.practice.arena.StandaloneArena;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import java.util.Set;
import org.bukkit.Location;
import club.minemen.practice.match.Match;
import club.minemen.practice.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;
import org.bukkit.entity.Item;
import java.util.HashSet;
import org.bukkit.Material;
import club.minemen.practice.match.MatchState;
import club.minemen.practice.player.PlayerState;
import org.bukkit.event.block.BlockBreakEvent;
import club.minemen.practice.Practice;
import org.bukkit.event.Listener;

public class WorldListener implements Listener
{
    private final Practice plugin;
    
    public WorldListener() {
        this.plugin = Practice.getInstance();
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            this.plugin.getLogger().warning(player.getName() + "'s player data is null");
            event.setCancelled(true);
            return;
        }
        if (playerData.getPlayerState() == PlayerState.FIGHTING) {
            final Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
            if (match.getKit().isBuild()) {
                if (!match.getPlacedBlockLocations().contains(event.getBlock().getLocation())) {
                    event.setCancelled(true);
                }
            }
            else if (match.getKit().isSpleef()) {
                double minX = match.getStandaloneArena().getMin().getX();
                double minZ = match.getStandaloneArena().getMin().getZ();
                double maxX = match.getStandaloneArena().getMax().getX();
                double maxZ = match.getStandaloneArena().getMax().getZ();
                if (minX > maxX) {
                    final double lastMinX = minX;
                    minX = maxX;
                    maxX = lastMinX;
                }
                if (minZ > maxZ) {
                    final double lastMinZ = minZ;
                    minZ = maxZ;
                    maxZ = lastMinZ;
                }
                if (match.getMatchState() == MatchState.STARTING) {
                    event.setCancelled(true);
                    return;
                }
                if (player.getLocation().getX() >= minX && player.getLocation().getX() <= maxX && player.getLocation().getZ() >= minZ && player.getLocation().getZ() <= maxZ) {
                    if (event.getBlock().getType() == Material.SNOW_BLOCK && player.getItemInHand().getType() == Material.DIAMOND_SPADE) {
                        final Location blockLocation = event.getBlock().getLocation();
                        event.setCancelled(true);
                        match.addOriginalBlockChange(event.getBlock().getState());
                        final Set<Item> items = new HashSet<Item>();
                        event.getBlock().getDrops().forEach(itemStack -> items.add(player.getWorld().dropItemNaturally(blockLocation.add(0.0, 0.25, 0.0), itemStack)));
                        this.plugin.getMatchManager().addDroppedItems(match, items);
                        event.getBlock().setType(Material.AIR);
                    }
                    else {
                        event.setCancelled(true);
                    }
                }
                else {
                    event.setCancelled(true);
                }
            }
            else {
                event.setCancelled(true);
            }
        }
        else if (!player.isOp() || player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            this.plugin.getLogger().warning(player.getName() + "'s player data is null");
            event.setCancelled(true);
            return;
        }
        if (playerData.getPlayerState() == PlayerState.FIGHTING) {
            final Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
            if (!match.getKit().isBuild()) {
                event.setCancelled(true);
            }
            else {
                double minX = match.getStandaloneArena().getMin().getX();
                double minZ = match.getStandaloneArena().getMin().getZ();
                double maxX = match.getStandaloneArena().getMax().getX();
                double maxZ = match.getStandaloneArena().getMax().getZ();
                if (minX > maxX) {
                    final double lastMinX = minX;
                    minX = maxX;
                    maxX = lastMinX;
                }
                if (minZ > maxZ) {
                    final double lastMinZ = minZ;
                    minZ = maxZ;
                    maxZ = lastMinZ;
                }
                if (player.getLocation().getX() >= minX && player.getLocation().getX() <= maxX && player.getLocation().getZ() >= minZ && player.getLocation().getZ() <= maxZ) {
                    if (player.getLocation().getY() - match.getStandaloneArena().getA().getY() < 5.0) {
                        match.addPlacedBlockLocation(event.getBlockPlaced().getLocation());
                    }
                    else {
                        event.setCancelled(true);
                    }
                }
                else {
                    event.setCancelled(true);
                }
            }
            return;
        }
        if (!player.isOp() || player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBucketEmpty(final PlayerBucketEmptyEvent event) {
        final Player player = event.getPlayer();
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            this.plugin.getLogger().warning(player.getName() + "'s player data is null");
            event.setCancelled(true);
            return;
        }
        if (playerData.getPlayerState() == PlayerState.FIGHTING) {
            final Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
            if (!match.getKit().isBuild()) {
                event.setCancelled(true);
            }
            else {
                double minX = match.getStandaloneArena().getMin().getX();
                double minZ = match.getStandaloneArena().getMin().getZ();
                double maxX = match.getStandaloneArena().getMax().getX();
                double maxZ = match.getStandaloneArena().getMax().getZ();
                if (minX > maxX) {
                    final double lastMinX = minX;
                    minX = maxX;
                    maxX = lastMinX;
                }
                if (minZ > maxZ) {
                    final double lastMinZ = minZ;
                    minZ = maxZ;
                    maxZ = lastMinZ;
                }
                if (player.getLocation().getX() >= minX && player.getLocation().getX() <= maxX && player.getLocation().getZ() >= minZ && player.getLocation().getZ() <= maxZ) {
                    if (player.getLocation().getY() - match.getStandaloneArena().getA().getY() < 5.0) {
                        final Block block = event.getBlockClicked().getRelative(event.getBlockFace());
                        match.addPlacedBlockLocation(block.getLocation());
                    }
                    else {
                        event.setCancelled(true);
                    }
                }
                else {
                    event.setCancelled(true);
                }
            }
            return;
        }
        if (!player.isOp() || player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockFromTo(final BlockFromToEvent event) {
        if (event.getToBlock() == null) {
            return;
        }
        for (final StandaloneArena arena : this.plugin.getArenaManager().getArenaMatchUUIDs().keySet()) {
            double minX = arena.getMin().getX();
            double minZ = arena.getMin().getZ();
            double maxX = arena.getMax().getX();
            double maxZ = arena.getMax().getZ();
            if (minX > maxX) {
                final double lastMinX = minX;
                minX = maxX;
                maxX = lastMinX;
            }
            if (minZ > maxZ) {
                final double lastMinZ = minZ;
                minZ = maxZ;
                maxZ = lastMinZ;
            }
            if (event.getToBlock().getX() >= minX && event.getToBlock().getZ() >= minZ && event.getToBlock().getX() <= maxX && event.getToBlock().getZ() <= maxZ) {
                final UUID matchUUID = this.plugin.getArenaManager().getArenaMatchUUID(arena);
                final Match match = this.plugin.getMatchManager().getMatchFromUUID(matchUUID);
                match.addPlacedBlockLocation(event.getToBlock().getLocation());
                break;
            }
        }
    }
    
    @EventHandler
    public void onChunkLoad(final PreChunkLoadEvent event) {
        if (this.plugin.getChunkManager().isChunksLoaded() && this.plugin.getArenaManager().getGeneratingArenaRunnables() == 0) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onWeatherChange(final WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onLeavesDecay(final LeavesDecayEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onHangingBreak(final HangingBreakEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onBlockBurn(final BlockBurnEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onBlockSpread(final BlockSpreadEvent event) {
        event.setCancelled(true);
    }
}
