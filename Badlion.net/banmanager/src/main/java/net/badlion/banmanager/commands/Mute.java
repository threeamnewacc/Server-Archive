package net.badlion.banmanager.commands;

import com.google.common.base.Joiner;
import net.badlion.banmanager.BanManager;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellyinventory.SmellyInventory;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Mute implements CommandExecutor {

	public enum MUTE_REASON { TOXIC_BEHAVIOR, RACISM, HACKUSATION, SPAM, DISRESPECT, BAD_SPORTSMANSHIP, FILTER_BYPASS, SUICIDE_ENCOURAGEMENT, HOMOPHOBIC_SLURS, INAPPROPRIATE_CONTENT, DEATH_THREATS}

	public static final Map<MUTE_REASON, List<Integer>> punishmentTimes = new HashMap<>();
	public static final List<MUTE_REASON> exemptSixMonthTimes = new ArrayList<>();

	static {
		final int MIN_15 = 60 * 15;
		final int HOUR = 60 * 60;
		final int DAY = 60 * 60 * 24;
		final int WEEK = 60 * 60 * 24 * 7;
		final int MONTH = 60 * 60 * 24 * 31;
		final int MONTH_3 = 60 * 60 * 24 * 92;
		final int MONTH_6 = 60 * 60 * 24 * 183;

		punishmentTimes.put(MUTE_REASON.TOXIC_BEHAVIOR, new ArrayList<Integer>(){{add(MIN_15); add(DAY); add(WEEK); add(MONTH);}});
		punishmentTimes.put(MUTE_REASON.RACISM, new ArrayList<Integer>(){{add(MIN_15); add(DAY); add(WEEK); add(MONTH);}});
		punishmentTimes.put(MUTE_REASON.HACKUSATION, new ArrayList<Integer>(){{add(MIN_15); add(HOUR); add(DAY); add(WEEK);}});
		punishmentTimes.put(MUTE_REASON.SPAM, new ArrayList<Integer>(){{add(MIN_15); add(HOUR); add(DAY); add(WEEK);}});
		punishmentTimes.put(MUTE_REASON.DISRESPECT, new ArrayList<Integer>(){{add(MIN_15); add(HOUR); add(DAY); add(WEEK);}});
		punishmentTimes.put(MUTE_REASON.BAD_SPORTSMANSHIP, new ArrayList<Integer>(){{add(MIN_15); add(HOUR); add(DAY); add(WEEK);}});
		punishmentTimes.put(MUTE_REASON.FILTER_BYPASS, new ArrayList<Integer>(){{add(MIN_15); add(DAY); add(WEEK); add(MONTH);}});
		punishmentTimes.put(MUTE_REASON.SUICIDE_ENCOURAGEMENT, new ArrayList<Integer>(){{add(WEEK); add(MONTH); add(MONTH_3); add(MONTH_6);}});
		punishmentTimes.put(MUTE_REASON.HOMOPHOBIC_SLURS, new ArrayList<Integer>(){{add(MIN_15); add(DAY); add(WEEK); add(MONTH);}});
		punishmentTimes.put(MUTE_REASON.INAPPROPRIATE_CONTENT, new ArrayList<Integer>(){{add(WEEK); add(MONTH); add(MONTH_3); add(MONTH_6);}});
		punishmentTimes.put(MUTE_REASON.DEATH_THREATS, new ArrayList<Integer>(){{add(WEEK); add(MONTH); add(MONTH_3); add(MONTH_6);}});

		exemptSixMonthTimes.add(MUTE_REASON.SUICIDE_ENCOURAGEMENT);
		exemptSixMonthTimes.add(MUTE_REASON.INAPPROPRIATE_CONTENT);
		exemptSixMonthTimes.add(MUTE_REASON.DEATH_THREATS);
	}

	protected static Map<UUID, MuteInfo> muteInfos = new HashMap<>();
	private BanManager plugin;
	private MuteInventory muteInventory;

	public Mute(BanManager plugin) {
		this.plugin = plugin;
		this.muteInventory = new MuteInventory();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Correct usage is /mute <username> <reason> or /mute <username> <time> <reason>");
			return true;
		}

		long timeToPunish = this.plugin.getTimeToPunish(args, sender, BanManager.PUNISHMENT_TYPE.MUTE);
		if (timeToPunish <= -10) {
			return true;
		}

		if (!sender.hasPermission("badlion.globalmod") && timeToPunish != -1) {
			sender.sendMessage(ChatColor.RED + "Only senior mods or higher can specify a custom time");
			return true;
		}

		String reason = "";

		if (timeToPunish == -1) {
			reason = ""; // Gets specified later
		} else if (args.length > 2) {
			reason = Joiner.on(" ").skipNulls().join(Arrays.copyOfRange(args, 2, args.length));
		}

		if (sender instanceof Player) {
			Player player = (Player) sender;
			Mute.muteInfos.put(player.getUniqueId(), new MuteInfo(args[0], timeToPunish, reason));
			this.muteInventory.openInventory(player);
		} else {
			// Call directly
			Mute.this.plugin.insertPunishment(sender, BanManager.CONSOLE_SENDER, BanManager.PUNISHMENT_TYPE.MUTE, args[0], reason, timeToPunish);
		}

		return true;
	}

	public class MuteInfo {

		private String beingMuted;
		private String reason;
		private long time;

		public MuteInfo(String beingMuted, long time, String reason) {
			this.beingMuted = beingMuted;
			this.time = time;
			this.reason = reason;
		}

		public String getBeingMuted() {
			return beingMuted;
		}

		public String getReason() {
			return reason;
		}

		public long getTime() {
			return time;
		}
	}


	public class MuteInventory {

		private SmellyInventory smellyInventory;

		public MuteInventory() {
			SmellyInventory smellyInventory = new SmellyInventory(new MuteScreenHandler(), 18,
					ChatColor.AQUA + ChatColor.BOLD.toString() + "Select Mute Reason");

			ItemStack item2 = new ItemStack(Material.LEASH);
			ItemMeta itemMeta2 = item2.getItemMeta();
			itemMeta2.setDisplayName(ChatColor.GREEN + "Racism");
			item2.setItemMeta(itemMeta2);
			smellyInventory.getMainInventory().addItem(item2);

			ItemStack item3 = new ItemStack(Material.DIAMOND_SWORD);
			ItemMeta itemMeta3 = item3.getItemMeta();
			itemMeta3.setDisplayName(ChatColor.GREEN + "Hackusation");
			item3.setItemMeta(itemMeta3);
			smellyInventory.getMainInventory().addItem(item3);

			ItemStack item4 = new ItemStack(Material.SIGN);
			ItemMeta itemMeta4 = item4.getItemMeta();
			itemMeta4.setDisplayName(ChatColor.GREEN + "Spam");
			item4.setItemMeta(itemMeta4);
			smellyInventory.getMainInventory().addItem(item4);

			ItemStack item5 = new ItemStack(Material.POTION, 1, (short) 8258);
			ItemMeta itemMeta5 = item5.getItemMeta();
			itemMeta5.setDisplayName(ChatColor.GREEN + "Bad Sportsmanship");
			item5.setItemMeta(itemMeta5);
			smellyInventory.getMainInventory().addItem(item5);

			ItemStack item6 = new ItemStack(Material.POTION, 1, (short) 8260);
			ItemMeta itemMeta6 = item6.getItemMeta();
			itemMeta6.setDisplayName(ChatColor.GREEN + "Toxic Behavior");
			item6.setItemMeta(itemMeta6);
			smellyInventory.getMainInventory().addItem(item6);

			ItemStack item7 = new ItemStack(Material.OBSIDIAN);
			ItemMeta itemMeta7 = item7.getItemMeta();
			itemMeta7.setDisplayName(ChatColor.GREEN + "Disrespect");
			item7.setItemMeta(itemMeta7);
			smellyInventory.getMainInventory().addItem(item7);

			ItemStack item8 = new ItemStack(Material.POTION, 1, (short) 8236);
			ItemMeta itemMeta8 = item8.getItemMeta();
			itemMeta8.setDisplayName(ChatColor.GREEN + "Death Threats");
			item8.setItemMeta(itemMeta8);
			smellyInventory.getMainInventory().addItem(item8);

			ItemStack item10 = new ItemStack(Material.REDSTONE_TORCH_ON);
			ItemMeta itemMeta10 = item10.getItemMeta();
			itemMeta10.setDisplayName(ChatColor.GREEN + "Filter Bypass");
			item10.setItemMeta(itemMeta10);
			smellyInventory.getMainInventory().addItem(item10);

			ItemStack item11 = new ItemStack(Material.TNT);
			ItemMeta itemMeta11 = item11.getItemMeta();
			itemMeta11.setDisplayName(ChatColor.GREEN + "Homophobic Slurs");
			item11.setItemMeta(itemMeta11);
			smellyInventory.getMainInventory().addItem(item11);

			ItemStack item15 = new ItemStack(Material.DIAMOND_SPADE);
			ItemMeta itemMeta15 = item15.getItemMeta();
			itemMeta15.setDisplayName(ChatColor.GREEN + "Inappropriate Content");
			item15.setItemMeta(itemMeta15);
			smellyInventory.getMainInventory().addItem(item15);

			smellyInventory.getMainInventory().addItem(ItemStackUtil.createItem(Material.POTION, (short) 8236, ChatColor.GREEN + "Suicide Encouragement"));

			ItemStack item9 = new ItemStack(Material.BOAT);
			ItemMeta itemMeta9 = item9.getItemMeta();
			itemMeta9.setDisplayName(ChatColor.GREEN + "Other");
			item9.setItemMeta(itemMeta9);
			smellyInventory.getMainInventory().addItem(item9);

			// UHC only
			if (Gberry.serverName.contains("uhc")) {

			}

			// Override the default close item
			ItemStack cancelReportItem = new ItemStack(Material.WOOL, 1, (short) 14);
			ItemMeta cancelInventoryItemMeta = cancelReportItem.getItemMeta();
			cancelInventoryItemMeta.setDisplayName(ChatColor.GREEN + "Cancel");
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
							player.openInventory(MuteInventory.this.smellyInventory.getMainInventory());
						}
					}
				});
			} else {
				player.openInventory(this.smellyInventory.getMainInventory());
			}
		}

		public class MuteScreenHandler implements SmellyInventory.SmellyInventoryHandler {

			@Override
			public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, final Player player, InventoryClickEvent event, ItemStack item, int slot) {
				if (!player.hasPermission("bm.mute")) {
					player.closeInventory();
					return;
				}

				// Did they click on a reason?
				if (slot != fakeHolder.getInventory().getSize() - 1) {
					MuteInfo muteInfo = Mute.muteInfos.remove(player.getUniqueId());
					String reason = item.getItemMeta().getDisplayName().substring(2);
					if (reason.equals("Other")) {
						if (!player.hasPermission("badlion.globalmod")) {
							player.sendMessage(ChatColor.RED + "Only senior mods and higher can use other");
							player.closeInventory();
							return;
						}

						reason = muteInfo.getReason();

						if (reason.replace(" ", "").length() == 0) {
							player.sendMessage(ChatColor.RED + "You must specify a mute reason if you choose other");
							player.closeInventory();
							return;
						}
					}

					Mute.this.plugin.insertPunishment(player, player.getUniqueId().toString(), BanManager.PUNISHMENT_TYPE.MUTE, muteInfo.getBeingMuted(), reason, muteInfo.getTime());
				} else { // Cancel the report
					Mute.muteInfos.remove(player.getUniqueId());
				}

				player.closeInventory(); // Do this last so we don't remove our stuff early
			}

			@Override
			public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {
				Mute.muteInfos.remove(player.getUniqueId());
			}

		}
	}

}
