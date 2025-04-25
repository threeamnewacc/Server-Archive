package us.zonix.api.model;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "friends")
public class Friend {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @NotNull private String sender;
    @NotNull private String receiver;
    private Timestamp sentTimestamp;
    private Timestamp acceptedTimestamp;
    private boolean accepted;

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("sender", this.sender);
        object.addProperty("receiver", this.receiver);
        object.addProperty("sent_timestamp", this.sentTimestamp.getTime());

        if (this.acceptedTimestamp != null) {
            object.addProperty("accepted_timestamp", this.acceptedTimestamp.getTime());
        }

        object.addProperty("accepted", this.accepted);

        return object;
    }

}
