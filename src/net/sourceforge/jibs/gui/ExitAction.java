package net.sourceforge.jibs.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import net.sourceforge.jibs.server.JibsServer;

public class ExitAction extends AbstractAction {
	private static final long serialVersionUID = 4654976179773232277L;
	private JibsServer jibsServer;

	public ExitAction(JibsServer jibsServer, String text, ImageIcon icon,
			String desc, Object mnemonic) {
		super(text, icon);
		this.jibsServer = jibsServer;
		putValue(SHORT_DESCRIPTION, desc);
		putValue(ACCELERATOR_KEY, mnemonic);
	}

	public void actionPerformed(ActionEvent e) {
		jibsServer.stopServerMenuItemActionPerformed(true, true);
	}
}
