package com.parasoft.parabank.domain;

import java.math.BigDecimal;
import java.util.Calendar;

import com.parasoft.parabank.test.util.AbstractBeanTestCase;

public class HistoryPointTest extends AbstractBeanTestCase<HistoryPoint> {
    private static final String SYMBOL = "AAR";
    private static final BigDecimal CLOSING_PRICE = new BigDecimal("30.00");
    
    public void testGetAndSetSymbol() {
        assertNull(bean.getSymbol());
        bean.setSymbol(SYMBOL);
        assertEquals(SYMBOL, bean.getSymbol());
    }
    
    public void testGetAndSetDate() {
        assertNull(bean.getDate());
        bean.setDate(Calendar.getInstance().getTime());
        assertEquals(Calendar.getInstance().getTime(), bean.getDate());
    }
    
    public void testGetAndSetClosingPrice() {
        assertNull(bean.getClosingPrice());
        bean.setClosingPrice(CLOSING_PRICE);
        assertEquals(CLOSING_PRICE, bean.getClosingPrice());
    }
}
