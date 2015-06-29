package com.parasoft.parabank.domain.logic.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.parasoft.parabank.dao.AccountDao;
import com.parasoft.parabank.dao.AdminDao;
import com.parasoft.parabank.dao.CustomerDao;
import com.parasoft.parabank.dao.TransactionDao;
import com.parasoft.parabank.dao.PositionDao;
import com.parasoft.parabank.domain.Account;
import com.parasoft.parabank.domain.Customer;
import com.parasoft.parabank.domain.HistoryPoint;
import com.parasoft.parabank.domain.LoanRequest;
import com.parasoft.parabank.domain.LoanResponse;
import com.parasoft.parabank.domain.Position;
import com.parasoft.parabank.domain.Transaction;
import com.parasoft.parabank.domain.TransactionCriteria;
import com.parasoft.parabank.domain.Account.AccountType;
import com.parasoft.parabank.domain.Transaction.TransactionType;
import com.parasoft.parabank.domain.logic.AdminParameters;
import com.parasoft.parabank.domain.logic.BankManager;
import com.parasoft.parabank.domain.logic.LoanProvider;

public class BankManagerImpl implements BankManager {
    private static final Log log = LogFactory.getLog(BankManagerImpl.class);
    
    private AccountDao accountDao;
    private CustomerDao customerDao;
    private PositionDao positionDao;
    private TransactionDao transactionDao;
    private AdminDao adminDao;
    private LoanProvider loanProvider;

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }
    
    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }
    
    public void setPositionDao(PositionDao positionDao) {
        this.positionDao = positionDao;
    }

    public void setTransactionDao(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }
    
    public void setAdminDao(AdminDao adminDao) {
        this.adminDao = adminDao;
    }
    
    public void setLoanProvider(LoanProvider loanProvider) {
        this.loanProvider = loanProvider;
    }
    
    /* ========================= Customer Methods ========================= */
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#getCustomer(int)
     */
    public Customer getCustomer(int id) {
        return customerDao.getCustomer(id);
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#getCustomer(java.lang.String, java.lang.String)
     */
    public Customer getCustomer(String username, String password) {
        return customerDao.getCustomer(username, password);
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#getCustomer(java.lang.String)
     */
    public Customer getCustomer(String ssn) {
        return customerDao.getCustomer(ssn);
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#createCustomer(com.parasoft.parabank.domain.Customer)
     */
    public int createCustomer(Customer customer) {
        int id = customerDao.createCustomer(customer);
        log.info("Created customer with id = " + id);
        Account account = new Account();
        account.setCustomerId(id);
        account.setType(AccountType.CHECKING);
        account.setBalance(new BigDecimal(adminDao.getParameter(AdminParameters.INITIAL_BALANCE)));
        accountDao.createAccount(account);
        log.info("Created new account with id = " + account.getId() + " and balance " + account.getBalance() + " for customer with id = " + id);
        return id;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#updateCustomer(com.parasoft.parabank.domain.Customer)
     */
    public void updateCustomer(Customer customer) {
        customerDao.updateCustomer(customer);
    }
    
    /* ========================= Account Methods ========================= */
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#getAccount(int)
     */
    public Account getAccount(int id) {
        return accountDao.getAccount(id);
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#getAccountsForCustomer(com.parasoft.parabank.domain.Customer)
     */
    public List<Account> getAccountsForCustomer(Customer customer) {
        return accountDao.getAccountsForCustomerId(customer.getId());
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#createAccount(com.parasoft.parabank.domain.Account)
     */
    public int createAccount(Account account, int fromAccountId) {
        int id = accountDao.createAccount(account);
        
        transfer(fromAccountId, id, new BigDecimal(adminDao.getParameter(AdminParameters.MINIMUM_BALANCE)));
        
        return id;
    }
    
    /* ========================= Position Methods ========================= */
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#getPosition(int)
     */
    public List<Position> getPositionsForCustomer(Customer customer) {
        return positionDao.getPositionsForCustomerId(customer.getId());
    }

    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#getPosition(int)
     */
    public Position getPosition(int positionId) {
        return positionDao.getPosition(positionId);
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#getPositionHistory(int, java.util.Date, java.util.Date)
     */
    public List<HistoryPoint> getPositionHistory(int positionId, Date startDate, Date endDate) {
        return positionDao.getPositionHistory(positionId, startDate, endDate);
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#buyPosition(int, int, java.lang.String, java.lang.String, int, java.math.BigDecimal)
     */
    public List<Position> buyPosition(int customerId, int accountId, String name, String symbol, int shares, BigDecimal pricePerShare) {
        withdraw(accountId, pricePerShare.multiply(new BigDecimal(shares)), "Funds Transfer Sent");
        log.info("Withdrew funds for Stock Purchase");

        Position position = createPosition(customerId, name, symbol, shares, pricePerShare);
        int positionId = positionDao.createPosition(position);
        log.info("Created position with id = " + positionId + " with " + shares + " shares");

        Customer customer = getCustomer(customerId);
        
        return getPositionsForCustomer(customer);
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#sellPosition(int, int, int, int, java.math.BigDecimal)
     */
    public List<Position> sellPosition(int customerId, int accountId, int positionId, int shares, BigDecimal pricePerShare) {
        Position position = positionDao.getPosition(positionId);
        
        if (shares == position.getShares()) {
            deletePosition(position);
            log.info("Deleted position with id = " + position.getPositionId());
        } else {
            int oldShares = position.getShares();
            position.setShares(oldShares - shares);
            updatePosition(position);
            log.info("Updated position with id = " + position.getPositionId() + ": new shares = " + (position.getShares() - shares));
        }            
        deposit(accountId, pricePerShare.multiply(new BigDecimal(shares)), "Funds Transfer Received");
        log.info("Deposited funds from Stock Sale");
        Customer customer = getCustomer(customerId);
        
        return getPositionsForCustomer(customer);
    }

    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#createPosition(com.parasoft.parabank.domain.Position)
     */
    public Position createPosition(int customerId, String name, String symbol, int shares, BigDecimal pricePerShare) {
        Position position = new Position();
        position.setCustomerId(customerId);
        position.setName(name);
        position.setSymbol(symbol);
        position.setShares(shares);
        position.setPurchasePrice(pricePerShare);
        return position;
    }

    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#updatePosition(com.parasoft.parabank.domain.Position)
     */
    public boolean updatePosition(Position position) {
        return positionDao.updatePosition(position);
    }

    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#deletePosition(com.parasoft.parabank.domain.Position)
     */
    public boolean deletePosition(Position position) {
        return positionDao.deletePosition(position);
    }

    /* ========================= Transaction Methods ========================= */
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#getTransaction(int)
     */
    public Transaction getTransaction(int id) {
        return transactionDao.getTransaction(id);
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#getTransactionsForAccount(com.parasoft.parabank.domain.Account)
     */
    public List<Transaction> getTransactionsForAccount(Account account) {
        return transactionDao.getTransactionsForAccount(account.getId());
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#getTransactionsForAccount(com.parasoft.parabank.domain.Account, com.parasoft.parabank.domain.TransactionCriteria)
     */
    public List<Transaction> getTransactionsForAccount(int accountId,
            TransactionCriteria criteria) {
        return transactionDao.getTransactionsForAccount(accountId, criteria);
    }
        
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#transfer(int, int, java.math.BigDecimal)
     */
    public void transfer(int fromAccountId, int toAccountId, BigDecimal amount) {
        withdraw(fromAccountId, amount, "Funds Transfer Sent");
        deposit(toAccountId, amount, "Funds Transfer Received");
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#withdraw(int, java.math.BigDecimal, java.lang.String)
     */
    public void withdraw(int accountId, BigDecimal amount, String description) {
        Account account = accountDao.getAccount(accountId);
        
        account.debit(amount);
        accountDao.updateAccount(account);
        log.info("Debited account with id = " + accountId + " in the amount of " + amount);
        
        Transaction transaction = createTransaction(account, TransactionType.Debit, new Date(), amount, description);
        transactionDao.createTransaction(transaction);
        log.info("Created debit transaction with id = " + transaction.getId() + " with description: " + description);
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#deposit(int, java.math.BigDecimal, java.lang.String)
     */
    public void deposit(int accountId, BigDecimal amount, String description) {
        Account account = accountDao.getAccount(accountId);
        
        account.credit(amount);
        accountDao.updateAccount(account);
        log.info("Credited account with id = " + accountId + " in the amount of " + amount);
        
        Transaction transaction = createTransaction(account, TransactionType.Credit, new Date(), amount, description);
        transactionDao.createTransaction(transaction);
        log.info("Created credit transaction with id = " + transaction.getId() + " with description: " + description);
    }
    
    private Transaction createTransaction(Account account, TransactionType type, Date date, BigDecimal amount, String description) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(account.getId());
        transaction.setType(type);
        transaction.setDate(date);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        return transaction;
    }
    
    /* ========================= Loan Methods ========================= */
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#requestLoan(int, java.math.BigDecimal, java.math.BigDecimal, int)
     */
    public LoanResponse requestLoan(int customerId, BigDecimal amount,
            BigDecimal downPayment, int fromAccountId) {
        List<Account> accounts = accountDao.getAccountsForCustomerId(customerId);
        BigDecimal availableFunds = BigDecimal.ZERO;
        for (Account account : accounts) {
            if (account.getType() != AccountType.LOAN) {
                availableFunds = availableFunds.add(account.getBalance());
            }
        }
        
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setRequestDate(new Date());
        loanRequest.setCustomerId(customerId);
        loanRequest.setAvailableFunds(availableFunds);
        loanRequest.setDownPayment(downPayment);
        loanRequest.setLoanAmount(amount);
        
        log.info("Submitting loan request for customer with id = " + customerId + " in the amount of $" + amount + " at " + loanRequest.getRequestDate());
        LoanResponse loanResponse = loanProvider.requestLoan(loanRequest);
        
        if (loanResponse.isApproved()) {
            Account loanAccount = new Account();
            loanAccount.setCustomerId(customerId);
            loanAccount.setType(AccountType.LOAN);
            loanAccount.setBalance(amount);
            int accountId = accountDao.createAccount(loanAccount);
            loanResponse.setAccountId(accountId);
            withdraw(fromAccountId, downPayment, "Down Payment for Loan # " + accountId);
        }
        
        return loanResponse;
    }    
}
