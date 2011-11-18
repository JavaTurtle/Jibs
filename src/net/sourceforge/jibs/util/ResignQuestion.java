package net.sourceforge.jibs.util;

import net.sourceforge.jibs.server.JibsQuestion;

public class ResignQuestion implements JibsQuestion {
    private int mode;

    public ResignQuestion(int i) {
        this.mode = i;
    }

    public int getResignMode() {
        return mode;
    }
}
