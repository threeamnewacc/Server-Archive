package net.badlion.gfactions.events.supermine;

import net.badlion.gberry.Gberry;
import net.badlion.gfactions.GFactions;
import net.badlion.gberry.utils.BukkitUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SuperMine implements Listener {

    private Location minLocation;
    private Location maxLocation;
    private Random random = new Random();
    private boolean isActive = false;
    private int numOfResets = 0;

    private List<Location> cobbleToReplace = new ArrayList<>();

    public SuperMine() {
        if (GFactions.plugin.getSuperMine() != null) {
            throw new RuntimeException("SuperMine already running");
        }

        GFactions.plugin.setSuperMine(this);

        this.minLocation = GFactions.plugin.getSuperMineConfig().getMinLocation();
        this.maxLocation = GFactions.plugin.getSuperMineConfig().getMaxLocation();

        Gberry.broadcastMessage(Gberry.convertChatColors(GFactions.plugin.getSuperMineConfig().getTenMinuteWarning()), false);

        // 5 min warning
        new BukkitRunnable() {
            public void run() {
                Gberry.broadcastMessage(Gberry.convertChatColors(GFactions.plugin.getSuperMineConfig().getFiveMinuteWarning()), false);
            }
        }.runTaskLater(GFactions.plugin, 20 * 5 * 60);

        // 2 min warning
        new BukkitRunnable() {
            public void run() {
                Gberry.broadcastMessage(Gberry.convertChatColors(GFactions.plugin.getSuperMineConfig().getTwoMinuteWarning()), false);
            }
        }.runTaskLater(GFactions.plugin, 20 * 8 * 60);

        // start
        new BukkitRunnable() {
            public void run() {
                SuperMine.this.createBlocks();
                SuperMine.this.kickOffBlockReplacementTask();

                GFactions.plugin.getServer().getPluginManager().registerEvents(SuperMine.this, GFactions.plugin);

                // Reset the mine every so often
                int resetTimeInSeconds = GFactions.plugin.getSuperMineConfig().getResetTimeInSeconds();
                BukkitUtil.runTaskTimer(new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!SuperMine.this.isActive) {
                            this.cancel();
                            return;
                        }

                        if (++SuperMine.this.numOfResets == GFactions.plugin.getSuperMineConfig().getNumOfResets()) {
                            SuperMine.this.stop();
                            this.cancel();
                            return;
                        }

                        Gberry.broadcastMessage(Gberry.convertChatColors(GFactions.plugin.getSuperMineConfig().getRefreshMsg()), false);

                        SuperMine.this.createBlocks();
                    }
                }, resetTimeInSeconds, resetTimeInSeconds);

                SuperMine.this.isActive = true;

                Gberry.broadcastMessage(Gberry.convertChatColors(GFactions.plugin.getSuperMineConfig().getStartMsg()), false);
            }
        }.runTaskLater(GFactions.plugin, 20 * 10 * 60);
    }

    public void stop() {
        this.isActive = false;
        BlockBreakEvent.getHandlerList().unregister(this);
        this.resetBlocks();
        GFactions.plugin.setSuperMine(null);

        Gberry.broadcastMessage(Gberry.convertChatColors(GFactions.plugin.getSuperMineConfig().getEndMsg()), false);
    }

    @EventHandler(priority=EventPriority.LAST)
    public void onOreBroken(BlockBreakEvent event) {
        if (Gberry.isLocationInBetween(this.minLocation, this.maxLocation, event.getBlock().getLocation())) {
            Block block = event.getBlock();
            if (block.getType() == Material.COAL_ORE || block.getType() == Material.IRON_ORE || block.getType() == Material.GOLD_ORE
                    || block.getType() == Material.DIAMOND_ORE || block.getType() == Material.EMERALD_ORE || block.getType() == Material.REDSTONE_ORE
                    || block.getType() == Material.LAPIS_ORE) {
                this.cobbleToReplace.add(block.getLocation());
                event.setCancelled(false);
            } else {
                event.setCancelled(true);
            }
        }
    }

    public void createBlocks() {
        final List<Location> locations = GFactions.plugin.getSuperMineConfig().getCobbleLocations();
        final int numOfBlocksPerTick = GFactions.plugin.getSuperMineConfig().getBlocksPerTick();

        BukkitUtil.runTaskTimer(new BukkitRunnable() {

            private int i = 0;

            @Override
            public void run() {
                if (i == locations.size()) {
                    this.cancel();
                    return;
                }

                for (int j = i; j < numOfBlocksPerTick; i++, j++) {
                    if (j == locations.size()) {
                        this.cancel();
                        return;
                    }

                    SuperMine.this.createOreBlock(locations.get(j));
                }
            }
        }, 1, 1);
    }

    public void resetBlocks() {
        final List<Location> locations = GFactions.plugin.getSuperMineConfig().getCobbleLocations();
        final int numOfBlocksPerTick = GFactions.plugin.getSuperMineConfig().getBlocksPerTick();

        BukkitUtil.runTaskTimer(new BukkitRunnable() {

            private int i = 0;

            @Override
            public void run() {
                if (i == locations.size()) {
                    this.cancel();
                    return;
                }

                for (int j = i; j < numOfBlocksPerTick; i++, j++) {
                    if (j == locations.size()) {
                        this.cancel();
                        return;
                    }

                    SuperMine.this.createOreBlock(locations.get(j));
                }
            }
        }, 1, 1);
    }

    private void createOreBlock(Location location) {
        int total = GFactions.plugin.getSuperMineConfig().getCoalChance() + GFactions.plugin.getSuperMineConfig().getDiamondChance()
                + GFactions.plugin.getSuperMineConfig().getEmeraldChance() + GFactions.plugin.getSuperMineConfig().getGoldChance()
                + GFactions.plugin.getSuperMineConfig().getIronChance() + GFactions.plugin.getSuperMineConfig().getLapisChance()
                + GFactions.plugin.getSuperMineConfig().getRedStoneChance();
        int rand = this.random.nextInt(total);

        int current = GFactions.plugin.getSuperMineConfig().getCoalChance();
        if (rand < current) {
            location.getBlock().setType(Material.COAL);
            return;
        }

        current += GFactions.plugin.getSuperMineConfig().getIronChance();
        if (rand < current) {
            location.getBlock().setType(Material.IRON_ORE);
            return;
        }

        current += GFactions.plugin.getSuperMineConfig().getGoldChance();
        if (rand < current) {
            location.getBlock().setType(Material.GOLD_ORE);
            return;
        }

        current += GFactions.plugin.getSuperMineConfig().getDiamondChance();
        if (rand < current) {
            location.getBlock().setType(Material.DIAMOND_ORE);
            return;
        }

        current += GFactions.plugin.getSuperMineConfig().getEmeraldChance();
        if (rand < current) {
            location.getBlock().setType(Material.EMERALD_ORE);
            return;
        }

        current += GFactions.plugin.getSuperMineConfig().getLapisChance();
        if (rand < current) {
            location.getBlock().setType(Material.LAPIS_ORE);
            return;
        }

        location.getBlock().setType(Material.REDSTONE_ORE);
    }

    public void kickOffBlockReplacementTask() {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (!SuperMine.this.isActive) {
                    this.cancel();
                    return;
                }

                for (Location location : SuperMine.this.cobbleToReplace) {
                    location.getBlock().setType(Material.COBBLESTONE);
                }
            }

        }.runTaskTimer(GFactions.plugin, 2, 2);
    }

}
