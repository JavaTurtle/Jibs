package net.sourceforge.jibs.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.JibsWriter;

import org.apache.log4j.Logger;

/**
 * The About command.
 */
public class About_Command implements JibsCommand {
	private static Logger logger = Logger.getLogger(About_Command.class);

	private void output_about(Server server, Player player) {
		BufferedReader reader = null;

		try {
			File abaoutFile = new File(server.getConfiguration().getResource(
					"about"));
			reader = new BufferedReader(new FileReader(abaoutFile));

			JibsWriter out = player.getOutputStream();
			String line = "";

			do {
				line = reader.readLine();

				if (line != null) {
					if (line.equals("")) {
						line = " ";
					}
					out.println(line);
				}
			} while (line != null);

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
		output_about(server, player);

		return true;
	}
}
