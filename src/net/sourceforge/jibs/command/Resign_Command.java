package net.sourceforge.jibs.command;

import net.sourceforge.jibs.backgammon.BackgammonBoard;
import net.sourceforge.jibs.backgammon.JibsGame;
import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.JibsWriter;
import net.sourceforge.jibs.util.ResignQuestion;

/**
 * The Resign command.
 */
public class Resign_Command implements JibsCommand {
	public boolean execute(Server server, Player player, String strArgs,
			String[] args) {
		JibsGame game = player.getGame();
		JibsMessages jibsMessages = server.getJibsMessages();
		JibsWriter out = player.getOutputStream();
		int points = 0;
		BackgammonBoard board = game.getBackgammonBoard();

		if ("normal".startsWith(args[1])) {
			points = board.getCubeNumber();
		}

		if ("gammon".startsWith(args[1])) {
			points = 2 * board.getCubeNumber();
		}

		if ("backgammon".startsWith(args[1])) {
			points = 2 * board.getCubeNumber();
		}

		Player opponentPlayer = player.getOpponent();
		JibsWriter out2 = opponentPlayer.getOutputStream();

		// m_you_resign=You want to resign. %0 will win %1 point.
		String msg = jibsMessages.convert("m_you_resign",  opponentPlayer.getName(), Integer.valueOf(points));

		out.println(msg);

		// m_other_resign=%0 wants to resign. You will win %1 point. Type
		// 'accept' or 'reject'.
		msg = jibsMessages.convert("m_other_resign",  player.getName(), Integer.valueOf(points));
		out2.println(msg);
		player.informWatcher(msg, null, false);
		opponentPlayer.informWatcher(msg, null, false);

		opponentPlayer.setQuestion(new ResignQuestion(points));

		return true;
	}
}
