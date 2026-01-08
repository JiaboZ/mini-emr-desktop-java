package com.miniehr.dao;

import com.miniehr.service.BusinessException;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class JdbcPatientDaoTest {

    @Test
    void translateSqlException_shouldReturnBusinessExceptionForDuplicateKey() {
        SQLException ex = new SQLException("duplicate", "23000", 2627);
        RuntimeException out = JdbcPatientDao.translateSqlException(ex);

        assertTrue(out instanceof BusinessException);
        assertEquals("Health Card Number already exists.", out.getMessage());
    }

    @Test
    void translateSqlException_shouldReturnRuntimeExceptionForOtherErrors() {
        SQLException ex = new SQLException("other", "42000", 1234);
        RuntimeException out = JdbcPatientDao.translateSqlException(ex);

        assertTrue(out instanceof RuntimeException);
        assertFalse(out instanceof BusinessException);
    }
}
