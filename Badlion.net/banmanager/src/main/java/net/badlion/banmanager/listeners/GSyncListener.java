package net.badlion.banmanager.listeners;

import net.badlion.banmanager.BanManager;
import net.badlion.gberry.events.GSyncEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class GSyncListener implements Listener {

	@EventHandler
	public void onGSyncEvent(GSyncEvent event) {
		if (event.getArgs().size() < 3) {
			return;
		}

		String subChannel = event.getArgs().get(0);
		if (subChannel.equals("BanManager")) {
			String msg = event.getArgs().get(1);
			String uuid = event.getArgs().get(2);

			if (msg.equals("BanSync")) {
				// Don't kick from lobbies
				if (BanManager.isLobby) {
					return;
				}

				Player p = BanManager.getInstance().getServer().getPlayer(UUID.fromString(uuid));
				if (p != null) {
					p.kickPlayer("You have been banned.");
				}
			}
		}
	}

}
