package com.miniehr.service;

import com.miniehr.dao.PatientDao;
import com.miniehr.model.Patient;

import java.time.LocalDate;
import java.util.List;

public class PatientService {
    private final PatientDao dao;

    public PatientService(PatientDao dao) {
        this.dao = dao;
    }

    public List<Patient> searchByName(String nameLike) {
        return dao.searchByName(nameLike);
    }

    public Patient create(Patient p) {
        validate(p);
        return dao.insert(p);
    }

    public void update(Patient p) {
        if (p.getPatientId() == null) throw new IllegalArgumentException("PatientId is required");
        validate(p);
        dao.update(p);
    }

    private void validate(Patient p) {
        if (p.getHealthCardNo() == null || p.getHealthCardNo().trim().isEmpty())
            throw new IllegalArgumentException("HealthCardNo is required");
        if (p.getFullName() == null || p.getFullName().trim().isEmpty())
            throw new IllegalArgumentException("FullName is required");
        LocalDate dob = p.getDateOfBirth();
        if (dob == null) throw new IllegalArgumentException("DOB is required");
        if (dob.isAfter(LocalDate.now())) throw new IllegalArgumentException("DOB cannot be in the future");
    }
}
