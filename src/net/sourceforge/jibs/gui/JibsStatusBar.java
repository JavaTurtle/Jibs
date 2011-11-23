package net.sourceforge.jibs.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import net.sourceforge.jibs.server.JibsServer;

public class JibsStatusBar extends JPanel {
	private static final long serialVersionUID = 7317884893631435720L;
	private JLabel txtDate = null;
	private JTextField txtPlayer = null;
	private Timer timer = null;

	public JibsStatusBar(JibsServer jibsServer) {
		txtDate = jibsServer.getJibsGUI().getStatusDate();
		txtPlayer = jibsServer.getJibsGUI().getStatusPlayer();
		timer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Date dt = new Date();
						DateFormat df = DateFormat
								.getTimeInstance(DateFormat.MEDIUM);
						String txt = df.format(dt);
						txtDate.setText(txt);
					}
				});
			}
		});
		timer.start();
	}

	public JTextField getPlayerLabel() {
		return txtPlayer;
	}
}
