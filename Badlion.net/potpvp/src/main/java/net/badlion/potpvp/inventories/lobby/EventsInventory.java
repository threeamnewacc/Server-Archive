package net.badlion.potpvp.inventories.lobby;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.events.Event;
import net.badlion.potpvp.events.Slaughter;
import net.badlion.potpvp.events.War;
import net.badlion.potpvp.exceptions.OutOfArenasException;
import net.badlion.potpvp.helpers.KitHelper;
import net.badlion.potpvp.managers.DonatorManager;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.potpvp.rulesets.BuildUHCRuleSet;
import net.badlion.potpvp.rulesets.EventRuleSet;
import net.badlion.potpvp.rulesets.IronBuildUHCRuleSet;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class EventsInventory {

	private static SmellyInventory smellyInventory;

	private static int eventKitItemSlot = -1;
	private static SmellyInventory.FakeHolder selectKitInventoryFakeHolder;

	public static void initialize() {
		SmellyInventory smellyInventory = new SmellyInventory(new EventsDisplayScreenHandler(), 54,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Events");
		// TODO: PERMA-POTION SCREEN TOO FOR EVENT KITS
		// Fill with default items
		ItemStack startingEventsItem = ItemStackUtil.createItem(Material.PISTON_BASE, ChatColor.GREEN + "Upcoming Events");

		ItemStack startedEventsItem = ItemStackUtil.createItem(Material.PISTON_BASE, ChatColor.GREEN + "Ongoing Events");

		ItemStack startEventItem = ItemStackUtil.createItem(Material.BOOK_AND_QUILL, ChatColor.GREEN + "Start New Event");
		for (int i = 0; i < 9; i++) {
			smellyInventory.getMainInventory().setItem(i, startingEventsItem);
		}

		for (int i = 18; i < 27; i++) {
			smellyInventory.getMainInventory().setItem(i, startedEventsItem);
		}

		smellyInventory.getMainInventory().setItem(45, startEventItem);

		// Create event creation inventory
		Inventory createEventInventory = smellyInventory.createInventory(smellyInventory.getFakeHolder(), new CreateEventScreenHandler(), 45, 18,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Start New Event");

		ItemStack createLMSEventItem = ItemStackUtil.createItem(Material.BOOK_AND_QUILL, ChatColor.GREEN + "Start Last Man Standing (LMS)");

		ItemStack createWarEventItem = ItemStackUtil.createItem(Material.BOOK_AND_QUILL, ChatColor.GREEN + "Start War");

		ItemStack createSlaughterEventItem = ItemStackUtil.createItem(Material.BOOK_AND_QUILL, ChatColor.GREEN + "Start Slaughter");

		ItemStack createInfectionEventItem = ItemStackUtil.createItem(Material.BOOK_AND_QUILL, ChatColor.GREEN + "Start Infection");

		ItemStack createKOTHItem = ItemStackUtil.createItem(Material.BOOK_AND_QUILL, ChatColor.GREEN + "Start KOTH");

		ItemStack createUHCMeetupItem = ItemStackUtil.createItem(Material.BOOK_AND_QUILL, ChatColor.GREEN + "Start UHC Meetup");

		createEventInventory.addItem(createLMSEventItem, createWarEventItem, createSlaughterEventItem, createUHCMeetupItem, createKOTHItem);

		EventsInventory.smellyInventory = smellyInventory;
	}

	public static void fillSelectKitInventory() {
		// Select kit inventory
		Inventory selectKitInventory = smellyInventory.createInventory(
				((SmellyInventory.FakeHolder) EventsInventory.smellyInventory.getFakeHolder().getSubInventory(45).getHolder()),
				new SelectKitScreenHandler(), 0, 27, ChatColor.AQUA + ChatColor.BOLD.toString() + "Select Kit");

		// Fill all kit rule set items
		int currentSlot = 0;
		for (KitRuleSet kitRuleSet : KitRuleSet.getAllKitRuleSets()) {
			if (kitRuleSet.getClass() != KitRuleSet.customRuleSet.getClass()) {
				if (kitRuleSet.isEnabledInEvents()) {
					// Save the event kit item slot for future use
					if (EventsInventory.eventKitItemSlot == -1
							&& kitRuleSet.getClass() == KitRuleSet.eventRuleSet.getClass()) {
						EventsInventory.eventKitItemSlot = currentSlot;
					}

					selectKitInventory.addItem(KitRuleSet.getKitRuleSetItem(kitRuleSet));
					currentSlot++;
				}
			}
		}

		// Store the selectKitInventory holder because we use it frequently
		EventsInventory.selectKitInventoryFakeHolder = (SmellyInventory.FakeHolder) selectKitInventory.getHolder();
	}

	public static Inventory getMainEventsInventory() {
		return EventsInventory.smellyInventory.getMainInventory();
	}

	public static void openEventsInventory(final Player player) {
		// Select event kit inventory
		final Inventory selectEventKitInventory = EventsInventory.smellyInventory.createInventory(EventsInventory.selectKitInventoryFakeHolder,
				new SelectEventKitScreenHandler(), EventsInventory.eventKitItemSlot,
				27, ChatColor.AQUA + ChatColor.BOLD.toString() + "Select Event Kit");

		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				final List<Integer> customKitNumbers = KitHelper.getSavedCustomKitNumbers(player, "Event");
				BukkitUtil.runTask(new Runnable() {
					@Override
					public void run() {
						for (Integer customKitNumber : customKitNumbers) {
							selectEventKitInventory.addItem(KitHelper.createEventKitInventoryItem(customKitNumber));
						}

						BukkitUtil.openInventory(player, EventsInventory.smellyInventory.getMainInventory());
					}
				});
			}
		});

		PotPvPPlayerManager.addDebug(player, "Open events inventory");

		BukkitUtil.openInventory(player, EventsInventory.smellyInventory.getMainInventory());
	}

	private static class EventsDisplayScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent bukkitEvent, ItemStack item, int slot) {
			if (slot == 45) { // Start event item
				// Can player start an event?
				if (DonatorManager.canStartEvent(player)) {
					BukkitUtil.openInventory(player, fakeHolder.getSubInventory(slot));
				} else {
					BukkitUtil.closeInventory(player);

					if (player.hasPermission("badlion.donator")) {
						// Get time remaining and format to HH:MM:SS
						long timeRemaining = DonatorManager.getTimeRemaining(player);
						String str = String.format("%02d:%02d:%02d",
										TimeUnit.MILLISECONDS.toHours(timeRemaining),
										TimeUnit.MILLISECONDS.toMinutes(timeRemaining) -
												TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeRemaining)),
										TimeUnit.MILLISECONDS.toSeconds(timeRemaining) -
												TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeRemaining)));

						player.sendMessage(ChatColor.RED + "You can start another event in " + str);
					}
				}
			} else if (slot > 8 && slot < 18) { // Join event queue
				if (bukkitEvent.getClick().equals(ClickType.MIDDLE)) { // Middle click
					Event event = Event.getEventForItem(item);
					if (event.getKitRuleSet() instanceof EventRuleSet) {
						KitHelper.openEventKitPreviewInventoryForEvent(fakeHolder.getSmellyInventory(), bukkitEvent.getView().getTopInventory(), player, event);
					} else {
						KitHelper.openKitPreviewInventory(fakeHolder.getSmellyInventory(), bukkitEvent.getView().getTopInventory(), player, event.getKitRuleSet());
					}
				} else { // Join event
					BukkitUtil.closeInventory(player);
					player.performCommand("joinevent " + Event.getUUIDForEvent(Event.getEventForItem(item)));
				}
			} else if (slot > 26) {
				if (bukkitEvent.getClick().equals(ClickType.MIDDLE)) { // Middle click
					Event event = Event.getEventForItem(item);

					if (event.getKitRuleSet() instanceof EventRuleSet) {
						KitHelper.openEventKitPreviewInventoryForEvent(fakeHolder.getSmellyInventory(), bukkitEvent.getView().getTopInventory(), player, event);
					} else {
						KitHelper.openKitPreviewInventory(fakeHolder.getSmellyInventory(), bukkitEvent.getView().getTopInventory(), player, event.getKitRuleSet());
					}
				} else {
					BukkitUtil.closeInventory(player);

					player.sendMessage(ChatColor.RED + "Cannot join event, event already started.");
				}
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

	private static class CreateEventScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			if (slot != 2) { // Slaughter
				int nonSlaughterEvents = 0;
				for (Event event2 : Event.getEvents().values()) {
					if (!(event2 instanceof Slaughter)) {
						nonSlaughterEvents++;
					}
				}

				if (nonSlaughterEvents > 2) {
					player.sendMessage(ChatColor.RED + "Events are limited to 3 at a time, excluding Slaughter.");

					BukkitUtil.closeInventory(player);
					return;
				}
			}

			switch(slot) {
				case 0: // LMS
					// Store the event type they're trying to create
					Gberry.log("EVENT", "Selected LMS");
					Event.storeEventCreation(player, Event.EventType.LMS);
					break;
				case 1: // War
					// Store the event type they're trying to create
					Gberry.log("EVENT", "Selected War");
					Event.storeEventCreation(player, Event.EventType.WAR);
					break;
				case 2: // Slaughter
					// Store the event type they're trying to create
					Gberry.log("EVENT", "Selected Slaughter");
					Event.storeEventCreation(player, Event.EventType.SLAUGHTER);
					break;
				case 3: // UHC Meetup
					// UHC Meetup only uses Build UHC kit, so create event now
					Gberry.log("EVENT", "Selected UHC Meetup");
					try {
						BukkitUtil.closeInventory(player);

						Event.createEvent(player, Event.EventType.UHC_MEETUP, KitRuleSet.buildUHCRuleSet, null, null);
					} catch (OutOfArenasException e) {
						player.sendMessage(ChatColor.RED + "Out of arenas for this event game type at the moment. Try later.");
					}
					return;
				case 4: // KOTH
					// Store the event type they're trying to create
					Gberry.log("EVENT", "Selected KOTH");
					Event.storeEventCreation(player, Event.EventType.KOTH);
					break;
				case 5: // Infection
					// Store the event type they're trying to create
					Gberry.log("EVENT", "Selected Infection");
					Event.storeEventCreation(player, Event.EventType.INFECTION);
					break;
			}

			// Open kit selection inventory
			BukkitUtil.openInventory(player, fakeHolder.getSubInventory(0));
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

	private static class SelectKitScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			if (event.getClick().equals(ClickType.MIDDLE)) {
				// Preview kit
				KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(item);
				if (kitRuleSet instanceof EventRuleSet) {
					BukkitUtil.openInventory(player, fakeHolder.getSubInventory(EventsInventory.eventKitItemSlot));
				} else {
					KitHelper.openKitPreviewInventory(fakeHolder.getSmellyInventory(), event.getView().getTopInventory(), player, kitRuleSet);
				}
			} else {
				Event.EventType eventCreating = Event.getEventCreating(player);
				KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(item);

				// Don't let them run events with BuildUHC
				if (kitRuleSet instanceof BuildUHCRuleSet || kitRuleSet instanceof IronBuildUHCRuleSet) {
					Event.removeEventCreating(player);

					BukkitUtil.closeInventory(player);

					player.sendMessage(ChatColor.RED + "This kit is temporarily disabled for this event due to lag issues.");
					return;
				}

				// Check if War/Slaughter with same kit is already running
				for (Event event2 : Event.getEvents().values()) {
					if (eventCreating == Event.EventType.WAR && event2 instanceof War) {
						if (event2.getKitRuleSet() == kitRuleSet) {
							BukkitUtil.closeInventory(player);

							Event.removeEventCreating(player);

							player.sendMessage(ChatColor.RED + "War with kit " + kitRuleSet.getName() + " is already running!");
							return;
						}
					} else if (eventCreating == Event.EventType.SLAUGHTER && event2 instanceof Slaughter) {
						if (event2.getKitRuleSet() == kitRuleSet) {
							BukkitUtil.closeInventory(player);

							Event.removeEventCreating(player);

							player.sendMessage(ChatColor.RED + "Slaughter with kit " + kitRuleSet.getName() + " is already running!");
							return;
						}
					}
				}

				if (kitRuleSet instanceof EventRuleSet) {
					// Hardcodes for events that are not compatible with event kits
					if (eventCreating == Event.EventType.UHC_MEETUP){
						BukkitUtil.closeInventory(player);

						Event.removeEventCreating(player);

						player.sendMessage(ChatColor.RED + "Cannot use event kits for this event!");
						return;
					}

					BukkitUtil.openInventory(player, fakeHolder.getSubInventory(EventsInventory.eventKitItemSlot));
				} else {
					try {
						BukkitUtil.closeInventory(player);

						Event.EventType eventType = Event.getEventCreating(player);

						Event.createEvent(player, eventType, kitRuleSet, null, null);
					} catch (OutOfArenasException e) {
						player.sendMessage(ChatColor.RED + "Out of arenas for this event game type at the moment. Try later.");
					}
				}
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

	private static class SelectEventKitScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			if (event.getClick().equals(ClickType.MIDDLE)) {
				// Preview kit
				KitHelper.openKitPreviewInventory(fakeHolder.getSmellyInventory(), event.getView().getTopInventory(), player, item);
			} else {
				BukkitUtil.closeInventory(player);

				final List<ItemStack[]> contents = KitHelper.getKit(player, KitRuleSet.eventRuleSet, KitHelper.getCustomKitNumberFromItem(item));

				try {
					Event.EventType eventType = Event.getEventCreating(player);

					Event.createEvent(player, eventType, KitRuleSet.eventRuleSet, contents.get(0), contents.get(1));
				} catch (OutOfArenasException e) {
					player.sendMessage(ChatColor.RED + "Out of arenas for this event game type at the moment. Try later.");
				}
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}