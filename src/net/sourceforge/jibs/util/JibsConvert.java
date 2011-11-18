package net.sourceforge.jibs.util;

import java.math.BigDecimal;

public class JibsConvert {
    public static String convBoolean(boolean b) {
        return (b ? "1" : "0");
    }

    public static double convdouble(double value, int scale) {
        double result = value;

        result = new BigDecimal("" + result).setScale(scale,
                                                      BigDecimal.ROUND_HALF_UP)
                                            .doubleValue();

        return result;
    }
}
