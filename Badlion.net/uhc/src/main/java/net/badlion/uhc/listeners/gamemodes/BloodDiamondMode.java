package net.badlion.uhc.listeners.gamemodes;

import net.badlion.gberry.utils.MessageUtil;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BloodDiamondMode implements GameMode {

	private Set<UUID> uuids = new HashSet<>();

    @EventHandler
    public void onDiamondBroken(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.DIAMOND_ORE) {
	        this.uuids.add(event.getPlayer().getUniqueId());
	        event.getPlayer().damage(1);
	        this.uuids.remove(event.getPlayer().getUniqueId());

	        UHCPlayerManager.updateHealthScores(event.getPlayer());
        }
    }

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerFuckedUpEvent(PlayerDeathEvent event) {
		if (this.uuids.contains(event.getEntity().getUniqueId())) {
			event.getEntity().setLastDamageCause(MessageUtil.CUSTOM_ENTITY_DAMAGE_EVENT);
			event.setDeathMessage(" overloaded himself with diamonds and died");
		}
	}

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.DIAMOND_ORE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Blood Diamond");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- Take 1/2 a heart for each diamond mined");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "https://www.reddit.com/u/pipiter";
    }

    @Override
    public void unregister() {
        BlockBreakEvent.getHandlerList().unregister(this);
    }

}
