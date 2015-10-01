package com.fasterxml.jackson.annotation;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class IncludeMergeTest extends TestBase
{
    public void testSimpleMerge()
    {
        JsonInclude.Value v1 = JsonInclude.Value.empty();

        assertEquals(Include.USE_DEFAULTS, v1.getValueInclusion());
        assertEquals(Include.USE_DEFAULTS, v1.getContentInclusion());
        
        JsonInclude.Value v2 = v1.withValueInclusion(Include.NON_ABSENT);

        assertEquals(Include.NON_ABSENT, v2.getValueInclusion());
        assertEquals(Include.USE_DEFAULTS, v2.getContentInclusion());

        JsonInclude.Value v3 = new JsonInclude.Value(Include.NON_EMPTY, Include.ALWAYS);
        assertEquals(Include.NON_EMPTY, v3.getValueInclusion());
        assertEquals(Include.ALWAYS, v3.getContentInclusion());

        // Ok; but then overrides, which should skip 'USE_DEFAULT' overrides
        JsonInclude.Value v4 = v3.withOverrides(v1);
        // no overrides with "empty":
        assertEquals(v3.getValueInclusion(), v4.getValueInclusion());
        assertEquals(v3.getContentInclusion(), v4.getContentInclusion());
    }
}
