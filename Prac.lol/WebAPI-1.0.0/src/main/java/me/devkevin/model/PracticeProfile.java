package me.devkevin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import java.io.Serializable;

import static me.devkevin.util.Constants.*;

/**
 * Created by DevKevin
 * Project: WebAPI
 * Date: 22/02/2022 @ 21:03
 */
@Data @NoArgsConstructor @AllArgsConstructor @Entity(name = "practice_season_" + PRACTICE_SEASON + "_profile")
public class PracticeProfile implements Serializable {
    @Id
    @Column(unique = true)
    private int playerId;

    private int unrankedWins;
    private int rankedWins;

    private int globalElo;

    // implement Kits elo & elo party, losses, wins
    private int nodebuffElo;
    private int nodebuffEloParty;
    private int nodebuffWins;
    private int nodebuffLosses;

    private int boxingElo;
    private int boxingEloParty;
    private int boxingWins;
    private int boxingLosses;

    private int bedFightElo;
    private int bedFightEloParty;
    private int bedFightWins;
    private int bedFightLosses;

    private int gappleElo;
    private int gappleEloParty;
    private int gappleWins;
    private int gappleLosses;

    private int buildUHCELo;
    private int buildUHCELoParty;
    private int buildUHCWins;
    private int buildUHCLosses;

    private int sumoElo;
    private int sumoEloParty;
    private int sumoWins;
    private int sumoLosses;

    private int finalUHCElo;
    private int finalUHCEloParty;
    private int finalUHCWins;
    private int finalUHCLosses;

    private int axeElo;
    private int axeEloParty;
    private int axeWins;
    private int axeLosses;

    private int soupElo;
    private int soupEloParty;
    private int soupWins;
    private int soupLosses;

    private int fullpvpElo;
    private int fullpvpEloParty;
    private int fullpvpWins;
    private int fullpvpLosses;
}
