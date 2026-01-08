package com.miniehr.dao;

import com.miniehr.dao.Db;

public class TestDb {
    public static void main(String[] args) throws Exception {
        System.out.println(Db.getConnection());
    }
}
