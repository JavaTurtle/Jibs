package net.sourceforge.jibs.util;

import net.sourceforge.jibs.backgammon.BackgammonBoard;
import net.sourceforge.jibs.backgammon.JibsGame;
import net.sourceforge.jibs.backgammon.JibsMatch;
import net.sourceforge.jibs.server.JibsServer;
import net.sourceforge.jibs.server.Player;

public class JibsNewGameData {
	private Player playerX;
	private Player playerO;
	private int die1Value;
	private int die2Value;
	private int turn;

	public JibsNewGameData(Player playerX, Player playerO, int startTurn,
			int die1, int die2) {
		this.playerO = playerO;
		this.playerX = playerX;
		this.die1Value = die1;
		this.die2Value = die2;
		this.turn = startTurn;
	}

	public Player getPlayerO() {
		return playerO;
	}

	public void setPlayerO(Player player) {
		this.playerO = player;
	}

	public Player getPlayerX() {
		return playerX;
	}

	public void setPlayerX(Player player) {
		this.playerX = player;
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public int getDie1Value() {
		return die1Value;
	}

	public void setDie1Value(int die1Value) {
		this.die1Value = die1Value;
	}

	public int getDie2Value() {
		return die2Value;
	}

	public void setDie2Value(int die2Value) {
		this.die2Value = die2Value;
	}

	public void startGame(JibsServer jibsServer, JibsNewGameData jngd,
			JibsGame game, Player player1, Player player2, int length,
			int turn, JibsMatch matchVersion, int mayDouble1, int mayDouble2) {
		BackgammonBoard board = game.getBackgammonBoard();
		game.startGame(turn, board.getPlayerXdie1Value(),
				board.getPlayerXdie2Value(), board.getPlayerXPoints(),
				board.getPlayerOPoints(), mayDouble1, mayDouble2);
	}

}
