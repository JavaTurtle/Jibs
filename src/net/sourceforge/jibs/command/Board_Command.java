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
	public boolean execute(Server server, Player player, String strArgs,
			String[] args) {
		JibsGame game = player.getGame();
		JibsWriter out = player.getOutputStream();
boolean didFlip = false;
		if (game != null) {
			BackgammonBoard board = game.getBackgammonBoard();
			Player opponent = board.getOpponent(player);
			int direction;
			int color;
			String name2;
			if (game.isPlayerX(player)) {
				direction = 1;
				color = -1;
				name2 = opponent.getName();
			} else {
				direction = -1;
				color = 1;
				name2 = opponent.getName();
				board.flip();
				didFlip = true;
			}
			String outBoard = board.outBoard("You", name2, direction, color);
			out.println(outBoard);
			if (didFlip)
				board.flip();
		}

		return true;
	}
}
