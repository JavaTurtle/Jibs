package net.sourceforge.jibs.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import net.sourceforge.jibs.server.JibsServer;

import org.apache.log4j.Logger;

public class ExitAction extends AbstractAction {
    private static Logger logger = Logger.getLogger(ExitAction.class);
    private static final long serialVersionUID = 4654976179773232277L;
    private JibsServer jibsServer;

    public ExitAction(JibsServer jibsServer, String text, ImageIcon icon,
                      String desc, Object mnemonic) {
        super(text, icon);
        this.jibsServer = jibsServer;
        putValue(SHORT_DESCRIPTION, desc);
        putValue(ACCELERATOR_KEY, mnemonic);

        // putValue(MNEMONIC_KEY, mnemonic);
    }

    public void actionPerformed(ActionEvent e) {
        boolean bStop = false;
        String warnExit = jibsServer.getConfiguration().getResource("warnExit");

        if (jibsServer.runs()) {
            if (Boolean.valueOf(warnExit).booleanValue()) {
                int option = JOptionPane.showConfirmDialog(null,
                                                           "There are still users connected.\n" +
                                                           "Do you really want to exit?",
                                                           "Exit",
                                                           JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.YES_OPTION) {
                    bStop = true;
                }
            } else {
                bStop = true;
            }
        } else {
            bStop = true;
        }

        if (bStop) {
            jibsServer.stopServerMenuItemActionPerformed(null);
            logger.info("Server exited.");

            if (e.getSource() instanceof Integer) {
                int retCode = Integer.valueOf(e.getSource().toString());
                System.exit(retCode);
            } else {
                System.exit(0);
            }
        }
    }
}
