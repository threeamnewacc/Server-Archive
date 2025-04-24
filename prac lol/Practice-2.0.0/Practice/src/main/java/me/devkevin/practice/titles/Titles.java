package me.devkevin.practice.titles;

import club.inverted.chatcolor.CC;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 11/02/2023 @ 1:34
 * Tiles / me.devkevin.practice.titles / Practice
 */
@Getter
@RequiredArgsConstructor
public enum Titles {

    NONE(CC.YELLOW + "Not Placed", 0, 25),

    // 25 to 25
    BRONZE(CC.YELLOW + "Bronze", 25, 50),
    BRONZE_I(CC.YELLOW + "Bronze I", 50, 75),
    BRONZE_II(CC.YELLOW + "Bronze II", 75, 100),
    BRONZE_III(CC.YELLOW + "Bronze III", 100, 125),
    BRONZE_IV(CC.YELLOW + "Bronze IV", 125, 150),
    BRONZE_V(CC.YELLOW + "Bronze V", 150, 175),

    // 50 to 50
    IRON(CC.YELLOW + "Iron", 175, 225),
    IRON_I(CC.YELLOW + "Iron I", 225, 275),
    IRON_II(CC.YELLOW + "Iron II", 275, 325),
    IRON_III(CC.YELLOW + "Iron III", 325, 375),
    IRON_IV(CC.YELLOW + "Iron IV", 375, 425),
    IRON_V(CC.YELLOW + "Iron V", 425, 475),

    // 75 to 75
    GOLD(CC.YELLOW + "Gold", 475, 550),
    GOLD_I(CC.YELLOW + "Gold I", 550, 625),
    GOLD_II(CC.YELLOW + "Gold II", 625, 700),
    GOLD_III(CC.YELLOW + "Gold III", 700, 775),
    GOLD_IV(CC.YELLOW + "Gold IV", 775, 850),
    GOLD_V(CC.YELLOW + "Gold V", 850, 925),

    // 100 to 100
    EMERALD(CC.YELLOW + "Emerald", 925, 1025),
    EMERALD_I(CC.YELLOW + "Emerald I", 1025, 1125),
    EMERALD_II(CC.YELLOW + "Emerald II", 1125, 1225),
    EMERALD_III(CC.YELLOW + "Emerald III", 1225, 1325),
    EMERALD_IV(CC.YELLOW + "Emerald IV", 1325, 1425),
    EMERALD_V(CC.YELLOW + "Emerald V", 1425, 1525),

    // 125 to 125
    DIAMOND(CC.YELLOW + "Diamond", 1525, 1650),
    DIAMOND_I(CC.YELLOW + "Diamond I", 1650, 1775),
    DIAMOND_II(CC.YELLOW + "Diamond II", 1775, 1900),
    DIAMOND_III(CC.YELLOW + "Diamond III", 1900, 2025),
    DIAMOND_IV(CC.YELLOW + "Diamond IV", 2025, 2150),
    DIAMOND_V(CC.YELLOW + "Diamond V", 2150, 2275),

    // 150 to 150
    MASTER(CC.YELLOW + "Master", 2275, 2425),
    MASTER_I(CC.YELLOW + "Master I", 2425, 2575),
    MASTER_II(CC.YELLOW + "Master II", 2575, 2725),
    MASTER_III(CC.YELLOW + "Master III", 2725, 2875),
    MASTER_IV(CC.YELLOW + "Master IV", 2875, 3025),
    MASTER_V(CC.YELLOW + "Master V", 3025, 3175),

    GRAND_MASTER(CC.YELLOW + "Grand Master", 3175, 9999999)
    ;

    public static final List<Titles> titles = Arrays.asList(values());

    private final String name;
    private final int minimumWins;
    private final int maximumWins;
}
