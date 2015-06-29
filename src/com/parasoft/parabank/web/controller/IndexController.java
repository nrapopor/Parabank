package com.parasoft.parabank.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.parasoft.parabank.domain.News;
import com.parasoft.parabank.domain.logic.NewsManager;

/**
 * Controller for home page
 */
public class IndexController implements Controller {
    private static final Log log = LogFactory.getLog(IndexController.class);
    
    private NewsManager newsManager;

    public void setNewsManager(NewsManager newsManager) {
        this.newsManager = newsManager;
    }
   
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        try {

        	if (request.getSession().getAttribute("ConnType") == null)
        		request.getSession().setAttribute("ConnType", "JDBC");
            Map<Date, List<News>> news = newsManager.getLatestNews();
            
            if (news.isEmpty()) {
            	response.sendRedirect("initializeDB.htm");
            	log.warn("Database not yet initialized. Initializing...");
            }

            for (Entry<Date, List<News>> newsDate : news.entrySet()) {
                model.put("date", newsDate.getKey());
                model.put("news", newsDate.getValue());
            }
        } catch (DataAccessException e) {
            log.warn("Database not yet initialized. Initializing...");
            response.sendRedirect("initializeDB.htm");
            return null;
        }
        
        return new ModelAndView("index", "model", model);
    }
}
