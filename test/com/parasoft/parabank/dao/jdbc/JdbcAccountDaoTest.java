package com.parasoft.parabank.dao.jdbc;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.parasoft.parabank.dao.AccountDao;
import com.parasoft.parabank.domain.Account;
import com.parasoft.parabank.domain.Account.AccountType;
import com.parasoft.parabank.test.util.AbstractParaBankDataSourceTest;

@SuppressWarnings("deprecation")
public class JdbcAccountDaoTest extends AbstractParaBankDataSourceTest {
    private static final int CUSTOMER_ID = 101;
    private static final AccountType TYPE = AccountType.SAVINGS;
    private static final BigDecimal BALANCE = new BigDecimal("22222.00");
    
    private AccountDao accountDao;
    private Account account;
    
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        account = new Account();
        account.setCustomerId(CUSTOMER_ID);
        account.setType(TYPE);
        account.setBalance(BALANCE);
    }
    
    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }
    
    @Override
    protected void onSetUpInTransaction() throws Exception {
        super.onSetUpInTransaction();
        super.executeSqlScript("classpath:com/parasoft/parabank/dao/jdbc/sql/insertCustomer.sql", true);
    }
    
    public void testGetAccount() {
        Account account = accountDao.getAccount(13455);
        assertEquals(13455, account.getId());
        assertEquals(12323, account.getCustomerId());
        assertEquals(AccountType.CHECKING, account.getType());
        assertEquals(new BigDecimal("2014.76"), account.getBalance());
        
        try {
            account = accountDao.getAccount(-1);
            fail("did not throw expected DataAccessException");
        } catch (DataAccessException e) { }
    }
    
    public void testGetAccountsForCustomerId() {
        List<Account> accounts = accountDao.getAccountsForCustomerId(12212);
        assertEquals("wrong number of accounts?", 11, accounts.size());
        
        accounts = accountDao.getAccountsForCustomerId(-1);
        assertEquals("expected no accounts for invalid id", 0, accounts.size());
    }
    
    public void testCreateAccount() {
        int id = accountDao.createAccount(this.account);
        assertEquals("wrong expected id?", 13566, id);
        
        Account account = accountDao.getAccount(id);
        assertFalse(this.account == account);
        assertEquals(this.account, account);
    }
    
    public void testUpdateAccount() {
        int id = accountDao.createAccount(this.account);
        
        Account account = accountDao.getAccount(id);
        assertFalse(this.account == account);
        assertEquals(this.account, account);
        
        account.setCustomerId(account.getCustomerId() + 1);
        account.setType(AccountType.CHECKING);
        account.setBalance(account.getBalance().add(new BigDecimal(1)));
        assertFalse(this.account.equals(account));

        accountDao.updateAccount(account);
        
        Account updatedAccount = accountDao.getAccount(id);
        assertFalse(account == updatedAccount);
        assertFalse(this.account.equals(updatedAccount));
        assertEquals(account, updatedAccount);
    }
}
