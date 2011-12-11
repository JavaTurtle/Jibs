package net.sourceforge.jibs.gui;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import net.sourceforge.jibs.server.JibsConfiguration;
import net.sourceforge.jibs.server.JibsServer;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.form.FormAccessor;

public class JibsGui extends FormPanel {
	private enum JibsColumn {
		Name, Rating, Experience, Last_Login_Date, E_Mail, isAdmin, Last_Logout_Date, Password, Last_Login_Host;
	};

	private static final long serialVersionUID = 3800005237698172527L;
	private JTextArea txtArea;
	private JTextField txtPlayer;
	private JLabel lblTime;
	private JibsUserTableModel jibsUserTableModel;
	private JButton btnInfo;
	private JButton btnExit;
	private JButton btnReload;
	private JButton btnStart;
	private JButton btnStop;
	private JButton btnClear;

	public JibsGui(JibsConfiguration configuration, JibsServer jibsServer) {
		super("frm/jibs.jfrm");

		// switch the Textarea with my implementation JibsTextArea
		FormAccessor console = getFormAccessor("playerconsole");
		txtArea = (JTextArea) console.getTextComponent("txtArea");

		String maxLines = configuration.getResource("maxConsoleLines");
		JibsDocument doc = new JibsDocument(Integer.valueOf(maxLines));
		txtArea.setDocument(doc);
		jibsServer.setDoc(doc);

		// get components from the statusbar
		FormAccessor status = getFormAccessor("status");
		txtPlayer = status.getTextField("player");
		lblTime = status.getLabel("time");
		// switch the JTable into proper format
		JTable table = getTable("jibsTable");
		table.setAutoCreateRowSorter(true);
		jibsUserTableModel = new JibsUserTableModel();
		table.setModel(jibsUserTableModel);
		// Highest ranking first
		table.getRowSorter().toggleSortOrder(JibsColumn.Rating.ordinal());
		table.getRowSorter().toggleSortOrder(JibsColumn.Rating.ordinal());
		int[] widhts = { 210, 60, 60, 230, 180, 60, 160, 160, 160 };

		for (int i = 0; i < jibsUserTableModel.getColumnCount(); i++) {
			TableColumn column = table.getColumnModel().getColumn(i);

			if (i == JibsColumn.Last_Login_Date.ordinal()) {
				column.setCellRenderer(new DateCellRenderer());
			}

			if (i == JibsColumn.Last_Logout_Date.ordinal()) {
				column.setCellRenderer(new DateCellRenderer());
			}

			column.setPreferredWidth(widhts[i]);
		}
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		table.getColumnModel().getColumn(JibsColumn.Name.ordinal())
				.setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(JibsColumn.Rating.ordinal())
				.setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(JibsColumn.Experience.ordinal())
				.setCellRenderer(centerRenderer);

		// ease adding actions later on
		btnInfo = (JButton) getButton("btnInfo");
		btnExit = (JButton) getButton("btnExit");
		btnReload = (JButton) getButton("btnReload");
		btnStart = (JButton) getButton("btnStart");
		btnStop = (JButton) getButton("btnStop");
	}

	public JTextArea getTextArea() {
		return txtArea;
	}

	public JLabel getStatusDate() {
		return lblTime;
	}

	public JTextField getStatusPlayer() {
		return txtPlayer;
	}

	public JibsUserTableModel getUserTableModel() {
		return jibsUserTableModel;
	}

	public AbstractButton getBtnReload() {
		return btnReload;
	}

	public AbstractButton getBtnInfo() {
		return btnInfo;
	}

	public AbstractButton getBtnExit() {
		return btnExit;
	}

	public AbstractButton getBtnStart() {
		return btnStart;
	}

	public AbstractButton getBtnStop() {
		return btnStop;
	}

	public JButton getBtnClear() {
		return btnClear;
	}

	public void setBtnClear(JButton btnClear) {
		this.btnClear = btnClear;
	}
}
