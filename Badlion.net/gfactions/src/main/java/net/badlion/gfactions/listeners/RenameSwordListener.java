package net.badlion.gfactions.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.regex.Pattern;

public class RenameSwordListener implements Listener {

	// % sign is not allowed because that [le]terally is what breaks this
	private Pattern p = Pattern.compile("^[a-zA-Z0-9\\s§!@#$^&*\\(\\)\\[\\]\\{\\}<>\\-\\|\\+=\\\\/]+$", Pattern.CASE_INSENSITIVE);

	@EventHandler
	public void onPlayerUserItem(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		if (item != null) {
			ItemMeta meta = item.getItemMeta();
			if (meta != null) {
				if (meta.hasDisplayName()) {
					if (!this.p.matcher(meta.getDisplayName()).find()) {
						meta.setDisplayName("");
						item.setItemMeta(meta);
						event.getPlayer().setItemInHand(item);
						event.getPlayer().sendMessage(ChatColor.RED + "Item renamed due to illegal characters in it.");
					}
				}
			}
		}
	}

}
