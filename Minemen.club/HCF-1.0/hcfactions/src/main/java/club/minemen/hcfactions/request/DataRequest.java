package club.minemen.hcfactions.request;

import club.minemen.core.api.impl.RegisterRequest;
import club.minemen.core.api.request.Request;
import club.minemen.core.util.finalutil.MapUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DataRequest implements Request {

	private final String path;

	@Override public String getPath() {
		return "/factions/" + this.path;
	}

	@Override public Map<String, Object> toMap() {
		return null;
	}

	public static final class LoadRequest extends RegisterRequest {
		public LoadRequest(UUID uuid) {
			super("fetch_by_uuid/" + uuid.toString());
		}

		@Override public Map<String, Object> toMap() {
			return null;
		}
	}

	public static final class SaveRequest extends RegisterRequest {
		private final UUID uuid;
		private final JsonArray kills;
		private final JsonArray deaths;
		private final JsonObject ores;
		private final UUID factionUuid;

		public SaveRequest(UUID uuid, JsonArray kills, JsonArray deaths, JsonObject ores, UUID factionUuid) {
			super("save/");

			this.uuid = uuid;
			this.kills = kills;
			this.deaths = deaths;
			this.ores = ores;
			this.factionUuid = factionUuid;
		}

		@Override public Map<String, Object> toMap() {
			return MapUtil.of(
					"uuid", this.uuid,
					"kills", this.kills.toString(),
					"deaths", this.deaths.toString(),
					"ores", this.ores.toString(),
					"faction", this.factionUuid == null ? "NONE" : this.factionUuid.toString()
			);
		}
	}

}
