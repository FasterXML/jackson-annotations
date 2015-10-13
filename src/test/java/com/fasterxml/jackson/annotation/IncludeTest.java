package com.fasterxml.jackson.annotation;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Tests to verify that it is possibly to merge {@link JsonInclude.Value}
 * instances for overrides
 */
public class IncludeTest extends TestBase
{
    private final JsonInclude.Value EMPTY = JsonInclude.Value.empty();

    public void testEquality() {
        assertTrue(EMPTY.equals(EMPTY));
        assertTrue(new JsonFormat.Value().equals(new JsonFormat.Value()));

        JsonInclude.Value v1 = JsonInclude.Value.construct(Include.NON_ABSENT, null);
        JsonInclude.Value v2 = JsonInclude.Value.construct(Include.NON_ABSENT, null);
        JsonInclude.Value v3 = JsonInclude.Value.construct(Include.NON_ABSENT, Include.NON_EMPTY);

        assertTrue(v1.equals(v2));
        assertTrue(v2.equals(v1));

        assertFalse(v1.equals(v3));
        assertFalse(v3.equals(v1));
        assertFalse(v2.equals(v3));
        assertFalse(v3.equals(v2));
    }
    
    public void testToString() {
        assertEquals("[value=NON_ABSENT,content=USE_DEFAULTS]",
                JsonInclude.Value.construct(Include.NON_ABSENT, null).toString());
    }
    
    public void testSimpleMerge()
    {
        JsonInclude.Value empty = JsonInclude.Value.empty();

        assertEquals(Include.USE_DEFAULTS, empty.getValueInclusion());
        assertEquals(Include.USE_DEFAULTS, empty.getContentInclusion());
        
        JsonInclude.Value v2 = empty.withValueInclusion(Include.NON_ABSENT);

        assertEquals(Include.NON_ABSENT, v2.getValueInclusion());
        assertEquals(Include.USE_DEFAULTS, v2.getContentInclusion());

        JsonInclude.Value v3 = new JsonInclude.Value(Include.NON_EMPTY, Include.ALWAYS);
        assertEquals(Include.NON_EMPTY, v3.getValueInclusion());
        assertEquals(Include.ALWAYS, v3.getContentInclusion());

        // Ok; but then overrides, which should skip 'USE_DEFAULT' overrides
        JsonInclude.Value merged = v3.withOverrides(empty);
        // no overrides with "empty":
        assertEquals(v3.getValueInclusion(), merged.getValueInclusion());
        assertEquals(v3.getContentInclusion(), merged.getContentInclusion());

        // but other values ought to be overridden (value, yes, content, no because it's default)
        merged = v3.withOverrides(v2);
        assertEquals(v2.getValueInclusion(), merged.getValueInclusion());
        assertEquals(v3.getContentInclusion(), merged.getContentInclusion());

        merged = empty.withOverrides(v3);
        assertEquals(v3.getValueInclusion(), merged.getValueInclusion());
        assertEquals(v3.getContentInclusion(), merged.getContentInclusion());
    }
}
