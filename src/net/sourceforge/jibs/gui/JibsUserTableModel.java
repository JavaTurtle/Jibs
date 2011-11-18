/*
 * Created on 22.02.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.sourceforge.jibs.gui;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.jibs.server.Player;

public class JibsUserTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 4658136755471549320L;
	private Vector<String> columnIdentifiers = new Vector<String>();
	private Vector<Player> dataVector = new Vector<Player>();
	@SuppressWarnings({ "rawtypes", "unused" })
	private Class[] types = new Class[] { String.class, // Name
			Double.class, // Rating
			Integer.class, // Experience
			Date.class, // Login
			String.class, // Email
			Boolean.class, // Admin
			Date.class, // Logout
			String.class, // Password
			String.class }; // Host

	public JibsUserTableModel() {
		columnIdentifiers.add("Name");
		columnIdentifiers.add("Rating");
		columnIdentifiers.add("Experience");
		columnIdentifiers.add("Last Login Date");
		columnIdentifiers.add("E-Mail");
		columnIdentifiers.add("isAdmin");
		columnIdentifiers.add("Last Logout Date");
		columnIdentifiers.add("Password");
		columnIdentifiers.add("Last Login Host");
	}

	public void deleteUser(Player player) {
		int index = dataVector.indexOf(player);

		if (index >= 0) {
			dataVector.removeElement(player);
			fireTableRowsDeleted(index, index);
		}
	}

	public Class<?> getColumnClass(int columnIndex) {
		// return types[columnIndex];
		switch (columnIndex) {
		case 0: // Name
			return String.class;
		case 1: // Rating
			return Double.class;
		case 2: // Experience
			return Integer.class;
		case 3: // Last_login_date
			return Date.class;
		case 4: // E-Mail
			return String.class;
		case 5: // isAdmin
			return Boolean.class;
		case 6: // Last_logout_date
			return Date.class;
		case 7: // Password
			return String.class;
		case 8: // Last_Login_Host
			return String.class;

		}

		return Object.class;
	}

	public String getColumnName(int column) {
		return (String) columnIdentifiers.get(column);
	}

	public Object getValueAt(int row, int column) {
		Player player = dataVector.get(row);

		switch (column) {
		case 0: // Name
			return player.getName();

		case 1: // Rating
			return Double.valueOf(player.getRating());

		case 2: // Experience
			return Integer.valueOf(player.getExperience());

		case 3: // Last_login_date
			Timestamp last_login_date = player.getLast_login_date();
			return last_login_date;

		case 4: // E-Mail
			return player.getEmail();

		case 5: // isAdmin
			return Boolean.valueOf(player.getAdmin());

		case 6: // Last_logout_date
			Timestamp last_logout_date = player.getLast_logout_date();
			return last_logout_date;

		case 7: // Password
			return player.getPassword();

		case 8: // Last_Login_Host
			return player.getLast_login_host();
		}

		return "null";
	}

	public int getColumnCount() {
		return columnIdentifiers.size();
	}

	public int getRowCount() {
		return dataVector.size();
	}

	public boolean isCellEditable(int row, int column) {
		return false;
	}

	public Vector<Player> getDataVector() {
		return dataVector;
	}
}
