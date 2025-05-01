package net.badlion.gcheat.listeners;

import net.badlion.gberry.events.GSyncEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class GSyncListener implements Listener {

    @EventHandler
    public void onGsync(GSyncEvent event) {
        if (event.getArgs().size() < 5) {
            return;
        }

        String subChannel = event.getArgs().get(0);
        if (subChannel.equals("GCheat")) {
            String msg = event.getArgs().get(1);

            if (msg.equals("ForgeMod") && event.getArgs().size() == 5) {
                UUID uuid = UUID.fromString(event.getArgs().get(2));
                String mod = event.getArgs().get(3);
                String version = event.getArgs().get(4);

            }
        }
    }

}
