package net.badlion.gfactions.tasks;

import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.listeners.CombatTagListener;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CombatLoggerDisappearTask extends BukkitRunnable {

	private Player player;

    public CombatLoggerDisappearTask(Player player) {
	    this.player = player;
    }

    @Override
    public void run() {
	    CombatTagListener.LoggerNPC loggerNPC = GFactions.plugin.getCombatLogNPC().get(this.player.getUniqueId());

	    if (loggerNPC != null) {
		    /*World world = loggerNPC.getEntity().getWorld();
		    Location location = loggerNPC.getEntity().getLocation();
		    for (ItemStack item : ((ItemStack[]) loggerNPC.getEntity().getMetadata("CombatLoggerInventory").get(0).value())) {
			    if (item != null && item.getType() != Material.AIR) {
				    world.dropItemNaturally(location, item);
			    }
		    }

		    for (ItemStack item : ((ItemStack[]) loggerNPC.getEntity().getMetadata("CombatLoggerArmorInventory").get(0).value())) {
			    if (item != null && item.getType() != Material.AIR) {
				    world.dropItemNaturally(location, item);
			    }
		    }

		    // Send death message
		    Player killer = loggerNPC.getEntity().getKiller();
		    if (killer != null) {
			    Gberry.broadcastMessage(ChatColor.YELLOW + loggerNPC.getEntity().getCustomName() + " (CombatLogger)"
					    + ChatColor.RED + " was slain by " + ChatColor.YELLOW + killer.getName());
		    } else {
			    Gberry.broadcastMessage(ChatColor.YELLOW + loggerNPC.getEntity().getCustomName() + " (CombatLogger)"
					    + ChatColor.RED + " has died");
		    }*/

		    // We want the player to live

		    GFactions.plugin.getPlayerNPCDied().add(loggerNPC.getUUID());
		    loggerNPC.remove();
	    }
    }

}
