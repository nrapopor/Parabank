package com.parasoft.parabank.dao.jdbc.internal;

import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import com.parasoft.parabank.dao.internal.DynamicDataInserter;

public class SampleJdbcDynamicDataInserter extends SimpleJdbcDaoSupport implements DynamicDataInserter {
    public void insertData() {
        // final String SQL = "INSERT INTO Foo (bar, baz) VALUES (:bar, :baz)";
        
        // getSimpleJdbcTemplate().update(SQL, "bar", "baz");
    }
}
