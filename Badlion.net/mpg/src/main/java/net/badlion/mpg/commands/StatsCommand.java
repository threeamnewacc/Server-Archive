package net.badlion.mpg.commands;

import com.google.common.collect.ImmutableList;
import net.badlion.gberry.Gberry;
import net.badlion.ministats.MiniStatsPlayer;
import net.badlion.ministats.managers.DatabaseManager;
import net.badlion.mpg.MPG;
import net.badlion.mpg.inventories.StatsInventory;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class StatsCommand implements CommandExecutor {

	private static StatsCommand instance;

	private StatsInventory statsInventory;

	public StatsCommand() {
		StatsCommand.instance = this;
	}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, final String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;

	        MPG.getInstance().getServer().getScheduler().runTaskAsynchronously(MPG.getInstance(), new Runnable() {
                @Override
                public void run() {
                    UUID uuid = player.getUniqueId();
                    String username = player.getName();

                    if (args.length > 0) {
	                    // Check for disguised name
	                    Player target = MPG.getInstance().getServer().getPlayerExact(args[0]);

	                    // Is player disguised?
	                    if (target != null) {
		                    if (target.isDisguised() && target.getDisguisedName().equalsIgnoreCase(args[0])) {
			                    // Get the UUID of a random player on the server
			                    uuid = ImmutableList.copyOf(MPG.getInstance().getServer().getOnlinePlayers()).get(
					                    Gberry.generateRandomInt(0, MPG.getInstance().getServer().getCurrentPlayers() - 1)).getUniqueId();
			                    username = target.getDisguisedName();
		                    } else {
			                    uuid = target.getUniqueId();
			                    username = target.getName();
		                    }
	                    } else {
		                    // Check offline users
		                    UUID uuidTmp = Gberry.getOfflineUUID(args[0]);
		                    if (uuidTmp != null) {
			                    uuid = uuidTmp;
			                    username = Gberry.getUsernameFromUUID(uuid);
		                    } else {
			                    player.sendMessage(ChatColor.RED + "Player not found.");
			                    return;
		                    }
	                    }
                    }

                    Connection connection = null;
                    final MiniStatsPlayer miniStatsPlayer;
                    try {
                        connection = Gberry.getConnection();

                        miniStatsPlayer = DatabaseManager.getPlayerStats(connection, uuid);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        player.sendMessage(ChatColor.RED + "Error retrieving stats.");
                        return;
                    } finally {
	                    Gberry.closeComponents(connection);
                    }

                    if (miniStatsPlayer == null) {
                        player.sendMessage(ChatColor.RED + "No stats found.");
                        return;
                    }

	                final UUID finalUUID = uuid;
	                final String finalUsername = username;
	                MPG.getInstance().getServer().getScheduler().runTask(MPG.getInstance(), new Runnable() {
                        @Override
                        public void run() {
	                        StatsCommand.this.statsInventory.openPlayerStatsInventory(player, finalUUID, finalUsername, miniStatsPlayer);
                        }
                    });
                }
            });
        }

        return true;
    }

	public static StatsCommand getInstance() {
		return StatsCommand.instance;
	}

	public void setStatsInventory(StatsInventory statsInventory) {
		this.statsInventory = statsInventory;
	}


}
