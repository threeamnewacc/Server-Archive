package net.badlion.skywars.commands;

import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.skywars.SkyWars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

public class PlayAgainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        final Player player = (Player) sender;

        new BukkitRunnable() {
            public void run() {
                try {
                    JSONObject result = HTTPCommon.executePOSTRequest("http://127.0.0.1:9014/AddToQueue/ffa_" + SkyWars.getInstance().getCurrentGame().getGamemode().getName().toLowerCase() + "/" + player.getUniqueId().toString() + "/" + player.getName() + "/4jzyuUGb5AQUvVGLeUpx11ih4vGFF", new JSONObject());

                    if (result == null) {
                        player.sendMessage(ChatColor.RED + "Error when trying to join queue");
                    } else if (result.containsKey("success")) {
                        player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));
                        player.sendMessage(ChatColor.GREEN + "Added to matchmaking. You will be put into a match when enough players have joined");
                        player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));
                    } else {
                        player.sendMessage(ChatColor.RED + "Already in queue.");
                    }
                } catch (HTTPRequestFailException e) {
                    Bukkit.getLogger().info("Error when player " + player.getName() + " joined ffa classic " + e.getResponseCode() + ": " + e.getResponse());
                }
            }
        }.runTaskAsynchronously(SkyWars.getInstance());

        return true;
    }

}
