package net.sourceforge.jibs.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import net.sourceforge.jibs.command.Exit_Command;
import net.sourceforge.jibs.command.Leave_Command;
import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.util.JibsShutdown;
import net.sourceforge.jibs.util.JibsWriter;

import org.apache.log4j.Logger;

public class Server implements Runnable {
	private static final Logger logger = Logger.getLogger(Server.class);
	private ServerSocket serverSocket;
	private JibsMessages jibsMessages;
	private JibsConfiguration jibsConfiguration;
	private Map<String, Player> allClients;
	private JibsServer jibsServer;
	private boolean runs;

	public Server(JibsConfiguration jibsConfiguration, JibsMessages jibsMessages) {
		init(jibsConfiguration, jibsMessages, null, null);
	}

	public Server(JibsConfiguration jibsConfiguration,
			JibsMessages jibsMessages, JibsServer server,
			ServerSocket listener, int portNo) {
		init(jibsConfiguration, jibsMessages, server, listener);
	}

	private void init(JibsConfiguration jibsConfiguration,
			JibsMessages jibsMessages, JibsServer server,
			ServerSocket serverSocket) {
		this.jibsConfiguration = jibsConfiguration;
		this.jibsMessages = jibsMessages;
		this.jibsServer = server;
		this.serverSocket = serverSocket;
		allClients = new ConcurrentHashMap<String, Player>();
		runs = true;
	}

	public JibsServer getJibsServer() {
		return jibsServer;
	}

	public Map<String, Player> getAllClients() {
		return allClients;
	}

	public boolean runs() {
		return runs;
	}

	public void logo(JibsWriter out, int nrTries) {
		BufferedReader inp = null;

		try {
			if (nrTries == 1) {
				String sName = jibsConfiguration.getResource("login");
				InputStream stream = ClassLoader
						.getSystemResourceAsStream(sName);
				inp = new BufferedReader(new InputStreamReader(stream));

				String theLine = inp.readLine();

				while (theLine != null) {
					out.println(theLine);
					theLine = inp.readLine();
				}

				// inp.close();

				SimpleDateFormat formatter = new SimpleDateFormat(
						"MMMMMMMMMM dd HH:mm:ss yyyy z", Locale.US);
				Date now = new Date();
				String myLoginStr1 = formatter.format(now);

				DateFormat fmt = new SimpleDateFormat(
						"MMMMMMMMMM dd HH:mm:ss yyyy z", Locale.US);

				fmt.setTimeZone(TimeZone.getTimeZone("UTC"));

				String myLoginStr2 = fmt.format(now);

				out.println(myLoginStr1 + "        [" + myLoginStr2 + "]");
			}

			out.print("login: ");
			out.flush();
		} catch (FileNotFoundException e) {
			jibsServer.logException(e);
		} catch (IOException e) {
			jibsServer.logException(e);
		}
	}

	public void run() {
		while (runs) {
			Socket client;
			BufferedReader in;
			JibsWriter out;
			boolean bLogin = true;

			try {
				client = serverSocket.accept();
				client.setKeepAlive(true);
				in = new BufferedReader(new InputStreamReader(
						client.getInputStream()));
				out = new JibsWriter(client.getOutputStream());

				JibsShutdown jibsShutdown = jibsServer.getJibsShutdown();

				if (jibsShutdown != null) {
					Date dt = jibsShutdown.getShutdownDate();
					Date now = new Date();
					long diff = Math.abs(now.getTime() - dt.getTime());

					if (diff < (10 * 60 * 1000)) {
						out.println("jIBS will shutdown. No login allowed.");
						client.close();
						bLogin = false;
					}
				}

				if (bLogin) {
					ClientWorker w = new ClientWorker(jibsConfiguration,
							jibsMessages, this, client, in, out);
					w.start();
				}
			} catch (IOException e) {
				boolean doLog = e.getMessage().equalsIgnoreCase(
						"Socket is closed");
				doLog |= e.getMessage().equalsIgnoreCase("socket closed");

				if (!doLog) {
					jibsServer.logException(e);
				}
			}
		}
	}

	public Player getPlayer(String string) {
		Map<String, Player> map = getAllClients();
		Set<Entry<String, Player>> set = map.entrySet();
		Iterator<Entry<String, Player>> iter = set.iterator();

		while (iter.hasNext()) {
			Entry<String, Player> entry = iter.next();
			Player curPlayer = (Player) entry.getValue();

			if (curPlayer.getName().equals(string)) {
				return curPlayer;
			}
		}

		return null;
	}

	public Collection<Player> getAwayPlayer() {
		Map<String, Player> map = getAllClients();
		Set<Entry<String, Player>> set = map.entrySet();
		Iterator<Entry<String, Player>> iter = set.iterator();
		Collection<Player> list = new ArrayList<Player>();

		while (iter.hasNext()) {
			Entry<String, Player> entry = iter.next();
			Player curPlayer = (Player) entry.getValue();

			if (curPlayer.checkToggle("away")) {
				list.add(curPlayer);
			}
		}

		if (list.size() > 0) {
			return list;
		} else {
			return null;
		}
	}

	public static Logger getLogger() {
		return logger;
	}

	public JibsMessages getJibsMessages() {
		return jibsMessages;
	}

	public void setJibsMessages(JibsMessages jibsMessages) {
		this.jibsMessages = jibsMessages;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public boolean isRuns() {
		return runs;
	}

	public void setRuns(boolean runs) {
		this.runs = runs;
	}

	public void setAllClients(HashMap<String, Player> allClients) {
		this.allClients = allClients;
	}

	public void setJibsServer(JibsServer jibsServer) {
		this.jibsServer = jibsServer;
	}

	public void leaveAllClients() {
		Map<String, Player> omap = getAllClients();
		Map<String, Player> map = Collections.synchronizedMap(omap);

		synchronized (map) {
			Set<Entry<String, Player>> set = map.entrySet();
			Iterator<Entry<String, Player>> iter = set.iterator();
			Exit_Command exitCommand = new Exit_Command();
			Leave_Command leaveCommand = new Leave_Command();

			while (iter.hasNext()) {
				Entry<String, Player> entry = iter.next();
				Player curPlayer = (Player) entry.getValue();
				leaveCommand.saveGamePanic(this, curPlayer.getGame());
				exitCommand.displayLogoff(
						jibsConfiguration.getResource("logout"),
						curPlayer.getOutputStream());

				ClientWorker cw = curPlayer.getClientWorker();

				if (cw != null) {
					cw.stopWatchThread();
				}
			}
		}
	}

	public Player isPlayerOnline(Player player) {
		Map<String, Player> map = getAllClients();
		Set<Entry<String, Player>> set = map.entrySet();
		Iterator<Entry<String, Player>> iter = set.iterator();

		while (iter.hasNext()) {
			Entry<String, Player> entry = iter.next();
			Player curPlayer = (Player) entry.getValue();

			if (curPlayer.getName().equals(player.getName())) {
				return curPlayer;
			}
		}

		return null;
	}

	public JibsConfiguration getConfiguration() {
		return jibsConfiguration;
	}

	public void setConfiguration(JibsConfiguration jibsConfiguration) {
		this.jibsConfiguration = jibsConfiguration;
	}

	public void closeAllClients() {
		Map<String, Player> map = getAllClients();

		synchronized (map) {
			Set<Entry<String, Player>> set = map.entrySet();
			Iterator<Entry<String, Player>> iter = set.iterator();

			while (iter.hasNext()) {
				Entry<String, Player> entry = iter.next();
				Player curPlayer = (Player) entry.getValue();
				ClientWorker clientWorker = curPlayer.getClientWorker();
				if (clientWorker != null) {
					clientWorker.stop();
					clientWorker.disConnectPlayer(
							jibsServer.getSqlSessionFactory(), curPlayer);
					Socket socket = clientWorker.getSocket();
					try {
						socket.getInputStream().close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					clientWorker.join();
				}
			}
		}
	}

}
