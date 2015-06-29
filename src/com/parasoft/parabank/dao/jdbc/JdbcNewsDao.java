package com.parasoft.parabank.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import com.parasoft.parabank.dao.NewsDao;
import com.parasoft.parabank.domain.News;

/*
 * JDBC implementation of NewsDao 
 */
public class JdbcNewsDao extends SimpleJdbcDaoSupport implements NewsDao {
    private static final Log log = LogFactory.getLog(JdbcNewsDao.class);
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.NewsDao#getNews()
     */
    public List<News> getNews() {
        final String SQL = "SELECT id, date, headline, story FROM News ORDER BY id DESC";
        
        List<News> news = getSimpleJdbcTemplate().query(SQL, new NewsMapper());
        log.info("Retrieved " + news.size() + " news items");
        
        return news;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.NewsDao#getNewsForDate(java.util.Date)
     */
    public List<News> getNewsForDate(Date date) {
        final String SQL = "SELECT id, date, headline, story FROM News WHERE date = ? ORDER BY id DESC";
        
        List<News> news = getSimpleJdbcTemplate().query(SQL, new NewsMapper(), date);
        log.info("Retrieved " + news.size() + " news items for date = " + date);
        
        return news;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.dao.NewsDao#getLatestNewsDate()
     */
    public Date getLatestNewsDate() {
        final String SQL = "SELECT MAX(date) FROM News";
        
        return getSimpleJdbcTemplate().queryForObject(SQL, Date.class);
    }
    
    private static final class NewsMapper implements RowMapper<News> {
        public News mapRow(ResultSet rs, int rowNum) throws SQLException {
            News news = new News();
            news.setId(rs.getInt("id"));
            news.setDate(rs.getDate("date"));
            news.setHeadline(rs.getString("headline"));
            news.setStory(rs.getString("story"));
            return news;
        }
    }
}
