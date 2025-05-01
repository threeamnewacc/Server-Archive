package net.badlion.uhc.listeners.gamemodes;

import net.badlion.gberry.UnregistrableListener;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.events.PVPProtectionTurnedOnEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class VaeconGameMode implements GameMode {

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.WATCH);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Vaecon");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- PVP is turned on and off every 5 minutes");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "http://twitch.tv/vaecon";
    }

    @EventHandler
    public void onPVPTurnedOn(PVPProtectionTurnedOnEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                BadlionUHC.getInstance().setPVP(!BadlionUHC.getInstance().isPVP());
                Bukkit.broadcastMessage(ChatColor.AQUA + "PvP is now " + (BadlionUHC.getInstance().isPVP() ? "enabled" : "disabled" ) + "!");
            }

        }.runTaskTimer(BadlionUHC.getInstance(), 20 * 60 * 5, 20 * 60 * 5);
    }

    @Override
    public void unregister() {
        PVPProtectionTurnedOnEvent.getHandlerList().unregister(this);
    }

}
