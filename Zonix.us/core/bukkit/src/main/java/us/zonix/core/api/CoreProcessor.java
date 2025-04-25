package us.zonix.core.api;

import us.zonix.core.CorePlugin;
import us.zonix.core.shared.api.processor.AbstractRequestProcessor;

public class CoreProcessor extends AbstractRequestProcessor {

	private final CorePlugin plugin;

	public CoreProcessor(CorePlugin plugin, String apiUrl, String apiKey) {
		super(apiUrl, apiKey);

		this.plugin = plugin;
	}

	@Override
	public boolean shouldSend() {
		return !this.plugin.getServer().isPrimaryThread();
	}

	@Override
	public void runTask(Runnable runnable) {
		if (this.plugin.getServer().isPrimaryThread()) {
			runnable.run();
		}
		else {
			this.plugin.getServer().getScheduler().runTask(this.plugin, runnable);
		}
	}

	@Override
	public void runTaskAsynchronously(Runnable runnable) {
		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, runnable);
	}

}
