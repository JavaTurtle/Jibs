package net.sourceforge.jibs.util;

public class RatingCalculator {
	public static void main(String[] args) {
		double ratingChange = RatingCalculator.ratingChange(true, 1, 0, 1500.0, 0, 1500.0);
		System.out.println(ratingChange);
	}
    public static double ratingChange(boolean aWin, int length, double expA,
                                      double ratingA, double expB,
                                      double ratingB) {
        double d;
        double p;
        double p_upset;
        double k_a;
        double k_b;
        double variationA;
        double variationB;

        // experience_A += length ;
        // experience_B += length ;
        d = Math.abs(ratingA - ratingB);
        p_upset = 1 / (Math.pow(10.0, ((d * Math.sqrt((double)length)) / 2000)) + 1);
        k_a = Math.max(1, (-expA / 100) + 5);
        k_b = Math.max(1, -expB / 100 + 5);

        if (ratingA <= ratingB) {
            if (aWin) {
                p = 1 - p_upset;
                variationA = 4 * k_a * Math.sqrt(length) * p;
                variationB = -4 * k_b * Math.sqrt(length) * p;
            } else {
                p = p_upset;
                variationA = -4 * k_a * Math.sqrt(length) * p;
                variationB = 4 * k_b * Math.sqrt(length) * p;
            }
        } else {
            if (aWin) {
                p = p_upset;
                variationA = 4 * k_a * Math.sqrt(length) * p;
                variationB = -4 * k_b * Math.sqrt(length) * p;
            } else {
                p = 1 - p_upset;
                variationA = -4 * k_a * Math.sqrt(length) * p;
                variationB = 4 * k_b * Math.sqrt(length) * p;
            }
        }

        variationA = Math.floor((variationA * 100) + 0.5) / 100.0;
        variationB = Math.floor((variationB * 100) + 0.5) / 100.0;

        if (aWin) {
            return (variationA);
        } else {
            return (variationB);
        }
    }
}
