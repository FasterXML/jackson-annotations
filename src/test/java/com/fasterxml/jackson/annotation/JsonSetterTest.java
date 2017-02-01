package com.fasterxml.jackson.annotation;

import com.fasterxml.jackson.annotation.JsonSetter.Nulls;

public class JsonSetterTest extends TestBase
{
    private final static class Bogus {
        @JsonSetter(nulls=Nulls.FAIL, contentNulls=Nulls.SKIP)
        public int field;
    }

    private final JsonSetter.Value EMPTY = JsonSetter.Value.empty();

    public void testEmpty()
    {
        assertEquals(JsonSetter.Nulls.DEFAULT, EMPTY.getValueNulls());
        assertEquals(JsonSetter.Nulls.DEFAULT, EMPTY.getContentNulls());

        assertEquals(JsonSetter.class, EMPTY.valueFor());

        assertNull(EMPTY.nonDefaultValueNulls());
        assertNull(EMPTY.nonDefaultContentNulls());
    }

    public void testStdMethods() {
        assertEquals("JsonSetter.Value(valueNulls=DEFAULT,contentNulls=DEFAULT)",
                EMPTY.toString());
        int x = EMPTY.hashCode();
        if (x == 0) { // no fixed value, but should not evalute to 0
            fail();
        }
        assertEquals(EMPTY, EMPTY);
        assertFalse(EMPTY.equals(null));
        assertFalse(EMPTY.equals("xyz"));
    }

    public void testFromAnnotation() throws Exception
    {
        assertSame(EMPTY, JsonSetter.Value.from(null)); // legal
        
        JsonSetter ann = Bogus.class.getField("field").getAnnotation(JsonSetter.class);
        JsonSetter.Value v = JsonSetter.Value.from(ann);
        assertEquals(JsonSetter.Nulls.FAIL, v.getValueNulls());
        assertEquals(JsonSetter.Nulls.SKIP, v.getContentNulls());
    }

    public void testConstruct() throws Exception
    {
        JsonSetter.Value v = JsonSetter.Value.construct(null, null);
        assertSame(EMPTY, v);
    }

    public void testFactories() throws Exception
    {
        JsonSetter.Value v = JsonSetter.Value.forContentNulls(Nulls.SET);
        assertEquals(Nulls.DEFAULT, v.getValueNulls());
        assertEquals(Nulls.SET, v.getContentNulls());
        assertEquals(Nulls.SET, v.nonDefaultContentNulls());

        JsonSetter.Value skip = JsonSetter.Value.forValueNulls(Nulls.SKIP);
        assertEquals(Nulls.SKIP, skip.getValueNulls());
        assertEquals(Nulls.DEFAULT, skip.getContentNulls());
        assertEquals(Nulls.SKIP, skip.nonDefaultValueNulls());
    }

    public void testSimpleMerge()
    {
        JsonSetter.Value v = EMPTY.withContentNulls(Nulls.SKIP);
        assertEquals(Nulls.SKIP, v.getContentNulls());
        v = v.withValueNulls(Nulls.FAIL);
        assertEquals(Nulls.FAIL, v.getValueNulls());
    }

    public void testWithMethods()
    {
        JsonSetter.Value v = EMPTY.withContentNulls(null);
        assertSame(EMPTY, v);
        v = v.withContentNulls(Nulls.FAIL);
        assertEquals(Nulls.FAIL, v.getContentNulls());

        v = v.withValueNulls(Nulls.SKIP);
        assertEquals(Nulls.SKIP, v.getValueNulls());

        v = v.withValueNulls(null, null);
        assertEquals(Nulls.DEFAULT, v.getContentNulls());
        assertEquals(Nulls.DEFAULT, v.getValueNulls());
        assertSame(v, v.withValueNulls(null, null));
    }
}
