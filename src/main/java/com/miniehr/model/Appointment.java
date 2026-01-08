package com.miniehr.model;

import java.time.LocalDateTime;

public class Appointment {
    private Integer appointmentId;
    private int patientId;
    private int providerId;
    private LocalDateTime startTime;
    private int durationMin;
    private String reason;
    private String status;

    private String patientName;
    private String providerName;

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public Integer getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Integer appointmentId) { this.appointmentId = appointmentId; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getProviderId() { return providerId; }
    public void setProviderId(int providerId) { this.providerId = providerId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public int getDurationMin() { return durationMin; }
    public void setDurationMin(int durationMin) { this.durationMin = durationMin; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
