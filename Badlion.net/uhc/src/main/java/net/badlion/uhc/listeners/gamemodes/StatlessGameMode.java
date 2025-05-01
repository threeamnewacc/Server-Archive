package net.badlion.uhc.listeners.gamemodes;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.events.GameStartEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class StatlessGameMode implements GameMode {

    public StatlessGameMode() {
        BadlionUHC.getInstance().getConfigurator().getBooleanOption(BadlionUHC.CONFIG_OPTIONS.STATS.name()).setValue("false");
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        BadlionUHC.getInstance().getConfigurator().getBooleanOption(BadlionUHC.CONFIG_OPTIONS.STATS.name()).setValue("false");
    }

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Statless");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- Stats are disabled for this game");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "Badlion";
    }

    @Override
    public void unregister() {
        GameStartEvent.getHandlerList().unregister(this);
    }

}
