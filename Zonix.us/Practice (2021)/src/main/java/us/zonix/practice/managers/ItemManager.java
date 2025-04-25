package us.zonix.practice.managers;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import us.zonix.practice.util.ItemUtil;

public class ItemManager {
    private final ItemStack[] spawnItems = new ItemStack[]{
        ItemUtil.createItem(Material.IRON_SWORD, ChatColor.RED + "Play Unranked"),
        ItemUtil.createItem(Material.DIAMOND_SWORD, ChatColor.DARK_RED + "Play Ranked"),
        ItemUtil.createItem(Material.GOLD_SWORD, ChatColor.YELLOW.toString() + "Play Premium"),
        null,
        ItemUtil.createItem(Material.NAME_TAG, ChatColor.GOLD + "Create Party"),
        null,
        ItemUtil.createItem(Material.EMERALD, ChatColor.GREEN + "Leaderboards"),
        ItemUtil.createItem(Material.BOOK, ChatColor.YELLOW + "Kit Editor"),
        ItemUtil.createItem(Material.WATCH, ChatColor.GRAY + "Settings")
    };
    private final ItemStack[] queueItems = new ItemStack[]{
        null, null, null, null, ItemUtil.createItem(Material.REDSTONE, ChatColor.RED.toString() + ChatColor.BOLD + "Leave Queue"), null, null, null, null
    };
    private final ItemStack[] partyItems;
    private final ItemStack[] tournamentItems;
    private final ItemStack[] eventItems;
    private final ItemStack[] sumoItems;
    private final ItemStack[] specItems = new ItemStack[]{
        null,
        null,
        null,
        null,
        ItemUtil.createItem(Material.NETHER_STAR, ChatColor.RED.toString() + ChatColor.BOLD + "Leave Spectator Mode"),
        null,
        null,
        null,
        null
    };
    private final ItemStack[] partySpecItems = new ItemStack[]{
        null, null, null, null, ItemUtil.createItem(Material.NETHER_STAR, ChatColor.RED.toString() + ChatColor.BOLD + "Leave Party"), null, null, null, null
    };
    private final ItemStack defaultBook;

    public ItemManager() {
        this.tournamentItems = new ItemStack[]{
            null,
            null,
            null,
            null,
            ItemUtil.createItem(Material.NETHER_STAR, ChatColor.RED.toString() + ChatColor.BOLD + "Leave Tournament"),
            null,
            null,
            null,
            null
        };
        this.eventItems = new ItemStack[]{
            null, null, null, null, ItemUtil.createItem(Material.NETHER_STAR, ChatColor.RED.toString() + "Leave Event"), null, null, null, null
        };
        this.sumoItems = new ItemStack[]{
            null, null, null, null, null, null, null, null, ItemUtil.createItem(Material.NETHER_STAR, ChatColor.RED.toString() + "Leave Event")
        };
        this.partyItems = new ItemStack[]{
            ItemUtil.createItem(Material.IRON_SWORD, ChatColor.RED + "Play 2v2 Unranked"),
            ItemUtil.createItem(Material.DIAMOND_SWORD, ChatColor.DARK_RED + "Play 2v2 Ranked"),
            null,
            ItemUtil.createItem(Material.PAPER, ChatColor.YELLOW + "Other Parties"),
            ItemUtil.createItem(Material.SKULL_ITEM, ChatColor.GREEN + "Party Information"),
            ItemUtil.createItem(Material.BOOK, ChatColor.YELLOW + "Kit Editor"),
            null,
            ItemUtil.createItem(Material.GOLD_AXE, ChatColor.AQUA + "Party Events"),
            ItemUtil.createItem(Material.NETHER_STAR, ChatColor.RED + "Leave Party")
        };
        this.defaultBook = ItemUtil.createItem(Material.ENCHANTED_BOOK, ChatColor.RED + "Default Kit");
    }

    public ItemStack[] getSpawnItems() {
        return this.spawnItems;
    }

    public ItemStack[] getQueueItems() {
        return this.queueItems;
    }

    public ItemStack[] getPartyItems() {
        return this.partyItems;
    }

    public ItemStack[] getTournamentItems() {
        return this.tournamentItems;
    }

    public ItemStack[] getEventItems() {
        return this.eventItems;
    }

    public ItemStack[] getSumoItems() {
        return this.sumoItems;
    }

    public ItemStack[] getSpecItems() {
        return this.specItems;
    }

    public ItemStack[] getPartySpecItems() {
        return this.partySpecItems;
    }

    public ItemStack getDefaultBook() {
        return this.defaultBook;
    }
}
