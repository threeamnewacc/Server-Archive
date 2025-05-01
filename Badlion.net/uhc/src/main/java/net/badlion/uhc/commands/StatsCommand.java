package net.badlion.uhc.commands;

import com.google.common.collect.ImmutableList;
import net.badlion.common.libraries.StringCommon;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.ministats.MiniStats;
import net.badlion.ministats.MiniStatsPlayer;
import net.badlion.ministats.managers.DatabaseManager;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCMiniStatsPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StatsCommand implements CommandExecutor {

    private static DecimalFormat df = new DecimalFormat("#.00");

	private final Map<UUID, UUID> disguisedStats = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, final String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;

            BadlionUHC.getInstance().getServer().getScheduler().runTaskAsynchronously(BadlionUHC.getInstance(), new Runnable() {
                @Override
                public void run() {
                    UUID uuid = player.getUniqueId();
                    String username = player.getName();

                    if (args.length > 0) {
	                    // Check for disguised name
	                    Player target = BadlionUHC.getInstance().getServer().getPlayerExact(args[0]);

	                    // Is player disguised?
	                    if (target != null) {
		                    if (target.isDisguised() && target.getDisguisedName().equalsIgnoreCase(args[0])) {
			                    // Check if we already have a random UUID for stats
			                    UUID statsUUID = StatsCommand.this.disguisedStats.get(target.getUniqueId());

			                    if (statsUUID == null) {
				                    // Get the UUID of a random player on the server
				                    statsUUID = ImmutableList.copyOf(BadlionUHC.getInstance().getServer().getOnlinePlayers()).get(Gberry.generateRandomInt(0, BadlionUHC.getInstance().getServer().getCurrentPlayers() - 1)).getUniqueId();

				                    StatsCommand.this.disguisedStats.put(target.getUniqueId(), statsUUID);
			                    }

			                    uuid = statsUUID;
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
                    BadlionUHC.getInstance().getServer().getScheduler().runTask(BadlionUHC.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            new StatsInventory(finalUsername, (UHCMiniStatsPlayer) miniStatsPlayer).openInventory(player);
                        }
                    });
                }
            });
        }

        return true;
    }

    public class StatsInventory {

        private SmellyInventory smellyInventory;

        public StatsInventory(String username, UHCMiniStatsPlayer uhcMiniStatsPlayer) {
	        SmellyInventory smellyInventory = new SmellyInventory(SmellyInventory.getEmptySmellyInventoryHandler(), 54,
			        ChatColor.AQUA + ChatColor.BOLD.toString() + "Stats for " + username);

            ItemStack item = new ItemStack(Material.BEACON);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GREEN + "" + uhcMiniStatsPlayer.getWins() + " wins");
            item.setItemMeta(itemMeta);
            smellyInventory.getMainInventory().addItem(item);

            ItemStack item2 = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta itemMeta2 = item2.getItemMeta();
            itemMeta2.setDisplayName(ChatColor.GREEN + "" + uhcMiniStatsPlayer.getKills() + " kills");
            item2.setItemMeta(itemMeta2);
            smellyInventory.getMainInventory().addItem(item2);

            ItemStack item3 = new ItemStack(Material.SKULL);
            ItemMeta itemMeta3 = item3.getItemMeta();
            itemMeta3.setDisplayName(ChatColor.GREEN + "" + uhcMiniStatsPlayer.getDeaths() + " deaths");
            item3.setItemMeta(itemMeta3);
            smellyInventory.getMainInventory().addItem(item3);

            ItemStack item4 = new ItemStack(Material.DIAMOND_HOE);
            ItemMeta itemMeta4 = item4.getItemMeta();
            itemMeta4.setDisplayName(ChatColor.GREEN + "KDR: " + df.format(uhcMiniStatsPlayer.getKdr()));
            item4.setItemMeta(itemMeta4);
            smellyInventory.getMainInventory().addItem(item4);

            ItemStack item5 = new ItemStack(Material.EXP_BOTTLE);
            ItemMeta itemMeta5 = item5.getItemMeta();
            itemMeta5.setDisplayName(ChatColor.GREEN + "" + uhcMiniStatsPlayer.getLevels() + " levels earned");
            item5.setItemMeta(itemMeta5);
            smellyInventory.getMainInventory().addItem(item5);

            ItemStack item6 = new ItemStack(Material.IRON_SWORD);
            ItemMeta itemMeta6 = item6.getItemMeta();
            itemMeta6.setDisplayName(ChatColor.GREEN + "" + df.format(uhcMiniStatsPlayer.getDamageDealt() / 2) + " hearts dealt");
            item6.setItemMeta(itemMeta6);
            smellyInventory.getMainInventory().addItem(item6);

            ItemStack item7 = new ItemStack(Material.IRON_AXE);
            ItemMeta itemMeta7 = item7.getItemMeta();
            itemMeta7.setDisplayName(ChatColor.GREEN + "" + df.format(uhcMiniStatsPlayer.getDamageTaken() / 2) + " hearts taken");
            item7.setItemMeta(itemMeta7);
            smellyInventory.getMainInventory().addItem(item7);

            ItemStack item8 = new ItemStack(Material.LEATHER_BOOTS);
            ItemMeta itemMeta8 = item8.getItemMeta();
            itemMeta8.setDisplayName(ChatColor.GREEN + "" + df.format(uhcMiniStatsPlayer.getFallDamage() / 2) + " fall damage hearts taken");
            item8.setItemMeta(itemMeta8);
            smellyInventory.getMainInventory().addItem(item8);

            ItemStack item9 = new ItemStack(Material.GOLD_SWORD);
            ItemMeta itemMeta9 = item9.getItemMeta();
            itemMeta9.setDisplayName(ChatColor.GREEN + "Highest Kill Streak: " + uhcMiniStatsPlayer.getHighestKillStreak());
            item9.setItemMeta(itemMeta9);
            smellyInventory.getMainInventory().addItem(item9);

            ItemStack item10 = new ItemStack(Material.WATCH);
            ItemMeta itemMeta10 = item10.getItemMeta();
            itemMeta10.setDisplayName(ChatColor.GREEN + "Time Played: " + df.format(uhcMiniStatsPlayer.getTimePlayed() / 3600) + " hours");
            item10.setItemMeta(itemMeta10);
            smellyInventory.getMainInventory().addItem(item10);

            ItemStack item11 = new ItemStack(Material.WOOD_SWORD);
            ItemMeta itemMeta11 = item11.getItemMeta();
            itemMeta11.setDisplayName(ChatColor.GREEN + "Hit Accuracy: " + df.format(uhcMiniStatsPlayer.getSwordAccuracy() * 100) + "%");
            item11.setItemMeta(itemMeta11);
            smellyInventory.getMainInventory().addItem(item11);

            ItemStack item12 = new ItemStack(Material.STONE_SWORD);
            ItemMeta itemMeta12 = item12.getItemMeta();
            itemMeta12.setDisplayName(ChatColor.GREEN + "Sword Blocks: " + uhcMiniStatsPlayer.getSwordBlocks());
            item12.setItemMeta(itemMeta12);
            smellyInventory.getMainInventory().addItem(item12);

            ItemStack item13 = new ItemStack(Material.ARROW);
            ItemMeta itemMeta13 = item13.getItemMeta();
            itemMeta13.setDisplayName(ChatColor.GREEN + "Arrows Shot: " + uhcMiniStatsPlayer.getArrowsShot());
            item13.setItemMeta(itemMeta13);
            smellyInventory.getMainInventory().addItem(item13);

            ItemStack item14 = new ItemStack(Material.BOW);
            ItemMeta itemMeta14 = item14.getItemMeta();
            itemMeta14.setDisplayName(ChatColor.GREEN + "Bow Accuracy: " + df.format(uhcMiniStatsPlayer.getArrowAccuracy() * 100) + "%");
            item14.setItemMeta(itemMeta14);
            smellyInventory.getMainInventory().addItem(item14);

            ItemStack item15 = new ItemStack(Material.BOW);
            ItemMeta itemMeta15 = item15.getItemMeta();
            itemMeta15.setDisplayName(ChatColor.GREEN + "Bow Punches: " + uhcMiniStatsPlayer.getBowPunches());
            item15.setItemMeta(itemMeta15);
            smellyInventory.getMainInventory().addItem(item15);

            ItemStack item16 = new ItemStack(Material.APPLE);
            ItemMeta itemMeta16 = item16.getItemMeta();
            itemMeta16.setDisplayName(ChatColor.GREEN + "Hearts Healed: " + uhcMiniStatsPlayer.getHeartsHealed());
            item16.setItemMeta(itemMeta16);
            smellyInventory.getMainInventory().addItem(item16);

            ItemStack item17 = new ItemStack(Material.GOLDEN_APPLE);
            ItemMeta itemMeta17 = item17.getItemMeta();
            itemMeta17.setDisplayName(ChatColor.GREEN + "Absorption Hearts: " + uhcMiniStatsPlayer.getAbsorptionHearts());
            item17.setItemMeta(itemMeta17);
            smellyInventory.getMainInventory().addItem(item17);

            ItemStack item18 = new ItemStack(Material.GOLDEN_APPLE);
            ItemMeta itemMeta18 = item18.getItemMeta();
            itemMeta18.setDisplayName(ChatColor.GREEN + "Golden Apples Eaten: " + uhcMiniStatsPlayer.getGoldenApples());
            item18.setItemMeta(itemMeta18);
            smellyInventory.getMainInventory().addItem(item18);

            ItemStack item19 = new ItemStack(Material.GOLDEN_APPLE);
            ItemMeta itemMeta19 = item19.getItemMeta();
            itemMeta19.setDisplayName(ChatColor.GREEN + "Golden Heads Eaten: " + uhcMiniStatsPlayer.getGoldenHeads());
            item19.setItemMeta(itemMeta19);
            smellyInventory.getMainInventory().addItem(item19);

            ItemStack item20 = new ItemStack(Material.SADDLE);
            ItemMeta itemMeta20 = item20.getItemMeta();
            itemMeta20.setDisplayName(ChatColor.GREEN + "Horses Tamed: " + uhcMiniStatsPlayer.getHorsesTamed());
            item20.setItemMeta(itemMeta20);
            smellyInventory.getMainInventory().addItem(item20);

            ItemStack item21 = new ItemStack(Material.NETHERRACK);
            ItemMeta itemMeta21 = item21.getItemMeta();
            itemMeta21.setDisplayName(ChatColor.GREEN + "Nether's Entered: " + uhcMiniStatsPlayer.getNetherPortals());
            item21.setItemMeta(itemMeta21);
            smellyInventory.getMainInventory().addItem(item21);

            ItemStack item22 = new ItemStack(Material.ENDER_STONE);
            ItemMeta itemMeta22 = item22.getItemMeta();
            itemMeta22.setDisplayName(ChatColor.GREEN + "End's Entered: " + uhcMiniStatsPlayer.getEndPortals());
            item22.setItemMeta(itemMeta22);
            smellyInventory.getMainInventory().addItem(item22);

            ItemStack item23 = new ItemStack(Material.DIAMOND_ORE);
            ItemMeta itemMeta23 = item23.getItemMeta();
            itemMeta23.setDisplayName(ChatColor.GREEN + "Blocks Mined");

            List<String> blocks = new ArrayList<>();

            for (Map.Entry<String, Object> entry : ((Map<String, Object>) uhcMiniStatsPlayer.getBlocksBroken()).entrySet()) {
                blocks.add(ChatColor.AQUA + StringCommon.cleanEnum(entry.getKey()) + ": " + ChatColor.GOLD + MiniStats.getLong(entry.getValue()));
            }

            itemMeta23.setLore(blocks);
            item23.setItemMeta(itemMeta23);
            smellyInventory.getMainInventory().addItem(item23);

            ItemStack item24 = new ItemStack(Material.SKULL_ITEM, 1, (short) 2);
            ItemMeta itemMeta24 = item24.getItemMeta();
            itemMeta24.setDisplayName(ChatColor.GREEN + "Animals/Mobs Slain");

            List<String> mobs = new ArrayList<>();

            for (Map.Entry<String, Object> entry : ((Map<String, Object>) uhcMiniStatsPlayer.getAnimalMobs()).entrySet()) {
                mobs.add(ChatColor.AQUA + StringCommon.cleanEnum(entry.getKey()) + ": " + ChatColor.GOLD + MiniStats.getLong(entry.getValue()));
            }

            itemMeta24.setLore(mobs);
            item24.setItemMeta(itemMeta24);
            smellyInventory.getMainInventory().addItem(item24);

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
