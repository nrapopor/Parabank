package com.parasoft.parabank.dao.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.parasoft.parabank.dao.InMemoryTransactionDao;
import com.parasoft.parabank.dao.TransactionDao;
import com.parasoft.parabank.domain.Transaction;
import com.parasoft.parabank.domain.TransactionCriteria;

public class InMemoryTransactionDaoTest extends TestCase {
    private static final int TRANSACTION1_ID = 1;
    private static final int TRANSACTION2_ID = 2;
    private static final int ACCOUNT_ID = 1;
    
    private TransactionDao transactionDao;
    
    @Override
    protected void setUp() throws Exception {
        List<Transaction> transactions = new ArrayList<Transaction>();
        
        Transaction transaction = new Transaction();
        transaction.setId(TRANSACTION1_ID);
        transaction.setAccountId(ACCOUNT_ID);
        transactions.add(transaction);
        
        transaction = new Transaction();
        transaction.setId(TRANSACTION2_ID);
        transaction.setAccountId(ACCOUNT_ID);
        transactions.add(transaction);
        
        transactionDao = new InMemoryTransactionDao(transactions);
    }
    
    public void testGetTransaction() {
        Transaction transaction = transactionDao.getTransaction(TRANSACTION1_ID);
        assertNotNull(transaction);
        assertEquals(TRANSACTION1_ID, transaction.getId());
        
        transaction = transactionDao.getTransaction(TRANSACTION2_ID);
        assertNotNull(transaction);
        assertEquals(TRANSACTION2_ID, transaction.getId());
                
        assertNull(transactionDao.getTransaction(-1));
    }
    
    public void testGetTransactionsForAccount() {
        List<Transaction> transactions = transactionDao.getTransactionsForAccount(ACCOUNT_ID);
        assertEquals(2, transactions.size());
        transactionDao.getTransactionsForAccount(ACCOUNT_ID, new TransactionCriteria());
        assertEquals(2, transactions.size());
        
        transactions = transactionDao.getTransactionsForAccount(-1);
        assertEquals(0, transactions.size());       
        transactions = transactionDao.getTransactionsForAccount(-1, new TransactionCriteria());
        assertEquals(0, transactions.size());
    }
    
    public void testCreateTransaction() {
        Transaction originalTransaction = new Transaction();
        int id = transactionDao.createTransaction(originalTransaction);
        assertEquals(3, id);
        Transaction newAccount = transactionDao.getTransaction(id);
        assertEquals(originalTransaction, newAccount);
    }    
}
