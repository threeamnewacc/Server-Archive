package net.badlion.uhc.listeners.gamemodes;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.commands.handlers.GameModeHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OreFrenzyGameMode implements GameMode {

    private Random random = new Random();

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.REDSTONE_ORE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Ore Frenzy");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- When you mine lapis ore it drops a health splash potion");
        lore.add(ChatColor.AQUA + "- When you mine emerald ore it drops 32 arrows");
        lore.add(ChatColor.AQUA + "- When you mine redstone ore it drops an unenchanted book");
        lore.add(ChatColor.AQUA + "- When you mine diamond ore it drops a diamond and 4 bottles of exp");
        lore.add(ChatColor.AQUA + "- When you mine quartz ore it drops a block of TNT");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "Badlion";
    }

    @EventHandler
    public void onBlockBroken(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.LAPIS_ORE) {
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
            // TODO: Fix
	        if (!GameModeHandler.GAME_MODES.contains("OPS_VS_WORLD")) {
		        event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.POTION, 1, (short) 16453));
	        }
            BadlionUHC.getInstance().createExpOrb(event.getBlock().getLocation(), this.random.nextInt(4) + 2);
        } else if (event.getBlock().getType() == Material.EMERALD_ORE) {
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
            event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.ARROW, 32));
            BadlionUHC.getInstance().createExpOrb(event.getBlock().getLocation(), this.random.nextInt(5) + 3);
        } else if (event.getBlock().getType() == Material.REDSTONE_ORE || event.getBlock().getType() == Material.GLOWING_REDSTONE_ORE) {
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
            event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.BOOK));
            BadlionUHC.getInstance().createExpOrb(event.getBlock().getLocation(), this.random.nextInt(5) + 1);
        } else if (event.getBlock().getType() == Material.DIAMOND_ORE) {
            event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.EXP_BOTTLE, 4));
        } else if (event.getBlock().getType() == Material.QUARTZ_ORE) {
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
            event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.TNT));
            BadlionUHC.getInstance().createExpOrb(event.getBlock().getLocation(), this.random.nextInt(4) + 2);
        }
    }

    @Override
    public void unregister() {
        BlockBreakEvent.getHandlerList().unregister(this);
    }

}
