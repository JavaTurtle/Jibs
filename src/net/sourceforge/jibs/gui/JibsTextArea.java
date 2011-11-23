package net.sourceforge.jibs.gui;

import java.text.DateFormat;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import net.sourceforge.jibs.server.JibsServer;

import org.apache.log4j.Logger;

public class JibsTextArea extends JTextArea {
	private static final long serialVersionUID = 7806501667238028220L;
	private static Logger logger = Logger.getLogger(JibsTextArea.class);

	public JibsTextArea(JibsServer jibsServer, int r, int c) {
		super(r, c);
	}

	public static void log(JibsServer jibsServer, String s, boolean addLog) {
		java.util.Date today = new java.util.Date();
		String myDate = DateFormat.getDateInstance().format(today);
		String myClock = DateFormat.getTimeInstance().format(today);

		if (jibsServer.useSwing()) {
			jibsServer.getTextArea().append(
					myDate + ":" + myClock + "\t" + s + "\r\n");
			jibsServer.getTextArea().setCaretPosition(
					jibsServer.getTextArea().getText().length());
		} else {
			System.out.println(s);
		}

		if (addLog) {
			logger.info(s);
		}
	}

	public static void append(JibsServer jibsServer, String s) {
		Document doc = jibsServer.getTextArea().getDocument();

		if (doc != null) {
			try {
				doc.insertString(doc.getLength(), s, null);
			} catch (BadLocationException e) {
				jibsServer.logException(e);
			}
		}
	}

	public void log(String s, boolean bWithDate) {
		if (s == null) {
			s = "null";
		}

		java.util.Date today = new java.util.Date();

		if (bWithDate) {
			String myDate = DateFormat.getDateInstance().format(today);
			String myClock = DateFormat.getTimeInstance().format(today);

			append(myDate + ":" + myClock + ":" + s + "\r\n");
			setCaretPosition(getText().length());
		} else {
			append(s + "\n");
			setCaretPosition(getText().length());
		}
	}
}
