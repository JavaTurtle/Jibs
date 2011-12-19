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
 * The Reject command.
 */
public class Reject_Command implements JibsCommand {
	public boolean execute(Server server, Player player, String strArgs,
			String[] args) {
		JibsMessages jibsMessages = server.getJibsMessages();
		JibsGame game = player.getGame();
		BackgammonBoard board = null;
		String msg = null;
		JibsQuestion question = player.getQuestion();
		JibsWriter out = player.getOutputStream();
		Player opponent = null;

		if (game != null) {
			board = game.getBackgammonBoard();
			opponent = board.getOpponent(player);

			if (question instanceof DoubleQuestion) {
				DoubleQuestion doubleQuestion = (DoubleQuestion) question;
				int points = doubleQuestion.getCubeOld();

				if (points == 1) {
					// m_you_giveup_1=You give up. %0 wins 1 point.
					msg = jibsMessages.convert("m_you_giveup_1", opponent.getName());
					out.println(msg);
					// m_other_giveup1=%0 gives up. You win 1 point.
					msg = jibsMessages.convert("m_other_giveup1", player.getName());
					opponent.getOutputStream().println(msg);
				} else {
					// m_you_giveup_1=You give up. %0 wins %1 points.
					msg = jibsMessages.convert("m_you_giveup_2", opponent.getName(),
							Integer.valueOf(points));
					out.println(msg);
					// m_other_giveup2=%0 gives up. You win %1 points.
					msg = jibsMessages.convert("m_other_giveup2", player.getName(),
							Integer.valueOf(points));
					opponent.getOutputStream().println(msg);
				}

				if (game.isPlayerX(player)) {
					game.winGameO(game, board, points);
				} else {
					game.winGameX(game, board, points);
				}
			}

			if (question instanceof ResignQuestion) {
				// m_you_reject=You reject. The game continues.
				msg = jibsMessages.convert("m_you_reject");
				out.println(msg);
				// m_other_reject=%0 rejects. The game continues.
				msg = jibsMessages.convert("m_other_reject", player.getName());
				opponent.getOutputStream().println(msg);
			}
		} else {
			// m_you_not_playing=** You're not playing.
			msg = jibsMessages.convert("m_you_not_playing");
			out.println(msg);
		}

		return true;
	}
}
