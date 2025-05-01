package net.badlion.uhc.listeners.gamemodes;

import net.badlion.common.libraries.StringCommon;
import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BroadcasterGameMode implements GameMode {

	private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getType() != Material.DIAMOND_ORE && event.getBlock().getType() != Material.GOLD_ORE) {
			return;
		}

		Gberry.broadcastMessage(
				ChatColor.GOLD + ChatColor.BOLD.toString()
						+ event.getPlayer().getName() + " has mined a "
						+ StringCommon.cleanEnum(event.getBlock().getType().name()) + " at "
						+ decimalFormat.format(event.getPlayer().getLocation().getX()) + ","
						+ decimalFormat.format(event.getPlayer().getLocation().getY()) + ","
						+ decimalFormat.format(event.getPlayer().getLocation().getZ())
		);
	}

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Broadcaster");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- Mining gold or diamonds will broadcast your location to the server");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "???";
    }

    @Override
    public void unregister() {
        BlockBreakEvent.getHandlerList().unregister(this);
    }

}
