package net.sourceforge.jibs.command;

import java.util.HashMap;

import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.ClipConstants;
import net.sourceforge.jibs.util.JibsWriter;

/**
 * The Who command.
 */
public class Who_Command implements JibsCommand {
	// ---------------------------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------------------------------
	public boolean execute(Server server, Player player, String strArgs,
			String[] args) {
		HashMap allClients = server.getAllClients();
		JibsWriter out = player.getOutputStream();
		for (Object obj : allClients.values()) {
			Player aPlayer = (Player) obj;
			JibsWriter aout = aPlayer.getOutputStream();
			StringBuilder builder = new StringBuilder();
			builder.append(ClipConstants.CLIP_WHO_INFO + " ");
			String whoinfo = Player.whoinfo(aPlayer);
			builder.append(whoinfo);
			out.println(builder.toString());
			if (!aPlayer.getName().equals(player.getName())) {
				if (aPlayer.canCLIP()) {
					aout.print(ClipConstants.CLIP_WHO_INFO + " ");
					String whoinfo2 = Player.whoinfo(player);
					aout.println(whoinfo2);
					aout.println(ClipConstants.CLIP_WHO_END + " ");
				}
			}
			aout.println(ClipConstants.CLIP_WHO_END + " ");

		}
		out.println(ClipConstants.CLIP_WHO_END + " ");
		return true;
	}
}
