package net.badlion.arenalobby.inventories.lobby;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.helpers.KitInventoryHelper;
import net.badlion.arenalobby.managers.LadderManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventQueueInventory {

	private static SmellyInventory smellyInventory;

	private static Map<Integer, ArenaCommon.EventType> eventTypeMap = new HashMap<>();

	public static void initialize() {
		SmellyInventory smellyInventory = new SmellyInventory(new EventQueueClickHandler(), 18,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Event Queues");

		smellyInventory.getFakeHolder().setParentInventory(SettingsInventory.smellyInventory.getMainInventory());
		// Fill with the kit rule set items
		EventQueueInventory.smellyInventory = smellyInventory;
		EventQueueInventory.updateQueueInventory();
	}

	public static void openEventQueueInventory(Player player) {
		BukkitUtil.openInventory(player, EventQueueInventory.smellyInventory.getMainInventory());
	}

	public static void updateQueueInventory() {
		EventQueueInventory.eventTypeMap.clear();

		EventQueueInventory.smellyInventory.getMainInventory().clear();

		// Add queue items
		ItemStack item = ItemStackUtil.createItem(Material.DIAMOND_SWORD, ChatColor.GREEN + "UHC Meetup");
		ItemMeta itemMeta = item.getItemMeta();
		List<String> itemLore = new ArrayList<>();
		itemLore.add(ChatColor.BLUE + "In queue: " + LadderManager.uhcMeetupQueue.getInQueue());
		itemLore.add(ChatColor.BLUE + "In game: " + LadderManager.uhcMeetupQueue.getInGame());
		itemLore.add("");
		itemLore.add(ChatColor.YELLOW + "Middle click to preview kit");
		itemMeta.setLore(itemLore);
		item.setItemMeta(itemMeta);

		EventQueueInventory.smellyInventory.getMainInventory().setItem(0, item);

		EventQueueInventory.eventTypeMap.put(0, ArenaCommon.EventType.UHCMEETUP);

		EventQueueInventory.smellyInventory.getMainInventory().setItem(17, SmellyInventory.getCloseInventoryItem());
	}

	private static class EventQueueClickHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			Group group = ArenaLobby.getInstance().getPlayerGroup(player);
			if (item != null){
				if (EventQueueInventory.eventTypeMap.containsKey(slot)){
					if (event.getClick().equals(ClickType.MIDDLE)) {
						KitInventoryHelper.openKitPreviewInventory(fakeHolder.getSmellyInventory(), event.getView().getTopInventory(), player, LadderManager.uhcMeetupQueue.getKitRuleSet());
						return;
					}

					LadderManager.joinEventQueue(group, player, EventQueueInventory.eventTypeMap.get(slot));

					BukkitUtil.closeInventory(player);
				}
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}
}
