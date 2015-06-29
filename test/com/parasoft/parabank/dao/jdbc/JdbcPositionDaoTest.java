package com.parasoft.parabank.dao.jdbc;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.parasoft.parabank.dao.jdbc.internal.StockDataInserter;
import com.parasoft.parabank.dao.PositionDao;
import com.parasoft.parabank.domain.HistoryPoint;
import com.parasoft.parabank.domain.Position;
import com.parasoft.parabank.test.util.AbstractParaBankDataSourceTest;

@SuppressWarnings("deprecation")
public class JdbcPositionDaoTest extends AbstractParaBankDataSourceTest {
    private static final int CUSTOMER_ID = 12212;
    private static final String NAME = "Test Company";
    private static final String SYMBOL = "TC";
    private static final int SHARES = 20;
    private static final BigDecimal PURCHASEPRICE = new BigDecimal("50.00");
    
    private PositionDao positionDao;
    private StockDataInserter stockDataInserter;

    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        stockDataInserter.insertData();
    }
    
    public void setPositionDao(PositionDao positionDao) {
        this.positionDao = positionDao;
    }
    
    public void setStockDataInserter(StockDataInserter stockDataInserter) {
        this.stockDataInserter = stockDataInserter;
    }

    public void testCreatePosition() {
        Position setUpPosition = new Position();
        setUpPosition.setCustomerId(CUSTOMER_ID);
        setUpPosition.setName(NAME);
        setUpPosition.setSymbol(SYMBOL);
        setUpPosition.setShares(SHARES);
        setUpPosition.setPurchasePrice(PURCHASEPRICE);

        int positionId = positionDao.createPosition(setUpPosition);
        assertEquals("wrong expected id?", 13017, positionId);
        
        Position position = positionDao.getPosition(positionId);
        assertEquals(setUpPosition, position);
    }
    
    public void testGetPosition() {
        Position position = positionDao.getPosition(12345);
        assertEquals(12345, position.getPositionId());
        assertEquals(12212, position.getCustomerId());
        assertEquals("AMR Corporation", position.getName());
        assertEquals("AAR", position.getSymbol());
        assertEquals(20, position.getShares());
        assertEquals(new BigDecimal("23.53"), position.getPurchasePrice());
        
        try {
            position = positionDao.getPosition(-1);
            fail("did not throw expected DataAccessException");
        } catch (DataAccessException e) { }
    }
    
    public void testGetPositionsForCustomerId() {
        List<Position> positions = positionDao.getPositionsForCustomerId(12212);
        assertEquals("wrong number of positions?", 3, positions.size());
        
        positions = positionDao.getPositionsForCustomerId(-1);
        assertEquals("expected no positions for invalid id", 0, positions.size());
    }
    
    public void testGetPositionHistory() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date endDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -10);
        Date startDate = calendar.getTime();
        List<HistoryPoint> history = positionDao.getPositionHistory(12345, startDate, endDate);
        
        assertEquals("wrong number of history points?", 11, history.size());
        
        for (HistoryPoint historyPoint : history) {
            assertEquals("wrong symbol?", "AAR", historyPoint.getSymbol());
        }
    }
    
    public void testUpdatePosition() {
        Position setUpPosition = new Position();
        setUpPosition.setCustomerId(CUSTOMER_ID);
        setUpPosition.setName(NAME);
        setUpPosition.setSymbol(SYMBOL);
        setUpPosition.setShares(SHARES);
        setUpPosition.setPurchasePrice(PURCHASEPRICE);

        int positionId = positionDao.createPosition(setUpPosition);
        
        Position position = positionDao.getPosition(positionId);
        assertEquals(setUpPosition, position);
        
        position.setShares(10);
        assertFalse(setUpPosition.equals(position));

        positionDao.updatePosition(position);
        
        Position updatedPosition = positionDao.getPosition(positionId);
        assertEquals(position, updatedPosition);
    }
    
    public void testDeletePosition() {
        Position setUpPosition = new Position();
        setUpPosition.setCustomerId(CUSTOMER_ID);
        setUpPosition.setName(NAME);
        setUpPosition.setSymbol(SYMBOL);
        setUpPosition.setShares(SHARES);
        setUpPosition.setPurchasePrice(PURCHASEPRICE);

        int positionId = positionDao.createPosition(setUpPosition);
        
        Position position = positionDao.getPosition(positionId);
        assertEquals(setUpPosition, position);
        
        positionDao.deletePosition(position);
        try {
            position = positionDao.getPosition(positionId);
            fail("did not throw expected DataAccessException");
        } catch (DataAccessException e) { }
    }
}
