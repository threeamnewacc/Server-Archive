package net.badlion.gberry.listeners;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.GSyncEvent;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class AprilFoolsListener implements Listener {

	@EventHandler
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().equalsIgnoreCase("/aprilfools") && event.getPlayer().hasPermission("badlion.admin")) {
			Gberry.broadcastSound(EnumCommon.getEnumValueOf(Sound.class, "WITHER_DEATH", "ENTITY_WITHER_DEATH"), 1F, (float) (0.4F + Math.random() * 0.6F));

			event.setCancelled(true);

			// Disabled for future uses, shouldn't be network wide
			/*BukkitUtil.runTaskAsync(new Runnable() {
				@Override
				public void run() {
					List<String> list = new ArrayList<>();
					list.add("aprilfools");

					Gberry.sendGSyncEvent(list);
				}
			});*/
		}
	}

	@EventHandler
	public void onGSyncEvent(GSyncEvent event) {
		if (event.getArgs().get(0).equals("aprilfools")) {
			Gberry.broadcastSound(EnumCommon.getEnumValueOf(Sound.class, "WITHER_DEATH", "ENTITY_WITHER_DEATH"), 1F, (float) (0.4F + Math.random() * 0.6F));
		}
	}

	@EventHandler(priority = EventPriority.LASTEST, ignoreCancelled = true)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if (Math.random() <= 0.02) {
			event.getDamager().getWorld().playSound(event.getDamager().getLocation(), EnumCommon.getEnumValueOf(Sound.class, "CHICKEN_HURT", "ENTITY_CHICKEN_HURT"), 1F, (float) (0.3F + Math.random() * 1.2F));
		}
	}

}
