package net.badlion.cosmetics.gadgets;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ExplodingBowGadget extends Gadget {

    public ExplodingBowGadget() {
        super("exploding_bow", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.BOW, ChatColor.GREEN + "Exploding Bow", ChatColor.GRAY + "Only real tryhards use", ChatColor.GRAY + "the Explosive Bow!"));
    }

    @Override
    public void giveGadget(Player player) {
        ItemStack bow = ItemStackUtil.createItem(Material.BOW, ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Exploding Bow");
        ItemMeta bowM = bow.getItemMeta();
        bowM.spigot().setUnbreakable(true);
        bow.setItemMeta(bowM);

        player.getInventory().setItem(5, Gberry.getGlowItem(bow));
        player.getInventory().setItem(10, ItemStackUtil.createItem(Material.ARROW, ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Exploding Bow Ammo"));
    }
}
