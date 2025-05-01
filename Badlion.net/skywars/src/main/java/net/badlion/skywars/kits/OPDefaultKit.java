package net.badlion.skywars.kits;

import net.badlion.mpg.kits.MPGKit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class OPDefaultKit extends MPGKit {

    private final static ItemStack[] defaultItems = new ItemStack[36];
    private final static ItemStack[] defaultArmor = new ItemStack[4];

    private final static OPDefaultKit kit = new OPDefaultKit();
    public static OPDefaultKit getKit() {
        return OPDefaultKit.kit;
    }

    public OPDefaultKit() {
        super(null, "Default Kit", 0, "op", "skywars", new ItemStack(Material.WOOD_SWORD), defaultItems, defaultArmor);

        this.inventoryContents[0] = new ItemStack(Material.STONE_SWORD);
        this.inventoryContents[1] = new ItemStack(Material.STONE_PICKAXE);
        this.inventoryContents[2] = new ItemStack(Material.STONE_SPADE);

        this.armorContents[3] = new ItemStack(Material.LEATHER_HELMET);
        this.armorContents[2] = new ItemStack(Material.LEATHER_CHESTPLATE);
        this.armorContents[1] = new ItemStack(Material.LEATHER_LEGGINGS);
        this.armorContents[0] = new ItemStack(Material.LEATHER_BOOTS);

        ItemMeta defaultMeta = this.previewItem.getItemMeta();
        defaultMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + "Default Kit"); // ChatColor.RESET to verify default kit
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.BLUE + "Default kit contains a stone sword,");
        lore.add(ChatColor.BLUE + "stone pickaxe, and a stone spade.");
        defaultMeta.setLore(lore);
        this.previewItem.setItemMeta(defaultMeta);
    }

}
