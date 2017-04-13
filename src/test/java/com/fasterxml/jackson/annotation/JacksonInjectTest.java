package com.fasterxml.jackson.annotation;

public class JacksonInjectTest extends TestBase
{
    private final static class Bogus {
        @JacksonInject(value="inject", useInput=OptBoolean.FALSE)
        public int field;

        @JacksonInject
        public int vanilla;
    }

    private final JacksonInject.Value EMPTY = JacksonInject.Value.empty();

    public void testEmpty()
    {
        assertNull(EMPTY.getId());
        assertNull(EMPTY.getUseInput());
        assertTrue(EMPTY.willUseInput(true));
        assertFalse(EMPTY.willUseInput(false));

        assertSame(EMPTY, JacksonInject.Value.construct(null, null));
        // also, "" gets coerced to null so
        assertSame(EMPTY, JacksonInject.Value.construct("", null));
    }

    public void testFromAnnotation() throws Exception
    {
        assertSame(EMPTY, JacksonInject.Value.from(null)); // legal

        JacksonInject ann = Bogus.class.getField("field").getAnnotation(JacksonInject.class);
        JacksonInject.Value v = JacksonInject.Value.from(ann);
        assertEquals("inject", v.getId());
        assertEquals(Boolean.FALSE, v.getUseInput());

        assertEquals("JacksonInject.Value(id=inject,useInput=false)", v.toString());
        assertFalse(v.equals(EMPTY));
        assertFalse(EMPTY.equals(v));

        JacksonInject ann2 = Bogus.class.getField("vanilla").getAnnotation(JacksonInject.class);
        v = JacksonInject.Value.from(ann2);
        assertSame(EMPTY, v);
    }

    public void testStdMethods() {
        assertEquals("JacksonInject.Value(id=null,useInput=null)",
                EMPTY.toString());
        int x = EMPTY.hashCode();
        if (x == 0) { // no fixed value, but should not evalute to 0
            fail();
        }
        assertEquals(EMPTY, EMPTY);
        assertFalse(EMPTY.equals(null));
        assertFalse(EMPTY.equals("xyz"));
    }

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

        int x = v2.hashCode();
        if (x == 0) { // no fixed value, but should not evalute to 0
            fail();
        }
    }
}
