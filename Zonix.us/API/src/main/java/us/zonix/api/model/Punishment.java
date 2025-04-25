package us.zonix.api.model;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "punishments")
public class Punishment {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @NotNull
    private String uuid;

    @NotNull
    private String type;

    @NotNull
    @Column(name = "added_at")
    private Timestamp addedAt;

    @Column(name = "added_by")
    private String addedBy;

    @NotNull
    private String reason;

    @Column(name = "removed_at")
    private Timestamp removedAt;

    @Column(name = "removed_by")
    private String removedBy;

    @Column(name = "removed_reason")
    private String removedReason;

    @NotNull
    private Long duration;

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("id", this.id);
        object.addProperty("type", this.type);
        object.addProperty("uuid", this.uuid);
        object.addProperty("added_by", this.addedBy);
        object.addProperty("added_at", this.addedAt.getTime());
        object.addProperty("reason", this.reason);
        object.addProperty("removed_by", this.removedBy);
        object.addProperty("removed_at", this.removedAt == null ? null : this.removedAt.getTime());
        object.addProperty("removed_reason", this.removedReason);
        object.addProperty("duration", this.duration);
        return object;
    }

}
