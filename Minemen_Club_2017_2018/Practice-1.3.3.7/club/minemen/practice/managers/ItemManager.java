// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.managers;

import club.minemen.core.util.finalutil.ItemUtil;
import club.minemen.core.util.finalutil.CC;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemManager
{
    private final ItemStack[] spawnItems;
    private final ItemStack[] queueItems;
    private final ItemStack[] partyItems;
    private final ItemStack[] tournamentItems;
    private final ItemStack[] specItems;
    private final ItemStack[] partySpecItems;
    private final ItemStack defaultBook;
    
    public ItemManager() {
        this.spawnItems = new ItemStack[] { ItemUtil.createItem(Material.STONE_SWORD, CC.YELLOW + "Join Unranked Queue"), ItemUtil.createItem(Material.IRON_SWORD, CC.GREEN + "Join Ranked Queue"), ItemUtil.createItem(Material.DIAMOND_SWORD, CC.B_GOLD + "Join Premium Queue"), null, ItemUtil.createItem(Material.NAME_TAG, CC.PRIMARY + "Create Party"), null, null, ItemUtil.createItem(Material.WATCH, CC.AQUA + "Settings"), ItemUtil.createItem(Material.BOOK, CC.GOLD + "Edit Kits") };
        this.queueItems = new ItemStack[] { null, null, null, null, null, null, null, null, ItemUtil.createItem(Material.REDSTONE, CC.RED + "Leave Queue") };
        this.specItems = new ItemStack[] { null, null, null, null, null, null, null, null, ItemUtil.createItem(Material.REDSTONE, CC.RED + "Leave Spectator Mode") };
        this.partySpecItems = new ItemStack[] { null, null, null, null, null, null, null, null, ItemUtil.createItem(Material.NETHER_STAR, CC.RED + "Leave Party") };
        this.tournamentItems = new ItemStack[] { null, null, null, null, null, null, null, null, ItemUtil.createItem(Material.NETHER_STAR, CC.RED + "Leave Tournament") };
        this.partyItems = new ItemStack[] { ItemUtil.createItem(Material.STONE_SWORD, CC.YELLOW + "Join 2v2 Unranked Queue"), ItemUtil.createItem(Material.IRON_SWORD, CC.GREEN + "Join 2v2 Ranked Queue"), null, null, ItemUtil.createItem(Material.DIAMOND_AXE, CC.AQUA + "Start Party Event"), ItemUtil.createItem(Material.IRON_AXE, CC.PRIMARY + "Fight Other Party"), null, null, ItemUtil.createItem(Material.NETHER_STAR, CC.RED + "Leave Party") };
        this.defaultBook = ItemUtil.createItem(Material.ENCHANTED_BOOK, CC.PRIMARY + "Default Kit");
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
