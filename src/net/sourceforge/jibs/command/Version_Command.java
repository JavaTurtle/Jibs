package net.sourceforge.jibs.command;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.JibsWriter;

import org.apache.ibatis.io.Resources;
import org.apache.log4j.Logger;

public class Version_Command implements JibsCommand {
	private static Logger logger = Logger.getLogger(Version_Command.class);

	@Override
	public boolean execute(Server server, Player player, String strArgs,
			String[] args) {
		try {
			StringBuilder version = new StringBuilder();
			JibsWriter out = player.getOutputStream();
			version.append(server.getConfiguration()
					.getResource("aboutVersion"));
			version.append(" (" + System.getProperty("os.name") + ")");
			InputStream resourceAsStream = Resources
					.getResourceAsStream("net/sourceforge/jibs/util/JibsConstants.properties");
			Properties props = new Properties();
			props.load(resourceAsStream);
			version.append(" " + (String) props.get("jibsDateBuild"));
			out.println("** Version " + version);
		} catch (IOException e) {
			logger.warn(e);
		}
		return false;
	}

}
