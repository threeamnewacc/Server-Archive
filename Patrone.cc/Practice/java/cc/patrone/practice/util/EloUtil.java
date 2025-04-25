package cc.patrone.practice.util;

public class EloUtil {

    private static double[] getEstimations(double rankingA, double rankingB) {
        double[] ret = new double[2];
        double estA = 1.0 / (1.0 + Math.pow(10.0, (rankingB - rankingA) / 400.0));
        double estB = 1.0 / (1.0 + Math.pow(10.0, (rankingA - rankingB) / 400.0));
        ret[0] = estA;
        ret[1] = estB;
        return ret;
    }

    private static int getConstant(int ranking) {
        if (ranking < 1400) {
            return 32;
        }
        if (ranking >= 1400 && ranking < 1800) {
            return 24;
        }
        if (ranking >= 1800 && ranking < 2400) {
            return 16;
        }
        return 0;
    }

    public static int[] getNewRankings(int rankingA, int rankingB, boolean victoryA) {
        int[] elo = new int[2];
        double[] estimates = getEstimations(rankingA, rankingB);
        int newRankA = (int) (rankingA + getConstant(rankingA) * ((victoryA ? 1 : 0) - estimates[0]));
        elo[0] = Math.round((float) newRankA);
        elo[1] = Math.round((float) (rankingB - (newRankA - rankingA)));
        return elo;
    }
}
