package net.sourceforge.jibs.backgammon;

import net.sourceforge.jibs.server.JibsServer;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.util.JibsSet;

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
	private int playerDie1Value;
	private int playerDie2Value;
	private int opponentDie1Value;
	private int opponentDie2Value;
	private int bar; // 25 or 0 (see home)
	private int[] board; // 26 numbers giving the board. Positions 0 and 25
							// represent the bars for the players (see below).
							// Positive numbers represent O's pieces negative
							// numbers represent X's pieces
	private int canMove;
	private int color; // -1 if you are X, +1 if you are O
	private int cubeNumber;// the number on the doubling cube
	private int didCrawford;// don't use this token
	private int direction; // -1 if you play from position 24 to position 1 +1
							// if you play from position 1 to position 24
	private int forcedMove;// don't use this token
	private int home; // 0 or 25 depending on direction
	private int matchlength; // match length or 9999 for unlimited matches
	private int mayDouble1;// 1 if player is allowed to double, 0 otherwise
	private int mayDouble2;// same for opponent
	private int onBar1;// number of player's pieces on the bar
	private int onBar2;// same for opponent
	private int onHome1;// number of pieces already removed from the board by
						// player
	private int onHome2;// same for opponent
	private int playerGot; // player's points in the match so far
	private String playerName;
	private int opponentGot; // opponent's points in the match so far
	private String opponentName;
	private int redoubles;// maximum number of instant redoubles in unlimited
							// matches
	private int turn; // -1 if it's X's turn, +1 if it's O's turn 0 if the game
						// is over
	private int wasDoubled; // 1 if your opponent has just doubled, 0 otherwise
	// Special data for jIBS
	private JibsMatch jibsMatchVersion;
	private Player joinPlayer1;
	private Player joinPlayer2;
	private boolean crawFordGame;
	private boolean isEnded;

	/**
     *
     */
	public BackgammonBoard(final BackgammonBoard b2) {
		this.init(b2.jibsServer, b2.playerName, b2.opponentName,
				b2.matchlength, b2.getMatchVersion());
		this.jibsServer = b2.jibsServer;
		this.playerX = b2.playerX;
		this.playerO = b2.playerO;
		this.playerDie1Value = b2.playerDie1Value;
		this.playerDie2Value = b2.playerDie2Value;
		this.opponentDie1Value = b2.opponentDie1Value;
		this.opponentDie2Value = b2.opponentDie2Value;
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
		this.playerGot = b2.playerGot;
		this.playerName = b2.playerName;
		this.opponentGot = b2.opponentGot;
		this.opponentName = b2.opponentName;
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
		this.playerName = player1Name;
		this.opponentName = player2Name;
		this.matchlength = matchlength;
		this.playerName = player1Name;
		this.opponentName = player2Name;
		this.jibsMatchVersion = matchVersion;

		playerDie1Value = 0;
		playerDie2Value = 0;
		opponentDie1Value = 0;
		opponentDie2Value = 0;

		cubeNumber = 1;
		playerGot = 0;
		opponentGot = 0;
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
		strBoard.append(opponentName);
		strBoard.append(":");
		strBoard.append(matchlength);
		strBoard.append(":");
		strBoard.append(playerGot);
		strBoard.append(":");
		strBoard.append(opponentGot);

		for (int i = 0; i < 26; i++) {
			strBoard.append(":");
			strBoard.append(board[i]);
		}

		strBoard.append(":");
		strBoard.append(turn);
		strBoard.append(":");
		strBoard.append(playerDie1Value);
		strBoard.append(":");
		strBoard.append(playerDie2Value);
		strBoard.append(":");
		strBoard.append(opponentDie1Value);
		strBoard.append(":");
		strBoard.append(opponentDie2Value);
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
		return playerGot;
	}

	/**
	 * 
	 * @return
	 */
	public String getPlayer1Name() {
		return playerName;
	}

	/**
	 * 
	 * @return
	 */
	public int getPlayer2Got() {
		return opponentGot;
	}

	/**
	 * 
	 * @return
	 */
	public String getPlayer2Name() {
		return opponentName;
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
	public String outBoard(String name, int turn, int dice1, int dice2, int j,
			int k) {
		try {
			Player player = getPlayerX();
			JibsSet jibsSet = player.getJibsSet();
			String strBoardStyle = (String) jibsSet.get("boardstyle");
			int boardstyle = new Integer(strBoardStyle).intValue();
			String outBoard = "";
			switch (boardstyle) {
			case 1:
			default:
			case 2:
				outBoard = outBoard_2(turn, dice1, dice2, j, k);
				break;

			case 3:
				outBoard = outBoard_3(name, turn, dice1, dice2, j, k);
				break;
			}
			return outBoard;
		} catch (NumberFormatException e) {
			logger.warn(e);
		}
		return null;
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
		bf.append(opponentName);
		bf.append(" - score: ");
		bf.append(opponentGot);

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
		bf.append(playerName);
		bf.append(" - score: ");
		bf.append(playerGot);
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
		strBoard.append("You");
		strBoard.append(":");
		strBoard.append(name);
		strBoard.append(":");

		if (getMatchVersion().getVersion() == JibsMatch.nPointMatch) {
			strBoard.append(matchlength);
		} else {
			strBoard.append("9999");
		}

		strBoard.append(":");
		strBoard.append(playerGot);
		strBoard.append(":");
		strBoard.append(opponentGot);

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
		this.playerGot = player1Got;
	}

	/**
	 * 
	 * @param player1Name
	 */
	public void setPlayer1Name(String player1Name) {
		this.playerName = player1Name;
	}

	/**
	 * 
	 * @param player2Got
	 */
	public void setPlayer2Got(int player2Got) {
		this.opponentGot = player2Got;
	}

	/**
	 * 
	 * @param player2Name
	 */
	public void setPlayer2Name(String player2Name) {
		this.opponentName = player2Name;
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
		return playerGot;
	}

	/**
	 * 
	 * @return
	 */
	public int getPlayerOPoints() {
		return opponentGot;
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
		playerGot = i;
	}

	/**
	 * 
	 * @param i
	 */
	public void setPlayerOPoints(int i) {
		opponentGot = i;
	}

	/**
	 * 
	 * @return
	 */
	public BackgammonBoard switch2O() {
		BackgammonBoard opBoard = new BackgammonBoard(this);
		opBoard.setPlayer1Name(getPlayer2Name());
		opBoard.setPlayer2Name(getPlayer1Name());
		//opBoard.setDirection(-1);
		//opBoard.setColor(1);
//		opBoard.setPlayerXdie1Value(opponentDie1Value);
//		opBoard.setPlayerXdie2Value(opponentDie2Value);
//		opBoard.setPlayerOdie1Value(playerDie1Value);
//		opBoard.setPlayerOdie2Value(playerDie2Value);
//		opBoard.setOnBar1(getOnBar2());
//		opBoard.setOnBar2(getOnBar1());
//		opBoard.setOnHome1(getOnHome2());
//		opBoard.setOnHome2(getOnHome1());
//		opBoard.setBar(getHome());
//		opBoard.setHome(getBar());
//		opBoard.setPlayer1Got(getPlayer2Got());
//		opBoard.setPlayer2Got(getPlayer1Got());
//		opBoard.setMayDouble1(getMayDouble2());
//		opBoard.setMayDouble2(getMayDouble1());

		return opBoard;
	}

	public int getPlayerXdie1Value() {
		return playerDie1Value;
	}

	public void setPlayerXdie1Value(int playerXdie1Value) {
		this.playerDie1Value = playerXdie1Value;
	}

	public int getPlayerXdie2Value() {
		return playerDie2Value;
	}

	public void setPlayerXdie2Value(int playerXdie2Value) {
		this.playerDie2Value = playerXdie2Value;
	}

	public int getPlayerOdie1Value() {
		return opponentDie1Value;
	}

	public void setPlayerOdie1Value(int playerOdie1Value) {
		this.opponentDie1Value = playerOdie1Value;
	}

	public int getPlayerOdie2Value() {
		return opponentDie2Value;
	}

	public void setPlayerOdie2Value(int playerOdie2Value) {
		this.opponentDie2Value = playerOdie2Value;
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
		return outBoard_3(playerName, turn, playerDie1Value, playerDie2Value,
				opponentDie1Value, opponentDie2Value);
	}
}
