package com.miniehr.dao;

import com.miniehr.model.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcPatientDao implements PatientDao {

    @Override
    public List<Patient> searchByName(String nameLike) {
        String sql = """
            SELECT TOP 100 PatientId, HealthCardNo, FullName, DateOfBirth, Sex, Phone, Address
            FROM dbo.Patients
            WHERE FullName LIKE ?
            ORDER BY PatientId DESC
            """;

        List<Patient> out = new ArrayList<>();
        String q = (nameLike == null) ? "" : nameLike.trim();

        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + q + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Patient p = new Patient();
                    p.setPatientId(rs.getInt("PatientId"));
                    p.setHealthCardNo(rs.getString("HealthCardNo"));
                    p.setFullName(rs.getString("FullName"));

                    Date dob = rs.getDate("DateOfBirth");
                    p.setDateOfBirth(dob == null ? null : dob.toLocalDate());

                    p.setSex(rs.getString("Sex"));
                    p.setPhone(rs.getString("Phone"));
                    p.setAddress(rs.getString("Address"));

                    out.add(p);
                }
            }

            return out;
        } catch (SQLException e) {
            throw new RuntimeException("DB search patients failed", e);
        }
    }

    @Override
    public Patient insert(Patient p) {
        String sql = """
        INSERT INTO dbo.Patients(HealthCardNo, FullName, DateOfBirth, Sex, Phone, Address)
        OUTPUT INSERTED.PatientId
        VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, p.getHealthCardNo());
            ps.setString(2, p.getFullName());
            ps.setDate(3, p.getDateOfBirth() == null ? null : Date.valueOf(p.getDateOfBirth()));
            ps.setString(4, p.getSex());
            ps.setString(5, p.getPhone());
            ps.setString(6, p.getAddress());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) p.setPatientId(rs.getInt(1));
            }
            return p;

        } catch (SQLException e) {

            throw translateSqlException(e);
        }
    }

    @Override
    public void update(Patient p) {
        String sql = """
        UPDATE dbo.Patients
        SET HealthCardNo=?, FullName=?, DateOfBirth=?, Sex=?, Phone=?, Address=?
        WHERE PatientId=?
        """;

        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, p.getHealthCardNo());
            ps.setString(2, p.getFullName());
            ps.setDate(3, p.getDateOfBirth() == null ? null : Date.valueOf(p.getDateOfBirth()));
            ps.setString(4, p.getSex());
            ps.setString(5, p.getPhone());
            ps.setString(6, p.getAddress());
            ps.setInt(7, p.getPatientId());

            ps.executeUpdate();

        } catch (SQLException e) {

            throw translateSqlException(e);
        }
    }

    @Override
    public java.util.Optional<Patient> findById(int patientId) {
        String sql = """
        SELECT PatientId, HealthCardNo, FullName, DateOfBirth, Sex, Phone, Address
        FROM dbo.Patients
        WHERE PatientId=?
        """;

        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return java.util.Optional.empty();

                Patient p = new Patient();
                p.setPatientId(rs.getInt("PatientId"));
                p.setHealthCardNo(rs.getString("HealthCardNo"));
                p.setFullName(rs.getString("FullName"));

                Date dob = rs.getDate("DateOfBirth");
                p.setDateOfBirth(dob == null ? null : dob.toLocalDate());

                p.setSex(rs.getString("Sex"));
                p.setPhone(rs.getString("Phone"));
                p.setAddress(rs.getString("Address"));
                return java.util.Optional.of(p);
            }

        } catch (SQLException e) {
            throw new RuntimeException("DB find patient failed", e);
        }
    }
    static RuntimeException translateSqlException(SQLException e) {
        int code = e.getErrorCode();
        if (code == 2627 || code == 2601) {
            return new com.miniehr.service.BusinessException("Health Card Number already exists.");
        }
        return new RuntimeException("Database error", e);
    }


}
