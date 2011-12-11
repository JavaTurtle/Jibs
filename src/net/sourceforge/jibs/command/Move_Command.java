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
			Player playerX = board.getPlayerX();
			Player playerO = board.getPlayerO();
			Player opponent = board.getOpponent(player);
			BackgammonBoard bgBoard = game.getBackgammonBoard();
			JibsWriter outX = playerX.getOutputStream();
			JibsWriter outO = playerO.getOutputStream();
			MoveBackgammon move;
			BackgammonBoard movedBoard = null;
			int turn = board.getTurn();
			strArgs = strArgs.trim();

			switch (turn) {
			case 1:// O's turn
				move = new MoveBackgammon(bgBoard);

				if (move.checkMove(playerO, strArgs, bgBoard.getCanMove())) {
					movedBoard = move.placeMoveO(strArgs);
					movedBoard.setTurn(-1); // Change turn
					movedBoard.flip();
					game.setBackgammonBoard(movedBoard);
					String outBoard = movedBoard.outBoard("You", playerX.getName(),
							-1, 1);
					outO.println(outBoard);

					// m_both_move=%0 moves %1.
					Object[] obj = new Object[] { playerO.getName(), strArgs };
					msg = jibsMessages.convert("m_both_move", obj);
					outX.println(msg);
					movedBoard.flip();
					outBoard = movedBoard.outBoard("You", playerO.getName(),
							1, -1);
					outX.println(outBoard);
				}

				break;

			case -1:// X's turn
				move = new MoveBackgammon(bgBoard);

				if (move.checkMove(playerX, strArgs, bgBoard.getCanMove())) {
					movedBoard = move.placeMoveX(strArgs);
					movedBoard.setTurn(1); // Change turn
					movedBoard.flip();
					game.setBackgammonBoard(movedBoard);
					String outBoard = movedBoard.outBoard("You", playerX.getName(),
							-1, 1);
					outO.println(outBoard);

					// m_both_move=%0 moves %1.
					Object[] obj = new Object[] { playerX.getName(), strArgs };
					msg = jibsMessages.convert("m_both_move", obj);
					outO.println(msg);
					movedBoard.flip();
					outBoard = movedBoard.outBoard("You",
							playerO.getName(), 1, -1);
					outX.println(outBoard);
				}

				break;
			}

			// won?
			bgBoard = game.getBackgammonBoard();

			if (bgBoard.getOnHome1() >= 15) {
				// PlayerX has won !
				game.winGameX(game, bgBoard, -1);

				return true;
			}

			if (bgBoard.getOnHome2() >= 15) {
				// PlayerO has won !
				game.winGameO(game, bgBoard, -1);

				return true;
			}

			JibsWriter opponentOut = null;

			if (!opponent.checkToggle("double")) {
				int maydouble = 0;

				switch (board.getTurn()) {
				case 1: // O's turn
					if (game.isPlayerX(opponent)) {
						maydouble = game.getBackgammonBoard().getMayDouble1();
						opponentOut = outX;
					} else {
						maydouble = game.getBackgammonBoard().getMayDouble2();
						opponentOut = outO;
					}

					break;

				case -1:// X's turn
					if (game.isPlayerX(opponent)) {
						maydouble = game.getBackgammonBoard().getMayDouble1();
						opponentOut = outX;
					} else {
						maydouble = game.getBackgammonBoard().getMayDouble2();
						opponentOut = outO;
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
				opponent.getClientWorker().executeCmd("roll");
			}
		}

		return true;
	}
}
