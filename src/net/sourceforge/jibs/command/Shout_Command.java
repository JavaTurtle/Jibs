package net.sourceforge.jibs.command;

import java.util.Map;

import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.ClipConstants;
import net.sourceforge.jibs.util.JibsWriter;

/**
 * The Shout command.
 */
public class Shout_Command implements JibsCommand {
	public boolean execute(Server server, Player player, String strArgs,
			String[] args) {
		String playerName = player.getName();
		Map<String, Player> allClients = server.getAllClients();
		JibsWriter out = player.getOutputStream();

		out.println(ClipConstants.CLIP_YOU_SHOUT + " " + strArgs);

		for (Player clientPlayer: allClients.values()) {
			if (!clientPlayer.getName().equals(player.getName())) {
				if (clientPlayer.checkToggle("silent")) {
					JibsWriter clientOut = clientPlayer.getOutputStream();

					clientOut.println(ClipConstants.CLIP_SHOUTS + " "
							+ playerName + strArgs);
				}
			}
		}

		return true;
	}
}
