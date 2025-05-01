package net.badlion.uhc.listeners;

import net.badlion.combattag.CombatTagPlugin;
import net.badlion.combattag.LoggerNPC;
import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.Pair;
import net.badlion.ministats.MiniStats;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.UHCTeam;
import net.badlion.uhc.events.GameStartEvent;
import net.badlion.uhc.events.ServerStateChangeEvent;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.managers.UHCTeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MiniUHCListener implements Listener {

    public static String API_KEY = "RALBAv4JWgqzFn24535XJ2q8mSGhWYnY";
    public static String API_URL = "http://127.0.0.1:20090/";
    public static int FINAL_SHRINK = 100;
    public static int WATER_LEVEL = 62;
    public static int MAX_ABOVE = 5;
    public static int GAME_TEAM_START_IN_SECONDS = 120;
    public static int GAME_FFA_START_IN_SECONDS = 60;
    public static int MIN_PLAYERS_TO_START = 24;
    public static int MAX_PLAYERS = 32;

    private static boolean initialized = false;
    private static Map<Pair, Integer> maxAllowedYValue = new HashMap<>();
    private static UUID serverUUID;
    private static List<String> gamemodes = new ArrayList<>();
    private static int teamSize = 0;
    private static boolean minHit = false;
    private static boolean allowConnections = true;

    public MiniUHCListener() {
        MiniUHCListener.serverUUID = UUID.randomUUID();

        // Go through and find all of the valid locations in 100x100 to prevent skybasing
        World world = BadlionUHC.getInstance().getServer().getWorld(BadlionUHC.UHCWORLD_NAME);
        for (int x = -MiniUHCListener.FINAL_SHRINK; x < MiniUHCListener.FINAL_SHRINK; x++) {
            for (int z = -MiniUHCListener.FINAL_SHRINK; z < MiniUHCListener.FINAL_SHRINK; z++) {
                Block block = world.getHighestBlockAt(x, z);
                if (block.getY() < MiniUHCListener.WATER_LEVEL) {
                    MiniUHCListener.maxAllowedYValue.put(Pair.of(x, z), this.getRavineHighestBlock(world, x, z));
                } else {
                    MiniUHCListener.maxAllowedYValue.put(Pair.of(x, z), block.getY() + MiniUHCListener.MAX_ABOVE);
                }
            }
        }

        new BukkitRunnable() {
            public void run() {
                MiniUHCListener.sendKeepAlive();
            }
        }.runTaskTimer(BadlionUHC.getInstance(), 100, 100);

        // Nice message
        new BukkitRunnable() {
            public void run() {
                if (MiniUHCListener.minHit) {
                    this.cancel();
                    return;
                }

                Gberry.broadcastMessage(BadlionUHC.PREFIX + ChatColor.DARK_GREEN + "A MiniUHC match requires " + ChatColor.GOLD + MiniUHCListener.MIN_PLAYERS_TO_START + ChatColor.DARK_GREEN + " players to begin.");
            }
        }.runTaskTimer(BadlionUHC.getInstance(), 20 * 30, 20 * 30);
    }

    private int getRavineHighestBlock(World world, int x, int z) {
        // Check each 4 directions until we find something
        for (int i = 1; i < 30; i++) {
            int res = this.getFromMapOrWorld(world, x - i, z);
            if (res != -1) {
                return res;
            }

            res = this.getFromMapOrWorld(world, x + i, z);
            if (res != -1) {
                return res;
            }

            res = this.getFromMapOrWorld(world, x, z - i);
            if (res != -1) {
                return res;
            }

            res = this.getFromMapOrWorld(world, x, z - i);
            if (res != -1) {
                return res;
            }
        }

        // This should never get hit
        return WATER_LEVEL + 8;
    }

    private int getFromMapOrWorld(World world, int x, int z) {
        Pair pair = Pair.of(x, z);
        if (MiniUHCListener.maxAllowedYValue.containsKey(pair)) {
            return MiniUHCListener.maxAllowedYValue.get(pair);
        }

        int y = world.getHighestBlockYAt(x, z);
        if (y >= WATER_LEVEL) {
            return y;
        }

        return -1;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        if (event.getBlock().getX() < MiniUHCListener.FINAL_SHRINK && event.getBlock().getX() >= -MiniUHCListener.FINAL_SHRINK
                && event.getBlock().getZ() < MiniUHCListener.FINAL_SHRINK && event.getBlock().getZ() >= -MiniUHCListener.FINAL_SHRINK) {
            if (event.getBlock().getWorld().getName().equals(BadlionUHC.UHCWORLD_NAME)) {
                int y = MiniUHCListener.maxAllowedYValue.get(Pair.of(event.getBlock().getX(), event.getBlock().getZ()));
                if (event.getBlock().getY() > y) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "You cannot place this block here. No skybasing allowed.");
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.LAST)
    public void onFullServerJoin(PlayerLoginEvent event) {
        if (!MiniUHCListener.allowConnections && !MiniUHCListener.minHit) {
            event.setKickMessage("A Hosted UHC is starting soon. MiniUHC is closed.");
            event.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
            return;
        }

        if (MiniUHCListener.MAX_PLAYERS == Bukkit.getServer().getOnlinePlayers().size()
                    && BadlionUHC.getInstance().getState().ordinal() < BadlionUHC.BadlionUHCState.COUNTDOWN.ordinal()) {
            if (event.getPlayer().hasPermission("badlion.donatorplus")) {
                Player lastPlayerJoined = this.getPlayerWithoutPermission("badlion.donator");

                if (lastPlayerJoined != null) {
                    lastPlayerJoined.kickPlayer("You have been removed to make room for a Donator+ to join. Donate to secure a slot.");
                    event.setResult(PlayerLoginEvent.Result.ALLOWED);
                } else {
                    lastPlayerJoined = this.getPlayerWithoutPermission("badlion.donatorplus");
                    if (lastPlayerJoined != null) {
                        lastPlayerJoined.kickPlayer("You have been removed to make room for a Donator+ to join. Become a Donator+ to secure a slot.");
                        event.setResult(PlayerLoginEvent.Result.ALLOWED);
                    } else {
                        lastPlayerJoined = this.getPlayerWithoutPermission("badlion.lion");
                        if (lastPlayerJoined != null) {
                            lastPlayerJoined.kickPlayer("You have been removed to make room for a Lion to join. Become a Lion to secure a slot.");
                            event.setResult(PlayerLoginEvent.Result.ALLOWED);
                        } else {
                            event.setKickMessage("No lesser ranks found to remove. Unable to join, server full.");
                        }
                    }
                }
            } else if (event.getPlayer().hasPermission("badlion.donator") || BadlionUHC.getInstance().getWhitelist().contains(event.getPlayer().getName().toLowerCase())) {
                Player lastPlayerJoined = this.getPlayerWithoutPermission("badlion.donator");

                if (lastPlayerJoined != null) {
                    lastPlayerJoined.kickPlayer("You have been removed to make room for a Donator to join. Donate to secure a slot.");
                    event.setResult(PlayerLoginEvent.Result.ALLOWED);
                } else {
                    event.setKickMessage("No lesser ranks found to remove. Unable to join, server full.");
                }
            }
        }

        if (BadlionUHC.getInstance().getServer().getOnlinePlayers().size() < MiniUHCListener.MAX_PLAYERS && BadlionUHC.getInstance().getState().ordinal() < BadlionUHC.BadlionUHCState.COUNTDOWN.ordinal()) {
            event.setResult(PlayerLoginEvent.Result.ALLOWED);
        }

        // Whitelisted people
        if (BadlionUHC.getInstance().getWhitelist().contains(event.getPlayer().getName().toLowerCase()) && BadlionUHC.getInstance().getState().ordinal() <= BadlionUHC.BadlionUHCState.COUNTDOWN.ordinal()) {
            event.setResult(PlayerLoginEvent.Result.ALLOWED);
        }

        // Allow spectators
        if (BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.STARTED && event.getPlayer().hasPermission("badlion.donatorplus")) {
            event.setResult(PlayerLoginEvent.Result.ALLOWED);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!MiniUHCListener.minHit) {
            if (MiniUHCListener.MIN_PLAYERS_TO_START == Bukkit.getServer().getOnlinePlayers().size()) {
                MiniUHCListener.minHit = true;

                if (BadlionUHC.getInstance().getGameType() == UHCTeam.GameType.TEAM) {
                    Gberry.broadcastMessage(BadlionUHC.PREFIX + ChatColor.DARK_GREEN + "Match Starting in 2 minutes. Form your teams using /team");
                } else {
                    Gberry.broadcastMessage(BadlionUHC.PREFIX + ChatColor.DARK_GREEN + "Match Starting in 1 minute.");
                }

                Gberry.broadcastMessage(BadlionUHC.PREFIX + ChatColor.DARK_GREEN + "You will be whitelisted when the game starts counting down.");

                new BukkitRunnable() {
                    public void run() {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "uhc start");
                    }
                }.runTaskLater(BadlionUHC.getInstance(), 20 * ((BadlionUHC.getInstance().getGameType() == UHCTeam.GameType.TEAM ? MiniUHCListener.GAME_TEAM_START_IN_SECONDS : MiniUHCListener.GAME_FFA_START_IN_SECONDS)));
            }
        }

    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        new BukkitRunnable() {
            public void run() {
                if (BadlionUHC.getInstance().getEndTime() != -1) {
                    this.cancel();
                    return;
                }

                List<UHCTeam> uhcTeamsTmp = UHCTeamManager.getAllAlivePlayingTeams();
                List<UHCTeam> uhcTeams = new ArrayList<>(uhcTeamsTmp);
                Collections.shuffle(uhcTeams);

                // First kill any and all loggers
                for (LoggerNPC loggerNPC : CombatTagPlugin.getInstance().getAllLoggers()) {
                    loggerNPC.remove(LoggerNPC.REMOVE_REASON.DEATH);

                    if (BadlionUHC.getInstance().checkForWinners()) {
                        this.cancel();
                        return;
                    }
                }

                for (UUID uuid : uhcTeams.get(0).getUuids()) {
                    Player p = BadlionUHC.getInstance().getServer().getPlayer(uuid);
                    if (p != null) {
                        p.damage(4.0);
                        p.sendMessage(ChatColor.RED + "Took too long to kill your opponents. BAM");
                    }

                    if (BadlionUHC.getInstance().checkForWinners()) {
                        this.cancel();
                        return;
                    }
                }
            }
        }.runTaskTimer(BadlionUHC.getInstance(), (20 * 3600) + (20 * 60 * 5), 100);
    }

    @EventHandler
    public void onOpenServer(ServerStateChangeEvent event) {
        if (event.getNewState() == BadlionUHC.BadlionUHCState.PRE_START) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wl off");
        }
    }

    private Player getPlayerWithoutPermission(String permission) {
        ConcurrentLinkedQueue<UHCPlayer> uhcPlayers = UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.PLAYER);

        List<Player> kickables = new ArrayList<>();

        for (UHCPlayer uhcPlayer : uhcPlayers) {
            Player player = uhcPlayer.getPlayer();
            if (!player.hasPermission(permission) && !BadlionUHC.getInstance().getWhitelist().contains(player.getName().toLowerCase()) && uhcPlayer.getTeam().getSize() < 2) {
                kickables.add(player);
            }
        }

        if (!kickables.isEmpty()) {
            return kickables.get(kickables.size() - 1);
        }

        return null;
    }

    public static void sendKeepAlive() {
        final JSONObject request = new JSONObject();
        request.put("server_name", Gberry.serverName);
        request.put("state", BadlionUHC.getInstance().getState().name());
        request.put("gamemodes", MiniUHCListener.gamemodes);
        request.put("teamsize", MiniUHCListener.teamSize);
        request.put("in_countdown", MiniUHCListener.minHit);
        request.put("player_count", BadlionUHC.getInstance().getServer().getOnlinePlayers().size());

        new BukkitRunnable() {
            public void run() {
                JSONObject response = null;

                try {
                    response = HTTPCommon.executePUTRequest(MiniUHCListener.API_URL + "KeepAlive/" + MiniUHCListener.serverUUID.toString() + "/" + MiniUHCListener.API_KEY, request);
                } catch (HTTPRequestFailException e) {
                    Bukkit.getLogger().info("Failed to get startup information with error code " + e.getResponseCode());

                    Bukkit.getScheduler().runTask(BadlionUHC.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
                        }
                    });
                }

                if (response != null && response.containsKey("teamsize")) {
                    MiniUHCListener.teamSize = (int) (long) MiniStats.getLong(response.get("teamsize"));
                    MiniUHCListener.gamemodes = (List<String>) response.get("gamemodes");

                    final boolean wasLocked = !MiniUHCListener.allowConnections;

                    if (response.containsKey("locked")) {
                        MiniUHCListener.allowConnections = !((boolean) response.get("locked"));
                    } else {
                        MiniUHCListener.allowConnections = true; // Just in case
                    }

                    if (!wasLocked && !MiniUHCListener.allowConnections && !MiniUHCListener.minHit) {
                        new BukkitRunnable() {
                            public void run() {
                                for (Player player : BadlionUHC.getInstance().getServer().getOnlinePlayers()) {
                                    player.kickPlayer("A Hosted UHC is going to start soon. Game cancelled.");
                                }
                            }
                        }.runTask(BadlionUHC.getInstance());
                    }

                    if (!MiniUHCListener.initialized) {
                        MiniUHCListener.initialized = true;
                        new BukkitRunnable() {
                            public void run() {
                                if (MiniUHCListener.teamSize == 1) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "uhc ffa");
                                } else {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "uhc teams");
                                }

                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "uhc gamemode miniuhc " + MiniUHCListener.MAX_PLAYERS + " 500");

                                if (MiniUHCListener.teamSize > 1) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "uhc config teamsize " + MiniUHCListener.teamSize);
                                }

                                for (String gameModeString : MiniUHCListener.gamemodes) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "uhc addgamemode " + gameModeString);
                                }
                            }
                        }.runTask(BadlionUHC.getInstance());
                    }

                }
            }
        }.runTaskAsynchronously(BadlionUHC.getInstance());
    }
}
