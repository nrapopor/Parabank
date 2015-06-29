package com.parasoft.parabank.dao.jdbc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.parasoft.parabank.domain.TransactionCriteria;
import com.parasoft.parabank.domain.Transaction.TransactionType;

/**
 * Takes a transaction criteria object and returns SQL restriction clauses
 * corresponding to the given criteria
 */
class JdbcTransactionQueryRestrictor {
    private static final Log log = LogFactory.getLog(JdbcTransactionQueryRestrictor.class);
    
    /**
     * Create a SQL query fragment restricting transactions to the given
     * criteria
     *  
     * @param criteria the search criteria
     * @param params a set parameters to populate with criteria parameters
     * @return SQL query fragment to be appended to a WHERE clause
     */
    String getRestrictions(TransactionCriteria criteria,
            List<Object> params) {
        String restrictionsSql = "";
        switch (criteria.getSearchType()) {
        case ACTIVITY:
            restrictionsSql = getActivityRestrictions(criteria, params);
            break;
        case ID:
            restrictionsSql = getIdRestrictions(criteria, params);
            break;
        case DATE:
            restrictionsSql = getDateRestrictions(criteria, params);
            break;
        case DATE_RANGE:
            restrictionsSql = getDateRangeRestrictions(criteria, params);
            break;
        case AMOUNT:
            restrictionsSql = getAmountRestrictions(criteria, params);
            break;
        }
        return restrictionsSql;
    }
    
    private String getActivityRestrictions(TransactionCriteria criteria,
            List<Object> params) {
        String restrictionsSql = "";
        
        if (criteria.getMonth() != null && !"All".equals(criteria.getMonth())) {
            try {
                Date date = new SimpleDateFormat("MMM").parse(criteria.getMonth());
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                restrictionsSql += " AND MONTH(date) = ?";
                params.add(cal.get(Calendar.MONTH) + 1);
            } catch (ParseException e) {
                log.error("Could not parse supplied month value: " + criteria.getMonth(), e);
            }
        }
        
        if (criteria.getTransactionType() != null && !"All".equals(criteria.getTransactionType())) {
            restrictionsSql += " AND TYPE = ?";
            params.add(TransactionType.valueOf(criteria.getTransactionType()).ordinal());
        }
        
        log.info("Searching transactions by activity with parameters: " + params);
        return restrictionsSql;
    }
    
    private String getIdRestrictions(TransactionCriteria criteria,
            List<Object> params) {
        params.add(criteria.getTransactionId());
        log.info("Searching transactions by id with parameters: " + params);
        return " AND id = ?";
    }
    
    private String getDateRestrictions(TransactionCriteria criteria,
            List<Object> params) {
        params.add(criteria.getOnDate());
        log.info("Searching transactions by date with parameters: " + params);
        return " AND date = ?";
    }
    
    private String getDateRangeRestrictions(TransactionCriteria criteria,
            List<Object> params) {
        params.add(criteria.getFromDate());
        params.add(criteria.getToDate());
        log.info("Searching transactions by date range with parameters: " + params);
        return " AND date >= ? AND date <= ?";
    }
    
    private String getAmountRestrictions(TransactionCriteria criteria,
            List<Object> params) {
        params.add(criteria.getAmount());
        log.info("Searching transactions by amount with parameters: " + params);
        return " AND amount = ?";
    }
}
