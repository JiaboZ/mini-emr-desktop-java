package com.miniehr.ui;

import com.miniehr.dao.JdbcPatientDao;
import com.miniehr.model.Patient;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class PatientPickerDialog extends JDialog {

    private final JdbcPatientDao dao = new JdbcPatientDao();

    private final JTextField searchField = new JTextField(25);
    private final JButton searchBtn = new JButton("Search");
    private final JButton selectBtn = new JButton("Select");
    private final JButton cancelBtn = new JButton("Cancel");

    private final JTable table = new JTable();
    private final PatientPickModel model = new PatientPickModel();

    private Patient selected;

    public PatientPickerDialog(Window owner) {
        super(owner, "Select Patient", ModalityType.APPLICATION_MODAL);

        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Name:"));
        top.add(searchField);
        top.add(searchBtn);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(selectBtn);
        bottom.add(cancelBtn);

        setLayout(new BorderLayout(10, 10));
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> refresh());
        selectBtn.addActionListener(e -> onSelect());
        cancelBtn.addActionListener(e -> dispose());

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) onSelect();
            }
        });

        setSize(800, 400);
        setLocationRelativeTo(owner);

        refresh();
    }

    public Patient getSelected() { return selected; }

    private void refresh() {
        List<Patient> rows = dao.searchByName(searchField.getText());
        model.setRows(rows);
    }

    private void onSelect() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a patient.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        selected = model.getRow(row);
        dispose();
    }

    static class PatientPickModel extends AbstractTableModel {
        private final String[] cols = {"PatientId", "HealthCardNo", "FullName", "DOB", "Sex", "Phone", "Address"};
        private List<Patient> rows = new ArrayList<>();

        public void setRows(List<Patient> rows) {
            this.rows = rows == null ? new ArrayList<>() : rows;
            fireTableDataChanged();
        }

        public Patient getRow(int i) { return rows.get(i); }

        @Override public int getRowCount() { return rows.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Patient p = rows.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> p.getPatientId();
                case 1 -> p.getHealthCardNo();
                case 2 -> p.getFullName();
                case 3 -> p.getDateOfBirth();
                case 4 -> p.getSex();
                case 5 -> p.getPhone();
                case 6 -> p.getAddress();
                default -> "";
            };
        }
    }
}