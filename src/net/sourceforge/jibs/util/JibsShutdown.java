package net.sourceforge.jibs.util;

import java.awt.event.ActionEvent;
import java.util.Date;

import net.sourceforge.jibs.gui.ExitAction;
import net.sourceforge.jibs.server.JibsServer;

public class JibsShutdown extends Thread {
    private JibsServer jibsServer;
    private int delay;
    private boolean bRestart;
    private Date shutdownDate;

    public JibsShutdown(JibsServer jibsServer, boolean bRestart, int msecs) {
        this.jibsServer = jibsServer;
        this.delay = msecs;
        this.bRestart = bRestart;
        shutdownDate = new Date(new Date().getTime() + msecs);
    }

    public void run() {
        try {
            Thread.sleep(delay);
            jibsServer.stopServerMenuItemActionPerformed(null);

            ExitAction exitAction = new ExitAction(jibsServer, "Exit", null,
                                                   "", null);

            if (bRestart) {            	
                String restart = jibsServer.getServer().getConfiguration().getResource("Restart");
                ActionEvent evt = new ActionEvent(Integer.valueOf(restart), 0,
                                                  "exit");
                exitAction.actionPerformed(evt);
            } else {
                // don't restart
                ActionEvent evt = new ActionEvent(Integer.valueOf(0), 0, "exit");
                exitAction.actionPerformed(evt);
            }
        } catch (InterruptedException e) {
            // don't log the exception on purpose
            // jibsServer.logException(e);
        }
    }

    public Date getShutdownDate() {
        return shutdownDate;
    }
}
