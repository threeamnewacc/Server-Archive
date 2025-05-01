package net.badlion.gfactions.commands;

import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class DoubleOresCommand implements CommandExecutor, Listener {

    public static boolean doubleOresActivated = false;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (DoubleOresCommand.doubleOresActivated) {
            BlockBreakEvent.getHandlerList().unregister(this);
            sender.sendMessage(ChatColor.GREEN + "Double ores de-activated");
        } else {
            GFactions.plugin.getServer().getPluginManager().registerEvents(this, GFactions.plugin);
            sender.sendMessage(ChatColor.GREEN + "Double ores activated");
        }

        DoubleOresCommand.doubleOresActivated = !DoubleOresCommand.doubleOresActivated;

        return true;
    }

    @EventHandler(priority=EventPriority.LAST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (DoubleOresCommand.doubleOresActivated) {
            if (event.getBlock().getType() == Material.IRON_ORE) {
                event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.IRON_INGOT));
                event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.IRON_INGOT));
                event.getBlock().setType(Material.AIR);
                event.setCancelled(true);
            } else if (event.getBlock().getType() == Material.GOLD_ORE) {
                event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.GOLD_INGOT));
                event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.GOLD_INGOT));
                event.getBlock().setType(Material.AIR);
                event.setCancelled(true);
            } else if (event.getBlock().getType() == Material.DIAMOND_ORE) {
                event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.DIAMOND));
                event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.DIAMOND));
                event.getBlock().setType(Material.AIR);
                event.setCancelled(true);
            } else if (event.getBlock().getType() == Material.EMERALD_ORE) {
                event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.EMERALD));
                event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.EMERALD));
                event.getBlock().setType(Material.AIR);
                event.setCancelled(true);
            }
        }
    }

}
