package net.badlion.uhc.commands.handlers;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.tasks.TenMinutesOfflineTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BanCommandHandler {

    public static Map<UUID, Location> deathLocations = new HashMap<>();

    public static void handleBanCommand(CommandSender sender, String[] args) {
        if (args.length >= 1) {
            Player ban = Bukkit.getPlayerExact(args[0]);
            if (ban != null) {
                UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(ban.getUniqueId());
                if (uhcPlayer.getState() == UHCPlayer.State.DEAD) {
                    sender.sendMessage(ChatColor.RED + "That player is already deathbanned!");
                } else {
                    UHCPlayerManager.updateUHCPlayerState(ban.getUniqueId(), UHCPlayer.State.DEAD);
                    ban.teleport(BadlionUHC.getInstance().getSpawnLocation());
                    ban.kickPlayer("You have been deathbanned by the host!");
                    sender.sendMessage(ChatColor.GREEN + "You have deathbanned " + ChatColor.YELLOW + ban.getName() + ChatColor.GREEN + "!");

                    uhcPlayer.storeDeathData(ban);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "That player is offline or does not exist!");
            }
        } else {
            sender.sendMessage("Usage: /uhc deathban <player> <reason>");
        }
    }

    public static void handleUnbanCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            UUID uuid = BadlionUHC.getInstance().getUUID(args[0]);
            UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(uuid);
            if (uhcPlayer != null) {
                sender.sendMessage(ChatColor.GREEN + "You have undeathbanned " + ChatColor.YELLOW + args[0] + ChatColor.GREEN + "!");
                UHCPlayerManager.updateUHCPlayerState(uuid, UHCPlayer.State.PLAYER);
                uhcPlayer.setGiveInventory(true);

	            Player player = BadlionUHC.getInstance().getServer().getPlayer(uuid);
	            if (player != null) {
		            player.kickPlayer("You have been revived, please reconnect!");
	            } else {
		            // Kick off AFK task manually if they're offline
		            if (BadlionUHC.getInstance().getConfig().getBoolean("anti-afk", true)) {
			            uhcPlayer.setOfflineTask(new TenMinutesOfflineTask(uhcPlayer).runTaskLater(BadlionUHC.getInstance(), 60 * 20 * 10));
		            }
	            }
            } else {
                sender.sendMessage(ChatColor.RED + "That player is not deathbanned or does not exist!");
            }
        } else {
            sender.sendMessage("Usage: /uhc undeathban <player>");
        }
    }

}
