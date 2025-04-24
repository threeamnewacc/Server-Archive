package club.minemen.practice.task;

import club.minemen.core.CorePlugin;
import club.minemen.core.api.abstr.AbstractBukkitCallback;
import club.minemen.practice.Practice;
import club.minemen.practice.request.PremiumRequest;
import com.google.gson.JsonElement;
import java.util.TimerTask;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PremiumResetTask extends TimerTask {

	private final Practice plugin = Practice.getInstance();

	@Override
	public void run() {
		CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(new PremiumRequest("reset", "", 0),
				new AbstractBukkitCallback() {
					@Override
					public void callback(JsonElement jsonElement) {
						plugin.getLogger().info("Successfully ran Practice Reset");
					}
				});
	}
}
