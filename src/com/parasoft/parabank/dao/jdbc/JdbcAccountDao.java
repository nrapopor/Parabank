package com.parasoft.parabank.dao.jdbc;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import com.parasoft.parabank.dao.AccountDao;
import com.parasoft.parabank.domain.Account;

/*
 * JDBC implementation of AccountDao
 */
public class JdbcAccountDao extends SimpleJdbcDaoSupport implements AccountDao {
    private static final Log log = LogFactory.getLog(JdbcAccountDao.class);
    
    private JdbcSequenceDao sequenceDao;
    
    public void setSequenceDao(JdbcSequenceDao sequenceDao) {
        this.sequenceDao = sequenceDao;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.AccountDao#getAccount(int)
     */
    public Account getAccount(int id) {
        final String SQL = "SELECT id, customer_id, type, balance FROM Account WHERE id = ?";
        
        log.info("Getting account object for id = " + id);
        Account account = getSimpleJdbcTemplate().queryForObject(
                SQL, new AccountMapper(), id);
        
        return account;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.AccountDao#getAccountsForCustomerId(int)
     */
    public List<Account> getAccountsForCustomerId(int customerId) {
        final String SQL = "SELECT id, customer_id, type, balance FROM Account WHERE customer_id = ?";
        
        List<Account> accounts = getSimpleJdbcTemplate().query(SQL, new AccountMapper(), customerId);
        log.info("Retrieved " + accounts.size() + " accounts for customerId = " + customerId);
        
        return accounts;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.AccountDao#createAccount(com.parasoft.parabank.domain.Account)
     */
    public int createAccount(Account account) {
        final String SQL = "INSERT INTO Account (id, customer_id, type, balance) VALUES (:id, :customerId, :intType, :balance)";
        
        int id = sequenceDao.getNextId("Account");
        account.setId(id);
        
        getSimpleJdbcTemplate().update(SQL, new BeanPropertySqlParameterSource(account));
        log.info("Created new account with id = " + id);
        
        return id;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.AccountDao#updateAccount(com.parasoft.parabank.domain.Account)
     */
    public void updateAccount(Account account) {
        final String SQL = "UPDATE Account SET customer_id = :customerId, type = :intType, balance = :balance WHERE id = :id";
        
        getSimpleJdbcTemplate().update(SQL, new BeanPropertySqlParameterSource(account));
        log.info("Updated information for account with id = " + account.getId());
    }
    
    private static class AccountMapper implements RowMapper<Account> {
        public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
            Account account = new Account();
            account.setId(rs.getInt("id"));
            account.setCustomerId(rs.getInt("customer_id"));
            account.setType(rs.getInt("type"));
            BigDecimal balance = rs.getBigDecimal("balance");
            account.setBalance(balance == null ? null : balance.setScale(2));
            return account;
        }
    }
}
