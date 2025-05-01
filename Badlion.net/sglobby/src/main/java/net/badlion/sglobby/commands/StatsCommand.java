package net.badlion.sglobby.commands;

import com.google.common.collect.ImmutableList;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.UserDataManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.gberry.utils.RatingUtil;
import net.badlion.ministats.MiniStatsPlayer;
import net.badlion.ministats.managers.DatabaseManager;
import net.badlion.sglobby.FakeSGMiniStatsPlayer;
import net.badlion.sglobby.SGLobby;
import net.badlion.sglobby.managers.RatingManager;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.UUID;

public class StatsCommand implements CommandExecutor {

    private static DecimalFormat df = new DecimalFormat("#.00");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, final String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;

	        SGLobby.getInstance().getServer().getScheduler().runTaskAsynchronously(SGLobby.getInstance(), new Runnable() {
                @Override
                public void run() {
                    UUID uuid = player.getUniqueId();
                    String username = player.getName();

                    if (args.length > 0) {
	                    // Check for disguised name
	                    Player target = SGLobby.getInstance().getServer().getPlayerExact(args[0]);

	                    // Is player disguised?
	                    if (target != null) {
		                    if (target.isDisguised() && target.getDisguisedName().equalsIgnoreCase(args[0])) {
			                    // Get the UUID of a random player on the server
			                    uuid = ImmutableList.copyOf(SGLobby.getInstance().getServer().getOnlinePlayers()).get(
					                    Gberry.generateRandomInt(0, SGLobby.getInstance().getServer().getCurrentPlayers() - 1)).getUniqueId();
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

	                // Load stats
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

	                // Load UserData
	                final UserDataManager.UserData userData = UserDataManager.getUserDataFromDB(uuid);

	                final UUID finalUUID = uuid;
	                final String finalUsername = username;
	                SGLobby.getInstance().getServer().getScheduler().runTask(SGLobby.getInstance(), new Runnable() {
                        @Override
                        public void run() {
	                        FakeSGMiniStatsPlayer sgMiniStatsPlayer = (FakeSGMiniStatsPlayer) miniStatsPlayer;

	                        JSONObject sgSettings = userData.getSGSettings();
	                        boolean ratingVisible = (boolean) sgSettings.get("rating_visibility");
	                        boolean statsVisible = (boolean) sgSettings.get("stats_visibility");

	                        SmellyInventory smellyInventory = new SmellyInventory(SmellyInventory.getEmptySmellyInventoryHandler(), 54,
			                        ChatColor.AQUA + ChatColor.BOLD.toString() + "Stats for " + finalUsername);

	                        if (ratingVisible) {
		                        // TODO: LADDERS WHEN I FIGURE OUT RATING STUFF, REFER TO LOBBY SIDEBAR
		                        /*Ladder ladder;
		                        if (MPG.GAME_TYPE == MPG.GameType.FFA) {
			                        ladder = Ladder.getLadder(SGLobby.getInstance().getSGGame().getGamemode().getName(), Ladder.LadderType.FFA);
		                        } else {
			                        ladder = Ladder.getLadder(SGLobby.getInstance().getSGGame().getGamemode().getName(), Ladder.LadderType.TEAMS);
		                        }*/

		                        //int rating = RatingManager.getPlayerRating(finalUUID, ladder);
		                        int rating = RatingManager.getPlayerRating(finalUUID);
		                        String division = RatingUtil.getDivisionFromRating(rating);

		                        // Are they hiding their rating?
		                        if ((boolean) sgSettings.get("rating_visibility")) {
			                        // Has the player not finished their placement matches to show their rating?
			                        if (sgMiniStatsPlayer.getWins() + sgMiniStatsPlayer.getLosses() < RatingUtil.SG_PLACEMENT_MATCHES) {
				                        division = ChatColor.WHITE + "[Unranked]";
			                        }
		                        } else {
			                        division = "§oHidden";
		                        }

		                        smellyInventory.getMainInventory().addItem(
				                        ItemStackUtil.createItem(Material.NETHER_STAR, ChatColor.GREEN + "Rating: " + division));
	                        } else {
		                        smellyInventory.getMainInventory().addItem(
				                        ItemStackUtil.createItem(Material.NETHER_STAR, ChatColor.GREEN + "Rating: " + ChatColor.WHITE + ChatColor.ITALIC + "Hidden"));
	                        }

	                        if (statsVisible) {
		                        smellyInventory.getMainInventory().addItem(
				                        ItemStackUtil.createItem(Material.BEACON, ChatColor.GREEN + "" + sgMiniStatsPlayer.getWins() + " wins"));

		                        smellyInventory.getMainInventory().addItem(
				                        ItemStackUtil.createItem(Material.DIAMOND_SWORD, ChatColor.GREEN + "" + sgMiniStatsPlayer.getKills() + " kills"));

		                        smellyInventory.getMainInventory().addItem(
				                        ItemStackUtil.createItem(Material.IRON_SWORD, ChatColor.GREEN + "" + StatsCommand.df.format(sgMiniStatsPlayer.getDamageDealt() / 2) + " hearts dealt"));

		                        smellyInventory.getMainInventory().addItem(
				                        ItemStackUtil.createItem(Material.GOLD_SWORD, ChatColor.GREEN + "Highest Kill Streak: " + sgMiniStatsPlayer.getHighestKillStreak()));

		                        smellyInventory.getMainInventory().addItem(
				                        ItemStackUtil.createItem(Material.WATCH, ChatColor.GREEN + "Time Played: " + StatsCommand.df.format(sgMiniStatsPlayer.getTimePlayed() / 3600) + " hours"));

		                        smellyInventory.getMainInventory().addItem(
				                        ItemStackUtil.createItem(Material.WOOD_SWORD, ChatColor.GREEN + "Hit Accuracy: " + StatsCommand.df.format(sgMiniStatsPlayer.getSwordAccuracy() * 100) + "%"));

		                        smellyInventory.getMainInventory().addItem(
				                        ItemStackUtil.createItem(Material.STONE_SWORD, ChatColor.GREEN + "Sword Blocks: " + sgMiniStatsPlayer.getSwordBlocks()));

		                        smellyInventory.getMainInventory().addItem(
				                        ItemStackUtil.createItem(Material.ARROW, ChatColor.GREEN + "Arrows Shot: " + sgMiniStatsPlayer.getArrowsShot()));

		                        smellyInventory.getMainInventory().addItem(
				                        ItemStackUtil.createItem(Material.BOW, ChatColor.GREEN + "Bow Accuracy: " + StatsCommand.df.format(sgMiniStatsPlayer.getArrowAccuracy() * 100) + "%"));

		                        smellyInventory.getMainInventory().addItem(
				                        ItemStackUtil.createItem(Material.BOW, ChatColor.GREEN + "Bow Punches: " + sgMiniStatsPlayer.getBowPunches()));

		                        smellyInventory.getMainInventory().addItem(
				                        ItemStackUtil.createItem(Material.CHEST, ChatColor.GREEN + "Tier 1 Chests Opened: " + sgMiniStatsPlayer.getNumberOfTierChestsOpened(1)));

		                        smellyInventory.getMainInventory().addItem(
				                        ItemStackUtil.createItem(Material.CHEST, ChatColor.GREEN + "Tier 2 Chests Opened: " + sgMiniStatsPlayer.getNumberOfTierChestsOpened(2)));

		                        smellyInventory.getMainInventory().addItem(
				                        ItemStackUtil.createItem(Material.ENDER_CHEST, ChatColor.GREEN + "Supply Drops Opened: " + sgMiniStatsPlayer.getNumberOfSupplyDropsOpened()));
	                        } else {
		                        smellyInventory.getMainInventory().addItem(
				                        ItemStackUtil.createItem(Material.IRON_DOOR, ChatColor.GREEN + "" + ChatColor.ITALIC + "Stats Are Hidden"));
	                        }

	                        BukkitUtil.openInventory(player, smellyInventory.getMainInventory());
                        }
                    });
                }
            });
        }

        return true;
    }

}
