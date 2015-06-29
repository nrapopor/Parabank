package com.parasoft.parabank.web;

import java.util.Map;

import org.springframework.test.web.AbstractModelAndViewTests;
import org.springframework.web.servlet.ModelAndView;

@SuppressWarnings({"deprecation", "rawtypes"})
public class ViewUtilTest extends AbstractModelAndViewTests {
    private static final String ERROR_MESSAGE = "error message";
    private static final String[] PARAMS = new String[] { "param1", "param2" };
    public void testCreateErrorView() {
        ModelAndView mav = ViewUtil.createErrorView(ERROR_MESSAGE);
        assertEquals("error", mav.getViewName());
        assertViewName(mav, "error");
        Map map = assertAndReturnModelAttributeOfType(mav, "model", Map.class);
        assertEquals(ERROR_MESSAGE, map.get("message"));
        assertNull(map.get("parameters"));
        
        mav = ViewUtil.createErrorView(ERROR_MESSAGE, PARAMS);
        assertEquals("error", mav.getViewName());
        assertViewName(mav, "error");
        map = assertAndReturnModelAttributeOfType(mav, "model", Map.class);
        assertEquals(ERROR_MESSAGE, map.get("message"));
        assertEquals(PARAMS, map.get("parameters"));
    }
}
