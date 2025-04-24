package me.devkevin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

import java.io.Serializable;

import static me.devkevin.util.Constants.PRACTICE_SEASON;

/**
 * Created by DevKevin
 * Project: WebAPI
 * Date: 22/02/2022 @ 21:01
 */
@Data @NoArgsConstructor @AllArgsConstructor @Entity(name = "practice_season" + PRACTICE_SEASON + "_matches")
public class Match implements Serializable {
    @Id
    private String id;

    private int inventory;

    private int winner;
    private int loser;

    private int winnerEloBefore;
    private int loserEloBefore;

    private int winnerEloAfter;
    private int loserEloAfter;
}
