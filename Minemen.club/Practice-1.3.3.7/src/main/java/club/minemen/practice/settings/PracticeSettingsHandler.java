package club.minemen.practice.settings;

import club.minemen.core.CorePlugin;
import club.minemen.core.inventory.InventoryUI;
import club.minemen.core.mineman.Mineman;
import club.minemen.core.rank.Rank;
import club.minemen.core.settings.SettingsHandler;
import club.minemen.core.util.finalutil.CC;
import club.minemen.core.util.finalutil.ItemUtil;
import club.minemen.practice.Practice;
import club.minemen.practice.player.PlayerData;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PracticeSettingsHandler implements SettingsHandler {

	private static final List<Integer> PING_RANGES = Arrays.asList(
			50, 75, 100, 125, 150, 200, 250, 300, -1
	);

	private static final List<Integer> ELO_RANGES = Arrays.asList(
			250, 350, 500, 600, 750, 1000, -1
	);

	private final Practice plugin = Practice.getInstance();

	@Override
	public void onCreateSettings(InventoryUI inventoryUI, Player player) {
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
		Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
		inventoryUI.addItem(new InventoryUI.AbstractClickableItem(
				ItemUtil.createItem(Material.REDSTONE,
						CC.PRIMARY + "Allowing Spectators: "
								+ (playerData.isAllowingSpectators() ? CC.GREEN + "Enabled" : CC.RED + "Disabled"))) {
			@Override
			public void onClick(InventoryClickEvent inventoryClickEvent) {
				player.performCommand("tsp");
				player.closeInventory();
			}
		});
		inventoryUI.addItem(new InventoryUI.AbstractClickableItem(
				ItemUtil.createItem(Material.DIAMOND_SWORD,
						CC.PRIMARY + "Allowing Duels: "
								+ (playerData.isAcceptingDuels() ? CC.GREEN + "Enabled" : CC.RED + "Disabled"))) {
			@Override
			public void onClick(InventoryClickEvent inventoryClickEvent) {
				player.performCommand("td");
				player.closeInventory();
			}
		});
		inventoryUI.addItem(new InventoryUI.AbstractClickableItem(
				ItemUtil.createItem(Material.MAP,
						CC.PRIMARY + "Sidebar Visibility: "
								+ (playerData.isScoreboardEnabled() ? CC.GREEN + "Enabled" : CC.RED + "Disabled"))) {
			@Override
			public void onClick(InventoryClickEvent inventoryClickEvent) {
				player.performCommand("tsb");
				player.closeInventory();
			}
		});
		inventoryUI.addItem(new InventoryUI.AbstractClickableItem(
				ItemUtil.createItem(Material.ENCHANTED_BOOK,
						CC.PRIMARY + "Matchmaking Settings")) {
			@Override
			public void onClick(InventoryClickEvent inventoryClickEvent) {
				if (!mineman.hasRank(Rank.CLUBBER)) {
					player.closeInventory();
					player.sendMessage(CC.RED + "Matchmaking Settings are for Clubber rank and higher.");
					return;
				}

				player.closeInventory();
				PracticeSettingsHandler.this.openMatchmakingSettings(player, playerData, mineman);
			}
		});
	}

	private void openMatchmakingSettings(Player player, PlayerData playerData, Mineman mineman) {
		InventoryUI matchmakingUI = new InventoryUI(CC.PRIMARY + "Matchmaking Settings", false, 1);

		matchmakingUI.addItem(new InventoryUI.AbstractClickableItem(
				ItemUtil.createItem(Material.STICK, CC.PRIMARY + "Ping Range: " + CC.SECONDARY
						+ (playerData.getPingRange() == -1 ? "Unrestricted" : playerData.getPingRange()))) {
			@Override
			public void onClick(InventoryClickEvent event) {
				if (!mineman.hasRank(Rank.PARTYMAN)) {
					player.sendMessage(CC.RED + "Ping-based Matchmaking Settings are for Partyman rank and higher.");
					player.closeInventory();
					return;
				}

				String[] args = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).split(":");
				int range = PracticeSettingsHandler.this.handleRangeClick(event.getClick(),
						PracticeSettingsHandler.PING_RANGES, PracticeSettingsHandler.this.parseOrDefault(args[1], -1));

				playerData.setPingRange(range);
				event.getClickedInventory().setItem(0, ItemUtil.createItem(Material.STICK, CC.PRIMARY + "Ping Range: " + CC.SECONDARY
						+ (playerData.getPingRange() == -1 ? "Unrestricted" : playerData.getPingRange())));
			}
		});

		matchmakingUI.addItem(new InventoryUI.AbstractClickableItem(
				ItemUtil.createItem(Material.BLAZE_ROD, CC.PRIMARY + "ELO Range: " + CC.SECONDARY
						+ (playerData.getEloRange() == -1 ? "Unrestricted" : playerData.getEloRange()))) {
			@Override
			public void onClick(InventoryClickEvent event) {
				String[] args = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).split(":");
				int range = PracticeSettingsHandler.this.handleRangeClick(event.getClick(),
						PracticeSettingsHandler.ELO_RANGES, PracticeSettingsHandler.this.parseOrDefault(args[1], -1));

				playerData.setEloRange(range);
				event.getClickedInventory().setItem(1, ItemUtil.createItem(Material.BLAZE_ROD, CC.PRIMARY + "ELO Range: " + CC.SECONDARY
						+ (playerData.getEloRange() == -1 ? "Unrestricted" : playerData.getEloRange())));
			}
		});

		player.openInventory(matchmakingUI.getCurrentPage());
	}

	private int handleRangeClick(ClickType clickType, List<Integer> ranges, int current) {
		int min = ranges.get(0);
		int max = ranges.get(ranges.size() - 1);

		if (clickType == ClickType.LEFT) {
			if (current == max) {
				current = min;
			} else {
				current = ranges.get(ranges.indexOf(current) + 1);
			}
		} else if (clickType == ClickType.RIGHT) {
			if (current == min) {
				current = max;
			} else {
				current = ranges.get(ranges.indexOf(current) - 1);
			}
		}

		return current;
	}

	private int parseOrDefault(String string, int def) {
		try {
			return Integer.parseInt(string.replace(" ", ""));
		} catch (NumberFormatException e) {
			return def;
		}
	}
}