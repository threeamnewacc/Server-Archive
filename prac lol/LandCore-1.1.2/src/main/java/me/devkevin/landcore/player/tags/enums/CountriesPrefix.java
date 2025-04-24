package me.devkevin.landcore.player.tags.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.message.CC;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 06/02/2023 @ 4:06
 * CountriesPrefix / me.devkevin.landcore.player.tags / LandCore
 */
@Getter
@AllArgsConstructor
public enum CountriesPrefix {
    ESP(0, CC.RED + CC.B + "E" + CC.YELLOW + CC.B + "S" + CC.RED + CC.B + "P", Rank.BASIC),
    MEX(1, CC.DARK_GREEN + CC.B + "M" + CC.WHITE + CC.B + "E" + CC.D_RED + CC.B + "X", Rank.BASIC),
    ARG(2, CC.AQUA + CC.B + "A" + CC.WHITE + CC.B + "R" + CC.AQUA + CC.B + "G", Rank.BASIC),
    USA(3, CC.D_RED + CC.B + "U" + CC.WHITE + CC.B + "S" + CC.BLUE + CC.B + "A", Rank.BASIC),
    CRC(4, CC.D_BLUE + CC.B + "C" + CC.D_RED + CC.B + "R" + CC.D_BLUE + CC.B + "C", Rank.BASIC),
    BOL(5, CC.D_RED + CC.B + "B" + CC.YELLOW + CC.B + "O" + CC.DARK_GREEN + CC.B + "L", Rank.BASIC),
    BRZ(6, CC.DARK_GREEN + CC.B + "B" + CC.YELLOW + CC.B + "R" + CC.BLUE + CC.B + "Z", Rank.BASIC),
    CAN(7, CC.RED + CC.B + "C" + CC.WHITE + CC.B + "A" + CC.RED + CC.B + "N", Rank.BASIC),
    CHL(8, CC.BLUE + CC.B + "C" + CC.WHITE + CC.B + "H" + CC.D_RED + CC.B + "L", Rank.BASIC),
    COL(9, CC.YELLOW + CC.B + "C" + CC.BLUE + CC.B + "O" + CC.D_RED + CC.B + "L", Rank.BASIC),
    ENG(10, CC.WHITE + CC.B + "E" + CC.RED + CC.B + "N" + CC.WHITE + CC.B + "G", Rank.BASIC),
    ECU(11, CC.YELLOW + CC.B + "E" + CC.BLUE + CC.B + "C" + CC.D_RED + CC.B + "N", Rank.BASIC),
    EU(12, CC.BLUE + CC.B + "EU" + CC.YELLOW + CC.B + "★", Rank.BASIC),
    FRA(13, CC.BLUE + CC.B + "F" + CC.WHITE + CC.B + "R" + CC.D_RED + CC.B + "A", Rank.BASIC),
    GER(14, CC.D_GRAY + CC.B + "G" + CC.D_RED + CC.B + "E" + CC.YELLOW + CC.B + "R", Rank.BASIC),
    GTM(15, CC.D_AQUA + CC.B + "G" + CC.WHITE + CC.B + "T" + CC.D_AQUA + CC.B + "M", Rank.BASIC),
    HN(16, CC.BLUE + CC.B + "H" + CC.WHITE + CC.B + "N", Rank.BASIC),
    ITA(17, CC.DARK_GREEN + CC.B + "I" + CC.WHITE + CC.B + "T" + CC.D_RED + CC.B + "A", Rank.BASIC),
    PAR(18, CC.D_RED + CC.B + "P" + CC.WHITE + CC.B + "A" + CC.BLUE + CC.B + "R", Rank.BASIC),
    PER(19,CC.D_RED + CC.B + "P" + CC.WHITE + CC.B + "E" + CC.D_RED + CC.B + "R", Rank.BASIC),
    PR(20, CC.BLUE + CC.B + "P" + CC.D_RED + CC.B + "R", Rank.BASIC),
    RD(21, CC.BLUE + CC.B + "R" + CC.D_RED + CC.B + "D", Rank.BASIC),
    UY(22, CC.WHITE + CC.B + "U" + CC.AQUA + CC.B + "Y", Rank.BASIC),
    VNZ(23, CC.YELLOW + CC.B + "V" + CC.BLUE + CC.B + "N" + CC.D_RED + "Z", Rank.BASIC);

    private final int id;
    private final String prefix;
    private final Rank rank;

    public static CountriesPrefix getPrefixOrDefault(String name) {
        CountriesPrefix prefix;

        try {
            prefix = CountriesPrefix.valueOf(name.toUpperCase());
        } catch (Exception e) {
            prefix = CountriesPrefix.ESP;
        }

        return prefix;
    }
}
