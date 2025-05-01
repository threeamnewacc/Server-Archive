package net.badlion.gfactions.listeners;

import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.tasks.manhunt.ManHuntTrackerTask;
import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ManHuntListener implements Listener {

    private GFactions plugin;

    public ManHuntListener(GFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void stepOnPressurePlate(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL) && event.getClickedBlock() != null && this.plugin.getManHuntPP() != null
		        && event.getClickedBlock().getLocation().equals(this.plugin.getManHuntPP().getLocation())) {
            if (this.plugin.getManHuntTagged() == null) {
	            // PvP Timer check
	            String uuidString = event.getPlayer().getUniqueId().toString();
	            if (GFactions.plugin.getMapNameToPvPTimeRemaining().containsKey(uuidString) &&
			            GFactions.plugin.getMapNameToJoinTime().containsKey(uuidString)) {
		            int timeRemaining = GFactions.plugin.getMapNameToPvPTimeRemaining().get(uuidString);
		            long timeJoined = GFactions.plugin.getMapNameToJoinTime().get(uuidString);
		            long currentTime = System.currentTimeMillis();

		            // Ok they are still protected...don't allow them to attack others
		            if ((timeJoined + timeRemaining) > currentTime) {
			            event.getPlayer().sendMessage(ChatColor.RED + "You can't participate in Man Hunt with a PvP Timer!");
			            return;
		            }
	            }

	            this.plugin.setManHuntTagged(event.getPlayer());
	            event.getPlayer().sendMessage(ChatColor.YELLOW + "You are now tagged for the Man Hunt! Stay alive for 10 minutes without " +
			            "leaving the warzone or logging out to get a rare prize!");
	            this.plugin.addCombatTagged(event.getPlayer());

	            new ManHuntTrackerTask(this.plugin, event.getPlayer()).runTaskTimer(this.plugin, 0L, 20L); // Run every second
            } else {
                event.getPlayer().sendMessage(ChatColor.RED + "Someone is already tagged for the Man Hunt! Go hunt them down!");
            }
        }
    }

    @EventHandler
    public void taggedNerdCombatLog(PlayerQuitEvent event) {
        if (this.plugin.getManHuntTagged() != null && this.plugin.getManHuntTagged().equals(event.getPlayer())) {
                this.plugin.setManHuntTagged(null);

                Gberry.broadcastMessage(ChatColor.GREEN + "[ManHunt] " + event.getPlayer().getName() + " has logged out like a peasant, the pressure plate is live again at "
                        + this.plugin.getManHuntPP().getX() + ", " + this.plugin.getManHuntPP().getY() + ", " + this.plugin.getManHuntPP().getZ() + "!");
        }
    }

    @EventHandler
    public void killManHuntTarget(PlayerDeathEvent event) {
        if (this.plugin.getManHuntTagged() != null && this.plugin.getManHuntTagged().equals(event.getEntity())) {
            if (event.getEntity().getKiller() != null) { // Has killer
                this.plugin.setManHuntTagged(event.getEntity().getKiller());
                event.getEntity().getKiller().sendMessage(ChatColor.YELLOW + "You are now tagged for Man Hunt! Stay alive for 10 minutes without " +
                        "leaving the warzone or logging out to get a rare prize!");

                Gberry.broadcastMessage(ChatColor.GREEN + "[ManHunt] " + event.getEntity().getKiller().getName() + " has been tagged for killing " + event.getEntity().getName() + " at "
                        + (int) event.getEntity().getKiller().getLocation().getX() + ", " + (int) event.getEntity().getKiller().getLocation().getY() + ", "
                        + (int) event.getEntity().getKiller().getLocation().getZ() + "!");

                this.plugin.addCombatTagged(event.getEntity().getKiller());

                new ManHuntTrackerTask(this.plugin, event.getEntity().getKiller()).runTaskTimer(this.plugin, 0L, 20L); // Run every second
            } else { // isScrub = true; for dying by natural causes
                this.plugin.setManHuntTagged(null);

                Gberry.broadcastMessage(ChatColor.GREEN + "[ManHunt] " + event.getEntity().getName() + " has died by natural causes, the pressure plate is live again at "
                        + this.plugin.getManHuntPP().getX() + ", " + this.plugin.getManHuntPP().getY() + ", " + this.plugin.getManHuntPP().getZ() + "!");
            }
        }
    }

    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerPearl(PlayerInteractEvent event) {
        if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (event.getItem().getType() == Material.ENDER_PEARL) {
                if (this.plugin.getManHuntTagged() != null && this.plugin.getManHuntTagged().equals(event.getPlayer())) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You cannot use enderpearls while tagged for manhunt.");
                    event.setCancelled(true);
                    event.setUseItemInHand(Event.Result.DENY);
                }
            }
        }
    }

}
