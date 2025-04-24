package me.devkevin.landcore.player.tags.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.message.CC;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 06/02/2023 @ 3:45
 * TextSymbolPrefix / me.devkevin.landcore.player.tags / LandCore
 */
@Getter
@AllArgsConstructor
public enum TextSymbolPrefix {
    LOLGANG(0, CC.GOLD + " #LOLGang", Rank.BASIC),
    L(1, CC.RED + CC.B + " L", Rank.BASIC),
    GG(2, CC.YELLOW + " GG", Rank.BASIC),
    EZ(3, CC.AQUA + " EZ", Rank.BASIC),
    DAB(4, CC.RED + " <o", Rank.BASIC),
    OFF(5, CC.PINK + " OFF", Rank.BASIC),
    BESTEU(6, CC.GOLD + CC.B + " BestEU", Rank.BASIC),
    BESTNA(7, CC.GOLD + CC.B + " BestNA", Rank.BASIC),
    UWU(8, CC.RED + " uwu", Rank.BASIC),
    NWN(9, CC.RED + " nwn", Rank.BASIC),
    WEED(10, CC.DARK_GREEN + " #WEED", Rank.BASIC),
    REACH(11, CC.PINK + " #REACH", Rank.BASIC),
    CHEATER(12, CC.D_RED + " #CHEATER", Rank.BASIC);

    private final int id;
    private final String prefix;
    private final Rank rank;
}
