package net.sourceforge.jibs.command;

import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;

/**
 * This is the <b>central</b> interface all commands must implement.
 * 
 */
public interface JibsCommand {
	/**
	 * The method which jIBS invokes when it identifies a command. All command
	 * implementation must perform their tasks under this routine.
	 * 
	 * @param jibsServer
	 *            calling server.
	 * @param player
	 *            calling player.
	 * @param strArgs
	 *            the argument as one big String.
	 * @param args
	 *            the arguments as an array.
	 */
	public boolean execute(Server server, Player player, String strArgs,
			String[] args);
}
