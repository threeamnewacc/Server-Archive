package net.badlion.potpvp.kits;

import net.badlion.potpvp.kits.kitpvp.*;
import net.badlion.potpvp.kits.soup.*;
import net.badlion.potpvp.kits.soup.DefaultKit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public abstract class Kit {

    // KitPvP Kits
    public static final Kit kitDefaultKit = new net.badlion.potpvp.kits.kitpvp.DefaultKit(new ItemStack(Material.IRON_SWORD));

    // Soup Kits
    public static final Kit soupArcherKit = new ArcherKit(new ItemStack(Material.BOW));
    public static final Kit soupChainmailKit = new ChainmailKit(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
    public static final Kit soupDefaultKit = new DefaultKit(new ItemStack(Material.IRON_SWORD));
    public static final Kit soupDiamondKit = new DiamondKit(new ItemStack(Material.DIAMOND_SWORD));
    public static final Kit soupGoldKit = new GoldKit(new ItemStack(Material.GOLD_SWORD));
    public static final Kit soupLeatherKit = new LeatherKit(new ItemStack(Material.LEATHER_HELMET));

    private ItemStack item;
    protected ItemStack[] inventoryItems = new ItemStack[36];
    protected ItemStack[] armorItems = new ItemStack[9];
    protected List<PotionEffect> potionEffects = new ArrayList<>();

    public Kit(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public void loadKit(Player player) {
        player.getInventory().setContents(this.inventoryItems);
        player.getInventory().setArmorContents(this.armorItems);
        player.updateInventory();
    }

    public void handleKill(Player player) {
        // Do any special effects such as extra coins
    }

}
