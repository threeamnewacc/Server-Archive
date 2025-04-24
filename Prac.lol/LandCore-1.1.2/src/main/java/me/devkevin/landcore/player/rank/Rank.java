package me.devkevin.landcore.player.rank;

import me.devkevin.landcore.utils.PlayerList;
import me.devkevin.landcore.utils.message.CC;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum Rank {

    MEMBER("Member", CC.GREEN, CC.GREEN, 1),

    //DONOR RANKS
    BASIC("Basic", CC.BLUE + "✦", CC.GREEN, 2),
    GOLD("Gold",CC.YELLOW + "✯", CC.GOLD, 3),
    EMERALD("Emerald", CC.GREEN + "✵", CC.DARK_GREEN, 4),
    DIAMOND("Diamond",CC.AQUA + "❇", CC.D_AQUA,5),
    LOL("LOL", CC.GOLD + "❊", CC.YELLOW, 6),


    // MEDIA RANKS
    MEDIA("Media", CC.D_GRAY + "[" + CC.PINK + "Media" + CC.D_GRAY  + "] ", CC.PINK, 7),
    YOUTUBER("Youtuber", CC.D_GRAY + "[" + CC.WHITE + "You" + CC.RED + "tuber" + CC.D_GRAY  + "] ", CC.RED, 8),
    TWITCH("Twitch", CC.D_GRAY + "[" + CC.PURPLE + "Twitch" + CC.D_GRAY  + "] ", CC.PURPLE, 9),
    FAMOUS("Famous", CC.PURPLE + "✳", CC.DARK_GREEN, 10),
    PARTNER("Partner", CC.D_GRAY + "[" + CC.BLUE + "Partner" + CC.D_GRAY  + "] ", CC.BLUE, 11),
    MEDIA_OWNER("Media-Owner",CC.D_GRAY + "[" + CC.D_RED + "Owner" + CC.D_GRAY  + "] ", CC.D_RED, 12),

    // STAFF RANK
    BUILDER("Builder", CC.D_GRAY + "[" + CC.DARK_GREEN + "Builder" + CC.D_GRAY  + "] ", CC.DARK_GREEN + CC.I, 13),
    HOST("Host",CC.D_GRAY + "[" + CC.PURPLE + CC.I + "Host" + CC.D_GRAY  + "] ", CC.PURPLE + CC.I, 14),
    TRIAL_MOD("Trainee",CC.D_GRAY + "[" + CC.YELLOW + CC.I + "Trainee" + CC.D_GRAY  + "] ", CC.YELLOW + CC.I, 15),
    MOD("Mod", CC.D_GRAY + "[" + CC.D_AQUA + CC.I + "Mod" + CC.D_GRAY  + "] ", CC.D_AQUA + CC.I , 16),
    SENIOR_HOST("Senior-Host", CC.D_GRAY + "[" + CC.PURPLE + CC.I + "Senior Host" + CC.D_GRAY  + "] ", CC.PURPLE + CC.I, 17),
    SENIOR_MOD("Senior-Mod", CC.D_GRAY + "[" + CC.D_AQUA + CC.I + "Senior Mod" + CC.D_GRAY  + "] ", CC.PURPLE + CC.I, 18),
    ADMIN("Admin", CC.D_GRAY + "[" + CC.RED + "Admin" + CC.D_GRAY  + "] ", CC.RED + CC.I, 19),
    SENIOR_ADMIN("Senior-Admin", CC.D_GRAY + "[" + CC.RED + CC.I + "Senior Admin" + CC.D_GRAY  + "] ", CC.RED + CC.I, 20),
    MANAGER("Manager", CC.D_GRAY + "[" + CC.D_RED + CC.I + "Manager" + CC.D_GRAY  + "] ", CC.D_RED + CC.I, 21),
    DEVELOPER("Developer", CC.D_GRAY + "[" + CC.AQUA + CC.I + "Developer" + CC.D_GRAY  + "] ", CC.AQUA + CC.I, 22),
    OWNER("Owner", CC.D_GRAY + "[" + CC.D_RED + CC.I + "Owner" + CC.D_GRAY  + "] ", CC.D_RED + CC.I, 23);

    private final String name;
    private final String rawFormat;
    private final String format;
    private final String color;
    private final List<String> permissions;
    private final int weight;

    Rank(String name, String rawFormat, String color, int weight) {
        this.name = name;
        this.rawFormat = rawFormat;
        this.format = String.format(rawFormat, color, color);
        this.color = color;
        this.permissions = new ArrayList<>();
        this.weight = weight;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public static Rank getByName(String name) {
        for (Rank rank : values()) {
            if (rank.getName().equalsIgnoreCase(name)) {
                return rank;
            }
        }

        return null;
    }

    public void apply(Player player) {
        String coloredName = color + player.getName();

        player.setPlayerListName(coloredName);
        player.setDisplayName(coloredName);
    }

    public static void importRanks() {
        CC.logConsole(CC.PRIMARY + "[LandCore] " + CC.SECONDARY + "Successfully imported " + CC.PRIMARY + values().length + CC.SECONDARY + " ranks.");
        CC.logConsole(CC.PRIMARY + "[LandCore] " + PlayerList.ORDERED_RANKS);
    }
}
