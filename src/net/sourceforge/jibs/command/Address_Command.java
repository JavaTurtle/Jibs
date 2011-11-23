package net.sourceforge.jibs.command;

import net.sourceforge.jibs.server.ClientWorker;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.JibsWriter;

/**
 * The Adress command.
 */
public class Address_Command implements JibsCommand {
	public boolean execute(Server server, Player player, String strArgs,
			String[] args) {
		String playername = player.getName();
		JibsWriter out = player.getOutputStream();

		if (args.length == 2) {
			String email_address = args[1];

			if (ClientWorker.changeAddress(server.getJibsServer()
					.getSqlSessionFactory(), player, email_address)) {
				out.println(playername + "'s adress changed to "
						+ email_address);
			} else {
				out.println("Error: " + playername + "'s adress unchanged");
			}
		} else {
			out.println("Wrong parameters");
		}

		out.flush();

		return true;
	}
}
