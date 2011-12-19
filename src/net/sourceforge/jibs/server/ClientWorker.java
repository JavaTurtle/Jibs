package net.sourceforge.jibs.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.SwingUtilities;

import net.sourceforge.jibs.command.JibsCommand;
import net.sourceforge.jibs.command.Login_Command;
import net.sourceforge.jibs.command.NewUser_Command;
import net.sourceforge.jibs.command.Unknown_Command;
import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.gui.JibsTextArea;
import net.sourceforge.jibs.util.JibsTimer;
import net.sourceforge.jibs.util.JibsWriter;
import net.sourceforge.jibs.util.TimeSpan;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class ClientWorker implements Runnable {
	private static final Logger logger = Logger.getLogger(ClientWorker.class);
	private JibsConfiguration jibsConfiguration;
	private JibsMessages messages;
	private BufferedReader in;
	private long inActiveMillis = 0;
	private JibsTimer inactiveTimer;
	private JibsServer jibsServer;
	private Thread workerThread;
	private Thread jibsThread;
	private String lastCmd;
	private Date now_Cmd;
	private int once;
	private JibsWriter out;
	private Player player;
	private boolean runs;
	private Server server;
	private Socket socket;
	private Date timeStampOfLastCmd;

	// Constructor
	public ClientWorker() {
	}

	public synchronized void start() {
		if (workerThread == null) {
			workerThread = new Thread(this);
			workerThread.start();
		}
	}

	public synchronized void stop() {
		runs = false;
	}

	public void join() {
		try {
			if (workerThread != null) {
				workerThread.join();
			}
		} catch (InterruptedException e) {
			logger.warn(e);
		}
	}

	public ClientWorker(JibsConfiguration configuration,
			JibsMessages jibsMessages, Server Server, Socket client,
			BufferedReader in, JibsWriter out) {
		this.jibsConfiguration = configuration;
		this.messages = jibsMessages;
		this.server = Server;
		this.socket = client;
		this.in = in;
		this.out = out;
		jibsServer = Server.getJibsServer();
		runs = true;
		once = 0;
		inActiveMillis = Integer.parseInt(configuration
				.getResource("ActivityTimeout"));
	}

	public static Player getPlayer(JibsServer jibsServer, Player player,
			String name) {
		Server server = jibsServer.getServer();
		Map<String, Player> allClients = server.getAllClients();
		Iterator<Player> iter = allClients.values().iterator();

		while (iter.hasNext()) {
			Player possibleInvitee = (Player) iter.next();

			if (possibleInvitee.getName().equalsIgnoreCase(name)) {
				return possibleInvitee;
			}
		}

		return null;
	}

	public static Player getPlayer(Server server, String name) {
		Map<String, Player> allClients = server.getAllClients();
		Iterator<Player> iter = allClients.values().iterator();

		while (iter.hasNext()) {
			Player obj = (Player) iter.next();

			if (obj.getName().equalsIgnoreCase(name)) {
				return obj;
			}
		}

		return null;
	}

	public void connectPlayer(Player player) {
		this.player = player;

		Map<String, Player> allClients = getServer().getAllClients();

		allClients.put(player.getName(), player);

		if (jibsServer.useSwing()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					jibsServer.getStatusBar().getPlayerLabel()
							.setText("" + getServer().getAllClients().size());
				}
			});
		}
	}

	public void disConnectPlayer(SqlSessionFactory sqlSessionFactory,
			Player player) {
		this.player = player;
		player.setLast_logout_date(new Timestamp(new Date().getTime()));

		Map<String, Player> allClients = getServer().getAllClients();
		allClients.remove(player.getName());
		updateDB(sqlSessionFactory, player, "Player.updateLogout");

		if (jibsServer.useSwing()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					jibsServer.getStatusBar().getPlayerLabel()
							.setText("" + getServer().getAllClients().size());
				}
			});
		}
	}

	public boolean executeCmd(String strCmd) {
		if (strCmd == null)
			return true;
		if (player != null) {
			logger.info(player.getName() + ":" + strCmd);
		} else {
			logger.info("<Unknown player>:" + strCmd);
		}

		if (inactiveTimer == null) {
			inactiveTimer = new JibsTimer(jibsServer, this, player,
					inActiveMillis);
			jibsThread = new Thread(inactiveTimer);
			jibsThread.start();
		}

		if (inactiveTimer != null) {
			jibsThread.interrupt();

			try {
				jibsThread.join();
			} catch (InterruptedException e) {
				// jibsServer.logException(e);
				jibsThread = null;
			}

			if (inActiveMillis >= 0) {
				jibsThread = new Thread(inactiveTimer);
				jibsThread.start();
			}
		}

		String strArgs = "";
		boolean retCode = true;

		if (strCmd != null) {
			now_Cmd = new Date(new java.util.Date().getTime());

			if (timeStampOfLastCmd == null) {
				timeStampOfLastCmd = now_Cmd;
			}

			TimeSpan duration = new TimeSpan(timeStampOfLastCmd.getTime(),
					now_Cmd.getTime());
			Player player = getPlayer();

			if (player != null) {
				player.setIdle(duration);
			}

			timeStampOfLastCmd = now_Cmd;

			StringTokenizer stoken = new StringTokenizer(strCmd);
			int nrOfArgs = stoken.countTokens();
			String[] totalArgs = null;

			if (nrOfArgs > 0) {
				totalArgs = new String[nrOfArgs];
			}

			stoken = new StringTokenizer(strCmd);

			int i = 0;

			while (stoken.hasMoreTokens()) {
				totalArgs[i++] = stoken.nextToken();
			}

			// split the command and the args
			int index = -1;

			if (totalArgs != null) {
				index = strCmd.indexOf(totalArgs[0]);

				if (index >= 0) {
					strArgs = strCmd.substring(index + totalArgs[0].length());
				}

				// is there a cmd to execute the request?
				JibsCommand fc = jibsServer.getCmd(totalArgs[0]);

				if (fc != null) {
					if ((lastCmd == null) || !jibsServer.isExitCmd(lastCmd)) {
						retCode = fc
								.execute(server, player, strArgs, totalArgs);
					}

					lastCmd = strCmd;
				} else {
					JibsCommand jibscmd = new Unknown_Command();
					jibscmd.execute(server, player, strArgs, totalArgs);
				}
			}
		}

		return retCode;
	}

	public BufferedReader getInputReader() {
		return in;
	}

	public Player getPlayer() {
		return player;
	}

	public JibsWriter getPrintWriter() {
		return out;
	}

	public Server getServer() {
		return server;
	}

	public Socket getSocket() {
		return socket;
	}

	public void stopRunning() {
		runs = false;
	}

	public void run() {
		String strCmd = null;
		boolean bStopLogin = false;
		String retries = jibsConfiguration.getResource("Retries");
		int MaxRetries = Integer.parseInt(retries);
		while (runs) {
			if (!bStopLogin) {
				if (!bStopLogin && (once < MaxRetries)) {
					once++;
					server.logo(out, once);
					try {
						strCmd = in.readLine();
					} catch (IOException e) {
						logger.warn(e);
					}
					player = Login_Command.login(
							jibsServer.getSqlSessionFactory(), strCmd);
					if (player != null) {
						if ("guest".equalsIgnoreCase(player.getName())) {
							NewUser_Command newUser = new NewUser_Command(
									jibsServer, in, out);
							player = newUser.createNewUser(jibsServer, server,
									socket, "", null, true);
							bStopLogin = true;
							runs = false;
						} else {
							player.setServer(server);
							player.setClientWorker(this);
							player.setStreams(in, out);
							if ("guest".equalsIgnoreCase(player.getName())) {
								bStopLogin = true;
								runs = false;
								executeCmd("bye");
							}
							connectPlayer(player);
							bStopLogin = true;
							player.clip_welcome(jibsServer);
							player.clip_ownInfo(jibsServer);
							player.setValid(true);
							player.setLast_login_date(new Timestamp(new Date()
									.getTime()));
							Socket client = getSocket();
							InetAddress iadr = client.getInetAddress();
							player.setLast_login_host(iadr.getHostName());
							updateLogin(jibsServer.getSqlSessionFactory(),
									player);
							executeCmd("motd");
							executeCmd("who");
							bStopLogin = true;
						}
					} else {
						out.println("Login incorrect");
					}
				} else {
					runs = false;
					bStopLogin = true;
					// m_too_many_errors=Too many errors for "%0" trying to
					// log in:Connection closed

					String msg = jibsServer.getJibsMessages().convert(
							"m_too_many_errors", "Unknown User");

					JibsTextArea.log(jibsServer, msg, true);
					socketClose();
				}
			} else {
				try {
					if (!player.isInputStreamClosed()) {
						strCmd = in.readLine();
						if (strCmd != null)
							executeCmd(strCmd);
						else {
							if (player != null) {
								String msg = server.getJibsMessages().convert(
										"m_drop_connection",
										player.getName() );
								JibsTextArea.log(jibsServer, msg, true);
								disConnectPlayer(
										jibsServer.getSqlSessionFactory(),
										player);
								stopWatchThread();
								try {
									getSocket().close();
								} catch (IOException e) {
									jibsServer.logException(e);
								}
							}
							runs = false;
						}
					} else
						runs = false;
				} catch (IOException e) {
					logger.warn(e);
				}
			}
		} // while
		try {
			socket.close();
		} catch (IOException e) {
			logger.warn(e);
		}
	}

	private void updateLogin(SqlSessionFactory sqlSessionFactory, Player player) {
		updateDB(sqlSessionFactory, player, "Player.updateLogin");
	}

	private static boolean updateDB(SqlSessionFactory sqlSessionFactory,
			Player player, String cmd) {
		SqlSession sqlSession = sqlSessionFactory.openSession(true);
		int update = sqlSession.update(cmd, player);
		sqlSession.close();
		return update >= 1;

	}

	private void socketClose() {
		try {
			getSocket().close();
		} catch (IOException e) {
			logger.warn(e);
		}
	}

	public void stopWatchThread() {
		if (jibsThread != null)
			jibsThread.interrupt();

		try {
			if (jibsThread != null)
				jibsThread.join();
		} catch (InterruptedException e) {
			jibsThread = null;
		}
	}

	public JibsConfiguration getConfiguration() {
		return jibsConfiguration;
	}

	public JibsMessages getMessages() {
		return messages;
	}

	public void setMessages(JibsMessages messages) {
		this.messages = messages;
	}

	public void setConfiguration(JibsConfiguration configuration) {
		this.jibsConfiguration = configuration;
	}

	public static boolean changePassword(SqlSessionFactory sqlSessionFactory,
			Player player, String newPassword1) {
		player.setPassword(newPassword1);
		return updateDB(sqlSessionFactory, player, "Player.updatePassword");
	}

	public static void changeRating(SqlSessionFactory sqlSessionFactory,
			Player player, double d, int exp) {
		player.setRating(d);
		player.setExperience(exp);
		updateDB(sqlSessionFactory, player, "Player.updateRating");
	}

	public static boolean changeAddress(SqlSessionFactory sqlSessionFactory,
			Player player, String newadr) {
		player.setEmail(newadr);
		return updateDB(sqlSessionFactory, player, "Player.updateMail");
	}

}
