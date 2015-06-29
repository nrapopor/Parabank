package com.parasoft.parabank.util;

import com.parasoft.parabank.util.Util;

import junit.framework.TestCase;

public class UtilTest extends TestCase {
    public void testEquals() {
        assertTrue(Util.equals(null, null));
        assertFalse(Util.equals("foo", null));
        assertFalse(Util.equals(null, "bar"));
        assertFalse(Util.equals("foo", "bar"));
        assertTrue(Util.equals("foo", "foo"));
    }
    
    public void testEmpty() {
        assertTrue(Util.isEmpty(null));
        assertTrue(Util.isEmpty(""));
        assertFalse(Util.isEmpty("s"));
    }
}
