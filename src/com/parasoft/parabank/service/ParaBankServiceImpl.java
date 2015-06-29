package com.parasoft.parabank.service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.List;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;

import com.parasoft.parabank.domain.Account;
import com.parasoft.parabank.domain.Address;
import com.parasoft.parabank.domain.Customer;
import com.parasoft.parabank.domain.HistoryPoint;
import com.parasoft.parabank.domain.LoanResponse;
import com.parasoft.parabank.domain.Position;
import com.parasoft.parabank.domain.Transaction;
import com.parasoft.parabank.domain.TransactionCriteria;
import com.parasoft.parabank.domain.TransactionCriteria.SearchType;
import com.parasoft.parabank.domain.logic.AdminManager;
import com.parasoft.parabank.domain.logic.BankManager;
import com.parasoft.parabank.util.Util;

/*
 * ParaBank web service implementation
 */
@WebService(endpointInterface = "com.parasoft.parabank.service.ParaBankService", serviceName = "ParaBank")
public class ParaBankServiceImpl implements ParaBankService {
    private static final Log log = LogFactory.getLog(ParaBankServiceImpl.class);
    

    private AdminManager adminManager;
    private BankManager bankManager;

    public void setAdminManager(AdminManager adminManager) {
        this.adminManager = adminManager;
    }
    
    public void setBankManager(BankManager bankManager) {
        this.bankManager = bankManager;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.service.ParaBankService#initializeDB()
     */
    public void initializeDB() {
        adminManager.initializeDB();
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.service.ParaBankService#cleanDB()
     */
    public void cleanDB() {
        adminManager.cleanDB();
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.service.ParaBankService#startupJmsListener()
     */
    public void startupJmsListener() {
        adminManager.startupJmsListener();
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.service.ParaBankService#shutdownJmsListener()
     */
    public void shutdownJmsListener() {
        adminManager.shutdownJmsListener();
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.service.ParaBankService#setParameter(java.lang.String, java.lang.String)
     */
    public void setParameter(String name, String value) {
        adminManager.setParameter(name, value);
    }
    
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.service.ParaBankService#login(java.lang.String, java.lang.String)
     */
    public Customer login(String username, String password) throws ParaBankServiceException {
        Customer customer = bankManager.getCustomer(username, password);
        if (customer == null) {
            throw new ParaBankServiceException("Invalid username and/or password");
        }
        return customer;//.getId();
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.service.ParaBankService#getCustomer(int)
     */
    public Customer getCustomer(int customerId) throws ParaBankServiceException {
        try {
            return bankManager.getCustomer(customerId);
        } catch (DataAccessException e) {
            log.error(e);
            throw new ParaBankServiceException("Could not find customer #" + customerId);
        }
    }
    
    /*
	 * (non-Javadoc)
	 * @see com.parasoft.parabank.service.ParaBankService#getAccounts(int)
	 */
	public List<Account> getAccounts(int customerId)
	        throws ParaBankServiceException {
	    try {
	        Customer customer = bankManager.getCustomer(customerId);
	        return bankManager.getAccountsForCustomer(customer);
	    } catch (DataAccessException e) {
	        log.error(e);
	        throw new ParaBankServiceException("Could not find customer #" + customerId);
	    }
	}
	
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.service.ParaBankService#getBalance(int)
     */
    public Account getAccount(int accountId) throws ParaBankServiceException {
        try {
            return bankManager.getAccount(accountId);
        } catch (DataAccessException e) {
            log.error(e);
            throw new ParaBankServiceException("Could not find account #" + accountId);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.service.ParaBankService#getPositions(int)
     */
    public List<Position> getPositions(int customerId)
            throws ParaBankServiceException {
        try {
            Customer customer = bankManager.getCustomer(customerId);
            return bankManager.getPositionsForCustomer(customer);
        } catch (DataAccessException e) {
            log.error(e);
            throw new ParaBankServiceException("Could not find customer #" + customerId);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.service.ParaBankService#getPosition(int)
     */
    public Position getPosition(int positionId) throws ParaBankServiceException {
        try {
            return bankManager.getPosition(positionId);
        } catch (DataAccessException e) {
            log.error(e);
            throw new ParaBankServiceException("Could not find position #" + positionId);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.service.ParaBankService#getPositionHistory(int, java.util.Date, java.util.Date)
     */
    public List<HistoryPoint> getPositionHistory(int positionId, String startDate, String endDate) throws ParaBankServiceException {
        try {
            return bankManager.getPositionHistory(positionId, Util.DATE_TIME_FORMATTER.get().parse(startDate), 
            													Util.DATE_TIME_FORMATTER.get().parse(endDate));
        } catch (Exception e) {
            log.error(e);
            throw new ParaBankServiceException("Could not find position #" + positionId);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#buyPosition(int, int, java.lang.String, java.lang.String, int, java.math.BigDecimal)
     */
    public List<Position> buyPosition(int customerId, int accountId, String name, String symbol, int shares, BigDecimal pricePerShare) 
            throws ParaBankServiceException {
        try {
            return bankManager.buyPosition(customerId, accountId, name, symbol, shares, pricePerShare);
        } catch (DataAccessException e) {
            log.error(e);
            throw new ParaBankServiceException("Could not buy position, Customer ID:" + customerId
                    + ", Account ID:" + accountId + ", Company Name:" + name + ", Company Symbol:"
                    + symbol + ", Shares:" + shares + ", Price:" + pricePerShare);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.BankManager#sellPosition(int, int, int, int, java.math.BigDecimal)
     */
    public List<Position> sellPosition(int customerId, int accountId, int positionId, int shares, BigDecimal pricePerShare) 
            throws ParaBankServiceException {
        try {
            return bankManager.sellPosition(customerId, accountId, positionId, shares, pricePerShare);
        } catch (DataAccessException e) {
            log.error(e);
            throw new ParaBankServiceException("Could not sell position, Customer ID:" + customerId
                    + ", Account ID:" + accountId + ", Position ID:" + positionId + ", Shares:"
                    + shares + ", Price:" + pricePerShare);
        }
    }
    
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.service.ParaBankService#getTransactions(int)
     */
    public List<Transaction> getTransactions(int accountId) throws ParaBankServiceException {
        try {
            Account account = bankManager.getAccount(accountId);
            return bankManager.getTransactionsForAccount(account);
        } catch (DataAccessException e) {
            log.error(e);
            throw new ParaBankServiceException("Could not find transactions for account #" + accountId);
        }
    }
    
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.service.ParaBankService#getTransaction(int)
     */
    public Transaction getTransaction(int transactionId)
            throws ParaBankServiceException {
        try {
            return bankManager.getTransaction(transactionId);
        } catch (DataAccessException e) {
            log.error(e);
            throw new ParaBankServiceException("Could not find transaction #" + transactionId);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.service.ParaBankService#deposit(int, java.math.BigDecimal)
     */
    public String deposit(int accountId, BigDecimal amount) throws ParaBankServiceException {
        try {
            bankManager.deposit(accountId, amount, "Deposit via Web Service");
            return "Successfully deposited $" + amount + " to account #" + accountId;
        } catch (DataAccessException e) {
            log.error(e);
            throw new ParaBankServiceException("Could not find account number " + accountId);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.service.ParaBankService#withdraw(int, java.math.BigDecimal)
     */
    public String withdraw(int accountId, BigDecimal amount) throws ParaBankServiceException {
        try {
            bankManager.withdraw(accountId, amount, "Withdraw via Web Service");
            return "Successfully withdrew $" + amount + " from account #" + accountId;
        } catch (DataAccessException e) {
            log.error(e);
            throw new ParaBankServiceException("Could not find account number " + accountId);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.service.ParaBankService#transfer(int, int, java.math.BigDecimal)
     */
    public String transfer(int fromAccountId, int toAccountId, BigDecimal amount) throws ParaBankServiceException {
        try {
            bankManager.transfer(fromAccountId, toAccountId, amount);
            return "Successfully transferred $" + amount + " from account #" + fromAccountId + " to account #" + toAccountId;
        } catch (DataAccessException e) {
            log.error(e);
            throw new ParaBankServiceException("Could not find account number " + fromAccountId + " and/or " + toAccountId);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.service.ParaBankService#requestLoan(int, java.math.BigDecimal, java.math.BigDecimal, int)
     */
    public LoanResponse requestLoan(int customerId, BigDecimal amount,
            BigDecimal downPayment, int fromAccountId) throws ParaBankServiceException {
        try {
            return bankManager.requestLoan(customerId, amount, downPayment, fromAccountId);
        } catch (DataAccessException e) {
            log.error(e);
            throw new ParaBankServiceException("Could not find account #" + fromAccountId);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.service.ParaBankService#createAccount(int, java.math.BigDecimal, java.math.BigDecimal, int)
     */
	public Account createAccount(int customerId, int newAccountType, 
												int fromAccountId) throws ParaBankServiceException {
		Account newAccount = new Account();
		
		try {
			newAccount = new Account();
			newAccount.setCustomerId(customerId);
			newAccount.setBalance(BigDecimal.ZERO);
			newAccount.setType(newAccountType);
			newAccount.setId(bankManager.createAccount(newAccount, fromAccountId));
			return newAccount;
		} catch (DataAccessException e) {
            log.error(e);
            throw new ParaBankServiceException("Could not create new account for customer "
            									+ customerId + " from account " + fromAccountId);
        }
	}

	/*
	 * (non-Javadoc)
	 * @see com.parasoft.parabank.service.ParaBankService#getTransactionsOnDate(int, java.lang.String)
	 */
	public List<Transaction> getTransactionsOnDate(int accountId, String onDate)
																	throws ParaBankServiceException {
		
		TransactionCriteria criteria = new TransactionCriteria();
		try {
			criteria.setOnDate(TransactionCriteria.DATE_FORMATTER.get().parse(onDate));
		}
		catch (ParseException e) {
			log.error(e);
			throw new ParaBankServiceException("Unable to parse date " + onDate);
		}
		
		criteria.setSearchType(SearchType.DATE);
		return bankManager.getTransactionsForAccount(accountId, criteria);
	}

	/*
	 * (non-Javadoc)
	 * @see com.parasoft.parabank.service.ParaBankService#getTransactions(int, java.lang.String, java.lang.String)
	 */
	public List<Transaction> getTransactionsByToFromDate(int accountId, String fromDate, String toDate)
																		throws ParaBankServiceException {
		
		TransactionCriteria criteria = new TransactionCriteria();
		
		try {
			criteria.setFromDate(TransactionCriteria.DATE_FORMATTER.get().parse(fromDate));
		}
		catch (ParseException e) {
			log.error(e);
			throw new ParaBankServiceException("Unable to parse date " + fromDate);
		}
		
		try {
			criteria.setToDate(TransactionCriteria.DATE_FORMATTER.get().parse(toDate));
		}
		catch (ParseException e) {
			log.error(e);
			throw new ParaBankServiceException("Unable to parse date " + toDate);
		}
		
		criteria.setSearchType(SearchType.DATE_RANGE);
		return bankManager.getTransactionsForAccount(accountId, criteria);
	}

	/*
	 * (non-Javadoc)
	 * @see com.parasoft.parabank.service.ParaBankService#getTransactions(int, java.math.BigDecimal)
	 */
	public List<Transaction> getTransactionsByAmount(int accountId, BigDecimal amount)
																	throws ParaBankServiceException {

		TransactionCriteria criteria = new TransactionCriteria();
		criteria.setAmount(amount);
		criteria.setSearchType(SearchType.AMOUNT);
		return bankManager.getTransactionsForAccount(accountId, criteria);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.parasoft.parabank.service.ParaBankService#getTransactionsByMonthAndType(int, java.lang.String, java.lang.String)
	 */
	public List<Transaction> getTransactionsByMonthAndType(int accountId, String month, String type) throws ParaBankServiceException {
		
		TransactionCriteria criteria = new TransactionCriteria();
		criteria.setSearchType(SearchType.ACTIVITY);
		criteria.setTransactionType(type);
		criteria.setMonth(month);
		return bankManager.getTransactionsForAccount(accountId, criteria);
	}

	
	/*
	 * (non-Javadoc)
	 * @see com.parasoft.parabank.service.ParaBankService#updateCustomer(int, java.lang.String, java.lang.String,
	 *  java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public String updateCustomer(int customerId, String firstName, String lastName, String street, 
									String city, String state, String zipCode, String phoneNumber, 
									String ssn, String username, String password) throws ParaBankServiceException {
		try {
			Address customerAddress = new Address();
			customerAddress.setStreet(URLDecoder.decode(street, "UTF-8"));
			customerAddress.setCity(URLDecoder.decode(city, "UTF-8"));
			customerAddress.setState(URLDecoder.decode(state, "UTF-8"));
			customerAddress.setZipCode(URLDecoder.decode(zipCode, "UTF-8"));
			Customer updatedCustomer = new Customer();
			updatedCustomer.setAddress(customerAddress);
			updatedCustomer.setFirstName(URLDecoder.decode(firstName, "UTF-8"));
			updatedCustomer.setLastName(URLDecoder.decode(lastName, "UTF-8"));
			updatedCustomer.setId(customerId);
			updatedCustomer.setPhoneNumber(URLDecoder.decode(phoneNumber, "UTF-8"));
			updatedCustomer.setSsn(ssn);
			updatedCustomer.setUsername(username);
			updatedCustomer.setPassword(password);
			bankManager.updateCustomer(updatedCustomer);
		}
		catch (UnsupportedEncodingException e) {
			throw new ParaBankServiceException("Unsupported encoding");
		}
		return "Successfully updated customer profile";
	}
}
