package com.parasoft.parabank.domain.logic.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.parasoft.parabank.dao.AccountDao;
import com.parasoft.parabank.dao.AdminDao;
import com.parasoft.parabank.dao.CustomerDao;
import com.parasoft.parabank.dao.InMemoryAccountDao;
import com.parasoft.parabank.dao.InMemoryAdminDao;
import com.parasoft.parabank.dao.InMemoryCustomerDao;
import com.parasoft.parabank.dao.InMemoryPositionDao;
import com.parasoft.parabank.dao.InMemoryTransactionDao;
import com.parasoft.parabank.dao.PositionDao;
import com.parasoft.parabank.dao.TransactionDao;
import com.parasoft.parabank.domain.Account;
import com.parasoft.parabank.domain.Customer;
import com.parasoft.parabank.domain.HistoryPoint;
import com.parasoft.parabank.domain.Position;
import com.parasoft.parabank.domain.Transaction;
import com.parasoft.parabank.domain.Transaction.TransactionType;
import com.parasoft.parabank.domain.logic.AdminParameters;
import com.parasoft.parabank.domain.logic.BankManager;

import junit.framework.TestCase;

public class BankManagerImplTest extends TestCase {
    private static final int ACCOUNT1_ID = 1;
    private static final int ACCOUNT2_ID = 2;
    private static final int ACCOUNT3_ID = 3;
    private static final int CUSTOMER_ID = 3;
    private static final String TRANSACTION_MESSAGE = "Transaction";
    private static final BigDecimal INITIAL_BALANCE = new BigDecimal("1111.11");
    private static final BigDecimal MINIMUM_BALANCE = new BigDecimal("2222.22");
    private static final int POSITION1_ID = 1;
    private static final int POSITION2_ID = 2;
    private static final String NAME = "Test Company";
    private static final String SYMBOL = "TC";
    private static final int SHARES = 10;
    private static final BigDecimal PRICEPERSHARE = new BigDecimal("10.00");
    
    private BankManager bankManager;
    
    private AccountDao accountDao;
    private CustomerDao customerDao;
    private PositionDao positionDao;
    private TransactionDao transactionDao;
    private AdminDao adminDao;
    
    @Override
    protected void setUp() throws Exception {
        List<Account> accounts = new ArrayList<Account>();
        
        Account account1 = new Account();
        account1.setId(ACCOUNT1_ID);
        account1.setBalance(new BigDecimal(100));
        accounts.add(account1);
        
        Account account2 = new Account();
        account2.setId(ACCOUNT2_ID);
        account2.setBalance(new BigDecimal(200));
        accounts.add(account2);
        
        Account account3 = new Account();
        account3.setId(ACCOUNT3_ID);
        account3.setBalance(new BigDecimal(100));
        account3.setCustomerId(CUSTOMER_ID);
        accounts.add(account3);

        accountDao = new InMemoryAccountDao(accounts);

        List<Customer> customers = new ArrayList<Customer>();
        
        Customer customer = new Customer();
        customer.setId(3);
        customers.add(customer);
        
        customerDao = new InMemoryCustomerDao(customers);

        List<Position> positions = new ArrayList<Position>();

        Position position1 = new Position();
        position1.setPositionId(POSITION1_ID);
        position1.setSymbol(SYMBOL);
        positions.add(position1);
        
        Position position2 = new Position();
        position2.setPositionId(POSITION2_ID);
        position2.setSymbol(SYMBOL);
        positions.add(position2);
        
        List<HistoryPoint> history = new ArrayList<HistoryPoint>();
        
        HistoryPoint historyPoint = new HistoryPoint();
        Calendar calendar = Calendar.getInstance();
        historyPoint.setDate(calendar.getTime());
        historyPoint.setSymbol(SYMBOL);
        history.add(historyPoint);
        
        historyPoint = new HistoryPoint();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        historyPoint.setDate(calendar.getTime());
        historyPoint.setSymbol(SYMBOL);
        history.add(historyPoint);
        
        positionDao = new InMemoryPositionDao(positions, history);
        transactionDao = new InMemoryTransactionDao();
        adminDao = new InMemoryAdminDao();
        
        adminDao.setParameter(AdminParameters.INITIAL_BALANCE, INITIAL_BALANCE.toString());
        adminDao.setParameter(AdminParameters.MINIMUM_BALANCE, MINIMUM_BALANCE.toString());
        
        BankManagerImpl bankManager = new BankManagerImpl();
        bankManager.setAccountDao(accountDao);
        bankManager.setCustomerDao(customerDao);
        bankManager.setPositionDao(positionDao);
        bankManager.setTransactionDao(transactionDao);
        bankManager.setAdminDao(adminDao);
        
        this.bankManager = bankManager;
    }
    
    public void testCreateCustomer() {
        int id = bankManager.createCustomer(new Customer());
        assertEquals(2, id);
        List<Account> accounts = accountDao.getAccountsForCustomerId(id);
        assertEquals(1, accounts.size());
        Account account = accounts.get(0);
        assertEquals(INITIAL_BALANCE, account.getBalance());
    }
    
    public void testCreateAccount() {
        Account account1 = bankManager.getAccount(1);
        List<Transaction> transactions = bankManager.getTransactionsForAccount(account1);
        assertEquals(0, transactions.size());
        
        Account account2 = new Account();
        account2.setBalance(BigDecimal.ZERO);
        int id = bankManager.createAccount(account2, account1.getId());
        assertEquals(id, account2.getId());
        
        account2 = bankManager.getAccount(account2.getId());
        assertEquals(MINIMUM_BALANCE, account2.getBalance());
        
        transactions = bankManager.getTransactionsForAccount(account1);
        assertEquals(1, transactions.size());
        
        transactions = bankManager.getTransactionsForAccount(account2);
        assertEquals(1, transactions.size());
    }
    
    public void testCreatePosition() {
        Position position = bankManager.createPosition(CUSTOMER_ID, NAME, SYMBOL, SHARES, PRICEPERSHARE);
        assertNotNull(position);
        assertEquals(CUSTOMER_ID, position.getCustomerId());
        assertEquals(NAME, position.getName());
        assertEquals(SYMBOL, position.getSymbol());
        assertEquals(SHARES, position.getShares());
        assertEquals(PRICEPERSHARE, position.getPurchasePrice());
    }
    
    public void testTransfer() {
        final BigDecimal AMOUNT = new BigDecimal(50);
        
        bankManager.transfer(ACCOUNT1_ID, ACCOUNT2_ID, AMOUNT);
        
        Account account1 = accountDao.getAccount(ACCOUNT1_ID);
        assertEquals(new BigDecimal(50), account1.getBalance());

        assertEquals(1, transactionDao.getTransactionsForAccount(ACCOUNT1_ID).size());
        
        Transaction transaction = transactionDao.getTransactionsForAccount(ACCOUNT1_ID).get(0);
        assertEquals(ACCOUNT1_ID, transaction.getAccountId());
        assertEquals(TransactionType.Debit, transaction.getType());
        assertEquals(AMOUNT, transaction.getAmount());
        assertEquals("Funds Transfer Sent", transaction.getDescription());
        
        assertEquals(1, transactionDao.getTransactionsForAccount(ACCOUNT2_ID).size());
        
        transaction = transactionDao.getTransactionsForAccount(ACCOUNT2_ID).get(0);
        assertEquals(ACCOUNT2_ID, transaction.getAccountId());
        assertEquals(TransactionType.Credit, transaction.getType());
        assertEquals(AMOUNT, transaction.getAmount());
        assertEquals("Funds Transfer Received", transaction.getDescription());
        
        Account account2 = accountDao.getAccount(ACCOUNT2_ID);
        assertEquals(new BigDecimal(250), account2.getBalance());
        
        bankManager.transfer(ACCOUNT2_ID, ACCOUNT1_ID, new BigDecimal(100));
        
        account1 = accountDao.getAccount(ACCOUNT1_ID);
        account2 = accountDao.getAccount(ACCOUNT2_ID);
        assertEquals(account1.getBalance(), account2.getBalance());
        
        assertEquals(2, transactionDao.getTransactionsForAccount(ACCOUNT1_ID).size());
        
        transaction = transactionDao.getTransactionsForAccount(ACCOUNT1_ID).get(0);
        assertEquals(ACCOUNT1_ID, transaction.getAccountId());
        assertEquals(TransactionType.Debit, transaction.getType());
        assertEquals(AMOUNT, transaction.getAmount());
        assertEquals("Funds Transfer Sent", transaction.getDescription());
        
        assertEquals(2, transactionDao.getTransactionsForAccount(ACCOUNT2_ID).size());
        
        transaction = transactionDao.getTransactionsForAccount(ACCOUNT2_ID).get(0);
        assertEquals(ACCOUNT2_ID, transaction.getAccountId());
        assertEquals(TransactionType.Credit, transaction.getType());
        assertEquals(AMOUNT, transaction.getAmount());
        assertEquals("Funds Transfer Received", transaction.getDescription());
    }
    
    public void testWithdraw() {
        final BigDecimal AMOUNT1 = new BigDecimal(50);
        
        bankManager.withdraw(ACCOUNT1_ID, AMOUNT1, TRANSACTION_MESSAGE);
        
        Account account1 = accountDao.getAccount(ACCOUNT1_ID);
        assertEquals(new BigDecimal(50), account1.getBalance());
        assertEquals(1, transactionDao.getTransactionsForAccount(ACCOUNT1_ID).size());
        
        Transaction transaction = transactionDao.getTransactionsForAccount(ACCOUNT1_ID).get(0);
        assertEquals(ACCOUNT1_ID, transaction.getAccountId());
        assertEquals(TransactionType.Debit, transaction.getType());
        assertEquals(AMOUNT1, transaction.getAmount());
        assertEquals(TRANSACTION_MESSAGE, transaction.getDescription());
        
        final BigDecimal AMOUNT2 = new BigDecimal(300);
        
        bankManager.withdraw(ACCOUNT2_ID, AMOUNT2, TRANSACTION_MESSAGE);
        
        Account account2 = accountDao.getAccount(ACCOUNT2_ID);
        assertEquals(new BigDecimal(-100), account2.getBalance());
        assertEquals(1, transactionDao.getTransactionsForAccount(ACCOUNT2_ID).size());
        
        transaction = transactionDao.getTransactionsForAccount(ACCOUNT2_ID).get(0);
        assertEquals(ACCOUNT2_ID, transaction.getAccountId());
        assertEquals(TransactionType.Debit, transaction.getType());
        assertEquals(AMOUNT2, transaction.getAmount());
        assertEquals(TRANSACTION_MESSAGE, transaction.getDescription());
    }
    
    public void testDeposit() {
        final BigDecimal AMOUNT1 = new BigDecimal(50);
        
        bankManager.deposit(ACCOUNT1_ID, AMOUNT1, TRANSACTION_MESSAGE);
        
        Account account1 = accountDao.getAccount(ACCOUNT1_ID);
        assertEquals(new BigDecimal(150), account1.getBalance());
        assertEquals(1, transactionDao.getTransactionsForAccount(ACCOUNT1_ID).size());
        
        Transaction transaction = transactionDao.getTransactionsForAccount(ACCOUNT1_ID).get(0);
        assertEquals(ACCOUNT1_ID, transaction.getAccountId());
        assertEquals(TransactionType.Credit, transaction.getType());
        assertEquals(AMOUNT1, transaction.getAmount());
        assertEquals(TRANSACTION_MESSAGE, transaction.getDescription());
        
        final BigDecimal AMOUNT2 = new BigDecimal(300);
        
        bankManager.deposit(ACCOUNT2_ID, AMOUNT2, TRANSACTION_MESSAGE);
        
        Account account2 = accountDao.getAccount(ACCOUNT2_ID);
        assertEquals(new BigDecimal(500), account2.getBalance());
        assertEquals(1, transactionDao.getTransactionsForAccount(ACCOUNT2_ID).size());
        
        transaction = transactionDao.getTransactionsForAccount(ACCOUNT2_ID).get(0);
        assertEquals(ACCOUNT2_ID, transaction.getAccountId());
        assertEquals(TransactionType.Credit, transaction.getType());
        assertEquals(AMOUNT2, transaction.getAmount());
        assertEquals(TRANSACTION_MESSAGE, transaction.getDescription());
    }

    public void testBuyAndSellPosition() {
        final BigDecimal AMOUNT1 = new BigDecimal(10000);
        final String NAME = "Test Company";
        final String SYMBOL = "TC";
        final int SHARES = 10;
        final BigDecimal PRICE = new BigDecimal(100);
        
        BigDecimal balance = bankManager.getAccount(ACCOUNT3_ID).getAvailableBalance();
        bankManager.deposit(ACCOUNT3_ID, AMOUNT1, TRANSACTION_MESSAGE);
        assertEquals(balance.add(AMOUNT1), bankManager.getAccount(ACCOUNT3_ID).getAvailableBalance());
        balance = balance.add(AMOUNT1);
        
        int customerId = bankManager.getAccount(ACCOUNT3_ID).getCustomerId();
        Customer customer = bankManager.getCustomer(customerId);
        assertEquals(customerId, customer.getId());
        List<Position> currentList = bankManager.getPositionsForCustomer(customer);
        int size = currentList.size();
        int[] positionIds = new int[size];
        int iterator = 0;
        for (Position pos : currentList) {
            positionIds[iterator] = pos.getPositionId();
            iterator++;
        }
        
        List<Position> positions = bankManager.buyPosition(customerId, ACCOUNT3_ID, NAME, SYMBOL, SHARES, PRICE);
        assertEquals(size+1, positions.size());
        
        int newPositionId = -1;
        for (Position pos : positions) {
            boolean found = false;
            int id = pos.getPositionId();
            for (int i = 0; i < positionIds.length; i++) {
                if (id == positionIds[i]) {
                    found = true;
                }
            }
            if (found == false) {
                newPositionId = id;
            }
        }

        balance = balance.subtract(new BigDecimal(1000));
        Position position = bankManager.getPosition(newPositionId);
        assertNotNull(position);
        assertEquals(newPositionId, position.getPositionId());
        assertEquals(customerId, position.getCustomerId());
        assertEquals(NAME, position.getName());
        assertEquals(SYMBOL, position.getSymbol());
        assertEquals(SHARES, position.getShares());
        assertEquals(PRICE, position.getPurchasePrice());
        BigDecimal costBasis = new BigDecimal(1000);
        assertEquals(costBasis, position.getCostBasis());
        
        Account account = bankManager.getAccount(ACCOUNT3_ID);
        BigDecimal availableBalance = account.getAvailableBalance();
        assertEquals(balance, availableBalance);
        
        positions = bankManager.sellPosition(customerId, ACCOUNT3_ID, newPositionId, 5, PRICE);
        assertEquals(size+1, positions.size());

        position = bankManager.getPosition(newPositionId);
        assertNotNull(position);
        assertEquals(newPositionId, position.getPositionId());
        assertEquals(customerId, position.getCustomerId());
        assertEquals(NAME, position.getName());
        assertEquals(SYMBOL, position.getSymbol());
        assertEquals(SHARES-5, position.getShares());
        assertEquals(PRICE, position.getPurchasePrice());
        costBasis = new BigDecimal(500);
        assertEquals(costBasis, position.getCostBasis());
        balance = balance.add(costBasis);
        account = bankManager.getAccount(ACCOUNT3_ID);
        availableBalance = account.getAvailableBalance();
        assertEquals(balance, availableBalance);
        
        positions = bankManager.sellPosition(customerId, ACCOUNT3_ID, newPositionId, 5, PRICE);
        assertEquals(size, positions.size());

        position = bankManager.getPosition(newPositionId);
        assertNull(position);

        costBasis = new BigDecimal(500);
        balance = balance.add(costBasis);
        account = bankManager.getAccount(ACCOUNT3_ID);
        availableBalance = account.getAvailableBalance();
        assertEquals(balance, availableBalance);
    }
}
