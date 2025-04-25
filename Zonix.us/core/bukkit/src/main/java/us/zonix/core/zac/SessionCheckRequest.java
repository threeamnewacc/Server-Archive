package us.zonix.core.zac;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import us.zonix.core.shared.api.request.Request;

@RequiredArgsConstructor
public class SessionCheckRequest implements Request {

	private final UUID uuid;

	@Override public String getPath() {
		return "/zac/session/get";
	}

	@Override public Map<String, Object> toMap() {
		return ImmutableMap.of("uuid", this.uuid.toString());
	}

}
