package com.parasoft.parabank.dao.jdbc;

import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * Manages sequences to automatically generate ids for new entities 
 */
public class JdbcSequenceDao extends SimpleJdbcDaoSupport {
    static final int OFFSET = 111;
    
    /**
     * Generate and return the next id for a given entity
     * 
     * @param name the name of the database table to generate an id 
     * @return a new id value to be used for a new entity object
     */
    public int getNextId(String name) {
        int nextId = getSimpleJdbcTemplate().queryForInt("SELECT next_id FROM Sequence WHERE name = ?", name);
        getSimpleJdbcTemplate().update("UPDATE Sequence SET next_id = ? WHERE name = ?", nextId + OFFSET, name);
        return nextId;
    }
}
