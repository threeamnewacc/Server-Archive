package net.badlion.uhc.listeners.gamemodes;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.events.GameStartEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class LimitedEnchantsGameMode implements GameMode {

	@EventHandler
	public void onGameStart(GameStartEvent event) {
		World world = BadlionUHC.getInstance().getServer().getWorld(BadlionUHC.UHCWORLD_NAME);
		world.getHighestBlockAt(0, 0).getLocation().getBlock().setType(Material.ENCHANTMENT_TABLE);

		world.getHighestBlockAt(-1500, -1500).getLocation().getBlock().setType(Material.ENCHANTMENT_TABLE);
		world.getHighestBlockAt(-1500, 1500).getLocation().getBlock().setType(Material.ENCHANTMENT_TABLE);
		world.getHighestBlockAt(1500, -1500).getLocation().getBlock().setType(Material.ENCHANTMENT_TABLE);
		world.getHighestBlockAt(1500, 1500).getLocation().getBlock().setType(Material.ENCHANTMENT_TABLE);
	}

	@EventHandler
	public void onCraft(CraftItemEvent event) {
		if (event.getCurrentItem().getType() == Material.ENCHANTMENT_TABLE) {
			event.setCancelled(true);
			event.setCurrentItem(null);
			event.setCursor(null);
			event.setResult(Event.Result.DENY);
			((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "You cannot craft enchantment chests!");
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getType() == Material.ENCHANTMENT_TABLE) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "You can't break this!");
		}
	}

	@EventHandler
	public void onBlockBreak(BlockPlaceEvent event) {
		if (event.getBlock().getType() == Material.ENCHANTMENT_TABLE) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "You can't place this!");
		}
	}

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.DIAMOND);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Limited Enchant");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- You cannot craft enchantment tables");
        lore.add(ChatColor.AQUA + "- There are unbreakable enchantment tables at 0,0 and +/- 1500 (five total)");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "Badlion";
    }

    @Override
    public void unregister() {
        PlayerDeathEvent.getHandlerList().unregister(this);
        BlockBreakEvent.getHandlerList().unregister(this);
        EntityExplodeEvent.getHandlerList().unregister(this);
        EntityDeathEvent.getHandlerList().unregister(this);
    }

}
