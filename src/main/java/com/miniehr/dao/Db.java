package com.miniehr.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Db {

    private static final String URL =
            "jdbc:sqlserver://localhost:1433;databaseName=MiniEMR;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASS = "YourStrong(!)Password"; // ← 改成你的

    private Db() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
