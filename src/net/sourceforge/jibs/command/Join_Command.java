package net.sourceforge.jibs.command;

import net.sourceforge.jibs.backgammon.BackgammonBoard;
import net.sourceforge.jibs.backgammon.Die;
import net.sourceforge.jibs.backgammon.JibsGame;
import net.sourceforge.jibs.backgammon.JibsMatch;
import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.gui.JibsTextArea;
import net.sourceforge.jibs.server.ClientWorker;
import net.sourceforge.jibs.server.JibsQuestion;
import net.sourceforge.jibs.server.Player;
import net.sourceforge.jibs.server.Server;
import net.sourceforge.jibs.util.ClipConstants;
import net.sourceforge.jibs.util.InviteQuestion;
import net.sourceforge.jibs.util.JibsNewGameData;
import net.sourceforge.jibs.util.JibsWriter;
import net.sourceforge.jibs.util.ResumeQuestion;
import net.sourceforge.jibs.util.SavedGameParam;

/**
 * The Join command.
 */
public class Join_Command implements JibsCommand {
	private Server server;

	public boolean execute(Server server, Player player, String strArgs,
			String[] args) {
		this.server = server;

		JibsGame game = player.getGame();
		Player opponent = null;
		int matchlength = 0;
		JibsMatch matchVersion = null;

		if (game != null) {
			BackgammonBoard board = game.getBackgammonBoard();

			if (board.isEnded()) {
				return doJoinForNewGame(player, args);
			} else {
				if (!board.isEnded()) {
					opponent = board.getOpponent(player);
					matchlength = board.getMatchlength();
					matchVersion = board.getMatchVersion();
					board.setJoinPlayer(player);

					if ((board.getJoinPlayer1() != null)
							&& (board.getJoinPlayer2() != null)) {
						// start next game
						BackgammonBoard nextBoard = null;
						nextBoard = new BackgammonBoard(server.getJibsServer(),
								board.getPlayerX().getName(), board
										.getPlayerO().getName(), matchlength,
								matchVersion);
						nextBoard.setPlayerX(board.getPlayerX());
						nextBoard.setPlayerO(board.getPlayerO());
						nextBoard.setPlayer1Got(board.getPlayer1Got());
						nextBoard.setPlayer2Got(board.getPlayer2Got());

						boolean useCrawford = player.checkToggle("crawford")
								&& opponent.checkToggle("crawford");

						if (useCrawford) {
							int diff1 = 0;
							int diff2 = 0;

							diff1 = board.getMatchlength()
									- board.getPlayerXPoints();

							if (diff1 == 1) {
								nextBoard.setCrawFordGame(true);
								nextBoard.setMayDouble1(1);
								nextBoard.setMayDouble2(0);
							}

							diff2 = board.getMatchlength()
									- board.getPlayerOPoints();

							if (diff2 == 1) {
								nextBoard.setCrawFordGame(true);
								nextBoard.setMayDouble2(1);
								nextBoard.setMayDouble1(0);
							}
						}

						board = nextBoard;
						game.setBackgammonBoard(board);

						greet(board.getPlayerX(), board.getPlayerO(),
								board.getMatchVersion(), board.getMatchlength());

						JibsNewGameData jibsNewGameData = jibsNewGameData(
								game.getPlayerX(), game.getPlayerO());
						player.getGame().getBackgammonBoard()
								.setDice1(jibsNewGameData.getDice1());
						player.getGame().getBackgammonBoard()
								.setDice2(jibsNewGameData.getDice2());
						player.getGame().getBackgammonBoard()
								.setDice3(jibsNewGameData.getDice3());
						player.getGame().getBackgammonBoard()
								.setDice4(jibsNewGameData.getDice4());
						player.getGame().getBackgammonBoard()
								.setJoinPlayer1(null);
						player.getGame().getBackgammonBoard()
								.setJoinPlayer2(null);
						// player.startGame(server.getJibsServer(),
						// jibsNewGameData, game, player.getGame()
						// .getBackgammonBoard().getPlayerX(),
						// player.getGame().getBackgammonBoard()
						// .getPlayerO(), matchlength,
						// jibsNewGameData.getTurn(), matchVersion,
						// board.getMayDouble1(), board.getMayDouble2());
					}
				}
			}
		} else {
			return doJoinForNewGame(player, args);
		}

		return true;
	}

	private boolean doJoinForNewGame(Player player, String[] args) {
		JibsMessages jibsMessages = server.getJibsMessages();
		Player opponent = null;
		int matchlength = 0;
		JibsMatch matchVersion = null;
		JibsWriter out = player.getOutputStream();
		JibsGame game = player.getGame();

		if (args.length <= 1) {
			// m_join_no_user=** Error: Join who?
			String msg = jibsMessages.convert("m_join_no_user");
			out.println(msg);
		} else {
			opponent = ClientWorker.getPlayer(server, args[1]);

			if (opponent == null) {
				// m_join_no_invite=** %0 didn't invite you.
				Object[] obj = { args[1] };
				String msg = jibsMessages.convert("m_join_no_invite", obj);
				out.println(msg);
			} else {
				player.setOpponent(opponent);
				opponent.setOpponent(player);

				JibsQuestion question = opponent.getQuestion();

				if (question instanceof ResumeQuestion) {
					informNewGame(null, player, opponent);

					ResumeQuestion resumeQuestion = (ResumeQuestion) question;
					SavedGameParam resumeData = resumeQuestion.getResumeData();
					JibsGame resumeGame = JibsGame.constructGame(
							server.getJibsServer(), resumeData);
					BackgammonBoard board = resumeGame.getBackgammonBoard();
					resumeGame.setJibsServer(server.getJibsServer());

					int startturn = board.getTurn();
					int dice1 = board.getDice1();
					int dice2 = board.getDice2();
					int player1Points = board.getPlayerXPoints();
					int player2Points = board.getPlayerOPoints();

					player.setGame(resumeGame);
					opponent.setGame(resumeGame);
					resumeGame.resumeGame(resumeData, startturn, dice1, dice2,
							player1Points, player2Points);
				}

				if (question instanceof InviteQuestion) {
					InviteQuestion inviteQuestion = (InviteQuestion) question;
					matchlength = inviteQuestion.getMatchLength();
					matchVersion = inviteQuestion.getMatchVersion();

					informNewGame(game, player, opponent);

					// m_player_joined=** Player %0 has joined you for a %1
					// point match.
					Object[] obj = new Object[] { player.getName(), matchlength };
					String msg = jibsMessages.convert("m_player_joined", obj);
					opponent.getOutputStream().println(msg);
					greet(player, opponent, matchVersion, matchlength);

					JibsNewGameData jibsNewGameData = jibsNewGameData(player,
							opponent); // PlayerX:player, PlayerO:opponent

					game = new JibsGame(server.getJibsServer(),
							jibsNewGameData.getPlayerX(),
							jibsNewGameData.getPlayerO(), matchlength, 0, 0,
							matchVersion, jibsNewGameData.getTurn());
					player.setGame(game);
					opponent.setGame(game);

					BackgammonBoard board = game.getBackgammonBoard();
					board.setDice1(jibsNewGameData.getDice1());
					board.setDice2(jibsNewGameData.getDice2());
					board.setDice3(jibsNewGameData.getDice3());
					board.setDice4(jibsNewGameData.getDice4());
					game.setBackgammonBoard(board);
					JibsGame.deleteGames(server.getJibsServer(),
							jibsNewGameData.getPlayerX(),
							jibsNewGameData.getPlayerO());
					// m_start_match=%0 and %1 start a new %2-point match.
					obj = new Object[] {
							jibsNewGameData.getPlayerX().getName(),
							jibsNewGameData.getPlayerO().getName(), matchlength };
					msg = jibsMessages.convert("m_start_match", obj);
					JibsTextArea.log(server.getJibsServer(), msg, true);

					jibsNewGameData.startGame(server.getJibsServer(),
							jibsNewGameData, game,
							jibsNewGameData.getPlayerX(),
							jibsNewGameData.getPlayerO(), matchlength,
							jibsNewGameData.getTurn(), matchVersion, 1, 1);
				}
			}
		}

		return true;
	}

	public void greet(Player player1, Player player2, JibsMatch matchVersion,
			int matchlength) {
		JibsMessages jibsMessages = server.getJibsMessages();
		String playerName = player1.getName();
		String oppplayerName = player2.getName();
		JibsWriter out1 = player1.getOutputStream();
		JibsWriter out2 = player2.getOutputStream();

		// m_start_match_with=Starting a new game with %0.
		Object[] obj = { oppplayerName };
		String msg = jibsMessages.convert("m_start_match_with", obj);
		out1.println(msg);

		if (matchVersion.getVersion() == JibsMatch.nPointMatch) {
			// m_start_match_with=Starting a new game with %0.
			obj = new Object[] { playerName };
			msg = jibsMessages.convert("m_start_match_with", obj);
			out2.println(msg);

			// m_start_match=%0 and %1 start a new %2-point match.
			oppplayerName = player2.getName();
			obj = new Object[] { playerName, oppplayerName,
					Integer.valueOf(matchlength) };
			msg = jibsMessages.convert("m_start_match", obj);
			player1.informPlayers(msg, player2);
			// message needs to be sent also to both watcher lists
			player1.informWatcher("m_start_match", obj, true);
			player2.informWatcher("m_start_match", obj, true);
		} else {
			// m_you_play_unlimited=** You are now playing an unlimited match
			// with %1
			obj = new Object[] { playerName };
			msg = jibsMessages.convert("m_you_play_unlimited", obj);
			out2.println(msg);
			// m_start_match_unlimited=%0 and %1 start an unlimited match.
			oppplayerName = player2.getName();
			obj = new Object[] { playerName, oppplayerName };
			msg = jibsMessages.convert("m_start_match_unlimited", obj);
			player1.informPlayers(msg, player2);
		}
	}

	private JibsNewGameData jibsNewGameData(Player player, Player opponent) {
		JibsNewGameData jngd = determineStartPlayer(player, opponent);
		return jngd;
	}

	private void informNewGame(JibsGame game, Player player, Player opponent) {
		informPlayerOfNewGame(player, opponent);
		informPlayerOfNewGame(opponent, player);
	}

	private void informPlayerOfNewGame(Player player, Player opponent) {
		JibsWriter out = player.getOutputStream();
		StringBuilder builder = new StringBuilder();
		builder.append(ClipConstants.CLIP_WHO_INFO + " ");
		String whoinfo = Player.whoinfo(player);
		builder.append(whoinfo);
		out.println(builder.toString());

		builder = new StringBuilder();
		builder.append(ClipConstants.CLIP_WHO_INFO + " ");
		whoinfo = Player.whoinfo(opponent);
		builder.append(whoinfo);
		out.println(builder.toString());

		if (player.canCLIP()) {
			out.println(ClipConstants.CLIP_WHO_END + " ");
		}
	}

	private JibsNewGameData determineStartPlayer(Player playerX, Player playerO) {
		// -1 if it's X's turn, +1 if it's O's turn 0 if the game
		// is over
		JibsMessages jibsMessages = server.getJibsMessages();

		JibsWriter outX = playerX.getOutputStream();
		JibsWriter outO = playerO.getOutputStream();
		String playerNameX = playerX.getName();
		String playerNameO = playerO.getName();
		String msg = null;
		Object[] obj = null;
		int startTurn = 0;
		int dice1 = 0;
		int dice2 = 0;
		int dice3 = 0;
		int dice4 = 0;
		do {
			dice1 = Die.roll(server.getJibsServer().getJibsRandom());
			dice2 = Die.roll(server.getJibsServer().getJibsRandom());
//			dice1 = 5; // joining Player
//			dice2 = 4; // inviting Player

			// m_both_roll=%0 rolled %1, %2 rolled %3
			obj = new Object[] { "You", dice1, playerNameO, dice2 };
			msg = jibsMessages.convert("m_both_roll", obj);
			outX.println(msg);
			// m_both_roll=%0 rolled %1, %2 rolled %3
			obj = new Object[] { playerX.getName(), dice1, playerNameO, dice2 };
			playerX.informWatcher("m_both_roll", obj, true);
			playerO.informWatcher("m_both_roll", obj, true);
			// m_both_roll=%0 rolled %1, %2 rolled %3
			obj = new Object[] { "You", dice2, playerNameX, dice1 };
			msg = jibsMessages.convert("m_both_roll", obj);
			outO.println(msg);
		} while (dice1 == dice2);

		if (dice1 > dice2) {
			// m_your_turn=It's your turn to move.
			msg = jibsMessages.convert("m_your_turn");
			outX.println(msg);
			// m_makes_first_move=%0 makes the first move.
			obj = new Object[] { playerNameX };
			msg = jibsMessages.convert("m_makes_first_move", obj);
			outO.println(msg);
			playerO.informWatcher("m_makes_first_move", obj, true);
			playerX.informWatcher("m_makes_first_move", obj, true);
			dice3 = 0;
			dice4 = 0;
			startTurn = -1;
		} else {
			// m_your_turn=It's your turn to move.
			msg = jibsMessages.convert("m_your_turn");
			outO.println(msg);
			// m_makes_first_move=%0 makes the first move.
			obj = new Object[] { playerNameO, };
			msg = jibsMessages.convert("m_makes_first_move", obj);
			outX.println(msg);
			playerX.informWatcher("m_makes_first_move", obj, true);
			playerO.informWatcher("m_makes_first_move", obj, true);
			startTurn = 1;
			dice3 = dice2;
			dice4 = dice1;
			dice1 = 0;
			dice2 = 0;
		}
		return new JibsNewGameData(playerX, playerO, startTurn, dice1, dice2,
				dice3, dice4);
	}
}
