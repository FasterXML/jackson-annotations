package com.fasterxml.jackson.annotation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JacksonInjectTest
{
    private final static class Bogus {
        @JacksonInject(value="inject", useInput=OptBoolean.FALSE,
                optional=OptBoolean.FALSE)
        public int field;

        @JacksonInject
        public int vanilla;

        @JacksonInject(optional = OptBoolean.TRUE)
        public int optionalField;
    }

    private final JacksonInject.Value EMPTY = JacksonInject.Value.empty();

    @Test
    public void testEmpty()
    {
        assertNull(EMPTY.getId());
        assertNull(EMPTY.getUseInput());
        assertTrue(EMPTY.willUseInput(true));
        assertFalse(EMPTY.willUseInput(false));

        assertSame(EMPTY, JacksonInject.Value.construct(null, null, null));
        // also, "" gets coerced to null so
        assertSame(EMPTY, JacksonInject.Value.construct("", null, null));
    }

    @Test
    public void testFromAnnotation() throws Exception
    {
        assertSame(EMPTY, JacksonInject.Value.from(null)); // legal

        JacksonInject ann = Bogus.class.getField("field").getAnnotation(JacksonInject.class);
        JacksonInject.Value v = JacksonInject.Value.from(ann);
        assertEquals("inject", v.getId());
        assertEquals(Boolean.FALSE, v.getUseInput());

        assertEquals("JacksonInject.Value(id=inject,useInput=false,optional=false)", v.toString());
        assertFalse(v.equals(EMPTY));
        assertFalse(EMPTY.equals(v));

        JacksonInject ann2 = Bogus.class.getField("vanilla").getAnnotation(JacksonInject.class);
        v = JacksonInject.Value.from(ann2);
        assertEquals(JacksonInject.Value.construct(null, null, null), v,
                "optional should be `null` by default");

        JacksonInject optionalField = Bogus.class.getField("optionalField")
                .getAnnotation(JacksonInject.class);
        v = JacksonInject.Value.from(optionalField);
        assertEquals(JacksonInject.Value.construct(null, null, true), v);
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testStdMethods() {
        assertEquals("JacksonInject.Value(id=null,useInput=null,optional=null)",
                EMPTY.toString());
        int x = EMPTY.hashCode();
        if (x == 0) { // no fixed value, but should not evaluate to 0
            fail();
        }
        assertEquals(EMPTY, EMPTY);
        assertFalse(EMPTY.equals(null));
        assertFalse(EMPTY.equals("xyz"));

        JacksonInject.Value equals1 = JacksonInject.Value.construct("value", true, true);
        JacksonInject.Value equals2 = JacksonInject.Value.construct("value", true, true);
        JacksonInject.Value valueNull = JacksonInject.Value.construct(null, true, true);
        JacksonInject.Value useInputNull = JacksonInject.Value.construct("value", null, true);
        JacksonInject.Value optionalNull = JacksonInject.Value.construct("value", true, null);
        JacksonInject.Value valueNotEqual = JacksonInject.Value.construct("not equal", true, true);
        JacksonInject.Value useInputNotEqual = JacksonInject.Value.construct("value", false, true);
        JacksonInject.Value optionalNotEqual = JacksonInject.Value.construct("value", true, false);
        String string = "string";

        assertEquals(equals1, equals2);
        assertNotEquals(equals1, valueNull);
        assertNotEquals(equals1, useInputNull);
        assertNotEquals(equals1, optionalNull);
        assertNotEquals(equals1, valueNotEqual);
        assertNotEquals(equals1, useInputNotEqual);
        assertNotEquals(equals1, optionalNotEqual);
        assertNotEquals(equals1, string);
    }

    @Test
    public void testFactories() throws Exception
    {
        JacksonInject.Value v = EMPTY.withId("name");
        assertNotSame(EMPTY, v);
        assertEquals("name", v.getId());
        assertSame(v, v.withId("name"));

        JacksonInject.Value v2 = v.withUseInput(Boolean.TRUE);
        assertNotSame(v, v2);
        assertFalse(v.equals(v2));
        assertFalse(v2.equals(v));
        assertSame(v2, v2.withUseInput(Boolean.TRUE));

        JacksonInject.Value v3 = v.withOptional(Boolean.TRUE);
        assertNotSame(v, v3);
        assertFalse(v.equals(v3));
        assertFalse(v3.equals(v));
        assertSame(v3, v3.withOptional(Boolean.TRUE));
        assertTrue(v3.getOptional());

        int x = v2.hashCode();
        if (x == 0) { // no fixed value, but should not evaluate to 0
            fail();
        }
    }
}
