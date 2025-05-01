package net.badlion.potpvp.events;

import net.badlion.gberry.Gberry;
import net.badlion.potpvp.Game;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.arenas.Arena;
import net.badlion.potpvp.exceptions.NotEnoughPlayersException;
import net.badlion.potpvp.exceptions.OutOfArenasException;
import net.badlion.potpvp.inventories.lobby.EventsInventory;
import net.badlion.potpvp.inventories.spectator.SpectateEventInventory;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.managers.DonatorManager;
import net.badlion.potpvp.rulesets.EventRuleSet;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.potpvp.tasks.EventTieTask;
import net.badlion.potpvp.tasks.EventTimerTask;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public abstract class Event implements Game {

    public enum EventType {
        LMS("LMS", 30, new ItemStack(Material.DIAMOND_SWORD)),
        SLAUGHTER("Slaughter", 15, new ItemStack(Material.DIAMOND_SWORD)),
        WAR("War", -1, new ItemStack(Material.DIAMOND_SWORD)),
        UHC_MEETUP("UHC Meetup", 30, new ItemStack(Material.DIAMOND_SWORD)),
        KOTH("KOTH", 30, new ItemStack(Material.DIAMOND_SWORD)),
        INFECTION("Infection", 20, new ItemStack(Material.DIAMOND_SWORD));

        private String name;
        private int matchLength;
        private ItemStack itemStack;

        EventType(String name, int matchLength, ItemStack itemStack) {
            this.name = name;
            this.matchLength = matchLength;
            this.itemStack = itemStack;
        }

        public String getName() {
            return name;
        }

        public int getMatchLength() {
            return matchLength;
        }

	    public ItemStack getItemStack() {
		    return itemStack;
	    }

    }

	private static Map<String, Event> events = new HashMap<>();
	private static Map<UUID, Event> eventUUIDs = new HashMap<>();

	private static Map<Player, EventType> creatingEvents = new HashMap<>();

	protected Player creator;

	protected Arena arena;
    private UUID eventUUID = UUID.randomUUID();
    private EventType eventType;
	protected ItemStack eventItem;
	protected KitRuleSet kitRuleSet;
    protected int minPlayers = 2;
    protected int maxPlayers = 1000;

	protected boolean started = false;
	protected boolean isOver = false;
	protected boolean eventTimeLimitReached = false;

	protected Set<Player> playersToJoin = new HashSet<>();
	protected List<Player> players = new ArrayList<>();
	protected List<Player> participants;
	protected Map<UUID, UUID> lastDamage = new HashMap<>();
    protected Map<String, Long> godAppleCooldowns = new HashMap<>();

	protected ItemStack[] armorContents;
	protected ItemStack[] inventoryContents;
    protected ItemStack[] extraItemContents;

    protected EventTimerTask eventTimerTask;
    protected EventTieTask eventTieTask;

    public Event(Player player, ItemStack eventItem, KitRuleSet kitRuleSet, EventType eventType, ArenaManager.ArenaType arenaType) throws OutOfArenasException {
	    Gberry.log("EVENT", "Event being created of type " + eventType.getName() + " with arena " + arenaType.name() + " and ruleset " + kitRuleSet.getName());
        this.creator = player;
        this.eventItem = eventItem;
	    this.kitRuleSet = kitRuleSet;
        this.eventType = eventType;
        this.arena = ArenaManager.getArena(arenaType);

	    // Store in our map
	    Event.events.put(Event.getEventString(eventItem), this);
	    Event.eventUUIDs.put(this.eventUUID, this);

	    // Add item to upcoming events in events inventory
	    Inventory inventory = EventsInventory.getMainEventsInventory();
	    inventory.addItem(eventItem);
    }

    protected boolean hasEnoughPlayersToStart() {
        if (this.playersToJoin.size() >= this.minPlayers) {
            return true;
        }

        for (Player player : this.playersToJoin) {
            Group group = PotPvP.getInstance().getPlayerGroup(player);
            State<Group> currentState = GroupStateMachine.getInstance().getCurrentState(group);
            try {
                GroupStateMachine.transitionBackToDefaultState(currentState, group);
                group.sendMessage(ChatColor.RED + "Not enough players to start this event.");
            } catch (IllegalStateTransitionException e) {
                PotPvP.getInstance().somethingBroke(player, group);
            }
        }

        Gberry.log("EVENT", this.getEventType().toString() + " event, not enough players");

        this.endGame(true);

        throw new NotEnoughPlayersException();
    }

    /**
     * Add to queue
     */
    public boolean addToQueue(Player player) {
	    if (this.started) {
		    player.sendMessage(ChatColor.RED + "Cannot join queue, event already started");
		    return false;
	    }

	    if (this.playersToJoin.contains(player)) {
		    player.sendMessage(ChatColor.RED + "Already queued for event");
		    return false;
	    }

        Gberry.log("EVENT2", "Adding " + player.getName() + " to " + this.eventType.getName() + " queue with kit " + this.kitRuleSet.getName());

	    this.playersToJoin.add(player);
	    player.sendMessage(ChatColor.GREEN + "Added to event queue");

		return true;
    }

    public boolean inQueue(Player player) {
        return this.playersToJoin.contains(player);
    }

    /**
     * Remove from queue
     */
    public boolean removeFromQueue(Player player) {
        Gberry.log("EVENT2", "Removing " + player.getName() + " from " + this.eventType.getName() + " queue with kit " + this.kitRuleSet.getName());
        return this.playersToJoin.remove(player);
    }

    /**
     * Try to start the game if we have the right number of players
     */
    public void tryToStart() {
        if (this.playersToJoin.size() == this.maxPlayers) {
            try {
                this.startGame();
            } catch (NotEnoughPlayersException e) {
                // Pass
            }

            this.eventTimerTask.cancel();
        }
    }

    /**
     * Start a game
     */
    public void startGame() {
        this.started = true;

        // Update event item
        this.handleItemsForStart();

        if (!this.hasEnoughPlayersToStart()) {
            return;
        }

        // Throw away any glitched players
        for (Player player : this.playersToJoin) {
            if (!Gberry.isPlayerOnline(player)) {
                continue;
            }

            Gberry.log("EVENT2", "Handling player in arena with kit " + this.kitRuleSet.getName());
            this.players.add(player);
        }

        this.playersToJoin = null;

        // Cache all participants
        this.participants = new ArrayList<>(this.players);

        PotPvP.printLagDebug("Event " + this.eventType.getName() + " with kit " + this.kitRuleSet.getName() + " has started");
    }

    /**
     * Handle end of an event
     */
    public void endGame(boolean premature) {
	    Gberry.log("EVENT", this.eventType.toString() + " event ending");
        // Events inventory
        Inventory inventory = EventsInventory.getMainEventsInventory();

	    // Remove the event item
	    inventory.remove(this.eventItem);

	    // Now shift up the items
        for (int i = 27; i < 45; i++) {
	        ItemStack item = inventory.getItem(i);

	        // Look for first empty slot
	        if (item == null || item.getType() == Material.AIR) {
		        boolean itemFound = false;

		        // Now check if there are any event items after that
		        for (int j = i; j < 45; j++) {
			        ItemStack item2 = inventory.getItem(i);
			        if (item2 != null && item2.getType() != Material.AIR) {
				        // Move that item to the empty slot
				        inventory.setItem(i, item2);
				        inventory.setItem(j, null);

				        itemFound = true;

				        break;
			        }
		        }

		        // Are all the items in order now?
		        if (!itemFound) {
			        break;
		        }
	        }
        }

	    // Remove item from spectate event inventory
        SpectateEventInventory.getMainSpectateEventInventory().remove(this.eventItem);

        Event.eventUUIDs.remove(this.eventUUID);
        Event.events.remove(Event.getEventString(this.eventItem));

	    // THIS NEEDS TO BE BEFORE WE TOGGLE THE ARENA
	    this.isOver = true;

        // Don't toggle for War's, the WarMatch already does that
        if (!(this instanceof War)) {
            Gberry.log("EVENT2", "Arena being toggled " + this.arena.getArenaName() + " for event " + this.eventType.getName() + " with kit " + this.kitRuleSet.getName());
            this.arena.toggleBeingUsed();
        } else if (premature) {
            Gberry.log("EVENT2", "Arena being toggled2 " + this.arena.getArenaName() + " for event " + this.eventType.getName() + " with kit " + this.kitRuleSet.getName());
            this.arena.toggleBeingUsed();
        }

        if (!premature) {
            PotPvP.printLagDebug("Event " + this.eventType.getName() + " with kit " + this.kitRuleSet.getName() + " has ended");
        }

	    Gberry.log("EVENT", this.eventType.toString() + " event ended");
    }

    /**
     * Game is over
     */
    public boolean isOver() {
        return this.isOver;
    }


	public void handleItemsForStart() {
		// Remove item from upcoming events in events inventory
		Inventory inventory = EventsInventory.getMainEventsInventory();
		inventory.remove(this.eventItem);

		// Add item to ongoing events in events inventory
		for (int i = 27; i < 45; i++) {
			// Find the first free inventory slot in the upcoming events section
			ItemStack item = inventory.getItem(i);
			if (item == null || item.getType() == Material.AIR) {
				inventory.setItem(i, this.eventItem);
				break;
			}
		}

        // Add item to spectate event inventory
        SpectateEventInventory.getMainSpectateEventInventory().addItem(this.eventItem);
    }

	/**
	 * Get the event creator
	 */
	public Player getCreator() {
		return creator;
	}

	/**
     * Get a KitRuleSet
     */
    public KitRuleSet getKitRuleSet() {
        return this.kitRuleSet;
    }

	/**
	 * Get armor contents for custom kits
	 */
	public ItemStack[] getArmorContents() {
		return armorContents;
	}

	/**
	 * Get inventory contents for custom kits
	 */
	public ItemStack[] getInventoryContents() {
		return inventoryContents;
	}

    /**
     * Get extra item contents for custom kits
     */
    public ItemStack[] getExtraItemContents() {
        return extraItemContents;
    }

    /**
     * @return - Number of players queued for the event
     */
    public int getNumberInQueue() {
        return this.playersToJoin.size();
    }

	/**
     * Get unmodifiable list of players involved
     */
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        players.addAll(this.players);

        return Collections.unmodifiableList(players);
    }

    /**
     * Check if a player is contained in this game mode
     */
    public boolean contains(Player player) {
        return this.players.contains(player);
    }


    /**
     * Some game modes have god apple cooldowns (this is nasty, idgaf)
     */
    public Map<String, Long> getGodAppleCooldowns() {
        return this.godAppleCooldowns;
    }

    /**
     * Get the event type
     */
    public EventType getEventType() {
        return this.eventType;
    }

    public abstract void handleDeath(Player player);

    public abstract boolean handleQuit(Player player, String reason);

    /**
     * Store last damage for internal usage
     */
    public void putLastDamage(UUID attacker, UUID defender, double damage, double finalDamage) {
        this.lastDamage.put(defender, attacker);
    }

	private static String getEventString(ItemStack item) {
		return item.getItemMeta().getDisplayName() + item.getItemMeta().getLore().get(0);
	}

	public static Map<String, Event> getEvents() {
		return Event.events;
	}

	public static UUID getUUIDForEvent(Event event) {
		for (UUID uuid : Event.eventUUIDs.keySet()) {
			Event evnt = Event.eventUUIDs.get(uuid);
			if (evnt == event) {
				return uuid;
			}
		}

		return null;
	}

    public static Event getEvent(UUID uuid) {
        return Event.eventUUIDs.get(uuid);
    }

	public static Event getEventForItem(ItemStack item) {
		return Event.events.get(Event.getEventString(item));
	}

	public static Event removeEventForItem(ItemStack item) {
		return Event.events.remove(Event.getEventString(item));
	}

	public static void storeEventCreation(Player player, EventType eventType) {
		Event.creatingEvents.put(player, eventType);
	}

	public static EventType getEventCreating(Player player) {
		return Event.creatingEvents.get(player);
	}

	public static EventType removeEventCreating(Player player) {
		return Event.creatingEvents.remove(player);
	}

    private static void makeNiceItem(ItemStack eventItem, String displayName, String kitRuleSetName) {
        ItemMeta eventItemMeta = eventItem.getItemMeta();
        eventItemMeta.setDisplayName(ChatColor.AQUA + displayName);
        List<String> eventItemLore = new ArrayList<>();
        eventItemLore.add(ChatColor.LIGHT_PURPLE + "Kit: " + kitRuleSetName);
        eventItemLore.add("");
        eventItemLore.add(ChatColor.YELLOW + "Middle click to preview kit");
        eventItemMeta.setLore(eventItemLore);
        eventItem.setItemMeta(eventItemMeta);
    }

    /**
     * Last two params should be null (if not a custom kit)
     */
	public static void createEvent(Player player, EventType eventType, KitRuleSet kitRuleSet, ItemStack[] armorContents, ItemStack[] inventoryContents) throws OutOfArenasException {
        // Sanity check
        if (!(kitRuleSet instanceof EventRuleSet) && (armorContents != null || inventoryContents != null)) {
            throw new RuntimeException("Armor/inventory provided in createEvent() but the kitruleset is not EventRuleSet");
        }

		// Debug
		if (kitRuleSet == null) {
			Gberry.log("BUG", "Kitruleset null when creating event: " + eventType.getName());
			Gberry.log("BUG", "Player: " + player.getName() + " and armor/inventory: " + armorContents + "|" + inventoryContents);
		}

        // Remove player from our event creating cache
        Event.removeEventCreating(player);

        Event customEvent = null;
        ItemStack eventItem = eventType.getItemStack().clone();

        if (eventType == EventType.LMS) {
	        // Check for armor in event kits
	        if (kitRuleSet instanceof EventRuleSet) {
		        boolean hasArmor = false;
		        for (ItemStack item : armorContents) {
			        if (item != null && item.getType() != Material.AIR) {
				        hasArmor = true;
				        break;
			        }
		        }

		        // Don't let them use this event kit if they don't have armor
		        if (!hasArmor) {
			        player.sendMessage(ChatColor.RED + "Can not start LMS with an event kit that has no armor!");
			        return;
		        }
	        }
        }

		Event.makeNiceItem(eventItem, eventType.getName(), kitRuleSet.getName());

        // Create new event now
        Gberry.log("EVENT", "Creating new " + eventType.getName());
        switch (eventType) {
            case LMS:
                customEvent = new LastManStanding(player, eventItem, kitRuleSet, armorContents, inventoryContents);
                break;
            case WAR:
                customEvent = new War(player, eventItem, kitRuleSet, armorContents, inventoryContents);
                break;
            case SLAUGHTER:
                customEvent = new Slaughter(player, eventItem, kitRuleSet, armorContents, inventoryContents);
                break;
            case INFECTION:
                customEvent = new Infection(player, eventItem, kitRuleSet, armorContents, inventoryContents);
                break;
            case KOTH:
                customEvent = new KOTH(player, eventItem, kitRuleSet, armorContents, inventoryContents);
                break;
            case UHC_MEETUP:
                customEvent = new UHCMeetup(player, eventItem);
                break;
        }

        try {
            GroupStateMachine.lobbyState.transition(GroupStateMachine.matchMakingState, PotPvP.getInstance().getPlayerGroup(player));

	        customEvent.addToQueue(player);

	        DonatorManager.setEventTime(player.getUniqueId(), System.currentTimeMillis());

	        EventTimerTask eventTimerTask = new EventTimerTask(customEvent, 3);
	        eventTimerTask.runTaskTimer(PotPvP.getInstance(), 0L, /*60L);*/1200L);

	        customEvent.setEventTimerTask(eventTimerTask);
        } catch (IllegalStateTransitionException e) {
            PotPvP.getInstance().somethingBroke(player, PotPvP.getInstance().getPlayerGroup(player));
        }
	}

    public void setEventTimerTask(EventTimerTask eventTimerTask) {
        this.eventTimerTask = eventTimerTask;
    }

    public String getInfo() {
        return this.eventType.getName() + " with kit " + this.kitRuleSet.getName();
    }

    public boolean isStarted() {
        return started;
    }

    /**
     * Get arena
     */
    public Arena getArena() {
        return this.arena;
    }

    public List<Player> getParticipants() {
        return participants;
    }

	public boolean isEventTimeLimitReached() {
		return eventTimeLimitReached;
	}

	public void setEventTimeLimitReached(boolean eventTimeLimitReached) {
		this.eventTimeLimitReached = eventTimeLimitReached;
	}

}
