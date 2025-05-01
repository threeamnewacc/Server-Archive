package net.badlion.skywarslobby.tasks;

import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.skywarslobby.SkyWarsLobby;
import net.badlion.skywarslobby.inventories.GameQueueInventory;
import net.badlion.skywarslobby.listeners.LobbyListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

public class CheckQueueStatusTask extends BukkitRunnable {

    public static int ffaClassic = 0;
    public static int ffaOp = 0;

    public void run() {
        try {
            JSONObject jsonObject = HTTPCommon.executeGETRequest("http://127.0.0.1:9014/GetQueueCount/ffa_classic/4jzyuUGb5AQUvVGLeUpx11ih4vGFF");

            if (jsonObject != null) {
                if (jsonObject.containsKey("error")) {
                    Bukkit.getLogger().info("Error " + jsonObject.get("error"));
                } else if (jsonObject.containsKey("success")) {
                    CheckQueueStatusTask.ffaClassic = Integer.parseInt("" + jsonObject.get("success"));

                    new BukkitRunnable() {
                        public void run() {
                            Sign sign = (Sign) LobbyListener.ffaClassicLocation.getBlock().getState();
                            sign.setLine(0, ChatColor.AQUA + "[Normal]");
                            sign.setLine(1, ChatColor.AQUA + "[FFA]");
                            sign.setLine(2, ChatColor.DARK_GREEN + "" + CheckQueueStatusTask.ffaClassic + " in queue");
                            sign.setLine(3, ChatColor.GOLD + "Click to join");
                            sign.update();

                            GameQueueInventory.updateFFAClassicQueueCount();
                        }
                    }.runTask(SkyWarsLobby.getInstance());
                } else {
                    Bukkit.getLogger().info("Unknown error 1");
                }
            } else {
                Bukkit.getLogger().info("Unknown error 2");
            }

            jsonObject = HTTPCommon.executeGETRequest("http://127.0.0.1:9014/GetQueueCount/ffa_op/4jzyuUGb5AQUvVGLeUpx11ih4vGFF");

            if (jsonObject != null) {
                if (jsonObject.containsKey("error")) {
                    Bukkit.getLogger().info("Error " + jsonObject.get("error"));
                } else if (jsonObject.containsKey("success")) {
                    CheckQueueStatusTask.ffaOp = Integer.parseInt("" + jsonObject.get("success"));

                    new BukkitRunnable() {
                        public void run() {
                            Sign sign = (Sign) LobbyListener.ffaOPLocation.getBlock().getState();
                            sign.setLine(0, ChatColor.AQUA + "[OP]");
                            sign.setLine(1, ChatColor.AQUA + "[FFA]");
                            sign.setLine(2, ChatColor.DARK_GREEN + "" + CheckQueueStatusTask.ffaOp + " in queue");
                            sign.setLine(3, ChatColor.GOLD + "Click to join");
                            sign.update();

                            GameQueueInventory.updateFFAOpQueueCount();
                        }
                    }.runTask(SkyWarsLobby.getInstance());
                } else {
                    Bukkit.getLogger().info("Unknown error 1");
                }
            } else {
                Bukkit.getLogger().info("Unknown error 2");
            }
        } catch (HTTPRequestFailException e) {
            Bukkit.getLogger().info("Error when fetching queue " + e.getResponseCode() + ": " + e.getResponse());
        }
    }

    // In here to avoid import issues
    public static void flushPlayers() {
        // Force the API to be updated
        for (Player pl : SkyWarsLobby.getInstance().getServer().getOnlinePlayers()) {
            try {
                HTTPCommon.executePOSTRequest("http://127.0.0.1:9014/RemoveFromQueue/" + pl.getUniqueId().toString() + "/" + pl.getName() + "/4jzyuUGb5AQUvVGLeUpx11ih4vGFF", new JSONObject());
            } catch (HTTPRequestFailException e) {
                Bukkit.getLogger().info("Error when player " + pl.getName() + " quit " + e.getResponseCode() + ": " + e.getResponse());
            }
        }
    }

}
