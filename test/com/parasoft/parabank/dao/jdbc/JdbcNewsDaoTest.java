package com.parasoft.parabank.dao.jdbc;

import java.sql.Date;
import java.util.List;

import com.parasoft.parabank.dao.NewsDao;
import com.parasoft.parabank.domain.News;
import com.parasoft.parabank.test.util.AbstractParaBankDataSourceTest;

@SuppressWarnings("deprecation")
public class JdbcNewsDaoTest extends AbstractParaBankDataSourceTest {   
    private NewsDao newsDao;
       
    public void setNewsDao(NewsDao newsDao) {
        this.newsDao = newsDao;
    }
    
    public void testGetNews() {
        List<News> news = newsDao.getNews();
        assertEquals(6, news.size());
    }
    
    public void testGetNewsForDate() {
        List<News> news = newsDao.getNewsForDate(new Date(110, 8, 13));
        assertEquals(3, news.size());
        News item = news.get(0);
        assertEquals(6, item.getId());
        assertEquals("2010-09-13", item.getDate().toString());
        assertEquals("ParaBank Is Now Re-Open", item.getHeadline());
        assertNotNull(item.getStory());
    }
    
    public void testLastestNewsDate() {
        assertEquals("2010-09-13 00:00:00.0", newsDao.getLatestNewsDate().toString());
    }
}
