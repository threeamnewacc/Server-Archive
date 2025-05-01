package net.badlion.uhc.listeners.gamemodes;

import net.badlion.gberry.Gberry;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.events.GameStartEvent;
import net.badlion.uhc.listeners.MiniStatsListener;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class RiskyRetrievalGameMode implements GameMode {

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		ItemStack[] items = event.getEntity().getEnderChest().getContents();
		for (ItemStack itemStack : items) {
			event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), itemStack);
		}
		event.getEntity().getEnderChest().clear();
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		ItemStack itemStack;
		if (event.getBlock().getType() == Material.DIAMOND_ORE) {
			itemStack = new ItemStack(Material.DIAMOND);
		} else if (event.getBlock().getType() == Material.GOLD_ORE) {
			itemStack = new ItemStack(Material.GOLD_INGOT);
		} else {
			return;
		}

		event.setCancelled(true);

		Player player = event.getPlayer();

		// Stat Tracking (do before we set to AIR)
		if ((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.STATS.name()).getValue()) {
			MiniStatsListener.handleBlockBreak(event);
		}

		BadlionUHC.getInstance().createExpOrb(event.getBlock().getLocation(), 1);
		if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType().getMaxDurability() > 0) {
			short dur = event.getPlayer().getItemInHand().getDurability();
			if (++dur >= event.getPlayer().getItemInHand().getType().getMaxDurability()) {
				player.setItemInHand(null);
				player.updateInventory();
			} else {
				player.getItemInHand().setDurability(dur);
			}
		}

		// Remove the block
		event.getBlock().setType(Material.AIR);

		if (!addItem(player.getEnderChest(), itemStack)) {
			if (!addItem(player.getInventory(), itemStack)) {
				event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), itemStack);
			}
		}
	}

	@EventHandler
	public void onGameStart(GameStartEvent event) {
		World world = BadlionUHC.getInstance().getServer().getWorld(BadlionUHC.UHCWORLD_NAME);
		world.getHighestBlockAt(0, 0).setType(Material.ENDER_CHEST);

		world.getHighestBlockAt(-1500, -1500).getLocation().getBlock().setType(Material.ENDER_CHEST);
		world.getHighestBlockAt(-1500, 1500).getLocation().getBlock().setType(Material.ENDER_CHEST);
		world.getHighestBlockAt(1500, -1000).getLocation().getBlock().setType(Material.ENDER_CHEST);
		world.getHighestBlockAt(1500, 1500).getLocation().getBlock().setType(Material.ENDER_CHEST);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getType() == Material.ENDER_CHEST) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "You can't break this!");
		}
	}

	@EventHandler
	public void onBlockBreak(BlockPlaceEvent event) {
		if (event.getBlock().getType() == Material.ENDER_CHEST) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "You can't place this!");
		}
	}

	@EventHandler
	public void onBlockBreak(CraftItemEvent event) {
		if (event.getCurrentItem().getType() == Material.ENDER_CHEST) {
			event.setCancelled(true);
			event.setCursor(null);
			event.setCurrentItem(null);
			event.setResult(Event.Result.DENY);
			((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "You can't craft this!");
		}
	}

	public static boolean addItem(Inventory inventory, ItemStack itemStack) {
		// Inventory is full
		if (inventory.firstEmpty() == -1) {
			// Inventory doesn't contain the item you are trying to add
			// But it is full...
			if (inventory.contains(itemStack)) {
				// It does contain it, check if it's a full stack...
				for (ItemStack itemStack1 : inventory.all(itemStack).values()) {
					if (itemStack1.getAmount() < 64) {
						// Not a full ItemStack, add it
						inventory.addItem(itemStack);
						return true;
					}
				}
				// Inventory is full and no space for more
				return false;
			}
			return false;
		} else {
			inventory.addItem(itemStack);
			return true;
		}
	}

	// Stop them placing other shit in their inventory
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory().equals(event.getWhoClicked().getEnderChest())) {
			if (event.getCurrentItem().getType() != Material.DIAMOND && event.getCurrentItem().getType() != Material.GOLD_INGOT) {
				event.setCancelled(true);
				((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "You can't put this in your Ender Chest!");
			}
		}
	}

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.ENDER_PEARL);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Ore Banker");

        List<String> lore = new ArrayList<>();

	    lore.add(ChatColor.AQUA + "- When you mine diamonds or gold it will be transferred into your Ender Chest");
	    lore.add(ChatColor.AQUA + "- You cannot craft Ender Chests, there are unbreakable ones at 0,0 and +/- 1500 (five total)");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "https://www.reddit.com/user/ShutUpBrick";
    }

    @Override
    public void unregister() {
		PlayerDeathEvent.getHandlerList().unregister(this);
	    BlockBreakEvent.getHandlerList().unregister(this);
	    BlockPlaceEvent.getHandlerList().unregister(this);
	    GameStartEvent.getHandlerList().unregister(this);
		CraftItemEvent.getHandlerList().unregister(this);
		InventoryClickEvent.getHandlerList().unregister(this);
    }

}
