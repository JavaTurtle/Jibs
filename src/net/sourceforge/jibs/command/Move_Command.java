package net.sourceforge.jibs.command;

import net.sourceforge.jibs.backgammon.BackgammonBoard;
import net.sourceforge.jibs.backgammon.JibsGame;
import net.sourceforge.jibs.backgammon.MoveBackgammon;
import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.JibsWriter;

/**
 * The Move command.
 */
public class Move_Command implements JibsCommand {
	public boolean execute(Server server, Player player, String strArgs,
			String[] args) {
		JibsMessages jibsMessages = server.getJibsMessages();
		String msg = null;
		JibsGame game = player.getGame();

		if (game != null) {
			BackgammonBoard board = game.getBackgammonBoard();
			Player player1 = board.getPlayerX();
			Player player2 = board.getPlayerO();
			Player opponent = board.getOpponent(player);
			BackgammonBoard bgBoard = game.getBackgammonBoard();
			BackgammonBoard opBoard;
			JibsWriter out1 = player1.getOutputStream();
			JibsWriter out2 = player2.getOutputStream();
			MoveBackgammon move;
			BackgammonBoard movedBoard = null;
			int turn = board.getTurn();
			strArgs = strArgs.trim();

			switch (turn) {
			case 1:
				move = new MoveBackgammon(bgBoard);

				if (move.checkMove(player2, strArgs, bgBoard.getCanMove())) {
					movedBoard = move.placeMoveO(strArgs);
					game.setBackgammonBoard(movedBoard);
					opBoard = movedBoard.switch2O();
					String outBoard = opBoard.outBoard("You", -turn, 0, 0, 0, 0);
					out2.println(outBoard);
					player2.show2WatcherBoard(opBoard, player2.getName(), 0, 0,
							0, 0);

					// m_both_move=%0 moves %1.
					Object[] obj = new Object[] { player2.getName(), strArgs };
					msg = jibsMessages.convert("m_both_move", obj);
					out1.println(msg);
					player1.show2WatcherMove(msg);
					player2.show2WatcherMove(msg);
					outBoard = movedBoard.outBoard("You", -turn, 0, 0, 0, 0);
					out1.println(outBoard);
					player1.show2WatcherBoard(movedBoard, player1.getName(), 0,
							0, 0, 0);
				}

				break;

			case -1:
				move = new MoveBackgammon(bgBoard);

				if (move.checkMove(player1, strArgs, bgBoard.getCanMove())) {
					movedBoard = move.placeMoveX(strArgs);
					game.setBackgammonBoard(movedBoard);
					String outBoard = movedBoard.outBoard("You", -turn, 0, 0, 0, 0);
					out1.println(outBoard);
					player1.show2WatcherBoard(movedBoard, player1.getName(), 0,
							0, 0, 0);

					// m_both_move=%0 moves %1.
					Object[] obj = new Object[] { player1.getName(), strArgs };
					msg = jibsMessages.convert("m_both_move", obj);
					out2.println(msg);
					player1.show2WatcherMove(msg);
					player2.show2WatcherMove(msg);
					opBoard = movedBoard.switch2O();
					outBoard = opBoard.outBoard("You", -turn, 0, 0, 0, 0);
					out2.println(outBoard);
					player1.show2WatcherBoard(opBoard, player1.getName(), 0, 0,
							0, 0);
				}

				break;
			}

			board = game.getBackgammonBoard();

			// won?
			bgBoard = game.getBackgammonBoard();

			if (bgBoard.getOnHome1() >= 15) {
				// Player1 has won !
				game.winGameX(game, bgBoard, -1);

				return true;
			}

			if (bgBoard.getOnHome2() >= 15) {
				// Player2 has won !
				game.winGameO(game, bgBoard, -1);

				return true;
			}

			JibsWriter opponentOut = null;

			switch (board.getTurn()) {
			case 1:
				board.setPlayerOdie1Value(0);
				board.setPlayerOdie2Value(0);

				break;

			case -1:
				board.setPlayerXdie1Value(0);
				board.setPlayerXdie2Value(0);

				break;
			}

			board.setTurn(-board.getTurn());

			if (!opponent.checkToggle("double")) {
				int maydouble = 0;

				switch (board.getTurn()) {
				case 1:

					if (game.isPlayerX(opponent)) {
						maydouble = game.getBackgammonBoard().getMayDouble1();
						opponentOut = out1;
					} else {
						maydouble = game.getBackgammonBoard().getMayDouble2();
						opponentOut = out2;
					}

					break;

				case -1:

					if (game.isPlayerX(opponent)) {
						maydouble = game.getBackgammonBoard().getMayDouble1();
						opponentOut = out1;
					} else {
						maydouble = game.getBackgammonBoard().getMayDouble2();
						opponentOut = out2;
					}

					break;
				}

				if (maydouble == 1) {
					// m_roll_or_double=It's your turn. Please roll or double
					msg = jibsMessages.convert("m_roll_or_double");
					opponentOut.println(msg);
				} else {
					opponent.getClientWorker().executeCmd("roll");
				}
			} else {
				board.setPlayerXdie1Value(0);
				board.setPlayerXdie2Value(0);
				opponent.getClientWorker().executeCmd("roll");
			}
		}

		return true;
	}
}
