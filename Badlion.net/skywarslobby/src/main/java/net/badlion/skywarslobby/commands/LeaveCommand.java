package net.badlion.skywarslobby.commands;

import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.skywarslobby.SkyWarsLobby;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

public class LeaveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
	    if (sender instanceof Player) {
		    final Player player = (Player) sender;
		    new BukkitRunnable() {
			    public void run() {
				    LeaveCommand.leaveQueue(player);
			    }
		    }.runTaskAsynchronously(SkyWarsLobby.getInstance());
	    }
	    return true;
    }

    public static void leaveQueue(final Player player) {
        try {
            JSONObject jsonObject = HTTPCommon.executePOSTRequest("http://127.0.0.1:9014/RemoveFromQueue/" + player.getUniqueId().toString() + "/" + player.getName() + "/4jzyuUGb5AQUvVGLeUpx11ih4vGFF", new JSONObject());

            if (jsonObject != null) {
                if (jsonObject.containsKey("error")) {
                    player.sendMessage(ChatColor.RED + "Not currently in queue");
                } else if (jsonObject.containsKey("success")) {
                    player.sendMessage(ChatColor.GREEN + "Removed from queue");

                    new BukkitRunnable() {
                        public void run() {
                            player.getInventory().setItem(8, new ItemStack(Material.AIR));
                            player.updateInventory();
                        }
                    }.runTask(SkyWarsLobby.getInstance());
                } else {
                    player.sendMessage(ChatColor.RED + "Something went wrong. Report it to an admin if it continues");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Something went wrong. Report it to an admin if it continues");
            }
        } catch (HTTPRequestFailException e) {
            player.sendMessage(ChatColor.RED + "Something went wrong. Report it to an admin if it continues");
            Bukkit.getLogger().info("Error when player " + player.getName() + " executed leave " + e.getResponseCode() + ": " + e.getResponse());
        }
    }

}
