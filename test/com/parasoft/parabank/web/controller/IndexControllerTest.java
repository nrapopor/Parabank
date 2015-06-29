package com.parasoft.parabank.web.controller;

import java.util.Date;
import java.util.List;

import org.springframework.web.servlet.ModelAndView;

import com.parasoft.parabank.domain.News;
import com.parasoft.parabank.domain.logic.NewsManager;

@SuppressWarnings({"unchecked", "deprecation"})
public class IndexControllerTest
extends AbstractControllerTest<IndexController> {
    private NewsManager newsManager;
    
    public void setNewsManager(NewsManager newsManager) {
        this.newsManager = newsManager;
    }
    
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        controller.setNewsManager(newsManager);
    }
    
    public void testHandleGetRequest() throws Exception {
        ModelAndView mav = controller.handleRequest(request, response);
        
        assertEquals("index", mav.getViewName());
        
        Date date = (Date)getModelValue(mav, "date");
        assertEquals("2010-09-13", date.toString());
        
        List<News> news = (List<News>)getModelValue(mav, "news");
        assertEquals(3, news.size());
    }
    
    public void testDatabaseUninitialized() throws Exception {
        getJdbcTemplate().execute("DROP TABLE News");
        ModelAndView mav = controller.handleRequest(request, response);
        assertNull(mav);
        assertEquals("initializeDB.htm", response.getRedirectedUrl());
    }
}
