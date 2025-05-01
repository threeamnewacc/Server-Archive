package net.badlion.skywars.kits;

import net.badlion.mpg.kits.MPGKit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ClassicDefaultKit extends MPGKit {

    private final static ItemStack[] defaultItems = new ItemStack[36];
    private final static ItemStack[] defaultArmor = new ItemStack[4];

    private final static ClassicDefaultKit kit = new ClassicDefaultKit();
    public static ClassicDefaultKit getKit() {
        return ClassicDefaultKit.kit;
    }

    public ClassicDefaultKit() {
        super(null, "Default Kit", 0, "classic", "skywars", new ItemStack(Material.WOOD_SWORD), defaultItems, defaultArmor);

        this.inventoryContents[0] = new ItemStack(Material.WOOD_SWORD);
        this.inventoryContents[1] = new ItemStack(Material.WOOD_PICKAXE);
        this.inventoryContents[2] = new ItemStack(Material.WOOD_SPADE);

        ItemMeta defaultMeta = this.previewItem.getItemMeta();
        defaultMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + "Default Kit"); // ChatColor.RESET to verify default kit
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.BLUE + "Default kit contains a wood sword,");
        lore.add(ChatColor.BLUE + "wood pickaxe, and a wood spade.");
        defaultMeta.setLore(lore);
        this.previewItem.setItemMeta(defaultMeta);
    }

}
