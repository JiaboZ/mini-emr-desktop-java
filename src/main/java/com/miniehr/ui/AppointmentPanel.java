package com.miniehr.ui;


import com.miniehr.dao.JdbcAppointmentDao;
import com.miniehr.model.Appointment;
import com.miniehr.service.AppointmentService;
import com.miniehr.service.BusinessException;
import com.miniehr.model.ProviderLite;
import com.miniehr.dao.JdbcProviderDao;
import com.miniehr.model.Patient;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AppointmentPanel extends JPanel {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final JdbcAppointmentDao dao = new JdbcAppointmentDao();
    private final AppointmentService service = new AppointmentService(dao);

    //private final JTextField patientId = new JTextField(6);
    //private final JTextField providerId = new JTextField(6);
    private final JdbcProviderDao providerDao = new JdbcProviderDao();

    private Patient selectedPatient = null;
    private final JTextField patientDisplay = new JTextField(25);
    private final JButton pickPatientBtn = new JButton("Select...");

    private final JComboBox<ProviderLite> providerCombo = new JComboBox<>();

    private final JTextField startTime = new JTextField(16); // yyyy-MM-dd HH:mm
    private final JTextField durationMin = new JTextField(4);
    private final JTextField reason = new JTextField(20);

    private final JButton bookBtn = new JButton("Book");
    private final JButton refreshBtn = new JButton("Refresh");

    private final JTable table = new JTable();
    private final AppointmentTableModel model = new AppointmentTableModel();





    public AppointmentPanel() {

        pickPatientBtn.addActionListener(e -> onPickPatient());
        setLayout(new BorderLayout(10, 10));

        patientDisplay.setEditable(false);
        loadProviders();
        providerCombo.addActionListener(e -> refresh()); // 换医生就刷新当天列表
        // 默认值：方便你快速测试
        startTime.setText(LocalDate.now() + " 10:00");
        durationMin.setText("30");

        table.setModel(model);

        add(buildTopForm(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        bookBtn.addActionListener(e -> onBook());
        refreshBtn.addActionListener(e -> refresh());

        refresh();
    }


    private void onPickPatient() {
        PatientPickerDialog dlg = new PatientPickerDialog(SwingUtilities.getWindowAncestor(this));
        dlg.setVisible(true);

        Patient p = dlg.getSelected();
        if (p != null) {
            selectedPatient = p;
            patientDisplay.setText(p.getFullName() + " (ID=" + p.getPatientId() + ", " + p.getHealthCardNo() + ")");
        }
    }

    private void loadProviders() {
        providerCombo.removeAllItems();
        for (ProviderLite p : providerDao.listAll()) {
            providerCombo.addItem(p);
        }
    }
    private JPanel buildTopForm() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 6, 4, 6);
        gc.anchor = GridBagConstraints.WEST;

        int r = 0;

        JPanel patientPick = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        patientPick.add(patientDisplay);
        patientPick.add(Box.createHorizontalStrut(6));
        patientPick.add(pickPatientBtn);

        // ✅ 第1行：Patient 选择 + Provider 下拉
        addRow(p, gc, r++, "Patient*", patientPick, "Provider*", providerCombo);

        // ✅ 第2行：StartTime + Duration
        addRow(p, gc, r++, "StartTime* (yyyy-MM-dd HH:mm)", startTime, "DurationMin*", durationMin);

        // ✅ 第3行：Reason
        addRow(p, gc, r++, "Reason", reason, "", new JLabel(""));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btns.add(bookBtn);
        btns.add(refreshBtn);

        gc.gridx = 0;
        gc.gridy = r;
        gc.gridwidth = 4;
        gc.fill = GridBagConstraints.HORIZONTAL;
        p.add(btns, gc);

        return p;
    }

    private void addRow(JPanel p, GridBagConstraints gc, int row,
                        String l1, Component f1, String l2, Component f2) {
        gc.gridy = row;

        gc.gridx = 0; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;
        p.add(new JLabel(l1), gc);

        gc.gridx = 1; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        p.add(f1, gc);

        gc.gridx = 2; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;
        p.add(new JLabel(l2), gc);

        gc.gridx = 3; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        p.add(f2, gc);
    }

    private void onBook() {
        try {

            if (selectedPatient == null) {
                throw new IllegalArgumentException("Please select a patient.");
            }
            ProviderLite prov = (ProviderLite) providerCombo.getSelectedItem();
            if (prov == null) {
                throw new IllegalArgumentException("Please select a provider.");
            }
            int pid = selectedPatient.getPatientId();
            int prid = prov.getProviderId();

            //int pid = Integer.parseInt(patientId.getText().trim());
            //int prid = Integer.parseInt(providerId.getText().trim());
            LocalDateTime st = LocalDateTime.parse(startTime.getText().trim(), DT_FMT);
            int dur = Integer.parseInt(durationMin.getText().trim());

            Appointment a = new Appointment();
            a.setPatientId(pid);
            a.setProviderId(prid);
            a.setStartTime(st);
            a.setDurationMin(dur);
            a.setReason(reason.getText().trim());

            service.book(a); // ✅ 会做冲突检测
            JOptionPane.showMessageDialog(this,
                    "Booked. AppointmentId=" + a.getAppointmentId(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            refresh();
        } catch (BusinessException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Validation",
                    JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid input: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refresh() {
        try {
            //String providerText = providerId.getText().trim();
            //if (providerText.isEmpty()) {
                //model.setRows(List.of());
                //return;
            //}

            //int prid = Integer.parseInt(providerText);
            ProviderLite prov = (ProviderLite) providerCombo.getSelectedItem();
            if (prov == null) {
                model.setRows(List.of());
                return;
            }
            int prid = prov.getProviderId();

            // 用 startTime 的日期作为“当天”
            LocalDate date = LocalDate.now();
            String stText = startTime.getText().trim();
            if (!stText.isEmpty()) {
                try {
                    date = LocalDateTime.parse(stText, DT_FMT).toLocalDate();
                } catch (Exception ignore) {}
            }

            LocalDateTime from = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime to = from.plusDays(1);

            List<Appointment> rows = dao.listByProvider(prid, from, to);
            model.setRows(rows);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    static class AppointmentTableModel extends AbstractTableModel {
        private final String[] cols = {"AppointmentId", "Patient", "Provider", "StartTime", "Duration", "Reason", "Status"};

        private List<Appointment> rows = new ArrayList<>();

        public void setRows(List<Appointment> rows) {
            this.rows = (rows == null) ? new ArrayList<>() : rows;
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return rows.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Appointment a = rows.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> a.getAppointmentId();
                case 1 -> a.getPatientName() + " (ID=" + a.getPatientId() + ")";
                case 2 -> a.getProviderName() + " (ID=" + a.getProviderId() + ")";
                case 3 -> a.getStartTime();
                case 4 -> a.getDurationMin();
                case 5 -> a.getReason();
                case 6 -> a.getStatus();
                default -> "";
            };
        }
    }
}

