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
@Entity(name = "practice")
public class PracticeStats implements Serializable {

    @Id
    private String uuid;

    // Common

    @Column(name = "ping_range")
    private int pingRange = 0;

    @Column(name = "elo_range")
    private int eloRange = 0;

    // Event Stats
    @Column(name = "oitc_event_deaths")
    private int oitcEventDeaths = 0;

    @Column(name = "oitc_event_kills")
    private int oitcEventKills = 0;

    @Column(name = "oitc_event_wins")
    private int oitcEventWins = 0;

    @Column(name = "oitc_event_losses")
    private int oitcEventLosses = 0;

    @Column(name = "sumo_event_wins")
    private int sumoEventWins = 0;

    @Column(name = "sumo_event_losses")
    private int sumoEventLosses = 0;

    @Column(name = "redrover_event_wins")
    private int redroverEventWins = 0;

    @Column(name = "redrover_event_losses")
    private int redroverEventLosses = 0;

    @Column(name = "parkour_event_wins")
    private int parkourEventWins = 0;

    @Column(name = "parkour_event_losses")
    private int parkourEventLosses = 0;

    // Kits/Ladders

    @Column(name = "nodebuff_wins")
    private int nodebuffWins = 0;

    @Column(name = "nodebuff_losses")
    private int nodebuffLosses = 0;

    @Column(name = "nodebuff_elo")
    private int nodebuffElo = 0;

    @Column(name = "nodebuff_elo_party")
    private int nodebuffEloParty = 0;

    @Column(name = "debuff_wins")
    private int debuffWins = 0;

    @Column(name = "debuff_losses")
    private int debuffLosses = 0;

    @Column(name = "debuff_elo")
    private int debuffElo = 0;

    @Column(name = "debuff_elo_party")
    private int debuffEloParty = 0;

    @Column(name = "combo_wins")
    private int comboWins = 0;

    @Column(name = "combo_losses")
    private int comboLosses = 0;

    @Column(name = "combo_elo")
    private int comboElo = 0;

    @Column(name = "combo_elo_party")
    private int comboEloParty = 0;

    @Column(name = "gapple_wins")
    private int gappleWins = 0;

    @Column(name = "gapple_losses")
    private int gappleLosses = 0;

    @Column(name = "gapple_elo")
    private int gappleElo = 0;

    @Column(name = "gapple_elo_party")
    private int gappleEloParty = 0;

    @Column(name = "soup_wins")
    private int soupWins = 0;

    @Column(name = "soup_losses")
    private int soupLosses = 0;

    @Column(name = "soup_elo")
    private int soupElo = 0;

    @Column(name = "soup_elo_party")
    private int soupEloParty = 0;

    @Column(name = "archer_wins")
    private int archerWins = 0;

    @Column(name = "archer_losses")
    private int archerLosses = 0;

    @Column(name = "archer_elo")
    private int archerElo = 0;

    @Column(name = "archer_elo_party")
    private int archerEloParty = 0;

    @Column(name = "sumo_wins")
    private int sumoWins = 0;

    @Column(name = "sumo_losses")
    private int sumoLosses = 0;

    @Column(name = "sumo_elo")
    private int sumoElo = 0;

    @Column(name = "sumo_elo_party")
    private int sumoEloParty = 0;

    @Column(name = "builduhc_wins")
    private int builduhcWins = 0;

    @Column(name = "builduhc_losses")
    private int builduhcLosses = 0;

    @Column(name = "builduhc_elo")
    private int builduhcElo = 0;

    @Column(name = "builduhc_elo_party")
    private int builduhcEloParty = 0;

    @Column(name = "vanilla_wins")
    private int vanillaWins = 0;

    @Column(name = "vanilla_losses")
    private int vanillaLosses = 0;

    @Column(name = "vanilla_elo")
    private int vanillaElo = 0;

    @Column(name = "vanilla_elo_party")
    private int vanillaEloParty = 0;

    @Column(name = "spleef_wins")
    private int spleefWins = 0;

    @Column(name = "spleef_losses")
    private int spleefLosses = 0;

    @Column(name = "spleef_elo")
    private int spleefElo = 0;

    @Column(name = "spleef_elo_party")
    private int spleefEloParty = 0;

    @Column(name = "axe_wins")
    private int axeWins = 0;

    @Column(name = "axe_losses")
    private int axeLosses = 0;

    @Column(name = "axe_elo")
    private int axeElo = 0;

    @Column(name = "axe_elo_party")
    private int axeEloParty = 0;

    @Column(name = "classic_wins")
    private int classicWins = 0;

    @Column(name = "classic_losses")
    private int classicLosses = 0;

    @Column(name = "classic_elo")
    private int classicElo = 0;

    @Column(name = "classic_elo_party")
    private int classicEloParty = 0;

    @Column(name = "hcf_wins")
    private int hcfWins = 0;

    @Column(name = "hcf_losses")
    private int hcfLosses = 0;

    @Column(name = "hcf_elo")
    private int hcfElo = 0;

    @Column(name = "hcf_elo_party")
    private int hcfEloParty = 0;

    @Column(name = "skywars_wins")
    private int skywarsWins = 0;

    @Column(name = "skywars_losses")
    private int skywarsLosses = 0;

    @Column(name = "skywars_elo")
    private int skywarsElo = 0;

    @Column(name = "skywars_elo_party")
    private int skywarsEloParty = 0;


    public JsonObject toJson() {
        JsonObject object = new JsonObject();

        object.addProperty("uuid", this.uuid);
        object.addProperty("ping_range", this.pingRange);
        object.addProperty("elo_range", this.eloRange);

        object.addProperty("parkour_event_losses", this.parkourEventLosses);
        object.addProperty("parkour_event_wins", this.parkourEventWins);

        object.addProperty("redrover_event_losses", this.redroverEventLosses);
        object.addProperty("redrover_event_wins", this.redroverEventWins);

        object.addProperty("sumo_event_losses", this.sumoEventLosses);
        object.addProperty("sumo_event_wins", this.sumoEventWins);

        object.addProperty("oitc_event_losses", this.oitcEventLosses);
        object.addProperty("oitc_event_wins", this.oitcEventWins);
        object.addProperty("oitc_event_kills", this.oitcEventKills);
        object.addProperty("oitc_event_deaths", this.oitcEventDeaths);

        object.addProperty("nodebuff_elo_party", this.nodebuffEloParty);
        object.addProperty("nodebuff_elo", this.nodebuffElo);
        object.addProperty("nodebuff_wins", this.nodebuffWins);
        object.addProperty("nodebuff_losses", this.nodebuffLosses);

        object.addProperty("debuff_elo_party", this.debuffEloParty);
        object.addProperty("debuff_losses", this.debuffLosses);
        object.addProperty("debuff_wins", this.debuffWins);
        object.addProperty("debuff_elo", this.debuffElo);

        object.addProperty("gapple_elo_party", this.gappleEloParty);
        object.addProperty("gapple_losses", this.gappleLosses);
        object.addProperty("gapple_wins", this.gappleWins);
        object.addProperty("gapple_elo", this.gappleElo);

        object.addProperty("archer_elo_party", this.archerEloParty);
        object.addProperty("archer_losses", this.archerLosses);
        object.addProperty("archer_wins", this.archerWins);
        object.addProperty("archer_elo", this.archerElo);

        object.addProperty("classic_elo_party", this.classicEloParty);
        object.addProperty("classic_losses", this.classicLosses);
        object.addProperty("classic_wins", this.classicWins);
        object.addProperty("classic_elo", this.classicElo);

        object.addProperty("axe_elo_party", this.axeEloParty);
        object.addProperty("axe_losses", this.axeLosses);
        object.addProperty("axe_wins", this.axeWins);
        object.addProperty("axe_elo", this.axeElo);

        object.addProperty("vanilla_elo_party", this.vanillaEloParty);
        object.addProperty("vanilla_losses", this.vanillaLosses);
        object.addProperty("vanilla_wins", this.vanillaWins);
        object.addProperty("vanilla_elo", this.vanillaElo);
        
        object.addProperty("soup_elo_party", this.axeEloParty);
        object.addProperty("soup_losses", this.axeLosses);
        object.addProperty("soup_wins", this.axeWins);
        object.addProperty("soup_elo", this.axeElo);

        object.addProperty("sumo_elo_party", this.sumoEloParty);
        object.addProperty("sumo_losses", this.sumoLosses);
        object.addProperty("sumo_wins", this.sumoWins);
        object.addProperty("sumo_elo", this.sumoElo);

        object.addProperty("spleef_elo_party", this.spleefEloParty);
        object.addProperty("spleef_losses", this.spleefLosses);
        object.addProperty("spleef_wins", this.spleefWins);
        object.addProperty("spleef_elo", this.spleefElo);

        object.addProperty("combo_elo_party", this.comboEloParty);
        object.addProperty("combo_losses", this.comboLosses);
        object.addProperty("combo_wins", this.comboWins);
        object.addProperty("combo_elo", this.comboElo);
        
        object.addProperty("builduhc_elo_party", this.builduhcEloParty);
        object.addProperty("builduhc_losses", this.builduhcLosses);
        object.addProperty("builduhc_wins", this.builduhcWins);
        object.addProperty("builduhc_elo", this.builduhcElo);

        object.addProperty("hcf_elo_party", this.hcfEloParty);
        object.addProperty("hcf_losses", this.hcfLosses);
        object.addProperty("hcf_wins", this.hcfWins);
        object.addProperty("hcf_elo", this.hcfElo);

        object.addProperty("skywars_elo_party", this.skywarsEloParty);
        object.addProperty("skywars_losses", this.skywarsLosses);
        object.addProperty("skywars_wins", this.skywarsWins);
        object.addProperty("skywars_elo", this.skywarsElo);

        return object;
    }
    
}
