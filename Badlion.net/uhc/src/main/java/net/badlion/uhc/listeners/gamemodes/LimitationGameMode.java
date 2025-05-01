package net.badlion.uhc.listeners.gamemodes;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LimitationGameMode implements GameMode {

	private static final int MAX_DIAMONDS_MINED = 16;
	private static final int MAX_GOLD_MINED = 32;
	private static final int MAX_IRON_MINED = 64;

	private Map<UUID, Integer> diamondsMined = new HashMap<>();
	private Map<UUID, Integer> goldMined = new HashMap<>();
	private Map<UUID, Integer> ironMined = new HashMap<>();

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		// In case they joined before the gamemode was added (fail-safe)
		if (!this.diamondsMined.containsKey(event.getPlayer().getUniqueId())) {
			this.diamondsMined.put(event.getPlayer().getUniqueId(), 0);
		}
		if (!this.goldMined.containsKey(event.getPlayer().getUniqueId())) {
			this.goldMined.put(event.getPlayer().getUniqueId(), 0);
		}
		if (!this.ironMined.containsKey(event.getPlayer().getUniqueId())) {
			this.ironMined.put(event.getPlayer().getUniqueId(), 0);
		}

		Player player = event.getPlayer();
		if (event.getBlock().getType() == Material.DIAMOND_ORE) {
			// Handle diamonds mined
			this.diamondsMined.put(player.getUniqueId(), this.diamondsMined.get(player.getUniqueId()) + 1);
			if (this.diamondsMined.get(player.getUniqueId()) > MAX_DIAMONDS_MINED) {
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
				player.sendMessage(ChatColor.RED + "You can't mine any more diamonds!");
			}
		} else if (event.getBlock().getType() == Material.GOLD_ORE) {
			// Handle gold mined
			this.goldMined.put(player.getUniqueId(), this.goldMined.get(player.getUniqueId()) + 1);
			if (this.goldMined.get(player.getUniqueId()) > MAX_GOLD_MINED) {
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
				player.sendMessage(ChatColor.RED + "You can't mine any more gold!");
			}
		} else if (event.getBlock().getType() == Material.IRON_ORE) {
			// Handle iron mined
			this.ironMined.put(player.getUniqueId(), this.ironMined.get(player.getUniqueId()) + 1);
			if (this.ironMined.get(player.getUniqueId()) > MAX_IRON_MINED) {
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
				player.sendMessage(ChatColor.RED + "You can't mine any more iron!");
			}
		}
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		// First time logging on?
		if (!this.diamondsMined.containsKey(event.getPlayer().getUniqueId())) {
			this.diamondsMined.put(event.getPlayer().getUniqueId(), 0);
			this.goldMined.put(event.getPlayer().getUniqueId(), 0);
			this.ironMined.put(event.getPlayer().getUniqueId(), 0);
		}
	}

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.LEASH);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Limitations");

        List<String> lore = new ArrayList<>();

	    lore.add(ChatColor.AQUA + "- Max of " + LimitationGameMode.MAX_DIAMONDS_MINED + " diamonds can be mined");
	    lore.add(ChatColor.AQUA + "- Max of " + LimitationGameMode.MAX_GOLD_MINED + " gold can be mined");
	    lore.add(ChatColor.AQUA + "- Max of " + LimitationGameMode.MAX_IRON_MINED + " iron can be mined");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "https://www.reddit.com/user/Kosslol";
    }

    @Override
    public void unregister() {
	    BlockBreakEvent.getHandlerList().unregister(this);
		PlayerJoinEvent.getHandlerList().unregister(this);
    }

}
