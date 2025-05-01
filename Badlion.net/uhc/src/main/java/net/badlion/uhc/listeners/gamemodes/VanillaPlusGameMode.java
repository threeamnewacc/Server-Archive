package net.badlion.uhc.listeners.gamemodes;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VanillaPlusGameMode implements GameMode {

    private Random random = new Random();

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.FLINT);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Vanilla+");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- Higher Apple Drop Rate");
        lore.add(ChatColor.AQUA + "- Higher Flint Drop Rate");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "Badlion";
    }

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LAST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getType() == Material.GRAVEL) {
			if (this.random.nextInt(10) == 0) {
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.FLINT));
			}
		} else if (event.getBlock().getType() == Material.LEAVES && event.getBlock().getData() % 4 == 0) {
			if (this.random.nextInt(200) == 0) {
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
			}
		} else if (event.getBlock().getType() == Material.LEAVES_2 && event.getBlock().getData() % 4 == 1) {
			if (this.random.nextInt(200) == 0) {
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
			}
		}
	}

	@EventHandler
	public void onLeafDecay(LeavesDecayEvent event) {
		if (event.getBlock().getType() == Material.LEAVES && event.getBlock().getData() % 4 == 0) {
			if (this.random.nextInt(200) == 0) {
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
			}
		} else if (event.getBlock().getType() == Material.LEAVES_2 && event.getBlock().getData() % 4 == 1) {
			if (this.random.nextInt(200) == 0) {
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
			}
		}
	}

    @Override
    public void unregister() {
        BlockBreakEvent.getHandlerList().unregister(this);
        LeavesDecayEvent.getHandlerList().unregister(this);
    }

}
