package com.miniehr.dao;

import com.miniehr.model.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientDao {
    List<Patient> searchByName(String nameLike);

    Patient insert(Patient p);
    void update(Patient p);
    Optional<Patient> findById(int patientId);
}
