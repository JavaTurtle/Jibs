package net.sourceforge.jibs.util;

public class RatingCalculator {
	public static void main(String[] args) {
		RatingChange ratingChange = RatingCalculator.ratingChange(true, 1, 247,
				1580.14, 42, 1918.64);
		System.out.println(ratingChange.getRatingA());
		System.out.println(ratingChange.getRatingB());
	}

	public static RatingChange ratingChange(boolean aWin, int length,
			double expA, double ratingA, double expB, double ratingB) {
		double d;
		double p;
		double p_upset;
		double k_a;
		double k_b;
		double variationA;
		double variationB;
		RatingChange change = new RatingChange();

		d = Math.abs(ratingA - ratingB);
		p_upset = 1 / (Math
				.pow(10.0, ((d * Math.sqrt((double) length)) / 2000)) + 1);
		k_a = Math.max(1, (-expA / 400) + 2);
		k_b = Math.max(1, -expB / 400 + 2);

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

		change.setRatingA(Math.floor(((variationA) * 100) + 0.5) / 100.0);
		change.setRatingB(Math.floor((( variationB) * 100) + 0.5) / 100.0);
		return change;

	}
}
