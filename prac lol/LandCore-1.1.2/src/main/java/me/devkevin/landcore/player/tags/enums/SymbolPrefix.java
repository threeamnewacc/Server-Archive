package me.devkevin.landcore.player.tags.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.message.CC;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 06/02/2023 @ 3:50
 * SymbolPrefix / me.devkevin.landcore.player.tags / LandCore
 */
@Getter
@AllArgsConstructor
public enum SymbolPrefix {
    LOVE(0, CC.RED + " ❤", Rank.BASIC),
    START(1, CC.GOLD + CC.B + " ★", Rank.BASIC),
    DOLLAR(2, CC.DARK_GREEN + CC.B + " $", Rank.BASIC),
    XXX(3, CC.BLACK + CC.B  + " ✖✖✖", Rank.BASIC),
    OTAKU(4, CC.PINK + CC.B + " ｡◕‿ ◕｡", Rank.BASIC),
    TREBOL(5, CC.DARK_GREEN + CC.B + " ♣", Rank.BASIC),
    SYMBOL_1(6, CC.B + CC.B + " ^◕‿◕^", Rank.BASIC),
    SYMBOL_2(7, CC.B + CC.B + " (◕^^◕)", Rank.BASIC),
    KING(8, CC.GOLD + CC.B + " ♛", Rank.BASIC),
    PEACE(9, CC.YELLOW + CC.B + " ✌", Rank.BASIC),
    TOXIC(10, CC.YELLOW + CC.B + " ☢", Rank.BASIC),
    PLUS(11, CC.GREEN + CC.B + " ✚", Rank.BASIC);

    private final int id;
    private final String prefix;
    private final Rank rank;

    public static SymbolPrefix getPrefixOrDefault(String name) {
        SymbolPrefix prefix;

        try {
            prefix = SymbolPrefix.valueOf(name.toUpperCase());
        } catch (Exception e) {
            prefix = SymbolPrefix.LOVE;
        }

        return prefix;
    }
}
