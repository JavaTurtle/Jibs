package net.sourceforge.jibs.backgammon;

import java.util.Collection;
import java.util.Iterator;

public class JibsCollection {
    public static boolean checkMove(Collection col, PossibleMove pMove,
                                    BackgammonBoard board) {
        boolean retCode = true;

        if (col.size() <= 0) {
            return true;
        }

        BackgammonBoard board1 = null;
        BackgammonBoard helpBoard1 = new BackgammonBoard(board);

        for (int j = 0; j <= pMove.getnrMoves(); j++) {
            Move mv = pMove.getMove(j);

            if (mv != null) {
                board1 = helpBoard1.placeMoveX(mv);
                helpBoard1 = board1;
            }
        }

        Iterator iter = col.iterator();
        BackgammonBoard board2 = null;
        BackgammonBoard helpBoard2 = new BackgammonBoard(board);

        while (iter.hasNext()) {
            PossibleMove posMove = (PossibleMove) iter.next();

            for (int j = 0; j <= posMove.getnrMoves(); j++) {
                Move mv = posMove.getMove(j);

                if (mv != null) {
                    board2 = helpBoard2.placeMoveX(mv);
                    helpBoard2 = board2;
                }
            }

            // check, if result boards are equal or different
            // return true --> a new valid move
            // return false --> move leads to same end position; don't add it
            for (int i = 0; i < 26; i++) {
                if (helpBoard1.getBoard()[i] != helpBoard2.getBoard()[i]) {
                    return true;
                }
            }

            retCode = false;
        }

        return retCode;
    }
}
