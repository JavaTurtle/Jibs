package net.sourceforge.jibs.server;

import java.io.BufferedReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import net.sourceforge.jibs.backgammon.BackgammonBoard;
import net.sourceforge.jibs.backgammon.JibsGame;
import net.sourceforge.jibs.backgammon.JibsMatch;
import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.gui.JibsTextArea;
import net.sourceforge.jibs.util.ClipConstants;
import net.sourceforge.jibs.util.Encoder;
import net.sourceforge.jibs.util.JibsConvert;
import net.sourceforge.jibs.util.JibsNewGameData;
import net.sourceforge.jibs.util.JibsSet;
import net.sourceforge.jibs.util.JibsToggle;
import net.sourceforge.jibs.util.JibsWriter;
import net.sourceforge.jibs.util.TimeSpan;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class Player {
	// ~ Static fields/initializers
	// ---------------------------------------------
	private static Logger logger = Logger.getLogger(Player.class);

	// ~ Instance fields
	// --------------------------------------------------------
	private int clipVersion;
	private BufferedReader in;
	private boolean valid;
	private TimeSpan idle;
	private JibsGame cur_Game;
	private JibsWriter outWriter;
	private JibsConfiguration configuration;
	private JibsMessages messages;
	// important fields
	private ClientWorker clientWorker;
	private boolean canCLIP;
	private String clientProgram;
	private Timestamp last_login_date;
	private Timestamp last_logout_date;
	private String last_login_host;
	private String name;
	private boolean admin;
	private JibsQuestion question;
	private Player oppnentPlayer;
	private Player watchingPlayer;
	private String password;
	private double rating;
	private String email;
	private int experience;
	private String strLastLogin;
	private JibsToggle jibsToggles;
	private JibsSet jibsSet;
	private Server server;
	private int wavings;
	private HashMap<String, Player> watcher;
	private String awayMsg;
	private String toggle;
	private String boardstyle;
	private String linelength;
	private String pagelength;
	private String redoubles;
	private String sortwho;
	private String timezone;

	private boolean isInputStreamClosed;

	// ~ Constructors
	// -----------------------------------------------------------
	public Player() {
	}

	public Player(String clientProgram, int clipVersion, String userName,
			String passWord, double rating, int experience, String email,
			boolean admin, Timestamp last_login, String last_host) {
		this.init(clientProgram, clipVersion, userName, passWord, rating,
				experience, admin, last_login, last_host);
	}

	// ~ Methods
	// ----------------------------------------------------------------
	private void init(String clientProgram, int ClipVersion, String strUser,
			String strPassword, double rating, int experience, boolean admin,
			Timestamp last_login, String last_host) {
		this.clientProgram = clientProgram;
		this.clipVersion = ClipVersion;
		this.canCLIP = ClipVersion >= 0;

		idle = new TimeSpan(0, 0);
		this.rating = rating;
		this.last_login_date = last_login;
		this.last_login_host = last_host;
		this.name = strUser;
		this.password = strPassword;

		jibsToggles = null;
		jibsSet = null;
		wavings = 0;
		watcher = null;
		isInputStreamClosed = false;
	}

	public int getExperience() {
		return experience;
	}

	public void setGame(JibsGame game) {
		this.cur_Game = game;
	}

	public boolean getAdmin() {
		return admin;
	}

	public JibsGame getGame() {
		return cur_Game;
	}

	public void startGame(JibsServer jibsServer, JibsNewGameData jngd,
			JibsGame game, Player player1, Player player2, int length,
			int turn, JibsMatch matchVersion, int mayDouble1, int mayDouble2) {
		BackgammonBoard board = game.getBackgammonBoard();
		game.startGame(turn, board.getPlayerXdie1Value(),
				board.getPlayerXdie2Value(), board.getPlayerXPoints(),
				board.getPlayerOPoints(), mayDouble1, mayDouble2);
	}

	public boolean canCLIP() {
		return canCLIP;
	}

	public void setCLIP(boolean canCLIP) {
		this.canCLIP = canCLIP;
	}

	public void setOpponent(Player player) {
		oppnentPlayer = player;
	}

	public void setMatchLength(int nrMatches) {
	}

	public String getName() {
		return ((name != null) && (name.length() > 0)) ? name : "-";
	}

	public String getPassword() {
		return password;
	}

	public double getRating() {
		return rating;
	}

	public String getTimezone() {
		return timezone;
	}

	public long getIdle() {
		long tend = idle.getEnd();
		long tstart = idle.getStart();

		return (tend - tstart) / 1000;
	}

	public Player getOpponent() {
		return oppnentPlayer;
	}

	public Player getWatcher() {
		return watchingPlayer;
	}

	public String getClientProgram() {
		return clientProgram;
	}

	public String getUserName() {
		return name;
	}

	public Timestamp getLast_logout_date() {
		return last_logout_date;
	}

	public void setLast_logout_date(Timestamp last_logout_date) {
		this.last_logout_date = last_logout_date;
	}

	public String getEmail() {
		if (email == null) {
			return "-";
		}

		return email;
	}

	public void setIdle(TimeSpan idle) {
		this.idle = idle;
	}

	public BufferedReader getInputStream() {
		return in;
	}

	public void clip_welcome(JibsServer jibsServer) {
		JibsMessages jibsMessages = jibsServer.getJibsMessages();
		JibsWriter out = getOutputStream();
		// output connect information
		String strName = getName();
		String myDateStr = "-";

		// m_log_in=%0 logs in.
		Object[] obj = new Object[] { strName };
		String msg = jibsMessages.convert("m_log_in", obj);

		JibsTextArea.log(jibsServer, msg, true);

		// player.informPlayers(msg, null);
		Map map = jibsServer.getServer().getAllClients();
		Set set = map.entrySet();
		Iterator iter = set.iterator();

		while (iter.hasNext()) {
			Entry entry = (Entry) iter.next();
			Player curPlayer = (Player) entry.getValue();
			out.println("");
			// out.println("1 aleucht 1320159195 free-249-110.mediaworksit.net");
			if (!curPlayer.getName().equalsIgnoreCase(getName())) {
				String omsg = ClipConstants.CLIP_LOGIN + " " + getName() + " "
						+ msg;
				curPlayer.getOutputStream().println(omsg);
			}
		}

		if (last_login_date != null) {
			strLastLogin = Long.toString(last_login_date.getTime() / 1000);

			SimpleDateFormat formatter = new SimpleDateFormat(
					"E MMMMMMMMMM dd HH:mm:ss yyyy z", Locale.US);
			TimeZone tz = TimeZone.getTimeZone(getTimezone());
			formatter.setTimeZone(tz);
			myDateStr = formatter.format(last_login_date);
		} else {
			myDateStr = "'Not known'";
		}

		out.println("");
		if (canCLIP()) {
			StringBuffer bf = new StringBuffer();

			bf.append(ClipConstants.CLIP_WELCOME + " " + strName + " "
					+ strLastLogin + " " + last_login_host);
			out.println(bf.toString());
		} else {
			// m_welcome=Welcome %0.
			obj = new Object[] { strName };
			msg = jibsMessages.convert("m_welcome", obj);
			out.println(msg);

			// m_you_lastlogin=Your last login was %0 from %1.
			obj = new Object[] { myDateStr, getLast_login_host() };
			msg = jibsMessages.convert("m_you_lastlogin", obj);
			out.println(msg);
		}
	}

	public void informPlayers(String msg, Player outsider) {
		// send msg to all other player's except outsider
		Map map = server.getAllClients();
		Set set = map.entrySet();
		Iterator iter = set.iterator();

		while (iter.hasNext()) {
			Entry entry = (Entry) iter.next();
			Player curPlayer = (Player) entry.getValue();

			if (outsider == null) {
				if (!curPlayer.getName().equals(getName())) {
					if (curPlayer.checkToggle("notify")) {
						JibsWriter out = curPlayer.getOutputStream();

						out.print(msg);
					}
				}
			} else {
				if (!curPlayer.getName().equals(getName())
						&& !curPlayer.getName().equals(outsider.getName())) {
					if (curPlayer.checkToggle("notify")) {
						JibsWriter out = curPlayer.getOutputStream();

						out.print(msg);
					}
				}
			}
		}
	}

	public static String whoinfo(Player loginPlayer) {
		StringBuilder bf = new StringBuilder();
		String name = loginPlayer.getName();
		String opponent = "-";
		Player oppPlayer = loginPlayer.getOpponent();

		if (oppPlayer != null) {
			opponent = oppPlayer.getName();
		}

		String watching = "-";
		Player watchPlayer = loginPlayer.getWatcher();

		if (watchPlayer != null) {
			watching = watchPlayer.getName();
		}

		boolean ready = loginPlayer.checkToggle("ready");
		boolean away = loginPlayer.checkToggle("away");
		double rating = loginPlayer.getRating();
		int experience = loginPlayer.getExperience();
		long idleSecs = loginPlayer.getIdle();
		Date login = loginPlayer.getLast_login_date();
		String hostname = loginPlayer.getLast_login_host();
		String client = loginPlayer.getClientProgram();
		String email = loginPlayer.getEmail();

		bf.append(name + " ");
		bf.append(opponent + " ");
		bf.append(watching + " ");
		bf.append(JibsConvert.convBoolean(ready) + " ");
		bf.append(JibsConvert.convBoolean(away) + " ");
		bf.append(JibsConvert.convdouble(rating, 2) + " ");
		bf.append(experience + " ");
		bf.append(idleSecs + " ");

		if (login == null) {
			long t = new java.util.Date().getTime();
			bf.append(t + " ");
		} else {
			bf.append(login.getTime());
			bf.append(" ");
		}

		if (hostname == null) {
			bf.append("- ");
		} else {
			bf.append(hostname + " ");
		}

		bf.append(client + " ");
		bf.append(email + " ");
		return bf.toString();
	}

	public void setValid(boolean is_valid) {
		this.valid = is_valid;
	}

	// public Player getPlayer(JibsServer jibsServer, String Name) {
	// try {
	// SqlSession sqlMap = jibsServer.getSqlSession();
	// Player retPlayer = (Player) sqlMap.selectOne("Player.getPlayer",
	// Name);
	//
	// if (retPlayer != null) {
	// retPlayer.setJibsServer(jibsServer);
	// }
	//
	// return retPlayer;
	// } catch (Exception e) {
	// jibsServer.logException(e);
	// }
	//
	// return null;
	// }

	public boolean is_valid() {
		return valid;
	}

	public boolean is_valid(SqlSessionFactory sqlMapper, Player retPlayer,
			String strUser, String strPassword) {
		boolean retCode = false;
		SqlSession sqlSession = sqlMapper.openSession(true);
		try {

			if (name.equals(strUser)) {
				if (Encoder.encrypt(strUser + strPassword, "MD5").equals(
						password)) {
					retCode = true;
				}
			}
			jibsSet = new JibsSet(boardstyle, linelength, pagelength,
					redoubles, sortwho, timezone);
			return retCode;
		} finally {
			sqlSession.close();
		}

	}

	public void setName(String string) {
		this.name = string;
	}

	public void setPassword(String string) {
		this.password = string;
	}

	public void setEmail(String string) {
		this.email = string;
	}

	public void setRating(double double1) {
		this.rating = double1;
	}

	public void setExperience(int int1) {
		this.experience = int1;
	}

	public void setLastLogin(Timestamp timestamp) {
		if (timestamp != null) {
			this.last_login_date = timestamp;
		} else {
			this.last_login_date = null;
		}
	}

	public void setAdmin(int boolean1) {
		this.admin = boolean1 > 0;
	}

	public void setQuestion(JibsQuestion question) {
		this.question = question;
	}

	public void ask(JibsQuestion question) {
		this.question = question;
	}

	public JibsQuestion getQuestion() {
		return question;
	}

	public void endGame(Player opponent) {
		JibsWriter out = opponent.getOutputStream();
		StringBuilder builder = new StringBuilder();
		builder.append(ClipConstants.CLIP_WHO_INFO + " ");
		String whoinfo = Player.whoinfo(this);
		builder.append(whoinfo);
		out.println(builder.toString());
		setOpponent(null);

		opponent.setOpponent(null);
		whoinfo(opponent);
	}

	public void setClientProgram(String clientProgram) {
		this.clientProgram = clientProgram;
	}

	public JibsSet getJibsSet() {
		return jibsSet;
	}

	public void canCLIP(boolean b) {
		this.canCLIP = b;
	}

	public JibsToggle getJibsToggles() {
		return jibsToggles;
	}

	public void informToggleChange() {
		Map map = server.getAllClients();
		Set set = map.entrySet();
		Iterator iter = set.iterator();

		while (iter.hasNext()) {
			Entry entry = (Entry) iter.next();
			Player curPlayer = (Player) entry.getValue();
			JibsWriter out = curPlayer.getOutputStream();
			StringBuilder builder = new StringBuilder();
			builder.append(ClipConstants.CLIP_WHO_INFO + " ");
			String whoinfo = Player.whoinfo(curPlayer);
			builder.append(whoinfo);
			out.println(builder.toString());
			if (curPlayer.canCLIP()) {
				out.println(ClipConstants.CLIP_WHO_END + " ");
			}
		}
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public boolean checkToggle(String string) {
		return jibsToggles.get(string).booleanValue();
	}

	public int getWavings() {
		return wavings;
	}

	public void setWavings(int wavings) {
		this.wavings = wavings;
	}

	public void clip_ownInfo(JibsServer jibsServer) {
		StringBuffer bf = new StringBuffer();

		bf.append(ClipConstants.CLIP_OWN_INFO);
		bf.append(" ");
		bf.append(getName());
		bf.append(" ");
		bf.append(JibsConvert.convBoolean(checkToggle("allowpip")));
		bf.append(" ");
		bf.append(JibsConvert.convBoolean(checkToggle("autoboard")));
		bf.append(" ");
		bf.append(JibsConvert.convBoolean(checkToggle("autodouble")));
		bf.append(" ");
		bf.append(JibsConvert.convBoolean(checkToggle("automove")));
		bf.append(" ");
		bf.append(JibsConvert.convBoolean(checkToggle("away")));
		bf.append(" ");
		bf.append(JibsConvert.convBoolean(checkToggle("bell")));
		bf.append(" ");
		bf.append(JibsConvert.convBoolean(checkToggle("crawford")));
		bf.append(" ");
		bf.append(JibsConvert.convBoolean(checkToggle("double")));
		bf.append(" ");
		bf.append(experience);
		bf.append(" ");
		bf.append(JibsConvert.convBoolean(checkToggle("greedy")));
		bf.append(" ");
		bf.append(JibsConvert.convBoolean(checkToggle("moreboards")));
		bf.append(" ");
		bf.append(JibsConvert.convBoolean(checkToggle("moves")));
		bf.append(" ");
		bf.append(JibsConvert.convBoolean(checkToggle("notify")));
		bf.append(" ");
		bf.append(JibsConvert.convdouble(rating, 2));
		bf.append(" ");
		bf.append(JibsConvert.convBoolean(checkToggle("ratings")));
		bf.append(" ");
		bf.append(JibsConvert.convBoolean(checkToggle("ready")));
		bf.append(" ");
		bf.append("0"); // redoubles
		bf.append(" ");
		bf.append(JibsConvert.convBoolean(checkToggle("report")));
		bf.append(" ");
		bf.append(JibsConvert.convBoolean(checkToggle("silent")));
		bf.append(" ");
		bf.append("UTC"); // Timezone

		if (canCLIP()) {
			JibsWriter out = getOutputStream();
			out.println(bf.toString());
		}
	}

	public void addWatcher(Player player) {
		JibsWriter out = getOutputStream();
		JibsMessages jibsMessages = server.getJibsMessages();
		String msg = null;

		if (watcher == null) {
			watcher = new HashMap<String, Player>();
		}

		if (!watcher.containsKey(player.getName())) {
			watcher.put(player.getName(), player);

			// m_you_watch=You're now watching %0.
			Object[] obj = new Object[] { getName() };
			msg = jibsMessages.convert("m_you_watch", obj);
			player.getOutputStream().print(msg);
			// m_other_watch=%0 is watching you.
			obj = new Object[] { player.getName() };
			msg = jibsMessages.convert("m_other_watch", obj);
			out.println(msg);
		}
	}

	public void show2WatcherBoard(BackgammonBoard board, String name, int i,
			int j, int k, int l) {
		if (watcher != null) {
			Set set = watcher.entrySet();
			Iterator iter = set.iterator();

			while (iter.hasNext()) {
				Entry entry = (Entry) iter.next();
				Player curPlayer = (Player) entry.getValue();

				if (curPlayer != null) {
					JibsWriter out = curPlayer.getOutputStream();
					String outBoard = board.outBoard(name, board.getTurn(), i, j, k, l);
					out.println(outBoard);
				}
			}
		}
	}

	public String getAwayMsg() {
		return awayMsg;
	}

	public void setAwayMsg(String awayMsg) {
		this.awayMsg = awayMsg;
	}

	public void show2WatcherMove(String msg) {
		try {
			if (watcher != null) {
				Set set = watcher.entrySet();
				Iterator iter = set.iterator();

				while (iter.hasNext()) {
					Entry entry = (Entry) iter.next();
					Player curPlayer = (Player) entry.getValue();

					if (curPlayer != null) {
						JibsWriter out = curPlayer.getOutputStream();
						out.println(msg);
					}
				}
			}
		} catch (Exception e) {
			logger.warn(e);
		}
	}

	public void show2WatcherRoll(String msg) {
		if (watcher != null) {
			Set set = watcher.entrySet();
			Iterator iter = set.iterator();

			while (iter.hasNext()) {
				Entry entry = (Entry) iter.next();
				Player curPlayer = (Player) entry.getValue();

				if (curPlayer != null) {
					JibsWriter out = curPlayer.getOutputStream();
					out.print(msg);
				}
			}
		}
	}

	public Player getWatchingPlayer() {
		return watchingPlayer;
	}

	public void setWatchingPlayer(Player watchingPlayer) {
		this.watchingPlayer = watchingPlayer;
	}

	public void stopWatching(Player player) {
		JibsWriter out = getOutputStream();
		JibsMessages jibsMessages = server.getJibsMessages();
		String msg = null;

		if (watcher != null) {
			Set set = watcher.entrySet();
			Iterator iter = set.iterator();

			while (iter.hasNext()) {
				Entry entry = (Entry) iter.next();
				String name = (String) entry.getKey();

				if (name.equals(player.getName())) {
					iter.remove();

					// m_you_stop_watch=You stop watching %0.
					Object[] obj = new Object[] { getName() };
					msg = jibsMessages.convert("m_you_stop_watch", obj);
					out.print(msg);

					// m_other_watch_stop=%0 stops watching you.
					obj = new Object[] { player.getName() };
					msg = jibsMessages.convert("m_other_watch_stop", obj);
					out.print(msg);
				}
			}
		}
	}

	public int informWatcher(String string, Object[] obj, boolean doConvert) {
		JibsMessages jibsMessages = server.getJibsMessages();
		String msg = null;
		int heard = 0;

		if (watcher != null) {
			Set set = watcher.entrySet();
			Iterator iter = set.iterator();

			while (iter.hasNext()) {
				Entry entry = (Entry) iter.next();
				Player wPlayer = (Player) entry.getValue();
				JibsWriter wOut = wPlayer.getOutputStream();

				if (doConvert) {
					msg = jibsMessages.convert(string, obj);
				} else {
					msg = string;
				}

				wOut.println(msg);
				heard++;
			}
		}

		return heard;
	}

	public void changeToggle(String string, Boolean value) {
		jibsToggles.getToggleMap().put(string, value);
	}

	public Timestamp getLast_login_date() {
		return last_login_date;
	}

	public void setLast_login_date(Timestamp last_login_date) {
		this.last_login_date = last_login_date;
	}

	public String getLast_login_host() {
		return last_login_host;
	}

	public void setLast_login_host(String last_login_host) {
		this.last_login_host = last_login_host;
	}

	public String getBoardStyle() {
		return boardstyle;
	}

	public void setBoardStyle(String boardStyle) {
		this.boardstyle = boardStyle;
	}

	public String getLineLength() {
		return linelength;
	}

	public void setLineLength(String lineLength) {
		this.linelength = lineLength;
	}

	public String getPageLength() {
		return pagelength;
	}

	public void setPageLength(String pageLength) {
		this.pagelength = pageLength;
	}

	public String getRedoubles() {
		return redoubles;
	}

	public void setRedoubles(String redoubles) {
		this.redoubles = redoubles;
	}

	public String getSortwho() {
		return sortwho;
	}

	public void setSortwho(String sortwho) {
		this.sortwho = sortwho;
	}

	public String getToggle() {
		return toggle;
	}

	public void setToggle(String toggle) {
		this.toggle = toggle;
		jibsToggles = new JibsToggle(toggle);
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public int getClipVersion() {
		return clipVersion;
	}

	public void setClipVersion(int clipVersion) {
		this.clipVersion = clipVersion;
	}

	public boolean isAdmin() {
		return admin;
	}

	@Override
	public String toString() {
		return name + ":" + rating + ":" + experience;
	}

	public static Player load(SqlSessionFactory sqlMapper, String strUser) {
		SqlSession sqlSession = sqlMapper.openSession(true);
		try {
			Player retPlayer = (Player) sqlSession.selectOne(
					"Player.getPlayer", strUser);
			return retPlayer;
		} finally {
			sqlSession.close();
		}
	}

	public JibsWriter getOutputStream() {
		return outWriter;
	}

	public JibsConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(JibsConfiguration configuration) {
		this.configuration = configuration;
	}

	public JibsMessages getMessages() {
		return messages;
	}

	public void setMessages(JibsMessages messages) {
		this.messages = messages;
	}

	public void setOutputStream(JibsWriter outputStream) {
		this.outWriter = outputStream;
	}

	public ClientWorker getClientWorker() {
		return clientWorker;
	}

	public void setClientWorker(ClientWorker clientWorker) {
		this.clientWorker = clientWorker;
	}

	public void setStreams(BufferedReader in2, JibsWriter out) {
		this.in = in2;
		this.outWriter = out;
	}

	public void setInputStreamClosed(boolean isInputStreamClosed) {
		this.isInputStreamClosed = isInputStreamClosed;
	}

	public boolean isInputStreamClosed() {
		return isInputStreamClosed;
	}
}
