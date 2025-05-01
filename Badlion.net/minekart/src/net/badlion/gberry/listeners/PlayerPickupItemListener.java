package net.badlion.gberry.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.tinywebteam.badlion.MineKart;
import com.tinywebteam.badlion.Racer;
import com.tinywebteam.badlion.tasks.UnlockPlayerSlowTask;

public class PlayerPickupItemListener implements Listener {
	
	private MineKart plugin;
	
	public PlayerPickupItemListener(MineKart plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (event.getItem().getItemStack().getTypeId() == 46) {
			Player player = event.getPlayer();
			//player.getWorld().createExplosion(player.getLocation(), 100);
			player.sendMessage(ChatColor.RED + "You hit a landmine.  Stunned for 2 seconds.");
			player.playSound(player.getLocation(), Sound.EXPLODE, 1, 1);
			Racer racer = this.plugin.getPlayerToRacer().get(player);
			racer.setLockSpeedChange(true);
			this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new UnlockPlayerSlowTask(this.plugin, racer), 40);
			racer.getRace().getItemsOnTrack().remove(event.getItem());
			racer.getHorse().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 100), true); // wait 2s
			event.setCancelled(true);
			event.getItem().remove();
			//this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, new RemoveItemTask(this.plugin, event.getItem()), 2);
			//event.getItem().setItemStack(new ItemStack(Material.AIR));
			//player.updateInventory();
			//event.setCancelled(true);
		}
	}

}
