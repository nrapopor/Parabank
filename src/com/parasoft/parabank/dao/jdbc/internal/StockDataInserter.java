package com.parasoft.parabank.dao.jdbc.internal;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import com.parasoft.parabank.dao.internal.DynamicDataInserter;
import com.parasoft.parabank.dao.jdbc.JdbcSequenceDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class StockDataInserter extends SimpleJdbcDaoSupport implements DynamicDataInserter {

    private SimpleDateFormat dateFormatter;
    private Random random;
    
    private JdbcSequenceDao sequenceDao;
    
    public StockDataInserter() {
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        random = new Random();
    }
    
    public void setSequenceDao(JdbcSequenceDao sequenceDao) {
        this.sequenceDao = sequenceDao;
    }
    
    public void insertData() {
        final String SQL = "INSERT INTO Stock (id, symbol, date, closing_price) " +
    
    //commented in order to make it database agnostic 
    //the below syntax is HSQL specific and so it is being replaced to a generic format 
    //              "VALUES (:id, :symbol, :date, :closingPrice)";
               "VALUES (?,?, ?, ?)";
        
        for(String symbol : getSymbols()) {
            Calendar calendar = Calendar.getInstance();
            for (int i = -1; i > -1826; i--) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                getSimpleJdbcTemplate().update(SQL, sequenceDao.getNextId("Stock"), 
                    symbol, dateFormatter.format(calendar.getTime()), getPrice());
            }
        }
    }
    
    public List<String> getSymbols() {
        final String SQL = "SELECT symbol FROM Company";

        return getJdbcTemplate().query(SQL, new ResultSetExtractor<List<String>>() {
            public List<String> extractData(ResultSet rs) throws SQLException,
                    DataAccessException {
                List<String> symbols = new ArrayList<String>();
                while (rs.next()) {
                    symbols.add(rs.getString("symbol"));
                }
                return symbols;
            }
        });
    }

    private String getPrice() {
        String priceDollars = String.valueOf(random.nextInt(90));
        String priceCents = String.valueOf(random.nextInt(100));
        priceCents = (priceCents.length() == 1) ? "0" + priceCents : priceCents;
        return priceDollars + "." + priceCents;
    }
}