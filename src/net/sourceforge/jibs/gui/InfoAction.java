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
	private JFrame jibsFrame;

	public InfoAction(JFrame jibsFrame, Server server, String text,
			ImageIcon icon, String desc, Object mnemonic) {
		super(text, icon);
		this.server = server;
		this.jibsFrame = jibsFrame;
		putValue(SHORT_DESCRIPTION, desc);
		putValue(ACCELERATOR_KEY, mnemonic);
	}

	public void actionPerformed(ActionEvent e) {
		JDialog dialog = new JDialog(jibsFrame, "jIBS", true);
		VMPanel vmPanel = new VMPanel(dialog, server);
		dialog.add(vmPanel);
		vmPanel.doShow();
		dialog.pack();
		dialog.setLocationRelativeTo(jibsFrame);
		dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		dialog.setVisible(true);
	}
}
