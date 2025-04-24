package club.minemen.practice.request;

import club.minemen.core.api.request.Request;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PremiumRequest implements Request {

	private final String subCommand;
	private final String name;
	private final int amount;

	@Override
	public String getPath() {
		return "/premium/" + this.subCommand + "/" + this.amount;
	}

	@Override
	public Map<String, Object> toMap() {
		return ImmutableMap.of(
				"sub-command", this.subCommand,
				"name", this.name,
				"amount", this.amount
		);
	}

}
