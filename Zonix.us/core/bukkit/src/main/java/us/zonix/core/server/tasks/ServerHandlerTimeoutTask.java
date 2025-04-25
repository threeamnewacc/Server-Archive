package us.zonix.core.server.tasks;

import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.core.CorePlugin;
import us.zonix.core.server.ServerData;

import java.util.Iterator;

@RequiredArgsConstructor
public class ServerHandlerTimeoutTask extends BukkitRunnable {

    private static final long TIME_OUT_DELAY = 15_000L;
    private final CorePlugin plugin;

    @Override
    public void run() {
        Iterator<String> serverIterator = this.plugin.getRedisManager().getServers().keySet().iterator();

        while (serverIterator.hasNext()) {

            ServerData serverData = this.plugin.getRedisManager().getServers().get(serverIterator.next());

            if (serverData != null) {
                if (System.currentTimeMillis() - serverData.getLastUpdate() >= ServerHandlerTimeoutTask.TIME_OUT_DELAY) {
                    serverIterator.remove();
                    this.plugin.getLogger().warning("The server \"" + serverData.getServerName() + "\" was removed due to it exceeding the timeout delay for heartbeats.");
                }
            }
        }
    }

}
