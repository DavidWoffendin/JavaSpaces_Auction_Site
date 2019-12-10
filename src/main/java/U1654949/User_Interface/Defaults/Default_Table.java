package U1654949.User_Interface.Defaults;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.util.Vector;

public class Default_Table extends JTable {


    public Default_Table(Vector<Vector<String>> data, Vector<String> columns){
        setModel(new UneditableTableModel(data, columns));
        init();
    }

    public Default_Table(Object[][] data, Object[] columns){
        setModel(new UneditableTableModel(data, columns));
        init();
    }

    private void init(){
        setShowHorizontalLines(true);
        setRowSelectionAllowed(true);
        setDefaultRenderer(String.class, new DefaultTableCellRenderer() {{
            setHorizontalAlignment(JLabel.CENTER);
        }});

        JTableHeader tableHeader = getTableHeader();

        tableHeader.setReorderingAllowed(false);
        tableHeader.setResizingAllowed(false);

        DefaultTableCellRenderer renderer =
                (DefaultTableCellRenderer) tableHeader.getDefaultRenderer();

        renderer.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private class UneditableTableModel extends DefaultTableModel {

        public UneditableTableModel(Vector<Vector<String>> data, Vector<String> columns){
            super(data, columns);
        }

        public UneditableTableModel(Object[][] data, Object[] columns){
            super(data, columns);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public Class getColumnClass(int column) {
            return String.class;
        }

    }

}
