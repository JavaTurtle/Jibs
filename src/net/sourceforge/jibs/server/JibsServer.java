package net.sourceforge.jibs.server;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.sourceforge.jibs.backgammon.JibsRandom;
import net.sourceforge.jibs.command.About_Command;
import net.sourceforge.jibs.command.Accept_Command;
import net.sourceforge.jibs.command.Address_Command;
import net.sourceforge.jibs.command.Away_Command;
import net.sourceforge.jibs.command.Back_Command;
import net.sourceforge.jibs.command.Board_Command;
import net.sourceforge.jibs.command.Double_Command;
import net.sourceforge.jibs.command.Exit_Command;
import net.sourceforge.jibs.command.Invite_Command;
import net.sourceforge.jibs.command.JibsCommand;
import net.sourceforge.jibs.command.Join_Command;
import net.sourceforge.jibs.command.Kibitz_Command;
import net.sourceforge.jibs.command.Leave_Command;
import net.sourceforge.jibs.command.Login_Command;
import net.sourceforge.jibs.command.Motd_Command;
import net.sourceforge.jibs.command.Move_Command;
import net.sourceforge.jibs.command.NImplemented_Command;
import net.sourceforge.jibs.command.Password_Command;
import net.sourceforge.jibs.command.Ratings_Command;
import net.sourceforge.jibs.command.Reject_Command;
import net.sourceforge.jibs.command.Resign_Command;
import net.sourceforge.jibs.command.Roll_Command;
import net.sourceforge.jibs.command.Set_Command;
import net.sourceforge.jibs.command.Shout_Command;
import net.sourceforge.jibs.command.Shutdown_Command;
import net.sourceforge.jibs.command.Tell_Command;
import net.sourceforge.jibs.command.Toggle_Command;
import net.sourceforge.jibs.command.Unwatch_Command;
import net.sourceforge.jibs.command.Version_Command;
import net.sourceforge.jibs.command.Watch_Command;
import net.sourceforge.jibs.command.Wave_Command;
import net.sourceforge.jibs.command.Whisper_Command;
import net.sourceforge.jibs.command.Who_Command;
import net.sourceforge.jibs.command.Whois_Command;
import net.sourceforge.jibs.gui.ExitAction;
import net.sourceforge.jibs.gui.InfoAction;
import net.sourceforge.jibs.gui.JibsConsole;
import net.sourceforge.jibs.gui.JibsDocument;
import net.sourceforge.jibs.gui.JibsGui;
import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.gui.JibsStatusBar;
import net.sourceforge.jibs.gui.JibsTextArea;
import net.sourceforge.jibs.gui.JibsUserPanel;
import net.sourceforge.jibs.gui.JibsUserTableModel;
import net.sourceforge.jibs.gui.JibsWindow;
import net.sourceforge.jibs.gui.ReloadAction;
import net.sourceforge.jibs.gui.RunAction;
import net.sourceforge.jibs.gui.SplashWindowJibs;
import net.sourceforge.jibs.gui.StopAction;
import net.sourceforge.jibs.util.JibsShutdown;
import net.sourceforge.jibs.util.JibsStackTrace;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

public class JibsServer {
	// -------------------------------------------------------------------------------------------------
	private static final Logger logger = Logger.getLogger(JibsServer.class);
	@SuppressWarnings("unused")
	private static final long serialVersionUID = -4324503684036776752L;
	private JFrame jibsFrame;
	private String confFileName = null;
	private Map<String, JibsCommand> allCmds;
	private JibsDocument doc = null;
	private JibsConfiguration configuration = null;
	private JibsConsole jibsConsole = null;
	private JibsMessages jibsMessages = null;
	private JibsRandom jibsRandom;
	private JibsUserPanel jibsUserPanel = null;
	private javax.swing.JInternalFrame jInternalFrame1;
	private ServerSocket serverSocket = null;
	private int portno;
	private JMenuItem startMenu;
	private JMenuItem stopMenu;
	private Thread serverThread;
	private boolean bServerRuns = false;
	private JibsGui jibsGUI = null;
	private JibsStatusBar jibsStatusbar = null;
	private InfoAction infoAction;
	private RunAction runAction;
	private StopAction stopAction;
	private ExitAction exitAction;
	private ReloadAction reloadAction;
	private JibsShutdown jibsShutdown;
	private boolean bUseSwing;

	// ------------------------------------------------------------------------------------------------------------
	private Server server = null;
	private SqlSessionFactory sqlMapper;
	private JMenuBar jibsMenuBar;

	// ---------------------------------------------------------------------------------------------------------------
	public JibsServer(String fileName) {
		confFileName = fileName;
		configuration = new JibsConfiguration(fileName);
		bUseSwing = Boolean.parseBoolean(configuration.getResource("useSwing"));
		initServerComponents();
		if (useSwing()) {
			initGUIComponents();

			Dimension dim = getJibsFrame().getToolkit().getScreenSize();
			int width = (int) (dim.getWidth());

			getJibsFrame().setSize(width, 490);
		}
	}

	public static void main(String[] args) {
		logger.info("Trying to start Jibs");

		JibsServer jibsServer = new JibsServer(args[0]);

		if (jibsServer.useSwing()) {
			jibsServer.getJibsFrame().setLocation(0, 0);
			jibsServer.getJibsFrame().setTitle(
					"jIBS_"
							+ jibsServer.getConfiguration().getResource(
									"aboutVersion"));

			URL imgURL = ClassLoader.getSystemResource("images/jibs_thumb.gif");
			ImageIcon image = new ImageIcon(imgURL);
			jibsServer.getJibsFrame().setIconImage(image.getImage());
		}

		jibsServer.startServerMenuItemActionPerformed(null);
	}

	public JibsCommand getCmd(String strCmd) {
		// get best match for strCmd to be executed, unless not ambigous
		// re -> unknown command re (conflict (ambigous) between 'resign' and
		// 'reject')
		String sCmd = strCmd.toLowerCase();
		JibsCommand cmd = null;

		Iterator<String> cmdIterator = allCmds.keySet().iterator();

		int matches = 0;

		while (cmdIterator.hasNext()) {
			String cmdString = cmdIterator.next();

			if (cmdString.startsWith(sCmd)) {
				cmd = (JibsCommand) allCmds.get(cmdString);
				matches++;
			}

			if (cmdString.equalsIgnoreCase(sCmd)) {
				cmd = (JibsCommand) allCmds.get(cmdString);
				matches = 0; // exact match, use it immediately

				break;
			}
		}

		if (matches > 1) {
			return null; // cmd is ambigous
		}

		return cmd;
	}

	// --------------------------------------------------------------------------------------------------------------------
	public JibsMessages getJibsMessages() {
		return jibsMessages;
	}

	public JibsRandom getJibsRandom() {
		return jibsRandom;
	}

	public JibsUserPanel getJibsUserPanel() {
		return jibsUserPanel;
	}

	public Server getServer() {
		return server;
	}

	public JTextArea getTextArea() {
		return jibsConsole.getTxtPane();
	}

	private void initServerComponents() {
		try {
			jibsMessages = new JibsMessages(
					configuration.getResource("MessageFile"));

			portno = Integer.parseInt(configuration.getResource("Port"));
			allCmds = new HashMap<String, JibsCommand>();

			// register all commands
			allCmds.put("about", new About_Command());
			allCmds.put("accept", new Accept_Command());
			allCmds.put("address", new Address_Command());
			allCmds.put("average", new NImplemented_Command());
			allCmds.put("away", new Away_Command());
			allCmds.put("back", new Back_Command());
			allCmds.put("beaver", new NImplemented_Command());
			allCmds.put("blind", new NImplemented_Command());
			allCmds.put("board", new Board_Command());
			allCmds.put("bye", new Exit_Command());
			allCmds.put("adios", new Exit_Command());
			allCmds.put("ciao", new Exit_Command());
			allCmds.put("end", new Exit_Command());
			allCmds.put("exit", new Exit_Command());
			allCmds.put("logout", new Exit_Command());
			allCmds.put("quit", new Exit_Command());
			allCmds.put("tschoe", new Exit_Command());
			allCmds.put("clear", new NImplemented_Command());
			allCmds.put("cls", new NImplemented_Command());
			allCmds.put("date", new NImplemented_Command());
			allCmds.put("dicetest", new NImplemented_Command());
			allCmds.put("double", new Double_Command());
			allCmds.put("erase", new NImplemented_Command());
			allCmds.put("gag", new NImplemented_Command());
			allCmds.put("help", new NImplemented_Command());
			allCmds.put("invite", new Invite_Command());
			allCmds.put("join", new Join_Command());
			allCmds.put("kibitz", new Kibitz_Command());
			allCmds.put("last", new NImplemented_Command());
			allCmds.put("leave", new Leave_Command());
			allCmds.put("login", new Login_Command());
			allCmds.put("look", new NImplemented_Command());
			allCmds.put("matrix", new NImplemented_Command());
			allCmds.put("man", new NImplemented_Command());
			allCmds.put("message", new NImplemented_Command());
			allCmds.put("motd", new Motd_Command());
			allCmds.put("move", new Move_Command());
			allCmds.put("m", new Move_Command());
			allCmds.put("off", new NImplemented_Command());
			allCmds.put("oldboard", new NImplemented_Command());
			allCmds.put("oldmoves", new NImplemented_Command());
			allCmds.put("otter", new NImplemented_Command());
			allCmds.put("panic", new NImplemented_Command());
			allCmds.put("password", new Password_Command());
			allCmds.put("pip", new NImplemented_Command());
			allCmds.put("raccoon", new NImplemented_Command());
			allCmds.put("ratings", new Ratings_Command());
			allCmds.put("rawwho", new NImplemented_Command());
			allCmds.put("redouble", new NImplemented_Command());
			allCmds.put("reject", new Reject_Command());
			allCmds.put("resign", new Resign_Command());
			allCmds.put("roll", new Roll_Command());
			allCmds.put("save", new NImplemented_Command());
			allCmds.put("say", new NImplemented_Command());
			allCmds.put("set", new Set_Command());
			allCmds.put("shout", new Shout_Command());
			allCmds.put("show", new NImplemented_Command());
			allCmds.put("shutdown", new Shutdown_Command());
			allCmds.put("sortwho", new NImplemented_Command());
			allCmds.put("stat", new NImplemented_Command());
			allCmds.put("tell", new Tell_Command());
			allCmds.put("time", new NImplemented_Command());
			allCmds.put("toggle", new Toggle_Command());
			allCmds.put("unwatch", new Unwatch_Command());
			allCmds.put("version", new Version_Command());
			allCmds.put("watch", new Watch_Command());
			allCmds.put("waitfor", new NImplemented_Command());
			allCmds.put("wave", new Wave_Command());
			allCmds.put("where", new NImplemented_Command());
			allCmds.put("whisper", new Whisper_Command());
			allCmds.put("who", new Who_Command());
			allCmds.put("whois", new Whois_Command());

			jibsRandom = new JibsRandom();
			serverSocket = new ServerSocket(portno);
			server = new Server(configuration, jibsMessages, this,
					serverSocket, portno);

		} catch (NumberFormatException e) {
			logger.warn(e);
		} catch (IOException e) {
			logger.warn(e);
		}
	}

	private void initGUIComponents() {
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e) {
			logException(e);
		} catch (InstantiationException e) {
			logException(e);
		} catch (IllegalAccessException e) {
			logException(e);
		} catch (UnsupportedLookAndFeelException e) {
			logException(e);
		}
		jibsFrame = new JFrame();
		infoAction = new InfoAction(jibsFrame, server, "Info", createImageIcon(
				"images/info.gif", ""), null, KeyStroke.getKeyStroke(
				KeyEvent.VK_I, KeyEvent.ALT_MASK));
		runAction = new RunAction(this, "Start", createImageIcon(
				"images/run.gif", ""), null, KeyStroke.getKeyStroke(
				KeyEvent.VK_S, KeyEvent.ALT_MASK));
		stopAction = new StopAction(this, "Stop", createImageIcon(
				"images/stop.gif", ""), null, KeyStroke.getKeyStroke(
				KeyEvent.VK_H, KeyEvent.ALT_MASK));
		exitAction = new ExitAction(this, "Exit", createImageIcon(
				"images/exit.gif", ""), null, KeyStroke.getKeyStroke(
				KeyEvent.VK_X, KeyEvent.ALT_MASK));
		reloadAction = new ReloadAction(this, "Reload", createImageIcon(
				"images/refresh.gif", ""), null, null);

		jibsMenuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");

		JMenuItem exitMenu = new JMenuItem("Exit");
		exitMenu.setAction(exitAction);
		fileMenu.add(exitMenu);

		JMenu serverMenu = new JMenu("Server");
		startMenu = new JMenuItem("Start server");
		startMenu.setAction(runAction);
		stopMenu = new JMenuItem("Stop");
		stopMenu.setAction(stopAction);
		serverMenu.add(startMenu);
		serverMenu.add(stopMenu);

		JMenu aboutMenu = new JMenu("About");
		JMenuItem vmMenu = new JMenuItem("Java VM...");
		vmMenu.setAction(infoAction);
		aboutMenu.add(vmMenu);
		jibsMenuBar.add(fileMenu);
		jibsMenuBar.add(serverMenu);
		jibsMenuBar.add(aboutMenu);
		jibsGUI = new JibsGui(configuration, this);

		jInternalFrame1 = new javax.swing.JInternalFrame();

		jibsConsole = new JibsConsole(this);
		jibsStatusbar = new JibsStatusBar(this);
		jibsUserPanel = new JibsUserPanel(this);
		jibsGUI.getBtnInfo().setAction(infoAction);
		jibsGUI.getBtnInfo().setText("");
		jibsGUI.getBtnExit().setAction(exitAction);
		jibsGUI.getBtnExit().setText("");
		jibsGUI.getBtnStart().setAction(runAction);
		jibsGUI.getBtnStart().setText("");
		jibsGUI.getBtnStop().setAction(stopAction);
		jibsGUI.getBtnStop().setText("");
		jibsGUI.getBtnReload().setAction(reloadAction);
		jibsGUI.getBtnReload().setText("");

		jInternalFrame1.setVisible(true);

		getJibsFrame().addWindowListener(new JibsWindow(this));

		getJibsFrame().getContentPane().add(jibsMenuBar,
				java.awt.BorderLayout.NORTH);
		getJibsFrame().getContentPane().add(jibsGUI,
				java.awt.BorderLayout.CENTER);
	}

	public boolean isExitCmd(String cmd) {
		String[] allExitCmds = { "bye", "adios", "ciao", "end", "exit",
				"logout", "quit", "tschoe" };
		boolean b = false;

		for (int i = 0; i < allExitCmds.length; i++)
			if (allExitCmds[i].equalsIgnoreCase(cmd)) {
				b = true;
			}

		return b;
	}

	public void startServerMenuItemActionPerformed(
			java.awt.event.ActionEvent evt) {
		SplashWindowJibs splashWindow = null;

		try {
			if (evt == null) {
				if (useSwing()) {
					String splashScreen = configuration
							.getResource("splashScreen");
					splashWindow = new SplashWindowJibs(this, splashScreen,
							null);
				}
			}

			if (useSwing()) {
				jibsUserPanel.readAllPlayers();
			}
			serverThread = new Thread(server);
			serverThread.start();

			if (useSwing()) {
				if (splashWindow != null) {
					splashWindow.dispose();
				}

				runAction.setEnabled(false);
				stopAction.setEnabled(true);
				bServerRuns = true;
				getJibsFrame().setVisible(true); // now display the main window
			}

			JibsTextArea.log(this, "Server started on port " + portno, true);
		} catch (Exception e) {
			String sMsg = "jIBS could not start. Check the configuration file '"
					+ confFileName + "'";
			logger.fatal(sMsg, e);

			if (splashWindow != null) {
				splashWindow.dispose();
			}

			JOptionPane.showMessageDialog(null, sMsg, "Warning",
					JOptionPane.WARNING_MESSAGE);
			System.exit(1);
		}
	}

	public void stopServerMenuItemActionPerformed(boolean bStop, boolean bExit) {
		if (runs()) {

			try {
				Server server = getServer();
				int onlinePlayer = server.getAllClients().size();

				if (onlinePlayer > 0) {
					String warnExit = configuration.getResource("warnExit");
					Boolean bWarn = Boolean.valueOf(warnExit);

					if (bWarn) {
						String msg;

						if (onlinePlayer > 1) {
							msg = "There are still "
									+ onlinePlayer
									+ " players online.\n"
									+ "When you continue these players will be disconnected without warning.\n"
									+ "They might consider this to be rude behaviour.\n"
									+ "\n" + "Do you want to continue?";
						} else {
							msg = "There is still one player online.\n"
									+ "When you continue this player will be disconnected without warning.\n"
									+ "He/She might consider this to be rude behaviour.\n"
									+ "\n" + "Do you want to continue?";
						}

						if (useSwing()) {
							int option = JOptionPane.showConfirmDialog(
									getJibsFrame(), msg, "Exit",
									JOptionPane.YES_NO_OPTION);

							if (option == JOptionPane.NO_OPTION) {
								bStop = false;
							}
						}
					}
				}
				if (bStop) {
					server.getServerSocket().close();
					server.setRuns(false);
					getServerThread().join();
					server.leaveAllClients();
					SqlSession session = sqlMapper.openSession();
					Connection connection = session.getConnection();
					try {
						Statement st = connection.createStatement();
						st.execute("SHUTDOWN");
						setSqlSessionFactory(null);
					} catch (SQLException e) {
						logger.warn(e);
					} finally {
						session.close();
					}
					bServerRuns = false;
					server.closeAllClients();
					runAction.setEnabled(true);
					stopAction.setEnabled(false);
					JibsTextArea.log(this, "Server stopped.", true);
				}
			} catch (IOException e) {
				logException(e);
			} catch (InterruptedException e) {
				logException(e);
			}
		}
		if (bStop && bExit)
			System.exit(0);
	}

	public void logException(Exception e) {
		String e1 = JibsStackTrace.getCustomStackTrace(e);
		JibsTextArea.log(this, e1, true);
		logger.warn(e);
	}

	public void setSqlSessionFactory(SqlSessionFactory factory) {
		sqlMapper = factory;
	}

	public SqlSessionFactory getSqlSessionFactory() {
		if (sqlMapper == null) {
			try {
				String sqlMapConfig = configuration
						.getResource("dbConfiguration");
				Reader reader = Resources.getResourceAsReader(sqlMapConfig);
				sqlMapper = new SqlSessionFactoryBuilder().build(reader);
			} catch (IOException e) {
				logException(e);
			}
		}

		return sqlMapper;
	}

	public Thread getServerThread() {
		return serverThread;
	}

	public void setServerThread(Thread serverThread) {
		this.serverThread = serverThread;
	}

	public boolean runs() {
		return bServerRuns;
	}

	public JibsStatusBar getStatusBar() {
		return jibsStatusbar;
	}

	public JibsGui getJibsGUI() {
		return jibsGUI;
	}

	public JibsUserTableModel getUserTableModel() {
		return jibsGUI.getUserTableModel();
	}

	public JibsShutdown getJibsShutdown() {
		return jibsShutdown;
	}

	public void setJibsShutdown(JibsShutdown jibsShutdown) {
		this.jibsShutdown = jibsShutdown;
	}

	public JibsDocument getDoc() {
		return doc;
	}

	public void setDoc(JibsDocument doc) {
		this.doc = doc;
	}

	protected static ImageIcon createImageIcon(String path, String description) {
		URL imgURL = ClassLoader.getSystemResource(path);

		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			logger.warn("Couldn't find file: " + path);

			return null;
		}
	}

	public JFrame getJibsFrame() {
		return jibsFrame;
	}

	public boolean useSwing() {
		return bUseSwing;
	}

	public JibsConfiguration getConfiguration() {
		return configuration;
	}
}
