package com.parasoft.parabank.dao.jdbc;

import com.parasoft.parabank.dao.*;
import com.parasoft.parabank.dao.internal.*;

import java.sql.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.springframework.core.io.*;
import org.springframework.dao.*;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.simple.*;
import org.springframework.test.jdbc.*;

/*
 * JDBC implementation of AdminDao
 */
public class JdbcAdminDao extends SimpleJdbcDaoSupport implements AdminDao {
    private static final Log log = LogFactory.getLog(JdbcAdminDao.class);
    
    private static final String SQL_PACKAGE = "com/parasoft/parabank/dao/jdbc/sql/";
    
    private List<DynamicDataInserter> inserters;
    
    public void setInserters(List<DynamicDataInserter> inserters) {
        this.inserters = inserters;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.AdminDao#initializeDB()
     */
    public void initializeDB() {
        Resource createResource = new ClassPathResource(SQL_PACKAGE + "create.sql");
        Resource insertResource = new ClassPathResource(SQL_PACKAGE + "insert.sql");
        
        log.info("Initializing database...");
        SimpleJdbcTestUtils.executeSqlScript(getSimpleJdbcTemplate(), createResource, false);
        SimpleJdbcTestUtils.executeSqlScript(getSimpleJdbcTemplate(), insertResource, false);
       
        for (DynamicDataInserter inserter : inserters) {
        	inserter.insertData();
        }
        
        log.info("Database initialized");
        
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.AdminDao#cleanDB()
     */
    public void cleanDB() {
        Resource resource = new ClassPathResource(SQL_PACKAGE + "reset.sql");
        
        log.info("Resetting database...");
        SimpleJdbcTestUtils.executeSqlScript(getSimpleJdbcTemplate(), resource, true);
        log.info("Database reset");
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.AdminDao#getParameter(java.lang.String)
     */
    public String getParameter(String name) {
        final String SQL = "SELECT value FROM Parameter WHERE name = ?";
        
        return getSimpleJdbcTemplate().queryForObject(SQL, String.class, name);
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.AdminDao#setParameter(java.lang.String, java.lang.String)
     */
    public void setParameter(String name, String value) {
        final String SQL = "UPDATE Parameter SET value = ? WHERE name = ?";
        
        getSimpleJdbcTemplate().update(SQL, value, name);
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.AdminDao#getParameters()
     */
    public Map<String, String> getParameters() {
        final String SQL = "SELECT name, value FROM Parameter";

        return getJdbcTemplate().query(SQL, new ResultSetExtractor<Map<String, String>>() {
            public Map<String, String> extractData(ResultSet rs) throws SQLException,
                    DataAccessException {
                Map<String, String> parameters = new HashMap<String, String>();
                while (rs.next()) {
                    parameters.put(rs.getString("name"), rs.getString("value"));
                }
                return parameters;
            }
        });
    }
}
