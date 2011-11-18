package net.sourceforge.jibs.backgammon;


public class Die {

    public Die() {
    }

    public static int roll(JibsRandom random) {
        return random.nextInt(6) + 1;
    }

}
