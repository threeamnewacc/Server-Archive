package net.badlion.arenapvp.inventory;

import net.badlion.arenacommon.kits.Kit;
import net.badlion.arenacommon.kits.KitCommon;
import net.badlion.arenacommon.kits.KitType;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.PotPvPPlayer;
import net.badlion.arenapvp.Team;
import net.badlion.arenapvp.helper.KitInventoryHelper;
import net.badlion.arenapvp.manager.MatchManager;
import net.badlion.arenapvp.manager.PotPvPPlayerManager;
import net.badlion.arenapvp.matchmaking.Match;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class CustomKitCreationInventories {


	public static void openCustomKitLoadInventory(Player player) {
		// Create select custom kit load inventory
		SmellyInventory selectCustomKitLoadInventory = new SmellyInventory(new CustomKitCreationLoadScreenHandler(),
				27, ChatColor.AQUA + ChatColor.BOLD.toString() + "Select Custom Kit to Load");

		KitType kitType = new KitType(player.getUniqueId().toString(), KitRuleSet.customRuleSet.getName());
		Map<KitType, List<Kit>> kitTypeListMap = KitCommon.inventories.get(player.getUniqueId());
		if (kitTypeListMap != null) {
			List<Kit> kits = kitTypeListMap.get(kitType);
			if (kits != null) {
				for (Kit kit : kits) {
					selectCustomKitLoadInventory.getMainInventory().addItem(KitInventoryHelper.createCustomKitInventoryItem(kit.getId() + 1));
				}
			}
		}
		BukkitUtil.openInventory(player, selectCustomKitLoadInventory.getMainInventory());
	}

	private static class CustomKitCreationLoadScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			if (event.getClick().equals(ClickType.MIDDLE)) { // Preview kit
				KitInventoryHelper.openKitPreviewInventory(fakeHolder.getSmellyInventory(), event.getView().getTopInventory(),
						player, item);
			} else {
				event.setCancelled(true);

				PotPvPPlayer potPvPPlayer = PotPvPPlayerManager.getPotPvPPlayer(player.getUniqueId());
				Team team = ArenaPvP.getInstance().getPlayerTeam(player);
				Match match = MatchManager.getActiveMatches().get(team);
				if (item == null) {
					return;
				}
				if (match != null) {
					if (potPvPPlayer.isSelectingKit()) {
						KitCommon.loadKit(player, KitRuleSet.customRuleSet, (KitInventoryHelper.getCustomKitNumberFromItem(item) - 1));
						potPvPPlayer.setSelectingKit(false);
					}
				}
				BukkitUtil.closeInventory(player);
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}


}
