package net.sourceforge.jibs.command;

import net.sourceforge.jibs.backgammon.BackgammonBoard;
import net.sourceforge.jibs.backgammon.JibsGame;
import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.JibsWriter;

/**
 * The Roll command.
 */
public class Roll_Command implements JibsCommand {
	public boolean execute(Server server, Player player, String strArgs,
			String[] args) {
		JibsMessages jibsMessages = server.getJibsMessages();
		JibsWriter out = player.getOutputStream();
		JibsGame game = player.getGame();

		if (game != null) {
			BackgammonBoard board = game.getBackgammonBoard();
			Player opponent = board.getOpponent(player);
			int turn = board.getTurn();
			Player playerX = board.getPlayerX();
			Player playerO = board.getPlayerO();
			JibsWriter outX = playerX.getOutputStream();
			JibsWriter outO = playerO.getOutputStream();
			int nrMoves = -1;

			switch (turn) {
			case 1: // O
				nrMoves = game.rollO(outX, outO, playerO, board, turn);

				if (nrMoves <= 0) {
					board.setTurn(-board.getTurn());
					opponent.getClientWorker().executeCmd("roll");
				} else {
					String nextCmd1 = game.checkForcedMove(playerO, board,
							outO);
					String nextCmd2 = game.checkGreedy(playerO, board, nrMoves,
							outO);
					String nextCmd = null;

					if (nextCmd1 != null) {
						nextCmd = nextCmd1;
					}

					if ((nextCmd == null) && (nextCmd2 != null)) {
						nextCmd = nextCmd2;
					}
					
					playerO.getClientWorker().executeCmd(nextCmd);
				}

				break;

			case -1: // X
				nrMoves = game.rollX(outX, outO, playerX, board, turn);

				if (nrMoves <= 0) {
					board.setTurn(-board.getTurn());
					opponent.getClientWorker().executeCmd("roll");
				} else {
					String nextCmd1 = game.checkForcedMove(playerX, board,
							outX);
					String nextCmd2 = game.checkGreedy(playerX, board, nrMoves,
							outX);
					String nextCmd = null;

					if (nextCmd1 != null) {
						nextCmd = nextCmd1;
					}

					if ((nextCmd == null) && (nextCmd2 != null)) {
						nextCmd = nextCmd2;
					}

					playerX.getClientWorker().executeCmd(nextCmd);
				}

				break;
			}
		} else {
			// m_you_not_playing=** You're not playing.
			String msg = jibsMessages.convert("m_you_not_playing");
			out.println(msg);
		}

		return true;
	}

}
