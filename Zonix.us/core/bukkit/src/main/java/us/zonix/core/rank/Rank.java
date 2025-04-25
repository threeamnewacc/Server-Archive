package us.zonix.core.rank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import us.zonix.core.util.CC;

@AllArgsConstructor
@Getter
public enum Rank {

	DEFAULT("", CC.GREEN, "Default", ""),
	E_GIRL(CC.RED + "❤", CC.GREEN, "E-Girl", ""),
	SILVER(CC.WHITE + "❖", CC.GRAY, "Silver", ""),
	GOLD(CC.YELLOW + "✯", CC.GOLD, "Gold", ""),
	PLATINUM(CC.AQUA + "❇", CC.DARK_AQUA, "Platinum", ""),
	EMERALD(CC.GREEN + "✵", CC.DARK_GREEN, "Emerald", ""),
	ZONIX(CC.GOLD + "❊", CC.DARK_RED + CC.BOLD, "Zonix", ""),
	BUILDER(CC.GRAY + "[", CC.BLUE, "Builder", CC.GRAY + "] "),
	MEDIA(CC.GRAY + "[", CC.LIGHT_PURPLE, "YouTuber", CC.GRAY + "] "),
	FAMOUS(CC.GRAY + "[", CC.LIGHT_PURPLE + CC.ITALIC, "Famous", CC.GRAY + "] "),
	PARTNER(CC.GRAY + "[", CC.LIGHT_PURPLE + CC.ITALIC, "Partner", CC.GRAY + "] "),
	MEDIA_OWNER(CC.GRAY + "[", CC.DARK_RED, "Owner", CC.GRAY + "] "),
	TRIAL_MOD(CC.GRAY + "[", CC.YELLOW, "Trial-Mod", CC.GRAY + "] "),
	MODERATOR(CC.GRAY + "[", CC.DARK_AQUA, "Mod", CC.GRAY + "] "),
	SENIOR_MODERATOR(CC.GRAY + "[", CC.DARK_PURPLE, "Sr. Mod", CC.GRAY + "] "),
	ADMINISTRATOR(CC.GRAY + "[", CC.RED, "Admin", CC.GRAY + "] "),
	PLATFORM_ADMINISTRATOR(CC.GRAY + "[", CC.RED, "Platform-Admin", CC.GRAY + "] "),
	MANAGER(CC.GRAY + "[", CC.RED, "Manager", CC.GRAY + "] "),
	DEVELOPER(CC.GRAY + "[", CC.AQUA, "Developer", CC.GRAY + "] "),
	COOWNER(CC.GRAY + "[", CC.DARK_RED, "Co-Owner", CC.GRAY + "] "),
	OWNER(CC.GRAY + "[", CC.DARK_RED, "Owner", CC.GRAY + "] ");

	private final String prefix;
	private final String color;
	private final String name;
	private final String suffix;

	public boolean isAboveOrEqual(Rank rank) {
		return this.ordinal() >= rank.ordinal();
	}

	public static Rank getRankOrDefault(String rankName) {
		Rank rank;

		try {
			rank = Rank.valueOf(rankName.toUpperCase());
		}
		catch (Exception e) {
			rank = Rank.DEFAULT;
		}

		return rank;
	}

}
