package net.sourceforge.jibs.command;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import net.sourceforge.jibs.backgammon.JibsGame;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.JibsWriter;

public class WhoisPlayer {

	public static void whois(Server server, JibsWriter out, Player whoisPlayerDB) {
		if (whoisPlayerDB != null) {
			String name = whoisPlayerDB.getName();
			String header = "Information on " + name + ":";
			out.println(header);

			SimpleDateFormat formatter = new SimpleDateFormat(
					"E MMMMMMMMMM dd HH:mm:ss yyyy z", Locale.US);
			TimeZone tz = TimeZone.getTimeZone(whoisPlayerDB.getTimezone());
			formatter.setTimeZone(tz);

			String myDateStr = formatter.format(whoisPlayerDB
					.getLast_login_date());
			out.println("  Last login:  " + myDateStr + " from "
					+ whoisPlayerDB.getLast_login_host());

			if (whoisPlayerDB.getLast_logout_date() != null) {
				myDateStr = formatter.format(whoisPlayerDB
						.getLast_logout_date());
				out.println("  Last logout: " + myDateStr);
			} else {
				out.println("  Last logout: Not known");
			}
			Player whoisPlayerOnline = server.isPlayerOnline(whoisPlayerDB);
			if (whoisPlayerOnline != null) {
				boolean ready = whoisPlayerOnline.checkToggle("ready");
				JibsGame game = whoisPlayerOnline.getGame();
				Player watcher = whoisPlayerOnline.getWatcher();
				StringBuilder builder = new StringBuilder();
				builder.append("  " + name);
				if (ready)
					builder.append(" is ready to play");
				else
					builder.append(" is not ready to play");
				if (watcher != null)
					builder.append(", watching" + watcher.getName());
				else
					builder.append(", not watching");
				if (game == null)
					builder.append(", not playing.");
				out.println(builder.toString());
				if (game != null)
					out.println(name
							+ " is playing with "
							+ game.getBackgammonBoard()
									.getOpponent(whoisPlayerOnline).getName());
				builder = new StringBuilder();
				boolean away = whoisPlayerOnline.checkToggle("away");
				if (away) {
					builder.append("  "+name+" is away:"+whoisPlayerOnline.getAwayMsg());
					out.println(builder.toString());
				}
			} else {
				out.println("  Not logged in right now.");
			}

			out.println("  Rating: " + whoisPlayerDB.getRating()
					+ " Experience: " + whoisPlayerDB.getExperience());

			if (whoisPlayerDB.getEmail().equals("-")) {
				out.println("  No email address.");
			} else {
				out.println("  Email address: " + whoisPlayerDB.getEmail());
			}
		}
	}

}
