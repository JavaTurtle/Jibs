package net.sourceforge.jibs.command;

import net.sourceforge.jibs.gui.JibsTextArea;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;

import org.apache.log4j.Logger;

/**
 * Some commands are mapped to this command, given a <i>not-yet-implemented</i>
 * to the user.
 */
public class NImplemented_Command implements JibsCommand {
	private static final Logger logger = Logger
			.getLogger(NImplemented_Command.class);

	public boolean execute(Server server, Player player, String strArgs,
			String[] args) {
		// m_not_yet_implemented=** Not yet implemented: '%0'
		Object[] obj = new Object[] { args[0] + strArgs };
		String msg = server.getJibsMessages().convert("m_not_yet_implemented",
				obj);
		JibsTextArea.log(server.getJibsServer(), "\"" + player.getName()
				+ "\": " + msg, false);
		player.getOutputStream().println(msg);
		logger.warn("\"" + player.getName() + "\": " + msg);

		return true;
	}
}
