package net.sourceforge.jibs.command;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.ClipConstants;
import net.sourceforge.jibs.util.JibsWriter;

import org.apache.log4j.Logger;

/**
 * The Motd command.
 */
public class Motd_Command implements JibsCommand {
	private static Logger logger = Logger.getLogger(Motd_Command.class);

	private void output_motd(Server server, Player player) {
		BufferedReader reader = null;

		try {
			String resource = server.getConfiguration().getResource("motd");
			InputStream systemResourceAsStream = ClassLoader
					.getSystemResourceAsStream(resource);
			reader = new BufferedReader(new InputStreamReader(
					systemResourceAsStream));

			JibsWriter out = player.getOutputStream();
			String line = "";

			if (player.canCLIP()) {
				out.println(ClipConstants.CLIP_MOTD_BEGIN);
			}

			do {
				line = reader.readLine();

				if (line != null) {
					if (line.equals("")) {
						line = " ";
					}

					out.println(line);
				}
			} while (line != null);

			if (player.canCLIP()) {
				out.println(ClipConstants.CLIP_MOTD_END);
			}

			reader.close();
			out.flush();
		} catch (FileNotFoundException e) {
			logger.warn(e);
		} catch (IOException e) {
			logger.warn(e);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				logger.warn(e);
			}
		}
	}

	public boolean execute(Server server, Player player, String strArgs,
			String[] args) {
		output_motd(server, player);

		return true;
	}
}
