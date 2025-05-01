package net.badlion.survivalgames.util;

import java.util.Collection;

public class RatingUtil {

    public static int getAverageRating(Collection<Integer> ratings) {
        int totalRating = 0;
        for (Integer rating : ratings) {
            totalRating += rating;
        }

        return totalRating / ratings.size();
    }

    public static int calculateRatingDiff(Collection<Integer> ratings, Integer oldRating, Integer position) {
        int middle = ratings.size() / 2;
        int average = RatingUtil.getAverageRating(ratings);
        int ratingChange;

        // This could be cleaned up, but fuck it
        if (ratings.size() % 2 == 0) {
            if (position < middle) {
                double E1 = 1 / (1 + Math.pow(10, (average - oldRating) / 400.0));
                int newFlatRating = (int) Math.round(oldRating + 0.0 + 32 * (1 - E1));
                ratingChange = newFlatRating - oldRating;
                ratingChange = (int) Math.round(ratingChange * (middle - position + 0.0) / middle);
            } else {
                double E1 = 1 / (1 + Math.pow(10, (average - oldRating) / 400.0));
                int newFlatRating = (int) Math.round(oldRating + 0.0 + 32 * (0 - E1));
                ratingChange = newFlatRating - oldRating;
                ratingChange = (int) Math.round(ratingChange * (position - middle + 1.0) / middle);
            }
        } else {
            if (position < middle) {
                double E1 = 1 / (1 + Math.pow(10, (average - oldRating) / 400.0));
                int newFlatRating = (int) Math.round(oldRating + 0.0 + 32 * (1 - E1));
                ratingChange = newFlatRating - oldRating;
                ratingChange = (int) Math.round(ratingChange * (middle - position + 0.0) / middle);
            } else if (position > middle) {
                double E1 = 1 / (1 + Math.pow(10, (average - oldRating) / 400.0));
                int newFlatRating = (int) Math.round(oldRating + 0.0 + 32 * (0 - E1));
                ratingChange = newFlatRating - oldRating;
                ratingChange = (int) Math.round(ratingChange * (position - middle + 1.0) / middle);
            } else {
                return 0;
            }
        }

        return ratingChange;
    }

}
