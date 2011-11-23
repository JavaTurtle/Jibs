package net.sourceforge.jibs.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import net.sourceforge.jibs.server.JibsServer;

import org.apache.log4j.Logger;

public class JibsWindow implements WindowListener, ActionListener {
	private static Logger logger = Logger.getLogger(JibsWindow.class);
	private JibsServer jibsServer;

	public JibsWindow(JibsServer jibsServer) {
		this.jibsServer = jibsServer;
	}

	public void windowOpened(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		jibsServer.stopServerMenuItemActionPerformed(null);
		logger.info("Server exited.");
		System.exit(0);
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void actionPerformed(ActionEvent e) {
	}
}
