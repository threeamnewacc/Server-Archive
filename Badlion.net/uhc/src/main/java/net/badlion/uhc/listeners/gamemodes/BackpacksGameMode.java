package net.badlion.uhc.listeners.gamemodes;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.events.UHCTeleportPlayerLocationEvent;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackpacksGameMode implements GameMode {

	public static Map<Integer, Inventory> teamInventories = new HashMap<>();

    // TODO: Drop items on last player's death

	@EventHandler
	public void onGameStart(UHCTeleportPlayerLocationEvent event) {
        UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId());

        if (!teamInventories.containsKey(uhcPlayer.getTeam().getTeamNumber())) {
			teamInventories.put(uhcPlayer.getTeam().getTeamNumber(), BadlionUHC.getInstance().getServer().createInventory(null, 27, ChatColor.AQUA + "Team #" + uhcPlayer.getTeam().getTeamNumber() + "'s Inventory"));
		}
	}

    @EventHandler
    public void onPreCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().equalsIgnoreCase("/bp")) {
            event.setCancelled(true);
            event.getPlayer().performCommand("team inventory");
        }
    }

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Backpacks");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- Have access to an inventory that is shared by your team");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "https://www.reddit.com/user/Audicyy & https://www.reddit.com/user/neislon241";
    }

    @Override
    public void unregister() {
        UHCTeleportPlayerLocationEvent.getHandlerList().unregister(this);
        PlayerCommandPreprocessEvent.getHandlerList().unregister(this);
    }

}
