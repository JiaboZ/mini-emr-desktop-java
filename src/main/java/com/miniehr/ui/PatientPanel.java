package com.miniehr.ui;

import com.miniehr.dao.JdbcPatientDao;
import com.miniehr.model.Patient;
import com.miniehr.service.PatientService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PatientPanel extends JPanel {

    private final PatientService service = new PatientService(new JdbcPatientDao());

    private final JTextField searchField = new JTextField(25);
    private final JButton searchBtn = new JButton("Search");
    private final JTable table = new JTable();
    private final PatientTableModel model = new PatientTableModel();
    private final JButton addBtn = new JButton("Add");
    private final JButton editBtn = new JButton("Edit");

    public PatientPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Name:"));
        top.add(searchField);
        top.add(searchBtn);
        top.add(addBtn);
        top.add(editBtn);

        table.setModel(model);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        searchBtn.addActionListener(e -> refresh());
        addBtn.addActionListener(e -> onAdd());
        editBtn.addActionListener(e -> onEdit());
        refresh();
    }

    private void refresh() {
        List<Patient> rows = service.searchByName(searchField.getText());
        model.setRows(rows);
    }
    private void onAdd() {
        PatientDialog dlg = new PatientDialog(SwingUtilities.getWindowAncestor(this), "Add Patient", null);
        dlg.setVisible(true);

        if (dlg.isSaved()) {
            try {
                service.create(dlg.getPatient());
                refresh();
            } catch (com.miniehr.service.BusinessException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Patient selected = model.getRow(row);
        PatientDialog dlg = new PatientDialog(SwingUtilities.getWindowAncestor(this), "Edit Patient", selected);
        dlg.setVisible(true);

        if (dlg.isSaved()) {
            try {
                Patient edited = dlg.getPatient();
                edited.setPatientId(selected.getPatientId()); // 确保带着 ID
                service.update(edited);
                refresh();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    static class PatientTableModel extends AbstractTableModel {
        private final String[] cols = {"PatientId", "HealthCardNo", "FullName", "DOB", "Sex", "Phone","Address"};
        private List<Patient> rows = new ArrayList<>();

        public void setRows(List<Patient> rows) {
            this.rows = (rows == null) ? new ArrayList<>() : rows;
            fireTableDataChanged();
        }
        public Patient getRow(int rowIndex) {
            return rows.get(rowIndex);
        }

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