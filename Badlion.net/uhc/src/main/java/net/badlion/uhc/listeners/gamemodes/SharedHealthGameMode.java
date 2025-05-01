package net.badlion.uhc.listeners.gamemodes;

import net.badlion.combattag.events.CombatTagDamageEvent;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SharedHealthGameMode implements GameMode {

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.POTION, 1, (short) 8229);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Shared Health");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- All damage and healing is shared between teammates");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "Badlion";
    }

    @EventHandler(priority=EventPriority.LAST, ignoreCancelled=true)
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(event.getEntity().getUniqueId());
            if (uhcPlayer.isAliveAndPlaying()) {
	            uhcPlayer.getTeam().damage(event.getFinalDamage(), event.getEntity().getUniqueId());
            }
        }
    }

    @EventHandler(priority=EventPriority.LAST, ignoreCancelled=true)
    public void onCombatTagTakeDamage(CombatTagDamageEvent event) {
        UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(event.getLoggerNPC().getUUID());
        if (uhcPlayer.isAliveAndPlaying()) {
	        uhcPlayer.getTeam().damage(event.getFinalDamage(), event.getLoggerNPC().getUUID());
        }
    }

    @EventHandler(priority=EventPriority.LAST, ignoreCancelled=true)
    public void onPlayerHeal(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(player.getUniqueId());
            if (uhcPlayer.isAliveAndPlaying()) {
                uhcPlayer.getTeam().heal(event.getAmount(), player.getUniqueId());
            }
        }
    }

    @EventHandler(priority=EventPriority.LAST, ignoreCancelled=true)
    public void onAbsorptionHearts(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(player.getUniqueId());
        if (uhcPlayer.isAliveAndPlaying()) {
	        if (event.getItem().getType().equals(Material.GOLDEN_APPLE)) {
		        if ((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.ABSORPTION.name()).getValue()) {
			        uhcPlayer.getTeam().addAbsorption(player.getUniqueId());
		        }
	        }
        }
    }

    @Override
    public void unregister() {
        EntityDamageEvent.getHandlerList().unregister(this);
        CombatTagDamageEvent.getHandlerList().unregister(this);
        EntityRegainHealthEvent.getHandlerList().unregister(this);
        PlayerItemConsumeEvent.getHandlerList().unregister(this);
    }

}
