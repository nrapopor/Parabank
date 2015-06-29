package com.parasoft.parabank.dao.internal;

/**
 * Interface for inserting dynamic data into the database at creation time
 */
public interface DynamicDataInserter {
    
    /**
     * Called at DB creation time to insert dynamic data into the DB
     */
    void insertData();
}
