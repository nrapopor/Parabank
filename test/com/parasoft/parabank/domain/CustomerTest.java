package com.parasoft.parabank.domain;

import com.parasoft.parabank.test.util.AbstractBeanTestCase;

public class CustomerTest extends AbstractBeanTestCase<Customer> {
    public void testGetFullName() {
        bean.setFirstName("first");
        bean.setLastName("last");
        assertEquals("first last", bean.getFullName());
    }
}
