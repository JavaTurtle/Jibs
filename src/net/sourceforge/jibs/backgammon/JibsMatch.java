package net.sourceforge.jibs.backgammon;

public class JibsMatch {
    public static final int nPointMatch = 1;
    public static final int unlimitedMatch = 2;
    public static final JibsMatch JIBSMATCH = new JibsMatch(JibsMatch.nPointMatch);
    private int version;

    public JibsMatch(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }
}
