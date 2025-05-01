package net.badlion.capturetheflag.listeners;

import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class GlobalListener implements Listener {

    @EventHandler
    public void onPlayerJoinMessage(final PlayerJoinEvent event){
	    event.getPlayer().sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));
	    event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Welcome to " + ChatColor.AQUA + "Badlion Capture the Flag" + ChatColor.DARK_GREEN + ".");
	    event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Please report any issues on the forums.");
	    event.getPlayer().sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));
    }
}
