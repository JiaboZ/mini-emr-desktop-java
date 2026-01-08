package com.miniehr.dao;

import com.miniehr.model.Appointment;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentDao {
    Appointment insert(Appointment a);

    /** 查询在 [start, end) 时间段内，与该 providerId 有重叠的预约数量 */
    int countOverlaps(int providerId, LocalDateTime start, LocalDateTime end);

    List<Appointment> listByProvider(int providerId, LocalDateTime fromInclusive, LocalDateTime toExclusive);
}
