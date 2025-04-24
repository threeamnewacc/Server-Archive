package club.minemen.events.event;

import club.minemen.events.util.JsonUtil;
import com.google.gson.JsonElement;
import java.util.Collections;
import java.util.Set;

public interface Event<T> {

	String getScoreboardDisplay();

	String getName();

	boolean isRunning();

	boolean canStart();

	void onStart();

	void onEnd(T winner);

	default void onTick() {

	}

	default Set<Event> getActiveEvents() {
		return Collections.singleton(this);
	}

	default JsonElement toJson() {
		return JsonUtil.toJson(this);
	}

}
