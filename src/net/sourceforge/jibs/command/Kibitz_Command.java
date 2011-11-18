package net.sourceforge.jibs.command;

import net.sourceforge.jibs.backgammon.BackgammonBoard;
import net.sourceforge.jibs.backgammon.JibsGame;
import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.ClipConstants;
import net.sourceforge.jibs.util.JibsWriter;

/**
 * The Kibitz command.
 */
public class Kibitz_Command implements JibsCommand {
    public boolean execute(Server server, Player player,
                           String strArgs, String[] args) {
        JibsMessages jibsMessages = server.getJibsMessages();
        JibsWriter out = player.getOutputStream();
        String msg = null;
        JibsGame game = player.getGame();

        if (game != null) {
            BackgammonBoard board = game.getBackgammonBoard();
            Player player1 = board.getPlayerX();
            Player player2 = board.getPlayerO();
            JibsWriter out1 = player1.getOutputStream();
            JibsWriter out2 = player2.getOutputStream();

            if (board.isPlayerX(player)) {
                out1.println(ClipConstants.CLIP_YOU_KIBITZ + " " + strArgs);
                out2.println(ClipConstants.CLIP_KIBITZES + " " +
                             player.getName() + " " + strArgs);
                msg = ClipConstants.CLIP_KIBITZES + " " + player.getName() +
                      " " + strArgs;

                int heard = player1.informWatcher(msg, null, false) +
                            player2.informWatcher(msg, null, false) + 1;

                out1.println("** " + heard + " user heard you.");
            } else {
                out2.println(ClipConstants.CLIP_YOU_KIBITZ + " " + strArgs);
                out1.println(ClipConstants.CLIP_KIBITZES + " " +
                             player.getName() + " " + strArgs);
                msg = ClipConstants.CLIP_KIBITZES + " " + player.getName() +
                      " " + strArgs;

                int heard = player1.informWatcher(msg, null, false) +
                            player2.informWatcher(msg, null, false) + 1;

                out2.println("** " + heard + " user heard you.");
            }
        } else {
            // m_kibitz_no_play=** You're not watching or playing.
            msg = jibsMessages.convert("m_kibitz_no_play");
            out.println(msg);
        }

        return true;
    }
}
