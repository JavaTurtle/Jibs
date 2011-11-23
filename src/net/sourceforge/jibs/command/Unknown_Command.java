/*
 * Created on 02.11.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.sourceforge.jibs.command;

import net.sourceforge.jibs.gui.JibsTextArea;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;

import org.apache.log4j.Logger;

/**
 * Whenever jIBS couldn't identify the command, it uses this command, given an
 * <i>Unknown command</i> to the user.
 */
public class Unknown_Command implements JibsCommand {
	private static final Logger logger = Logger
			.getLogger(Unknown_Command.class);

	public boolean execute(Server server, Player player, String strArgs,
			String[] args) {
		// m_unknown_command=** Unknown command: '%0'
		Object[] obj = new Object[] { strArgs };
		String msg = server.getJibsMessages().convert("m_unknown_command", obj);

		JibsTextArea.log(server.getJibsServer(), "\"" + player.getName()
				+ "\": " + msg, false);
		player.getOutputStream().println(msg);
		logger.warn("\"" + player.getName() + "\": " + msg);

		return true;
	}
}
