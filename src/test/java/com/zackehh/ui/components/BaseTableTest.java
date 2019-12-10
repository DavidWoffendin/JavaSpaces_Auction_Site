package com.zackehh.ui.components;

import org.testng.annotations.Test;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.util.Vector;

import static org.testng.Assert.*;

public class BaseTableTest {

    private Object[] columnArr = { "Column One", "Column Two", "Column Three" };
    private Object[][] dataArr = { new Object[] { "One", "Two", "Three" } };
    private Vector<String> columnVec = new Vector<String>(){{
        add("Column One");
        add("Column Two");
        add("Column Three");
    }};
    private Vector<Vector<String>> dataVec = new Vector<Vector<String>>(){{
        add(new Vector<String>(){{
            add("One");
            add("Two");
            add("Three");
        }});
    }};

    @Test
    public void testBaseTableWithArrays() throws Exception {
        BaseTable t = new BaseTable(dataArr, columnArr);

        assertTrue(t.getShowHorizontalLines());
        assertTrue(t.getRowSelectionAllowed());

        DefaultTableCellRenderer renderer = ((DefaultTableCellRenderer) t.getDefaultRenderer(String.class));

        assertEquals(renderer.getHorizontalAlignment(), JLabel.CENTER);

        JTableHeader tableHeader1 = t.getTableHeader();

        assertFalse(tableHeader1.getReorderingAllowed());
        assertFalse(tableHeader1.getResizingAllowed());

        DefaultTableModel tableModel = (DefaultTableModel) t.getModel();

        assertEquals(tableModel.getRowCount(), 1);
        assertEquals(tableModel.getColumnCount(), 3);
        assertEquals(tableModel.getValueAt(0, 1), "Two");
    }

    @Test
    public void testBaseTableWithVectors() throws Exception {
        BaseTable t = new BaseTable(dataVec, columnVec);

        assertTrue(t.getShowHorizontalLines());
        assertTrue(t.getRowSelectionAllowed());

        DefaultTableCellRenderer renderer = ((DefaultTableCellRenderer) t.getDefaultRenderer(String.class));

        assertEquals(renderer.getHorizontalAlignment(), JLabel.CENTER);

        JTableHeader tableHeader1 = t.getTableHeader();

        assertFalse(tableHeader1.getReorderingAllowed());
        assertFalse(tableHeader1.getResizingAllowed());

        DefaultTableModel tableModel = (DefaultTableModel) t.getModel();

        assertEquals(tableModel.getRowCount(), 1);
        assertEquals(tableModel.getColumnCount(), 3);
        assertEquals(tableModel.getValueAt(0, 1), "Two");
    }

    @Test
    public void testBaseTableUneditableModelWithArrays() throws Exception {
        BaseTable t = new BaseTable(dataArr, columnArr);

        DefaultTableModel tableModel = (DefaultTableModel) t.getModel();

        for(int i = 0; i < columnArr.length; i++) {
            assertEquals(tableModel.getColumnClass(i), String.class);
        }

        assertFalse(tableModel.isCellEditable(0, 1));
    }

    @Test
    public void testBaseTableUneditableModelWithVectors() throws Exception {
        BaseTable t = new BaseTable(dataVec, columnVec);

        DefaultTableModel tableModel = (DefaultTableModel) t.getModel();

        for(int i = 0; i < columnVec.size(); i++) {
            assertEquals(tableModel.getColumnClass(i), String.class);
        }

        assertFalse(tableModel.isCellEditable(0, 1));
    }

}