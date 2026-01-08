package com.miniehr.ui;

import com.miniehr.model.Patient;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class PatientDialog extends JDialog {

    private final JTextField healthCardNo = new JTextField(20);
    private final JTextField fullName = new JTextField(20);
    private final JTextField dob = new JTextField(10); // yyyy-MM-dd
    private final JComboBox<String> sex = new JComboBox<>(new String[]{"", "F", "M", "X"});
    private final JTextField phone = new JTextField(20);
    private final JTextField address = new JTextField(25);

    private boolean saved = false;
    private Patient patient;

    public PatientDialog(Window owner, String title, Patient existing) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        this.patient = existing == null ? new Patient() : copy(existing);

        setLayout(new BorderLayout(10, 10));
        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        if (existing != null) fillForm(existing);

        pack();
        setLocationRelativeTo(owner);
    }

    public boolean isSaved() { return saved; }

    public Patient getPatient() { return patient; }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 6, 4, 6);
        gc.anchor = GridBagConstraints.WEST;

        int r = 0;
        addRow(p, gc, r++, "HealthCardNo*", healthCardNo);
        addRow(p, gc, r++, "FullName*", fullName);
        addRow(p, gc, r++, "DOB* (yyyy-MM-dd)", dob);
        addRow(p, gc, r++, "Sex", sex);
        addRow(p, gc, r++, "Phone", phone);
        addRow(p, gc, r++, "Address", address);

        return p;
    }

    private JPanel buildButtons() {
        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");

        save.addActionListener(e -> onSave());
        cancel.addActionListener(e -> dispose());

        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.add(save);
        p.add(cancel);
        return p;
    }

    private void onSave() {
        try {
            patient.setHealthCardNo(healthCardNo.getText().trim());
            patient.setFullName(fullName.getText().trim());

            String dobText = dob.getText().trim();
            patient.setDateOfBirth(LocalDate.parse(dobText)); // 会自动校验格式，不对会抛异常

            patient.setSex(((String) sex.getSelectedItem()));
            patient.setPhone(phone.getText().trim());
            patient.setAddress(address.getText().trim());

            saved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid input: " + ex.getMessage(),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillForm(Patient p) {
        healthCardNo.setText(p.getHealthCardNo());
        fullName.setText(p.getFullName());
        dob.setText(p.getDateOfBirth() == null ? "" : p.getDateOfBirth().toString());
        sex.setSelectedItem(p.getSex() == null ? "" : p.getSex());
        phone.setText(p.getPhone() == null ? "" : p.getPhone());
        address.setText(p.getAddress() == null ? "" : p.getAddress());
    }

    private Patient copy(Patient p) {
        Patient x = new Patient();
        x.setPatientId(p.getPatientId());
        x.setHealthCardNo(p.getHealthCardNo());
        x.setFullName(p.getFullName());
        x.setDateOfBirth(p.getDateOfBirth());
        x.setSex(p.getSex());
        x.setPhone(p.getPhone());
        x.setAddress(p.getAddress());
        return x;
    }

    private void addRow(JPanel p, GridBagConstraints gc, int row, String label, Component field) {
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;
        p.add(new JLabel(label), gc);

        gc.gridx = 1; gc.gridy = row; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        p.add(field, gc);
    }
}

