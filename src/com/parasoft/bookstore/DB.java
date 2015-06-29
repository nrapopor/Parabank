package com.parasoft.bookstore;

import java.sql.*;

public abstract class DB {
    private static final String NL_DRIVER         = "org.hsqldb.jdbcDriver";
    // NYI make these settable by the deployment descriptor
    private static final String NL_HOST           = "localhost";
    private static final String NL_PORT           = "";
    private static final String NL_DBNAME         = "bookstore";
    private static final String NL_USERNAME       = "sa";
    private static final String NL_PASSWORD       = "";
    private static final String NL_CONNECTION_URL = constructConnectionURL();
    private Connection connection;

    private static final String constructConnectionURL() {
        return "jdbc:hsqldb:hsql://" + NL_HOST + NL_PORT + "/" + NL_DBNAME;// + "&characterEncoding=UTF8";
    }
    
	protected DB() 
	    throws SQLException,
	        InstantiationException,
	        IllegalAccessException,
	        ClassNotFoundException 
	{
        Class.forName(NL_DRIVER).newInstance();
        connect();
	}
	
    protected void connect() throws SQLException {
        if (isClosed()) {
            connection = DriverManager.getConnection(NL_CONNECTION_URL, NL_USERNAME, NL_PASSWORD);
        }
    }
    
    /**
     * Call close() when finished.
     * @param  query an SQL statement
     */
    protected PreparedStatement prepareStatement(String query) throws SQLException {
        return connection.prepareStatement(query);
    }
    
    protected PreparedStatement prepareStatement(String query, int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.prepareStatement(query, resultSetType, resultSetConcurrency);
    }
    
    public boolean isClosed() throws SQLException {
        return connection == null || connection.isClosed();
    }
    
    public void close() throws SQLException {
        connection.close();
    }
    
    public Connection getConnection() {
        return connection;
    }
    /**
     * Don't rely on this, call close() manually.
     */
    public void finalize() throws Throwable {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            super.finalize();
        }
    }
}