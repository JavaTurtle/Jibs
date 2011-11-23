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
			Player player1 = null;
			Player player2 = null;
			JibsWriter out1 = null;
			JibsWriter out2 = null;
			int nrMoves = -1;

			switch (turn) {
			case 1: // O
				player1 = board.getPlayerX();
				player2 = board.getPlayerO();
				out1 = player1.getOutputStream();
				out2 = player2.getOutputStream();
				nrMoves = game.rollO(out1, out2, player2, board, turn);

				if (nrMoves <= 0) {
					board.setTurn(-board.getTurn());
					board.clearDie();
					opponent.getClientWorker().executeCmd("roll");
				} else {
					String nextCmd1 = game.checkForcedMove(player2, board,
							nrMoves, out2);
					String nextCmd2 = game.checkGreedy(player2, board, nrMoves,
							out2);
					String nextCmd = null;

					if (nextCmd1 != null) {
						nextCmd = nextCmd1;
					}

					if ((nextCmd == null) && (nextCmd2 != null)) {
						nextCmd = nextCmd2;
					}

					player2.getClientWorker().executeCmd(nextCmd);
				}

				break;

			case -1: // X
				player1 = board.getPlayerX();
				player2 = board.getPlayerO();
				out1 = player1.getOutputStream();
				out2 = player2.getOutputStream();
				nrMoves = game.rollX(out1, out2, player1, board, turn);

				if (nrMoves <= 0) {
					board.setTurn(-board.getTurn());
					board.clearDie();
					opponent.getClientWorker().executeCmd("roll");
				} else {
					String nextCmd1 = game.checkForcedMove(player1, board,
							nrMoves, out1);
					String nextCmd2 = game.checkGreedy(player1, board, nrMoves,
							out1);
					String nextCmd = null;

					if (nextCmd1 != null) {
						nextCmd = nextCmd1;
					}

					if ((nextCmd == null) && (nextCmd2 != null)) {
						nextCmd = nextCmd2;
					}

					player1.getClientWorker().executeCmd(nextCmd);
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
