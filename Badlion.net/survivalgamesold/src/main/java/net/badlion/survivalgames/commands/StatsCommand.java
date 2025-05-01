package net.badlion.survivalgames.commands;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.ministats.MiniStatsPlayer;
import net.badlion.ministats.managers.DatabaseManager;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.survivalgames.SurvivalGames;
import net.badlion.survivalgames.SGMiniStatsPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

public class StatsCommand implements CommandExecutor {

    private static DecimalFormat df = new DecimalFormat("#.00");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        final Player player = (Player) sender;

        SurvivalGames.getInstance().getServer().getScheduler().runTaskAsynchronously(SurvivalGames.getInstance(), new Runnable() {
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
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (miniStatsPlayer == null) {
                    player.sendMessage(ChatColor.RED + "No stats found.");
                    return;
                }

                final String finalUsername = username;
                SurvivalGames.getInstance().getServer().getScheduler().runTask(SurvivalGames.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        new StatsInventory(finalUsername, (SGMiniStatsPlayer) miniStatsPlayer).openInventory(player);
                    }
                });
            }
        });


        return true;
    }

    public class StatsInventory {

        private SmellyInventory smellyInventory;

        public StatsInventory(String username, SGMiniStatsPlayer sgMiniStatsPlayer) {
            SmellyInventory smellyInventory = new SmellyInventory(new StatsScreenHandler(), 54,
                                                                         ChatColor.AQUA + ChatColor.BOLD.toString() + "Stats for " + username);

            ItemStack item = new ItemStack(Material.BEACON);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GREEN + "" + sgMiniStatsPlayer.getWins() + " wins");
            item.setItemMeta(itemMeta);
            smellyInventory.getMainInventory().addItem(item);

            ItemStack item2 = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta itemMeta2 = item2.getItemMeta();
            itemMeta2.setDisplayName(ChatColor.GREEN + "" + sgMiniStatsPlayer.getKills() + " kills");
            item2.setItemMeta(itemMeta2);
            smellyInventory.getMainInventory().addItem(item2);

            ItemStack item6 = new ItemStack(Material.IRON_SWORD);
            ItemMeta itemMeta6 = item6.getItemMeta();
            itemMeta6.setDisplayName(ChatColor.GREEN + "" + df.format(sgMiniStatsPlayer.getDamageDealt() / 2) + " hearts dealt");
            item6.setItemMeta(itemMeta6);
            smellyInventory.getMainInventory().addItem(item6);

            ItemStack item9 = new ItemStack(Material.GOLD_SWORD);
            ItemMeta itemMeta9 = item9.getItemMeta();
            itemMeta9.setDisplayName(ChatColor.GREEN + "Highest Kill Streak: " + sgMiniStatsPlayer.getHighestKillStreak());
            item9.setItemMeta(itemMeta9);
            smellyInventory.getMainInventory().addItem(item9);

            ItemStack item10 = new ItemStack(Material.WATCH);
            ItemMeta itemMeta10 = item10.getItemMeta();
            itemMeta10.setDisplayName(ChatColor.GREEN + "Time Played: " + df.format(sgMiniStatsPlayer.getTimePlayed() / 3600) + " hours");
            item10.setItemMeta(itemMeta10);
            smellyInventory.getMainInventory().addItem(item10);

            ItemStack item11 = new ItemStack(Material.WOOD_SWORD);
            ItemMeta itemMeta11 = item11.getItemMeta();
            itemMeta11.setDisplayName(ChatColor.GREEN + "Hit Accuracy: " + (sgMiniStatsPlayer.getSwordAccuracy() * 100) + "%");
            item11.setItemMeta(itemMeta11);
            smellyInventory.getMainInventory().addItem(item11);

            ItemStack item12 = new ItemStack(Material.STONE_SWORD);
            ItemMeta itemMeta12 = item12.getItemMeta();
            itemMeta12.setDisplayName(ChatColor.GREEN + "Sword Blocks: " + sgMiniStatsPlayer.getSwordBlocks());
            item12.setItemMeta(itemMeta12);
            smellyInventory.getMainInventory().addItem(item12);

            ItemStack item13 = new ItemStack(Material.ARROW);
            ItemMeta itemMeta13 = item13.getItemMeta();
            itemMeta13.setDisplayName(ChatColor.GREEN + "Arrows Shot: " + sgMiniStatsPlayer.getArrowsShot());
            item13.setItemMeta(itemMeta13);
            smellyInventory.getMainInventory().addItem(item13);

            ItemStack item14 = new ItemStack(Material.BOW);
            ItemMeta itemMeta14 = item14.getItemMeta();
            itemMeta14.setDisplayName(ChatColor.GREEN + "Bow Accuracy: " + (sgMiniStatsPlayer.getArrowAccuracy() * 100) + "%");
            item14.setItemMeta(itemMeta14);
            smellyInventory.getMainInventory().addItem(item14);

            ItemStack item15 = new ItemStack(Material.BOW);
            ItemMeta itemMeta15 = item15.getItemMeta();
            itemMeta15.setDisplayName(ChatColor.GREEN + "Bow Punches: " + sgMiniStatsPlayer.getBowPunches());
            item15.setItemMeta(itemMeta15);
            smellyInventory.getMainInventory().addItem(item15);

            ItemStack item16 = new ItemStack(Material.CHEST);
            ItemMeta itemMeta16 = item16.getItemMeta();
            itemMeta16.setDisplayName(ChatColor.GREEN + "Tier 1 Chests Opened: " + sgMiniStatsPlayer.getTier1Opened());
            item16.setItemMeta(itemMeta16);
            smellyInventory.getMainInventory().addItem(item16);

            ItemStack item17 = new ItemStack(Material.ENDER_CHEST);
            ItemMeta itemMeta17 = item17.getItemMeta();
            itemMeta17.setDisplayName(ChatColor.GREEN + "Tier 2 Chests Opened: " + sgMiniStatsPlayer.getTier2Opened());
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

        public class StatsScreenHandler implements SmellyInventory.SmellyInventoryHandler {

            @Override
            public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, final Player player, InventoryClickEvent event, ItemStack item, int slot) {
                if (slot == fakeHolder.getInventory().getSize() - 1) {
                    player.closeInventory();
                }
            }

            @Override
            public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

            }

        }
    }

}
