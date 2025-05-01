package net.badlion.gfactions.events.scavenge;

import net.badlion.gberry.Gberry;
import net.badlion.gfactions.GFactions;
import net.badlion.gguard.ProtectedRegion;
import net.badlion.smellyloot.SmellyLoot;
import net.badlion.smellyloot.managers.LootManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

public class Scavenge implements Listener {

    private Set<Location> chests = new HashSet<>();
    private BukkitTask endTask = null;

    public Scavenge() {
        if (GFactions.plugin.getScavenge() != null) {
            throw new RuntimeException("Scavenge already running");
        }

        GFactions.plugin.setScavenge(this);

        Gberry.broadcastMessage(Gberry.convertChatColors(GFactions.plugin.getScavengeConfig().getTenMinuteWarning()), false);

        // 5 min warning
        new BukkitRunnable() {
            public void run() {
                Gberry.broadcastMessage(Gberry.convertChatColors(GFactions.plugin.getScavengeConfig().getFiveMinuteWarning()), false);
            }
        }.runTaskLater(GFactions.plugin, 20 * 5 * 60);

        // 2 min warning
        new BukkitRunnable() {
            public void run() {
                Gberry.broadcastMessage(Gberry.convertChatColors(GFactions.plugin.getScavengeConfig().getTwoMinuteWarning()), false);
            }
        }.runTaskLater(GFactions.plugin, 20 * 8 * 60);

        // Start
        new BukkitRunnable() {
            public void run() {
                int x, z;
                Block block;
                boolean cont = true;

                for (int i = 0; i < GFactions.plugin.getScavengeConfig().getNumOfScavengeChests(); i++) {
                    // Keep scanning randomly the war zone until we find a spot not located in an event zone
                    do {
                        x = GFactions.plugin.generateRandomInt(GFactions.plugin.getWarZoneMinX(), GFactions.plugin.getWarZoneMaxX());
                        z = GFactions.plugin.generateRandomInt(GFactions.plugin.getWarZoneMinZ(), GFactions.plugin.getWarZoneMaxZ());

                        block = GFactions.plugin.getServer().getWorld("world").getBlockAt(x, GFactions.plugin.getServer().getWorld("world").getHighestBlockYAt(x, z), z);

                        // Find something in the war zone but not in an event zone
                        ProtectedRegion region = GFactions.plugin.getgGuardPlugin().getProtectedRegion(block.getLocation(), GFactions.plugin.getgGuardPlugin().getProtectedRegions());
                        if (region != null && region.getRegionName().equals("warzone") && !Scavenge.this.chests.contains(block.getLocation())) {
                            cont = false;
                        }
                    } while (cont);

                    // Drop Loot and keep track of it
                    LootManager.dropEventLootChest("scavenge", block.getLocation());
                    Scavenge.this.chests.add(block.getLocation());
                }

                // Compass for finding chests
                new BukkitRunnable() {
                    public void run() {
                        if (Scavenge.this.chests.size() == 0) {
                            this.cancel();
                            return;
                        }

                        // Point them to the nearest chests
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.getItemInHand().getType() == Material.COMPASS) {
                                if (p.getLocation().getBlockX() >= GFactions.plugin.getWarZoneMinX()
                                    && p.getLocation().getBlockX() <= GFactions.plugin.getWarZoneMaxX()
                                    && p.getLocation().getBlockZ() >= GFactions.plugin.getWarZoneMinZ()
                                    && p.getLocation().getBlockZ() <= GFactions.plugin.getWarZoneMaxX()) {

                                    double distance = 99999999;
                                    Location location = null;
                                    for (Location l : Scavenge.this.chests) {
                                        double d = p.getLocation().distance(l);
                                        if (d < distance) {
                                            distance = d;
                                            location = l;
                                        }
                                    }

                                    if (location != null) {
                                        p.setCompassTarget(location);
                                    }
                                }
                            }
                        }
                    }
                }.runTaskTimer(GFactions.plugin, 20, 20);

                Scavenge.this.endTask = new BukkitRunnable() {

                    @Override
                    public void run() {
                        Scavenge.this.endTask = null;
                        Scavenge.this.stop(false);
                    }

                }.runTaskLater(GFactions.plugin, 20 * GFactions.plugin.getScavengeConfig().getNumOfSecondsTillDespawn());

                GFactions.plugin.getServer().getPluginManager().registerEvents(Scavenge.this, GFactions.plugin);

                Gberry.broadcastMessage(Gberry.convertChatColors(GFactions.plugin.getScavengeConfig().getScavengeStart()), false);
            }
        }.runTaskLater(GFactions.plugin, 20 * 10 * 60);
    }

    public void stop(boolean foundAllChests) {
        if (this.endTask != null) {
            this.endTask.cancel();
        }

        if (foundAllChests) {
            Gberry.broadcastMessage(Gberry.convertChatColors(GFactions.plugin.getScavengeConfig().getFoundAllChests()), false);
        } else {
            Gberry.broadcastMessage(Gberry.convertChatColors(GFactions.plugin.getScavengeConfig().getExpiredChests()), false);
        }

        this.chests.clear();

        PlayerInteractEvent.getHandlerList().unregister(this);
        GFactions.plugin.setScavenge(null);
    }

    @EventHandler
    public void onChestFound(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (this.chests.contains(event.getClickedBlock().getLocation())) {
                // Drop items from chest
                SmellyLoot.dropItemsFromChest(event);

                this.chests.remove(event.getClickedBlock().getLocation());

                if (this.chests.size() == 0) {
                    this.stop(true);
                }
            }
        }
    }

	public Set<Location> getChests() {
		return chests;
	}

}
