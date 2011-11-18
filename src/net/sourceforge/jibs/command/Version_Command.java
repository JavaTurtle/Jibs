package net.sourceforge.jibs.command;

import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.JibsWriter;

public class Version_Command implements JibsCommand {
	@Override
	public boolean execute(Server server, Player player, String strArgs,
			String[] args) {
		JibsWriter out = player.getOutputStream();
		String version = server.getConfiguration().getResource("aboutVersion");
		out.println("** "+version);
		return false;
	}

}
