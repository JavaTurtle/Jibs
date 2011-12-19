package net.sourceforge.jibs.command;

import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.JibsWriter;

/**
 * The Whois command.
 */
public class Whois_Command implements JibsCommand {
	public boolean execute(Server server, Player player, String strArgs,
			String[] args) {
		JibsMessages jibsMessages = server.getJibsMessages();
		JibsWriter out = player.getOutputStream();
		String msg = null;
		if (args.length == 2) {
			String name = args[1];
			Player whoisPlayerDB = Player.load(server.getJibsServer()
					.getSqlSessionFactory(), name); //

			if (whoisPlayerDB != null) {
				WhoisPlayer.whois(server, out, whoisPlayerDB);
			} else {
				// m_noone=** There is no one called %0.
				msg = jibsMessages.convert("m_noinformation", name);
				out.println(msg);

				return true;
			}
		} else {
			// m_argument_mssing=** please give a name as an argument.
			msg = jibsMessages.convert("m_argument_mssing");
			out.println(msg);
		}

		return true;
	}
}
