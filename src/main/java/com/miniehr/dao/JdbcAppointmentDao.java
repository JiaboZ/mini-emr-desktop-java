package com.miniehr.dao;

import com.miniehr.model.Appointment;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcAppointmentDao implements AppointmentDao {

    @Override
    public Appointment insert(Appointment a) {
        String sql = """
            INSERT INTO dbo.Appointments(PatientId, ProviderId, StartTime, DurationMin, Reason, Status)
            OUTPUT INSERTED.AppointmentId
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, a.getPatientId());
            ps.setInt(2, a.getProviderId());
            ps.setTimestamp(3, Timestamp.valueOf(a.getStartTime()));
            ps.setInt(4, a.getDurationMin());
            ps.setString(5, a.getReason());
            ps.setString(6, a.getStatus() == null ? "BOOKED" : a.getStatus());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) a.setAppointmentId(rs.getInt(1));
            }
            return a;

        } catch (SQLException e) {
            throw new RuntimeException("DB insert appointment failed", e);
        }
    }

    @Override
    public int countOverlaps(int providerId, LocalDateTime start, LocalDateTime end) {
        // overlap condition: existingStart < newEnd AND existingEnd > newStart
        // existingEnd = DATEADD(minute, DurationMin, StartTime)
        String sql = """
            SELECT COUNT(1) AS Cnt
            FROM dbo.Appointments
            WHERE ProviderId = ?
              AND StartTime < ?
              AND DATEADD(minute, DurationMin, StartTime) > ?
              AND Status = 'BOOKED'
            """;

        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, providerId);
            ps.setTimestamp(2, Timestamp.valueOf(end));
            ps.setTimestamp(3, Timestamp.valueOf(start));

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("Cnt");
            }

        } catch (SQLException e) {
            throw new RuntimeException("DB overlap check failed", e);
        }
    }

    @Override
    public List<Appointment> listByProvider(int providerId, LocalDateTime fromInclusive, LocalDateTime toExclusive) {
        String sql = """
            
                SELECT a.AppointmentId, a.PatientId, p.FullName AS PatientName,
                       a.ProviderId, pr.FullName AS ProviderName,
                       a.StartTime, a.DurationMin, a.Reason, a.Status
                FROM dbo.Appointments a
                JOIN dbo.Patients p ON p.PatientId = a.PatientId
                JOIN dbo.Providers pr ON pr.ProviderId = a.ProviderId
                WHERE a.ProviderId = ?
                  AND a.StartTime >= ?
                  AND a.StartTime < ?
                ORDER BY a.StartTime
            """;
        List<Appointment> out = new ArrayList<>();

        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, providerId);
            ps.setTimestamp(2, Timestamp.valueOf(fromInclusive));
            ps.setTimestamp(3, Timestamp.valueOf(toExclusive));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Appointment a = new Appointment();
                    a.setAppointmentId(rs.getInt("AppointmentId"));
                    a.setPatientId(rs.getInt("PatientId"));
                    a.setProviderId(rs.getInt("ProviderId"));
                    a.setStartTime(rs.getTimestamp("StartTime").toLocalDateTime());
                    a.setDurationMin(rs.getInt("DurationMin"));
                    a.setReason(rs.getString("Reason"));
                    a.setStatus(rs.getString("Status"));
                    a.setPatientName(rs.getString("PatientName"));
                    a.setProviderName(rs.getString("ProviderName"));
                    out.add(a);
                }
            }
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("DB list appointments failed", e);
        }
    }
}
