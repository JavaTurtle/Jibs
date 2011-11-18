package net.sourceforge.jibs.command;

import net.sourceforge.jibs.backgammon.BackgammonBoard;
import net.sourceforge.jibs.backgammon.JibsGame;
import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.JibsWriter;

/**
 * The Watch command.
 */
public class Watch_Command implements JibsCommand {
    public boolean execute(Server server, Player player,
                           String strArgs, String[] args) {
        JibsMessages jibsMessages = server.getJibsMessages();
    	JibsWriter out = player.getOutputStream();
        String msg = null;
        Object[] obj = null;

        if (args.length >= 2) {
            String Name = args[1];
            Player playingPlayer = server.getPlayer(Name);

            if (playingPlayer == null) {
                // m_noone=** There is no one called %0.
                obj = new Object[] { args[1] };
                msg = jibsMessages.convert("m_noone", obj);
                out.println(msg);

                return true;
            }

            JibsGame game = playingPlayer.getGame();
            // valid player
            playingPlayer.addWatcher(player);
            player.changeToggle("watch", Boolean.TRUE);
            player.setWatchingPlayer(playingPlayer);

            if (game == null) {
                // m_watch_no_interesting=%0 is not doing anything interesting.
                obj = new Object[] { playingPlayer.getName() };
                msg = jibsMessages.convert("m_watch_no_interesting", obj);
                out.println(msg);
                // m_you_watch_not_play=%0 is not playing
                obj = new Object[] { playingPlayer.getName() };
                msg = jibsMessages.convert("m_you_watch_not_play", obj);
                out.println(msg);
            } else {
                BackgammonBoard board = game.getBackgammonBoard();

                switch (board.getTurn()) {
                case 1: // O

                    int d1 = board.getPlayerXdie1Value();
                    int d2 = board.getPlayerXdie2Value();
                    player.show2WatcherBoard(board, player.getName(), 0, 0, d1,
                                             d2);

                    break;

                case -1:
                    d1 = board.getPlayerXdie1Value();
                    d2 = board.getPlayerXdie2Value();
                    playingPlayer.show2WatcherBoard(board,
                                                    playingPlayer.getName(),
                                                    d1, d2, 0, 0);

                    break;
                }
            }

            return true;
        } else {
            // m_watch_who=** Watch who?
            msg = jibsMessages.convert("m_watch_who");
            out.println(msg);
        }

        return true;
    }
}
