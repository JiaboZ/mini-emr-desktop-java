package com.miniehr.service;

import com.miniehr.dao.AppointmentDao;
import com.miniehr.model.Appointment;

import java.time.LocalDateTime;

public class AppointmentService {
    private final AppointmentDao dao;

    public AppointmentService(AppointmentDao dao) {
        this.dao = dao;
    }

    public Appointment book(Appointment a) {
        if (a.getPatientId() <= 0) throw new IllegalArgumentException("PatientId is required");
        if (a.getProviderId() <= 0) throw new IllegalArgumentException("ProviderId is required");
        if (a.getStartTime() == null) throw new IllegalArgumentException("StartTime is required");
        if (a.getDurationMin() <= 0) throw new IllegalArgumentException("Duration must be positive");

        LocalDateTime start = a.getStartTime();
        LocalDateTime end = start.plusMinutes(a.getDurationMin());

        int overlaps = dao.countOverlaps(a.getProviderId(), start, end);
        if (overlaps > 0) {
            throw new BusinessException("Time slot is not available for this provider.");
        }

        if (a.getStatus() == null || a.getStatus().isBlank()) a.setStatus("BOOKED");
        return dao.insert(a);
    }
}
