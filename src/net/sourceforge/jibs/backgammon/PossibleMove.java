package net.sourceforge.jibs.backgammon;

public class PossibleMove {
    private Move[] move;
    private int nrmove;

    public PossibleMove() {
        nrmove = -1;
        move = new Move[4];
        move[0] = null;
        move[1] = null;
        move[2] = null;
        move[3] = null;
    }

    public PossibleMove(PossibleMove pmv) {
        this.nrmove = pmv.nrmove;
        this.move = new Move[4];

        for (int i = 0; i < 4; i++) {
            this.move[i] = pmv.move[i];
        }
    }

    public void add(Move mv) {
        nrmove++;
        move[nrmove] = mv;
    }

    public String conv2MoveString() {
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i <= nrmove; i++) {
            if (move[i] != null) {
                if ((move[i].source <= 0) || (move[i].source >= 25)) {
                    buffer.append("bar");
                } else {
                    buffer.append(move[i].source);
                }

                buffer.append("-");

                if ((move[i].destination <= 0) || (move[i].destination >= 25)) {
                    buffer.append("off");
                } else {
                    buffer.append(move[i].destination);
                }

                buffer.append(" ");
            }
        }

        return buffer.toString();
    }

    public Move getMove(int index) {
        return move[index];
    }

    public int getnrMoves() {
        return nrmove;
    }

    public String toString() {
        return conv2MoveString();
    }
}
