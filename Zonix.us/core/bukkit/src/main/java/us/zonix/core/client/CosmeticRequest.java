package us.zonix.core.client;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import us.zonix.core.shared.api.request.Request;

@RequiredArgsConstructor
public final class CosmeticRequest implements Request {

	private final String subCommand;
	private final String player;
	private final String type;
	private final String data;

	@Override public String getPath() {
		return "/client/cosmetic/" + this.player + "/" + this.subCommand;
	}

	@Override public Map<String, Object> toMap() {
		return ImmutableMap.of(
				"type", this.type,
				"data", this.data
		);
	}

}
