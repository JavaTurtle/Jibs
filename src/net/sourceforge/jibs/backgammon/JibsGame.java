package net.sourceforge.jibs.backgammon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;

import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.gui.JibsTextArea;
import net.sourceforge.jibs.server.ClientWorker;
import net.sourceforge.jibs.server.JibsServer;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.util.GameEnder;
import net.sourceforge.jibs.util.JibsWriter;
import net.sourceforge.jibs.util.RatingCalculator;
import net.sourceforge.jibs.util.SavedGameParam;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * This models one game between two players. A match, hence can consist of
 * several games.
 */
public class JibsGame {
	// private static final Logger logger = Logger.getLogger(JibsGame.class);
	private BackgammonBoard board;
	private Collection<PossibleMove> col;
	private JibsServer jibsServer;
	private Player playerX;
	private Player playerO;

	public JibsGame() {
	}

	public JibsGame(JibsServer jibsserver, Player playerX, Player playerO,
			int Matchlength, int player1Points, int player2Points,
			JibsMatch matchVersion, int jibsTurn) {
		this.jibsServer = jibsserver;
		this.playerX = playerX;
		this.playerO = playerO;
		this.board = new BackgammonBoard(jibsserver, playerX.getName(),
				playerO.getName(), Matchlength, matchVersion);
		this.board.setPlayerX(playerX);
		this.board.setPlayerO(playerO);
		this.board.setTurn(jibsTurn);
	}

	public static JibsGame constructGame(JibsServer jibsServer,
			SavedGameParam resumeData) {
		Player playerX = resumeData.getPlayerX();
		Player playerO = resumeData.getPlayerO();

		JibsGame game = new JibsGame();
		BackgammonBoard board = new BackgammonBoard(jibsServer,
				resumeData.getPlayer_A(), resumeData.getPlayer_B(), resumeData
						.getMatchlength().intValue(), new JibsMatch(
						resumeData.getMatchVersion()));
		board.setPlayerX(playerX);
		board.setPlayerO(playerO);
		game.setBackgammonBoard(board);
		game.getBackgammonBoard().setMatchVersion(
				new JibsMatch(resumeData.getMatchVersion()));

		game.getBackgammonBoard().setPlayerXdie1Value(Die.roll(jibsServer.getJibsRandom()));
		game.getBackgammonBoard().setPlayerXdie2Value(Die.roll(jibsServer.getJibsRandom()));

		StringTokenizer tokenizer = new StringTokenizer(resumeData.getBoard(),
				":");
		int position = 0;
		boolean bError = false;
		int help;

		while (tokenizer.hasMoreTokens()) {
			String x = tokenizer.nextToken();

			switch (position) {
			case 0:

				if (!x.equals("board")) {
					bError = true;
				}

				break;

			case 1:
				board.setPlayerX(playerX);
				board.setPlayer1Name(playerX.getName());

				if (!x.equals("You")) {
					bError = true;
				}

				break;

			case 2:

				Player player2 = jibsServer.getServer().getPlayer(x);
				board.setPlayer2Name(player2.getName());
				board.setPlayerO(player2);

				break;

			case 3:
				help = Integer.valueOf(x);
				board.setMatchlength(help);

				break;

			case 4:
				help = Integer.valueOf(x);
				board.setPlayer1Got(help);

				break;

			case 5:
				help = Integer.valueOf(x);
				board.setPlayer2Got(help);

				break;

			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
			case 24:
			case 25:
			case 26:
			case 27:
			case 28:
			case 29:
			case 30:
			case 31:
				help = Integer.valueOf(x);
				board.getBoard()[position - 6] = help;

				break;

			case 32: // turn

				int turn = Integer.valueOf(x);
				board.setTurn(turn);

				break;

			case 33: // player dice
				help = Integer.valueOf(x);
				board.setPlayerXdie1Value(help);

				break;

			case 34: // player dice
				help = Integer.valueOf(x);
				board.setPlayerXdie2Value(help);

				break;

			case 35: // opp dice
				help = Integer.valueOf(x);
				board.setPlayerOdie1Value(help);

				break;

			case 36: // opp dice
				help = Integer.valueOf(x);
				board.setPlayerOdie2Value(help);

				break;

			case 37: // cube
				help = Integer.valueOf(x);
				board.setCubeNumber(help);

				break;

			case 38: // maydouble1
				help = Integer.valueOf(x);
				board.setMayDouble1(help);

				break;

			case 39: // maydouble2
				help = Integer.valueOf(x);
				board.setMayDouble2(help);

				break;

			case 40: // wasdoubled
				help = Integer.valueOf(x);
				board.setWasDoubled(help);

				break;

			case 41: // color
				help = Integer.valueOf(x);
				board.setColor(help);

				break;

			case 42: // direction
				help = Integer.valueOf(x);
				board.setDirection(help);

				break;

			case 43: // home
				help = Integer.valueOf(x);
				board.setHome(help);

				break;

			case 44: // bar
				help = Integer.valueOf(x);
				board.setBar(help);

				break;

			case 45: // player onHome
				help = Integer.valueOf(x);
				board.setOnHome1(help);

				break;

			case 46: // opp onHome
				help = Integer.valueOf(x);
				board.setOnHome2(help);

				break;

			case 47: // player onBar
				help = new Integer(x).intValue();
				board.setOnBar1(help);

				break;

			case 48: // opp onBar
				help = Integer.valueOf(x);
				board.setOnBar2(help);

				break;

			case 49: // canMove
				help = Integer.valueOf(x);
				board.setCanMove(help);

				break;

			case 50: // forced move
				help = Integer.valueOf(x);
				board.setForcedMove(help);

				break;

			case 51: // did crawford
				help = Integer.valueOf(x);
				board.setDidCrawford(help);

				break;

			case 52: // redoubles
				help = new Integer(x).intValue();
				board.setRedoubles(help);

				break;
			}

			position++;
		}

		if (tokenizer.hasMoreTokens()) {
			bError = true;
		}

		if (!bError) {
			return game;
		} else {
			return null;
		}
	}

	public static void deleteGames(JibsServer jibsServer, Player player1,
			Player player2) {
		SavedGameParam savedGame = new SavedGameParam(player1.getName(),
				player2.getName());
		SqlSessionFactory sqlSessionFactory = jibsServer.getSqlSessionFactory();
		deleteSavedGame(sqlSessionFactory, savedGame);
	}

	public static SavedGameParam loadGame(JibsServer jibsServer, String name1,
			String name2) {
		SqlSessionFactory sqlSessionFactory = jibsServer.getSqlSessionFactory();
		SavedGameParam savedGame = new SavedGameParam(name1, name2);
		SavedGameParam retSavedGame = null;

		retSavedGame = loadSavedGame(sqlSessionFactory, savedGame);

		return retSavedGame;
	}

	private static SavedGameParam loadSavedGame(
			SqlSessionFactory sqlSessionFactory, SavedGameParam savedGame) {
		SavedGameParam retSavedGame;
		SqlSession sqlSession = sqlSessionFactory.openSession(true);
		try {
			retSavedGame = (SavedGameParam) sqlSession.selectOne(
					"SavedGames.loadSavedGame", savedGame);
			return retSavedGame;
		} finally {
			sqlSession.close();
		}
	}

	private static SavedGameParam deleteSavedGame(
			SqlSessionFactory sqlSessionFactory, SavedGameParam savedGame) {
		SavedGameParam retSavedGame;
		SqlSession sqlSession = sqlSessionFactory.openSession(true);
		try {
			retSavedGame = (SavedGameParam) sqlSession.selectOne(
					"SavedGames.deleteSavedGame", savedGame);
			return retSavedGame;
		} finally {
			sqlSession.close();
		}
	}

	private static SavedGameParam saveSavedGame(
			SqlSessionFactory sqlSessionFactory, SavedGameParam savedGame) {
		SavedGameParam retSavedGame;
		SqlSession sqlSession = sqlSessionFactory.openSession(true);
		try {
			retSavedGame = (SavedGameParam) sqlSession.selectOne(
					"SavedGames.saveGame", savedGame);
			return retSavedGame;
		} finally {
			sqlSession.close();
		}
	}

	public int calcPossibleMoves(JibsGame game, Player player,
			boolean isFirstMove, int turn, MoveChances nrChances,
			JibsWriter out1, JibsWriter out2, BackgammonBoard board,
			int dice1, int dice2) {
		Object[] obj = null;
		String msg = null;
		int nrMoves = 0;
		Player opponent = board.getOpponent(player);

		switch (turn) {
		case -1:

			BackgammonBoard opBoard = board.switch2O();
			opBoard.setCanMove(0);
			opBoard.outBoard(out2, "You", turn, 0, 0, dice1, dice2); // O
																		// out2.println("board:You:aleucht:2:0:0:0:-2:0:0:0:0:5:0:3:0:0:0:-5:5:0:0:0:-3:0:-5:0:0:0:0:2:0:-1:0:0:5:3:1:1:1:0:1:-1:0:25:0:0:0:0:0:0:0:0");
																		// out2.println("board:You:aleucht:2:0:1:0:-2:0:0:0:0:5:0:3:0:0:0:-5:5:0:0:0:-3:0:-5:0:0:0:0:2:0:-1:0:0:5:3:1:1:0:0:1:-1:0:25:0:0:0:0:0:0:0:0");

			board.getPlayerO().show2WatcherBoard(opBoard,
					board.getPlayerO().getName(), 0, 0, dice1, dice2);
			nrMoves = nrChances.calcPossibleMovesX(turn, dice1, dice2);
			board.setCanMove(nrMoves);
			board.outBoard(out1, "You", turn, dice1, dice2, 0, 0); // X
			board.getPlayerX().show2WatcherBoard(board,
					board.getPlayerX().getName(), dice1, dice2, 0, 0);

			if (!isFirstMove) {
				if (nrMoves > 0) {
					// m_please_move=Please move %0 pieces.
					obj = new Object[] { Integer.valueOf(nrMoves) };
					msg = jibsServer.getJibsMessages().convert("m_please_move",
							obj);
					player.getOutputStream().println(msg);

				} else {
					// m_you_cant_move=You can't move.
					msg = jibsServer.getJibsMessages().convert(
							"m_you_cant_move");
					player.getOutputStream().println(msg);
					// m_other_cant_move=%0 can't move.
					obj = new Object[] { player.getName() };
					msg = jibsServer.getJibsMessages().convert(
							"m_other_cant_move", obj);
					opponent.getOutputStream().println(msg);
				}
			}

			break;

		case 1:
			nrMoves = nrChances.calcPossibleMovesO(turn, dice1, dice2);
			board.setCanMove(0);
			board.outBoard(out1, "You", turn, 0, 0, dice1, dice2); // X
			board.getPlayerX().show2WatcherBoard(board,
					board.getPlayerX().getName(), 0, 0, dice1, dice2);
			board.setCanMove(nrMoves);
			opBoard = board.switch2O();
			opBoard.outBoard(out2, "You", turn, dice1, dice2, 0, 0); // O
			board.getPlayerO().show2WatcherBoard(opBoard,
					board.getPlayerO().getName(), dice1, dice2, 0, 0);

			if (!isFirstMove) {
				if (nrMoves > 0) {
					// m_please_move=Please move %0 pieces.
					obj = new Object[] { Integer.valueOf(nrMoves) };
					msg = jibsServer.getJibsMessages().convert("m_please_move",
							obj);
					out2.println(msg);
				} else {
					// m_you_cant_move=You can't move.
					msg = jibsServer.getJibsMessages().convert(
							"m_you_cant_move");
					out2.println(msg);
					// m_other_cant_move=%0 can't move.
					obj = new Object[] { board.getPlayerO().getName() };
					msg = jibsServer.getJibsMessages().convert(
							"m_other_cant_move", obj);
					out1.println(msg);
				}
			}

			break;
		}
		return nrMoves;
	}

	public String checkForcedMove(Player player, BackgammonBoard bgBoard,
			int nrMoves, JibsWriter out) {
		JibsMessages jibsMessages = jibsServer.getJibsMessages();

		if (player.checkToggle("automove")) {
			if (col.size() == 1) {
				Iterator iter = col.iterator();
				PossibleMove posMove = (PossibleMove) iter.next();
				String movString = posMove.conv2MoveString();

				// m_only_move=The only possible move is %0.
				Object[] obj = new Object[] { movString };
				String msg = jibsMessages.convert("m_only_move", obj);
				out.println(msg);

				String nextCmd = "m " + movString;

				return nextCmd;
			}
		}

		return null;
	}

	public String checkGreedy(Player player, BackgammonBoard bgBoard,
			int nrMoves, JibsWriter out) {
		if (player.checkToggle("greedy")) {
			if (col.size() > 0) {
				BackgammonBoard helpboard = new BackgammonBoard(bgBoard);

				for (PossibleMove posMove : col) {
					BackgammonBoard board1 = null;
					BackgammonBoard setBoard = new BackgammonBoard(bgBoard);

					for (int j = 0; j <= posMove.getnrMoves(); j++) {
						Move mv = posMove.getMove(j);

						if (mv != null) {
							board1 = setBoard.placeMoveX(mv);
							setBoard = board1;
						}
					}

					int diff = setBoard.getOnHome1() - helpboard.getOnHome1();

					if (diff >= nrMoves) {
						// yes, we've found a possible greedy move
						return "m " + posMove.conv2MoveString();
					}
				}
			}
		}

		return null;
	}

	public BackgammonBoard getBackgammonBoard() {
		return board;
	}

	public void resumeGame(SavedGameParam resumeData, int startTurn, int dice1,
			int dice2, int player1Points, int player2Points) {
		SavedGameParam savedGame = new SavedGameParam(resumeData.getPlayer_A(),
				resumeData.getPlayer_B());
		SqlSessionFactory sqlSessionFactory = jibsServer.getSqlSessionFactory();
		deleteSavedGame(sqlSessionFactory, savedGame);

		Player player1 = resumeData.getPlayerX();
		Player player2 = resumeData.getPlayerO();

		if (board.getMatchVersion().getVersion() == JibsMatch.nPointMatch) {
			// m_resume_match=%0 and %1 are resuming their %2-point match.
			Object[] obj = { player1.getName(), player2.getName(),
					Integer.valueOf(resumeData.getMatchlength()) };
			JibsMessages jibsMessages = jibsServer.getJibsMessages();
			String msg = jibsMessages.convert("m_resume_match", obj);
			JibsTextArea.log(jibsServer, msg, true);

			board.getPlayerX().informPlayers(msg, player2);
		} else {
			// m_resume_match_unlimited=%0 and %1 are resuming their unlimited
			// match.
			Object[] obj = { player1.getName(), player2.getName() };
			JibsMessages jibsMessages = jibsServer.getJibsMessages();
			String msg = jibsMessages.convert("m_resume_match_unlimited", obj);
			JibsTextArea.log(jibsServer, msg, true);

			board.getPlayerX().informPlayers(msg, player2);
		}

		JibsWriter out1 = player1.getOutputStream();
		JibsWriter out2 = player2.getOutputStream();

		MoveChances nrChances = null;

		switch (board.getTurn()) {
		case -1:
			nrChances = new MoveChances(jibsServer, this, board);
			calcPossibleMoves(this, board.getPlayerX(), true, board.getTurn(),
					nrChances, out1, out2, board, dice1, dice2);

			break;

		case 1:
			nrChances = new MoveChances(jibsServer, this, board);
			calcPossibleMoves(this, board.getPlayerX(), true, board.getTurn(),
					nrChances, out1, out2, board, dice1, dice2);
		}
	}

	public int rollO(JibsWriter out, JibsWriter out2, Player player,
			BackgammonBoard bgBoard, int turn) {
		int dice11 = Die.roll(jibsServer.getJibsRandom());
		int dice12 = Die.roll(jibsServer.getJibsRandom());
		board.setPlayerOdie1Value(dice11);
		board.setPlayerOdie2Value(dice12);

		// m_you_roll=You roll %0 and %1.
		Object[] obj = new Object[] { dice11, dice12 };
		String msg = jibsServer.getJibsMessages().convert("m_you_roll", obj);
		player.getOutputStream().println(msg);
		// m_other_roll=%0 rolls %1 and %2.
		obj = new Object[] { player.getName(), dice11, dice12 };
		msg = jibsServer.getJibsMessages().convert("m_other_roll", obj);
		showWatcherRoll(board.getPlayerX(), msg);

		Player tPlayer = board.getOpponent(player);
		tPlayer.getOutputStream().println(msg);

		MoveChances nrChances = new MoveChances(jibsServer, this, bgBoard);
		int nrMoves = calcPossibleMoves(this, player, false, turn, nrChances,
				out, out2, bgBoard, dice11, dice12);

		return nrMoves;
	}

	public int rollX(JibsWriter out, JibsWriter out2, Player player,
			BackgammonBoard bgBoard, int turn) {
		int dice11 = Die.roll(jibsServer.getJibsRandom());
		int dice12 = Die.roll(jibsServer.getJibsRandom());

		board.setPlayerXdie1Value(dice11);
		board.setPlayerXdie2Value(dice12);

		// m_you_roll=You roll %0 and %1.
		Object[] obj = new Object[] { dice11, dice12 };
		String msg = jibsServer.getJibsMessages().convert("m_you_roll", obj);
		player.getOutputStream().println(msg);

		// m_other_roll=%0 rolls %1 and %2.
		obj = new Object[] { player.getName(), dice11, dice12 };
		msg = jibsServer.getJibsMessages().convert("m_other_roll", obj);
		showWatcherRoll(board.getPlayerO(), msg);

		Player tPlayer = board.getOpponent(player);
		tPlayer.getOutputStream().println(msg);
		showWatcherRoll(board.getPlayerX(), msg);

		MoveChances nrChances = new MoveChances(jibsServer, this, bgBoard);
		int nrMoves = calcPossibleMoves(this, player, false, turn, nrChances,
				out, out2, bgBoard, dice11, dice12);

		return nrMoves;
	}

	public void save() {
		SqlSessionFactory sqlSessionFactory = jibsServer.getSqlSessionFactory();

		if (true) {
			// delete old games
			JibsGame.deleteGames(jibsServer, board.getPlayerX(),
					board.getPlayerO());

			// save current game
			SavedGameParam savedGame = new SavedGameParam(this,
					board.getPlayerX(), board.getPlayerO(),
					getBackgammonBoard(), board.getMatchlength(),
					board.getTurn(), board.getMatchVersion(), new Date());
			saveSavedGame(sqlSessionFactory, savedGame);
			board.getPlayerX().endGame(board.getPlayerO());
			board.getPlayerO().endGame(board.getPlayerX());
			board.setEnded(true);
			board.getPlayerO().setGame(null);
			board.getPlayerX().setGame(null);
		}
	}

	public void setBackgammonBoard(BackgammonBoard b) {
		this.board = b;
	}

	public void setJibsServer(JibsServer jibsServer) {
		this.jibsServer = jibsServer;
	}

	/*
	 * public void setResumeData(ResumeData resumeData) { this.resumeData =
	 * resumeData; }
	 */
	private void showWatcherRoll(Player player, String msg) {
		player.show2WatcherRoll(msg);
	}

	public void startGame(int startTurn, int dice1, int dice2,
			int player1Points, int player2Points, int mayDouble1, int mayDouble2) {
		JibsWriter out1 = board.getPlayerX().getOutputStream();
		JibsWriter out2 = board.getPlayerO().getOutputStream();
		Player opponent = board.getOpponent(board.getPlayerX());

		board.setMayDouble1(mayDouble1);
		board.setMayDouble2(mayDouble2);
		board.setPlayer1Got(player1Points);
		board.setPlayer2Got(player2Points);
		board.setTurn(startTurn);

		MoveChances nrChances = null;

		nrChances = new MoveChances(jibsServer, this, board);

		int nrMoves = 0;

		switch (board.getTurn()) {
		case -1:
			nrMoves = calcPossibleMoves(this, board.getPlayerO(), true,
					board.getTurn(), nrChances, out1, out2, board, dice1, dice2);

			if (nrMoves > 0) {
				String nextCmd1 = checkForcedMove(board.getPlayerO(), board,
						nrMoves, out2);
				String nextCmd2 = checkGreedy(board.getPlayerO(), board,
						nrMoves, out2);
				String nextCmd = null;

				if (nextCmd1 != null) {
					nextCmd = nextCmd1;
				}

				if ((nextCmd == null) && (nextCmd2 != null)) {
					nextCmd = nextCmd2;
				}

				board.getPlayerO().getClientWorker().executeCmd(nextCmd);
			} else {
				board.setTurn(-board.getTurn());

				board.setPlayerXdie1Value(0);
				board.setPlayerXdie2Value(0);
				opponent.getClientWorker().executeCmd("roll");
			}

			break;

		case 1:
			nrMoves = calcPossibleMoves(this, board.getPlayerO(), true,
					board.getTurn(), nrChances, out1, out2, board, dice1, dice2);

			if (nrMoves > 0) {
				String nextCmd1 = checkForcedMove(board.getPlayerX(), board,
						nrMoves, out1);
				String nextCmd2 = checkGreedy(board.getPlayerX(), board,
						nrMoves, out1);
				String nextCmd = null;

				if (nextCmd1 != null) {
					nextCmd = nextCmd1;
				}

				if ((nextCmd == null) && (nextCmd2 != null)) {
					nextCmd = nextCmd2;
				}

				board.getPlayerX().getClientWorker().executeCmd(nextCmd);
			} else {
				board.setTurn(-board.getTurn());
				board.setPlayerXdie1Value(0);
				board.setPlayerXdie2Value(0);
				opponent.getClientWorker().executeCmd("roll");
			}

			break;
		}
	}

	public void winGameO(JibsGame game, BackgammonBoard board, int winPoints) {
		String msg = null;
		Object[] obj = null;
		Player winPlayer = board.getPlayerO();
		Player loosePlayer = board.getPlayerX();
		JibsWriter out1 = board.getPlayerO().getOutputStream();
		JibsWriter out2 = board.getPlayerX().getOutputStream();

		// determine winPoints
		if (winPoints < 0) {
			int calc_winPoints = 1; // Normal

			if (board.getOnHome1() <= 0) {
				calc_winPoints = 2; // Gammon

				for (int i = 1; i <= 6; i++) {
					if (board.getBoard()[i] < 0) {
						calc_winPoints = 3; // Backgammon
					}
				}
			}

			board.setPlayer2Got(board.getPlayer2Got()
					+ (calc_winPoints * board.getCubeNumber()));

			winPoints = calc_winPoints;
		} else {
			board.setPlayer2Got(winPoints);
		}

		int gamePoints = board.getPlayer2Got();
		JibsMessages jibsMessages = jibsServer.getJibsMessages();

		switch (winPoints) {
		case 1:
			// m_you_win_game=%0 win the game and get 1 point. Congratulation!
			obj = new Object[] { "You" };
			msg = jibsMessages.convert("m_you_win_game", obj);
			out1.println(msg);
			obj = new Object[] { winPlayer.getName() };
			winPlayer.informWatcher("m_you_win_game", obj, true);

			// m_other_win_game=%0 wins the game and gets 1 point. Sorry.
			obj = new Object[] { winPlayer.getName(), Integer.valueOf(1) };

			msg = jibsMessages.convert("m_other_win_game", obj);
			out2.println(msg);
			loosePlayer.informWatcher("m_other_win_game", obj, true);

			break;

		default:
			// m_you_win_game2=%0 win the game and get %0 points.
			// Congratulation!
			obj = new Object[] { Integer.valueOf(winPoints) };
			msg = jibsMessages.convert("m_you_win_game2", obj);
			out1.println(msg);
			obj = new Object[] { winPlayer.getName(),
					Integer.valueOf(winPoints) };
			winPlayer.informWatcher("m_you_win_game2", obj, true);

			// m_other_win_game2=%0 wins the game and gets %1 points. Sorry.
			obj = new Object[] { winPlayer.getName(),
					Integer.valueOf(winPoints) };
			msg = jibsMessages.convert("m_other_win_game2", obj);
			out2.println(msg);
			loosePlayer.informWatcher("m_other_win_game2", obj, true);

			break;
		}

		BackgammonBoard opBoard = board.switch2O();
		opBoard.outBoard(out1, "You", 0, 0, 0, 0, 0);
		board.outBoard(out2, "You", 0, 0, 0, 0, 0);

		if ((gamePoints < board.getMatchlength())) {
			// match hasn't ended yet, start another game
			// m_join_next_game=Type 'join' if you want to play the next game,
			// type 'leave" if you don't.
			msg = jibsMessages.convert("m_join_next_game");
			out1.println(msg);

			msg = jibsMessages.convert("m_join_next_game");
			out2.println(msg);
		}

		if (gamePoints >= board.getMatchlength()) {
			Object[] obj6 = null;

			// m_match_over_all=%0 wins a %1 point match against %2 %3-%4 .
			obj6 = new Object[] { winPlayer.getName(),
					Integer.valueOf(board.getMatchlength()),
					loosePlayer.getName(),
					Integer.valueOf(board.getPlayerOPoints()),
					Integer.valueOf(board.getPlayerXPoints()) };

			msg = jibsMessages.convert("m_match_over_all", obj6);
			JibsTextArea.log(jibsServer, msg, true);
			winPlayer.informWatcher("m_match_over_all", obj6, true);
			loosePlayer.informWatcher("m_match_over_all", obj6, true);

			// m_you_win_match=You win the %0 point match %1-%2 .
			obj6 = new Object[] { Integer.valueOf(board.getMatchlength()),
					Integer.valueOf(board.getPlayerOPoints()),
					Integer.valueOf(board.getPlayerXPoints()) };

			String winMsg = jibsMessages.convert("m_you_win_match", obj6);

			// m_other_win_match=%0 wins the %1 point match %2-%3 .
			obj6 = new Object[] { winPlayer.getName(),
					Integer.valueOf(board.getMatchlength()),
					Integer.valueOf(board.getPlayerOPoints()),
					Integer.valueOf(board.getPlayerXPoints()) };

			String looseMsg = jibsMessages.convert("m_other_win_match", obj6);

			int expA = board.getPlayerX().getExperience();
			double ratingA = board.getPlayerX().getRating();
			int expB = board.getPlayerO().getExperience();
			double ratingB = board.getPlayerO().getRating();
			double d = RatingCalculator.ratingChange(false,
					board.getMatchlength(), expA, ratingA, expB, ratingB);
			double newRating;
			int newExperience;

			newRating = ratingA - d;
			newExperience = expA + board.getMatchlength();

			ClientWorker.changeRating(jibsServer.getSqlSessionFactory(), board.getPlayerX(), newRating, newExperience);

			newRating = ratingB + d;
			newExperience = expB + board.getMatchlength();

			ClientWorker.changeRating(jibsServer.getSqlSessionFactory(), board.getPlayerO(), newRating, newExperience);
			out1.println(winMsg);
			out2.println(looseMsg);
			new GameEnder(jibsServer, board.getPlayerX(), board.getPlayerO())
					.start();
		}
	}

	public void winGameX(JibsGame game, BackgammonBoard board, int winPoints) {
		String msg = null;
		Object[] obj = null;
		Player winPlayer = board.getPlayerX();
		Player loosePlayer = board.getPlayerO();
		JibsWriter out1 = board.getPlayerX().getOutputStream();
		JibsWriter out2 = board.getPlayerO().getOutputStream();

		// determine winPoints
		if (winPoints < 0) {
			int calc_winPoints = 1; // Normal

			if (board.getOnHome2() <= 0) {
				calc_winPoints = 2; // Gammon

				for (int i = 1; i <= 6; i++) {
					if (board.getBoard()[i] < 0) {
						calc_winPoints = 3; // Backgammon
					}
				}
			}

			board.setPlayer1Got(board.getPlayer1Got()
					+ (calc_winPoints * board.getCubeNumber()));

			winPoints = calc_winPoints;
		} else {
			board.setPlayer1Got(board.getPlayer1Got() + winPoints);
		}

		int gamePoints = board.getPlayer1Got();
		JibsMessages jibsMessages = jibsServer.getJibsMessages();

		switch (winPoints) {
		case 1:
			// m_you_win_game=%0 win the game and get 1 point. Congratulation!
			obj = new Object[] { "You" };
			msg = jibsMessages.convert("m_you_win_game", obj);
			out1.println(msg);
			obj = new Object[] { winPlayer.getName() };
			winPlayer.informWatcher("m_you_win_game", obj, true);

			// m_other_win_game=%0 wins the game and gets 1 point. Sorry.
			obj = new Object[] { winPlayer.getName(), Integer.valueOf(1) };

			msg = jibsMessages.convert("m_other_win_game", obj);
			out2.println(msg);
			loosePlayer.informWatcher("m_other_win_game", obj, true);

			break;

		default:
			// m_you_win_game2=%0 win the game and get %0 points.
			// Congratulation!
			obj = new Object[] { "You", Integer.valueOf(winPoints) };
			msg = jibsMessages.convert("m_you_win_game2", obj);
			out1.println(msg);
			obj = new Object[] { winPlayer.getName(),
					Integer.valueOf(winPoints) };
			winPlayer.informWatcher("m_you_win_game2", obj, true);

			// m_other_win_game2=%0 wins the game and gets %1 points. Sorry.
			obj = new Object[] { winPlayer.getName(),
					Integer.valueOf(winPoints) };
			msg = jibsMessages.convert("m_other_win_game2", obj);
			out2.println(msg);
			loosePlayer.informWatcher("m_other_win_game2", obj, true);

			break;
		}

		board.outBoard(out1, "You", 0, 0, 0, 0, 0);

		BackgammonBoard opBoard = board.switch2O();
		opBoard.outBoard(out2, "You", 0, 0, 0, 0, 0);

		if (gamePoints < board.getMatchlength()) {
			// match hasn't ended yet, start another game
			// m_join_next_game=Type 'join' if you want to play the next game,
			// type 'leave" if you don't.
			msg = jibsMessages.convert("m_join_next_game");
			out1.println(msg);

			msg = jibsMessages.convert("m_join_next_game");
			out2.println(msg);
		}

		if (gamePoints >= board.getMatchlength()) {
			Object[] obj6 = null;

			// m_match_over_all=%0 wins a %1 point match against %2 %3-%4 .
			obj6 = new Object[] { winPlayer.getName(),
					Integer.valueOf(board.getMatchlength()),
					loosePlayer.getName(),
					Integer.valueOf(board.getPlayer1Got()),
					Integer.valueOf(board.getPlayer2Got()) };

			msg = jibsMessages.convert("m_match_over_all", obj6);
			JibsTextArea.log(jibsServer, msg, true);
			winPlayer.informWatcher("m_match_over_all", obj6, true);
			loosePlayer.informWatcher("m_match_over_all", obj6, true);

			// m_you_win_match=You win the %0 point match %1-%2 .
			obj6 = new Object[] { Integer.valueOf(board.getMatchlength()),
					Integer.valueOf(board.getPlayer1Got()),
					Integer.valueOf(board.getPlayer2Got()) };

			String winMsg = jibsMessages.convert("m_you_win_match", obj6);

			// m_other_win_match=%0 wins the %1 point match %2-%3 .
			obj6 = new Object[] { winPlayer.getName(),
					Integer.valueOf(board.getMatchlength()),
					Integer.valueOf(board.getPlayer1Got()),
					Integer.valueOf(board.getPlayer2Got()) };

			String looseMsg = jibsMessages.convert("m_other_win_match", obj6);

			int expA = board.getPlayerX().getExperience();
			double ratingA = board.getPlayerX().getRating();
			int expB = board.getPlayerO().getExperience();
			double ratingB = board.getPlayerO().getRating();
			double d = RatingCalculator.ratingChange(true,
					board.getMatchlength(), expA, ratingA, expB, ratingB);
			double newRating;
			int newExperience;

			newRating = ratingA + d;
			newExperience = expA + board.getMatchlength();
			ClientWorker.changeRating(jibsServer.getSqlSessionFactory(), board.getPlayerX(), newRating, newExperience);

			newRating = ratingB - d;
			newExperience = expB + board.getMatchlength();
			ClientWorker.changeRating(jibsServer.getSqlSessionFactory(), board.getPlayerO(), newRating, newExperience);

			out1.println(winMsg);
			out2.println(looseMsg);
			new GameEnder(jibsServer, board.getPlayerX(), board.getPlayerO())
					.start();
		}
	}

	public void setCollection(ArrayList<PossibleMove> name) {
		this.col = name;
	}

	public Collection<PossibleMove> getCollection() {
		return this.col;
	}

	public Player getPlayerA() {
		return playerX;
	}

	public Player getPlayerB() {
		return playerO;
	}
}
