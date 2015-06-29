package com.parasoft.parabank.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import com.parasoft.parabank.dao.PositionDao;
import com.parasoft.parabank.domain.HistoryPoint;
import com.parasoft.parabank.domain.Position;

/*
 * JDBC implementation of PositionDao
 */
public class JdbcPositionDao extends SimpleJdbcDaoSupport implements PositionDao {
    private static final Log log = LogFactory.getLog(JdbcPositionDao.class);
    
    private final String BASE_QUERY_SQL = "SELECT position_id, customer_id, name, symbol, shares, purchase_price FROM Positions";
        
    private JdbcSequenceDao sequenceDao;
    
    public void setSequenceDao(JdbcSequenceDao sequenceDao) {
        this.sequenceDao = sequenceDao;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.PositionDao#getPosition(int)
     */
    public Position getPosition(int position_id) {
        final String SQL = BASE_QUERY_SQL + " WHERE position_id = ?";
        
        log.info("Getting position object for position id = " + position_id);
        Position position = getSimpleJdbcTemplate().queryForObject(
                SQL, new PositionMapper(), position_id);
        
        return position;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.PositionDao#getPositionsForCustomerId(int)
     */
    public List<Position> getPositionsForCustomerId(int customer_id) {
        final String SQL = BASE_QUERY_SQL + " WHERE customer_id = ?";
        
        List<Position> positions = getSimpleJdbcTemplate().query(SQL, new PositionMapper(), customer_id);
        log.info("Retrieved " + positions.size() + " positions for customer id = " + customer_id);
        
        return positions;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.PositionDao#getPositionHistory(int, java.util.Date, java.util.Date)
     */
    public List<HistoryPoint> getPositionHistory(int positionId, Date startDate, Date endDate) {
        final String SQL = "SELECT * FROM Stock WHERE symbol = :symbol " +
                "AND date BETWEEN :endDate AND :startDate";
        
        String symbol = getPosition(positionId).getSymbol();
        
        List<HistoryPoint> history = getSimpleJdbcTemplate().query(SQL, new HistoryPointMapper(), symbol, startDate, endDate);
        
        log.info("Retrieved position history for position #" + positionId + " and date range "
                + startDate + " to " + endDate);
        
        return history;
    }

    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.PositionDao#createPosition(com.parasoft.parabank.domain.Position)
     */
    public int createPosition(Position position) {
        final String SQL = "INSERT INTO Positions (position_id, customer_id, name, symbol, shares, purchase_price) VALUES (:positionId, :customerId, :name, :symbol, :shares, :purchasePrice)";
        
        int position_id = sequenceDao.getNextId("Position");
        position.setPositionId(position_id);
        
        getSimpleJdbcTemplate().update(SQL, new BeanPropertySqlParameterSource(position));
        log.info("Created new position with position id = " + position_id);
        
        return position_id;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.PositionDao#updatePosition(com.parasoft.parabank.domain.Position)
     */
    public boolean updatePosition(Position position) {
        boolean success = false;
        final String SQL = "UPDATE Positions SET shares = :shares WHERE position_id = :positionId";
        
        getSimpleJdbcTemplate().update(SQL, new BeanPropertySqlParameterSource(position));
        log.info("Updated shares information for position with position id = " + position.getPositionId());
        return success;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.PositionDao#deletePosition(com.parasoft.parabank.domain.Position)
     */
    public boolean deletePosition(Position position) {
        boolean success = false;
        final String SQL = "DELETE FROM Positions WHERE position_id = :positionId";
        
        getSimpleJdbcTemplate().update(SQL, new BeanPropertySqlParameterSource(position));
        log.info("Deleted position with position id = " + position.getPositionId());
        return success;
    }
    
    private static class PositionMapper implements RowMapper<Position> {
        public Position mapRow(ResultSet rs, int rowNum) throws SQLException {
            Position position = new Position();
            position.setPositionId(rs.getInt("position_id"));
            position.setCustomerId(rs.getInt("customer_id"));
            position.setName(rs.getString("name"));
            position.setSymbol(rs.getString("symbol"));
            position.setShares(rs.getInt("shares"));
            position.setPurchasePrice(rs.getBigDecimal("purchase_price"));
            return position;
        }
    }
    
    private static class HistoryPointMapper implements RowMapper<HistoryPoint> {
        public HistoryPoint mapRow(ResultSet rs, int rowNum) throws SQLException {
            HistoryPoint historyPoint = new HistoryPoint();
            historyPoint.setSymbol(rs.getString("symbol"));
            historyPoint.setDate(rs.getDate("date"));
            historyPoint.setClosingPrice(rs.getBigDecimal("closing_price"));
            return historyPoint;
        }
    }
}
