package net.badlion.uhc.listeners.gamemodes;

import net.badlion.gberry.utils.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BloodEnchantGameMode implements GameMode {

	private Set<UUID> uuids = new HashSet<>();

    @EventHandler
    public void onPlayerLevelChangeEvent(PlayerLevelChangeEvent event) {
        if (event.getOldLevel() > event.getNewLevel()) {
	        double damage = event.getOldLevel() - event.getNewLevel();

	        this.uuids.add(event.getPlayer().getUniqueId());
	        event.getPlayer().damage(damage);
	        this.uuids.remove(event.getPlayer().getUniqueId());
        }
    }

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerFuckedUpEvent(PlayerDeathEvent event) {
		if (this.uuids.contains(event.getEntity().getUniqueId())) {
			event.getEntity().setLastDamageCause(MessageUtil.CUSTOM_ENTITY_DAMAGE_EVENT);
			event.setDeathMessage(" enchanted to his own demise");
		}
	}

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.ENCHANTMENT_TABLE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Blood Enchant");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- You lose half a heart for every level you enchant");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "https://www.reddit.com/user/Yerru";
    }

    @Override
    public void unregister() {
        EnchantItemEvent.getHandlerList().unregister(this);
    }

}
