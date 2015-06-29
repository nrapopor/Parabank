package com.parasoft.parabank.domain.logic.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.parasoft.parabank.dao.NewsDao;
import com.parasoft.parabank.domain.News;
import com.parasoft.parabank.domain.logic.NewsManager;

/*
 * Implementation of news manager
 */
public class NewsManagerImpl implements NewsManager {
    private static final Log log = LogFactory.getLog(NewsManagerImpl.class);
    
    private NewsDao newsDao;
    
    public void setNewsDao(NewsDao newsDao) {
        this.newsDao = newsDao;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.NewsManager#getNews()
     */
    public Map<Date, List<News>> getNews() {
        return createNewsMap(newsDao.getNews());
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.NewsManager#getLatestNews()
     */
    public Map<Date, List<News>> getLatestNews() {
        Date date = newsDao.getLatestNewsDate();
        return createNewsMap(newsDao.getNewsForDate(date));
    }
    
    /*
     * Convert list of news to a map of news items grouped by common dates
     */
    private Map<Date, List<News>> createNewsMap(List<News> news) {
        Map<Date, List<News>> newsMap = new LinkedHashMap<Date, List<News>>();
        for (News item : news) {
            Date date = item.getDate();
            if (!newsMap.containsKey(date)) {
                log.info("Creating new list for news date: " + date);
                newsMap.put(date, new ArrayList<News>());
            }
            List<News> newsList = newsMap.get(date);
            newsList.add(item);
            log.info("Adding news item with id = " + item.getId());
        }
        return newsMap;
    }
}
