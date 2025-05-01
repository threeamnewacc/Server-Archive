package net.badlion.uhc.tasks;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DonatorSpectatorTask extends BukkitRunnable {

    private static Map<UUID, Location> previousValidLocation = new HashMap<>();
    public static boolean bypassSpectatorLimits = false;

    @Override
    public void run() {
        for (UHCPlayer uhcPlayer : UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.SPEC)) {
            Player player = uhcPlayer.getPlayer();
            if (player == null) {
                continue;
            }

            if (player.isDead()) {
                continue;
            }

            if (uhcPlayer.getState() == UHCPlayer.State.SPEC && player.hasPermission("badlion.uhctrial")) {
                continue;
            }

            if (DonatorSpectatorTask.bypassSpectatorLimits) {
                continue;
            }

            Location prevLocation = previousValidLocation.get(player.getUniqueId());
            if (prevLocation == null) {
                DonatorSpectatorTask.previousValidLocation.put(player.getUniqueId(), player.getLocation());
            } else if (player.getWorld().getEnvironment() != World.Environment.NETHER) {
                if (player.hasPermission("badlion.famous")) {
                    Block block = player.getLocation().getBlock();
                    int y = player.getLocation().getWorld().getHighestBlockYAt(block.getX(), block.getZ());
                    // 250 check because player.getLocation().getBlock() returns bedrock at that level
                    if (player.getLocation().getY() < 250 && y - block.getY() > 10) {
                        player.teleport(prevLocation);
                        player.sendMessage(ChatColor.RED + "Cannot follow players underground as a spectator.");
                    } else {
                        DonatorSpectatorTask.previousValidLocation.put(player.getUniqueId(), player.getLocation());
                    }
                } else if (player.hasPermission("badlion.lion") || player.hasPermission("badlion.lionplus")) {
                    if (player.getLocation().getX() < -500 || player.getLocation().getX() > 500 || player.getLocation().getZ() < -500 || player.getLocation().getZ() > 500) {
                        player.teleport(prevLocation);
                        player.sendMessage(ChatColor.RED + "Lion spectators cannot leave 500 blocks of 0,0");
                    } else {
                        Block block = player.getLocation().getBlock();
                        int y = player.getLocation().getWorld().getHighestBlockYAt(block.getX(), block.getZ());
                        // 250 check because player.getLocation().getBlock() returns bedrock at that level
                        if (player.getLocation().getY() < 250 && y - block.getY() > 10) {
                            player.teleport(prevLocation);
                            player.sendMessage(ChatColor.RED + "Cannot follow players underground as a Lion spectator.");
                        } else {
                            DonatorSpectatorTask.previousValidLocation.put(player.getUniqueId(), player.getLocation());
                        }
                    }
                } else {
                    if (player.getLocation().getX() < -100 || player.getLocation().getX() > 100 || player.getLocation().getZ() < -100 || player.getLocation().getZ() > 100) {
                        player.teleport(prevLocation);
                        player.sendMessage(ChatColor.RED + "Donator+ spectators cannot leave 100 blocks of 0,0");
                    } else {
                        Block block = player.getLocation().getBlock();
                        int y = player.getLocation().getWorld().getHighestBlockYAt(block.getX(), block.getZ());
                        // 250 check because player.getLocation().getBlock() returns bedrock at that level
                        if (player.getLocation().getY() < 250 && y - block.getY() > 10) {
                            player.teleport(prevLocation);
                            player.sendMessage(ChatColor.RED + "Cannot follow players underground as a Donator+ spectator.");
                        } else {
                            DonatorSpectatorTask.previousValidLocation.put(player.getUniqueId(), player.getLocation());
                        }
                    }
                }
            }
        }
    }

}
