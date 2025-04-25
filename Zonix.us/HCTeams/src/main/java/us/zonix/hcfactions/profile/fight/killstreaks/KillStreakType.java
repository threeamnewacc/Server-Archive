package us.zonix.hcfactions.profile.fight.killstreaks;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import us.zonix.hcfactions.util.ItemBuilder;
import us.zonix.hcfactions.util.ItemBuilder;

public enum KillStreakType {

    THREE_KILLS(3, "3 Golden Apples", new ItemStack[] {new ItemBuilder(Material.GOLDEN_APPLE).amount(3).build()}),
    SIX_KILLS(6, "Debuffs", new ItemStack[] {new ItemBuilder(Material.POTION).durability(16388).build(), new ItemBuilder(Material.POTION).durability(16394).build()}),
    TEN_KILLS(10, "Invis Splash", new ItemStack[] {new ItemBuilder(Material.POTION).durability(16318).build()}),
    TWELVE_KILLS(12, "Cobwebs", new ItemStack[] {new ItemBuilder(Material.WEB).amount(20).build()}),
    FIFTEEN_KILLS(15, "God Apple", new ItemStack[] {new ItemBuilder(Material.GOLDEN_APPLE).durability(1).build()}),
    TWENTY_KILLS(20, "Strength II", new ItemStack[] {new ItemBuilder(Material.POTION).durability(16377).build()});

    private int count;
    private String message;
    private ItemStack[] item;

    private KillStreakType(int count, String message, ItemStack[] item) {
        this.count = count;
        this.message = message;
        this.item = item;
    }

    public int getCount() {
        return count;
    }

    public ItemStack[] getItems() {
        return item;
    }

    public String getMessage() {
        return message;
    }
}
