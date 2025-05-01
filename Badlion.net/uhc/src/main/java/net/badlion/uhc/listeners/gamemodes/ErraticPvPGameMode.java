package net.badlion.uhc.listeners.gamemodes;

import net.badlion.gberry.Gberry;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.events.GameStartEvent;
import net.badlion.uhc.tasks.ErraticPvPTask;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ErraticPvPGameMode implements GameMode {

	int runnable;

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.IRON_SWORD);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Erratic PvP");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- PvP gets toggled at random intervals");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "Badlion";
    }

	@EventHandler
	public void onGameStart(GameStartEvent event) {
		// When the game starts, start off the fun!
		// It starts at 25 minutes+ so it doesnt screw with the normal pvp toggle
		ErraticPvPTask.taskId = new ErraticPvPTask().runTaskLater(BadlionUHC.getInstance(), Gberry.generateRandomInt(20 * 60 * 25, 20 * 60 * 28)).getTaskId();
	}

    @Override
    public void unregister() {
	    BadlionUHC.getInstance().getServer().getScheduler().cancelTask(this.runnable);
        GameStartEvent.getHandlerList().unregister(this);
    }

}
