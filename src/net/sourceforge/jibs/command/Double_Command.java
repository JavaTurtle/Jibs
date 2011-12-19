package net.sourceforge.jibs.command;

import net.sourceforge.jibs.backgammon.BackgammonBoard;
import net.sourceforge.jibs.backgammon.JibsGame;
import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.server.JibsQuestion;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.DoubleQuestion;
import net.sourceforge.jibs.util.JibsWriter;

/**
 * The Double command.
 */
public class Double_Command implements JibsCommand {
	public boolean execute(Server server, Player player, String strArgs,
			String[] args) {
		JibsMessages jibsMessages = server.getJibsMessages();
		JibsGame game = player.getGame();
		String msg = null;
		JibsWriter out = player.getOutputStream();

		if (game != null) {
			BackgammonBoard board = game.getBackgammonBoard();
			Player playerX = board.getPlayerX();
			Player playerO = board.getPlayerO();
			JibsWriter outO = playerO.getOutputStream();
			JibsWriter outX = playerX.getOutputStream();

			switch (board.getTurn()) {
			case 1: // O

				if (!board.isCrawFordGame()) {
					int mayDouble = board.getMayDouble2();

					if (mayDouble == 0) {
						// m_you_double_notallowed=** It's not your turn to
						// double
						msg = jibsMessages.convert("m_you_double_notallowed");
						outO.println(msg);

						return player.getClientWorker().executeCmd("roll");
					} else {
						if ((board.getDice1() != 0) && (board.getDice2() != 0)) {
							// m_you_double_roll=** You can only double before
							// you roll the dice.
							msg = jibsMessages.convert("m_you_double_roll");
							outO.println(msg);

							return player.getClientWorker().executeCmd("roll");
						}

						// m_you_double=You double. Please wait for %0 to accept
						// or reject.
						msg = jibsMessages.convert("m_you_double", playerX.getName());
						outO.println(msg);
						// m_opponent_double=%0 doubles. Type 'accept' or
						// 'reject'.
						msg = jibsMessages.convert("m_opponent_double", playerO.getName());
						outX.println(msg);

						int cubeNumber = board.getCubeNumber() * 2;
						JibsQuestion doubleQuestion = new DoubleQuestion(
								cubeNumber, board.getCubeNumber());
						playerX.ask(doubleQuestion);
					}
				}

				break;

			case -1: // X

				if (!board.isCrawFordGame()) {
					int mayDouble = board.getMayDouble1();

					if (mayDouble == 0) {
						// m_you_double_notallowed=** It's not your turn to
						// double
						msg = jibsMessages.convert("m_you_double_notallowed");
						outX.println(msg);

						return player.getClientWorker().executeCmd("roll");
					} else {
						if ((board.getDice1() != 0) && (board.getDice2() != 0)) {
							// m_you_double_roll=** You can only double before
							// you roll the dice.
							msg = jibsMessages.convert("m_you_double_roll");
							outX.println(msg);

							return player.getClientWorker().executeCmd("roll");
						}

						// m_you_double=You double. Please wait for %0 to accept
						// or reject.
						msg = jibsMessages.convert("m_you_double", playerO.getName());
						outX.println(msg);
						// m_opponent_double=%0 doubles. Type 'accept' or
						// 'reject'.
						msg = jibsMessages.convert("m_opponent_double",  playerX.getName());
						outO.println(msg);

						int cubeNumber = board.getCubeNumber() * 2;
						JibsQuestion doubleQuestion = new DoubleQuestion(
								cubeNumber, board.getCubeNumber());
						playerO.ask(doubleQuestion);
					}
				}

				break;
			}
		} else {
			// m_you_not_playing=** You're not playing.
			msg = jibsMessages.convert("m_you_not_playing");
			out.println(msg);
		}

		return true;
	}
}
