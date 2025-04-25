package us.zonix.practice.listeners;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import us.zonix.practice.Practice;
import us.zonix.practice.arena.StandaloneArena;
import us.zonix.practice.match.Match;
import us.zonix.practice.match.MatchState;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;

public class WorldListener implements Listener {
    private final Practice plugin = Practice.getInstance();

    @EventHandler(
        ignoreCancelled = true,
        priority = EventPriority.HIGH
    )
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            this.plugin.getLogger().warning(player.getName() + "'s player data is null");
            event.setCancelled(true);
        } else {
            if (playerData.getPlayerState() == PlayerState.FIGHTING) {
                Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
                if (match.getKit().isBuild()) {
                    if (!match.getPlacedBlockLocations().contains(event.getBlock().getLocation())) {
                        event.setCancelled(true);
                    }
                } else if (match.getKit().isSpleef()) {
                    double minX = match.getStandaloneArena().getMin().getX();
                    double minZ = match.getStandaloneArena().getMin().getZ();
                    double maxX = match.getStandaloneArena().getMax().getX();
                    double maxZ = match.getStandaloneArena().getMax().getZ();
                    if (minX > maxX) {
                        double lastMinX = minX;
                        minX = maxX;
                        maxX = lastMinX;
                    }

                    if (minZ > maxZ) {
                        double lastMinZ = minZ;
                        minZ = maxZ;
                        maxZ = lastMinZ;
                    }

                    if (match.getMatchState() == MatchState.STARTING) {
                        event.setCancelled(true);
                        return;
                    }

                    if (!(player.getLocation().getX() >= minX)
                        || !(player.getLocation().getX() <= maxX)
                        || !(player.getLocation().getZ() >= minZ)
                        || !(player.getLocation().getZ() <= maxZ)) {
                        event.setCancelled(true);
                    } else if (event.getBlock().getType() == Material.SNOW_BLOCK && player.getItemInHand().getType() == Material.DIAMOND_SPADE) {
                        Location blockLocation = event.getBlock().getLocation();
                        event.setCancelled(true);
                        match.addOriginalBlockChange(event.getBlock().getState());
                        Set<Item> items = new HashSet<>();
                        event.getBlock()
                            .getDrops()
                            .forEach(itemStack -> items.add(player.getWorld().dropItemNaturally(blockLocation.add(0.0, 0.25, 0.0), itemStack)));
                        this.plugin.getMatchManager().addDroppedItems(match, items);
                        event.getBlock().setType(Material.AIR);
                    } else {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            } else if (!player.isOp() || player.getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(
        ignoreCancelled = true,
        priority = EventPriority.HIGH
    )
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            this.plugin.getLogger().warning(player.getName() + "'s player data is null");
            event.setCancelled(true);
        } else if (playerData.getPlayerState() == PlayerState.FIGHTING) {
            Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
            if (match == null) {
                event.setCancelled(true);
            } else {
                if (!match.getKit().isBuild()) {
                    event.setCancelled(true);
                } else {
                    double minX = match.getStandaloneArena().getMin().getX();
                    double minZ = match.getStandaloneArena().getMin().getZ();
                    double maxX = match.getStandaloneArena().getMax().getX();
                    double maxZ = match.getStandaloneArena().getMax().getZ();
                    if (minX > maxX) {
                        double lastMinX = minX;
                        minX = maxX;
                        maxX = lastMinX;
                    }

                    if (minZ > maxZ) {
                        double lastMinZ = minZ;
                        minZ = maxZ;
                        maxZ = lastMinZ;
                    }

                    if (!(player.getLocation().getX() >= minX)
                        || !(player.getLocation().getX() <= maxX)
                        || !(player.getLocation().getZ() >= minZ)
                        || !(player.getLocation().getZ() <= maxZ)) {
                        event.setCancelled(true);
                    } else if (player.getLocation().getY() - match.getStandaloneArena().getA().getY() < 5.0 && event.getBlockPlaced() != null) {
                        match.addPlacedBlockLocation(event.getBlockPlaced().getLocation());
                    } else {
                        event.setCancelled(true);
                    }
                }
            }
        } else {
            if (!player.isOp() || player.getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            this.plugin.getLogger().warning(player.getName() + "'s player data is null");
            event.setCancelled(true);
        } else if (playerData.getPlayerState() == PlayerState.FIGHTING) {
            Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
            if (!match.getKit().isBuild()) {
                event.setCancelled(true);
            } else {
                double minX = match.getStandaloneArena().getMin().getX();
                double minZ = match.getStandaloneArena().getMin().getZ();
                double maxX = match.getStandaloneArena().getMax().getX();
                double maxZ = match.getStandaloneArena().getMax().getZ();
                if (minX > maxX) {
                    double lastMinX = minX;
                    minX = maxX;
                    maxX = lastMinX;
                }

                if (minZ > maxZ) {
                    double lastMinZ = minZ;
                    minZ = maxZ;
                    maxZ = lastMinZ;
                }

                if (!(player.getLocation().getX() >= minX)
                    || !(player.getLocation().getX() <= maxX)
                    || !(player.getLocation().getZ() >= minZ)
                    || !(player.getLocation().getZ() <= maxZ)) {
                    event.setCancelled(true);
                } else if (player.getLocation().getY() - match.getStandaloneArena().getA().getY() < 5.0) {
                    Block block = event.getBlockClicked().getRelative(event.getBlockFace());
                    match.addPlacedBlockLocation(block.getLocation());
                } else {
                    event.setCancelled(true);
                }
            }
        } else {
            if (!player.isOp() || player.getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        if (event.getToBlock() != null) {
            for (StandaloneArena arena : this.plugin.getArenaManager().getArenaMatchUUIDs().keySet()) {
                double minX = arena.getMin().getX();
                double minZ = arena.getMin().getZ();
                double maxX = arena.getMax().getX();
                double maxZ = arena.getMax().getZ();
                if (minX > maxX) {
                    double lastMinX = minX;
                    minX = maxX;
                    maxX = lastMinX;
                }

                if (minZ > maxZ) {
                    double lastMinZ = minZ;
                    minZ = maxZ;
                    maxZ = lastMinZ;
                }

                if ((double)event.getToBlock().getX() >= minX
                    && (double)event.getToBlock().getZ() >= minZ
                    && (double)event.getToBlock().getX() <= maxX
                    && (double)event.getToBlock().getZ() <= maxZ) {
                    UUID matchUUID = this.plugin.getArenaManager().getArenaMatchUUID(arena);
                    Match match = this.plugin.getMatchManager().getMatchFromUUID(matchUUID);
                    match.addPlacedBlockLocation(event.getToBlock().getLocation());
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }
}
