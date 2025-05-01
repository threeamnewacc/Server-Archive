package net.badlion.gcheat.listeners;

import net.badlion.gcheat.GCheat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoClickerListener implements Listener {

    public static int CPS_THRESHOLD = 1000000;
    public static int CPS_THRESHOLD_EXPERIMENTAL = 1000000;
    public static int CPS_EXPERIMENTAL = 25;
    public static int CPS_LOW = 35;
    public static int CPS_INVALID = 50;
    public static int CPS_VERY_INVALID = 70;
    public static int CPS_BAN_THIS_NIGGER = 100;
    public static int INSTA_BAN = 500;

    public static Map<Player, Long> lastTickWithPacketSent = new HashMap<>(); // This map keeps track of the last tick we received a packet
    public static Map<Player, Boolean> gotLastTickPacket = new HashMap<>(); // This map is used for if we get 5 clicks in one tick
    public static Map<Player, Long> experimentalHitsSinceLastCheck = new HashMap<>(); // This map is our tracking system

    public static Map<Player, Long> lastTickCheck = new HashMap<>(); // This map checks for our 20 ticks (one sec)
    public static Map<Player, Long> hitsSinceLastCheck = new HashMap<>();

    private static Map<UUID, List<Long>> highDetectionTimes = new HashMap<>();
    private static Map<UUID, List<Long>> fastDetectionTimes = new HashMap<>();
    private static Map<UUID, List<Long>> longDetectionTimes = new HashMap<>();

	private GCheat plugin;

	public AutoClickerListener(GCheat plugin) {
		this.plugin = plugin;
	}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        AutoClickerListener.lastTickCheck.put(event.getPlayer(), 0L);
        AutoClickerListener.hitsSinceLastCheck.put(event.getPlayer(), 0L);
        AutoClickerListener.lastTickWithPacketSent.put(event.getPlayer(), 0L);
        AutoClickerListener.gotLastTickPacket.put(event.getPlayer(), false);
        AutoClickerListener.experimentalHitsSinceLastCheck.put(event.getPlayer(), 0L);

        AutoClickerListener.highDetectionTimes.put(event.getPlayer().getUniqueId(), new ArrayList<Long>());
        AutoClickerListener.fastDetectionTimes.put(event.getPlayer().getUniqueId(), new ArrayList<Long>());
        AutoClickerListener.longDetectionTimes.put(event.getPlayer().getUniqueId(), new ArrayList<Long>());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        AutoClickerListener.lastTickCheck.remove(event.getPlayer());
        AutoClickerListener.hitsSinceLastCheck.remove(event.getPlayer());
        AutoClickerListener.lastTickWithPacketSent.remove(event.getPlayer());
        AutoClickerListener.gotLastTickPacket.remove(event.getPlayer());
        AutoClickerListener.experimentalHitsSinceLastCheck.remove(event.getPlayer());

        AutoClickerListener.highDetectionTimes.remove(event.getPlayer().getUniqueId());
        AutoClickerListener.fastDetectionTimes.remove(event.getPlayer().getUniqueId());
        AutoClickerListener.longDetectionTimes.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
        if (!GCheat.isAntiCheatActivated()) {
            return;
        }

        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
            this.handleExperimentalAutoClicking(event.getPlayer(), "C");
            this.handleAutoClicking(event.getPlayer(), "A");
        }
    }

    @EventHandler
    public void onPlayerAttack(PlayerAttackEvent event) {
        if (!GCheat.isAntiCheatActivated()) {
            return;
        }

        this.handleExperimentalAutoClicking(event.getPlayer(), "D");
        this.handleAutoClicking(event.getPlayer(), "B");
    }

    private void handleAutoClicking(Player player, String type) {
        Long lastTickCheck = AutoClickerListener.lastTickCheck.get(player);
        Long hitsSinceLastCheck = AutoClickerListener.hitsSinceLastCheck.get(player);

        long currentTick = GCheat.plugin.getServer().getCurrentTick();

        // Been 20 ticks, check
        if (lastTickCheck + 20 <= currentTick) {
            /*if (hitsSinceLastCheck >= AutoClickerListener.INSTA_BAN) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + player.getName() + " [GCheat] Autoclicker");
                Bukkit.getPluginManager().callEvent(new GCheatEvent(player, GCheatEvent.Type.AUTO_CLICKER, GCheatEvent.Level.MOD, player.getName() + " is using Autoclicker Type " + type + " VL[" + hitsSinceLastCheck + "]"));
            } if (hitsSinceLastCheck >= AutoClickerListener.CPS_BAN_THIS_NIGGER) {
                Bukkit.getPluginManager().callEvent(new GCheatEvent(player, GCheatEvent.Type.AUTO_CLICKER, GCheatEvent.Level.MOD, player.getName() + " is using Autoclicker Type " + type + " VL[" + hitsSinceLastCheck + "]"));
            } */if (hitsSinceLastCheck >= AutoClickerListener.CPS_VERY_INVALID) {
                Bukkit.getPluginManager().callEvent(new GCheatEvent(player, GCheatEvent.Type.AUTO_CLICKER, GCheatEvent.Level.ADMIN, player.getName() + " is using Autoclicker Type " + type + " VL[" + hitsSinceLastCheck + "]"));
            } else if (hitsSinceLastCheck >= AutoClickerListener.CPS_INVALID) {
                Bukkit.getPluginManager().callEvent(new GCheatEvent(player, GCheatEvent.Type.AUTO_CLICKER, GCheatEvent.Level.ADMIN, player.getName() + " is using Autoclicker Type " + type + " VL[" + hitsSinceLastCheck + "]"));
            } else if (hitsSinceLastCheck >= AutoClickerListener.CPS_LOW) {
                Bukkit.getPluginManager().callEvent(new GCheatEvent(player, GCheatEvent.Type.AUTO_CLICKER, GCheatEvent.Level.ADMIN, player.getName() + " is using Autoclicker Type " + type + " VL[" + hitsSinceLastCheck + "]"));
            } else if (hitsSinceLastCheck >= AutoClickerListener.CPS_THRESHOLD) {
                Bukkit.getPluginManager().callEvent(new GCheatEvent(player, GCheatEvent.Type.AUTO_CLICKER, GCheatEvent.Level.ADMIN, player.getName() + " is using Autoclicker Type E VL[" + hitsSinceLastCheck + "]"));
            }

            AutoClickerListener.lastTickCheck.put(player, currentTick);
            AutoClickerListener.hitsSinceLastCheck.put(player, 0L);
        } else {
            // Just update another hit
            ++hitsSinceLastCheck;
            AutoClickerListener.hitsSinceLastCheck.put(player, hitsSinceLastCheck);
        }
    }

    private void handleExperimentalAutoClicking(Player player, String type) {
        Long lastTickCheck = AutoClickerListener.lastTickCheck.get(player);
        Long lastHitPacketReceivedTick = AutoClickerListener.lastTickWithPacketSent.get(player);
        Boolean gotAlreadyThisTick = AutoClickerListener.gotLastTickPacket.get(player);
        Long hitsSinceLastCheck = AutoClickerListener.experimentalHitsSinceLastCheck.get(player);

        long currentTick = GCheat.plugin.getServer().getCurrentTick();

        // We haven't triggered anything weird yet
        if (lastHitPacketReceivedTick != currentTick) {
            // Reset packet hack check because it's a new tick but still add 1 for violation
            AutoClickerListener.gotLastTickPacket.put(player, false);
            AutoClickerListener.lastTickWithPacketSent.put(player, currentTick);
            AutoClickerListener.experimentalHitsSinceLastCheck.put(player, ++hitsSinceLastCheck);
        } else if (!gotAlreadyThisTick) {
            // Skip 1 packet check
            AutoClickerListener.gotLastTickPacket.put(player, true);
        } else {
            // This is a violation
            AutoClickerListener.experimentalHitsSinceLastCheck.put(player, ++hitsSinceLastCheck);
        }

        if (lastTickCheck + 20 <= currentTick) {
            if (hitsSinceLastCheck >= AutoClickerListener.CPS_EXPERIMENTAL) {
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "ad [AutoClicker " + type + "]: [" + hitsSinceLastCheck + "] " + player.getName() + " has a LOW autoclicker level.");
                this.plugin.logMessage(player, player.getName() + " is using Autoclicker (Experimental) Type " + type + " VL[" + hitsSinceLastCheck + "]");
            } else if (hitsSinceLastCheck >= AutoClickerListener.CPS_THRESHOLD_EXPERIMENTAL) {
                Bukkit.getPluginManager().callEvent(new GCheatEvent(player, GCheatEvent.Type.AUTO_CLICKER, GCheatEvent.Level.ADMIN, player.getName() + " is using Autoclicker Type F VL[" + hitsSinceLastCheck + "]"));
            }

            // Reset # of hits since last check but don't reset anything else
            AutoClickerListener.experimentalHitsSinceLastCheck.put(player, 0L);
        }
    }

    private static Pattern p = Pattern.compile("is using Autoclicker Type [A-Z] VL\\[(\\d+)"); // Stolen from CraftChatMessage

    @EventHandler
    public void onPlayerAutoClicking(GCheatEvent event) {
        if (event.getType() == GCheatEvent.Type.AUTO_CLICKER) {
            String msg = event.getMsg();

            // Manual detection
            if (msg.contains("Type E") || msg.contains("Type F")) {
                return;
            }

            Matcher matcher = p.matcher(msg);
            if (matcher.find()) {
                Integer lvl;
                try {
                    lvl = Integer.parseInt(matcher.group(1));
                } catch (NumberFormatException e) {
                    Bukkit.getLogger().info("Wrong autoclicker int " + matcher.group(1));
                    return;
                }

                //if (lvl > 100) {
                //    GCheat.handleTimeDetection(AutoClickerListener.highDetectionTimes, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 50000, 2, " [GCheat] Autoclicker.");
                //}

                if (lvl > 40) {
                    GCheat.handleTimeDetection(AutoClickerListener.fastDetectionTimes, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 10000, 5, " [GCheat] Unfair Advantage");
                }

                GCheat.handleTimeDetection(AutoClickerListener.longDetectionTimes, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 600000, 30, " [GCheat] Unfair Advantage");
            }
        }
    }

}
