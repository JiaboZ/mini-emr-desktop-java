package com.miniehr.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        super("MiniEMR Desktop");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Patients", new PatientPanel());
        tabs.addTab("Appointments", new AppointmentPanel());


        add(tabs, BorderLayout.CENTER);
    }
}
