package club.minemen.hcfactions.fight;

import club.minemen.core.util.CustomLocation;
import com.google.gson.JsonObject;
import java.util.UUID;

public class Fight {

	private UUID fightUuid;
	private UUID playerUuid;
	private FightKillerType killerType;
	private String killerName;
	private String killerUuid;
	private CustomLocation location;
	private long timestamp;

	public JsonObject toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("fight_uuid", this.fightUuid.toString());
		object.addProperty("player_uuid", this.playerUuid.toString());
		object.addProperty("killer_type", this.killerType.toString());
		object.addProperty("killer_name", this.killerName);
		object.addProperty("killer_uuid", this.killerUuid);
		object.addProperty("location", CustomLocation.locationToString(this.location));
		object.addProperty("timestamp", this.timestamp);
		return object;
	}

}
