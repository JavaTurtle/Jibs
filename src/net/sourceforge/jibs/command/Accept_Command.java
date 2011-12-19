package net.sourceforge.jibs.command;

import net.sourceforge.jibs.backgammon.BackgammonBoard;
import net.sourceforge.jibs.backgammon.JibsGame;
import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.server.JibsQuestion;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.DoubleQuestion;
import net.sourceforge.jibs.util.JibsWriter;
import net.sourceforge.jibs.util.ResignQuestion;

/**
 * The Accept command.
 */
public class Accept_Command implements JibsCommand {
	public boolean execute(Server server, Player player, String strArgs,
			String[] args) {
		JibsMessages jibsMessages = server.getJibsMessages();
		JibsGame game = player.getGame();
		JibsQuestion question = player.getQuestion();
		JibsWriter out = player.getOutputStream();
		BackgammonBoard board = game.getBackgammonBoard();
		Player opponent = board.getOpponent(player);
		JibsWriter out2 = opponent.getOutputStream();

		if (question instanceof ResignQuestion) {
			BackgammonBoard bgBoard1 = null;
			ResignQuestion resignQuestion = (ResignQuestion) question;
			int points = resignQuestion.getResignMode();

			bgBoard1 = game.getBackgammonBoard();

			if (game.isPlayerX(player)) {
				game.winGameX(game, bgBoard1, points);
			} else {
				game.winGameO(game, bgBoard1, points);
			}
		}

		if (question instanceof DoubleQuestion) {
			DoubleQuestion doubleQuestion = (DoubleQuestion) question;

			// m_accept_double=You accept the double. The cube shows %0.
			String msg = jibsMessages.convert("m_accept_double", Integer.valueOf(doubleQuestion.getCubeNew()));

			out.println(msg);
			// m_accept_double_opponent=%0 accepts the double. The cube shows
			// %1.
			msg = jibsMessages.convert("m_accept_double_opponent", player.getName(),
					Integer.valueOf(doubleQuestion.getCubeNew()));
			out2.println(msg);

			board.setCubeNumber(doubleQuestion.getCubeNew());

			switch (board.getTurn()) {
			case -1: // X accepts double from O
				board.setMayDouble2(0); // No
				board.setMayDouble1(1);
				opponent.getClientWorker().executeCmd("roll");

				break;

			case 1: // O acepts double from X
				board.setMayDouble1(0);
				board.setMayDouble2(1);
				opponent.getClientWorker().executeCmd("roll");

				break;
			}
		}

		return true;
	}
}
