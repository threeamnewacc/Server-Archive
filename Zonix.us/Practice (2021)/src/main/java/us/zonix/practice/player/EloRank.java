package us.zonix.practice.player;

public enum EloRank {
    BRONZE_IV(0, 649, 939),
    BRONZE_III(650, 724, 939),
    BRONZE_II(725, 799, 939),
    BRONZE_I(800, 874, 939),
    SILVER_IV(875, 939, 1179),
    SILVER_III(940, 999, 1179),
    SILVER_II(1000, 1059, 1179),
    SILVER_I(1060, 1119, 1179),
    GOLD_IV(1120, 1179, 1419),
    GOLD_III(1180, 1239, 1419),
    GOLD_II(1240, 1299, 1419),
    GOLD_I(1300, 1359, 1419),
    PLATINUM_IV(1360, 1419, 5000),
    PLATINUM_III(1420, 1489, 5000),
    PLATINUM_II(1490, 1539, 5000),
    PLATINUM_I(1540, 1599, 5000),
    DIAMOND(1600, 5000, 5000);

    private int min;
    private int max;
    private int eloRange;

    private EloRank(int min, int max, int eloRange) {
        this.min = min;
        this.max = max;
        this.eloRange = eloRange;
    }

    public static EloRank getRankByElo(int elo) {
        for (EloRank eloRank : values()) {
            if (elo >= eloRank.getMin() && elo <= eloRank.getMax()) {
                return eloRank;
            }
        }

        return BRONZE_IV;
    }

    public boolean isAboveOrEqual(EloRank rank) {
        return this.ordinal() >= rank.ordinal();
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    public int getEloRange() {
        return this.eloRange;
    }
}
