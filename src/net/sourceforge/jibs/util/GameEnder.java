package net.sourceforge.jibs.util;

import net.sourceforge.jibs.server.JibsServer;
import net.sourceforge.jibs.server.Player;

public class GameEnder extends Thread {
    private Player playerX;
    private Player playerO;
    private JibsServer jibsServer;

    public GameEnder(JibsServer jibsServer, Player playerX, Player playerO) {
        this.playerX = playerX;
        this.playerO = playerO;
        this.jibsServer = jibsServer;
    }

    public void run() {
        try {
            Thread.sleep(200);
            playerX.endGame(playerO);
            playerX.setGame(null);
            playerO.endGame(playerX);
            playerO.setGame(null);
        } catch (InterruptedException e) {
            jibsServer.logException(e);
        }
    }
}
