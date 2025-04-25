package us.zonix.core.punishment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.core.profile.Profile;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.Clickable;
import us.zonix.core.util.DateUtil;
import us.zonix.core.util.UUIDType;

@Getter
public class Punishment {

	private int id;
	private PunishmentType type;
	private UUID uuid;
	private UUID addedBy;
	private Long addedAt;
	private String reason;
	@Setter private UUID removedBy;
	@Setter private Long removedAt;
	@Setter private String removedReason;
	private Long duration;

	public Punishment(int id, PunishmentType type, UUID uuid, UUID addedBy, Long addedAt, String reason, long duration) {
		this.uuid = uuid;
		this.type = type;
		this.addedBy = addedBy;
		this.addedAt = addedAt;
		this.reason = reason;
		this.duration = duration;
	}

	public Punishment(int id, PunishmentType type, UUID uuid, UUID addedBy, Long addedAt, String reason, UUID removedBy, Long removedAt, String removedReason, Long duration) {
		this.id = id;
		this.type = type;
		this.uuid = uuid;
		this.addedBy = addedBy;
		this.addedAt = addedAt;
		this.reason = reason;
		this.removedBy = removedBy;
		this.removedAt = removedAt;
		this.removedReason = removedReason;
		this.duration = duration;
	}

	public boolean isActive() {
		if (this.isRemoved()) {
			return false;
		}

		if (this.isPermanent()) {
			return true;
		}

		if ((System.currentTimeMillis() < (this.addedAt + this.duration))) {
			return true;
		}

		return false;
	}

	public boolean isRemoved() {
		return this.removedReason != null;
	}

	public boolean isPermanent() {
		return this.duration == -1;
	}

	public String getTimeLeft() {
		if (this.isRemoved()) {
			return "Removed";
		}

		if (this.isPermanent()) {
			return "Permanent";
		}

		if (!(this.isActive())) {
			return "Expired";
		}

		Calendar from = Calendar.getInstance();
		from.setTime(new Date(System.currentTimeMillis()));

		Calendar to = Calendar.getInstance();
		to.setTime(new Date(this.addedAt + this.duration));

		return DateUtil.formatDateDiff(from, to);
	}

	public String getAddedAtFormatted() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(this.addedAt));

		return calendar.getTime().toString();
	}

	public String getRemovedAtFormatted() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(this.removedAt));

		return calendar.getTime().toString();
	}

	public void announce(String name, String sender, boolean silent, boolean undo) {
		String context = undo ? this.type.getUndoContext() : this.type.getContext();

		for (Player player : Bukkit.getOnlinePlayers()) {
			Profile profile = Profile.getByUuid(player.getUniqueId());

			if (profile != null && profile.getRank().isAboveOrEqual(Rank.TRIAL_MOD)) {
				Clickable clickable = new Clickable(ChatColor.GREEN + name + " was " + context + " by " + sender + ".", ChatColor.YELLOW + "Reason: " + ChatColor.RED + reason + "\n" + ChatColor.YELLOW + "Duration: " + ChatColor.RED + this.getTimeLeft(), "");
				clickable.sendToPlayer(player);
			}
			else {
				if (!silent || player.getName().equals(name)) {
					player.sendMessage(ChatColor.GREEN + name + " was " + context + " by " + sender + ".");
				}
			}
		}

		Bukkit.getConsoleSender().sendMessage((silent && this.type.name().contains("BAN") && !undo ? ChatColor.RED + "(Punishment) " : "") + ChatColor.GREEN + name + " was " + context + " by " + sender + " for " + reason + ".");
		Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Duration: " + ChatColor.RED + this.getTimeLeft());
	}

	public JsonObject toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("id", this.id);

		if(this.type != null) {
			object.addProperty("type", this.type.name());
		}

		object.addProperty("uuid", this.uuid.toString());
		object.addProperty("added_by", this.addedBy == null ? null : this.addedBy.toString());
		object.addProperty("added_at", this.addedAt);
		object.addProperty("reason", this.reason);
		object.addProperty("removed_by", this.removedBy == null ? null : this.removedBy.toString());
		object.addProperty("removed_at", this.removedAt);
		object.addProperty("removed_reason", this.removedReason);
		object.addProperty("duration", this.duration);
		return object;
	}

	public static Punishment fromJson(JsonObject object) {
		int id = object.get("id").getAsInt();
		PunishmentType type = PunishmentType.valueOf(object.get("type").getAsString());
		UUID uuid = UUIDType.fromString(object.get("uuid").getAsString());
		UUID addedBy = object.get("added_by").isJsonNull() ? null : UUIDType.fromString(object.get("added_by").getAsString());
		Long addedAt = object.get("added_at").getAsLong();
		String reason = object.get("reason").getAsString();
		UUID removedBy = null;
		Long removedAt = null;
		String removedReason = null;
		Long duration = object.get("duration").isJsonNull() ? null : object.get("duration").getAsLong();

		if (!object.get("removed_reason").isJsonNull()) {
			removedBy = object.get("removed_by").isJsonNull() ? null : UUIDType.fromString(object.get("removed_by").getAsString());
			removedAt = object.get("removed_at").getAsLong();
			removedReason = object.get("removed_reason").isJsonNull() ? null : object.get("removed_reason").getAsString();
		}

		return new Punishment(id, type, uuid, addedBy, addedAt, reason, removedBy, removedAt, removedReason, duration);
	}

}
