package net.sourceforge.jibs.command;

import net.sourceforge.jibs.backgammon.BackgammonBoard;
import net.sourceforge.jibs.backgammon.JibsGame;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.ClipConstants;
import net.sourceforge.jibs.util.JibsWriter;

/**
 * The Whisper command.
 */
public class Whisper_Command implements JibsCommand {
    public boolean execute(Server server, Player player,
                           String strArgs, String[] args) {
        JibsGame game = player.getGame();
        JibsWriter out = player.getOutputStream();

        if (game != null) {
            BackgammonBoard board = game.getBackgammonBoard();
            Player player1 = board.getPlayerX();
            Player player2 = board.getPlayerO();
            out.println(ClipConstants.CLIP_YOU_WHISPER + " " + strArgs);

            String msg = ClipConstants.CLIP_WHISPERS + " " + player1.getName() +
                         " " + strArgs;
            int heard = player1.informWatcher(msg, null, false) +
                        player2.informWatcher(msg, null, false);

            out.println("** " + heard + " user heard you.");
        }

        return true;
    }
}
