package net.sourceforge.jibs.command;

import net.sourceforge.jibs.backgammon.BackgammonBoard;
import net.sourceforge.jibs.backgammon.JibsGame;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.JibsWriter;

/**
 * The Board command.
 */
public class Board_Command implements JibsCommand {
	public boolean execute(Server server, Player player,
			String strArgs, String[] args) {
		JibsGame game = player.getGame();
		JibsWriter out = player.getOutputStream();

		if (game != null) {
			BackgammonBoard board = game.getBackgammonBoard();
			int turn = board.getTurn();

			if (board.isPlayerX(player)) {
				board.outBoard(out, "You", turn, board.getPlayerXdie1Value(),
						board.getPlayerXdie2Value(),
						board.getPlayerOdie1Value(),
						board.getPlayerOdie2Value());
			} else {
				BackgammonBoard opBoard = board.switch2O();
				opBoard.outBoard(out, "You", turn,
						opBoard.getPlayerOdie1Value(),
						opBoard.getPlayerOdie2Value(),
						opBoard.getPlayerXdie1Value(),
						opBoard.getPlayerXdie2Value());
			}
		}

		return true;
	}
}
