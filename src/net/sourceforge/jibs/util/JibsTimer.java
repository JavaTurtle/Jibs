package net.sourceforge.jibs.util;

import net.sourceforge.jibs.gui.JibsMessages;
import net.sourceforge.jibs.gui.JibsTextArea;
import net.sourceforge.jibs.server.ClientWorker;
import net.sourceforge.jibs.server.JibsServer;
import net.sourceforge.jibs.server.Player;

public class JibsTimer implements Runnable {
    private JibsServer jibsServer;
    private ClientWorker cw;
    private Player player;
    private long millis;

    public JibsTimer(JibsServer jibsServer, ClientWorker cw, Player player,
                     long millis) {
        this.jibsServer = jibsServer;
        this.cw = cw;
        this.player = player;
        this.millis = millis;
    }

    public void run() {
        try {
            Thread.sleep(millis);

            JibsMessages jm = jibsServer.getJibsMessages();
            Object[] obj = null;
            String msg = null;

            if (player != null) {
                obj = new Object[] { player.getName() };
            } else {
                obj = new Object[] { "\"<Unknown>\"" };
            }

            // m_client_inactive="** Client '%0' was inactive too long.
            msg = jm.convert("m_client_inactive", obj);
            JibsTextArea.log(jibsServer, msg, true);
            cw.executeCmd("bye");
        } catch (InterruptedException e) {
            // jibsServer.logException(e);
        }
    }
}
