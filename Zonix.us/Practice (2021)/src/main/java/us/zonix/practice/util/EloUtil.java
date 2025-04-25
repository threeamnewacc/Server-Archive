package us.zonix.practice.util;

public class EloUtil {
    private static final KFactor[] K_FACTORS = new KFactor[]{
        new KFactor(0, 1000, 25.0), new KFactor(1001, 1400, 20.0), new KFactor(1401, 1800, 15.0), new KFactor(1801, 2200, 10.0)
    };
    private static final int DEFAULT_K_FACTOR = 25;
    private static final int WIN = 1;
    private static final int LOSS = 0;

    public static int getNewRating(int rating, int opponentRating, boolean won) {
        return won ? getNewRating(rating, opponentRating, 1) : getNewRating(rating, opponentRating, 0);
    }

    public static int getNewRating(int rating, int opponentRating, int score) {
        double kFactor = getKFactor(rating);
        double expectedScore = getExpectedScore(rating, opponentRating);
        int newRating = calculateNewRating(rating, score, expectedScore, kFactor);
        if (score == 1 && newRating == rating) {
            newRating++;
        }

        return newRating;
    }

    private static int calculateNewRating(int oldRating, int score, double expectedScore, double kFactor) {
        return oldRating + (int)(kFactor * ((double)score - expectedScore));
    }

    private static double getKFactor(int rating) {
        for (int i = 0; i < K_FACTORS.length; i++) {
            if (rating >= K_FACTORS[i].getStartIndex() && rating <= K_FACTORS[i].getEndIndex()) {
                return K_FACTORS[i].getValue();
            }
        }

        return 25.0;
    }

    private static double getExpectedScore(int rating, int opponentRating) {
        return 1.0 / (1.0 + Math.pow(10.0, (double)(opponentRating - rating) / 400.0));
    }
}
