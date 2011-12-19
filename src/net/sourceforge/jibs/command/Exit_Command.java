package net.sourceforge.jibs.command;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.sourceforge.jibs.backgammon.JibsGame;
import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.gui.JibsTextArea;
import net.sourceforge.jibs.server.ClientWorker;
import net.sourceforge.jibs.server.JibsConfiguration;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.JibsWriter;

import org.apache.log4j.Logger;

/**
 * The Exit command.
 */
public class Exit_Command implements JibsCommand {
	private static Logger logger = Logger.getLogger(Exit_Command.class);

	public void displayLogoff(String logoutResource, JibsWriter out) {
		InputStream systemResourceAsStream = ClassLoader
				.getSystemResourceAsStream(logoutResource);
		BufferedReader inp = null;

		try {
			inp = new BufferedReader(new InputStreamReader(
					systemResourceAsStream));

			String theLine = inp.readLine();

			while (theLine != null) {
				out.println(theLine);
				theLine = inp.readLine();
			}

			inp.close();
		} catch (FileNotFoundException e) {
			logger.warn(e);
		} catch (IOException e) {
			logger.warn(e);
		} finally {
			try {
				if (inp != null) {
					inp.close();
				}
			} catch (IOException e) {
				logger.warn(e);
			}
		}
	}

	public boolean execute(Server server, Player player, String strArgs,
			String[] args) {
		JibsConfiguration configuration = server.getConfiguration();
		JibsMessages messages = server.getJibsMessages();
		String logout = configuration.getResource("logout");
		JibsWriter out = player.getOutputStream();

		if (out != null) {
			displayLogoff(logout, out);
		}

		JibsGame game = player.getGame();

		if (game != null) {
			if (!game.getBackgammonBoard().isEnded()) {
				game.save();
			}
		}
		ClientWorker cw = player.getClientWorker();
		if (cw != null) {
			cw.stopWatchThread();
			cw.disConnectPlayer(server.getJibsServer().getSqlSessionFactory(),
					player);
			try {
				cw.getSocket().close();
				player.setInputStreamClosed(true);
			} catch (IOException e) {
				logger.warn(e);
			}
		}

		// m_you_log_out=%0 logs out.
		String msg = messages.convert("m_you_log_out", player.getName());
		JibsTextArea.log(server.getJibsServer(), msg, true);

		return false;
	}
}
