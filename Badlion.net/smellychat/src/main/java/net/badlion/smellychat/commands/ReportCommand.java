package net.badlion.smellychat.commands;

import net.badlion.banmanager.BanManager;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.smellychat.SmellyChat;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.Bukkit;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ReportCommand implements CommandExecutor {

	public static HashSet<UUID> REPORTS_DISABLED = new HashSet<>();
	public static HashSet<UUID> ACCEPTING_ALL_REPORTS = new HashSet<>();

	private ReportInventory reportInventory;

	private Map<UUID, Report> reportCooldown = new HashMap<>();
	private Map<UUID, Report> playerReportCooldown = new HashMap<>();

	private Map<UUID, Integer> timesReported = new HashMap<>();
	private Map<Long, UUID> lastReportTimestamp = new HashMap<>();

	public ReportCommand() {
		this.reportInventory = new ReportInventory();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
		if (sender instanceof Player) {
			final Player player = (Player) sender;
			if (args.length > 0) {
				if (player.hasPermission("badlion.admin")) {
					if (args[0].equals("off")) {
						ReportCommand.REPORTS_DISABLED.add(player.getUniqueId());
						player.sendMessage(ChatColor.GREEN + "Reports disabled");
						return true;
					} else if (args[0].equals("on")) {
						ReportCommand.REPORTS_DISABLED.remove(player.getUniqueId());
						player.sendMessage(ChatColor.GREEN + "Reports enabled");
						return true;
					}
				}

				if (player.hasPermission("badlion.globalmod") && args[0].equalsIgnoreCase("toggle")) {
					if (ReportCommand.ACCEPTING_ALL_REPORTS.contains(player.getUniqueId())) {
						ReportCommand.ACCEPTING_ALL_REPORTS.remove(player.getUniqueId());
						player.sendMessage(ChatColor.GREEN + "No longer accepting all reports");
						return true;
					} else {
						ReportCommand.ACCEPTING_ALL_REPORTS.add(player.getUniqueId());
						player.sendMessage(ChatColor.GREEN + "Now accepting all reports");
						return true;
					}
				}

				Player reportedPlayer = Bukkit.getPlayerExact(args[0]);

				// Is player online?
				if (reportedPlayer == null) {
					player.sendMessage(ChatColor.RED + "Player not found.");
					return true;
				} else if (args[0].equalsIgnoreCase(player.getName()) || args[0].equalsIgnoreCase(player.getDisguisedName())) {
					player.sendMessage(ChatColor.RED + "You cannot report yourself!");
					return true;
				}

				// Cooldown checks
				Report report = this.reportCooldown.get(player.getUniqueId());
				Report report2 = this.playerReportCooldown.get(player.getUniqueId());
				if (report != null && report.getTimeStamp() + 300000 > System.currentTimeMillis()) {
					player.sendMessage(ChatColor.RED + "Can only use /report once every 5 minutes.");
					return true;
				} else if (report2 != null && args[0].equalsIgnoreCase(report2.getReportedName())
						&& report2.getTimeStamp() + 900000 > System.currentTimeMillis()) {
					player.sendMessage(ChatColor.RED + "Can only report the same player once every 15 minutes.");
					return true;
				}

				// Store the info to retrieve later
				this.reportCooldown.put(player.getUniqueId(), new Report(reportedPlayer.getName(), reportedPlayer.getUniqueId()));

				// Open the reason select inventory
				this.reportInventory.openInventory(player);
			} else {
				player.sendMessage(ChatColor.RED + "Please use /report <name>");
			}
		}
		return true;
	}

	public void handleTimesReported(UUID uuid) {
		Integer current = this.timesReported.get(uuid);
		if (current != null) {
			this.timesReported.put(uuid, current + 1);
		} else {
			this.timesReported.put(uuid, 1);
		}

		// Check if 30 minutes passed for any of these
		Set<Long> remove = new HashSet<>();
		for (Long timeStamp : this.lastReportTimestamp.keySet()) {
			if (timeStamp + 900000 <= System.currentTimeMillis()) {
				remove.add(timeStamp);

				// counter--
				UUID uuid2 = this.lastReportTimestamp.get(timeStamp);
				this.timesReported.put(uuid2, this.timesReported.get(uuid2) - 1);
			}
		}

		// Remove the expired records
		for (Long timeStamp : remove) {
			this.lastReportTimestamp.remove(timeStamp);
		}

		this.lastReportTimestamp.put(System.currentTimeMillis(), uuid);
	}

	public void saveReport(final String reporterUUID, final String reportedUUID, final String message) {
		String query = "INSERT INTO reports (time, server_name, reporter, reported, reason) VALUES (?, ?, ?, ?, ?);";
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);

			ps.setTimestamp(1, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(2, Gberry.serverName.toUpperCase());
			ps.setString(3, reporterUUID);
			ps.setString(4, reportedUUID);
			ps.setString(5, message);

			Gberry.executeUpdate(connection, ps);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(ps, connection);
		}
	}

	public class Report {

		private Long timeStamp = 0L;

		private String reportedName;
		private UUID reportedUUID;

		public Report(String reportedName, UUID reportedUUID) {
			this.reportedName = reportedName;
			this.reportedUUID = reportedUUID;
		}

		public Long getTimeStamp() {
			return timeStamp;
		}

		public void setTimeStamp(Long timeStamp) {
			this.timeStamp = timeStamp;
		}

		public String getReportedName() {
			return reportedName;
		}

		public UUID getReportedUUID() {
			return reportedUUID;
		}

	}

	public class ReportInventory {

		private SmellyInventory smellyInventory;

		public ReportInventory() {
			SmellyInventory smellyInventory = new SmellyInventory(new ReportScreenHandler(), 18,
					ChatColor.AQUA + ChatColor.BOLD.toString() + "Select Report Reason");

			// UHC only
			if (BanManager.plugin.getServer().getPluginManager().getPlugin("BadlionUHC") != null) {
				smellyInventory.getMainInventory().addItem(this.createItem("IPvP", Material.LAVA_BUCKET));
			}

			smellyInventory.getMainInventory().addItem(this.createItem("Kill Aura/Forcefield", Material.IRON_SWORD),
					this.createItem("Anti-Knockback", Material.LEASH),
					this.createItem("Flying", Material.GHAST_TEAR), this.createItem("Speed Hacks", Material.POTION, (short) 8258),
					this.createItem("Aimbot", Material.COMPASS), this.createItem("AutoPot/AutoSoup", Material.MUSHROOM_SOUP),
					this.createItem("Fast Bow", Material.BOW), this.createItem("Fast Eat", Material.APPLE),
					this.createItem("Camping", Material.ENDER_PEARL), this.createItem("Teaming (FFA)", Material.REDSTONE_COMPARATOR),
					this.createItem("Chat", Material.SIGN), this.createItem("Inappropriate Content", Material.NAME_TAG));

			// UHC only
			if (Gberry.serverName.contains("uhc")) {
				smellyInventory.getMainInventory().addItem(this.createItem("Excessive Stalking", Material.SUGAR_CANE),
						this.createItem("X-Ray", Material.DIAMOND));
			}

			// Always add other last
			smellyInventory.getMainInventory().addItem(this.createItem("Other", Material.BOAT));

			// Override the default close item
			ItemStack cancelReportItem = new ItemStack(Material.WOOL, 1, (short) 14);
			ItemMeta cancelInventoryItemMeta = cancelReportItem.getItemMeta();
			cancelInventoryItemMeta.setDisplayName(ChatColor.GREEN + "Cancel");
			cancelReportItem.setItemMeta(cancelInventoryItemMeta);

			smellyInventory.getMainInventory().setItem(smellyInventory.getMainInventory().getSize() - 1, cancelReportItem);

			this.smellyInventory = smellyInventory;
		}

		private ItemStack createItem(String displayName, Material material) {
			return this.createItem(displayName, material, (short) 0);
		}

		private ItemStack createItem(String displayName, Material material, short data) {
			ItemStack item = new ItemStack(material, 1, data);
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName(ChatColor.GREEN + displayName);
			item.setItemMeta(itemMeta);
			return item;
		}

		public void openInventory(final Player player) {
			BukkitUtil.openInventory(player, ReportInventory.this.smellyInventory.getMainInventory());
		}

		public class ReportScreenHandler implements SmellyInventory.SmellyInventoryHandler {

			@Override
			public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, final Player player, InventoryClickEvent event, ItemStack item, int slot) {
				// Did they click on a reason?
				if (slot != fakeHolder.getInventory().getSize() - 1) {
					final Report report = ReportCommand.this.reportCooldown.get(player.getUniqueId());
					final String reason = item.getItemMeta().getDisplayName().substring(2);

					// Generate message
					StringBuilder sb = new StringBuilder();
					sb.append(ChatColor.DARK_PURPLE);
					sb.append("[");
					sb.append("Report");
					sb.append("]");
					sb.append(ChatColor.GOLD);
					sb.append(" (");
					sb.append(Gberry.serverName.toUpperCase());
					sb.append(") ");
					sb.append(ChatColor.RED);
					sb.append(player.getName());
					sb.append(ChatColor.GREEN);
					sb.append(" has reported ");
					sb.append(ChatColor.RED);
					sb.append(report.getReportedName());
					sb.append(ChatColor.GREEN);
					sb.append(" for ");
					sb.append(ChatColor.AQUA);
					sb.append(reason);

					// Store player report count
					ReportCommand.this.handleTimesReported(report.getReportedUUID());

					Integer numTimesReported = ReportCommand.this.timesReported.get(report.getReportedUUID());
					if (numTimesReported != null && numTimesReported != 1) {
						sb.append(ChatColor.RESET);
						sb.append(ChatColor.BOLD);
						sb.append(ChatColor.YELLOW);
						sb.append(" [");
						sb.append(numTimesReported);
						sb.append("x]");
					}

					final String message = sb.toString();

					player.sendMessage(ChatColor.GREEN + "Thank you for your report, it will be handled soon.");

					for (Player pl : SmellyChat.getInstance().getMods()) {
						if (pl.hasPermission(SmellyChat.getInstance().getReportMessagePermission())) {
							if (!ReportCommand.REPORTS_DISABLED.contains(pl.getUniqueId()) || ReportCommand.ACCEPTING_ALL_REPORTS.contains(pl.getUniqueId())) {
								pl.sendMessage(message);
							}
						}
					}

					// Has this player been reported at least once?
					if (Gberry.serverName.contains("arena")) {
						if (numTimesReported != null && numTimesReported > 1) {
							// Send across network
							SmellyChat.getInstance().networkBroadcast(message, "Report");
						}
					} else {
						// Send across network
						SmellyChat.getInstance().networkBroadcast(message, "Report");
					}

					// Internal management stuff
					report.setTimeStamp(System.currentTimeMillis());
					ReportCommand.this.playerReportCooldown.put(player.getUniqueId(), report);

					// Save in database
					new BukkitRunnable() {
						@Override
						public void run() {
							ReportCommand.this.saveReport(player.getUniqueId().toString(), report.getReportedUUID().toString(), reason);
						}
					}.runTaskAsynchronously(SmellyChat.getInstance());
				}

				BukkitUtil.closeInventory(player);
			}

			@Override
			public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {
			}

		}

	}

}
