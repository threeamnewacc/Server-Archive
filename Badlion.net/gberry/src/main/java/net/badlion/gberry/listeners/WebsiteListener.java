package net.badlion.gberry.listeners;

import com.google.common.base.Joiner;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.GSyncEvent;
import net.badlion.gberry.events.MCPCommandEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

// TODO: Next version allow AND or OR clauses for reboot combinations
// TODO: Example: Contains "sg" AND No One Online
public class WebsiteListener implements Listener {

    @EventHandler
    public void onWebsiteSyncEvent(GSyncEvent event) {
        List<String> args = event.getArgs();
        if (event.getArgs().size() < 4) {
            return;
        }

        String subChannel = event.getArgs().get(0);
        if (subChannel.equals("WebSync")) {
            String typeOfSync = event.getArgs().get(1);
            String serverOrPluginName = event.getArgs().get(2);
            String command = Joiner.on(" ").skipNulls().join(event.getArgs().subList(3, event.getArgs().size()));

            if (typeOfSync.equals("StartsWith") && Gberry.serverName.startsWith(serverOrPluginName)) {
                this.executeCommand(typeOfSync, command);
            } else if (typeOfSync.equals("Contains") && Gberry.serverName.contains(serverOrPluginName)) {
                this.executeCommand(typeOfSync, command);
            } else if (typeOfSync.equals("EndsWith") && Gberry.serverName.endsWith(serverOrPluginName)) {
                this.executeCommand(typeOfSync, command);
            } else if (typeOfSync.equals("Plugin") && Bukkit.getPluginManager().getPlugin(serverOrPluginName) != null) {
                this.executeCommand(typeOfSync, command);
            } else if (typeOfSync.equals("NotContains") && !Gberry.serverName.contains(serverOrPluginName)) {
                this.executeCommand(typeOfSync, command);
            } else if (typeOfSync.equals("NoOneOnline") && Bukkit.getCurrentPlayers() == 0) {
                this.executeCommand(typeOfSync, command);
            }
        }
    }

    private void executeCommand(String typeOfSync, String command) {
        Bukkit.getLogger().info("Web Command " + typeOfSync + " executed " + command);

        MCPCommandEvent event = new MCPCommandEvent(typeOfSync, command);
        Gberry.plugin.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

}
