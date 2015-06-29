package com.parasoft.parabank.domain;

import java.math.BigDecimal;

import com.parasoft.parabank.domain.Account.AccountType;
import com.parasoft.parabank.test.util.AbstractBeanTestCase;

public class AccountTest extends AbstractBeanTestCase<Account> {
    private static final BigDecimal ONE_HUNDRED_DOLLARS = new BigDecimal(100.00);

    public void testGetAndSetType() {
        assertNull(bean.getType());
        bean.setType(0);
        assertEquals(AccountType.CHECKING, bean.getType());
    }
    
    public void testGetAvailableBalance() {
        bean.setBalance(ONE_HUNDRED_DOLLARS);
        assertEquals(ONE_HUNDRED_DOLLARS, bean.getAvailableBalance());
        
        bean.setBalance(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, bean.getAvailableBalance());
        
        bean.setBalance(ONE_HUNDRED_DOLLARS.negate());
        assertEquals(BigDecimal.ZERO, bean.getAvailableBalance());
    }
    
    public void testCredit() {
        bean.setBalance(BigDecimal.ZERO);
        
        bean.credit(ONE_HUNDRED_DOLLARS);
        assertEquals(100, bean.getBalance().intValue());
        
        bean.credit(ONE_HUNDRED_DOLLARS);
        assertEquals(200, bean.getBalance().intValue());
    }
    
    public void testDebit() {
        bean.setBalance(ONE_HUNDRED_DOLLARS);
        
        bean.debit(ONE_HUNDRED_DOLLARS);
        assertEquals(0, bean.getBalance().intValue());
        
        bean.debit(ONE_HUNDRED_DOLLARS);
        assertEquals(-100, bean.getBalance().intValue());
    }
}
