package com.parasoft.bookstore.jdbc;

import com.parasoft.parabank.dao.*;
import com.parasoft.parabank.dao.jdbc.*;

import java.util.*;

import org.apache.commons.logging.*;
import org.springframework.core.io.*;
import org.springframework.jdbc.core.simple.*;
import org.springframework.test.jdbc.*;

public class JdbcBookstoreDao extends SimpleJdbcDaoSupport implements AdminDao {
    private static final Log log = LogFactory.getLog(JdbcAdminDao.class);
    
    private static final String SQL_PACKAGE = "com/parasoft/bookstore/jdbc/sql/";
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.AdminDao#initializeDB()
     */
    public void initializeDB() {
        Resource createResource = new ClassPathResource(SQL_PACKAGE + "bookstoreCreate.sql");
        Resource insertResource = new ClassPathResource(SQL_PACKAGE + "bookstoreInsert.sql");
        
        log.info("Initializing database...");
        SimpleJdbcTestUtils.executeSqlScript(getSimpleJdbcTemplate(), createResource, false);
        SimpleJdbcTestUtils.executeSqlScript(getSimpleJdbcTemplate(), insertResource, false);
        
        log.info("Database initialized");
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.AdminDao#cleanDB()
     */
    public void cleanDB() {
        //NYI
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.AdminDao#getParameter(java.lang.String)
     */
    public String getParameter(String name) {
        //NYI
        return "";
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.AdminDao#setParameter(java.lang.String, java.lang.String)
     */
    public void setParameter(String name, String value) {
        //NYI
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.AdminDao#getParameters()
     */
    public Map<String, String> getParameters() {
        //NYI
        return null;
    }
}
