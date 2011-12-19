package net.sourceforge.jibs.command;

import java.util.List;

import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.JibsWriter;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

/**
 * The Ratings command.
 */
public class Ratings_Command implements JibsCommand {
	private static Logger logger = Logger.getLogger(Ratings_Command.class);

	public boolean execute(Server server, Player player, String strArgs,
			String[] args) {
		JibsMessages jibsMessages = server.getJibsMessages();
		JibsWriter out = player.getOutputStream();
		String msg = null;
		SqlSessionFactory sqlSessionFactory = server.getJibsServer()
				.getSqlSessionFactory();
		SqlSession sqlSession = sqlSessionFactory.openSession(true);
		try {
			@SuppressWarnings("unchecked")
			List<Player> list = sqlSession.selectList("Player.readAllPlayer",
					null);

			int from = 1;
			int to = 20;

			if (args.length == 1) {
				out.println(" rank name                 rating    Experience");
				showRanking(list, from, to, player);

				return true;
			}

			int i = 1;
			int j = 0;
			boolean run = true;

			while (run) {
				if (args.length > i) {
					String name = args[i];

					if (name.equalsIgnoreCase("from")) {
						try {
							from = new Integer(args[i + 1]).intValue();
							i = i + 1;

							if (from < 0) {
								// m_rating_fromerror=** Please give a positive
								// number after 'from'.
								msg = jibsMessages
										.convert("m_rating_fromerror");
								out.println(msg);

								return true;
							}
						} catch (NumberFormatException e) {
							// m_rating_fromerror=** Please give a positive
							// number
							// after 'from'.
							msg = jibsMessages.convert("m_rating_fromerror");
							out.println(msg);
							logger.warn(e);
							return true;
						}
					} else {
						if (name.equalsIgnoreCase("to")) {
							try {
								to = new Integer(args[i + 1]).intValue();
								i = i + 1;

								if (to < 0) {
									// m_rating_toerror=** Please give a
									// positive
									// number after 'to'.
									msg = jibsMessages
											.convert("m_rating_toerror");
									out.println(msg);

									return true;
								}
							} catch (NumberFormatException e) {
								// m_rating_toerror=** Please give a positive
								// number
								// after 'to'.
								msg = jibsMessages.convert("m_rating_toerror");
								out.println(msg);
								logger.warn(e);
								return true;
							}
						} else {
							j++;
						}
					}

					i++;
				} else {
					run = false;
				}
			}

			String[] nameObj = new String[j];

			i = 1;
			run = true;

			int k = 0;

			while (run) {
				if (args.length > i) {
					String name = args[i];

					if (name.equalsIgnoreCase("from")) {
						from = new Integer(args[i + 1]).intValue();
						i = i + 1;
					} else {
						if (name.equalsIgnoreCase("to")) {
							to = new Integer(args[i + 1]).intValue();
							i = i + 1;
						} else {
							nameObj[k++] = name;
						}
					}

					i++;
				} else {
					run = false;
				}
			}

			if (nameObj.length > 1) {
				// m_rating_nouser=** Please use only one of the given names
				// '%0' and '%1'
				msg = jibsMessages.convert("m_rating_nouser", nameObj[0], nameObj[1]);
				out.println(msg);

				return true;
			}

			if (nameObj.length == 1) {
				// show rating of only the user
				out.println(" rank name                 rating    Experience");
				showRankingUser(list, nameObj[0], out);

				return true;
			}

			if (Math.abs(to - from) > 100) {
				// m_rating_rangebig=** range currently limited to 100.
				msg = jibsMessages.convert("m_rating_rangebig");
				out.println(msg);
			}

			if (to < from) {
				// m_rsting_rangeerror=** invalid range from %0 to %1.
				msg = jibsMessages.convert("m_rsting_rangeerror", Integer.valueOf(from),
						Integer.valueOf(to) );
				out.println(msg);

				return true;
			}

			out.println(" rank name                 rating    Experience");

			if (to < list.size()) {
				showRanking(list, from, to, player);
			}

			return true;
		} finally {
			sqlSession.close();
		}
	}

	private void showRankingUser(List<Player> list, String string,
			JibsWriter out) {
		for (int i = 0; i < list.size(); i++) {
			Player obj = (Player) list.get(i);

			if (obj.getName().equals(string)) {
				StringBuffer sBuffer = new StringBuffer();

				sBuffer.append("*");

				if (obj.getExperience() < 50) {
					sBuffer.append("-");
				} else {
					sBuffer.append(i + 1);
				}

				sBuffer.append("    ");

				String s1 = sBuffer.substring(0, 4);

				sBuffer = new StringBuffer();
				sBuffer.append(obj.getName());
				sBuffer.append("                  ");

				String s2 = sBuffer.substring(0, 20);

				sBuffer = new StringBuffer();
				sBuffer.append(obj.getRating());
				sBuffer.append("       ");

				String s3 = sBuffer.substring(0, 8);

				sBuffer = new StringBuffer();
				sBuffer.append(obj.getExperience());
				sBuffer.append("     ");

				String s4 = sBuffer.substring(0, 5) + "   ";

				out.println(" " + s1 + " " + s2 + " " + s3 + "   " + s4);
			}
		}
	}

	private void showRanking(List<Player> list, int from, int to, Player player) {
		JibsWriter out = player.getOutputStream();

		for (int i = Math.max(0, from - 1); i <= Math
				.min(list.size(), (to - 1)); i++) {
			if ((i >= 0) && (i < list.size())) {
				Player obj = (Player) list.get(i);
				StringBuffer sBuffer = new StringBuffer();

				if (obj.getExperience() < 50) {
					sBuffer.append("-");
				} else {
					sBuffer.append(i + 1);
				}

				sBuffer.append("    ");

				String s1 = sBuffer.substring(0, 4);

				sBuffer = new StringBuffer();
				sBuffer.append(obj.getName());
				sBuffer.append("                  ");

				String s2 = sBuffer.substring(0, 20);

				sBuffer = new StringBuffer();
				sBuffer.append(obj.getRating());
				sBuffer.append("       ");

				String s3 = sBuffer.substring(0, 8);

				sBuffer = new StringBuffer();
				sBuffer.append(obj.getExperience());
				sBuffer.append("     ");

				String s4 = sBuffer.substring(0, 5) + "   ";

				out.println(" " + s1 + " " + s2 + " " + s3 + "   " + s4);
			}
		}

		showRankingUser(list, player.getName(), out);
	}
}
