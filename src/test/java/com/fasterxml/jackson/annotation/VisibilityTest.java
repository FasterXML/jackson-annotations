package com.fasterxml.jackson.annotation;

import java.lang.reflect.Member;

// Silly test for JsonAutoDetect.Visibility type, for code coverage
public class VisibilityTest extends TestBase
{
    static class Bogus {
        public String value;
    }
    
    public void testProperties() throws Exception
    {
        Member m = Bogus.class.getField("value");
        
        assertTrue(JsonAutoDetect.Visibility.ANY.isVisible(m));
        assertFalse(JsonAutoDetect.Visibility.NONE.isVisible(m));

        assertTrue(JsonAutoDetect.Visibility.NON_PRIVATE.isVisible(m));
        assertTrue(JsonAutoDetect.Visibility.PUBLIC_ONLY.isVisible(m));
        assertTrue(JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC.isVisible(m));
        assertTrue(JsonAutoDetect.Visibility.NON_PRIVATE.isVisible(m));

        // forget why DEFAULT would give false but
        assertFalse(JsonAutoDetect.Visibility.DEFAULT.isVisible(m));
    }
}
