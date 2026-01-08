package com.miniehr.service;

import com.miniehr.dao.AppointmentDao;
import com.miniehr.model.Appointment;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    @Test
    void book_shouldRejectConflictingSlot() {
        AppointmentDao dao = mock(AppointmentDao.class);
        AppointmentService svc = new AppointmentService(dao);

        Appointment a = new Appointment();
        a.setPatientId(1);
        a.setProviderId(10);
        a.setStartTime(LocalDateTime.of(2026, 1, 8, 10, 0));
        a.setDurationMin(30);

        when(dao.countOverlaps(eq(10), any(), any())).thenReturn(1);

        assertThrows(BusinessException.class, () -> svc.book(a));
        verify(dao, never()).insert(any());
    }

    @Test
    void book_shouldInsertWhenSlotFree() {
        AppointmentDao dao = mock(AppointmentDao.class);
        AppointmentService svc = new AppointmentService(dao);

        Appointment a = new Appointment();
        a.setPatientId(1);
        a.setProviderId(10);
        a.setStartTime(LocalDateTime.of(2026, 1, 8, 11, 0));
        a.setDurationMin(30);
        a.setReason("Checkup");

        when(dao.countOverlaps(eq(10), any(), any())).thenReturn(0);
        when(dao.insert(any(Appointment.class))).thenAnswer(inv -> {
            Appointment saved = inv.getArgument(0);
            saved.setAppointmentId(123);
            return saved;
        });

        Appointment saved = svc.book(a);

        assertEquals(123, saved.getAppointmentId());
        assertEquals("BOOKED", saved.getStatus());
        verify(dao, times(1)).insert(any());
    }
}
