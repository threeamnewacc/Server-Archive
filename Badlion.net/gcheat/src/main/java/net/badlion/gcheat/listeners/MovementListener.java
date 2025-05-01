package net.badlion.gcheat.listeners;

import net.badlion.gcheat.GCheat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.GCheatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MovementListener implements Listener {

    private static Map<UUID, List<Long>> lastSpeedHack = new HashMap<>();
    private static Map<UUID, List<Long>> lastHighSpeedHack = new HashMap<>();
    private static Map<UUID, List<Long>> lastHoverHack = new HashMap<>();
    private static Map<UUID, List<Long>> lastSpeedTypeD = new HashMap<>();
    private static Map<UUID, List<Long>> lastFlyTypeD = new HashMap<>();
    private static Map<UUID, List<Long>> lastMinijumps = new HashMap<>();

    private static Map<UUID, Location> teleportMap = new HashMap<>();

    private static Pattern p = Pattern.compile("VL ([0-9]*\\.[0-9]+|[0-9]+)");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        MovementListener.lastSpeedHack.put(event.getPlayer().getUniqueId(), new ArrayList<Long>());
        MovementListener.lastHighSpeedHack.put(event.getPlayer().getUniqueId(), new ArrayList<Long>());
        MovementListener.lastHoverHack.put(event.getPlayer().getUniqueId(), new ArrayList<Long>());
        MovementListener.teleportMap.put(event.getPlayer().getUniqueId(), null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        MovementListener.lastSpeedHack.remove(event.getPlayer().getUniqueId());
        MovementListener.lastHighSpeedHack.remove(event.getPlayer().getUniqueId());
        MovementListener.lastHoverHack.remove(event.getPlayer().getUniqueId());
        MovementListener.teleportMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority= EventPriority.LAST, ignoreCancelled=true)
    public void onTeleport(PlayerTeleportEvent event) {
        MovementListener.teleportMap.put(event.getPlayer().getUniqueId(), event.getTo());
    }

    @EventHandler
    public void onPlayerSpeedHacking(GCheatEvent event) {
        if (event.getType() == GCheatEvent.Type.SPEED) {
            Location tpLocation = MovementListener.teleportMap.get(event.getPlayer().getUniqueId());
            if (tpLocation != null) {
                // False positive
                if (MovementListener.getSquareRoot(tpLocation, event.getPlayer().getLocation()) < 5) {
                    return;
                }
            }

            if (!event.getMsg().contains("Type C")) {
                return;
            }

            String msg = event.getMsg();

            Matcher matcher = p.matcher(msg);
            if (matcher.find()) {
                Double lvl;
                try {
                    lvl = Double.parseDouble(matcher.group(1));
                } catch (NumberFormatException e) {
                    Bukkit.getLogger().info("Wrong Timer int " + matcher.group(1));
                    return;
                }

                if (lvl > 1.5) {
                    GCheat.handleTimeDetection(MovementListener.lastHighSpeedHack, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 300000, 3);
                } else {
                    GCheat.handleTimeDetection(MovementListener.lastSpeedHack, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 300000, 15);
                }
            }
        }
    }

    @EventHandler
    public void onLastHoveHacking(GCheatEvent event) {
        if (event.getType() == GCheatEvent.Type.HOVER) {
            Location tpLocation = MovementListener.teleportMap.get(event.getPlayer().getUniqueId());
            if (tpLocation != null) {
                // False positive
                if (MovementListener.getSquareRoot(tpLocation, event.getPlayer().getLocation()) < 5) {
                    return;
                }
            }

            //if (event.getLevel() == GCheatEvent.Level.ADMIN) {
            //    return;
            //}

            GCheat.handleTimeDetection(MovementListener.lastHoverHack, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 300000, 5);
        }
    }

    @EventHandler
    public void onSpeedFlyTypeD(GCheatEvent event) {
        if (event.getType() == GCheatEvent.Type.SPEED) {
            if (event.getMsg().contains("Type D")) {
                GCheat.handleTimeDetection(MovementListener.lastSpeedTypeD, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 10000, 8);
            }
        } else if (event.getType() == GCheatEvent.Type.FLY) {
            if (event.getMsg().contains("Type D")) {
                GCheat.handleTimeDetection(MovementListener.lastFlyTypeD, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 10000, 8);
            }
        }
    }

    @EventHandler
    public void onMinijumps(GCheatEvent event) {
        if (event.getType() == GCheatEvent.Type.CRIT) {
            if (event.getMsg().contains("uses minijumps")) {
                GCheat.handleTimeDetection(MovementListener.lastMinijumps, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 30000, 10);
            }
        }
    }

    public static double getSquareRoot(Location location, Location location2) {
        return Math.sqrt(Math.pow(location.getX() - location2.getX(), 2) + Math.pow(location.getZ() - location2.getZ(), 2));
    }

}
