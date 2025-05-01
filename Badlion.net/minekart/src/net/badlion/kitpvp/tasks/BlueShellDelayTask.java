package net.badlion.kitpvp.tasks;

import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.tinywebteam.badlion.MineKart;
import com.tinywebteam.badlion.Racer;

public class BlueShellDelayTask extends BukkitRunnable {

	private MineKart plugin;
	private Racer racer;
	
	public BlueShellDelayTask(MineKart plugin, Racer racer) {
		this.plugin = plugin;
		this.racer = racer;
	}
	
	@Override
	public void run() {
		this.racer.getHorse().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 100), true);
		this.racer.getPlayer().sendMessage(ChatColor.RED + "Hit by blue missile.  Stunned for 3 seconds.");
		this.racer.setLockSpeedChange(true);
		this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new UnlockPlayerSlowTask(this.plugin, racer), 40);
	}
}
