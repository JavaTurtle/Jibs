package net.sourceforge.jibs.backgammon;

import java.util.ArrayList;
import java.util.Collection;

import net.sourceforge.jibs.server.JibsServer;

public class MoveChances {
    private BackgammonBoard board;
    private JibsGame game;

    public MoveChances(JibsServer jibsServer, JibsGame game,
                       BackgammonBoard board) {
        this.game = game;
        this.board = new BackgammonBoard(board);
    }

    public int calcPossibleMovesO(int turn, int dice1, int dice2) {
        // Player is playing 'X' with direction +1 (home=25, bar=0)
        // Opponent is playing 'O' with direction -1 (home=0, bar=25)
        int d1 = Math.max(dice1, dice2);
        int d2 = Math.min(dice1, dice2);

        game.setCollection(new ArrayList<PossibleMove>());

        if (d1 == d2) {
            // try to set all 4 dices
            try4TimesO(d1, d2, d1, d2, game.getCollection());

            if (game.getCollection().size() > 0) {
                return 4; // opponent can move 4 dice
            }

            // try to set 3 dices
            try3TimesO(d1, d2, d1, game.getCollection());

            if (game.getCollection().size() > 0) {
                return 3; // opponent can move 3 dice
            }

            // try to set 2 dices
            try2TimesO(d1, d2, game.getCollection());

            if (game.getCollection().size() > 0) {
                return 2; // opponent can move 2 dice
            }

            // try to set 3 dices
            try2TimesO(d2, d1, game.getCollection());

            if (game.getCollection().size() > 0) {
                return 2; // opponent can move 2 dice
            }

            // try to set 1 die (the higher one)
            try1TimesO(d1, game.getCollection());

            if (game.getCollection().size() > 0) {
                return 1; // opponent can move 1 die
            }

            // try to set 1 die (the lower one)
            try1TimesO(d2, game.getCollection());

            if (game.getCollection().size() > 0) {
                return 1; // opponent can move one die
            }
        } else {
            // try to set 2 dices
            try2TimesO(d1, d2, game.getCollection());

            if (game.getCollection().size() > 0) {
                return 2; // opponentcan move 2 dice
            }

            // try to set 2 dices
            try2TimesO(d2, d1, game.getCollection());

            if (game.getCollection().size() > 0) {
                return 2; // opponent can move 2 dice
            }

            // try to set 1 die (the higher one)
            try1TimesO(d1, game.getCollection());

            if (game.getCollection().size() > 0) {
                return 1; // opponent can move 1 die
            }

            // try to set 1 die (the lower one)
            try1TimesO(d2, game.getCollection());

            if (game.getCollection().size() > 0) {
                return 1; // opponent can move 1 die
            }
        }

        return 0; // opponent can't move
    }

    public int calcPossibleMovesX(int turn, int dice1, int dice2) {
        int d1 = Math.max(dice1, dice2);
        int d2 = Math.min(dice1, dice2);

        game.setCollection(new ArrayList<PossibleMove>());

        // Player is playing 'X' with direction 1 (home=25, bar=0)
        // Opponent is playing 'O' with direction -1 (home=0, bar=25)
        // try to set both moves
        if (d1 == d2) {
            // try to set all 4 dices
            try4TimesX(d1, d2, d1, d2, game.getCollection());

            if (game.getCollection().size() > 0) {
                return 4; // player can move 4 dice
            }

            // try to set 3 dices
            try3TimesX(d1, d2, d1, game.getCollection());

            if (game.getCollection().size() > 0) {
                return 3; // player can move 3 dice
            }

            // try to set 2 dices (higher one first)
            try2TimesX(d1, d2, game.getCollection());

            if (game.getCollection().size() > 0) {
                return 2; // player can move 2 dice
            }

            // try to set 2 dices (lower one first)
            try2TimesX(d2, d1, game.getCollection());

            if (game.getCollection().size() > 0) {
                return 2; // player can move 2 dice
            }

            // try to set 1 die (the higher one)
            try1TimesX(d1, game.getCollection());

            if (game.getCollection().size() > 0) {
                return 1; // player can move 1 die
            }

            // try to set 1 die (the lower one)
            try1TimesX(d2, game.getCollection());

            if (game.getCollection().size() > 0) {
                return 1; // player can move one die
            }
        } else {
            // try to set 2 dices (higher one first)
            try2TimesX(d1, d2, game.getCollection());

            if (game.getCollection().size() > 0) {
                return 2; // player can move 2 dice
            }

            // try to set 2 dices (lower one first)
            try2TimesX(d2, d1, game.getCollection());

            if (game.getCollection().size() > 0) {
                return 2; // player can move 2 dice
            }

            // try to set 1 die (the higher one)
            try1TimesX(d1, game.getCollection());

            if (game.getCollection().size() > 0) {
                return 1; // player can move 1 die
            }

            // try to set 1 die (the lower one)
            try1TimesX(d2, game.getCollection());

            if (game.getCollection().size() > 0) {
                return 1; // player can move 1 die
            }
        }

        return 0; // player can't move
    }

    private boolean canMoveO(BackgammonBoard board2, int slot, int die) {
        int destination = slot - die;

        if (board2.getOnBar2() > 0) {
            if ((slot == 25) && (board2.getBoard()[25 - die] >= -1)) {
                return true; // man on bar , can move in
            }

            return false; // man on bar
        }

        if (board2.getBoard()[slot] <= 0) {
            return false; // no own piece
        }

        if ((destination) <= 0) {
            if (canMoveOffO(board2)) {
                if ((destination) == 0) {
                    return true; // can bear off
                }

                if ((destination) < 0) {
                    // can bear off, only when no more men behind this piece
                    boolean allowed = true;

                    for (int j = slot + 1; j <= 6; j++) {
                        if (board2.getBoard()[j] > 0) {
                            allowed = false;
                        }
                    }

                    if (allowed) {
                        return true;
                    }

                    return false;
                }
            } else {
                return false; // can't bear off
            }
        } else { // normal move

            if (board2.getBoard()[destination] < -1) {
                return false; // destination occupied
            }
        }

        return true;
    }

    private boolean canMoveOffO(BackgammonBoard board) {
        boolean retCode = true;

        for (int i = 7; i < 25; i++) {
            if (board.getBoard()[i] > 0) {
                retCode = false;
            }
        }

        return retCode;
    }

    private boolean canMoveOffX(BackgammonBoard board) {
        boolean retCode = true;

        for (int i = 1; i < 19; i++) {
            if (board.getBoard()[i] < 0) {
                retCode = false;
            }
        }

        return retCode;
    }

    private boolean canMoveX(BackgammonBoard board2, int slot, int die) {
        int destination = slot + die;

        if (board2.getOnBar1() > 0) {
            if ((slot == 0) && (board2.getBoard()[die] <= 1)) {
                return true; // man on bar , can move in
            }

            return false; // man on bar
        }

        if (board2.getBoard()[slot] >= 0) {
            return false; // no own piece
        }

        if ((destination) >= 25) {
            if (canMoveOffX(board2)) {
                if ((destination) == 25) {
                    return true; // can bear off
                }

                if ((destination) > 25) {
                    // can bear off, only when no more men behind this piece
                    boolean allowed = true;

                    for (int j = slot - 1; j >= 19; j--) {
                        if (board2.getBoard()[j] < 0) {
                            allowed = false;
                        }
                    }

                    if (allowed) {
                        return true;
                    }

                    return false;
                }
            } else {
                return false; // can't bear off
            }
        } else { // normal move

            if (board2.getBoard()[destination] > 1) {
                return false; // destination occupied
            }
        }

        return true;
    }

    private void try1TimesO(int d1, Collection<PossibleMove> col) {
        col.clear();

        BackgammonBoard board_backup = new BackgammonBoard(board);

        for (int i = 0; i < 26; i++) {
            board = new BackgammonBoard(board_backup);

            if (canMoveO(board, i, d1)) {
                PossibleMove pmove = new PossibleMove();

                pmove.add(new Move(i, i - d1));
                col.add(pmove);
            }
        }
    }

    private void try1TimesX(int d1, Collection<PossibleMove> col) {
        col.clear();

        BackgammonBoard board_backup = new BackgammonBoard(board);

        for (int i = 0; i < 26; i++) {
            board = new BackgammonBoard(board_backup);

            if (canMoveX(board, i, d1)) {
                PossibleMove pmove = new PossibleMove();

                pmove.add(new Move(i, i + d1));
                col.add(pmove);
            }
        }
    }

    private void try2TimesO(int d1, int d2, Collection<PossibleMove> col) {
        col.clear();

        BackgammonBoard board_backup = new BackgammonBoard(board);

        for (int i = 0; i < 26; i++) {
            board = new BackgammonBoard(board_backup);

            if (canMoveO(board, i, d1)) {
                BackgammonBoard board2 = new BackgammonBoard(board.placeMoveO(new Move(i,
                                                                                       i -
                                                                                       d1)));

                for (int j = 0; j < 26; j++) {
                    if (canMoveO(board2, j, d2)) {
                        PossibleMove pmove = new PossibleMove();

                        pmove.add(new Move(i, i - d1));
                        pmove.add(new Move(j, j - d2));
                        col.add(pmove);
                    } // if
                } // for
            } // if
        } // for
    }

    private void try2TimesX(int d1, int d2, Collection<PossibleMove> col) {
        col.clear();

        BackgammonBoard board_backup = new BackgammonBoard(board);

        for (int i = 0; i < 26; i++) {
            board = new BackgammonBoard(board_backup);

            if (canMoveX(board, i, d1)) {
                BackgammonBoard board2 = new BackgammonBoard(board.placeMoveX(new Move(i,
                                                                                       i +
                                                                                       d1)));

                for (int j = 0; j < 26; j++) {
                    if (canMoveX(board2, j, d2)) {
                        PossibleMove pmove = new PossibleMove();

                        pmove.add(new Move(i, i + d1));
                        pmove.add(new Move(j, j + d2));
                        col.add(pmove);
                    } // if
                } // for
            } // if
        } // for
    }

    private void try3TimesO(int d1, int d2, int d12,
                            Collection<PossibleMove> col) {
        col.clear();

        BackgammonBoard board_backup = new BackgammonBoard(board);

        for (int a = 0; a < 26; a++) {
            board = new BackgammonBoard(board_backup);

            if (canMoveO(board, a, d1)) {
                BackgammonBoard board2 = new BackgammonBoard(board.placeMoveO(new Move(a,
                                                                                       a -
                                                                                       d1)));

                for (int b = 0; b < 26; b++) {
                    if (canMoveO(board2, b, d2)) {
                        BackgammonBoard board3 = new BackgammonBoard(board2.placeMoveO(new Move(b,
                                                                                                b -
                                                                                                d2)));

                        for (int c = 0; c < 26; c++) {
                            if (canMoveO(board3, c, d2)) {
                                PossibleMove pmove = new PossibleMove();

                                pmove.add(new Move(a, a - d1));
                                pmove.add(new Move(b, b - d2));
                                pmove.add(new Move(c, c - d2));

                                if (JibsCollection.checkMove(col, pmove,
                                                                 board_backup)) {
                                    col.add(pmove);
                                }
                            } // if
                        } // if
                    } // for
                }
            }
        }
    }

    private void try3TimesX(int d1, int d2, int d11,
                            Collection<PossibleMove> col) {
        col.clear();

        BackgammonBoard board_backup = new BackgammonBoard(board);

        for (int a = 0; a < 26; a++) {
            board = new BackgammonBoard(board_backup);

            if (canMoveX(board, a, d1)) {
                BackgammonBoard board2 = new BackgammonBoard(board.placeMoveX(new Move(a,
                                                                                       a +
                                                                                       d1)));

                for (int b = 0; b < 26; b++) {
                    if (canMoveX(board2, b, d2)) {
                        BackgammonBoard board3 = new BackgammonBoard(board2.placeMoveX(new Move(b,
                                                                                                b +
                                                                                                d2)));

                        for (int c = 0; c < 26; c++) {
                            if (canMoveX(board3, c, d2)) {
                                PossibleMove pmove = new PossibleMove();

                                pmove.add(new Move(a, a + d1));
                                pmove.add(new Move(b, b + d2));
                                pmove.add(new Move(c, c + d2));

                                if (JibsCollection.checkMove(col, pmove,
                                                                 board_backup)) {
                                    col.add(pmove);
                                }
                            } // if
                        } // if
                    } // for
                }
            }
        }
    }

    private void try4TimesO(int d1, int d2, int d12, int d22,
                            Collection<PossibleMove> col) {
        col.clear();

        BackgammonBoard board_backup = new BackgammonBoard(board);

        for (int a = 0; a < 26; a++) {
            board = new BackgammonBoard(board_backup);

            if (canMoveO(board, a, d1)) {
                BackgammonBoard board2 = new BackgammonBoard(board.placeMoveO(new Move(a,
                                                                                       a -
                                                                                       d1)));

                for (int b = 0; b < 26; b++) {
                    if (canMoveO(board2, b, d2)) {
                        BackgammonBoard board3 = new BackgammonBoard(board2.placeMoveO(new Move(b,
                                                                                                b -
                                                                                                d2)));

                        for (int c = 0; c < 26; c++) {
                            if (canMoveO(board3, c, d2)) {
                                BackgammonBoard board4 = new BackgammonBoard(board3.placeMoveO(new Move(c,
                                                                                                        c -
                                                                                                        d2)));

                                for (int d = 0; d < 26; d++) {
                                    if (canMoveO(board4, d, d2)) {
                                        PossibleMove pmove = new PossibleMove();

                                        pmove.add(new Move(a, a - d1));
                                        pmove.add(new Move(b, b - d2));
                                        pmove.add(new Move(c, c - d2));
                                        pmove.add(new Move(d, d - d2));

                                        if (JibsCollection.checkMove(col,
                                                                         pmove,
                                                                         board_backup)) {
                                            col.add(pmove);
                                        }
                                    } // if
                                } // for
                            } // if
                        } // for
                    }
                }
            }
        }
    }

    private void try4TimesX(int d1, int d2, int d11, int d22,
                            Collection<PossibleMove> col) {
        col.clear();

        BackgammonBoard board_backup = new BackgammonBoard(board);

        for (int a = 0; a < 26; a++) {
            board = new BackgammonBoard(board_backup);

            if (canMoveX(board, a, d1)) {
                BackgammonBoard board2 = new BackgammonBoard(board.placeMoveX(new Move(a,
                                                                                       a +
                                                                                       d1)));

                for (int b = 0; b < 26; b++) {
                    if (canMoveX(board2, b, d2)) {
                        BackgammonBoard board3 = new BackgammonBoard(board2.placeMoveX(new Move(b,
                                                                                                b +
                                                                                                d2)));

                        for (int c = 0; c < 26; c++) {
                            if (canMoveX(board3, c, d2)) {
                                BackgammonBoard board4 = new BackgammonBoard(board3.placeMoveX(new Move(c,
                                                                                                        c +
                                                                                                        d2)));

                                for (int d = 0; d < 26; d++) {
                                    if (canMoveX(board4, d, d2)) {
                                        PossibleMove pmove = new PossibleMove();

                                        pmove.add(new Move(a, a + d1));
                                        pmove.add(new Move(b, b + d2));
                                        pmove.add(new Move(c, c + d2));
                                        pmove.add(new Move(d, d + d2));

                                        if (JibsCollection.checkMove(col,
                                                                         pmove,
                                                                         board_backup)) {
                                            col.add(pmove);
                                        }
                                    } // if
                                } // for
                            } // if
                        } // for
                    }
                }
            }
        }
    }
}
