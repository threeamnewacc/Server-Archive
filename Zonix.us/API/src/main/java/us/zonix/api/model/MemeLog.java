package us.zonix.api.model;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "anticheat")
public class MemeLog {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "message")
    private String message;

    @Column(name = "time")
    private Timestamp timestamp;

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("id", this.id);
        object.addProperty("uuid", this.uuid);
        object.addProperty("message", this.message);
        object.addProperty("timestamp", this.timestamp.getTime());
        return object;
    }

}
