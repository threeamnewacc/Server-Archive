package us.zonix.api.model;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "premium")
public class PremiumMatches implements Serializable {

    @Id
    private String uuid;
    
    @Column(name = "premium_matches_played")
    private int premiumMatchesPlayed = 0;

    @Column(name = "premium_matches_extra")
    private int premiumMatchesExtra = 0;

    @Column(name = "premium_wins")
    private int premiumWins = 0;

    @Column(name = "premium_losses")
    private int premiumLosses = 0;

    @Column(name = "premium_elo")
    private int premiumElo = 1000;

    public JsonObject toJson() {
        JsonObject object = new JsonObject();

        object.addProperty("uuid", this.uuid);
        object.addProperty("premium_matches_extra", this.premiumMatchesExtra);
        object.addProperty("premium_matches_played", this.premiumMatchesPlayed);
        object.addProperty("premium_wins", this.premiumWins);
        object.addProperty("premium_losses", this.premiumLosses);
        object.addProperty("premium_elo", this.premiumElo);

        return object;
    }
    
}
