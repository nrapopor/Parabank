package com.parasoft.parabank.web.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.web.servlet.ModelAndView;

import com.parasoft.parabank.domain.News;
import com.parasoft.parabank.domain.logic.NewsManager;

@SuppressWarnings("unchecked")
public class NewsControllerTest
extends AbstractControllerTest<NewsController> {
    private NewsManager newsManager;
    
    public void setNewsManager(NewsManager newsManager) {
        this.newsManager = newsManager;
    }
    
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        controller.setNewsManager(newsManager);
    }
    
    public void testHandleRequest() throws Exception {
        ModelAndView mav = controller.handleRequest(request, response);
        
        assertEquals("news", mav.getViewName());
        
        Map<Date, List<News>> news = (Map<Date, List<News>>)getModelValue(mav, "news");
        assertEquals(4, news.size());
    }    
}
