package com.parasoft.parabank.dao.jdbc;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import com.parasoft.parabank.dao.TransactionDao;
import com.parasoft.parabank.domain.Transaction;
import com.parasoft.parabank.domain.TransactionCriteria;

/*
 * JDBC implementation of TransactionDao
 */
public class JdbcTransactionDao extends SimpleJdbcDaoSupport implements TransactionDao {
    private static final Log log = LogFactory.getLog(JdbcTransactionDao.class);
    
    private JdbcSequenceDao sequenceDao;
    
    public void setSequenceDao(JdbcSequenceDao sequenceDao) {
        this.sequenceDao = sequenceDao;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.TransactionDao#getTransaction(int)
     */
    public Transaction getTransaction(int id) {
        final String SQL = "SELECT id, account_id, type, date, amount, description FROM Transaction WHERE id = ? ORDER BY date";
        
        log.info("Getting transaction object for id = " + id);
        Transaction transaction = getSimpleJdbcTemplate().queryForObject(SQL,
                new TransactionMapper(), id);
        
        return transaction;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.TransactionDao#getTransactionsForAccount(int)
     */
    public List<Transaction> getTransactionsForAccount(int accountId) {
        // Return in chronological order.
        final String SQL = "SELECT id, account_id, type, date, amount, description FROM Transaction WHERE account_id = ? ORDER BY date, id";
        
        List<Transaction> transactions = getSimpleJdbcTemplate().query(SQL,
                new TransactionMapper(), accountId);
        log.info("Retrieved " + transactions.size() + " transactions for accountId = " + accountId);
        
        return transactions;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.TransactionDao#getTransactionsForAccount(int, com.parasoft.parabank.domain.TransactionCriteria)
     */
    public List<Transaction> getTransactionsForAccount(int accountId, TransactionCriteria criteria) {
        String SQL = "SELECT id, account_id, type, date, amount, description, MONTH(date) as month FROM Transaction WHERE account_id = ?";
        
        List<Object> params = new ArrayList<Object>(); 
        params.add(accountId);
        
        SQL += new JdbcTransactionQueryRestrictor().getRestrictions(criteria, params);

        // Return in chronological order.
        SQL += " ORDER BY date, id";
        
        List<Transaction> transactions = getSimpleJdbcTemplate().query(SQL,
                new TransactionMapper(), params.toArray());
        log.info("Retrieved " + transactions.size() + " transactions for accountId = " + accountId + " with search type = " + criteria.getSearchType());
        
        return transactions;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.TransactionDao#createTransaction(com.parasoft.parabank.domain.Transaction)
     */
    public int createTransaction(Transaction transaction) {
        final String SQL = "INSERT INTO Transaction (id, account_id, type, date, amount, description) VALUES (:id, :accountId, :intType, :date, :amount, :description)";
        
        int id = sequenceDao.getNextId("Transaction");
        transaction.setId(id);
        
        getSimpleJdbcTemplate().update(SQL, new BeanPropertySqlParameterSource(transaction));
        log.info("Created new transaction with id = " + id);
        
        return id;
    }
    
    private static class TransactionMapper implements RowMapper<Transaction> {
        public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
            Transaction transaction = new Transaction();
            transaction.setId(rs.getInt("id"));
            transaction.setAccountId(rs.getInt("account_id"));
            transaction.setType(rs.getInt("type"));
            transaction.setDate(rs.getDate("date"));
            BigDecimal amount = rs.getBigDecimal("amount");
            transaction.setAmount(amount == null ? null : amount.setScale(2));
            transaction.setDescription(rs.getString("description"));
            return transaction;
        }
    }
}
