package net.sourceforge.jibs.backgammon;

public enum JibsTurn {
	Player0(1), PlayerX(-1);
	private int turn;

	private JibsTurn(int turn) {
		this.turn = turn;
	}

	public int getTurn() {
		return turn;
	}
}
