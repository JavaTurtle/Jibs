package net.sourceforge.jibs.util;

import net.sourceforge.jibs.backgammon.BackgammonBoard;
import net.sourceforge.jibs.backgammon.JibsGame;
import net.sourceforge.jibs.backgammon.JibsMatch;
import net.sourceforge.jibs.server.JibsServer;
import net.sourceforge.jibs.server.Player;

public class JibsNewGameData {
	private Player playerX;
	private Player playerO;
	private int dice1;
	private int dice2;
	private int dice3;
	private int dice4;
	private int turn;

	public JibsNewGameData(Player playerX, Player playerO, int startTurn,
			int die1, int die2, int die3, int die4) {
		this.playerO = playerO;
		this.playerX = playerX;
		this.dice1 = die1;
		this.dice2 = die2;
		this.dice3 = die3;
		this.dice4 = die4;
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

	public int getDice1() {
		return dice1;
	}

	public void setDice1(int dice1) {
		this.dice1 = dice1;
	}

	public int getDice2() {
		return dice2;
	}

	public void setDice2(int dice2) {
		this.dice2 = dice2;
	}

	public int getDice3() {
		return dice3;
	}

	public void setDice3(int dice3) {
		this.dice3 = dice3;
	}

	public int getDice4() {
		return dice4;
	}

	public void setDice4(int dice4) {
		this.dice4 = dice4;
	}

	public void startGame(JibsServer jibsServer, JibsNewGameData jngd,
			JibsGame game, Player player1, Player player2, int length,
			int turn, JibsMatch matchVersion, int mayDouble1, int mayDouble2) {
		BackgammonBoard board = game.getBackgammonBoard();
		game.startGame(turn, board.getDice1(),
				board.getDice2(), board.getDice3(),board.getDice4(), board.getPlayerXPoints(),
				board.getPlayerOPoints(), mayDouble1, mayDouble2);
	}

}
