package com.parasoft.parabank.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import com.parasoft.parabank.dao.jdbc.internal.StockDataInserter;
import com.parasoft.parabank.domain.Account;
import com.parasoft.parabank.domain.Customer;
import com.parasoft.parabank.domain.HistoryPoint;
import com.parasoft.parabank.domain.LoanResponse;
import com.parasoft.parabank.domain.Position;
import com.parasoft.parabank.domain.Transaction;
import com.parasoft.parabank.test.util.AbstractParaBankDataSourceTest;
import com.parasoft.parabank.util.Util;

@SuppressWarnings("deprecation")
public class ParaBankServiceImplTest extends AbstractParaBankDataSourceTest {
    private static final int CUSTOMER_ID = 12212;
    private static final int ACCOUNT1_ID = 12345;
    private static final int ACCOUNT2_ID = 54321;
    private static final int TRANSACTION_ID = 13033;
    private static final int POSITION_ID = 12345;
    private static final BigDecimal ONE_HUNDRED_DOLLARS = new BigDecimal("100.00");
    private static final String NAME = "Test Company";
    private static final String SYMBOL = "TC";
    
    private ParaBankService paraBankService;
    private StockDataInserter stockDataInserter;

    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        stockDataInserter.insertData();
    }
    
    public void setParaBankService(ParaBankService paraBankService) {
        this.paraBankService = paraBankService;
    }
    
    public void setStockDataInserter(StockDataInserter stockDataInserter) {
        this.stockDataInserter = stockDataInserter;
    }
    
    public void testLogin() throws Exception {
        assertEquals(12212, paraBankService.login("john", "demo").getId());
        
        try {
            paraBankService.login(null, null);
            paraBankService.login("", null);
            paraBankService.login(null, "");
            paraBankService.login("", "");
            fail("Did not throw expected ParaBankServiceException");
        } catch (ParaBankServiceException e) { }
    }
    
    public void testGetCustomer() throws Exception {
        Customer customer = paraBankService.getCustomer(CUSTOMER_ID);
        assertEquals(CUSTOMER_ID, customer.getId());
        assertEquals("John Smith", customer.getFullName());
        
        try {
            paraBankService.getCustomer(-1);
            fail("Did not throw expected ParaBankServiceException");
        } catch (ParaBankServiceException e) { }
    }
    
    public void testGetAccounts() throws Exception {
        List<Account> accounts = paraBankService.getAccounts(CUSTOMER_ID);
        assertEquals(11, accounts.size());
        
        try {
            paraBankService.getAccounts(-1);
            fail("Did not throw expected ParaBankServiceException");
        } catch (ParaBankServiceException e) { }
    }
    
    public void testGetAccount() throws Exception {
        Account account = paraBankService.getAccount(ACCOUNT1_ID);
        assertEquals(ACCOUNT1_ID, account.getId());
        assertEquals(new BigDecimal("-2300.00"), account.getBalance());
        
        try {
            paraBankService.getAccount(-1);
            fail("Did not throw expected ParaBankServiceException");
        } catch (ParaBankServiceException e) { }
    }
    
    public void testGetPositions() throws Exception {
        List<Position> positions = paraBankService.getPositions(CUSTOMER_ID);
        assertEquals(3, positions.size());
        
        try {
            paraBankService.getPositions(-1);
            fail("Did not throw expected ParaBankServiceException");
        } catch (ParaBankServiceException e) { }
    }
    
    public void testGetPositionHistory() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String endDate = Util.DATE_TIME_FORMATTER.get().format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, -10);
        String startDate = Util.DATE_TIME_FORMATTER.get().format(calendar.getTime());
        List<HistoryPoint> history = paraBankService.getPositionHistory(POSITION_ID, startDate, endDate);
        assertEquals(11, history.size());
        
        try {
            paraBankService.getPositionHistory(-1, startDate, endDate);
            fail("Did not throw expected ParaBankServiceException");
        } catch (ParaBankServiceException e) { }
    }
    
    public void testGetPosition() throws Exception {
        Position position = paraBankService.getPosition(POSITION_ID);
        assertEquals(POSITION_ID, position.getPositionId());
        assertEquals(new BigDecimal("23.53"), position.getPurchasePrice());
        
        try {
            paraBankService.getPosition(-1);
            fail("Did not throw expected ParaBankServiceException");
        } catch (ParaBankServiceException e) { }
    }
    
    public void testBuyAndSellPosition() throws Exception {
        Position position = paraBankService.getPosition(POSITION_ID);
        assertEquals(POSITION_ID, position.getPositionId());
        assertEquals(new BigDecimal("23.53"), position.getPurchasePrice());
        BigDecimal cost = position.getPurchasePrice();
        
        Account account = paraBankService.getAccount(ACCOUNT2_ID);
        assertEquals(ACCOUNT2_ID, account.getId());
        BigDecimal balance = account.getAvailableBalance();
        
        List<Position> currentList = paraBankService.getPositions(CUSTOMER_ID);
        int size = currentList.size();
        int[] positionIds = new int[size];
        int iterator = 0;
        for (Position pos : currentList) {
            positionIds[iterator]=pos.getPositionId();
            iterator++;
        }
        
        //position = paraBankService.buyPosition(CUSTOMER_ID, ACCOUNT2_ID, NAME, SYMBOL, 1, cost);
        List<Position> positions = paraBankService.buyPosition(CUSTOMER_ID, ACCOUNT2_ID, NAME, SYMBOL, 1, cost);
        assertEquals(size+1, positions.size());
        
        account = paraBankService.getAccount(ACCOUNT2_ID);
        assertEquals(ACCOUNT2_ID, account.getId());
        balance = balance.subtract(cost);
        assertEquals(balance, account.getAvailableBalance());
        
        //int positionId = position.getPositionId();
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
        
        position = paraBankService.getPosition(newPositionId);
        assertNotNull(position.getPositionId());
        assertEquals(cost, position.getCostBasis());

        positions = paraBankService.sellPosition(CUSTOMER_ID, ACCOUNT2_ID, newPositionId, 1, cost);
        assertEquals(size, positions.size());

        account = paraBankService.getAccount(ACCOUNT2_ID);
        assertEquals(ACCOUNT2_ID, account.getId());
        balance = balance.add(cost);
        assertEquals(balance, account.getAvailableBalance());

        try {
            paraBankService.getPosition(newPositionId);
            fail("Did not throw expected ParaBankServiceException");
        } catch (ParaBankServiceException e) { }
    }
    
    public void testGetTransactions() throws Exception{
        List<Transaction> transactions = paraBankService.getTransactions(ACCOUNT1_ID);
        assertEquals(7, transactions.size());
        
        try {
            paraBankService.getTransactions(-1);
            fail("Did not throw expected ParaBankServiceException");
        } catch (ParaBankServiceException e) { }
    }
    
    public void testGetTransaction() throws Exception {
        Transaction transaction = paraBankService.getTransaction(TRANSACTION_ID);
        assertEquals(TRANSACTION_ID, transaction.getId());
        assertEquals(ONE_HUNDRED_DOLLARS, transaction.getAmount());
        
        try {
            paraBankService.getTransaction(-1);
            fail("Did not throw expected ParaBankServiceException");
        } catch (ParaBankServiceException e) { }
    }
    
    public void testDeposit() throws Exception {
        String message = paraBankService.deposit(ACCOUNT1_ID, ONE_HUNDRED_DOLLARS);
        assertEquals("Successfully deposited $" + ONE_HUNDRED_DOLLARS +
                " to account #" + ACCOUNT1_ID, message);
        
        try {
            paraBankService.deposit(-1, ONE_HUNDRED_DOLLARS);
            fail("Did not throw expected ParaBankServiceException");
        } catch (ParaBankServiceException e) { }
    }
    
    public void testWithdraw() throws Exception {
        String message = paraBankService.withdraw(ACCOUNT1_ID, ONE_HUNDRED_DOLLARS);
        assertEquals("Successfully withdrew $" + ONE_HUNDRED_DOLLARS +
                " from account #" + ACCOUNT1_ID, message);
        
        try {
            paraBankService.withdraw(-1, ONE_HUNDRED_DOLLARS);
            fail("Did not throw expected ParaBankServiceException");
        } catch (ParaBankServiceException e) { }
    }
    
    public void testTransfer() throws Exception {
        String message = paraBankService.transfer(ACCOUNT1_ID, ACCOUNT2_ID, ONE_HUNDRED_DOLLARS);
        assertEquals("Successfully transferred $" + ONE_HUNDRED_DOLLARS +
                " from account #" + ACCOUNT1_ID + " to account #" + ACCOUNT2_ID, message);
        
        try {
            paraBankService.transfer(-1, ACCOUNT2_ID, ONE_HUNDRED_DOLLARS);
            paraBankService.transfer(ACCOUNT1_ID, -1, ONE_HUNDRED_DOLLARS);
            paraBankService.transfer(-1, -1, ONE_HUNDRED_DOLLARS);
            fail("Did not throw expected ParaBankServiceException");
        } catch (ParaBankServiceException e) { }
    }
    
    public void testRequestLoan() throws Exception {
        LoanResponse response = paraBankService.requestLoan(CUSTOMER_ID, ONE_HUNDRED_DOLLARS, ONE_HUNDRED_DOLLARS, ACCOUNT1_ID);
        assertTrue(response.isApproved());
        assertNotNull(response.getResponseDate());
        
        try {
            paraBankService.requestLoan(-1, ONE_HUNDRED_DOLLARS, ONE_HUNDRED_DOLLARS, ACCOUNT1_ID);
            paraBankService.requestLoan(CUSTOMER_ID, ONE_HUNDRED_DOLLARS, ONE_HUNDRED_DOLLARS, -1);
            paraBankService.requestLoan(-1, ONE_HUNDRED_DOLLARS, ONE_HUNDRED_DOLLARS, -1);
            fail("Did not throw expected ParabankServiceException");
        } catch (ParaBankServiceException e) { }
    }
}
