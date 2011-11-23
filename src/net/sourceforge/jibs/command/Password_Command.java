package net.sourceforge.jibs.command;

import java.util.StringTokenizer;

import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.server.ClientWorker;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.JibsWriter;

/**
 * The Password command.
 */
public class Password_Command implements JibsCommand {
	public boolean execute(Server server, Player player, String strArgs,
			String[] args) {
		JibsMessages jibsMessages = server.getJibsMessages();
		String msg = null;
		JibsWriter out = player.getOutputStream();

		if (args.length != 2) {
			// m_password_useage1=** usage: password <old password>:<new
			// password>:<new password>
			msg = jibsMessages.convert("m_password_useage1");
			out.println(msg);
			// m_password_useage2=** NOTE: The character between the passwords
			// is now a colon!
			msg = jibsMessages.convert("m_password_useage2");
			out.println(msg);

			return true;
		}

		String oldPassword = null;
		String newPassword1 = null;
		String newPassword2 = null;
		boolean useage = false;
		StringTokenizer token = new StringTokenizer(args[1], ":");

		if (token.hasMoreElements()) {
			oldPassword = token.nextToken();

			if (token.hasMoreElements()) {
				newPassword1 = token.nextToken();

				if (token.hasMoreElements()) {
					newPassword2 = token.nextToken();
					useage = true;
				}
			}
		}

		if (!useage) {
			// m_password_useage1=** usage: password <old password>:<new
			// password>:<new password>
			msg = jibsMessages.convert("m_password_useage1");
			out.println(msg);
			// m_password_useage2=** NOTE: The character between the passwords
			// is now a colon!
			msg = jibsMessages.convert("m_password_useage2");
			out.println(msg);

			return true;
		}

		// useage is ok are the parameters?
		if (!player.getPassword().equals(oldPassword)) {
			// m_password_badpassword1=** Sorry. Old password not correct.
			msg = jibsMessages.convert("m_password_badpassword1");
			out.println(msg);

			return true;
		}

		if (!newPassword1.equals(newPassword2)) {
			// m_password_badpassword2=** Please give your new password twice.
			msg = jibsMessages.convert("m_password_badpassword2");
			out.println(msg);

			return true;
		}

		// Everything ok to change the password
		ClientWorker.changePassword(server.getJibsServer()
				.getSqlSessionFactory(), player, newPassword1);
		// m_password_change=Password changed.
		msg = jibsMessages.convert("m_password_change");
		out.println(msg);

		return true;
	}
}
