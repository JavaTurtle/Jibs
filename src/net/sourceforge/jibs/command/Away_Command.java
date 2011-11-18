package net.sourceforge.jibs.command;

import java.util.Collection;

import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.JibsWriter;

/**
 * The Away command.
 */
public class Away_Command implements JibsCommand {
    public boolean execute(Server server, Player player,
                           String strArgs, String[] args) {
        JibsMessages jibsMessages = server.getJibsMessages();
        JibsWriter out = player.getOutputStream();
        String msg = null;
        String strPlayer = null;
        StringBuffer sBuffer = null;

        if (args.length > 1) {
            player.changeToggle("away", Boolean.TRUE);
            player.setAwayMsg(strArgs);
            // m_you_away=You're away. Please type 'back'
            msg = jibsMessages.convert("m_you_away");
            out.println(msg);
            player.informToggleChange();
        } else {
            Collection<Player> awayPlayerCollection = server.getAwayPlayer();

            if ((awayPlayerCollection == null) ||
                    (awayPlayerCollection.size() == 0)) {
                // m_away_none=None of the users is away.
                msg = jibsMessages.convert("m_away_none");
                out.println(msg);
            } else {
                // m_away_list=The following users are away:
                msg = jibsMessages.convert("m_away_list");
                out.println(msg);

                for (Player awayPlayer : awayPlayerCollection) {
                    sBuffer = new StringBuffer();
                    strPlayer = awayPlayer.getName();

                    if (strPlayer.equals(player.getName())) {
                        strPlayer = "You";
                    }

                    sBuffer.append(strPlayer);
                    sBuffer.append(":");
                    sBuffer.append(awayPlayer.getAwayMsg());
                    out.println(sBuffer.toString());
                }
            }
        }

        return true;
    }
}
