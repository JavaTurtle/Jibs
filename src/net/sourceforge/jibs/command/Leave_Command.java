package net.sourceforge.jibs.command;

import net.sourceforge.jibs.backgammon.BackgammonBoard;
import net.sourceforge.jibs.backgammon.JibsGame;
import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.gui.JibsTextArea;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.JibsWriter;

/**
 * The Leave command.
 */
public class Leave_Command implements JibsCommand {
	public boolean execute(Server server, Player player, String strArgs,
			String[] args) {
		JibsWriter out = player.getOutputStream();
		JibsMessages jibsMessages = server.getJibsMessages();
		String msg = null;
		JibsGame game = player.getGame();

		if (game == null) {
			msg = jibsMessages.convert("m_leave_error");
			out.println(msg);
		} else {
			BackgammonBoard board = game.getBackgammonBoard();
			msg = jibsMessages.convert("m_leave");
			out.println(msg);

			Player opponent = board.getOpponent(player);
			JibsWriter outOpponent = opponent.getOutputStream();
			Object[] obj = { player.getName() };

			msg = jibsMessages.convert("m_other_leave", obj);
			JibsTextArea.log(server.getJibsServer(), msg, true);
			outOpponent.println(msg);
			game.save();
		}

		return true;
	}

	public boolean saveGamePanic(Server server, JibsGame game) {
		if (game != null) {
			game.save();
		}
		return true;
	}

}
