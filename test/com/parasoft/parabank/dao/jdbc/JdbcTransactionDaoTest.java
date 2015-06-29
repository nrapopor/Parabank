package com.parasoft.parabank.dao.jdbc;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.parasoft.parabank.dao.TransactionDao;
import com.parasoft.parabank.domain.Transaction;
import com.parasoft.parabank.domain.Transaction.TransactionType;
import com.parasoft.parabank.domain.TransactionCriteria;
import com.parasoft.parabank.domain.TransactionCriteria.SearchType;
import com.parasoft.parabank.test.util.AbstractParaBankDataSourceTest;

@SuppressWarnings("deprecation")
public class JdbcTransactionDaoTest extends AbstractParaBankDataSourceTest {
    private static final int ACCOUNT_ID = 201;
    private static final TransactionType TYPE = TransactionType.Debit;
    private static final Date DATE = new Date(22222);
    private static final BigDecimal AMOUNT = new BigDecimal("33333.00");
    private static final String DESCRIPTION = "44444";
    
    private TransactionDao transactionDao;
    private Transaction transaction;
    
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        transaction = new Transaction();
        transaction.setAccountId(ACCOUNT_ID);
        transaction.setType(TYPE);
        transaction.setDate(DATE);
        transaction.setAmount(AMOUNT);
        transaction.setDescription(DESCRIPTION);
    }
    
    @Override
    protected void onSetUpInTransaction() throws Exception {
        super.onSetUpInTransaction();
        super.executeSqlScript("classpath:com/parasoft/parabank/dao/jdbc/sql/insertCustomer.sql", true);
        super.executeSqlScript("classpath:com/parasoft/parabank/dao/jdbc/sql/insertAccount.sql", true);
    }
    
    public void setTransactionDao(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }
    
    public void testGetTransaction() {
        Transaction transaction = transactionDao.getTransaction(12256);
        assertEquals(12256, transaction.getId());
        assertEquals(12345, transaction.getAccountId());
        assertEquals(TransactionType.Debit, transaction.getType());
        assertEquals("2009-12-12", transaction.getDate().toString());
        assertEquals(new BigDecimal("100.00"), transaction.getAmount());
        assertEquals("Check # 1211", transaction.getDescription());
        
        try {
            transaction = transactionDao.getTransaction(-1);
            fail("did not throw expected DataAccessException");
        } catch (DataAccessException e) { }
    }
    
    public void testGetTransactionsForAccount() {
        List<Transaction> transactions = transactionDao.getTransactionsForAccount(12345);
        assertEquals(7, transactions.size());
        
        transactions = transactionDao.getTransactionsForAccount(-1);
        assertEquals(0, transactions.size());
    }
    
    public void testGetTransactionsForAccountWithActivityCriterion() {
        TransactionCriteria criteria = new TransactionCriteria();
        criteria.setSearchType(SearchType.ACTIVITY);
        
        List<Transaction> transactions = transactionDao.getTransactionsForAccount(12345, criteria);
        assertEquals(7, transactions.size());
        
        criteria.setMonth("January");
        transactions = transactionDao.getTransactionsForAccount(12345, criteria);
        assertEquals(0, transactions.size());
        
        criteria.setMonth("December");
        transactions = transactionDao.getTransactionsForAccount(12345, criteria);
        assertEquals(2, transactions.size());
        
        criteria.setMonth("All");
        transactions = transactionDao.getTransactionsForAccount(12345, criteria);
        assertEquals(7, transactions.size());
        
        criteria.setMonth("Invalid");
        transactions = transactionDao.getTransactionsForAccount(12345, criteria);
        assertEquals(7, transactions.size());
        
        criteria.setTransactionType("Debit");
        transactions = transactionDao.getTransactionsForAccount(12345, criteria);
        assertEquals(6, transactions.size());
        
        criteria.setTransactionType("Credit");
        transactions = transactionDao.getTransactionsForAccount(12345, criteria);
        assertEquals(1, transactions.size());
        
        criteria.setTransactionType("All");
        transactions = transactionDao.getTransactionsForAccount(12345, criteria);
        assertEquals(7, transactions.size());
        
        criteria.setMonth("December");
        criteria.setTransactionType("Debit");
        transactions = transactionDao.getTransactionsForAccount(12345, criteria);
        assertEquals(1, transactions.size());
    }
    
    public void testGetTransactionsForAccountWithIdCriterion() {
        TransactionCriteria criteria = new TransactionCriteria();
        criteria.setSearchType(SearchType.ID);
        
        List<Transaction> transactions = transactionDao.getTransactionsForAccount(12345, criteria);
        assertEquals(0, transactions.size());
        
        criteria.setTransactionId(12345);
        transactions = transactionDao.getTransactionsForAccount(12345, criteria);
        assertEquals(0, transactions.size());
        
        criteria.setTransactionId(14143);
        transactions = transactionDao.getTransactionsForAccount(12345, criteria);
        assertEquals(1, transactions.size());        
    }
    
    public void testGetTransactionsForAccountWithDateCriterion() {
        TransactionCriteria criteria = new TransactionCriteria();
        criteria.setSearchType(SearchType.DATE);
        
        List<Transaction> transactions = transactionDao.getTransactionsForAccount(12345, criteria);
        assertEquals(0, transactions.size());
        
        criteria.setOnDate(new Date(100, 0, 1));
        transactions = transactionDao.getTransactionsForAccount(12345, criteria);
        assertEquals(0, transactions.size());        
        
        criteria.setOnDate(new Date(110, 7, 23));
        transactions = transactionDao.getTransactionsForAccount(12345, criteria);
        assertEquals(2, transactions.size());        
    }
    
    public void testGetTransactionsForAccountWithDateRangeCriterion() {
        TransactionCriteria criteria = new TransactionCriteria();
        criteria.setSearchType(SearchType.DATE_RANGE);
        
        List<Transaction> transactions = transactionDao.getTransactionsForAccount(12345, criteria);
        assertEquals(0, transactions.size());
        
        criteria.setFromDate(new Date(100, 0, 1));
        transactions = transactionDao.getTransactionsForAccount(12345, criteria);
        assertEquals(0, transactions.size());
        
        criteria.setToDate(new Date(110, 11, 31));
        transactions = transactionDao.getTransactionsForAccount(12345, criteria);
        assertEquals(7, transactions.size());
        
        criteria.setFromDate(new Date(100, 0, 1));
        criteria.setToDate(new Date(110, 11, 31));
        transactions = transactionDao.getTransactionsForAccount(12345, criteria);
        assertEquals(7, transactions.size());
        
        criteria.setFromDate(new Date(110, 7, 1));
        criteria.setToDate(new Date(110, 7, 31));
        transactions = transactionDao.getTransactionsForAccount(12345, criteria);
        assertEquals(5, transactions.size());
        
        criteria.setFromDate(new Date(110, 11, 31));
        criteria.setToDate(new Date(100, 0, 1));
        transactions = transactionDao.getTransactionsForAccount(12345, criteria);
        assertEquals(0, transactions.size());
    }
    
    public void testGetTransactionsForAccountWithAmountCriterion() {
        TransactionCriteria criteria = new TransactionCriteria();
        criteria.setSearchType(SearchType.AMOUNT);
        
        List<Transaction> transactions = transactionDao.getTransactionsForAccount(12345, criteria);
        assertEquals(0, transactions.size());
        
        criteria.setAmount(new BigDecimal(1000));
        transactions = transactionDao.getTransactionsForAccount(12345, criteria);
        assertEquals(3, transactions.size());
        
        criteria.setAmount(new BigDecimal(-1));
        transactions = transactionDao.getTransactionsForAccount(12345, criteria);
        assertEquals(0, transactions.size());        
    }    
    
    public void testCreateTransaction() {
        int id = transactionDao.createTransaction(this.transaction);
        assertEquals("wrong expected id?", 14476, id);
        
        Transaction transaction = transactionDao.getTransaction(id);
        assertFalse(this.transaction == transaction);
        assertEquals(this.transaction, transaction);
    }
}
