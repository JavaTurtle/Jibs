package net.sourceforge.jibs.backgammon;

import net.sourceforge.jibs.server.JibsServer;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.util.JibsSet;
import net.sourceforge.jibs.util.JibsWriter;

import org.apache.log4j.Logger;

/**
 *
 *
 */
public class BackgammonBoard {
	private static Logger logger = Logger.getLogger(BackgammonBoard.class);
	char[] value = new char[] { 13, 10 };
	public String newline = new String(value);
	private JibsServer jibsServer;
	private Player playerX;
	private Player playerO;
	private int playerXdie1Value;
	private int playerXdie2Value;
	private int playerOdie1Value;
	private int playerOdie2Value;
	private int bar;
	private int[] board;
	private int canMove;
	private int color;
	private int cubeNumber;
	private int didCrawford;
	private int direction;
	private int forcedMove;
	private int home;
	private int matchlength;
	private int mayDouble1;
	private int mayDouble2;
	private int onBar1;
	private int onBar2;
	private int onHome1;
	private int onHome2;
	private int player1Got;
	private String player1Name;
	private int player2Got;
	private String player2Name;
	private int redoubles;
	private int turn;
	private int wasDoubled;
	private JibsMatch jibsMatchVersion;
	private Player joinPlayer1;
	private Player joinPlayer2;
	private boolean crawFordGame;
	private boolean isEnded;

	/**
     *
     */
	public BackgammonBoard(final BackgammonBoard b2) {
		this.init(b2.jibsServer, b2.player1Name, b2.player2Name,
				b2.matchlength, b2.getMatchVersion());
		this.jibsServer = b2.jibsServer;
		this.playerX = b2.playerX;
		this.playerO = b2.playerO;
		this.playerXdie1Value = b2.playerXdie1Value;
		this.playerXdie2Value = b2.playerXdie2Value;
		this.playerOdie1Value = b2.playerOdie1Value;
		this.playerOdie2Value = b2.playerOdie2Value;
		this.jibsServer = b2.jibsServer;
		this.bar = b2.bar;
		this.canMove = b2.canMove;
		this.color = b2.color;
		this.cubeNumber = b2.cubeNumber;
		this.didCrawford = b2.didCrawford;
		this.direction = b2.direction;
		this.forcedMove = b2.forcedMove;
		this.home = b2.home;
		this.matchlength = b2.matchlength;
		this.mayDouble1 = b2.mayDouble1;
		this.mayDouble2 = b2.mayDouble2;
		this.onBar1 = b2.onBar1;
		this.onBar2 = b2.onBar2;
		this.onHome1 = b2.onHome1;
		this.onHome2 = b2.onHome2;
		this.player1Got = b2.player1Got;
		this.player1Name = b2.player1Name;
		this.player2Got = b2.player2Got;
		this.player2Name = b2.player2Name;
		this.redoubles = b2.redoubles;
		this.turn = b2.turn;
		this.wasDoubled = b2.wasDoubled;
		this.jibsMatchVersion = b2.jibsMatchVersion;
		this.joinPlayer1 = b2.joinPlayer1;
		this.joinPlayer2 = b2.joinPlayer2;
		this.crawFordGame = b2.crawFordGame;

		for (int i = 0; i < 26; i++) {
			this.board[i] = b2.board[i];
		}

		this.isEnded = b2.isEnded;
	}

	/**
	 * 
	 * @param jibsServer
	 * @param player1Name
	 * @param player2Name
	 * @param matchlength
	 * @param matchVersion
	 */
	public BackgammonBoard(JibsServer jibsServer, String player1Name,
			String player2Name, int matchlength, JibsMatch matchVersion) {
		this.init(jibsServer, player1Name, player2Name, matchlength,
				matchVersion);
	}

	/**
     *
     */
	public void init(JibsServer jibsServer, String player1Name,
			String player2Name, int matchlength, JibsMatch matchVersion) {
		this.player1Name = player1Name;
		this.player2Name = player2Name;
		this.matchlength = matchlength;
		this.player1Name = player1Name;
		this.player2Name = player2Name;
		this.jibsMatchVersion = matchVersion;

		playerXdie1Value = 0;
		playerXdie2Value = 0;
		playerOdie1Value = 0;
		playerOdie2Value = 0;

		cubeNumber = 1;
		player1Got = 0;
		player2Got = 0;
		board = new int[26];

		// standard backgammon board
		for (int i = 0; i < board.length; i++) {
			board[i] = 0;
		}

		board[24] = 2;
		board[13] = 5;
		board[8] = 3;
		board[6] = 5;
		board[1] = -2;
		board[12] = -5;
		board[17] = -3;
		board[19] = -5;

		forcedMove = 0;
		didCrawford = 0;
		redoubles = 0;
		mayDouble1 = 1;
		mayDouble2 = 1;
		wasDoubled = 0;
		color = -1;
		direction = 1;
		home = 25;
		bar = 0;

		// board[0]=2; // Bar 'X'
		int totalPlayer1 = board[0];
		int totalPlayer2 = board[25];

		for (int i = 1; i <= 24; i++) {
			if (board[i] > 0) {
				totalPlayer2 += Math.abs(board[i]);
			}

			if (board[i] < 0) {
				totalPlayer1 += Math.abs(board[i]);
			}
		}

		onHome1 = 15 - totalPlayer1;
		onHome2 = 15 - totalPlayer2;

		onBar1 = board[0];
		onBar2 = board[25];
		this.isEnded = false;
	}

	/**
	 * 
	 * @return
	 */
	public static Logger getLogger() {
		return logger;
	}

	/**
	 * 
	 * @param logger
	 */
	public static void setLogger(Logger logger) {
		BackgammonBoard.logger = logger;
	}

	/**
	 * 
	 * @return
	 */
	public int getBar() {
		return bar;
	}

	/**
	 * 
	 * @return
	 */
	public int[] getBoard() {
		return board;
	}

	/**
	 * 
	 * @return
	 */
	public int getCanMove() {
		return canMove;
	}

	/**
	 * 
	 * @return
	 */
	public int getColor() {
		return color;
	}

	/**
	 * 
	 * @return
	 */
	public int getCubeNumber() {
		return cubeNumber;
	}

	/**
	 * 
	 * @return
	 */
	public int getDidCrawford() {
		return didCrawford;
	}

	/**
	 * 
	 * @return
	 */
	public int getDirection() {
		return direction;
	}

	/**
     *
     */
	public int getForcedMove() {
		return forcedMove;
	}

	/**
	 * 
	 * @return
	 */
	public int getHome() {
		return home;
	}

	/**
	 * 
	 * @return
	 */
	public int getMatchlength() {
		return matchlength;
	}

	/**
	 * 
	 * @return
	 */
	public int getMayDouble1() {
		return mayDouble1;
	}

	/**
	 * 
	 * @return
	 */
	public int getMayDouble2() {
		return mayDouble2;
	}

	/**
	 * 
	 * @return
	 */
	public int getOnBar1() {
		return onBar1;
	}

	/**
	 * 
	 * @return
	 */
	public int getOnBar2() {
		return onBar2;
	}

	/**
	 * 
	 * @return
	 */
	public int getOnHome1() {
		return onHome1;
	}

	/**
	 * 
	 * @return
	 */
	public int getOnHome2() {
		return onHome2;
	}

	/**
	 * 
	 * @param game
	 * @return
	 */
	public String getOutputBoard(JibsGame game) {
		StringBuffer strBoard = new StringBuffer();

		strBoard.append("board:");
		strBoard.append("You:");
		strBoard.append(player2Name);
		strBoard.append(":");
		strBoard.append(matchlength);
		strBoard.append(":");
		strBoard.append(player1Got);
		strBoard.append(":");
		strBoard.append(player2Got);

		for (int i = 0; i < 26; i++) {
			strBoard.append(":");
			strBoard.append(board[i]);
		}

		strBoard.append(":");
		strBoard.append(turn);
		strBoard.append(":");
		strBoard.append(playerXdie1Value);
		strBoard.append(":");
		strBoard.append(playerXdie2Value);
		strBoard.append(":");
		strBoard.append(playerOdie1Value);
		strBoard.append(":");
		strBoard.append(playerOdie2Value);
		strBoard.append(":");
		strBoard.append(cubeNumber);
		strBoard.append(":");
		strBoard.append(mayDouble1);
		strBoard.append(":");
		strBoard.append(mayDouble2);
		strBoard.append(":");
		strBoard.append(wasDoubled);
		strBoard.append(":");
		strBoard.append(color);
		strBoard.append(":");
		strBoard.append(direction);
		strBoard.append(":");
		strBoard.append(home);
		strBoard.append(":");
		strBoard.append(bar);
		strBoard.append(":");
		strBoard.append(onHome1);
		strBoard.append(":");
		strBoard.append(onHome2);
		strBoard.append(":");
		strBoard.append(onBar1);
		strBoard.append(":");
		strBoard.append(onBar2);
		strBoard.append(":");
		strBoard.append(canMove);
		strBoard.append(":");
		strBoard.append(forcedMove);
		strBoard.append(":");
		strBoard.append(didCrawford);
		strBoard.append(":");
		strBoard.append(redoubles);

		return strBoard.toString();
	}

	/**
	 * 
	 * @return
	 */
	public int getPlayer1Got() {
		return player1Got;
	}

	/**
	 * 
	 * @return
	 */
	public String getPlayer1Name() {
		return player1Name;
	}

	/**
	 * 
	 * @return
	 */
	public int getPlayer2Got() {
		return player2Got;
	}

	/**
	 * 
	 * @return
	 */
	public String getPlayer2Name() {
		return player2Name;
	}

	/**
	 * 
	 * @return
	 */
	public int getRedoubles() {
		return redoubles;
	}

	/**
	 * 
	 * @return
	 */
	public int getTurn() {
		return turn;
	}

	/**
	 * 
	 * @return
	 */
	public int getWasDoubled() {
		return wasDoubled;
	}

	/**
	 * 
	 * @param out2
	 * @param i
	 * @param dice1
	 * @param dice2
	 * @param j
	 * @param k
	 */
	public void outBoard(JibsWriter out2, String name, int turn, int dice1,
			int dice2, int j, int k) {
		try {
			Player player = getPlayerX();
			JibsSet jibsSet = player.getJibsSet();
			String strBoardStyle = (String) jibsSet.get("boardstyle");
			int boardstyle = new Integer(strBoardStyle).intValue();

			switch (boardstyle) {
			case 1:
			default:
			case 2:
				String x = outBoard_2(turn, dice1, dice2, j, k);
				out2.print(x);
				break;

			case 3:
				x = outBoard_3(name, turn, dice1, dice2, j, k);
				out2.print(x);
				break;
			}
		} catch (NumberFormatException e) {
			logger.warn(e);
		}
	}

	/**
	 * 
	 * @param out1
	 * @param turn
	 * @param dice11
	 * @param dice12
	 * @param dice21
	 * @param dice22
	 */
	public String outBoard_2(int turn, int dice11, int dice12, int dice21,
			int dice22) {
		int lines;
		int i;
		StringBuffer bf = new StringBuffer();

		bf.append(newline);
		bf.append("+-1--2--3--4--5--6-------7--8--9-10-11-12-+");
		bf.append(" o:");
		bf.append(player2Name);
		bf.append(" - score: ");
		bf.append(player2Got);

		for (lines = 1; lines <= 5; lines++) {
			bf.append(newline);
			bf.append("|");

			for (i = 1; i <= 6; i++) {
				int which = board[i];
				String symbol = "   ";

				if (which < 0) {
					symbol = " x ";
				} else {
					if (which > 0) {
						symbol = " o ";
					}
				}

				if (Math.abs(which) >= lines) {
					bf.append(symbol);
				} else {
					symbol = "   ";
					bf.append(symbol);
				}
			}

			bf.append("|   |");

			for (i = 7; i <= 12; i++) {
				int which = board[i];
				String symbol = "   ";

				if (which < 0) {
					symbol = " x ";
				} else {
					if (which > 0) {
						symbol = " o ";
					}
				}

				if (Math.abs(which) >= lines) {
					bf.append(symbol);
				} else {
					symbol = "   ";
					bf.append(symbol);
				}
			}

			bf.append("|");
		}

		bf.append(newline);
		bf.append("|                  |BAR|                  | v ");

		if (getMatchVersion().getVersion() == JibsMatch.nPointMatch) {
			bf.append(matchlength);
			bf.append("-point match");
		} else {
			bf.append("unlimited match");
		}

		for (lines = 5; lines >= 1; lines--) {
			bf.append("\r\n|");

			for (i = 24; i >= 19; i--) {
				int which = board[i];
				String symbol = "   ";

				if (which < 0) {
					symbol = " x ";
				} else {
					if (which > 0) {
						symbol = " o ";
					}
				}

				if (Math.abs(which) >= lines) {
					bf.append(symbol);
				} else {
					symbol = "   ";
					bf.append(symbol);
				}
			}

			bf.append("|   |");

			for (i = 18; i >= 13; i--) {
				int which = board[i];
				String symbol = "   ";

				if (which < 0) {
					symbol = " x ";
				} else {
					if (which > 0) {
						symbol = " o ";
					}
				}

				if (Math.abs(which) >= lines) {
					bf.append(symbol);
				} else {
					symbol = "   ";
					bf.append(symbol);
				}
			}

			bf.append("|");
		}

		bf.append(newline);
		bf.append("+24-23-22-21-20-19------18-17-16-15-14-13-+");
		bf.append(" x:");
		bf.append(player1Name);
		bf.append(" - score: ");
		bf.append(player1Got);
		bf.append(newline);
		bf.append("BAR: O:");
		bf.append(onBar2);
		bf.append(" X:");
		bf.append(onBar1);
		bf.append("   OFF: O:");
		bf.append(onHome2);
		bf.append(" X:");
		bf.append(onHome1);
		bf.append("   Cube: ");
		bf.append(cubeNumber);

		if (dice11 > 0) {
			bf.append("   You rolled " + dice11 + " and " + dice12 + ".");
		}

		return bf.toString();
	}

	/**
	 * 
	 * @param out1
	 * @param turn
	 * @param dice11
	 * @param dice12
	 * @param dice21
	 * @param dice22
	 * @return
	 */
	public String outBoard_3(String name, int turn, int dice11, int dice12,
			int dice21, int dice22) {
		StringBuffer strBoard = new StringBuffer();

		strBoard.append("board:");
		strBoard.append(name);
		strBoard.append(":");
		strBoard.append(getPlayer2Name());
		strBoard.append(":");

		if (getMatchVersion().getVersion() == JibsMatch.nPointMatch) {
			strBoard.append(matchlength);
		} else {
			strBoard.append("9999");
		}

		strBoard.append(":");
		strBoard.append(player1Got);
		strBoard.append(":");
		strBoard.append(player2Got);

		for (int i = 0; i < 26; i++) {
			strBoard.append(":");
			strBoard.append(board[i]);
		}

		strBoard.append(":");
		strBoard.append(turn);
		strBoard.append(":");
		strBoard.append(dice11);
		strBoard.append(":");
		strBoard.append(dice12);
		strBoard.append(":");
		strBoard.append(dice21);
		strBoard.append(":");
		strBoard.append(dice22);
		strBoard.append(":");
		strBoard.append(cubeNumber);
		strBoard.append(":");
		strBoard.append(mayDouble1);
		strBoard.append(":");
		strBoard.append(mayDouble2);
		strBoard.append(":");
		strBoard.append(wasDoubled);
		strBoard.append(":");
		strBoard.append(color);
		strBoard.append(":");
		strBoard.append(direction);
		strBoard.append(":");
		strBoard.append(home);
		strBoard.append(":");
		strBoard.append(bar);
		strBoard.append(":");
		strBoard.append(onHome1);
		strBoard.append(":");
		strBoard.append(onHome2);
		strBoard.append(":");
		strBoard.append(onBar1);
		strBoard.append(":");
		strBoard.append(onBar2);
		strBoard.append(":");
		strBoard.append(canMove);
		strBoard.append(":");
		strBoard.append(forcedMove);
		strBoard.append(":");
		strBoard.append(didCrawford);
		strBoard.append(":");
		strBoard.append(redoubles);
		logger.debug(strBoard.toString());
		return strBoard.toString();
	}

	/**
	 * 
	 * @param mv
	 * @return
	 */
	public BackgammonBoard placeMoveO(Move mv) {
		BackgammonBoard helpBoard = new BackgammonBoard(this);
		int source = mv.source;
		int destination = mv.destination;

		if (source == 25) { // moving from bar
			helpBoard.board[0]--;
			helpBoard.onBar2--;
		} else {
			helpBoard.board[source]--;
		}

		if (destination <= 0) {
			// bearing off
			helpBoard.onHome2++;
		} else {
			helpBoard.board[destination]++;

			if (helpBoard.board[destination] == 0) {
				helpBoard.board[destination]++;
				helpBoard.board[0]++;
				helpBoard.onBar1++;
			}

			if (destination <= 0) {
				helpBoard.onHome2++;
			}
		}

		return helpBoard;
	}

	/**
	 * Perform a move on this board.
	 * 
	 * @param mv
	 *            a half-move
	 * 
	 * @return A modified backgammon board where the move has been placed on.
	 */
	public BackgammonBoard placeMoveX(Move mv) {
		BackgammonBoard helpBoard = new BackgammonBoard(this);
		int source = mv.source;
		int destination = mv.destination;

		if (source == bar) { // moving from bar
			helpBoard.board[0]--;
			helpBoard.onBar1--;
		} else {
			helpBoard.board[source]++;
		}

		if (destination <= 0) {
			// bearing off
			helpBoard.onHome1++;
		} else {
			if ((destination >= 1) && (destination <= 24)) {
				helpBoard.board[destination]--;

				if (helpBoard.board[destination] == 0) {
					helpBoard.board[destination]--;
					helpBoard.board[25]++;
					helpBoard.onBar2++;
				}
			}

			if (destination >= 25) {
				helpBoard.onHome1++;
			}
		}

		return helpBoard;
	}

	/**
	 * 
	 * @param bar
	 */
	public void setBar(int bar) {
		this.bar = bar;
	}

	/**
	 * 
	 * @param board
	 */
	public void setBoard(int[] board) {
		this.board = board;
	}

	/**
	 * 
	 * @param canMove
	 */
	public void setCanMove(int canMove) {
		this.canMove = canMove;
	}

	/**
	 * 
	 * @param color
	 */
	public void setColor(int color) {
		this.color = color;
	}

	/**
	 * 
	 * @param cubeNumber
	 */
	public void setCubeNumber(int cubeNumber) {
		this.cubeNumber = cubeNumber;
	}

	/**
	 * 
	 * @param didCrawford
	 */
	public void setDidCrawford(int didCrawford) {
		this.didCrawford = didCrawford;
	}

	/**
	 * 
	 * @param direction
	 */
	public void setDirection(int direction) {
		this.direction = direction;
	}

	/**
	 * 
	 * @param forcedMove
	 */
	public void setForcedMove(int forcedMove) {
		this.forcedMove = forcedMove;
	}

	/**
	 * 
	 * @param home
	 */
	public void setHome(int home) {
		this.home = home;
	}

	/**
	 * 
	 * @param matchlength
	 */
	public void setMatchlength(int matchlength) {
		this.matchlength = matchlength;
	}

	/**
	 * 
	 * @param mayDouble1
	 */
	public void setMayDouble1(int mayDouble1) {
		this.mayDouble1 = mayDouble1;
	}

	/**
	 * 
	 * @param mayDouble2
	 */
	public void setMayDouble2(int mayDouble2) {
		this.mayDouble2 = mayDouble2;
	}

	/**
	 * 
	 * @param onBar1
	 */
	public void setOnBar1(int onBar1) {
		this.onBar1 = onBar1;
	}

	/**
	 * 
	 * @param onBar2
	 */
	public void setOnBar2(int onBar2) {
		this.onBar2 = onBar2;
	}

	/**
	 * 
	 * @param onHome1
	 */
	public void setOnHome1(int onHome1) {
		this.onHome1 = onHome1;
	}

	/**
	 * 
	 * @param onHome2
	 */
	public void setOnHome2(int onHome2) {
		this.onHome2 = onHome2;
	}

	/**
	 * 
	 * @param player1Got
	 */
	public void setPlayer1Got(int player1Got) {
		this.player1Got = player1Got;
	}

	/**
	 * 
	 * @param player1Name
	 */
	public void setPlayer1Name(String player1Name) {
		this.player1Name = player1Name;
	}

	/**
	 * 
	 * @param player2Got
	 */
	public void setPlayer2Got(int player2Got) {
		this.player2Got = player2Got;
	}

	/**
	 * 
	 * @param player2Name
	 */
	public void setPlayer2Name(String player2Name) {
		this.player2Name = player2Name;
	}

	/**
	 * 
	 * @param redoubles
	 */
	public void setRedoubles(int redoubles) {
		this.redoubles = redoubles;
	}

	/**
	 * 
	 * @param turn
	 */
	public void setTurn(int turn) {
		this.turn = turn;
	}

	/**
	 * 
	 * @param wasDoubled
	 */
	public void setWasDoubled(int wasDoubled) {
		this.wasDoubled = wasDoubled;
	}

	/**
	 * 
	 * @return
	 */
	public Player getPlayerX() {
		return playerX;
	}

	/**
	 * 
	 * @return
	 */
	public Player getPlayerO() {
		return playerO;
	}

	/**
	 * 
	 * @param matchVersion
	 */
	public void setMatchVersion(JibsMatch matchVersion) {
		this.jibsMatchVersion = matchVersion;
	}

	/**
	 * 
	 * @param playerX
	 */
	public void setPlayerX(Player playerX) {
		this.playerX = playerX;
	}

	/**
	 * 
	 * @param playerO
	 */
	public void setPlayerO(Player playerO) {
		this.playerO = playerO;
	}

	/**
	 * 
	 * @return
	 */
	public JibsMatch getMatchVersion() {
		return jibsMatchVersion;
	}

	/**
	 * 
	 * @param player
	 * @return
	 */
	public Player getOpponent(Player player) {
		if (player.getName().equals(playerX.getName())) {
			return playerO;
		}

		if (player.getName().equals(playerO.getName())) {
			return playerX;
		}

		return null;
	}

	/**
	 * 
	 * @return
	 */
	public int getPlayerXPoints() {
		return player1Got;
	}

	/**
	 * 
	 * @return
	 */
	public int getPlayerOPoints() {
		return player2Got;
	}

	/**
	 * 
	 * @param player
	 * @return
	 */
	public boolean isPlayerX(Player player) {
		if (player.getName().equals(this.playerX.getName())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean isEnded() {
		return isEnded;
	}

	/**
	 * 
	 * @param player
	 */
	public void setJoinPlayer(Player player) {
		if (joinPlayer1 == null) {
			joinPlayer1 = player;
		} else {
			joinPlayer2 = player;
		}
	}

	/**
	 * 
	 * @return
	 */
	public Object getJoinPlayer1() {
		return joinPlayer1;
	}

	/**
	 * 
	 * @return
	 */
	public Object getJoinPlayer2() {
		return joinPlayer2;
	}

	/**
	 * 
	 * @param b
	 */
	public void setPostCrawFord(boolean b) {
		// TODO Auto-generated method stub
	}

	/**
	 * 
	 * @return
	 */
	public boolean isCrawFordGame() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 
	 * @param b
	 */
	public void setCrawFordGame(boolean b) {
		crawFordGame = b;
	}

	/**
	 * 
	 * @param object
	 */
	public void setJoinPlayer1(Player object) {
		joinPlayer1 = object;
	}

	/**
	 * 
	 * @param object
	 */
	public void setJoinPlayer2(Player object) {
		joinPlayer2 = object;
	}

	/**
	 * 
	 * @param i
	 */
	public void setPlayerXPoints(int i) {
		player1Got = i;
	}

	/**
	 * 
	 * @param i
	 */
	public void setPlayerOPoints(int i) {
		player2Got = i;
	}

	/**
	 * 
	 * @return
	 */
	public BackgammonBoard switch2O() {
		BackgammonBoard opBoard = new BackgammonBoard(this);
		opBoard.setPlayer1Name(getPlayer2Name());
		opBoard.setPlayer2Name(getPlayer1Name());
		opBoard.setDirection(-1);
		opBoard.setColor(1);
		opBoard.setPlayerXdie1Value(playerOdie1Value);
		opBoard.setPlayerXdie2Value(playerOdie2Value);
		opBoard.setPlayerOdie1Value(playerXdie1Value);
		opBoard.setPlayerOdie2Value(playerXdie2Value);
		opBoard.setOnBar1(getOnBar2());
		opBoard.setOnBar2(getOnBar1());
		opBoard.setOnHome1(getOnHome2());
		opBoard.setOnHome2(getOnHome1());
		opBoard.setBar(getHome());
		opBoard.setHome(getBar());
		opBoard.setPlayer1Got(getPlayer2Got());
		opBoard.setPlayer2Got(getPlayer1Got());
		opBoard.setMayDouble1(getMayDouble2());
		opBoard.setMayDouble2(getMayDouble1());

		return opBoard;
	}

	public int getPlayerXdie1Value() {
		return playerXdie1Value;
	}

	public void setPlayerXdie1Value(int playerXdie1Value) {
		this.playerXdie1Value = playerXdie1Value;
	}

	public int getPlayerXdie2Value() {
		return playerXdie2Value;
	}

	public void setPlayerXdie2Value(int playerXdie2Value) {
		this.playerXdie2Value = playerXdie2Value;
	}

	public int getPlayerOdie1Value() {
		return playerOdie1Value;
	}

	public void setPlayerOdie1Value(int playerOdie1Value) {
		this.playerOdie1Value = playerOdie1Value;
	}

	public int getPlayerOdie2Value() {
		return playerOdie2Value;
	}

	public void setPlayerOdie2Value(int playerOdie2Value) {
		this.playerOdie2Value = playerOdie2Value;
	}

	public void setEnded(boolean isEnded) {
		this.isEnded = isEnded;
	}

	public void clearDie() {
		setPlayerXdie1Value(0);
		setPlayerXdie2Value(0);
	}

	@Override
	public String toString() {
		return outBoard_3(player1Name, turn, playerXdie1Value,
				playerXdie2Value, playerOdie1Value, playerOdie2Value);
	}
}
