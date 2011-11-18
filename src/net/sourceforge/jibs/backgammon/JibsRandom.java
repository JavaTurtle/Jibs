package net.sourceforge.jibs.backgammon;

import java.util.Random;

public class JibsRandom extends Random {
    private static final long serialVersionUID = 3144806616075414762L;
    private int count;
    private int[] history;

    public JibsRandom() {
        super();
        count = 0;
        history = new int[6];
        history[0] = history[1] = history[2] = history[3] = history[4] = history[5] = 0;
    }

    public int nextInt(int n) {
        int value = super.nextInt(n);

        if ((value >= 0) && (value < 6)) {
            count++;
            history[value]++;
        }

        return value;
    }
}
