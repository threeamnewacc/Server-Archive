package net.badlion.gguard.listeners;

import net.badlion.gguard.GGuard;
import net.badlion.gguard.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ProjectileListener implements Listener {
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onEPThrow(PlayerInteractEvent event) {
		// If they are right clicking, and have an EP in hand
		if ((event.getAction() == Action.LEFT_CLICK_AIR) || (event.getAction() == Action.LEFT_CLICK_BLOCK) || 
				(event.getItem() == null) || (event.getItem().getType() != Material.ENDER_PEARL)) {
			return;
	    }

		Location location = event.getPlayer().getLocation();
		ProtectedRegion region = GGuard.getInstance().getProtectedRegion(location, GGuard.getInstance().getProtectedRegions());
		if (region != null && !region.isAllowEnderPearls()) {
			event.getPlayer().sendMessage(ChatColor.RED + "Not allowed to use ender pearls here.");
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEPLand(PlayerTeleportEvent event) {
		if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
			Location location = event.getTo();
			ProtectedRegion region = GGuard.getInstance().getProtectedRegion(location, GGuard.getInstance().getProtectedRegions());
			if (region != null && !region.isAllowEnderPearlIn()) {
				event.getPlayer().sendMessage(ChatColor.RED + "Not allowed to use ender pearls here.");
				event.setCancelled(true);
			}
		}
	}
	
//	@EventHandler(priority=EventPriority.LOWEST)
//	public void onPotionThrow(PlayerInteractEvent event) {
//		// If they are right clicking and have a splash pot in hand
//		// HACK: https://github.com/sk89q/worldguard/commit/8dec32fa6a1238a11743cea8b8302a6c9d2aaa55
//		if ((event.getAction() == Action.LEFT_CLICK_AIR) || (event.getAction() == Action.LEFT_CLICK_BLOCK) ||
//				(event.getItem() == null) || (event.getItem().getType() != Material.POTION) || /*(Potion.fromItemStack(event.getItem()).isSplash())*/
//				event.getItem().getDurability() == 0 || Potion.fromDamage(event.getItem().getDurability() & 0x3F).isSplash()) {
//			return;
//	    }
//
//		UnsafeLocation location = event.getPlayer().getLocation();
//		ProtectedRegion region = GGuard.getInstance().getProtectedRegion(event.getPlayer().getLocation(), GGuard.getInstance().getProtectedRegions());
//		if (region != null && !region.isAllowPotionEffects()) {
//			event.getPlayer().sendMessage(ChatColor.RED + "Not allowed to use potions here.");
//			event.setCancelled(true);
//		}
//	}

}
