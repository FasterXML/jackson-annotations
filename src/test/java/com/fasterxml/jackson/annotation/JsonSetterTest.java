package com.fasterxml.jackson.annotation;

public class JsonSetterTest extends TestBase
{
    private final static class Bogus {
        @JsonSetter(nulls=Nulls.FAIL, contentNulls=Nulls.SKIP)
        public int field;
    }

    private final JsonSetter.Value EMPTY = JsonSetter.Value.empty();

    public void testEmpty()
    {
        assertEquals(Nulls.DEFAULT, EMPTY.getValueNulls());
        assertEquals(Nulls.DEFAULT, EMPTY.getContentNulls());

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
        assertEquals(Nulls.FAIL, v.getValueNulls());
        assertEquals(Nulls.SKIP, v.getContentNulls());
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
        assertSame(v, v.withContentNulls(Nulls.FAIL));

        JsonSetter.Value v2 = v.withValueNulls(Nulls.SKIP);
        assertEquals(Nulls.SKIP, v2.getValueNulls());
        assertFalse(v.equals(v2));
        assertFalse(v2.equals(v));

        JsonSetter.Value v3 = v2.withValueNulls(null, null);
        assertEquals(Nulls.DEFAULT, v3.getContentNulls());
        assertEquals(Nulls.DEFAULT, v3.getValueNulls());
        assertSame(v3, v3.withValueNulls(null, null));

        JsonSetter.Value merged = v3.withOverrides(v2);
        assertNotSame(v2, merged);
        assertEquals(merged, v2);
        assertEquals(v2, merged);
    }
}
