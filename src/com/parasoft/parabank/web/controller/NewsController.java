package com.parasoft.parabank.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.parasoft.parabank.domain.logic.NewsManager;

/**
 * Controller for displaying news page
 */
public class NewsController implements Controller {
    private NewsManager newsManager;
    
    public void setNewsManager(NewsManager newsManager) {
        this.newsManager = newsManager;
    }
    
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {      
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("news", newsManager.getNews());
        return new ModelAndView("news", "model", model);
    }
}
