package com.fasterxml.jackson.annotation;

import java.util.*;

/**
 * Tests to verify that it is possibly to merge {@link JsonIgnoreProperties.Value}
 * instances for overrides
 */
public class JsonIgnorePropertiesTest extends TestBase
{
    private final JsonIgnoreProperties.Value EMPTY = JsonIgnoreProperties.Value.empty();

    public void testEquality() {
        assertTrue(EMPTY.equals(EMPTY));
        assertTrue(new JsonFormat.Value().equals(new JsonFormat.Value()));
    }
    
    public void testToString() {
        assertEquals(
                "[ignored=[],ignoreUnknown=false,allowGetters=false,allowSetters=true,merge=true]",
                EMPTY.withAllowSetters()
                    .withMerge()
                    .toString());
    }

    public void testMutantFactories()
    {
        assertEquals(0, EMPTY.getIgnored().size());
        assertEquals(2, EMPTY.withIgnored("a", "b").getIgnored().size());
        assertEquals(1, EMPTY.withIgnored(Collections.singleton("x")).getIgnored().size());
        assertEquals(0, EMPTY.withIgnored((Set<String>) null).getIgnored().size());

        assertTrue(EMPTY.withIgnoreUnknown().getIgnoreUnknown());
        assertFalse(EMPTY.withoutIgnoreUnknown().getIgnoreUnknown());

        assertTrue(EMPTY.withAllowGetters().getAllowGetters());
        assertFalse(EMPTY.withoutAllowGetters().getAllowGetters());
        assertTrue(EMPTY.withAllowSetters().getAllowSetters());
        assertFalse(EMPTY.withoutAllowSetters().getAllowSetters());

        assertTrue(EMPTY.withMerge().getMerge());
        assertFalse(EMPTY.withoutMerge().getMerge());
    }

    public void testSimpleMerge()
    {
        JsonIgnoreProperties.Value v1 = EMPTY.withIgnoreUnknown().withAllowGetters();
        JsonIgnoreProperties.Value v2a = EMPTY.withMerge()
                .withIgnored("a");
        JsonIgnoreProperties.Value v2b = EMPTY.withoutMerge();

        // when merging, should just have union of things
        JsonIgnoreProperties.Value v3a = v1.withOverrides(v2a);
        assertEquals(Collections.singleton("a"), v3a.getIgnored());
        assertTrue(v3a.getIgnoreUnknown());
        assertTrue(v3a.getAllowGetters());
        assertFalse(v3a.getAllowSetters());

        // when NOT merging, simply replacing values
        JsonIgnoreProperties.Value v3b = v1.withOverrides(v2b);
        assertEquals(Collections.emptySet(), v3b.getIgnored());
        assertFalse(v3b.getIgnoreUnknown());
        assertFalse(v3b.getAllowGetters());
        assertFalse(v3b.getAllowSetters());

        // and effectively really just uses overrides as is
        assertEquals(v2b, v3b);
    }
}
