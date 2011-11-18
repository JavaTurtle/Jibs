package net.sourceforge.jibs.util;

import java.sql.Timestamp;
import java.util.Date;

import net.sourceforge.jibs.backgammon.BackgammonBoard;
import net.sourceforge.jibs.backgammon.JibsGame;
import net.sourceforge.jibs.backgammon.JibsMatch;
import net.sourceforge.jibs.server.Player;

public class SavedGameParam {
    private Long id;
    private String player_A;
    private String player_B;
    private Timestamp savedDate;
    private String board;
    private Integer matchlength;
    private int turn;
    private Integer matchVersion;
    private Player player1;
    private Player player2;

    public SavedGameParam() {
    }

    public SavedGameParam(String name1, String name2) {
        this.player_A = name1;
        this.player_B = name2;
    }

    public SavedGameParam(JibsGame game, Player playerX, Player playerO,
                          BackgammonBoard backgammonBoard, int matchlength2,
                          int jibsTurn, JibsMatch matchVersion2, Date date) {
        this.player1 = playerX;
        this.player2 = playerO;
        this.player_A = playerX.getName();
        this.player_B = playerO.getName();
        this.savedDate = new Timestamp(date.getTime());
        this.board = backgammonBoard.getOutputBoard(game);
        this.matchlength = matchlength2;
        this.turn = jibsTurn;
        this.matchVersion = matchVersion2.getVersion();
    }

    public Date getSavedDate() {
        return savedDate;
    }

    public void setSavedDate(Date savedDate) {
        this.savedDate = new Timestamp(savedDate.getTime());
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public Integer getMatchlength() {
        return matchlength;
    }

    public void setMatchlength(Integer matchlength) {
        this.matchlength = matchlength;
    }

    public Integer getMatchVersion() {
        return matchVersion;
    }

    public void setMatchVersion(Integer matchVersion) {
        this.matchVersion = matchVersion;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getPlayer_A() {
        return player_A;
    }

    public void setPlayer_A(String playerA) {
        this.player_A = playerA;
    }

    public String getPlayer_B() {
        return player_B;
    }

    public void setPlayer_B(String playerB) {
        this.player_B = playerB;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer1(Player player) {
        this.player1 = player;
    }

    public void setPlayer2(Player player) {
        this.player2 = player;
    }

    public Player getPlayerX() {
        if (player_A.equalsIgnoreCase(player1.getName())) {
            return player1;
        }

        if (player_A.equalsIgnoreCase(player2.getName())) {
            return player2;
        }

        return null;
    }

    public Player getPlayerO() {
        if (player_B.equalsIgnoreCase(player2.getName())) {
            return player2;
        }

        if (player_B.equalsIgnoreCase(player1.getName())) {
            return player1;
        }

        return null;
    }
}
