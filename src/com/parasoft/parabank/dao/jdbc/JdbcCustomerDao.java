package com.parasoft.parabank.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import com.parasoft.parabank.dao.CustomerDao;
import com.parasoft.parabank.domain.Address;
import com.parasoft.parabank.domain.Customer;

/*
 * JDBC implementation of CustomerDao
 */
public class JdbcCustomerDao extends SimpleJdbcDaoSupport implements CustomerDao {
    private static final Log log = LogFactory.getLog(JdbcCustomerDao.class);
    
    private final String BASE_QUERY_SQL = "SELECT id, first_name, last_name, address, city, state, zip_code, phone_number, ssn, username, password FROM Customer";
        
    private JdbcSequenceDao sequenceDao;
    
    public void setSequenceDao(JdbcSequenceDao sequenceDao) {
        this.sequenceDao = sequenceDao;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.CustomerDao#getCustomer(int)
     */
    public Customer getCustomer(int id) {
        final String SQL = BASE_QUERY_SQL + " WHERE id = ?";
        
        log.info("Getting customer object for id = " + id);
        Customer customer = getSimpleJdbcTemplate().queryForObject(
                SQL, new CustomerMapper(), id);
        
        return customer;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.CustomerDao#getCustomer(java.lang.String, java.lang.String)
     */
    public Customer getCustomer(String username, String password) {
        final String SQL = BASE_QUERY_SQL + " WHERE username = ? and password = ?";
        
        Customer customer = null;
        
        try {
            log.info("Getting customer object for username = " + username + " and password = " + password);
            customer = getSimpleJdbcTemplate().queryForObject(
                    SQL, new CustomerMapper(), username, password);
        } catch (DataAccessException e) {
            log.warn("Invalid login attempt with username = " + username + " and password = " + password);
        }
        
        return customer;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.CustomerDao#getCustomer(java.lang.String)
     */
    public Customer getCustomer(String ssn) {
        final String SQL = BASE_QUERY_SQL + " WHERE ssn = ?";
        
        Customer customer = null;
        
        try {
            log.info("Getting customer object for ssn = " + ssn);
            customer = getSimpleJdbcTemplate().queryForObject(
                    SQL, new CustomerMapper(), ssn);
        } catch (DataAccessException e) {
            log.warn("Invalid customer lookup attempt with ssn = " + ssn);
        }
        
        return customer;
    }

    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.CustomerDao#createCustomer(com.parasoft.parabank.domain.Customer)
     */
    public int createCustomer(Customer customer) {
        final String SQL = "INSERT INTO Customer (id, first_name, last_name, address, city, state, zip_code, phone_number, ssn, username, password) VALUES (:id, :firstName, :lastName, :address.street, :address.city, :address.state, :address.zipCode, :phoneNumber, :ssn, :username, :password)";
        
        int id = sequenceDao.getNextId("Customer");
        customer.setId(id);
        
        getSimpleJdbcTemplate().update(SQL, new BeanPropertySqlParameterSource(customer));
        log.info("Created new customer with id = " + id);
        
        return id;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.CustomerDao#updateCustomer(com.parasoft.parabank.domain.Customer)
     */
    public void updateCustomer(Customer customer) {
        final String SQL = "UPDATE Customer SET first_name = :firstName, last_name = :lastName, address = :address.street, city = :address.city, state = :address.state, zip_code = :address.zipCode, phone_number = :phoneNumber, ssn = :ssn, username = :username, password = :password WHERE id = :id";
        
        getSimpleJdbcTemplate().update(SQL, new BeanPropertySqlParameterSource(customer));
        log.info("Updated information for customer with id = " + customer.getId());
    }
    
    private static class CustomerMapper implements RowMapper<Customer> {
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Customer customer = new Customer();
            customer.setId(rs.getInt("id"));
            customer.setFirstName(rs.getString("first_name"));
            customer.setLastName(rs.getString("last_name"));
            Address address = new Address();
            address.setStreet(rs.getString("address"));
            address.setCity(rs.getString("city"));
            address.setState(rs.getString("state"));
            address.setZipCode(rs.getString("zip_code"));
            customer.setAddress(address);
            customer.setPhoneNumber(rs.getString("phone_number"));
            customer.setSsn(rs.getString("ssn"));
            customer.setUsername(rs.getString("username"));
            customer.setPassword(rs.getString("password"));
            return customer;
        }
    }
}
