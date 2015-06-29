package com.parasoft.parabank.web.controller;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.parasoft.parabank.test.util.AbstractParaBankDataSourceTest;

@SuppressWarnings({"deprecation", "rawtypes"})
abstract class AbstractControllerTest<T> extends AbstractParaBankDataSourceTest {
    private Class<T> controllerClass;
    protected T controller;
    
    protected MockHttpServletRequest request;
    protected MockHttpServletResponse response;

    @SuppressWarnings("unchecked")
    protected AbstractControllerTest() {
        controllerClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
    
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        
        controller = controllerClass.newInstance();
        
        request = new MockHttpServletRequest();
        request.setMethod("GET");
        
        response = new MockHttpServletResponse();
    }
    
    protected final Object getModelValue(ModelAndView mav, String name) {
        ModelMap model = mav.getModelMap();
        Map map = (Map)model.get("model");
        return map.get(name);
    }
}
