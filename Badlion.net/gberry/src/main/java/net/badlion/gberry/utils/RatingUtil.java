package net.badlion.gberry.utils;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;

public class RatingUtil {

	public static final int DEFAULT_RATING = 1500;

	public static final int SG_PLACEMENT_MATCHES = 10;

	public static final int ARENA_PLACEMENT_MATCHES = 10;

	public static final int ARENA_UNRANKED_WINS_NEEDED_FOR_RANKED = 5;

	public enum Rank {

		NONE("Not Placed", -1, ChatColor.WHITE, Material.BEDROCK, -2, 0, Color.BLACK),

		BRONZE_V("Bronze V", 0, ChatColor.WHITE, Material.BRICK, 900, 975, Color.MAROON),
		BRONZE_IV("Bronze IV", 1, ChatColor.WHITE, Material.BRICK, 975, 1050, Color.MAROON),
		BRONZE_III("Bronze III", 2, ChatColor.WHITE, Material.BRICK, 1050, 1125, Color.MAROON),
		BRONZE_II("Bronze II", 3, ChatColor.WHITE, Material.BRICK, 1125, 1200, Color.MAROON),
		BRONZE_I("Bronze I", 4, ChatColor.WHITE, Material.BRICK, 1200, 1275, Color.MAROON),

		SILVER_V("Silver V", 5, ChatColor.GRAY, Material.IRON_INGOT, 1275, 1350, Color.WHITE, Color.GRAY),
		SILVER_IV("Silver IV", 6, ChatColor.GRAY, Material.IRON_INGOT, 1350, 1425, Color.WHITE, Color.GRAY),
		SILVER_III("Silver III", 7, ChatColor.GRAY, Material.IRON_INGOT, 1425, 1500, Color.WHITE, Color.GRAY),
		SILVER_II("Silver II", 8, ChatColor.GRAY, Material.IRON_INGOT, 1500, 1575, Color.WHITE, Color.GRAY),
		SILVER_I("Silver I", 9, ChatColor.GRAY, Material.IRON_INGOT, 1575, 1650, Color.WHITE, Color.GRAY),

		GOLD_V("Gold V", 10, ChatColor.GOLD, Material.GOLD_INGOT, 1650, 1725, Color.YELLOW, Color.OLIVE),
		GOLD_IV("Gold IV", 11, ChatColor.GOLD, Material.GOLD_INGOT, 1725, 1800, Color.YELLOW, Color.OLIVE),
		GOLD_III("Gold III", 12, ChatColor.GOLD, Material.GOLD_INGOT, 1800, 1875, Color.YELLOW, Color.OLIVE),
		GOLD_II("Gold II", 13, ChatColor.GOLD, Material.GOLD_INGOT, 1875, 1950, Color.YELLOW, Color.OLIVE),
		GOLD_I("Gold I", 14, ChatColor.GOLD, Material.GOLD_INGOT, 1950, 2025, Color.YELLOW, Color.OLIVE),

		EMERALD_V("Emerald V", 20, ChatColor.GREEN, Material.EMERALD, 2025, 2100, Color.GREEN, Color.LIME),
		EMERALD_IV("Emerald IV", 21, ChatColor.GREEN, Material.EMERALD, 2100, 2175, Color.GREEN, Color.LIME),
		EMERALD_III("Emerald III", 22, ChatColor.GREEN, Material.EMERALD, 2175, 2250, Color.GREEN, Color.LIME),
		EMERALD_II("Emerald II", 23, ChatColor.GREEN, Material.EMERALD, 2250, 2325, Color.GREEN, Color.LIME),
		EMERALD_I("Emerald I", 24, ChatColor.GREEN, Material.EMERALD, 2325, 2400, Color.GREEN, Color.LIME),

		DIAMOND_V("Diamond V", 25, ChatColor.AQUA, Material.DIAMOND, 2400, 2475, Color.WHITE, Color.BLUE, Color.TEAL),
		DIAMOND_IV("Diamond IV", 26, ChatColor.AQUA, Material.DIAMOND, 2475, 2550, Color.WHITE, Color.BLUE, Color.TEAL),
		DIAMOND_III("Diamond III", 27, ChatColor.AQUA, Material.DIAMOND, 2550, 2625, Color.WHITE, Color.BLUE, Color.TEAL),
		DIAMOND_II("Diamond II", 28, ChatColor.AQUA, Material.DIAMOND, 2625, 2700, Color.WHITE, Color.BLUE, Color.TEAL),
		DIAMOND_I("Diamond I", 29, ChatColor.AQUA, Material.DIAMOND, 2700, 2775, Color.WHITE, Color.BLUE, Color.TEAL),

		MASTERS("Masters", 30, ChatColor.RED, Material.GOLDEN_APPLE, 2775, 20000, Color.WHITE, Color.SILVER, Color.OLIVE, Color.YELLOW);


		private final String name;
		private final int rank;
		private final ChatColor chatColor;
		private final Material type;
		private final Color[] colors;
		private final int minElo;
		private final int maxElo;

		Rank(String name, int rank, ChatColor chatColor, Material material, int minElo, int maxElo, Color... colors) {
			this.name = name;
			this.rank = rank;
			this.chatColor = chatColor;
			this.type = material;
			this.colors = colors;
			this.minElo = minElo;
			this.maxElo = maxElo;
		}

		public static Rank getByRankId(int id) {
			for (Rank rank : Rank.values()) {
				if (rank.getRank() == id) {
					return rank;
				}
			}
			return Rank.NONE;
		}

		public static Rank getRankByElo(double elo) {
			for (Rank rank : Rank.values()) {
				if (elo >= rank.minElo && elo < rank.maxElo) {
					return rank;
				}
			}
			return Rank.NONE;
		}

		public static int getPoints(double elo) {
			Rank rank = Rank.getRankByElo(elo);
			if (rank == Rank.NONE) {
				return 0;
			}
			if (rank == Rank.MASTERS) {
				// Hardcode for masters since their elo can be more than minElo + 75
				return (int) Math.round((Math.min(elo, 2850) - rank.minElo) / 0.075);
			}
			return (int) Math.round((elo - rank.minElo) / 0.075);
		}

		public static boolean isDemotionGame(double elo) {
			if (Rank.getRankByElo(elo).equals(Rank.BRONZE_V) || Rank.getRankByElo(elo).equals(Rank.NONE)) {
				return false;
			}
			return elo % 75 == 0;
		}

		public static boolean isPromotionGame(double elo) {
			if (Rank.getRankByElo(elo).equals(Rank.MASTERS) || Rank.getRankByElo(elo).equals(Rank.NONE)) {
				return false;
			}
			return (elo + 0.0001) % 75 == 0;
		}

		public static ChatColor getChatColorFromPoints(int points) {
			if (points <= 250) {
				return ChatColor.RED;
			}

			if (points <= 500) {
				return ChatColor.YELLOW;
			}

			return ChatColor.GREEN;
		}

		public String getName() {
			return name;
		}

		public int getRank() {
			return rank;
		}

		public Material getType() {
			return this.type;
		}

		public Color[] getColors() {
			return this.colors;
		}

		public ChatColor getChatColor() {
			return this.chatColor;
		}

	}

	public static String getDivisionFromRating(double rating) {
		Rank rank = Rank.getRankByElo(rating);
		if (rank != null) {
			return rank.getName();
		} else {
			throw new RuntimeException("Invalid rating processed: " + rating);
		}
	}

	public static String getDivisionColorFromRating(double rating) {
		Rank rank = Rank.getRankByElo(rating);
		if (rank != null) {
			return rank.getChatColor().toString();
		} else {
			throw new RuntimeException("Invalid rating processed: " + rating);
		}
	}

}
