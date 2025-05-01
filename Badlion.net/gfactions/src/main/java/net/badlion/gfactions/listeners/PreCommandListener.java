package net.badlion.gfactions.listeners;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import net.badlion.gfactions.GFactions;
//import net.badlion.gfactions.tasks.tp.FHomeTeleportTask;
import net.badlion.gfactions.tasks.FHomeTeleportTask;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PreCommandListener implements Listener {
	
	private GFactions plugin;
	
	public PreCommandListener(GFactions plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPreCommandListener(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().toLowerCase().startsWith("/help") || event.getMessage().toLowerCase().startsWith("/?")) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.GOLD + "Read help information at spawn on the signs!");
		} else if (event.getMessage().toLowerCase().startsWith("/f home")) {
            if (this.plugin.isInCombat(event.getPlayer())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "Cannot /f home while in combat.");
            } else if (event.getPlayer().getWorld().getEnvironment() == World.Environment.THE_END) {
	            event.setCancelled(true);
	            event.getPlayer().sendMessage(ChatColor.RED + "Cannot /f home in The End.");
            } else {
                // Hack in a CD
                Player player = event.getPlayer();

                // Do they even have a home?
                FPlayer fplayer = FPlayers.i.get(player);
                if (fplayer.getFaction().getHome() == null) {
                    player.sendMessage(ChatColor.RED + "You do not have a /f home set. Use /f sethome to set one.");
                    event.setCancelled(true);
                    return;
                }

                // Hashcode
                String hashCode = this.plugin.getCmdSigns().generateHash();

	            if (event.getPlayer().getWorld().getEnvironment() == World.Environment.NETHER) {
		            player.sendMessage(ChatColor.GOLD + "Teleporting to your faction home. Do not move for 14 seconds.");
		            new FHomeTeleportTask(player.getLocation(), hashCode, player.getUniqueId(), 280).runTaskTimer(this.plugin, 0L, 5L);
	            } else {
		            player.sendMessage(ChatColor.GOLD + "Teleporting to your faction home. Do not move for 7 seconds.");
		            new FHomeTeleportTask(player.getLocation(), hashCode, player.getUniqueId()).runTaskTimer(this.plugin, 0L, 5L);
	            }

                event.setCancelled(true);
            }
        } else if (event.getMessage().toLowerCase().startsWith("/f stuck")) {
            event.getPlayer().performCommand("stuck");
            event.setCancelled(true);
        } else if (event.getMessage().toLowerCase().startsWith("/f access")) {
            event.setCancelled(true);
        }
        /*else if (event.getMessage().toLowerCase().startsWith("/pv")) {
			if (this.plugin.getCombatTagApi().isInCombat(event.getPlayer())) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "Cannot /pv while in combat.");
			}
		}*/
		//} else if (event.getMessage().startsWith("/pay")) {
        //    event.setMessage("/money pay" + event.getMessage().substring(4));
        /*} else if (event.getMessage().equals("/f c f") || event.getMessage().equals("/f")) {
            event.setMessage("/ch f");
        } else if (event.getMessage().equals("/g")) {
            event.setMessage("/ch g");
        } else if (event.getMessage().equals("/e")) {
            event.setMessage("/ch e");
        }*/
	}



}
