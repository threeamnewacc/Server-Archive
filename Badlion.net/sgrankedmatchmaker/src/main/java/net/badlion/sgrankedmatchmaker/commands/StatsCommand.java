package net.badlion.sgrankedmatchmaker.commands;

import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.ministats.MiniStats;
import net.badlion.sgrankedmatchmaker.SGRankedMatchMaker;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StatsCommand implements CommandExecutor {

    private static DecimalFormat df = new DecimalFormat("#.00");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        final Player player = (Player) sender;

        SGRankedMatchMaker.getInstance().getServer().getScheduler().runTaskAsynchronously(SGRankedMatchMaker.getInstance(), new Runnable() {
            @Override
            public void run() {
                UUID uuid = player.getUniqueId();
                String username = player.getName();

                if (args.length > 0) {
                    UUID uuidTmp = Gberry.getOfflineUUID(args[0]);
                    if (uuidTmp != null) {
                        uuid = uuidTmp;
                        username = args[0];
                    }
                }

                // Fetch their rating
                final Map<String, Integer> ratings = StatsCommand.getRatings(uuid);

                // Fethc the rest of their stats
                JSONObject rawJsonData;
                try {
                    rawJsonData = Gberry.executeCouchDBGetQuery(MiniStats.TAG, "player-uuid", uuid.toString());
                } catch (HTTPRequestFailException e) {
                    if (e.getResponseCode() == 404) {
                        player.sendMessage(ChatColor.RED + "No stats found.");
                    } else {
                        player.sendMessage(ChatColor.RED + "Error retrieving stats.");
                    }

                    return;
                }

                if (rawJsonData == null) {
                    player.sendMessage(ChatColor.RED + "Error retrieving stats.");
                    return;
                }

                ArrayList<JSONObject> rows = ((ArrayList<JSONObject>) rawJsonData.get("rows"));
                if (rows.size() > 1) {
                    player.sendMessage(ChatColor.RED + "Internal error when retrieving stats. Contact an admin");
                } else if (rows.size() == 1) {
                    final JSONObject doc = (JSONObject) rows.get(0).get("doc");

                    if (doc != null) {

                        final String finalUsername = username;
                        SGRankedMatchMaker.getInstance().getServer().getScheduler().runTask(SGRankedMatchMaker.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                new StatsInventory(finalUsername, ratings, doc).openInventory(player);
                            }
                        });
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "No stats found.");
                }
            }
        });


        return true;
    }

    public static Map<String, Integer> getRatings(UUID uuid) {
        String query = "SELECT * FROM mcsg_ladder_ratings WHERE uuid = ?;";

        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        Map<String, Integer> ratings = new HashMap<>();

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid.toString());
            rs = Gberry.executeQuery(connection, ps);

            while (rs.next()) {
                if (rs.getInt("lid") == 0) {
                    continue;
                }

                // TODO unhard-code
                ratings.put("Classic", rs.getInt("rating"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }

        return ratings;
    }

    public class StatsInventory {

        private SmellyInventory smellyInventory;

        public StatsInventory(String username, Map<String, Integer> ratings, JSONObject data) {
            SmellyInventory smellyInventory = new SmellyInventory(SmellyInventory.getEmptySmellyInventoryHandler(), 54,
		            ChatColor.AQUA + ChatColor.BOLD.toString() + "Stats for " + username);

            ItemStack itemRating = new ItemStack(Material.NETHER_STAR);
            ItemMeta itemRatingMeta = itemRating.getItemMeta();
            itemRatingMeta.setDisplayName(ChatColor.GREEN + "Ratings");

            List<String> ratingsString = new ArrayList<>();

            for (Map.Entry<String, Integer> entry : ratings.entrySet()) {
                ratingsString.add(ChatColor.AQUA + entry.getKey() + ": " + ChatColor.GOLD + entry.getValue());
            }

            itemRatingMeta.setLore(ratingsString);
            itemRating.setItemMeta(itemRatingMeta);
            smellyInventory.getMainInventory().addItem(itemRating);

            ItemStack item = new ItemStack(Material.BEACON);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GREEN + "" + data.get("wins") + " wins");
            item.setItemMeta(itemMeta);
            smellyInventory.getMainInventory().addItem(item);

            ItemStack item2 = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta itemMeta2 = item2.getItemMeta();
            itemMeta2.setDisplayName(ChatColor.GREEN + "" + data.get("kills") + " kills");
            item2.setItemMeta(itemMeta2);
            smellyInventory.getMainInventory().addItem(item2);

            ItemStack item3 = new ItemStack(Material.SKULL);
            ItemMeta itemMeta3 = item3.getItemMeta();
            itemMeta3.setDisplayName(ChatColor.GREEN + "" + data.get("deaths") + " deaths");
            item3.setItemMeta(itemMeta3);
            smellyInventory.getMainInventory().addItem(item3);

            double kdr;
            if (((long) data.get("deaths")) == 0L) {
                kdr = (long) data.get("kills");
            } else if (((long) data.get("kills")) == 0L) {
                kdr = 0;
            } else {
                kdr = (long) data.get("kills") / ((Long) data.get("deaths")).floatValue();
            }

            ItemStack item4 = new ItemStack(Material.DIAMOND_HOE);
            ItemMeta itemMeta4 = item4.getItemMeta();
            itemMeta4.setDisplayName(ChatColor.GREEN + "KDR: " + df.format(kdr));
            item4.setItemMeta(itemMeta4);
            smellyInventory.getMainInventory().addItem(item4);

            ItemStack item6 = new ItemStack(Material.IRON_SWORD);
            ItemMeta itemMeta6 = item6.getItemMeta();
            itemMeta6.setDisplayName(ChatColor.GREEN + "" + df.format((double) data.get("damageDealt") / 2) + " hearts dealt");
            item6.setItemMeta(itemMeta6);
            smellyInventory.getMainInventory().addItem(item6);

            ItemStack item7 = new ItemStack(Material.IRON_AXE);
            ItemMeta itemMeta7 = item7.getItemMeta();
            itemMeta7.setDisplayName(ChatColor.GREEN + "" + df.format((double) data.get("damageTaken") / 2) + " hearts taken");
            item7.setItemMeta(itemMeta7);
            smellyInventory.getMainInventory().addItem(item7);

            ItemStack item9 = new ItemStack(Material.GOLD_SWORD);
            ItemMeta itemMeta9 = item9.getItemMeta();
            itemMeta9.setDisplayName(ChatColor.GREEN + "Highest Kill Streak: " + data.get("highestKillStreak"));
            item9.setItemMeta(itemMeta9);
            smellyInventory.getMainInventory().addItem(item9);

            ItemStack item10 = new ItemStack(Material.WATCH);
            ItemMeta itemMeta10 = item10.getItemMeta();
            itemMeta10.setDisplayName(ChatColor.GREEN + "Time Played: " + df.format(((double) MiniStats.getLong(data.get("timePlayed"))) / 3600) + " hours");
            item10.setItemMeta(itemMeta10);
            smellyInventory.getMainInventory().addItem(item10);

            ItemStack item11 = new ItemStack(Material.WOOD_SWORD);
            ItemMeta itemMeta11 = item11.getItemMeta();
            itemMeta11.setDisplayName(ChatColor.GREEN + "Hit Accuracy: " + (MiniStats.getLong(data.get("swordSwings")) != 0 ? df.format(((double) MiniStats.getLong(data.get("swordHits")) / (MiniStats.getLong(data.get("swordSwings")) + MiniStats.getLong(data.get("swordHits")))) * 100) : 0) + "%");
            item11.setItemMeta(itemMeta11);
            smellyInventory.getMainInventory().addItem(item11);

            ItemStack item12 = new ItemStack(Material.STONE_SWORD);
            ItemMeta itemMeta12 = item12.getItemMeta();
            itemMeta12.setDisplayName(ChatColor.GREEN + "Sword Blocks: " + (MiniStats.getLong(data.get("swordBlocks"))));
            item12.setItemMeta(itemMeta12);
            smellyInventory.getMainInventory().addItem(item12);

            ItemStack item13 = new ItemStack(Material.ARROW);
            ItemMeta itemMeta13 = item13.getItemMeta();
            itemMeta13.setDisplayName(ChatColor.GREEN + "Arrows Shot: " + (MiniStats.getLong(data.get("arrowsShot"))));
            item13.setItemMeta(itemMeta13);
            smellyInventory.getMainInventory().addItem(item13);

            ItemStack item14 = new ItemStack(Material.BOW);
            ItemMeta itemMeta14 = item14.getItemMeta();
            itemMeta14.setDisplayName(ChatColor.GREEN + "Bow Accuracy: " + (MiniStats.getLong(data.get("arrowsShot")) != 0 ? df.format(((double) MiniStats.getLong(data.get("arrowsHit")) / MiniStats.getLong(data.get("arrowsShot"))) * 100) : 0) + "%");
            item14.setItemMeta(itemMeta14);
            smellyInventory.getMainInventory().addItem(item14);

            ItemStack item15 = new ItemStack(Material.BOW);
            ItemMeta itemMeta15 = item15.getItemMeta();
            itemMeta15.setDisplayName(ChatColor.GREEN + "Bow Punches: " + MiniStats.getLong(data.get("bowPunches")));
            item15.setItemMeta(itemMeta15);
            smellyInventory.getMainInventory().addItem(item15);

            ItemStack item16 = new ItemStack(Material.CHEST);
            ItemMeta itemMeta16 = item16.getItemMeta();
            itemMeta16.setDisplayName(ChatColor.GREEN + "Tier 1 Chests Opened: " + MiniStats.getLong(data.get("tier1_opened")));
            item16.setItemMeta(itemMeta16);
            smellyInventory.getMainInventory().addItem(item16);

            ItemStack item17 = new ItemStack(Material.ENDER_CHEST);
            ItemMeta itemMeta17 = item17.getItemMeta();
            itemMeta17.setDisplayName(ChatColor.GREEN + "Tier 2 Chests Opened: " + MiniStats.getLong(data.get("tier2_opened")));
            item17.setItemMeta(itemMeta17);
            smellyInventory.getMainInventory().addItem(item17);

            ItemStack cancelReportItem = new ItemStack(Material.WOOL, 1, (short) 14);
            ItemMeta cancelInventoryItemMeta = cancelReportItem.getItemMeta();
            cancelInventoryItemMeta.setDisplayName(ChatColor.GREEN + "Close");
            cancelReportItem.setItemMeta(cancelInventoryItemMeta);

            smellyInventory.getMainInventory().setItem(smellyInventory.getMainInventory().getSize() - 1, cancelReportItem);

            this.smellyInventory = smellyInventory;
        }

        public void openInventory(final Player player) {
            if (player.getOpenInventory() != null) {
                BukkitUtil.runTaskNextTick(new Runnable() {
                    @Override
                    public void run() {
                        if (player.isOnline()) {
                            player.closeInventory();
                            player.openInventory(StatsInventory.this.smellyInventory.getMainInventory());
                        }
                    }
                });
            } else {
                player.openInventory(this.smellyInventory.getMainInventory());
            }
        }

    }

}
