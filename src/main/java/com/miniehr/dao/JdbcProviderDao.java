package com.miniehr.dao;

import com.miniehr.model.ProviderLite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcProviderDao implements ProviderDao {

    @Override
    public List<ProviderLite> listAll() {
        String sql = """
            SELECT ProviderId, FullName, Specialty
            FROM dbo.Providers
            ORDER BY FullName
            """;

        List<ProviderLite> out = new ArrayList<>();
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ProviderLite p = new ProviderLite();
                p.setProviderId(rs.getInt("ProviderId"));
                p.setFullName(rs.getString("FullName"));
                p.setSpecialty(rs.getString("Specialty"));
                out.add(p);
            }
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("DB list providers failed", e);
        }
    }
}
