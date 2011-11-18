package net.sourceforge.jibs.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;

import net.sourceforge.jibs.server.Server;

public class InfoAction extends AbstractAction {
    private static final long serialVersionUID = 4654976179773232277L;
    private Server server;

    public InfoAction(Server server, String text, ImageIcon icon,
                      String desc, Object mnemonic) {
        super(text, icon);
        this.server = server;
        putValue(SHORT_DESCRIPTION, desc);
        putValue(ACCELERATOR_KEY, mnemonic);

        // putValue(MNEMONIC_KEY, mnemonic);
    }

    public void actionPerformed(ActionEvent e) {
        JDialog dialog = new JDialog((JFrame)null, "Parameter", true);

        dialog.add(new VMPanel(dialog, server));

        VMPanel panel = (VMPanel) dialog.getContentPane().getComponent(0);
        panel.doShow();
        dialog.pack();
        dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);
    }
}
