package us.zonix.core.api.callback;

import org.bukkit.Bukkit;
import us.zonix.core.shared.api.callback.Callback;
import us.zonix.core.shared.api.callback.ErrorCallback;

public abstract class AbstractBukkitCallback implements Callback, ErrorCallback {

	@Override
	public void onError(String message) {
		Bukkit.getLogger().severe("[Callback Error]: " + message);
	}

}
