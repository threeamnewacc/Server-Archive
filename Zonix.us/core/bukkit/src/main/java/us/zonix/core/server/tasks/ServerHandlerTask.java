package us.zonix.core.server.tasks;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.core.CorePlugin;

@RequiredArgsConstructor
public class ServerHandlerTask extends BukkitRunnable {

	private final CorePlugin plugin;

	@Override
	public void run() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("server-name", this.plugin.getServerId());
		jsonObject.addProperty("server-type", this.plugin.getServerType().name());
		jsonObject.addProperty("server-state", this.plugin.getServerDataProvider().getState().name());
		jsonObject.addProperty("player-count", this.plugin.getServerDataProvider().getPlayerCount());
		jsonObject.addProperty("player-max", this.plugin.getServer().getMaxPlayers());
		jsonObject.addProperty("whitelisted", this.plugin.getServer().hasWhitelist());

		this.plugin.getRedisManager().writeServer(jsonObject);
	}

}
