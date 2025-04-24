package club.minemen.practice.request;

import club.minemen.core.api.request.Request;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;

@RequiredArgsConstructor
public class InventorySnapshotRequest implements Request {

	private final JSONObject inventoryA;
	private final JSONObject inventoryB;
	private final UUID matchId;

	@Override
	public String getPath() {
		return "/matches/insert/inventory";
	}

	@Override
	public Map<String, Object> toMap() {
		return ImmutableMap.of(
				"match-id", this.matchId.toString(),
				"inventory-a", this.inventoryA.toJSONString(),
				"inventory-b", this.inventoryB.toJSONString()
		);
	}

}
