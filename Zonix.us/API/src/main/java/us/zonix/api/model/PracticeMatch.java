package us.zonix.api.model;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "practice_match")
public class PracticeMatch implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "winner_uuid")
    private String winnerUuid = null;

    @Column(name = "loser_uuid")
    private String loserUuid = null;

    @Column(name = "winner_elo_change")
    private int winnerEloChange;

    @Column(name = "winner_new_elo")
    private int winnerNewElo;

    @Column(name = "loser_elo_change")
    private int loserEloChange;

    @Column(name = "loser_new_elo")
    private int loserNewElo;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("winner_uuid", this.winnerUuid);
        object.addProperty("loser_uuid", this.loserUuid);
        object.addProperty("winner_elo_change", this.winnerEloChange);
        object.addProperty("winner_new_elo", this.winnerNewElo);
        object.addProperty("loser_elo_change", this.loserEloChange);
        object.addProperty("loser_new_elo", this.loserNewElo);
        object.addProperty("timestamp", this.timestamp.getTime());
        return object;
    }

}
