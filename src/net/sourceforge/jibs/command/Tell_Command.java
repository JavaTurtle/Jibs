package net.sourceforge.jibs.command;

import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.ClipConstants;
import net.sourceforge.jibs.util.JibsWriter;

/**
 * The Tell command.
 */
public class Tell_Command implements JibsCommand {
    public boolean execute(Server server, Player player,
                           String strArgs, String[] args) {
        JibsMessages messages = server.getJibsMessages();
    	JibsWriter out = player.getOutputStream();
        String msg;

        if (args.length >= 1) {
            Player destPlayer = server.getPlayer(args[1]);

            /*
             * m_no_listen** %0 won't listen to you. m_you_gagged=** You can't
             * talk if you won't listen. m_tell_yourself=You say to yourself: %0
             */
            if (destPlayer != null) {
                int index = strArgs.indexOf(args[1]);
                String sayText = null;

                if (index >= 0) {
                    sayText = strArgs.substring(index + args[1].length());
                }

                JibsWriter outDest = destPlayer.getOutputStream();

                outDest.println(ClipConstants.CLIP_SAYS + " " +
                                player.getName() + " " + sayText.trim());
                out.println(ClipConstants.CLIP_YOU_SAY + " " +
                            destPlayer.getName() + " " + sayText.trim());
            } else {
                // m_noone=** There is no one called %0.
                Object[] obj = new Object[] { args[1] };

                msg = messages.convert("m_noone", obj);
                out.println(msg);
            }
        }

        return true;
    }
}
