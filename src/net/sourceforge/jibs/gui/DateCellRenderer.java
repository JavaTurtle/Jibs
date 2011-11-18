package net.sourceforge.jibs.gui;

import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DateCellRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = -2689422704018338553L;
    DateFormat sdf = new SimpleDateFormat("MM-dd-yyyy/HH:mm:ss");
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus, int row,
                                                   int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                                            row, column);

        if (value instanceof Date) {
            String strDate = sdf.format((Date) value);
            this.setText(strDate);
        }
        return this;
    }
}
