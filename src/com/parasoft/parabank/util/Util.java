package com.parasoft.parabank.util;

import java.text.DateFormat;

/**
 * Utility methods for ParaBank
 */
public final class Util {
	
	public static final ThreadLocal<DateFormat> DATE_TIME_FORMATTER = 
    		new ThreadLocal<DateFormat>() { protected DateFormat initialValue() {
    											return new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");}};
	
    private Util() { }
    
    /**
     * Convenience method for comparing two, possibly null, objects
     * 
     * @return true if objects are both null or equal, false otherwise
     */
    public static final boolean equals(Object o1, Object o2) {
        return (o1 == o2) || (o1 != null && o1.equals(o2));
    }
    
    /**
     * Convenience method for testing if a string is null or empty
     * 
     * @return true if string is null or empty, false otherwise 
     */
    public static final boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }
}
