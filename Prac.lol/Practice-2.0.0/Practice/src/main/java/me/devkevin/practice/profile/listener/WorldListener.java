package me.devkevin.practice.profile.listener;

import me.devkevin.practice.Practice;
import me.devkevin.practice.arena.standalone.StandaloneArena;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchState;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.state.ProfileState;
import me.devkevin.practice.util.TaskUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WorldListener implements Listener {

    private Practice plugin;

    public WorldListener() {
        this.plugin = Practice.getInstance();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    final void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.isCancelled() && event.getPlayer().getLocation().getBlockY() > event.getBlock().getLocation().getBlockY()) {
            event.getPlayer().teleport(event.getPlayer().getLocation());
            event.getPlayer().setVelocity(new Vector());
            new BukkitRunnable() {
                public void run() {
                    event.getPlayer().setVelocity(new Vector(0.0, -0.25, 0.0));
                }
            }.runTaskLaterAsynchronously(this.plugin, 1L);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        if (profile == null) {
            this.plugin.getLogger().warning(player.getName() + "'s player data is null");
            event.setCancelled(true);
            return;
        }
        /*PracticeEvent playerEvent = this.plugin.getEventManager().getEventPlaying(player);

        if(playerEvent instanceof SpleefEvent){
            SpleefEvent spleefEvent = (SpleefEvent)playerEvent;

            if (event.getBlock().getType() == Material.SNOW_BLOCK && player.getItemInHand().getType() == Material.DIAMOND_SPADE) {
                Location blockLocation = event.getBlock().getLocation();

                event.setCancelled(true);
                spleefEvent.addBlock(event.getBlock().getLocation(), event.getBlock());
                event.getBlock().getDrops().forEach(itemStack -> player.getInventory().addItem(itemStack));
                event.getBlock().setType(Material.AIR);
                player.sendBlockChange(event.getBlock().getLocation(), Material.AIR, (byte)0);
                player.getWorld().getPlayers().forEach(other -> {
                    other.sendBlockChange(event.getBlock().getLocation(), Material.AIR, (byte)0);
                });
            }
        }*/

        if (profile.getState() == ProfileState.FIGHTING) {
            Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());

            // cancel event when match is starting
            if (match.getMatchState() == MatchState.STARTING) {
                event.setCancelled(true);
                return;
            }

            if (match.getKit().isBuild()) {
                if (!match.getPlacedBlockLocations().contains(event.getBlock().getLocation())) {
                    player.sendMessage(ChatColor.RED + "You can't break this block.");
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
                if (player.getLocation().getX() >= minX && player.getLocation().getX() <= maxX
                        && player.getLocation().getZ() >= minZ && player.getLocation().getZ() <= maxZ) {
                    if (event.getBlock().getType() == Material.SNOW_BLOCK && player.getItemInHand().getType() == Material.DIAMOND_SPADE) {
                        Location blockLocation = event.getBlock().getLocation();

                        event.setCancelled(true);
                        match.addOriginalBlockChange(event.getBlock().getState());
                        Set<Item> items = new HashSet<>();
                        event.getBlock().getDrops().forEach(itemStack -> items.add(player.getWorld().dropItemNaturally(blockLocation.add(0.0D, 0.25D, 0.0D), itemStack)));
                        this.plugin.getMatchManager().addDroppedItems(match, items);
                        event.getBlock().setType(Material.AIR);
                    } else {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        } else {
            if (player.getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        if (profile == null) {
            this.plugin.getLogger().warning(player.getName() + "'s player data is null");
            event.setCancelled(true);
            return;
        }

        if (profile.getState() == ProfileState.FIGHTING) {
            Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());

            // cancel event when match is starting
            if (match.getMatchState() == MatchState.STARTING) {
                event.setCancelled(true);
                return;
            }

            if(match == null) {
                event.setCancelled(true);
                return;
            }

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
                if (player.getLocation().getX() >= minX && player.getLocation().getX() <= maxX
                        && player.getLocation().getZ() >= minZ && player.getLocation().getZ() <= maxZ) {
                    if ((player.getLocation().getY() - match.getStandaloneArena().getA().getY()) < 5.0D && event.getBlockPlaced() != null) {
                        match.addPlacedBlockLocation(event.getBlockPlaced().getLocation());
                    } else {
                        event.setCancelled(true);
                    }
                } else {
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
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();

        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());
        if (profile == null) {
            this.plugin.getLogger().warning(player.getName() + "'s player data is null");
            event.setCancelled(true);
            return;
        }

        if (profile.getState() == ProfileState.FIGHTING) {
            Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());

            // cancel event when match is starting
            if (match.getMatchState() == MatchState.STARTING) {
                event.setCancelled(true);
                return;
            }

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
                if (player.getLocation().getX() >= minX && player.getLocation().getX() <= maxX
                        && player.getLocation().getZ() >= minZ && player.getLocation().getZ() <= maxZ) {
                    if ((player.getLocation().getY() - match.getStandaloneArena().getA().getY()) < 5.0D) {
                        Block block = event.getBlockClicked().getRelative(event.getBlockFace());
                        match.addPlacedBlockLocation(block.getLocation());
                    } else {
                        event.setCancelled(true);
                    }
                } else {
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
    public void onBlockFromTo(BlockFromToEvent event) {
        if (event.getToBlock() == null) {
            return;
        }
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
            if (event.getToBlock().getX() >= minX && event.getToBlock().getZ() >= minZ
                    && event.getToBlock().getX() <= maxX && event.getToBlock().getZ() <= maxZ) {
                UUID matchUUID = this.plugin.getArenaManager().getArenaMatchUUID(arena);
                Match match = this.plugin.getMatchManager().getMatchFromUUID(matchUUID);

                match.addPlacedBlockLocation(event.getToBlock().getLocation());
                break;
            }
        }
    }

    @EventHandler
    public void onWorldChange(PlayerTeleportEvent event){
        Location to = event.getTo();
        Location from = event.getFrom();

        if (to.getWorld() == from.getWorld()) return;
        if (!to.getWorld().getName().equalsIgnoreCase("ss")) return;

        TaskUtil.runLater(() -> to.getWorld().getPlayers().forEach(player -> {
            player.showPlayer(event.getPlayer());
            event.getPlayer().showPlayer(player);
        }), 20L);
    }

    @EventHandler
    public void onCropsTrampling(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL
                && event.getClickedBlock().getType() == Material.SOIL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    final void onLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    final void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    final void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    final void onChunkUnload(ChunkUnloadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    final void onCreatureSpawn(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    final void onWorldLoad(WorldLoadEvent event) {
        event.getWorld().getEntities().clear();
        event.getWorld().setDifficulty(Difficulty.HARD);
        event.getWorld().setStorm(false);
    }
}