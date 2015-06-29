package com.parasoft.parabank.dao.jdbc;

import org.springframework.dao.DataAccessException;

import com.parasoft.parabank.dao.CustomerDao;
import com.parasoft.parabank.domain.Address;
import com.parasoft.parabank.domain.Customer;
import com.parasoft.parabank.test.util.AbstractParaBankDataSourceTest;

@SuppressWarnings("deprecation")
public class JdbcCustomerDaoTest extends AbstractParaBankDataSourceTest {
    private static final String FIRST_NAME = "Steve";
    private static final String LAST_NAME = "Jobs";
    private static final String STREET = "1 Infinite Loop";
    private static final String CITY = "Cupertino";
    private static final String STATE = "CA";
    private static final String ZIP_CODE = "95014";
    private static final String PHONE_NUMBER = "1-800-MY-APPLE";
    private static final String SSN = "666-66-6666";
    private static final String USERNAME = "steve";
    private static final String PASSWORD = "jobs";
    
    private CustomerDao customerDao;
    private Customer customer;
    
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        customer = new Customer();
        customer.setFirstName(FIRST_NAME);
        customer.setLastName(LAST_NAME);
        Address address = new Address();
        address.setStreet(STREET);
        address.setCity(CITY);
        address.setState(STATE);
        address.setZipCode(ZIP_CODE);
        customer.setAddress(address);
        customer.setPhoneNumber(PHONE_NUMBER);
        customer.setSsn(SSN);
        customer.setUsername(USERNAME);
        customer.setPassword(PASSWORD);
    }
    
    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }
    
    public void testGetCustomer() {
        Customer customer = customerDao.getCustomer(12212);
        defaultCustomerTest(customer);
        
        try {
            customer = customerDao.getCustomer(-1);
            fail("did not throw expected DataAccessException");
        } catch (DataAccessException e) { }
    }
    
    public void testGetCustomerByUsername() {
        Customer customer = customerDao.getCustomer("john", "demo");
        defaultCustomerTest(customer);
        
        assertNull(customerDao.getCustomer(null, null));
        assertNull(customerDao.getCustomer("john", null));
        assertNull(customerDao.getCustomer(null, "demo"));
        assertNull(customerDao.getCustomer("john", "foo"));
        assertNull(customerDao.getCustomer("foo", "demo"));
        assertNull(customerDao.getCustomer("foo", "bar"));
    }
    
    public void testGetCustomerBySSN() {
        Customer customer = customerDao.getCustomer("622-11-9999");
        defaultCustomerTest(customer);
        
        assertNull(customerDao.getCustomer(null));
        assertNull(customerDao.getCustomer("foo"));
        assertNull(customerDao.getCustomer("111-11-1111"));
    }
    
    private void defaultCustomerTest(Customer customer) {
        assertEquals(12212, customer.getId());
        assertEquals("John", customer.getFirstName());
        assertEquals("Smith", customer.getLastName());
        assertEquals("1431 Main St", customer.getAddress().getStreet());
        assertEquals("Beverly Hills", customer.getAddress().getCity());
        assertEquals("CA", customer.getAddress().getState());
        assertEquals("90210", customer.getAddress().getZipCode());       
        assertEquals("310-447-4121", customer.getPhoneNumber());
        assertEquals("622-11-9999", customer.getSsn());
        assertEquals("john", customer.getUsername());
        assertEquals("demo", customer.getPassword());
    }
    
    public void testCreateCustomer() {
        int id = customerDao.createCustomer(this.customer);
        assertEquals("wrong expected id?", 12434, id);
        
        Customer customer = customerDao.getCustomer(id);
        assertFalse(this.customer == customer);
        assertEquals(this.customer, customer);
    }
    
    public void testUpdateCustomer() {
        int id = customerDao.createCustomer(this.customer);
        
        Customer customer = customerDao.getCustomer(id);
        assertFalse(this.customer == customer);
        assertEquals(this.customer, customer);
        
        customer.setFirstName(customer.getFirstName() + "*");
        customer.setLastName(customer.getLastName() + "*");
        Address address = new Address();
        address.setStreet(customer.getAddress().getStreet() + "*");
        address.setCity(customer.getAddress().getCity() + "*");
        address.setState(customer.getAddress().getState() + "*");
        address.setZipCode(customer.getAddress().getZipCode() + "*");
        customer.setAddress(address);
        customer.setPhoneNumber(customer.getPhoneNumber() + "*");
        customer.setSsn(customer.getSsn() + "*");
        customer.setUsername(customer.getUsername() + "*");
        customer.setPassword(customer.getPassword() + "*");
        
        customerDao.updateCustomer(customer);
        
        Customer updatedCustomer = customerDao.getCustomer(id);
        assertFalse(customer == updatedCustomer);
        assertFalse(this.customer.equals(updatedCustomer));
        assertEquals(customer, updatedCustomer);
    }
}
