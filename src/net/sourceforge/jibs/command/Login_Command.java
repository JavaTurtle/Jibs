package net.sourceforge.jibs.command;

import java.util.StringTokenizer;

import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;

import org.apache.ibatis.session.SqlSessionFactory;

/**
 * The Login_Command will be invoked by jIBS when a user tries to login. Though
 * it adheres to the JibsCommand interface, normal processing is done by jIBS in
 * a different way.
 */
public class Login_Command implements JibsCommand {
	public static Player login(SqlSessionFactory sqlMapper, String cmd) {
		Player player = null;
		if (cmd != null) {
			StringTokenizer stoken = new StringTokenizer(cmd);
			String[] args = new String[5];
			int i = 0;

			while (stoken.hasMoreTokens()) {
				args[i++] = stoken.nextToken();
			}
			if ((i == 1) && "guest".equalsIgnoreCase(args[0])) {
				player = new Player();
				player.setName("guest");
			}
			if (i == 5) {
				// bypass password confirmation
				player = Player.load(sqlMapper, args[3]);
				if (player != null) {
					player.canCLIP(true);
					player.setValid(player.is_valid(sqlMapper, player, args[3],
							args[4]));
					if (!player.is_valid())
						player = null;
				}
			}
		}
		return player;
	}

	public boolean execute(Server server, Player player, String strArgs,
			String[] args) {
		throw new IllegalArgumentException("Not possible");
	}
}
