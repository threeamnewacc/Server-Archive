package me.devkevin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

import static me.devkevin.util.Constants.*;

/**
 * Created by DevKevin
 * Project: WebAPI
 * Date: 22/02/2022 @ 20:57
 */
@Data @NoArgsConstructor @AllArgsConstructor @Entity(name = "practice_season_" + PRACTICE_SEASON + "_inventories")
public class Inventory implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    private String winnerInventory;
    private String loserInventory;
    private String matchId;

}
