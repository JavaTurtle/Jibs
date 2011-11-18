package net.sourceforge.jibs.util;

import net.sourceforge.jibs.server.JibsQuestion;

public class DoubleQuestion implements JibsQuestion {
    private int cubeNew;
    private int cubeOld;

    public DoubleQuestion(int cubeNew, int cubeOld) {
        this.cubeNew = cubeNew;
        this.cubeOld = cubeOld;
    }

    public int getCubeNew() {
        return cubeNew;
    }

    public void setCube(int cube) {
        this.cubeNew = cube;
    }

    public int getCubeOld() {
        return cubeOld;
    }
}
