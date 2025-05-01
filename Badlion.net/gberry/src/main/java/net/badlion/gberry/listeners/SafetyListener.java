package net.badlion.gberry.listeners;

import net.badlion.gberry.tasks.BanEveryoneTask;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class SafetyListener implements Listener {

    @EventHandler
    public void onPlayerExecuteCommand(PlayerCommandPreprocessEvent event) {
        if (event.getPlayer().isOp() && !BanEveryoneTask.uuids.contains(event.getPlayer().getUniqueId())) {
            event.getPlayer().setOp(false);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + event.getPlayer().getName() + " Exploits");

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().isOp()) {
            event.getPlayer().setOp(false);
        }

	    if (event.getPlayer().getName().equals("SmellyPenguin")) {
		    event.getPlayer().performCommand("report off");
	    }
    }

}
