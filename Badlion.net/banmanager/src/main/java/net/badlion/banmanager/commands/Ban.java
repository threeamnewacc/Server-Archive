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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Ban implements CommandExecutor {

	protected static Map<UUID, BanInfo> banInfos = new HashMap<>();
	private BanManager plugin;
	private BanInventory banInventory;

	public Ban(BanManager plugin) {
		this.plugin = plugin;
		this.banInventory = new BanInventory();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Correct usage is /ban <username> <reason> or /ban <username> <time> <reason>");
			return true;
		}

		long timeToPunish = this.plugin.getTimeToPunish(args, sender, BanManager.PUNISHMENT_TYPE.BAN);
		if (timeToPunish <= -10) {
			return true;
		}

		String reason = "";
		if (timeToPunish == BanManager.PERMANENT_TIME) {
			reason = Joiner.on(" ").skipNulls().join(Arrays.copyOfRange(args, 1, args.length));
		} else {
			reason = Joiner.on(" ").skipNulls().join(Arrays.copyOfRange(args, 2, args.length));
		}

		if (sender instanceof Player) {
			Player player = (Player) sender;
			Ban.banInfos.put(player.getUniqueId(), new BanInfo(args[0], timeToPunish, reason));
			this.banInventory.openInventory(player);
		} else {
			// Call directly
			Ban.this.plugin.insertPunishment(sender, BanManager.CONSOLE_SENDER, BanManager.PUNISHMENT_TYPE.BAN, args[0], reason, timeToPunish);
		}

		return true;
	}

	public class BanInfo {

		private String beingBanned;
		private String reason;
		private long time;

		public BanInfo(String beingBanned, long time, String reason) {
			this.beingBanned = beingBanned;
			this.time = time;
			this.reason = reason;
		}

		public String getBeingBanned() {
			return beingBanned;
		}

		public String getReason() {
			return reason;
		}

		public long getTime() {
			return time;
		}
	}


	public class BanInventory {

		private SmellyInventory smellyInventory;

		public BanInventory() {
			SmellyInventory smellyInventory = new SmellyInventory(new BanScreenHandler(), 27,
					ChatColor.AQUA + ChatColor.BOLD.toString() + "Select Ban Reason");

			// Kill Aura
			ItemStack item = new ItemStack(Material.IRON_SWORD);
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName(ChatColor.GREEN + "Kill Aura/Forcefield");
			item.setItemMeta(itemMeta);
			smellyInventory.getMainInventory().addItem(item);

			// Anti-KB
			ItemStack item2 = new ItemStack(Material.LEASH);
			ItemMeta itemMeta2 = item2.getItemMeta();
			itemMeta2.setDisplayName(ChatColor.GREEN + "Anti-Knockback");
			item2.setItemMeta(itemMeta2);
			smellyInventory.getMainInventory().addItem(item2);

			ItemStack item4 = new ItemStack(Material.GHAST_TEAR);
			ItemMeta itemMeta4 = item4.getItemMeta();
			itemMeta4.setDisplayName(ChatColor.GREEN + "Flying");
			item4.setItemMeta(itemMeta4);
			smellyInventory.getMainInventory().addItem(item4);

			ItemStack item5 = new ItemStack(Material.POTION, 1, (short) 8258);
			ItemMeta itemMeta5 = item5.getItemMeta();
			itemMeta5.setDisplayName(ChatColor.GREEN + "Speed");
			item5.setItemMeta(itemMeta5);
			smellyInventory.getMainInventory().addItem(item5);

			ItemStack item6 = new ItemStack(Material.BOW);
			ItemMeta itemMeta6 = item6.getItemMeta();
			itemMeta6.setDisplayName(ChatColor.GREEN + "Fast Bow");
			item6.setItemMeta(itemMeta6);
			smellyInventory.getMainInventory().addItem(item6);

			ItemStack item7 = new ItemStack(Material.APPLE);
			ItemMeta itemMeta7 = item7.getItemMeta();
			itemMeta7.setDisplayName(ChatColor.GREEN + "Fast Eat");
			item7.setItemMeta(itemMeta7);
			smellyInventory.getMainInventory().addItem(item7);

			ItemStack item8 = new ItemStack(Material.COMPASS);
			ItemMeta itemMeta8 = item8.getItemMeta();
			itemMeta8.setDisplayName(ChatColor.GREEN + "Aimbot");
			item8.setItemMeta(itemMeta8);
			smellyInventory.getMainInventory().addItem(item8);

			ItemStack item9 = new ItemStack(Material.BOAT);
			ItemMeta itemMeta9 = item9.getItemMeta();
			itemMeta9.setDisplayName(ChatColor.GREEN + "Other");
			item9.setItemMeta(itemMeta9);
			smellyInventory.getMainInventory().addItem(item9);

			ItemStack item10 = new ItemStack(Material.MUSHROOM_SOUP);
			ItemMeta itemMeta10 = item10.getItemMeta();
			itemMeta10.setDisplayName(ChatColor.GREEN + "AutoPot/AutoSoup");
			item10.setItemMeta(itemMeta10);
			smellyInventory.getMainInventory().addItem(item10);

			ItemStack item11 = new ItemStack(Material.DIAMOND);
			ItemMeta itemMeta11 = item11.getItemMeta();
			itemMeta11.setDisplayName(ChatColor.GREEN + "X-Ray");
			item11.setItemMeta(itemMeta11);
			smellyInventory.getMainInventory().addItem(item11);

			ItemStack item12 = new ItemStack(Material.COBBLESTONE);
			ItemMeta itemMeta12 = item12.getItemMeta();
			itemMeta12.setDisplayName(ChatColor.GREEN + "Skybasing");
			item12.setItemMeta(itemMeta12);
			smellyInventory.getMainInventory().addItem(item12);

			ItemStack item13 = new ItemStack(Material.DAYLIGHT_DETECTOR);
			ItemMeta itemMeta13 = item13.getItemMeta();
			itemMeta13.setDisplayName(ChatColor.GREEN + "Bug Abuse");
			item13.setItemMeta(itemMeta13);
			smellyInventory.getMainInventory().addItem(item13);

			ItemStack item15 = new ItemStack(Material.DIAMOND_SPADE);
			ItemMeta itemMeta15 = item15.getItemMeta();
			itemMeta15.setDisplayName(ChatColor.GREEN + "Inappropriate Content");
			item15.setItemMeta(itemMeta15);
			smellyInventory.getMainInventory().addItem(item15);

			smellyInventory.getMainInventory().addItem(ItemStackUtil.createItem(Material.POTION, (short) 8236, ChatColor.GREEN + "Death Threats"));

			smellyInventory.getMainInventory().addItem(ItemStackUtil.createItem(Material.POTION, (short) 8236, ChatColor.GREEN + "Suicide Encouragement"));

			smellyInventory.getMainInventory().addItem(ItemStackUtil.createItem(Material.PAINTING, ChatColor.GREEN + "Advertising"));

			smellyInventory.getMainInventory().addItem(ItemStackUtil.createItem(Material.MELON, ChatColor.GREEN + "No Slowdown"));

			// UHC only
			if (Gberry.serverName.contains("uhc")) {
				ItemStack item14 = new ItemStack(Material.SIGN);
				ItemMeta itemMeta14 = item14.getItemMeta();
				itemMeta14.setDisplayName(ChatColor.GREEN + "Spoiling");
				item14.setItemMeta(itemMeta14);
				smellyInventory.getMainInventory().addItem(item14);

				ItemStack item16 = new ItemStack(Material.DIAMOND_PICKAXE);
				ItemMeta itemMeta16 = item16.getItemMeta();
				itemMeta16.setDisplayName(ChatColor.GREEN + "Illegal Mining");
				item16.setItemMeta(itemMeta16);
				smellyInventory.getMainInventory().addItem(item16);
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
							player.openInventory(BanInventory.this.smellyInventory.getMainInventory());
						}
					}
				});
			} else {
				player.openInventory(this.smellyInventory.getMainInventory());
			}
		}

		public class BanScreenHandler implements SmellyInventory.SmellyInventoryHandler {

			@Override
			public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, final Player player, InventoryClickEvent event, ItemStack item, int slot) {
				if (!player.hasPermission("bm.ban")) {
					player.closeInventory();
					return;
				}

				Gberry.log("BM", "Processing click for ban with " + player.getName());

				// Did they click on a reason?
				if (slot != fakeHolder.getInventory().getSize() - 1) {
					BanInfo banInfo = Ban.banInfos.remove(player.getUniqueId());
					String reason = item.getItemMeta().getDisplayName().substring(2);
					if (reason.equals("Other")) {
						reason = banInfo.getReason();

						if (reason.replace(" ", "").length() == 0) {
							player.sendMessage(ChatColor.RED + "You must specify a ban reason if you choose other");
							player.closeInventory();
							return;
						}
					}

					Gberry.log("BM", "Inserting ban with " + player.getName());
					Ban.this.plugin.insertPunishment(player, player.getUniqueId().toString(), BanManager.PUNISHMENT_TYPE.BAN, banInfo.getBeingBanned(), reason, banInfo.getTime());
				} else { // Cancel the report
					Ban.banInfos.remove(player.getUniqueId());
				}

				player.closeInventory(); // Do this last so we don't remove our stuff early
			}

			@Override
			public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {
				Ban.banInfos.remove(player.getUniqueId());
			}

		}
	}

}
