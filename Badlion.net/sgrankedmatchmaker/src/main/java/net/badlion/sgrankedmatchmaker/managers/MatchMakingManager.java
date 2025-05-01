package net.badlion.sgrankedmatchmaker.managers;

import net.badlion.gberry.Gberry;
import net.badlion.sgrankedmatchmaker.services.MatchMakingService;
import net.badlion.sgrankedmatchmaker.SGRankedMatchMaker;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MatchMakingManager implements Listener {

    private static MatchMakingService service;
    private static Map<UUID, String> userToServer = new HashMap<>();

    public MatchMakingManager(MatchMakingService service) {
        MatchMakingManager.service = service;
    }

    public static MatchMakingService getService() {
        return MatchMakingManager.service;
    }

    public static void addPlayer(Player player) {
        MatchMakingManager.service.add(player.getUniqueId());
    }

    public static void storePlayerServer(UUID uuid, String server) {
        Gberry.log("SGRMM2", "Storing uuid " + uuid + " in server " + server);
        MatchMakingManager.userToServer.put(uuid, server);
    }

    public static String getPlayerServer(UUID uuid) {
        return MatchMakingManager.userToServer.get(uuid);
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (MatchMakingManager.userToServer.containsKey(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(ChatColor.BLUE + "========================================================");
            event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "You are in a match, sending you to the correct server.");
            event.getPlayer().sendMessage(ChatColor.BLUE + "========================================================");

            new BukkitRunnable() {
                @Override
                public void run() {
                    SGRankedMatchMaker.getInstance().sendPlayerToServer(event.getPlayer(), MatchMakingManager.userToServer.get(event.getPlayer().getUniqueId()));
                }

            }.runTaskLater(SGRankedMatchMaker.getInstance(), 10);
        } else {
            MatchMakingManager.addPlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!MatchMakingManager.userToServer.containsKey(event.getPlayer().getUniqueId())) {
            MatchMakingManager.service.remove(event.getPlayer().getUniqueId());
        }
    }

    public static void handleMap(Map<String, String> players) {
        Map<UUID, String> newUserToServer = new HashMap<>();
        for (Map.Entry<String, String> entry : players.entrySet()) {
            newUserToServer.put(UUID.fromString(entry.getKey()), entry.getValue());
        }

        MatchMakingManager.userToServer = newUserToServer;
    }

    public static void removeDeadPlayers(Collection<String> uuidStrings) {
        Set<UUID> uuidSet = MatchMakingManager.userToServer.keySet();
        for (String uuidString : uuidStrings) {
            UUID uuid = UUID.fromString(uuidString);
            uuidSet.remove(uuid);
            MatchMakingManager.userToServer.remove(uuid);

            Gberry.log("SGRMM2", "Removing dead player " + uuid);
        }
    }

    public static boolean isInMatch(UUID uuid) {
        return MatchMakingManager.userToServer.containsKey(uuid);
    }

    public static Set<UUID> getAllAlivePlayers() {
        return MatchMakingManager.userToServer.keySet();
    }
}
