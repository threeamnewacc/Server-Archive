package cc.patrone.practice.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public enum Items {

    UNRANKED(new ItemBuilder(Material.IRON_SWORD).setName(ChatColor.LIGHT_PURPLE + "Join an Unranked Queue").toItemStack()),
    RANKED(new ItemBuilder(Material.DIAMOND_SWORD).setName(ChatColor.LIGHT_PURPLE + "Join a Ranked Queue").toItemStack()),
    STATS(new ItemBuilder(Material.EMERALD).setName(ChatColor.LIGHT_PURPLE + "View Stats").toItemStack()),
    EDIT_KIT(new ItemBuilder(Material.BOOK).setName(ChatColor.LIGHT_PURPLE + "Edit a Kit").toItemStack()),
    VIEW_QUEUE(new ItemBuilder(Material.BOOK).setName(ChatColor.LIGHT_PURPLE + "View Queue").toItemStack()),
    LEAVE_QUEUE(new ItemBuilder(Material.INK_SACK, 0, (byte) 1).setName(ChatColor.RED + "Leave Queue").toItemStack()),
    CUSTOM_KIT(new ItemBuilder(Material.ENCHANTED_BOOK).setName(ChatColor.GREEN + "Custom Kit").toItemStack()),
    DEFAULT_KIT(new ItemBuilder(Material.BOOK).setName(ChatColor.YELLOW + "Default Kit").toItemStack()),
    MATCH_INFO(new ItemBuilder(Material.BOOK).setName(ChatColor.AQUA + "Match Info").toItemStack()),
    STOP_SPECTATING(new ItemBuilder(Material.REDSTONE_TORCH_ON).setName(ChatColor.RED + "Stop Spectating").toItemStack());

    private ItemStack item;
}
