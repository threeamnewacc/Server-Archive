package us.zonix.core.symbols;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import us.zonix.core.rank.Rank;

@AllArgsConstructor
@Getter
public enum Symbol {

    SYMBOL_0(0, "", Rank.DEFAULT),
    SYMBOL_1(1, ChatColor.WHITE + "❖", Rank.SILVER),
    SYMBOL_2(2, ChatColor.WHITE + "✤", Rank.SILVER),
    SYMBOL_3(3, ChatColor.WHITE + "✦", Rank.SILVER),
    SYMBOL_4(4, ChatColor.YELLOW + "✯", Rank.GOLD),
    SYMBOL_5(5, ChatColor.YELLOW + "❆", Rank.GOLD),
    SYMBOL_6(6, ChatColor.YELLOW + "❂", Rank.GOLD),
    SYMBOL_7(7, ChatColor.AQUA + "❇", Rank.PLATINUM),
    SYMBOL_8(8, ChatColor.AQUA + "✸", Rank.PLATINUM),
    SYMBOL_9(9, ChatColor.AQUA + "❀", Rank.PLATINUM),
    SYMBOL_10(10, ChatColor.AQUA + "❁", Rank.PLATINUM),
    SYMBOL_11(11, ChatColor.GREEN + "✵", Rank.EMERALD),
    SYMBOL_12(12, ChatColor.GREEN + "✡", Rank.EMERALD),
    SYMBOL_13(13, ChatColor.GREEN + "✴", Rank.EMERALD),
    SYMBOL_14(14, ChatColor.GREEN + "❋", Rank.EMERALD),
    SYMBOL_15(15, ChatColor.GREEN + "✹", Rank.EMERALD),
    SYMBOL_16(16, ChatColor.GOLD + "❊", Rank.ZONIX),
    SYMBOL_17(17, ChatColor.GOLD + "❤", Rank.ZONIX),
    SYMBOL_18(18, ChatColor.GOLD + "☯", Rank.ZONIX),
    SYMBOL_19(19, ChatColor.GOLD + "☣", Rank.ZONIX),
    SYMBOL_20(20, ChatColor.GOLD + "✪", Rank.ZONIX),
    SYMBOL_21(21, ChatColor.GOLD + "☢", Rank.ZONIX);

    private final int id;
    private final String prefix;
    private final Rank rank;

    public static Symbol getSymbolOrDefault(String symbolName) {
        Symbol symbol;

        try {
            symbol = Symbol.valueOf(symbolName.toUpperCase());
        } catch (Exception e) {
            symbol = Symbol.SYMBOL_1;
        }

        return symbol;
    }

    public static Symbol getDefaultSymbolByRank(Rank rank) {
        if (rank == Rank.SILVER) {
            return SYMBOL_1;
        } else if (rank == Rank.GOLD) {
            return SYMBOL_4;
        } else if (rank == Rank.PLATINUM) {
            return SYMBOL_7;
        } else if (rank == Rank.EMERALD) {
            return SYMBOL_11;
        } else if (rank == Rank.ZONIX) {
            return SYMBOL_16;
        } else {
            return SYMBOL_0;
        }
    }

}
