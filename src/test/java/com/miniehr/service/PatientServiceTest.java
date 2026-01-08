package com.miniehr.service;

import com.miniehr.dao.PatientDao;
import com.miniehr.model.Patient;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientServiceTest {

    @Test
    void create_shouldRejectBlankHealthCardNo() {
        PatientDao dao = mock(PatientDao.class);
        PatientService svc = new PatientService(dao);

        Patient p = new Patient();
        p.setHealthCardNo("   ");
        p.setFullName("Alice");
        p.setDateOfBirth(LocalDate.of(1990, 1, 1));

        assertThrows(IllegalArgumentException.class, () -> svc.create(p));
        verify(dao, never()).insert(any());
    }

    @Test
    void create_shouldRejectFutureDob() {
        PatientDao dao = mock(PatientDao.class);
        PatientService svc = new PatientService(dao);

        Patient p = new Patient();
        p.setHealthCardNo("HC30001");
        p.setFullName("Bob");
        p.setDateOfBirth(LocalDate.now().plusDays(1));

        assertThrows(IllegalArgumentException.class, () -> svc.create(p));
        verify(dao, never()).insert(any());
    }

    @Test
    void create_shouldCallDaoInsertWhenValid() {
        PatientDao dao = mock(PatientDao.class);
        PatientService svc = new PatientService(dao);

        Patient p = new Patient();
        p.setHealthCardNo("HC30001");
        p.setFullName("Bob");
        p.setDateOfBirth(LocalDate.of(1992, 1, 3));

        // DAO insert returns the saved patient (with id)
        Patient saved = new Patient();
        saved.setPatientId(99);
        saved.setHealthCardNo(p.getHealthCardNo());
        saved.setFullName(p.getFullName());
        saved.setDateOfBirth(p.getDateOfBirth());

        when(dao.insert(any(Patient.class))).thenReturn(saved);

        Patient result = svc.create(p);

        assertEquals(99, result.getPatientId());
        verify(dao, times(1)).insert(any(Patient.class));
    }

    @Test
    void update_shouldRejectMissingId() {
        PatientDao dao = mock(PatientDao.class);
        PatientService svc = new PatientService(dao);

        Patient p = new Patient();
        p.setHealthCardNo("HC30001");
        p.setFullName("Bob");
        p.setDateOfBirth(LocalDate.of(1992, 1, 3));

        assertThrows(IllegalArgumentException.class, () -> svc.update(p));
        verify(dao, never()).update(any());
    }
}